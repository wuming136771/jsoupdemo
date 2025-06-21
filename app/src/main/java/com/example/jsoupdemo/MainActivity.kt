package com.example.jsoupdemo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.jsoupdemo.databinding.ActivityMainBinding
import com.example.jsouplib.vm.ChineseZodiacViewModel
import com.example.jsouplib.vm.CompassViewModel
import com.example.jsouplib.vm.ConstellationViewModel
import com.example.jsouplib.vm.TodayFortuneViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * @author cmz$
 * @date 2025/6/13$
 * @description
 */
private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val todayFortuneViewModel by lazy { TodayFortuneViewModel() }
    private val compassViewModel by lazy { CompassViewModel() }
    private val constellationViewModel by lazy { ConstellationViewModel() }
    private val chineseZodiacViewModel by lazy { ChineseZodiacViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFlow()
        initListen()
    }

    private fun initFlow() {
        lifecycleScope.launch {
            launch {
                chineseZodiacViewModel.currentPositionFlow.collectLatest {
                    when (it) {
                        0 -> {
                            when(chineseZodiacViewModel.isFirst0Flow.value){
                                true -> {
                                    chineseZodiacViewModel.getZodiacSignData(
                                        "2021-01-01",
                                        0,
                                        onSuccess = {
                                            launch {
                                                chineseZodiacViewModel.chineseZodiac0Flow.collectLatest {
                                                    Log.d(TAG, "isFirst 0 it = $it ")
                                                }
                                            }

                                        }
                                    )
                                }
                                false ->{
                                    chineseZodiacViewModel.chineseZodiac0Flow.collectLatest {
                                        Log.d(TAG, "no First 0 it = $it ")
                                    }
                                }
                            }



                        }
                        1 -> {
                            when(chineseZodiacViewModel.isFirst1Flow.value) {
                                true ->{
                                    chineseZodiacViewModel.getZodiacSignData(
                                        "2021-01-01",
                                        1,
                                        onSuccess = {
                                            launch {
                                                chineseZodiacViewModel.chineseZodiac1Flow.collectLatest {
                                                    Log.d(TAG, "isFirst 1 it = $it ")
                                                }
                                            }

                                        }
                                    )
                                }

                                false ->{
                                    chineseZodiacViewModel.chineseZodiac1Flow.collectLatest {
                                        Log.d(TAG, "no First 1 it = $it ")
                                    }
                                }
                             }

                        }
                    }
                }
            }

        }
    }

    private fun initListen() {
        binding.btn1.setOnClickListener {
            todayFortuneViewModel.getTodayFortuneData(
                "2025-06-13",
                0,
                1,
                "张三",
                onSuccess = {
                    Log.d(TAG, "get data success")
                },
                onError = {
                    Log.d(TAG, "get data fail")
                }
            )
        }


        binding.btn2.setOnClickListener {
            compassViewModel.getCompassData(
                "2025-06-13",
                0,
                1,
                "张三",
                onSuccess = {
                    Log.d(TAG, "get data success")
                },
                onError = {
                    Log.d(TAG, "get data fail")
                }
            )
        }


        binding.btn3.setOnClickListener {
            constellationViewModel.getConstellationData(
                "2025-06-13",
                0,
                onSuccess = {
                    Log.d(TAG, "get data success")
                },
                onError = {
                    Log.d(TAG, "get data fail")
                }
            )
        }

        binding.btnLeft.setOnClickListener {
            chineseZodiacViewModel.currentPositionFlow.value = 0
        }

        binding.btnRight.setOnClickListener {
            chineseZodiacViewModel.currentPositionFlow.value = 1
        }
    }
}