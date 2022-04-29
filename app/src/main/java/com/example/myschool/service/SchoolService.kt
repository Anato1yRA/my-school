package com.example.myschool.service

import android.app.*
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.myschool.R
import com.example.myschool.data.domain.usecase.service.UseCaseUpdateSchoolData
import com.example.myschool.presentation.main.ActivityMain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SchoolService : Service() {

    private val useCaseUpdateSchoolData: UseCaseUpdateSchoolData by inject()

    private var countDownTimer: CountDownTimer? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Начальное уведомление
        showNotification(
            "Уведомления об успеваемости",
            "Новых уведомлений нет"
        )

        // Таймер обновления данных
        startDataTimer(1000 * 60 * 10)

        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        // Создаем канал для уведомлений
        val serviceChannel = NotificationChannel(
            R.string.app_name.toString(),
            R.string.app_name.toString(),
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }

    override fun onDestroy() {
        super.onDestroy()

        countDownTimer?.cancel()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    // Конструктор уведомлений
    private fun showNotification(titleNotification: String, textNotification: String) {
        val intent = Intent(this, ActivityMain::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, R.string.app_name.toString())
            .setSmallIcon(R.drawable.ic_school) // Иконка
            .setContentTitle(titleNotification) // Заголовок
            .setContentText(textNotification) // Обвычное описание
            .setShowWhen(true) // Показывать / Не показывать время
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(textNotification)
            ) // Большое описание, если поддерживается
            .setPriority(NotificationCompat.PRIORITY_MAX) // Приоритет максимальный
            .setContentIntent(pendingIntent) // Ссылка на приложение
            .setAutoCancel(false) // Закрывать уведомление после клика
        //.setTicker(titleNotification + "\n" + textNotification) // Пуш уведомление
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        startForeground(1, builder.build())
    }

    private fun startDataTimer(timeMillis: Long) {
        Log.d("MySchool", "startDataTimer ${System.currentTimeMillis()}")

        GlobalScope.launch(Dispatchers.IO) {

            val response = useCaseUpdateSchoolData.execute()

            if (response["title"] != "") {
                showNotification(
                    "${response["title"]}",
                    "${response["text"]}"
                )
            }
        }

        // Таймер перезапуска функции
        countDownTimer = object : CountDownTimer(timeMillis, timeMillis) {
            override fun onTick(timeMillis: Long) {}

            override fun onFinish() {
                startDataTimer(timeMillis)
            }
        }.start()
    }
}