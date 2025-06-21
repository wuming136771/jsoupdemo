package com.example.jsouplib.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.TimeUtils
import com.example.jsouplib.bean.ConstellationBean
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
class ConstellationViewModel : ViewModel() {
    private val URL_CONSTELLATION_PART_1 = "http://www.ibazi.cn/gratis/astro/daily.php?"
    private val URL_CONSTELLATION_PART_2 = "iAstro="
    private val URL_CONSTELLATION_PART_3 = "&astrotime="
    private val URL_CONSTELLATION_PART_4 = "&iType="


    val constellationShortComment0Flow = MutableStateFlow<String?>(null)
    val constellationShortComment4Flow = MutableStateFlow<String?>(null)
    val constellationShortComment1Flow = MutableStateFlow<String?>(null)
    val constellationShortComment2Flow = MutableStateFlow<String?>(null)

    val constellationPicList0Flow = MutableStateFlow<List<String>?>(null)
    val constellationPicList4Flow = MutableStateFlow<List<String>?>(null)
    val constellationPicList1Flow = MutableStateFlow<List<String>?>(null)
    val constellationPicList2Flow = MutableStateFlow<List<String>?>(null)

    val constellationTxtList0Flow = MutableStateFlow<List<String>?>(null)
    val constellationTxtList4Flow = MutableStateFlow<List<String>?>(null)
    val constellationTxtList1Flow = MutableStateFlow<List<String>?>(null)
    val constellationTxtList2Flow = MutableStateFlow<List<String>?>(null)

    /**
     * Constellation data
     * @param dateOfBirth Date of birth, in the format yyyy-MM-dd
     * @param type 0-Today's fortune, 4-Tomorrow's Luck, 1-This week's fortune , 2-This month's fortune
     *
     */
    fun getConstellationData(
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
                    var currentIastro = 0
                    val currentConstellation =
                        TimeUtils.getZodiac(TimeUtils.string2Millis(dateOfBirth))
                    when (currentConstellation) {
                        "白羊座" -> {
                            currentIastro = 0
                        }

                        "金牛座" -> {
                            currentIastro = 1
                        }

                        "双子座" -> {
                            currentIastro = 2
                        }

                        "巨蟹座" -> {
                            currentIastro = 3
                        }

                        "狮子座" -> {
                            currentIastro = 4
                        }

                        "处女座" -> {
                            currentIastro = 5
                        }

                        "天秤座" -> {
                            currentIastro = 6
                        }

                        "天蝎座" -> {
                            currentIastro = 7
                        }

                        "射手座" -> {
                            currentIastro = 8
                        }

                        "摩羯座" -> {
                            currentIastro = 9
                        }

                        "水瓶座" -> {
                            currentIastro = 10
                        }

                        "双鱼座" -> {
                            currentIastro = 11
                        }
                    }
                    url = URL_CONSTELLATION_PART_1 +
                            URL_CONSTELLATION_PART_2 +
                            "$currentIastro" +
                            "${URL_CONSTELLATION_PART_3}${dateOfBirth}" +
                            "${URL_CONSTELLATION_PART_4}${type}"
                    Log.d("MainActivity", "url:${url} ")
                    val document = Jsoup.connect(url).get()
                    //图片列表
                    val picElements = document.select("div.gy_right_ystb_ico")
                    //欢爱图片
                    val haDoc = document.select("div.xxk_k1_bt03").first()
                    var haPic = ""
                    haDoc?.let {
                        haPic = haDoc.select("img").attr("src")
                    }
                    //星座图
                    val topPicElements = document.select("div.gy_right_xia_xinzuo")
                    //短评
                    val dpContent = document.select("div.gy_right_xia_you_hang1").text()
                    val picList: MutableList<String> = ArrayList()
                    for (picElement in picElements) {
                        picList.add(picElement.select("img").attr("src"))
                    }
                    //文案
                    val textList: MutableList<String> = ArrayList()
                    val select = document.select("div.xxk_k1_left_wz01")
                    for (element in 0..<select.size) {
                        textList.add(select[element].text())
                        Log.d("cmz", "文案: " + element + select[element].text())
                    }
                    val constellationBean = ConstellationBean(haPic, dpContent, picList, textList)
                    when(type){
                        0 ->{
                            constellationShortComment0Flow.value = constellationBean.constellationDpContent
                            constellationPicList0Flow.value = constellationBean.constellationPicList
                            constellationTxtList0Flow.value = constellationBean.constellationTextList
                        }
                        4 ->{
                            constellationShortComment4Flow.value = constellationBean.constellationDpContent
                            constellationPicList4Flow.value = constellationBean.constellationPicList
                            constellationTxtList4Flow.value = constellationBean.constellationTextList
                        }
                        1 ->{
                            constellationShortComment1Flow.value = constellationBean.constellationDpContent
                            constellationPicList1Flow.value = constellationBean.constellationPicList
                            constellationTxtList1Flow.value = constellationBean.constellationTextList
                        }
                        2 ->{
                            constellationShortComment2Flow.value = constellationBean.constellationDpContent
                            constellationPicList2Flow.value = constellationBean.constellationPicList
                            constellationTxtList2Flow.value = constellationBean.constellationTextList
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