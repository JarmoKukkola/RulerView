package com.github.anastr.rulerview

import android.util.DisplayMetrics
import android.util.TypedValue

/**
 * Created by Anas Altair on 8/29/2018.
 */
enum class RulerUnit(val converter: Float, val unit: String) {
    MM(25.4f, "MM"),
    CM(2.54f, "CM"),
    IN(1f   , "IN");

    companion object {

        fun mmToPx(mm: Float, displayMetrics: DisplayMetrics): Float {
            return mm * TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1f, displayMetrics)
        }

        fun pxToIn(px: Float, displayMetrics: DisplayMetrics): Float {
            return px / TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, 1f, displayMetrics)
        }

    }

    /**
     * @param value in IN
     */
    fun getUnitString(value: Float) = "${(value * converter).format(2)} $unit"

    /**
     * @param value in IN
     *
     * @return value in #unit
     */
    fun convert(value: Float) = value * converter
}

private fun Float.format(value: Int) = java.lang.String.format("%.${value}f", this)
