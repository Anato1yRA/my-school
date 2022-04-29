package com.example.myschool.data.domain.repository

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.text.Html
import androidx.lifecycle.LiveData
import com.example.myschool.data.database.LessonDao
import com.example.myschool.data.database.entity.*
import com.example.myschool.data.network.NetworkRepository
import com.example.myschool.data.storage.StorageRepository
import kotlinx.coroutines.coroutineScope
import org.json.JSONObject
import java.util.*
import kotlin.math.round


class DomainRepositoryImpl(
    private val networkRepository: NetworkRepository,
    private val storageRepository: StorageRepository,
    private val lessonDao: LessonDao
) : DomainRepository {

    override fun checkUpdate(): LiveData<String> {
        return lessonDao.checkUpdateData()
    }

    @SuppressLint("SimpleDateFormat")
    override suspend fun getListReport(): List<Map<String, String>> {
        val response = mutableListOf<Map<String, String>>()

        val reportList = lessonDao.getReport()

        reportList.forEach {
            val date = Date(it.date!!)

            val format = SimpleDateFormat("dd.MM.yyyy [HH:mm]")

            response.add(mapOf("date" to "Отчет за " + format.format(date), "text" to it.text))
        }

        if(response.count() == 0){
            response.add(mapOf("date" to "Отчетов по успеваемости пока нет", "text" to ""))
        }

        return response
    }

    override suspend fun setNewSession(
        login: String,
        password: String,
        gu: Boolean,
        sessionId: String,
        offline: Boolean
    ): Map<String, String> {
        if (!offline) {
            val parameters = mapOf(
                "url" to "https://e-school.ryazangov.ru/api/ProfileService/GetPersonData",
                "test" to "1",
                "sessionId" to sessionId
            )

            // Тестовый запрос к серверу
            val responseNetwork = networkRepository.getRequestData(parameters)

            if (responseNetwork["responseCode"] == "200") {
                storageRepository.set(
                    login = login,
                    password = password,
                    gu = gu,
                    sessionId = sessionId,
                    offline = offline
                )

                return mapOf(
                    "responseCode" to "200",
                    "responseMessage" to "Авторизация успешна",
                    "offline" to "$offline"
                )
            }
        }

        if (offline) {
            val userData = storageRepository.get()

            if (editStr("${userData["login"]}") == editStr(login) && editStr("${userData["password"]}") == editStr(
                    password
                )
            ) {
                storageRepository.set(
                    login = login,
                    password = password,
                    gu = gu,
                    sessionId = sessionId,
                    offline = offline
                )

                return mapOf(
                    "responseCode" to "200",
                    "responseMessage" to "Включен offline режим\nСервер временно не доступен",
                    "offline" to "$offline"
                )
            }
        }

        return mapOf(
            "responseCode" to "400",
            "responseMessage" to "Необходимо авторизоваться",
            "offline" to "$offline"
        )
    }

    override suspend fun getSession(): Map<String, String> {
        return storageRepository.get()
    }

    override suspend fun exitSession() {
        storageRepository.exit()
    }

    override suspend fun getListLesson(): List<Map<String, String>> {
        // Пустой список
        val listLesson = mutableListOf<Map<String, String>>()

        // Получаем login пользователя из storage
        val userLogin = storageRepository.getUserLogin()

        if (userLogin.isEmpty()) return listLesson

        // Получаем id пользователя из room
        lessonDao.getUserIdByLogin(login = userLogin) ?: return listLesson

        // Получаем список предметов
        val lessonList: List<EntityLesson> = lessonDao.getListLesson()

        // Получаем список оценок
        val listMark: List<EntityMark> = lessonDao.getListMark()

        val listInfo = mutableListOf<Map<String, String>>()
        val listNoInfo = mutableListOf<Map<String, String>>()

        lessonList.forEach { itLesson ->
            var grade = 0
            var summ = 0
            var average = 0F
            var countFive = 0

            listMark.forEach listMark@{ itMark ->
                if (itLesson.id == itMark.lessonId) {
                    grade++

                    summ += itMark.mark
                }
            }

            if (grade > 0) {
                average = round(summ.toFloat() / grade.toFloat() * 100) / 100
            }

            if (average < 3.56) {
                var gradeDump = grade
                var averageDump = average

                while (averageDump < 3.56) {
                    countFive++
                    gradeDump++
                    summ += 5
                    averageDump = round(summ.toFloat() / gradeDump.toFloat() * 100) / 100
                }
            }

            if (grade + countFive < 3) {
                countFive += 3 - (grade + countFive)
            }

            var info = ""
            if (countFive > 0) {
                info = "Нужно 5: $countFive шт."
            }

            val map = mapOf(
                "id" to "${itLesson.id}",
                "name" to itLesson.name,
                "grade" to "Оценок: $grade",
                "average" to "Средняя: $average",
                "info" to info
            )

            if (info != "") {
                listInfo.add(map)
            } else {
                listNoInfo.add(map)
            }
        }

        listInfo.sortBy { "name" }
        listLesson.sortBy { "name" }

        listInfo.forEach {
            listLesson.add(it)
        }

        listNoInfo.forEach {
            listLesson.add(it)
        }

        return listLesson
    }


    @SuppressLint("SimpleDateFormat")
    override suspend fun updateSchoolData(): Map<String, String> {
        // Ответ по умолчанию
        val response = mutableMapOf("title" to "", "text" to "")

        // Данные текущего пользователя
        val storageResponse = storageRepository.get()

        if (storageResponse["sessionId"] == "") return response

        var parameters = mapOf(
            "url" to "https://e-school.ryazangov.ru/api/ProfileService/GetPersonData",
            "test" to "1",
            "sessionId" to "${storageResponse["sessionId"]}"
        )

        // Тестовый запрос данных с сервера
        val networkResponse = networkRepository.getRequestData(parameters)

        if (networkResponse["responseCode"] != "200") {

            if (storageResponse["offline"] != "1") {
                storageRepository.setOffline()

                val time = System.currentTimeMillis().toString()
                lessonDao.insertUpdate(EntityUpdate(time))
            }
            return response
        } else {
            if (storageResponse["offline"] != "0") {
                storageRepository.setOnline()

                val time = System.currentTimeMillis().toString()
                lessonDao.insertUpdate(EntityUpdate(time))
            }
        }

        val login = storageResponse["login"]
        val password = storageResponse["password"]

        // Получаем id пользователя
        var userId: Int?
        coroutineScope {
            userId = lessonDao.getUserIdByLogin("$login")
        }

        // Если новый пользователь...
        if (userId == null) {
            coroutineScope {
                // Очищаем все таблицы
                lessonDao.clearData()

                // Добавляем нового пользователя в базу
                lessonDao.insertUser(EntityUser(0, "$login", "$password}"))
            }
        }

        //--> Получаем с сервера список уроков на текущую неделю
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        val calendar = Calendar.getInstance()

        val todayDate = dateFormat.format(calendar.time)

        parameters = mapOf(
            "url" to "https://e-school.ryazangov.ru/api/ScheduleService/GetDiary?date=$todayDate&is_diary=true",
            "test" to "0",
            "sessionId" to "${storageResponse["sessionId"]}"
        )

        val diaryList = mutableListOf<Map<String, String>>()

        var responseGetDiary: Map<String, String>?
        coroutineScope {
            responseGetDiary = networkRepository.getRequestData(parameters)
        }

        if (responseGetDiary == null || responseGetDiary!!["responseCode"] != "200" || responseGetDiary!!["responseData"] == "") return response

        var responseParseDiary: List<Map<String, String>>?
        coroutineScope {
            responseParseDiary = responseGetDiary!!["responseData"]?.let { parseDiary(it) }
        }

        responseParseDiary?.forEach {
            diaryList.add(it)
        }

        responseParseDiary = null
        responseGetDiary = null
        //<--

        //--> Получаем с сервера список уроков на следующую неделю
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek + 1)

        val nextWeekDate = dateFormat.format(calendar.time)

        parameters = mapOf(
            "url" to "https://e-school.ryazangov.ru/api/ScheduleService/GetDiary?date=$nextWeekDate&is_diary=true",
            "test" to "0",
            "sessionId" to "${storageResponse["sessionId"]}"
        )

        coroutineScope {
            responseGetDiary = networkRepository.getRequestData(parameters)
        }

        if (responseGetDiary != null && responseGetDiary!!["responseCode"] == "200" && responseGetDiary!!["responseData"] != "") {
            coroutineScope {
                responseParseDiary = parseDiary(responseGetDiary!!["responseData"]!!)
            }
        }

        responseParseDiary?.forEach {
            diaryList.add(it)
        }

        responseParseDiary = null
        responseGetDiary = null
        //<--

        //-->  Получаем с сервера список текущих оценок
        parameters = mapOf(
            "url" to "https://e-school.ryazangov.ru/api/MarkService/GetSummaryMarks?date=$todayDate",
            "test" to "0",
            "sessionId" to "${storageResponse["sessionId"]}"
        )

        var responseGetSummaryMarks: Map<String, String>?
        coroutineScope {
            responseGetSummaryMarks = networkRepository.getRequestData(parameters)
        }

        var summaryMarks: List<Map<String, String>>? = null
        if (responseGetSummaryMarks != null && responseGetSummaryMarks!!["responseCode"] == "200" && responseGetSummaryMarks!!["responseData"] != "") {
            coroutineScope {
                summaryMarks = parseSummaryMarks(responseGetSummaryMarks!!["responseData"]!!)
            }
        }

        responseGetSummaryMarks = null
        //<--

        //--> Обрабатываем список предметов
        var lessonList = mutableListOf<String>()
        diaryList.forEach {
            if (it["lesson"] !in lessonList) {
                lessonList.add(it["lesson"]!!)
            }
        }
        summaryMarks?.forEach {
            if (it["lesson"] !in lessonList) {
                lessonList.add(it["lesson"]!!)
            }
        }

        var lessonListNameToBase: List<String>?
        coroutineScope {
            lessonListNameToBase = lessonDao.getListLessonByName(lessonList)
        }

        lessonListNameToBase?.forEach {
            lessonList.remove(it)
        }
        lessonListNameToBase = null

        val listNewLesson = mutableListOf<EntityLesson>()
        lessonList.forEach {
            listNewLesson.add(EntityLesson(0, it))
        }
        if (listNewLesson.isNotEmpty()) {
            listNewLesson.sortBy { it.name }

            coroutineScope {
                lessonDao.insertLessonList(listNewLesson)
            }
        }

        lessonList.clear()
        listNewLesson.clear()
        //<--

        //--> Обновляем расписание предметов
        lessonList = mutableListOf()

        diaryList.forEach {
            if (it["lesson"] !in lessonList) {
                lessonList.add(it["lesson"]!!)
            }
        }

        // Получаем список предметов из базы
        var listAllLesson: List<EntityLesson>?
        listAllLesson = null

        coroutineScope {
            listAllLesson = lessonDao.getListLesson()
        }

        // Собираем массив для insert
        val listNewSchedule = mutableListOf<EntitySchedule>()

        diaryList.forEach { diary ->
            val lessonName = diary["lesson"]

            val lessonId = getLessonId("$lessonName", listAllLesson!!)

            listNewSchedule.add(
                EntitySchedule(
                    "${diary["date"]}",
                    lessonId,
                    "${diary["timeRun"]}",
                    "${diary["timeEnd"]}",
                    "${diary["theme"]}",
                    "${diary["homework"]}",
                )
            )
        }

        lessonList.clear()

        // Обновление расписания
        coroutineScope {
            lessonDao.deleteAllSchedules()
            lessonDao.insertScheduleList(listNewSchedule)
        }

        listNewSchedule.clear()
        diaryList.clear()
        //<--

        //--> Текущие оценки
        var listMarks: MutableList<EntityMark>?
        listMarks = null

        coroutineScope {
            listMarks = lessonDao.getListMark()
        }
        //<--

        //--> Новые оценки
        val listNewMarks = mutableListOf<EntityMark>()

        summaryMarks?.forEach { marks ->
            val lessonName = marks["lesson"]

            val lessonId = getLessonId("$lessonName", listAllLesson!!)

            val mark = marks["mark"]!!.toInt()

            listNewMarks.add(
                EntityMark(
                    "${marks["date"]}",
                    lessonId,
                    mark,
                    "${marks["description"]}",
                    0
                )
            )
        }

        listNewMarks.sortWith(
            compareBy(
                { it.lessonId },
                { it.date },
                { it.mark },
                { it.description })
        )

        // Сравнение старых и новых
        if (listMarks != listNewMarks) {
            var reportMessage: String
            reportMessage = ""

            val listMarksLeft = listMarks!! - listNewMarks
            val listMarksRight = listNewMarks - listMarks!!

            // Обновляем иаблицу оценок
            val listNewMarksInsert = mutableListOf<EntityMark>()
            var number = 0
            listNewMarks.forEach {
                number++
                listNewMarksInsert.add(
                    EntityMark(
                        it.date,
                        it.lessonId,
                        it.mark,
                        it.description,
                        number
                    )
                )
            }
            coroutineScope {
                lessonDao.deleteAllMarks()
                lessonDao.insertMark(listNewMarksInsert)
            }
            listNewMarksInsert.clear()

            val listMarksLeftCount = listMarksLeft.count()
            val listMarksRightCount = listMarksRight.count()

            if (listMarksLeftCount > 1 || listMarksRightCount > 1) {
                response["title"] = "Есть несколько изменений"
                response["text"] = "Подробности смотри в отчёте"

                reportMessage = "Было:\n"
                listMarksLeft.forEach { left ->
                    val lessonName = getLessonName(left.lessonId, listAllLesson!!)

                    reportMessage += "${convertDate(left.date)} / $lessonName / Оценка: ${left.mark} - ${left.description}\n"
                }

                reportMessage += "\nСтало:"
                listMarksRight.forEach { right ->
                    val lessonName = getLessonName(right.lessonId, listAllLesson!!)

                    reportMessage += "\n${convertDate(right.date)} / $lessonName / Оценка: ${right.mark} - ${right.description}"
                }

            } else if (listMarksLeftCount == 0 && listMarksRightCount == 1) {
                val lessonName = getLessonName(listMarksRight[0].lessonId, listAllLesson!!)

                response["title"] = "Новая оценка / ${convertDate(listMarksRight[0].date)}"
                response["text"] =
                    "$lessonName / Оценка: ${listMarksRight[0].mark}\n${listMarksRight[0].description}"

                reportMessage = response["title"] + "\n" + response["text"]
            } else if (listMarksLeftCount == 1 && listMarksRightCount == 0) {
                val lessonName = getLessonName(listMarksLeft[0].lessonId, listAllLesson!!)

                response["title"] = "Оценка удалена / ${convertDate(listMarksLeft[0].date)}"
                response["text"] =
                    "$lessonName / Оценка: ${listMarksLeft[0].mark}\n${listMarksLeft[0].description}"

                reportMessage = response["title"] + "\n" + response["text"]
            } else {
                if (listMarksLeft[0].lessonId == listMarksRight[0].lessonId && listMarksLeft[0].mark != listMarksRight[0].mark) {
                    val lessonName = getLessonName(listMarksRight[0].lessonId, listAllLesson!!)

                    response["title"] = "Оценка изменена / ${convertDate(listMarksRight[0].date)}"
                    response["text"] =
                        "$lessonName / Оценка: ${listMarksLeft[0].mark} на ${listMarksRight[0].mark}\n${listMarksRight[0].description}"

                    reportMessage = response["title"] + "\n" + response["text"]
                } else if (listMarksLeft[0].lessonId == listMarksRight[0].lessonId) {

                    if (listMarksLeft[0].lessonId == listMarksRight[0].lessonId && listMarksLeft[0].date != listMarksRight[0].date) {
                        val lessonName = getLessonName(listMarksRight[0].lessonId, listAllLesson!!)

                        response["title"] =
                            "Дата изменена / ${convertDate(listMarksLeft[0].date)} -> ${
                                convertDate(
                                    listMarksRight[0].date
                                )
                            }"
                        response["text"] =
                            "$lessonName / Оценка: ${listMarksRight[0].mark}\n${listMarksRight[0].description}"

                        reportMessage = response["title"] + "\n" + response["text"]
                    } else {
                        val lessonName = getLessonName(listMarksRight[0].lessonId, listAllLesson!!)

                        response["title"] =
                            "Подпись изменена / ${convertDate(listMarksRight[0].date)}"
                        response["text"] =
                            "$lessonName / Оценка: ${listMarksRight[0].mark}\n${listMarksRight[0].description}"

                        reportMessage = response["title"] + "\n" + response["text"]
                    }
                } else {
                    response["title"] = "Есть несколько изменений"
                    response["text"] = "Подробности смотри в отчёте"

                    reportMessage = "Было:\n"
                    listMarksLeft.forEach { left ->
                        val lessonName = getLessonName(left.lessonId, listAllLesson!!)

                        reportMessage += "${convertDate(left.date)} / $lessonName / Оценка: ${left.mark} - ${left.description}\n"
                    }

                    reportMessage += "\nСтало:"
                    listMarksRight.forEach { right ->
                        val lessonName = getLessonName(right.lessonId, listAllLesson!!)

                        reportMessage += "\n${convertDate(right.date)} / $lessonName / Оценка: ${right.mark} - ${right.description}"
                    }
                }
            }

            coroutineScope {
                lessonDao.insertReport(EntityReport(reportMessage))

                val time = System.currentTimeMillis().toString()
                lessonDao.insertUpdate(EntityUpdate(time))
            }
        }

        listNewMarks.clear()
        listMarks = null
        listAllLesson = null
        summaryMarks = null
        //<--

        return response
    }

    override suspend fun getLessonDetail(lessonId: Int): Map<String, String> {
        var lessonName = ""
        var lessonMarks = ""
        var lessonMarksCount = 0
        var lessonMarksSumm = 0
        var lessonMarksaverage = 0F

        coroutineScope {
            lessonName = lessonDao.getLessonNameById(lessonId)

            val lessonMarksResponse: List<EntityMark> = lessonDao.getListMarkById(lessonId)
            lessonMarksResponse.forEach {
                if (lessonMarks != "") {
                    lessonMarks += "\n"
                }

                lessonMarks += "${convertDate(it.date)} оценка ${it.mark} - ${it.description}"

                lessonMarksCount++

                lessonMarksSumm += it.mark
            }
        }

        var info = "Дполнительных оценок не требуется.\nТы молодец! Так держать!"
        if (lessonMarksCount > 0) {
            lessonMarksaverage =
                round(lessonMarksSumm.toFloat() / lessonMarksCount.toFloat() * 100) / 100

            var countFive = 0

            if (lessonMarksaverage < 3.56) {
                var countDump = lessonMarksCount
                var averageDump = lessonMarksaverage

                while (averageDump < 3.56) {
                    countFive++
                    countDump++
                    lessonMarksSumm += 5
                    averageDump = round(lessonMarksSumm.toFloat() / countDump.toFloat() * 100) / 100
                }
            }

            if (lessonMarksCount + countFive < 3) {
                countFive += 3 - (lessonMarksCount + countFive)
            }

            if (countFive > 0) {
                info = "Нужно ещё 5: $countFive шт."
            }
        } else {
            info = "Нужно ещё 5: 3 шт."
        }

        if (lessonMarks == "") {
            lessonMarks = "нет оценок"
        }

        val response = mutableMapOf<String, String>()
        response["id"] = "$lessonId"
        response["name"] = lessonName
        response["marks"] = lessonMarks
        response["count"] = "Всего оценок: $lessonMarksCount"
        response["average"] = "Средняя оценка: $lessonMarksaverage"
        response["info"] = info

        return response
    }

    private fun getLessonName(id: Int, lesson: List<EntityLesson>): String {
        var name = ""
        lesson.forEach lesson@{
            if (it.id == id) {
                name = it.name
                return@lesson
            }
        }

        return name
    }

    private fun getLessonId(name: String, lesson: List<EntityLesson>): Int {
        var id = 0
        lesson.forEach lesson@{
            if (it.name == name) {
                id = it.id
                return@lesson
            }
        }

        return id
    }

    //2022-04-20 -> 20.04.2022
    private fun convertDate(dateStr: String): String {
        val stringArray: List<String> = dateStr.split("-")

        return stringArray[2] + "." + stringArray[1] + "." + stringArray[0]
    }

    private fun parseSummaryMarks(json: String): MutableList<Map<String, String>> {
        val marksList = mutableListOf<Map<String, String>>()

        val jsonArray = JSONObject(json).getJSONArray("discipline_marks")

        for (itemData in Array(jsonArray.length()) { i -> jsonArray.getJSONObject(i) }) {
            val lesson = editStr("${itemData["discipline"]}")

            val marksArray = itemData.getJSONArray("marks")

            for (itemMarks in Array(marksArray.length()) { i -> marksArray.getJSONObject(i) }) {
                val date = "${itemMarks["date"]}".trim()
                val mark = "${itemMarks["mark"]}".trim()
                val description = editStr("${itemMarks["description"]}".substringBefore(":"))

                val map = mapOf(
                    "lesson" to lesson,
                    "date" to date,
                    "mark" to mark,
                    "description" to description
                )
                marksList.add(map)
            }
        }

        return marksList
    }

    private fun parseDiary(json: String): List<Map<String, String>> {
        val diaryList = mutableListOf<Map<String, String>>()

        val jsonArray = JSONObject(json).getJSONArray("days")

        for (itemData in Array(jsonArray.length()) { i -> jsonArray.getJSONObject(i) }) {
            if (!itemData.optBoolean("is_weekend")) {
                val lessonsArray = itemData.getJSONArray("lessons")

                for (itemLesson in Array(lessonsArray.length()) { i -> lessonsArray.getJSONObject(i) }) {
                    val date = itemLesson["date"].toString().trim()

                    val lesson = editStr("${itemLesson["discipline"]}")

                    val timeRun = "${itemLesson["time_begin"]}".trim()
                    val timeEnd = "${itemLesson["time_end"]}".trim()
                    val theme = "${itemLesson["theme"]}".trim()

                    val homework: String =
                        Html.fromHtml(itemLesson["homework"].toString(), Html.FROM_HTML_MODE_LEGACY)
                            .toString()
                            .replace("\n", " ")
                            .replace(" ", " ")
                            .replace("\u200B", "")
                            .replace("  ", " ")
                            .trim()

                    //val files = itemLesson["materials"] TODO Найти расписание с файлами и обработать их

                    val map = mapOf(
                        "date" to date,
                        "lesson" to lesson,
                        "timeRun" to timeRun,
                        "timeEnd" to timeEnd,
                        "theme" to theme,
                        "homework" to homework
                    )
                    diaryList.add(map)
                }
            }
        }

        return diaryList
    }

    private fun editStr(str: String): String {
        var newStr = str.trim().lowercase()
        newStr = newStr[0].uppercase() + newStr.drop(1)

        return newStr
    }
}