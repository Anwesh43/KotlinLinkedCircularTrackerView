package com.anwesh.uiprojects.kotlinlinkedcirculartrackerview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.linkedcirculartrackerview.LinkedCircularTrackerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LinkedCircularTrackerView.create(this)
    }
}
