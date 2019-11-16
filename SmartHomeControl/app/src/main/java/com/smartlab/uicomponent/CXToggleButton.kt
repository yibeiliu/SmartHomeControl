package com.smartlab.uicomponent

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatCheckBox
import com.smartlab.R

class CXToggleButton : AppCompatCheckBox {

    private var mButtonBackground: Drawable? = null

    private var mListener : OnToggleClickListener? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttribute(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttribute(context, attrs)
    }

    private fun initAttribute(context: Context, attrs: AttributeSet) {
        mButtonBackground = context.resources.getDrawable(R.drawable.bg_general_toggle_selector)
        val a = context.obtainStyledAttributes(attrs, R.styleable.CXToggleButton)

        if (a != null) {
            val buttonBackground = a.getDrawable(R.styleable.CXToggleButton_buttonBackground)

            if (buttonBackground != null) {
                mButtonBackground = buttonBackground
            }

            setButtonDrawable(android.R.color.transparent)
            background = mButtonBackground
            a.recycle()
        }
    }

    fun setCxCheckBoxBackground(drawable: Drawable) {
        this.mButtonBackground = drawable
        setButtonDrawable(android.R.color.transparent)
        background = mButtonBackground
    }

    fun setOnToggleClickListener(l : OnToggleClickListener?) {
        this.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                l?.onToggleClick(v)
            }
        })
    }

    fun setToggleButtonState(state : Boolean) {
        isChecked = state
    }

    interface OnToggleClickListener {
        fun onToggleClick(view: View?)
    }
}
