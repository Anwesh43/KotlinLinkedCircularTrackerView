package com.anwesh.uiprojects.linkedcirculartrackerview
/**
 * Created by anweshmishra on 24/06/18.
 */

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.view.View
import android.view.MotionEvent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

val CT_NODES : Int = 5

class LinkedCircularTrackerView (ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : Renderer = Renderer(this)

    var onStepCompletionListener : StepCompletionListener? = null

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    fun addOnStepCompletionListener(onComplete : (Int) -> Unit) {
        onStepCompletionListener = StepCompletionListener(onComplete)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
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
                j += dir.toInt()
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
            prev?.draw(canvas, paint)
            if ((state.scales[0] != 0f || state.scales[1] != 0f) || i == 0) {
                paint.strokeWidth = Math.min(w, h) / 50
                paint.strokeCap = Paint.Cap.ROUND
                paint.color = Color.parseColor("#3949AB")
                canvas.save()
                canvas.translate(w / 2, h / 2)
                canvas.save()
                canvas.rotate(gap * (i))
                paint.style = Paint.Style.STROKE
                canvas.drawArc(RectF(-r, -r, r, r), 0f, gap * state.scales[0] * 0.85f, false, paint)
                canvas.save()
                canvas.rotate(gap * 0.15f + gap * state.scales[0] * 0.85f)
                paint.style = Paint.Style.STROKE
                canvas.drawCircle(r, 0f, ballR, paint)
                paint.style = Paint.Style.FILL
                canvas.drawCircle(r, 0f, ballR * state.scales[1], paint)
                canvas.restore()
                canvas.restore()
                canvas.restore()
                paint.color = Color.parseColor("#00C853")
            }
            val x_gap: Float = (0.9f * w) / CT_NODES
            canvas.drawLine(w/20 + i * x_gap, 0.9f * h, w/20 + i * x_gap + (x_gap / 2) * (state.scales[0] + state.scales[1]), 0.9f * h, paint)
        }

        fun update(stopcb : (Float, Int) -> Unit) {
            state.update {
                stopcb(it, i)
            }
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

        fun update(stopcb : (Float, Int) -> Unit) {
            curr.update {scale, j ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                stopcb(scale, j)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            curr.startUpdating(startcb)
        }
    }

    data class Renderer(var view : LinkedCircularTrackerView) {

        private val linkedCT : LinkedCT = LinkedCT(0)

        private val animator : CTAnimator = CTAnimator(view)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#212121"))
            linkedCT.draw(canvas, paint)
            animator.animate {
                linkedCT.update { scale, j ->
                    animator.stop()
                    when(scale) {
                        1f -> view.onStepCompletionListener?.onComplete?.invoke(j)
                    }
                }
            }
        }

        fun handleTap() {
            linkedCT.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : LinkedCircularTrackerView {
            val view : LinkedCircularTrackerView = LinkedCircularTrackerView(activity)
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            activity.setContentView(view)
            return view
        }
    }

    data class StepCompletionListener(var onComplete: (Int) -> Unit)
}