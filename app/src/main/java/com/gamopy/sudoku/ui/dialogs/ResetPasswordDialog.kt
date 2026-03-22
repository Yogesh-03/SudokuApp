package com.gamopy.sudoku.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.gamopy.sudoku.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.util.Objects

class ResetPasswordDialog(context: Context) : Dialog(context) {

    private val dialogView: View =
        LayoutInflater.from(context).inflate(R.layout.dialog_forgot_login_pass, null)

    private val dialog: AlertDialog = AlertDialog.Builder(context)
        .setView(dialogView)
        .create()

    private val dialogTitle = dialogView.findViewById<TextView>(R.id.tvForgotPass)
    val dialogButton: MaterialButton = dialogView.findViewById(R.id.btnRestPassword)
    val progressBar: ProgressBar = dialogView.findViewById(R.id.pbResetPass)
    private val dialogInput = dialogView.findViewById<TextInputEditText>(R.id.etLoginEmailText)


    init {
        dialogButton.setOnClickListener {
            val inputText = dialogInput.text.toString()
            // Do something with the input text
            onDialogButtonClicked(inputText)
            dialog.dismiss()
        }

        Objects.requireNonNull<Window>(this.window).setBackgroundDrawable(
            AppCompatResources.getDrawable(
                context,
                R.drawable.dialog_background_inset
            )
        )
        this.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

    }


    fun setTitle(title: String) {
        dialogTitle.text = title
    }

    override fun show() {
        dialog.show()
    }

    var onDialogButtonClicked: (inputText: String) -> Unit = {}
}