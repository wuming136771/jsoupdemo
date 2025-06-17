
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * @author cmz$
 * @date 2025/6/12$
 * @description
 */
class DataViewModel private constructor() : ViewModel() {
    companion object {
        val instance by lazy { DataViewModel() }
    }

    private val todayContent1Flow = MutableStateFlow<List<String>?>(null)
    private val todayContent2Flow = MutableStateFlow<Array<String>?>(null)
    private val todayContent3Flow = MutableStateFlow<Array<String>?>(null)
    private val todayContent4Flow = MutableStateFlow<String?>(null)


    val dataFlow1 = MutableStateFlow<List<String>?>(null)
    val dataFlow2 = MutableStateFlow<MutableList<String>?>(null)
    var strListFlow = MutableStateFlow<List<String>?>(null)
    var spanFlow = MutableStateFlow<String?>(null)





    /**
     * 今日运势
     */
    fun setTodayData1(content1:List<String>?){
        todayContent1Flow.value = content1
    }

    fun setTodayData2(content2:Array<String>?){
        todayContent2Flow.value = content2
    }

    fun setTodayData3(content3:Array<String>?){
        todayContent3Flow.value = content3
    }

    fun setTodayData4(content4:String?){
        todayContent4Flow.value = content4
    }

    /**
     * 紫薇斗盘
     */
    fun setCompassData1(list : MutableList<String>?){
        dataFlow1.value = list
    }

    fun setCompassData2(list : MutableList<String>?){
        dataFlow2.value = list
    }

    fun setCompassStrList(list : List<String>?){
        strListFlow.value = list
    }

    fun setCompassSpan(str : String){
        spanFlow.value = str
    }
}