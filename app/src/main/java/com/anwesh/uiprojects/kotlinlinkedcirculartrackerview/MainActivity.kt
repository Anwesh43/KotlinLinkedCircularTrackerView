package com.anwesh.uiprojects.kotlinlinkedcirculartrackerview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.anwesh.uiprojects.linkedcirculartrackerview.LinkedCircularTrackerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view : LinkedCircularTrackerView = LinkedCircularTrackerView.create(this)
        fullScreen()
        view.addOnStepCompletionListener {
            Toast.makeText(this, "${it} is completed" ,Toast.LENGTH_SHORT).show()
        }
    }
}

fun MainActivity.fullScreen() {
    supportActionBar?.hide()
    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}