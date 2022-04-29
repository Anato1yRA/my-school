package com.example.myschool.presentation.custom

import android.graphics.Bitmap
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.myschool.presentation.authorization.ViewModelAuthorization

class WebViewClientCustom(private var viewModel: ViewModelAuthorization) : WebViewClient() {

    private var login: String = ""
    private var password: String = ""
    private var gu: Boolean = true
    private var previousPageURL: String = ""

    private var limitExhausted: Boolean = true

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        Thread {
            Thread.sleep(10000)

            if (limitExhausted) {
                viewModel.setNewSession(login, password, gu, "", true)
            }
        }.start()
    }

    override fun onPageFinished(view: WebView, url: String) {
        limitExhausted = false

        var badPage = false

        when (view.title?.substringBefore(' ').toString()) {
            "OperationalError",
            "502",
            "Технические",
            "RuntimeError",
            "about:blank" -> {
                badPage = true

                viewModel.setNewSession(login, password, gu, "", true)
            }
        }

        val substringURL = url.substringBefore('?').substringBefore('#')
        when (substringURL) {
            "https://esia.gosuslugi.ru/aas/oauth2/ac",
            "https://esia.gosuslugi.ru/idp/profile/SAML2/Redirect/SSO" -> {
                badPage = true
            }
        }

        if (previousPageURL == substringURL) {
            badPage = true
        }

        if (!badPage) {
            previousPageURL = substringURL

            when (substringURL) {
                // Страница авторизации Дневник
                "https://e-school.ryazangov.ru/auth/login-page" -> {
                    if (gu) {
                        view.loadUrl("javascript:document.getElementsByClassName('gosuslugi').item(0).click();")
                    } else {
                        viewModel.sendMessage("На данный момент приложение работает только с ГосУслугами")
                        viewModel.progressBarStop()
                    }
                }

                // Страница авторизации ГосУслуги
                "https://esia.gosuslugi.ru/idp/rlogin" -> {
                    view.loadUrl(
                        "javascript:" +
                                "input = document.querySelector('#login');" +
                                "input.value = '$login';" +
                                "input.dispatchEvent(new KeyboardEvent('keydown', { bubbles: true }));" +
                                "input.dispatchEvent(new KeyboardEvent('keypress', { bubbles: true }));" +
                                "input.dispatchEvent(new KeyboardEvent('keyup', { bubbles: true }));" +
                                "input.dispatchEvent(new Event('input', { bubbles: true }));" +
                                "input.dispatchEvent(new Event('change', { bubbles: true }));" +
                                "\$('#password').val('$password');" +
                                "\$('.ui-button-text')[0].click();"
                    )

                    view.evaluateJavascript(
                        "javascript:" +
                                "var countErr = 0;" +
                                "\$('.field-error').each(function(index){" +
                                "if(\$(this).css('display') != 'none'){countErr++;};" +
                                "});" +
                                "(function(){return countErr})()"
                    ) {
                        run {
                            if (it != "0") {
                                viewModel.sendMessage("Логин или Пароль заданы не верно")
                                viewModel.progressBarStop()
                            }
                        }
                    }
                }

                // Страница капчи
                "https://esia.gosuslugi.ru/captcha/" -> {
                    // Вернуть false тк авторизация не удалась
                    viewModel.sendMessage("Логин или Пароль заданы не верно")
                    viewModel.progressBarStop()
                }

                // Авторизация прошла успешно
                "https://e-school.ryazangov.ru/personal-area/" -> {
                    val cookies = CookieManager.getInstance().getCookie(url)

                    val sessionId = cookies.split("sessionid=")[1].split(";")[0]

                    viewModel.setNewSession(login, password, gu, sessionId, false)
                }

                else -> {
                    Log.d("MySchool", "WebView new url: $substringURL")
                }
            }
        }
    }

    fun setUserNewData(loginClient: String, passwordClient: String, guClient: Boolean) {
        login = loginClient
        password = passwordClient
        gu = guClient
    }
}