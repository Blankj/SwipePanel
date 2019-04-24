package com.blankj.swipepanel.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.blankj.utilcode.util.Utils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Utils.init(this)

        testLayoutSwipePanel.setOnClickListener {
            LayoutSwipePanelActivity.start(this)
        }

        testBack.setOnClickListener {
            BackActivity.start(this)
        }
    }
}
