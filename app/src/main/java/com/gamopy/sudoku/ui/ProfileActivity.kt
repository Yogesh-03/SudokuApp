package com.gamopy.sudoku.ui

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.gamopy.sudoku.R
import com.gamopy.sudoku.data.Resource
import com.gamopy.sudoku.databinding.ActivityProfileBinding
import com.gamopy.sudoku.ui.dialogs.LogoutDialog
import com.gamopy.sudoku.ui.dialogs.ResetPasswordDialog
import com.gamopy.sudoku.viewmodel.AuthViewModel
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val viewModel by viewModels<AuthViewModel>()
    private lateinit var resetDialog: ResetPasswordDialog
    private lateinit var logoutDialog: LogoutDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.materialToolbarProfile.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        if (viewModel.currentUser == null){
            binding.tvLoggedOut.visibility  = View.VISIBLE
            binding.llProfile.visibility = View.GONE
        }

        viewModel.passResetResult.observe(this, Observer {
            when (it) {
                Resource.Empty -> {
                    Toast.makeText(this, "Reset link sent on email", Toast.LENGTH_SHORT).show()
                    toggleBtnProgressBarVisibility(
                        true,
                        resetDialog.dialogButton,
                        resetDialog.progressBar
                    )
                    resetDialog.dismiss()
                }

                is Resource.Failure -> {
                    toggleBtnProgressBarVisibility(
                        false,
                        resetDialog.dialogButton,
                        resetDialog.progressBar
                    )
                }

                Resource.Loading -> {

                }

                is Resource.Success -> {

                }

                null -> {

                }
            }
        })

        binding.llResetPassword.setOnClickListener {
            resetDialog = ResetPasswordDialog(this)
            resetDialog.setTitle("Reset Password")


            resetDialog.onDialogButtonClicked = { inputText ->
                Toast.makeText(this, "Input: $inputText", Toast.LENGTH_SHORT).show()

                if (inputText.isEmpty()) {
                    Toast.makeText(this, "Pleas enter email", Toast.LENGTH_SHORT).show()
                } else {
                    handleResetPassword(
                        inputText,
                        resetDialog.dialogButton,
                        resetDialog.progressBar
                    )
                }

            }
            resetDialog.show()
        }

        binding.llLogout.setOnClickListener {
            logoutDialog = LogoutDialog(this)

            logoutDialog.onLogoutButtonClicked = {
                viewModel.logout()
                Toast.makeText(this, "Successfully Logged out", Toast.LENGTH_SHORT).show()
                onBackPressedDispatcher.onBackPressed()
            }

            logoutDialog.onCancelButtonClicked = {
                logoutDialog.dismiss()
            }
            logoutDialog.show()
        }
    }

    private fun handleResetPassword(email: String, btn: MaterialButton, pb: ProgressBar) {
        toggleBtnProgressBarVisibility(false, btn, pb)
        viewModel.resetPassword(email)
    }

    private fun toggleBtnProgressBarVisibility(
        visible: Boolean,
        btn: MaterialButton,
        pb: ProgressBar
    ) {
        //With ref to visibility of button
        if (visible) {
            btn.visibility = View.VISIBLE
            btn.isClickable = true
            pb.visibility = View.GONE
        } else {
            // btn invisible
            btn.visibility = View.INVISIBLE
            btn.isClickable = false
            pb.visibility = View.VISIBLE
        }
    }
}