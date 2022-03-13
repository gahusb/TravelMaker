package com.example.bgg89.travelmaker_project.Common

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.TypefaceSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Created by bgg89 on 2018-11-21.
 */
class FontUtils(context: Context) {
    private var fontUtils: FontUtils? = null
    private var mTypeface: Typeface? = Typeface.createFromAsset(context.assets, "NotoSansCJKkr-DemiLight.otf") // 외부폰트 사용
    private var context: Context = context

    fun getInstance(context: Context): FontUtils? {
        if (fontUtils == null) {
            fontUtils = FontUtils(context)
        }
        if (mTypeface == null) {
            mTypeface = Typeface.createFromAsset(context.assets, "NotoSansCJKkr-DemiLight.otf") // 외부폰트 사용
        }
        return fontUtils
    }

    fun typeface(string: CharSequence): SpannableString {
        val s = SpannableString(string)
        s.setSpan(TypefaceSpan(mTypeface as String), 0, s.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return s
    }

    fun setGlobalFont(view: View?) {
        if (view != null) {
            if (view is ViewGroup) {
                val vg = view as ViewGroup?
                val vgCnt = vg!!.childCount
                for (i in 0 until vgCnt) {
                    val v = vg.getChildAt(i)
                    if (v is TextView) {
                        v.typeface = mTypeface
                        v.includeFontPadding = false
                    }
                    setGlobalFont(v)
                }
            }
        }
    }
}
