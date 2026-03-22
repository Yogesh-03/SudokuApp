package com.gamopy.sudoku.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.appcompat.content.res.AppCompatResources
import com.gamopy.sudoku.R
import java.util.Objects

class LogoutDialog(context: Context) : Dialog(context) {

    private val dialogView: View =
        LayoutInflater.from(context).inflate(R.layout.dialog_logout, null)

    private val dialog: AlertDialog = AlertDialog.Builder(context)
        .setView(dialogView)
        .create()

    private val dialogButtonLogout: Button = dialogView.findViewById(R.id.btnLogoutConfirm)
    private val dialogButtonCancel: Button = dialogView.findViewById(R.id.btnCancelLogoutConfirm)


    init {
        dialogButtonLogout.setOnClickListener {
            onLogoutButtonClicked()
            dialog.dismiss()
        }

        dialogButtonCancel.setOnClickListener {
            onCancelButtonClicked()
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

    override fun show() {
        dialog.show()
    }

    var onLogoutButtonClicked: () -> Unit = {}
    var onCancelButtonClicked: () -> Unit = {}
}