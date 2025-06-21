package com.example.jsouplib.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jsouplib.bean.TodayFortuneBean
import com.example.jsouplib.utils.Contant.URL_PART_DAY
import com.example.jsouplib.utils.Contant.URL_PART_HOUR
import com.example.jsouplib.utils.Contant.URL_PART_MONTH
import com.example.jsouplib.utils.Contant.URL_PART_NAME
import com.example.jsouplib.utils.Contant.URL_PART_SEX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * @author cmz$
 * @date 2025/6/20$
 * @description
 */
class TodayFortuneViewModel : ViewModel() {
    private  val URL_TODAY_PART = "http://www.ibazi.cn/gratis/ziweiriyunshi/result.php?iYear="

    val todayFortuneDataFlow = MutableStateFlow<TodayFortuneBean?>(null)
    /**
     * Get today's fortune data
     * @param dateOfBirth Date of birth, in the format yyyy-MM-dd
     * @param timeOfBirth Time of birth ,0-23
     *                      (00:00-00:59   早子)
     *                      (01:00-02:59   丑)
     *                      (03:00-04:59   寅)
     *                      (05:00-06:59   卯)
     *                      (07:00-08:59   辰)
     *                      (09:00-10:59   巳)
     *                      (11:00-12:59   午)
     *                      (13:00-14:59   未)
     *                      (15:00-16:59   申)
     *                      (17:00-18:59   酉)
     *                      (19:00-20:59   戌)
     *                      (21:00-22:59   亥)
     *                      (23:00-23:59   晚子)
     * @param genderType  1-man, 2-woman
     * @param name
     * @param onSuccess Request successfully executed this code block
     * @param onError   Request failed to execute this code block
     */
    fun getTodayFortuneData(
        dateOfBirth: String, timeOfBirth: Int,
        genderType: Int, name: String,
        onStart : (() -> Unit)? = null,
        onSuccess: (() -> Unit)? = null,
        onError: ((Exception) -> Unit)? = null
    ) {
        var url = ""
        var currentGender = "man"
        val dateSplit = dateOfBirth.split("-")
        when (genderType) {
            1 -> currentGender = "man"
            2 -> currentGender = "woman"
        }
        url = "${URL_TODAY_PART}${dateSplit[0]}" +
                "${URL_PART_MONTH}${dateSplit[1]}" +
                "${URL_PART_DAY}${dateSplit[2]}" +
                "${URL_PART_HOUR}${timeOfBirth}" +
                "${URL_PART_SEX}${currentGender}" +
                "${URL_PART_NAME}${name}"
        Log.d("MainActivity", "getTodayFortuneData:${url} ")
        viewModelScope.launch {
            try {
                onStart?.let { it() }
                withContext(Dispatchers.IO) {
                    val document: Document = Jsoup.connect(url).get()
                    //顶部文案
                    val element: Element? = document.select("span.b").first()
                    val content: List<String>? = element?.text()?.split(" ")

                    //今日运文案
                    val element1: String = document.select("p.mal25").first()?.text() ?: ""
                    val content1 = element1.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()

                    //底部文案
                    val element2: Element? = document.select("div.jsjs03").first()
                    val text: String = element2?.text().toString()
                    val content2 = text.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()

                    val content3 = document.select("span.sco02").text()
                    val todayFortuneBean = TodayFortuneBean(content, content1.toList(), content2.toList(), content3)
                    todayFortuneDataFlow.value = todayFortuneBean
                }
                onSuccess?.let { it() }
            } catch (e: Exception) {
                onError?.let { it(e) }
            }
        }

    }
}