package com.example.bgg89.travelmaker_project.Common

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.ViewGroup
import android.widget.ProgressBar

import com.example.bgg89.travelmaker_project.R

/**
 * Created by bgg89 on 2018-11-21.
 */
class CustomProgressDialog(context: Context) : Dialog(context, R.style.NewDialog) {
    companion object {

        @JvmOverloads
        fun show(context: Context, title: CharSequence,
                 message: CharSequence, indeterminate: Boolean = false,
                 cancelable: Boolean = false, cancelListener: DialogInterface.OnCancelListener? = null): CustomProgressDialog {
            val dialog = CustomProgressDialog(context)
            dialog.setTitle(title)
            dialog.setCancelable(cancelable)
            dialog.setOnCancelListener(cancelListener)
            /* The next line will add the ProgressBar to the dialog. */
            val pb = ProgressBar(context)
            dialog.addContentView(pb, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            dialog.show()
            return dialog
        }
    }
}