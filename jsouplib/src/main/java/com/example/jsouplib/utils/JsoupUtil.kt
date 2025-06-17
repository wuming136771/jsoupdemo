import android.util.Log
import com.example.jsouplib.utils.StringUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * @author cmz$
 * @date 2025/6/11$
 * @description
 */
object JsoupUtil {
    private val scope = MainScope()
    val dataVm = DataViewModel.instance


    /**
     * path
     */
    private const val URL_PUBLIC_PART_MONTH = "&iMonth="
    private const val URL_PUBLIC_PART_DAY = "&iDay="
    private const val URL_PUBLIC_PART_HOUR = "&iHour="
    private const val URL_PUBLIC_PART_SEX = "&iSex="
    private const val URL_PUBLIC_PART_NAME = "&NickData="

    private const val URL_TODAY_PART = "http://www.ibazi.cn/gratis/ziweiriyunshi/result.php?iYear="


    private const val URL_COMPASS_PART_1 = "http://www.ibazi.cn/gratis/rshndzwmp/"
    private const val URL_COMPASS_PART_2 = "result01"
    private const val URL_COMPASS_PART_3 = ".php?iYear="


    /**
     * 获取今日运势数据
     * @param dateOfBirth 出生日期, 格式为 yyyy-MM-dd
     * @param timeOfBirth 出生时辰 ,0-23
     * @param sex 性别, 1-男, 2-女
     * @param name 姓名
     * @param onSuccess 成功高阶函数代码块
     * @param onError 失败高阶函数代码块
     */
    fun getTodayFortuneData(
        dateOfBirth: String, timeOfBirth: String,
        sex: String, name: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        var url = ""
        val dateSplit = dateOfBirth.split("-")
        url = "${URL_TODAY_PART}${dateSplit[0]}" +
                "${URL_PUBLIC_PART_MONTH}${dateSplit[1]}" +
                "${URL_PUBLIC_PART_DAY}${dateSplit[2]}" +
                "${URL_PUBLIC_PART_HOUR}${timeOfBirth}" +
                "${URL_PUBLIC_PART_SEX}${sex}" +
                "${URL_PUBLIC_PART_NAME}${name}"
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
                    dataVm.setTodayData1(content)
                    dataVm.setTodayData2(content1)
                    dataVm.setTodayData3(content2)
                    dataVm.setTodayData4(content3)
                }
                onSuccess()
            } catch (e: Exception) {
                onError()
            }
        }

    }

    /**
     * 获取紫薇斗盘数据
     */
    fun getCompassData(
        dateOfBirth: String, timeOfBirth: String,
        sex: String, name: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        var url = ""
        val dateSplit = dateOfBirth.split("-")
        url = URL_COMPASS_PART_1 +
                URL_COMPASS_PART_2 +
                "${URL_COMPASS_PART_3}${dateSplit[0]}" +
                "${URL_PUBLIC_PART_MONTH}${dateSplit[1]}" +
                "${URL_PUBLIC_PART_DAY}${dateSplit[2]}" +
                "${URL_PUBLIC_PART_HOUR}${timeOfBirth}" +
                "${URL_PUBLIC_PART_SEX}${sex}" +
                "${URL_PUBLIC_PART_NAME}${name}"
        scope.launch {
            try {
                withContext(Dispatchers.IO){
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
                    dataVm.setCompassData1(data1List)
                    //十二块表盘中间数据
                    //未点击时显示的数据
                    val select2 = doc.select("p.ZIWEI_C_t05")
                    for (element in select2) {
                        data2List.add(element.text())
                    }
                    dataVm.setCompassData2(data2List)
                    //显示时候的数据
                    val body = doc.toString()
                    val ss: Array<String> = StringUtils.substringsBetween( body,
                        "='",
                        "';")

                    //解析script
                    ss.forEach {
                        Log.d("lxq", "ss:${it} ")
                    }
//                    dataVm.centerData = ss
                }
            }catch (_:Exception){
                onError()
            }
        }
    }


    fun destroy() {
        scope.cancel()
    }

}