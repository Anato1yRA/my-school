package com.example.myschool.data.network

import android.annotation.SuppressLint
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.gildor.coroutines.okhttp.await
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class NetworkRepositoryImpl : NetworkRepository {

    private var client: OkHttpClient = getUnsafeOkHttpClient()

    override suspend fun getRequestData(parameters: Map<String, String>): Map<String, String> {
        // Ответ по умолчанию
        var responseCode: Int
        var responseMessage: String
        var responseData = ""

        try {
            val request = getRequestBuilder(parameters).build()

            try {
                val result = client.newCall(request).await()

                responseCode = result.code

                if (responseCode != 200) {
                    responseMessage = "Ошибочный запрос"
                } else {
                    responseMessage = "Запрос выполнен"

                    if(parameters["test"] == "0") {
                        responseData = result.body?.string().toString()
                    }
                }
            } catch (e: Exception) {
                responseCode = 400
                responseMessage = "Сервер не отвечает"
            }
        } catch (e: Exception) {
            responseCode = 500
            responseMessage = "Ошибка запроса данных"
        }


        return mapOf(
            "responseCode" to "$responseCode",
            "responseMessage" to responseMessage,
            "responseData" to responseData
        )
    }

    private fun getRequestBuilder(parameters: Map<String, String>): Request.Builder {
        return Request.Builder()
            .url("${parameters["url"]}")
            .header(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.60 Safari/537.36"
            )
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .addHeader("Cookie", "sessionid=${parameters["sessionId"]}")
    }

    private fun getUnsafeOkHttpClient(): OkHttpClient {
        try {
            val trustAllCerts = arrayOf<TrustManager>(
                @SuppressLint("CustomX509TrustManager")
                object : X509TrustManager {
                    @SuppressLint("TrustAllX509TrustManager")
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    @SuppressLint("TrustAllX509TrustManager")
                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }
                }
            )

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            val sslSocketFactory = sslContext.socketFactory

            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier { _, _ -> true }

            return builder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}