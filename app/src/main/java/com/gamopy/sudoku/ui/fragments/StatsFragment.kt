package com.gamopy.sudoku.ui.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Observer
import com.gamopy.sudoku.R
import com.gamopy.sudoku.data.Resource
import com.gamopy.sudoku.databinding.FragmentStatsBinding
import com.gamopy.sudoku.viewmodel.AuthViewModel
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import java.util.Objects
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.gamopy.sudoku.sharedpreferences.UserSettings
import com.gamopy.sudoku.ui.ProfileActivity
import com.gamopy.sudoku.ui.SettingsActivity
import com.gamopy.sudoku.viewmodel.SudokuDataViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.textfield.TextInputEditText


@AndroidEntryPoint
class StatsFragment : Fragment() {
    private lateinit var binding: FragmentStatsBinding
    private val viewModel by viewModels<AuthViewModel>()
    private val dataViewModel by viewModels<SudokuDataViewModel>()
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentStatsBinding.inflate(layoutInflater, container, false)

        sharedPreferences = requireActivity().getSharedPreferences(
            UserSettings().PREFERENCES,
            AppCompatActivity.MODE_PRIVATE
        )

        //Login Dialog
        val loginDialog = Dialog(requireActivity())
        loginDialog.setContentView(R.layout.dialog_email_pass_login)

        Objects.requireNonNull<Window>(loginDialog.window).setBackgroundDrawable(
            AppCompatResources.getDrawable(
                requireActivity(),
                R.drawable.dialog_background_inset
            )
        )
        loginDialog.window
            ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val btnLogin = loginDialog.findViewById<MaterialButton>(R.id.button2)
        val loginEmail = loginDialog.findViewById<TextInputEditText>(R.id.etLoginEmailText)
        val loginPass = loginDialog.findViewById<TextInputEditText>(R.id.etLoginPasswordText)
        val loginPb = loginDialog.findViewById<ProgressBar>(R.id.pbLogin)
        val forgotPassword = loginDialog.findViewById<TextView>(R.id.tvForgotPasswordLogin)

        //Sign Up Dialog

        val signupDialog = Dialog(requireActivity())
        signupDialog.setContentView(R.layout.dialog_email_pass_signup)

        Objects.requireNonNull<Window>(signupDialog.window).setBackgroundDrawable(
            AppCompatResources.getDrawable(
                requireActivity(),
                R.drawable.dialog_background_inset
            )
        )
        signupDialog.window
            ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val btnSignup = signupDialog.findViewById<MaterialButton>(R.id.btnSignup)
        val signupName = signupDialog.findViewById<TextInputEditText>(R.id.etSignupNameText)
        val signupEmail = signupDialog.findViewById<TextInputEditText>(R.id.etSignupEmailText)
        val signupPass = signupDialog.findViewById<TextInputEditText>(R.id.etSignupPasswordText)
        val signupPb = signupDialog.findViewById<ProgressBar>(R.id.pbSignup)

        //Forgot Pass Dialog

        val forgotPassDialog = Dialog(requireActivity())
        forgotPassDialog.setContentView(R.layout.dialog_forgot_login_pass)

        Objects.requireNonNull<Window>(forgotPassDialog.window).setBackgroundDrawable(
            AppCompatResources.getDrawable(
                requireActivity(),
                R.drawable.dialog_background_inset
            )
        )
        forgotPassDialog.window
            ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val btnResetPass = forgotPassDialog.findViewById<MaterialButton>(R.id.btnRestPassword)
        val loginEmailReset =
            forgotPassDialog.findViewById<TextInputEditText>(R.id.etLoginEmailText)
        val resetPassPb = forgotPassDialog.findViewById<ProgressBar>(R.id.pbResetPass)


        viewModel.loginResult.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
//                    Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                    toggleBtnProgressBarVisibility(true, btnLogin, loginPb)
                    binding.tvUserName.text = it.result.displayName
                    loginDialog.dismiss()
                    loginDialog.setCancelable(true)
                    viewModel.fetchProfileData()
                    binding.ivEditProfileImage.visibility = View.VISIBLE
                    toggleRegisterUI(false)
                    binding.progressBar.visibility = View.GONE
                }

                is Resource.Failure -> {
                    toggleBtnProgressBarVisibility(true, btnLogin, loginPb)
                    Toast.makeText(context, it.exception.message, Toast.LENGTH_SHORT).show()
                    loginDialog.setCancelable(true)

                }

                Resource.Loading -> {
                    loginDialog.setCancelable(false)
                    binding.progressBar.visibility = View.VISIBLE
                }

                null -> {

                }

                Resource.Empty -> {

                }
            }
        }

        viewModel.signupResult.observe(requireActivity()) {
            when (it) {
                is Resource.Failure -> {
                    toggleBtnProgressBarVisibility(true, btnSignup, signupPb)
                }

                Resource.Loading -> {

                }

                is Resource.Success -> {
                    Toast.makeText(requireActivity(), "Sign up Successful", Toast.LENGTH_SHORT)
                        .show()
                    toggleBtnProgressBarVisibility(true, btnSignup, signupPb)
                    binding.tvUserName.text = it.result.displayName
                    signupDialog.dismiss()
                }

                null -> {

                }

                Resource.Empty -> {

                }
            }
        }

        viewModel.passResetResult.observe(requireActivity()) {
            when (it) {
                Resource.Empty -> {
                    Toast.makeText(
                        requireActivity(),
                        "Reset link sent on email",
                        Toast.LENGTH_SHORT
                    ).show()
                    toggleBtnProgressBarVisibility(true, btnResetPass, resetPassPb)
                    forgotPassDialog.dismiss()

                }

                is Resource.Failure -> {
                    toggleBtnProgressBarVisibility(false, btnResetPass, resetPassPb)

                }

                Resource.Loading -> {

                }

                is Resource.Success -> {

                }

                null -> {

                }
            }
        }

        viewModel.fetchProfileResult.observe(requireActivity(), Observer {
            when (it) {
                Resource.Empty -> {

                }

                is Resource.Failure -> {

                }

                Resource.Loading -> {

                }

                is Resource.Success -> {
                    binding.tvUserEmail.apply {
                        text = it.result.email
                        visibility = View.VISIBLE
                    }
                    if (it.result.profileImage != null) {
                        Glide.with(requireActivity())
                            .load(it.result.profileImage)
                            .into(binding.ivUserImage)
                        binding.pbUserImageLoading.visibility = View.GONE
                        binding.ivUserImage.visibility = View.VISIBLE
                    }
                }

                null -> {

                }

            }
        })

        viewModel.updateProfileImage.observe(requireActivity(), Observer {
            when (it) {
                Resource.Empty -> {

                }

                is Resource.Failure -> {

                }

                Resource.Loading -> {

                }

                is Resource.Success -> {
                    Glide.with(requireActivity())
                        .load(it.result)
                        .into(binding.ivUserImage)
                    binding.pbUserImageLoading.visibility = View.GONE
                    binding.ivUserImage.visibility = View.VISIBLE
                }

                null -> {

                }

            }
        })



        binding.btnLogin.setOnClickListener {
            loginDialog.show()
        }

//        binding.tvIconRef.setOnClickListener{
//            val url = "https://icons8.com/icon/48282/brain"
//            openUrl(url)
//        }

//        binding.ivIconLink.setOnClickListener {
//            val url = "https://icons8.com"
//            openUrl(url)
//        }

        btnLogin.setOnClickListener {
            val email = loginEmail.text.toString().trim()
            val password = loginPass.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireActivity(), "Fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                handleLogIn(
                    email,
                    password,
                    btnLogin,
                    loginPb
                )
            }
        }

        binding.tvSignUp.setOnClickListener {
            signupDialog.show()
        }

        btnSignup.setOnClickListener {
            val name = signupName.text.toString()
            val email = signupEmail.text.toString().trim()
            val password = signupPass.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireActivity(), "Fill all fields", Toast.LENGTH_SHORT).show()
            } else if (password.length < 8) {
                Toast.makeText(
                    requireActivity(),
                    "Password should be at least 8 characters",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                handleSignUp(
                    signupName.text.toString(),
                    email,
                    password,
                    btnSignup,
                    signupPb
                )
            }
        }

        forgotPassword.setOnClickListener {
            loginDialog.dismiss()
            forgotPassDialog.show()
        }

        btnResetPass.setOnClickListener {
            val email = loginEmailReset.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(requireActivity(), "Pleas enter email", Toast.LENGTH_SHORT).show()
            } else {
                handleResetPassword(email, btnResetPass, resetPassPb)
            }
        }

        binding.ivEditProfileImage.setOnClickListener {
            ImagePicker.with(this)
                .compress(512)         //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080,
                    1080
                )  //Final image resolution will be less than 1080 x 1080(Optional)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                    binding.pbUserImageLoading.visibility = View.VISIBLE
                    binding.ivUserImage.visibility = View.INVISIBLE
                }
        }

        binding.statsMaterialToolbar.setOnMenuItemClickListener {

            when (it.itemId) {
                R.id.profileSettingItem -> {
                    val intent = Intent(requireActivity(), SettingsActivity().javaClass)
                    startActivity(intent)
                    true
                }

                R.id.profileIcon -> {
                    val intent = Intent(requireActivity(), ProfileActivity().javaClass)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }

        return binding.root
    }

    /**
     * Result for selected image for profile picture
     */
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!
                //mProfileUri = fileUri
                //binding.ivUserImage.setImageURI(fileUri)
                viewModel.updateProfileImage(fileUri)
                //imgProfile.setImageURI(fileUri)
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(requireActivity(), ImagePicker.getError(data), Toast.LENGTH_SHORT)
                    .show()
                binding.pbUserImageLoading.visibility = View.GONE
                binding.ivUserImage.visibility = View.VISIBLE
            } else {
                Toast.makeText(requireActivity(), "Task Cancelled", Toast.LENGTH_SHORT).show()
                binding.pbUserImageLoading.visibility = View.GONE
                binding.ivUserImage.visibility = View.VISIBLE
            }
        }

    private fun handleResetPassword(email: String, btn: MaterialButton, pb: ProgressBar) {
        toggleBtnProgressBarVisibility(false, btn, pb)
        viewModel.resetPassword(email)
    }

    private fun handleSignUp(
        name: String,
        email: String,
        password: String,
        btn: MaterialButton,
        pb: ProgressBar
    ) {
        toggleBtnProgressBarVisibility(false, btn, pb)
        viewModel.signUp(name, email.trim(), password)
    }

    /**
     * @param email
     * @param password
     * @param btn
     * @param pb
     */
    private fun handleLogIn(
        email: String,
        password: String,
        btn: MaterialButton,
        pb: ProgressBar
    ) {
        toggleBtnProgressBarVisibility(false, btn, pb)
        viewModel.login(email, password)
    }

    private fun openUrl(url:String){
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    /**
     * Toggle between visibility of button and progress bar
     * @param visible
     * true for button visible and progress bar visibility gone and vice versa
     * @param btn
     * @param pb
     */
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
        } else {  // btn invisible
            btn.visibility = View.INVISIBLE
            btn.isClickable = false
            pb.visibility = View.VISIBLE
        }

    }

    /**
     * Toggles the visibility between Login Button, Sign Up text, Dont have account text and tab layout
     * @param visible
     * true -> login btn, signup text and don't have account are visible, tab layout invisible.
     */
    private fun toggleRegisterUI(visible: Boolean) {
        if (visible) {
            binding.btnLogin.visibility = View.VISIBLE
            binding.tvSignUp.visibility = View.VISIBLE
            binding.tvDontHaveAccount.visibility = View.VISIBLE
            binding.tvUserName.text = "Login to Sync"
            binding.tvUserEmail.visibility = View.INVISIBLE
            binding.ivEditProfileImage.visibility = View.GONE
            binding.ivUserImage.setImageDrawable(resources.getDrawable(R.drawable.baseline_person_3_24))
        } else {
            binding.btnLogin.visibility = View.GONE
            binding.tvSignUp.visibility = View.GONE
            binding.tvDontHaveAccount.visibility = View.GONE
            binding.tvUserName.visibility = View.VISIBLE
            binding.tvUserEmail.visibility = View.VISIBLE
            binding.ivEditProfileImage.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        val brainCount =
            sharedPreferences.getInt(UserSettings().BRAIN, UserSettings().getCustomBrain())
        binding.tvBrainCount.text = brainCount.toString()
        if (viewModel.currentUser == null) {
            toggleRegisterUI(true)
        }
    }
}