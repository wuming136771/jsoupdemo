package com.example.jsouplibrary.vm

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

    private val content1Flow = MutableStateFlow<List<String>?>(null)
    private val content2Flow = MutableStateFlow<Array<String>?>(null)
    private val content3Flow = MutableStateFlow<Array<String>?>(null)
    private val content4Flow = MutableStateFlow<String?>(null)


    /**
     * 今日运势
     */
    fun setData1(content1:List<String>?){
        content1Flow.value = content1
    }

    fun setData2(content2:Array<String>?){
        content2Flow.value = content2
    }

    fun setData3(content3:Array<String>?){
        content3Flow.value = content3
    }

    fun setData4(content4:String?){
        content4Flow.value = content4
    }
}