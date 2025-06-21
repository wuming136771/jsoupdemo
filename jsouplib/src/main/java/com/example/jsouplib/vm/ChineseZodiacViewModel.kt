package com.example.jsouplib.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.TimeUtils
import com.example.jsouplib.bean.ChineseZodiacBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

/**
 * @author cmz$
 * @date 2025/6/20$
 * @description
 */
class ChineseZodiacViewModel : ViewModel() {

    private val URL_ZODIAC_SIGN_PART_1 = "http://www.ibazi.cn/gratis/animal/result.php?sx="
    private val URL_ZODIAC_SIGN_PART_2 = "&m="

    val chineseZodiac0Flow = MutableStateFlow<ChineseZodiacBean?>(null)
    val chineseZodiac1Flow = MutableStateFlow<ChineseZodiacBean?>(null)
    val currentPositionFlow = MutableStateFlow(0)
    val isFirst0Flow = MutableStateFlow(true)
    val isFirst1Flow = MutableStateFlow(true)

    /**
     * Zodiac sign data
     * @param dateOfBirth
     * @param type 0-This month 1-Next month
     */
    fun getZodiacSignData(
        dateOfBirth: String,
        type: Int,
        onStart: (() -> Unit)? = null,
        onSuccess: (() -> Unit)? = null,
        onError: ((Exception) -> Unit)? = null
    ) {
        viewModelScope.launch {
            try {
                onStart?.let { it() }
                withContext(Dispatchers.IO) {
                    var url = ""
                    val currentZodiac =
                        TimeUtils.getChineseZodiac(TimeUtils.string2Millis(dateOfBirth))
                    var currentSx = 0
                    when (currentZodiac) {
                        "鼠" -> {
                            currentSx = 0
                        }

                        "牛" -> {
                            currentSx = 1
                        }

                        "虎" -> {
                            currentSx = 2
                        }

                        "兔" -> {
                            currentSx = 3
                        }

                        "龙" -> {
                            currentSx = 4
                        }

                        "蛇" -> {
                            currentSx = 5
                        }

                        "马" -> {
                            currentSx = 6
                        }

                        "羊" -> {
                            currentSx = 7
                        }

                        "猴" -> {
                            currentSx = 8
                        }

                        "鸡" -> {
                            currentSx = 9
                        }

                        "狗" -> {
                            currentSx = 10
                        }

                        "猪" -> {
                            currentSx = 11
                        }
                    }
                    url = URL_ZODIAC_SIGN_PART_1 + currentSx + URL_ZODIAC_SIGN_PART_2 + type
                    val doc = Jsoup.connect(url).get()
                    //获取生肖运势
                    val selects = doc.select("div.leftzde").first()
                    val ul = selects!!.select("ul").first()
                    val li = ul!!.select("li")

                    val yunqitextList: MutableList<String> =
                        ArrayList()
                    for (element in li) {
                        yunqitextList.add(element.text())
                    }

                    //获取文案
                    val select = doc.select("dd.yueyun>p")
                    val wenanList: MutableList<String> =
                        ArrayList()
                    for (element in select) {
                        wenanList.add(element.text())
                        Log.d("mmmmmmm", "文案:==== " + element.text())
                    }

                    //获取幸运镜囊文案
                    val jingnangList: MutableList<String> =
                        ArrayList()
                    val select1 = doc.select("p.huazhong")
                    for (element in select1) {
                        val span = element.select("span")
                        for (spans in span) {
                            jingnangList.add(spans.text())
                        }
                    }

                    //获取表格数据
                    val tableElements = doc.select("td.aiwenzi")
                    val tableList: MutableList<String> =
                        ArrayList()
                    for (tableElement in tableElements) {
                        tableList.add(tableElement.text())
                    }

                    val tableElements2 = doc.select("td.shiwenzi")
                    val tableList2: MutableList<String> =
                        ArrayList()
                    for (tableElement in tableElements2) {
                        tableList2.add(tableElement.text())
                    }

                    val tableElements3 = doc.select("td.caiwenzi")
                    val tableList3: MutableList<String> =
                        ArrayList()
                    for (tableElement in tableElements3) {
                        tableList3.add(tableElement.text())
                    }

                    val tableElements4 = doc.select("td.jianwenzi")
                    val tableList4: MutableList<String> =
                        ArrayList()
                    for (tableElement in tableElements4) {
                        tableList4.add(tableElement.text())
                    }
                    when (type) {
                        0 -> {
                            chineseZodiac0Flow.value = ChineseZodiacBean(
                                content1 = yunqitextList,
                                content2 = wenanList,
                                content3 = jingnangList,
                                content4 = tableList,
                                content5 = tableList2,
                                content6 = tableList3,
                                content7 = tableList4
                            )
                        }

                        1-> {
                            chineseZodiac1Flow.value = ChineseZodiacBean(
                                content1 = yunqitextList,
                                content2 = wenanList,
                                content3 = jingnangList,
                                content4 = tableList,
                                content5 = tableList2,
                                content6 = tableList3,
                            )
                        }
                     }
                }
                onSuccess?.let { it() }
            } catch (e: Exception) {
                onError?.let { it(e) }
            }
        }
    }
}