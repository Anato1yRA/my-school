package com.example.myschool.presentation.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.myschool.R
import com.example.myschool.databinding.ActivityMainBinding
import com.example.myschool.presentation.authorization.ActivityAuthorization
import com.example.myschool.presentation.fragment.lesson.FragmentLesson
import com.example.myschool.presentation.fragment.report.FragmentReport
import com.example.myschool.presentation.fragment.schedule.FragmentSchedule
import com.example.myschool.service.SchoolService
import org.koin.androidx.viewmodel.ext.android.viewModel


class ActivityMain : AppCompatActivity() {

    private val viewModelMain by viewModel<ViewModelMain>()

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // Запрет ночного режима

        openFragment(FragmentLesson.newInstance())

        viewModelMain.apply {
            isOffline.observe(this@ActivityMain) { offline ->
                var colorPrimary = R.color.color_primary
                var colorPrimaryVariant = R.color.color_primary_variant

                if (offline) {
                    colorPrimary = R.color.red
                    colorPrimaryVariant = R.color.red_lite
                }

                window.statusBarColor = getColor(colorPrimaryVariant)
                supportActionBar?.setBackgroundDrawable(ColorDrawable(getColor(colorPrimary)))
                window.navigationBarColor = getColor(colorPrimary)
            }

            isAuthorization.observe(this@ActivityMain) {
                if (it == false && viewModelMain.isOffline.value == false) {
                    stopService(Intent(this@ActivityMain, SchoolService::class.java))

                    startActivity(Intent(this@ActivityMain, ActivityAuthorization::class.java))
                    finish()
                }

                if (it && !isSchoolServiceRunning()) {
                    startService(Intent(this@ActivityMain, SchoolService::class.java))
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    // Меню тулбара
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    // Обработка нажатия меню тулбара и подключение соответствующих фрагментов
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuLesson -> {
                openFragment(FragmentLesson.newInstance())
            }

            R.id.menuSchedule -> {
                openFragment(FragmentSchedule.newInstance())
            }

            R.id.menuReport -> {
                openFragment(FragmentReport.newInstance())
            }

            R.id.menuExit -> {
                viewModelMain.exit()
            }
        }

        return true
    }

    // Подключаем нужный фрагмент
    private fun openFragment(fragment: Fragment) {
        val countBackFragment = supportFragmentManager.backStackEntryCount
        if (countBackFragment > 0) {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameLayoutMain, fragment)
            .commit()
    }

    // Проверка запущенного сервиса
    @Suppress("DEPRECATION")
    private fun isSchoolServiceRunning(): Boolean {
        val manager: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service: ActivityManager.RunningServiceInfo in manager.getRunningServices(Integer.MAX_VALUE))
            if (SchoolService::class.java.name.equals(service.service.className)) {
                return true
            }
        return false
    }
}