package com.example.capstoneproject.CustomButton

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.capstoneproject.R

class ButtonBack:AppCompatButton {
    constructor(context: Context):super (context)
    constructor(context: Context, attrs: AttributeSet):super(context,attrs)
    init {
        background = ContextCompat.getDrawable(context, R.drawable.bg_back_btn)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textSize = 12f
        setTextColor(ContextCompat.getColor(context, R.color.white))
        gravity = Gravity.CENTER
        text = "Back"
    }
}