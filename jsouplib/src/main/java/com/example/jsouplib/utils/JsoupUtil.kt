package com.example.jsouplibrary.utils

import android.util.Log
import android.view.View
import com.example.jsouplibrary.vm.DataViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.IOException
import kotlin.concurrent.thread

/**
 * @author cmz$
 * @date 2025/6/11$
 * @description
 */
object JsoupUtil {
    private val scope = MainScope()
    val dataVm = DataViewModel.instance
    private const val URL_PART_1 = "http://www.ibazi.cn/gratis/ziweiriyunshi/result.php?iYear="
    private const val URL_PART_2 = "&iMonth="
    private const val URL_PART_3 = "&iDay="
    private const val URL_PART_4 = "&iHour="
    private const val URL_PART_5 = "&iSex="
    private const val URL_PART_6 = "&NickData="


    /**
     * 获取今日运势数据
     * @param dateOfBirth 出生日期, 格式为 yyyy-MM-dd
     * @param timeOfBirth 出生时辰 ,0-23
     * @param sex 性别, 1-男, 2-女
     * @param name 姓名
     */
    fun getTodayFortuneData(
        dateOfBirth: String, timeOfBirth: String,
        sex: String, name: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        var url = ""
        val dateSplit = dateOfBirth.split("-")
        url = "${URL_PART_1}${dateSplit[0]}${URL_PART_2}${dateSplit[1]}${URL_PART_3}${dateSplit[2]}${URL_PART_4}${timeOfBirth}${URL_PART_5}${sex}${URL_PART_6}${name}}"
        scope.launch {
            try {
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
                    dataVm.setData1(content)
                    dataVm.setData2(content1)
                    dataVm.setData3(content2)
                    dataVm.setData4(content3)
                }
                onSuccess()
            } catch (e: Exception) {
                onError()
            }
        }

    }


    fun destroy() {
        scope.cancel()
    }

}