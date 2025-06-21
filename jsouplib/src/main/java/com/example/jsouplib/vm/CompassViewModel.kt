package com.example.jsouplib.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jsouplib.bean.CompassBean
import com.example.jsouplib.utils.Contant.URL_PART_DAY
import com.example.jsouplib.utils.Contant.URL_PART_HOUR
import com.example.jsouplib.utils.Contant.URL_PART_MONTH
import com.example.jsouplib.utils.Contant.URL_PART_NAME
import com.example.jsouplib.utils.Contant.URL_PART_SEX
import com.example.jsouplib.utils.StringUtils
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
class CompassViewModel : ViewModel() {

    private val URL_COMPASS_PART_1 = "http://www.ibazi.cn/gratis/rshndzwmp/"
    private val URL_COMPASS_PART_2 = "result01"
    private val URL_COMPASS_PART_3 = ".php?iYear="

    val compassDataFlow = MutableStateFlow<CompassBean?>(null)
    val compassTab2SpanFlow = MutableStateFlow<String?>(null)
    val compassTab3SpanFlow = MutableStateFlow<String?>(null)
    val compassTab4SpanFlow = MutableStateFlow<String?>(null)
    val compassTab5SpanFlow = MutableStateFlow<String?>(null)
    val compassTab6SpanFlow = MutableStateFlow<String?>(null)

    val compassTab2StrListFlow = MutableStateFlow<List<String>?>(null)
    val compassTab3StrListFlow = MutableStateFlow<List<String>?>(null)
    val compassTab4StrListFlow = MutableStateFlow<List<String>?>(null)
    val compassTab5StrListFlow = MutableStateFlow<List<String>?>(null)
    val compassTab6StrListFlow = MutableStateFlow<List<String>?>(null)

    val tab1IsFirst = MutableStateFlow(true)
    val tab2IsFirst = MutableStateFlow(true)
    val tab3IsFirst = MutableStateFlow(true)
    val tab4IsFirst = MutableStateFlow(true)
    val tab5IsFirst = MutableStateFlow(true)
    val tab6IsFirst = MutableStateFlow(true)

    /**
     *Get compass data
     * @param dateOfBirth Date of birth, in the format yyyy-MM-dd
     *      * @param timeOfBirth Time of birth ,0-23
     *      *                      (00:00-00:59   早子)
     *      *                      (01:00-02:59   丑)
     *      *                      (03:00-04:59   寅)
     *      *                      (05:00-06:59   卯)
     *      *                      (07:00-08:59   辰)
     *      *                      (09:00-10:59   巳)
     *      *                      (11:00-12:59   午)
     *      *                      (13:00-14:59   未)
     *      *                      (15:00-16:59   申)
     *      *                      (17:00-18:59   酉)
     *      *                      (19:00-20:59   戌)
     *      *                      (21:00-22:59   亥)
     *      *                      (23:00-23:59   晚子)
     * @param genderType  1-man, 2-woman
     *
     */
    fun getCompassData(
        dateOfBirth: String, timeOfBirth: Int,
        genderType: Int, name: String,
        onStart: (() -> Unit)? = null,
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
        url = URL_COMPASS_PART_1 +
                URL_COMPASS_PART_2 +
                "${URL_COMPASS_PART_3}${dateSplit[0]}" +
                "${URL_PART_MONTH}${dateSplit[1]}" +
                "${URL_PART_DAY}${dateSplit[2]}" +
                "${URL_PART_HOUR}${timeOfBirth}" +
                "${URL_PART_SEX}${currentGender}" +
                "${URL_PART_NAME}${name}"
        viewModelScope.launch {
            try {
                onStart?.let { it() }
                withContext(Dispatchers.IO) {
                    val data1List: MutableList<String> =
                        ArrayList()
                    val data2List: MutableList<String> =
                        ArrayList()
                    val doc = Jsoup.connect(url)
                        .ignoreContentType(true)
                        .timeout(50000)
                        .get()
                    //十二块表盘数据
                    val select = doc.select("div.BOX")
                    for (element in select) {
                        val select1 = element.select("div.LORD")
                        val sb = StringBuilder()
                        for (element1 in select1) {
                            sb.append(element1.select("div.LT01").text())
                        }
                        data1List.add(sb.toString()) //top
                        data1List.add(element.select("div.PAL").text()) //start
                        data1List.add(element.select("div.YEAR").text()) //end
                    }
                    //十二块表盘中间数据
                    //未点击时显示的数据
                    val select2 = doc.select("p.ZIWEI_C_t05")
                    for (element in select2) {
                        data2List.add(element.text())
                    }
                    //显示时候的数据
                    val body = doc.toString()
                    val ss: Array<String> = StringUtils.substringsBetween(
                        body,
                        "='",
                        "';"
                    )
                    val compassBean = CompassBean(data1List, data2List, ss.toList())
                    compassDataFlow.value = compassBean
                }
                onSuccess?.let { it() }
            } catch (e: Exception) {
                onError?.let { it(e) }
            }
        }
    }


    /**
     * Get other compass tab data
     * @param dateOfBirth Date of birth, in the format yyyy-MM-dd
     * @param timeOfBirth Time of birth ,0-23
     * @param genderType  1-man, 2-woman
     * @param resultType  2-命宫主星, 3-夫妻宫,4-事业宫, 5-财帛宫 6-父母宫
     *                      If necessary, you can use enumeration
     *
     */
    fun getCompassOtherTabData(
        dateOfBirth: String,
        timeOfBirth: Int,
        genderType: Int,
        name: String,
        resultType: Int,
        onStart: (() -> Unit)? = null,
        onSuccess: (() -> Unit)? = null,
        onError: ((Exception) -> Unit)? = null
    ) {
        var url = ""
        var currentResultURL = ""
        var currentGender = "man"
        val dateSplit = dateOfBirth.split("-")
        when (genderType) {
            1 -> currentGender = "man"
            2 -> currentGender = "woman"
        }
        when (resultType) {
            2 -> currentResultURL = "result02"
            3 -> currentResultURL = "result03"
            4 -> currentResultURL = "result04"
            5 -> currentResultURL = "result05"
            6 -> currentResultURL = "result06"
        }

        url = URL_COMPASS_PART_1 +
                currentResultURL +
                "${URL_COMPASS_PART_3}${dateSplit[0]}" +
                "${URL_PART_MONTH}${dateSplit[1]}" +
                "${URL_PART_DAY}${dateSplit[2]}" +
                "${URL_PART_HOUR}${timeOfBirth}" +
                "${URL_PART_SEX}${currentGender}" +
                "${URL_PART_NAME}${name}"

        viewModelScope.launch {
            try {
                onStart?.let { it() }
                withContext(Dispatchers.IO) {
                    val doc = Jsoup.connect(url).get()
                    val element = doc.select("table")
                    if (element.size > 5) {
                        val element1 = element[5]
                        val span = element1.select("span.ti03").text()
                        val select = element1.select("td.tx02")
                        val strList: MutableList<String> =
                            java.util.ArrayList()
                        for (element2 in select) {
                            val text = element2.text()
                            strList.add(text)
                        }
                        when (resultType) {
                            2 -> {
                                compassTab2SpanFlow.value = span
                                compassTab2StrListFlow.value = strList
                            }

                            3 -> {
                                compassTab3SpanFlow.value = span
                                compassTab3StrListFlow.value = strList
                            }

                            4 -> {
                                compassTab4SpanFlow.value = span
                                compassTab4StrListFlow.value = strList
                            }

                            5 -> {
                                compassTab5SpanFlow.value = span
                                compassTab5StrListFlow.value = strList
                            }

                            6 -> {
                                compassTab6SpanFlow.value = span
                                compassTab6StrListFlow.value = strList
                            }
                        }
                    }
                }
                onSuccess?.let { it() }
            }catch (e: Exception){
                onError?.let { it(e) }
            }
        }
    }


    fun setTab1IsFirst(isFirst: Boolean) {
        tab1IsFirst.value = isFirst
    }

    fun setTab2IsFirst(isFirst: Boolean) {
        tab2IsFirst.value = isFirst
    }

    fun setTab3IsFirst(isFirst: Boolean) {
        tab3IsFirst.value = isFirst
    }

    fun setTab4IsFirst(isFirst: Boolean) {
        tab4IsFirst.value = isFirst
    }

    fun setTab5IsFirst(isFirst: Boolean) {
        tab5IsFirst.value = isFirst
    }

    fun setTab6IsFirst(isFirst: Boolean) {
        tab6IsFirst.value = isFirst
    }


}