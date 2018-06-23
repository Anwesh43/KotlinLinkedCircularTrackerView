package com.anwesh.uiprojects.linkedcirculartrackerview
/**
 * Created by anweshmishra on 24/06/18.
 */

import android.content.Context
import android.view.View
import android.view.MotionEvent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

val CT_NODES : Int = 5

class LinkedCircularTrackerView (ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class CTState (var prevScale : Float = 0f, var j : Int = 0, var dir : Float = 0f) {

        val scales : Array<Float> = arrayOf(0f, 0f)

        fun update(stopcb : (Float) -> Unit) {
            scales[j] += 0.1f * dir
            if (Math.abs(scales[j] - prevScale) > 1) {
                scales[j] = prevScale + dir
                j -= dir.toInt()
                if (j == scales.size || j == -1) {
                    j -= dir.toInt()
                    dir = 0f
                    prevScale = scales[j]
                    stopcb(prevScale)
                }
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                startcb()
            }
        }
    }

    data class CTAnimator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class CTNode(var i : Int, val state : CTState = CTState()) {

        private var next : CTNode? = null

        private var prev : CTNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < CT_NODES - 1) {
                next = CTNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            val w : Float = canvas.width.toFloat()
            val h : Float = canvas.height.toFloat()
            val r : Float = Math.min(w, h) / 3
            val ballR : Float = r / 6
            val gap : Float = 360f / CT_NODES
            paint.strokeWidth = Math.min(w, h) / 50
            paint.strokeCap = Paint.Cap.ROUND
            paint.color = Color.parseColor("#d35400")
            prev?.draw(canvas, paint)
            canvas.save()
            canvas.translate(w/2, h/2)
            paint.style = Paint.Style.STROKE
            canvas.drawArc(RectF(-r, -r, r, r), i * gap, gap * state.scales[0], false, paint)
            canvas.save()
            canvas.rotate(gap * (i + 1))
            paint.style = Paint.Style.STROKE
            canvas.drawCircle(r, 0f, ballR, paint)
            paint.style = Paint.Style.FILL
            canvas.drawCircle(r, 0f, ballR * state.scales[1], paint)
            canvas.restore()
            canvas.restore()
        }

        fun update(stopcb : (Float) -> Unit) {
            state.update(stopcb)
        }

        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : CTNode {
            var curr : CTNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class LinkedCT(var i : Int) {

        private var curr : CTNode = CTNode(0)

        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(stopcb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            curr.startUpdating(startcb)
        }
    }
}