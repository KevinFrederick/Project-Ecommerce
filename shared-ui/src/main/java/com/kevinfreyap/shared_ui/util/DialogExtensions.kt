package com.kevinfreyap.shared_ui.util

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun Context.showGenericDialog(
    title: String,
    message: String,
    positiveButtonText: String = "OK",
    negativeButtonText: String? = null,
    isCancelable: Boolean = true,
    onPositiveClick: (() -> Unit)? = null,
    onNegativeClick: (() -> Unit)? = null
) {
    val builder = MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setMessage(message)
        .setCancelable(isCancelable)
        .setPositiveButton(positiveButtonText) { dialog, _ ->
            onPositiveClick?.invoke()
            dialog.dismiss()
        }

    if (negativeButtonText != null) {
        builder.setNegativeButton(negativeButtonText) { dialog, _ ->
            onNegativeClick?.invoke()
            dialog.dismiss()
        }
    }

    builder.show()
}