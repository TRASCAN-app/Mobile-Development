package com.example.capstoneproject.CustomButton

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.capstoneproject.R

class ButtonImageX:AppCompatButton {
    constructor(context: Context):super(context)
    constructor(context: Context, attrs: AttributeSet):super(context,attrs)

    init {
        val imageIcon = ContextCompat.getDrawable(context, R.drawable.image_icon)
        setCompoundDrawablesWithIntrinsicBounds(null, imageIcon, null, null)
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)


        background = ContextCompat.getDrawable(context, R.drawable.bg_camera_x)


    }
}