package com.github.anastr.rulerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Parcelable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import java.util.*

/**
 * Created by Anas Altair on 8/29/2018.
 * Modified by Dr. Jarmo Kukkola on 10/11/2022.
 */
class OneDimensionRulerView:View {

    companion object {
        const val UpperSection = 1
        const val LowerSection = 2
    }

    private var timePressed:Long = 0
    private val colorPaintMask = Paint(Paint.ANTI_ALIAS_FLAG)
    private val grayPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val grayPaintReplace:Paint
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val textPaintReplace:TextPaint

    private var upperY:Float = 0f
    private var lowerY:Float = 1f

    private val minDistance = dpTOpx(10f)

    var markCmWidth = dpTOpx(20f)
        set(value) {
            field = value
            invalidate()
        }
    var markHalfCmWidth = dpTOpx(15f)
        set(value) {
            field = value
            invalidate()
        }
    var markMmWidth = dpTOpx(10f)
        set(value) {
            field = value
            invalidate()
        }

    private var currentSection = 0
    private var pointerY = 0f

    constructor(context:Context):this(context,null)

    constructor(context:Context,attrs:AttributeSet?):this(context,attrs,0)

    constructor(context:Context,attrs:AttributeSet?,defStyleAttr:Int):super(context,attrs,defStyleAttr)

    init {
        setBackgroundColor(Color.DKGRAY)
        colorPaintMask.color = Color.BLACK

        grayPaint.color = Color.DKGRAY
        grayPaintReplace = Paint(grayPaint)
        grayPaintReplace.color = Color.WHITE

        textPaint.textSize = dpTOpx(25f)
        textPaint.textAlign = Paint.Align.CENTER
        textPaintReplace = TextPaint(textPaint)
        textPaintReplace.color = Color.WHITE
    }

    override fun onSizeChanged(w:Int,h:Int,oldw:Int,oldh:Int) {
        super.onSizeChanged(w,h,oldw,oldh)
        upperY = h*.3f
        lowerY = h*.7f
        invalidate()
    }

    override fun onDraw(canvas:Canvas?) {
        super.onDraw(canvas)

        drawMarks(canvas,grayPaint)

        canvas?.drawRect(0f,0f,width.toFloat(),upperY,colorPaintMask)
        canvas?.drawRect(0f,lowerY,width.toFloat(),height.toFloat(),colorPaintMask)

        drawMarks(canvas,grayPaintReplace)

        canvas?.drawText(
            RulerUnit.CM.getUnitString(RulerUnit.pxToIn(Math.abs(upperY-lowerY),resources.displayMetrics)),width*.5f,2*textPaint.textSize+5,
            textPaintReplace
        )
        canvas?.drawText(
            RulerUnit.IN.getUnitString(RulerUnit.pxToIn(Math.abs(upperY-lowerY),resources.displayMetrics)),
            width*.5f,
            4*textPaint.textSize+5,
            textPaintReplace
        )
    }

    private fun drawMarks(canvas:Canvas?,paint:Paint) {
        val oneMmInPx = RulerUnit.mmToPx(1f,resources.displayMetrics)
        for(i in 1..1000) {
            val y = oneMmInPx*i
            val markWidth = when {
                i%10==0->markCmWidth
                i%5==0->markHalfCmWidth
                else->markMmWidth
            }
            canvas?.drawLine(0f,y,markWidth,y,paint)
            canvas?.drawLine(width.toFloat(),y*2.54f,width-markWidth,y*2.54f,paint)
            if(y>=height) break
        }
    }

    override fun onTouchEvent(event:MotionEvent?):Boolean {
        when(event?.actionMasked) {
            MotionEvent.ACTION_DOWN-> {
                timePressed = GregorianCalendar().timeInMillis
                val centerPoint = (lowerY+upperY)/2
                currentSection = when {
                    event.y<centerPoint->UpperSection
                    event.y>centerPoint->LowerSection
                    else->0
                }
                pointerY = event.y
                return currentSection!=0
            }
            MotionEvent.ACTION_MOVE-> {
                val dy = event.y-pointerY
                when(currentSection) {
                    UpperSection-> {
                        upperY += dy
                        upperY = Math.max(0f,Math.min(lowerY-minDistance,upperY))
                    }
                    LowerSection-> {
                        lowerY += dy
                        lowerY = Math.max(upperY+minDistance,Math.min(height.toFloat(),lowerY))
                    }
                }
                pointerY = event.y
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP,MotionEvent.ACTION_CANCEL-> {
                val timeDifference = GregorianCalendar().timeInMillis-timePressed
                if(timeDifference<100) (context as AppCompatActivity).onBackPressed()
                return false
            }
        }
        return false
    }

    override fun onSaveInstanceState():Parcelable? {
        super.onSaveInstanceState()
        val bundle = Bundle()
        bundle.putParcelable("superState",super.onSaveInstanceState())
        return bundle
    }

    override fun onRestoreInstanceState(state:Parcelable?) {
        var _state = state
        val bundle = _state as Bundle
        _state = bundle.getParcelable("superState")
        super.onRestoreInstanceState(_state)
    }

    /**
     * convert dp to **pixel**.
     * @param dp to convert.
     * @return Dimension in pixel.
     */
    private fun dpTOpx(dp:Float):Float {
        return dp*context.resources.displayMetrics.density
    }
}