package com.example.myschool.presentation.authorization

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myschool.databinding.ActivityAuthorizationBinding
import com.example.myschool.presentation.custom.WebViewClientCustom
import com.example.myschool.presentation.main.ActivityMain
import org.koin.androidx.viewmodel.ext.android.viewModel

class ActivityAuthorization : AppCompatActivity() {
    private val viewModelAuthorization by viewModel<ViewModelAuthorization>()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAuthorizationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "Авторизация"

        viewModelAuthorization.apply {
            progressBar.observe(this@ActivityAuthorization) {
                if (it) {
                    binding.progressBar.visibility = View.VISIBLE
                } else {
                    binding.progressBar.visibility = View.GONE
                }
            }

            errorMessage.observe(this@ActivityAuthorization) {
                if (it != "") {
                    Toast.makeText(this@ActivityAuthorization, it, Toast.LENGTH_SHORT).show()
                }
            }

            isAuthorization.observe(this@ActivityAuthorization) {
                if (it) {
                    startActivity(Intent(this@ActivityAuthorization, ActivityMain::class.java))
                    finish()
                }
            }

            formAuthorization.observe(this@ActivityAuthorization) {
                binding.textEditLogin.setText(it.login)
                binding.textEditPassword.setText(it.password)
                binding.checkBoxGU.isChecked = it.gu
            }
        }

        // Параметры WebView
        val webView: WebView = binding.webView
        webView.setInitialScale(150)
        webView.settings.userAgentString =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.60 Safari/537.36"
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClientCustom(viewModel = viewModelAuthorization)

        // Проверка соединения через WebView
        binding.buttonConnection.setOnClickListener {
            viewModelAuthorization.progressBarStart()

            // Параметры авторизации Логин, Пароль, ГУ
            val login = binding.textEditLogin.text.toString()
            val password = binding.textEditPassword.text.toString()
            val gu = binding.checkBoxGU.isChecked

            // Очистка данных предыдущих запросов
            CookieManager.getInstance().removeAllCookies(null)
            webView.clearCache(true)
            webView.loadUrl("")

            (webView.webViewClient as WebViewClientCustom).setUserNewData(
                loginClient = login,
                passwordClient = password,
                guClient = gu
            )

            // Попытка загрузить начальную страницу электронного дневника
            webView.loadUrl("https://e-school.ryazangov.ru/personal-area")
        }
    }
}