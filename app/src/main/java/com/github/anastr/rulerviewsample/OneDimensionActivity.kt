package com.github.anastr.rulerviewsample

import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.github.anastr.rulerview.RulerUnit
import kotlinx.android.synthetic.main.activity_one_dimension.*

class OneDimensionActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_dimension)
    }

    override fun onResume() {
        super.onResume()
        @Suppress("DEPRECATION")
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.R) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        } else {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }
    }
}
