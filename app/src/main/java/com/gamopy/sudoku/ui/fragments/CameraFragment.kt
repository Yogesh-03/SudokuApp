package com.gamopy.sudoku.ui.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.gamopy.sudoku.R
import com.gamopy.sudoku.databinding.FragmentCameraBinding
import com.gamopy.sudoku.image.SudokuImageProcessor
import com.gamopy.sudoku.sharedpreferences.UserSettings
import com.gamopy.sudoku.sudokugenerator.Constants
import com.gamopy.sudoku.sudokugenerator.Constants.Companion.TAG
import com.gamopy.sudoku.sudokugenerator.Solver
import com.gamopy.sudoku.ui.SudokuPlayActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import org.opencv.core.Rect as OpenCVRect


class CameraFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentCameraBinding
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var originalBitmap: Bitmap
    private lateinit var originalMat: Mat

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCameraBinding.inflate(layoutInflater, container, false)

        OpenCVLoader.initDebug()
        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.apply {
            imageView.visibility = View.GONE
            selectionView.visibility = View.GONE
        }

        sharedPreferences =
            requireActivity().getSharedPreferences(UserSettings().PREFERENCES, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            startCamera(cameraProvider)
        }, ContextCompat.getMainExecutor(requireContext()))



        if (allPermissionsGranted()) {
            //Toast.makeText(requireContext(), "We have Permission", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                Constants.REQUIRED_PERMISSION,
                Constants.REQUEST_CODE_PERMISSION
            )
        }

        binding.btnDone.setOnClickListener {
            // Declare the result variable
            var result: List<Int>
            binding.pbCamera.visibility = View.VISIBLE
            binding.btnDone.visibility = View.GONE
            binding.btnTakeImage.visibility = View.GONE
            binding.previewView.visibility = View.GONE

            // Launch the coroutine to perform background work
            CoroutineScope(Dispatchers.IO).launch {
                // Perform the image processing in the IO dispatcher
                result = SudokuImageProcessor.processImage(binding.imageView, requireContext())

                // Switch back to the main dispatcher for UI operations
                withContext(Dispatchers.Main) {
                    // Proceed only if the result is not empty
                    binding.imageView.visibility = View.GONE
                    if (result.isNotEmpty()) {
                        // Convert result to ArrayList
                        val resultList: ArrayList<Int> = ArrayList(result)

                        // Prepare the 2D array
                        val gridSize = 9
                        val array2D: Array<IntArray> = Array(gridSize) { rowIndex ->
                            IntArray(gridSize) { colIndex ->
                                result[rowIndex * gridSize + colIndex]
                            }
                        }

                        // Count the number of zeroes
                        val zeroes = result.count { it == 0 }

                        // Prepare the bundle and user settings
                        val bundle = Bundle()
                        val userSettings = UserSettings()

                        if ((81 - zeroes) <= 18) {
                            Toast.makeText(requireContext(), "Error occured", Toast.LENGTH_SHORT)
                                .show()
                            this@CameraFragment.dismiss()
                        }

                        when {
                            (81 - zeroes) <= 32 -> {
                                bundle.putString("expert", "Expert")
                                editor.putString(userSettings.CURRENT_GAME_DIFFICULTY, "Expert")
                            }

                            (81 - zeroes) <= 37 -> {
                                bundle.putString("hard", "Hard")
                                editor.putString(userSettings.CURRENT_GAME_DIFFICULTY, "Hard")
                            }

                            (81 - zeroes) <= 43 -> {
                                bundle.putString("medium", "Medium")
                                editor.putString(userSettings.CURRENT_GAME_DIFFICULTY, "Medium")
                            }

                            (81 - zeroes) <= 53 -> {
                                bundle.putString("easy", "Easy")
                                editor.putString(userSettings.CURRENT_GAME_DIFFICULTY, "Easy")
                            }
                        }
                        bundle.putString("scanned", "true")
                        bundle.putIntegerArrayList("list", resultList)

                        // Check if the Sudoku puzzle is solvable
                        if (Solver().solvable(array2D)) {
                            val intent = Intent(activity, SudokuPlayActivity::class.java)
                            userSettings.setCurrentGame(true)
                            editor.putBoolean(
                                userSettings.IS_CURRENT_GAME,
                                userSettings.getCurrentGame()
                            )
                            editor.apply()
                            intent.putExtras(bundle)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            Log.d("SOLVER", "Solvable -> true")
                            delay(7000)
                            startActivity(intent)
                            this@CameraFragment.dismiss()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Try scanning properly, there is some issue",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        // Handle the case where the result is empty
                        Toast.makeText(
                            requireContext(),
                            "Failed to process image",
                            Toast.LENGTH_SHORT
                        ).show()
                        this@CameraFragment.dismiss()
                    }
                }
            }


        }

        return binding.root
    }

    private fun allPermissionsGranted() =
        Constants.REQUIRED_PERMISSION.all {
            ContextCompat.checkSelfPermission(
                requireActivity().baseContext, it
            ) == PackageManager.PERMISSION_GRANTED
        }


    private fun startCamera(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(binding.root.findViewById<PreviewView>(R.id.previewView).surfaceProvider)
        }

        val imageCapture = ImageCapture.Builder().build()

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)

        binding.root.findViewById<Button>(R.id.btnTakeImage).setOnClickListener {
            if (binding.btnTakeImage.text == "Crop") {
                cropImage(originalMat, binding.selectionView.getSelectionRect())
                binding.selectionView.visibility = View.GONE
                it.visibility = View.INVISIBLE
                binding.btnDone.visibility = View.VISIBLE
            } else {
                captureImage(imageCapture)
            }

        }
    }


    private fun captureImage(imageCapture: ImageCapture) {
        val photoFile =
            File(requireContext().externalMediaDirs.first(), "${System.currentTimeMillis()}.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
//                    Toast.makeText(
//                        requireActivity().baseContext,
//                        "Photo capture succeeded: $savedUri",
//                        Toast.LENGTH_SHORT
//                    ).show()
                    binding.imageView.setImageURI(savedUri)
                    binding.imageView.visibility = View.VISIBLE
                    binding.previewView.visibility = View.GONE
                    binding.btnTakeImage.text = "Crop"
                    binding.selectionView.visibility = View.VISIBLE

                    val mat = Imgcodecs.imread(photoFile.absolutePath)
                    val bitmap =
                        Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888)
                    Utils.matToBitmap(mat, bitmap)
                   // globalMat = mat
                    originalMat = mat
                    //globalOriginalBitmap = bitmap
                    originalBitmap = bitmap
                    stopCamera()
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
                }
            }
        )
    }

    private fun cropImage(mat: Mat, selectionRect: android.graphics.Rect) {
        // Map the selection rectangle from ImageView coordinates to original image coordinates
        val mappedRect =
            mapRectFromViewToImage(binding.imageView, originalBitmap, selectionRect)

        val left = mappedRect.left.coerceIn(0, mat.width() - 1)
        val top = mappedRect.top.coerceIn(0, mat.height() - 1)
        val right = mappedRect.right.coerceIn(left + 1, mat.width())
        val bottom = mappedRect.bottom.coerceIn(top + 1, mat.height())

        val roi = OpenCVRect(
            left,
            top,
            right - left,
            bottom - top
        )

        val croppedMat1 = Mat(mat, roi)

        val croppedBitmap1 = Bitmap.createBitmap(
                croppedMat1.cols(),
        croppedMat1.rows(),
        Bitmap.Config.ARGB_8888
        )

        Utils.matToBitmap(croppedMat1, croppedBitmap1)

        // ONLY update ImageView (do NOT overwrite originalBitmap)
        binding.imageView.setImageBitmap(croppedBitmap1)

        // Ensure the mapped rectangle is within the bounds of the original image
//        val validRect = OpenCVRect(
//            mappedRect.left.coerceIn(0, mat.width() - 1),
//            mappedRect.top.coerceIn(0, mat.height() - 1),
//            mappedRect.width().coerceIn(1, mat.width() - mappedRect.left),
//            mappedRect.height().coerceIn(1, mat.height() - mappedRect.top)
//        )

        // Crop the original image using the valid rectangle
        //val croppedMat = Mat(mat, validRect)

        // Convert the cropped Mat to Bitmap
//        val croppedBitmap =
//            Bitmap.createBitmap(croppedMat.cols(), croppedMat.rows(), Bitmap.Config.ARGB_8888)
//        Utils.matToBitmap(croppedMat, croppedBitmap)
//        globalOriginalBitmap = croppedBitmap

        // Update the ImageView with the cropped image for verification
//        binding.imageView.setImageBitmap(originalBitmap)
    }

    // Mapping function to convert coordinates
    private fun mapRectFromViewToImage(
        imageView: ImageView,
        bitmap: Bitmap,
        rect: android.graphics.Rect
    ): android.graphics.Rect {
        // Get the ImageView's dimensions and the Bitmap dimensions
        val imageViewWidth = imageView.width.toFloat()
        val imageViewHeight = imageView.height.toFloat()
        val bitmapWidth = bitmap.width.toFloat()
        val bitmapHeight = bitmap.height.toFloat()

        val matrix = imageView.imageMatrix
        val inverse = android.graphics.Matrix()

        matrix.invert(inverse)

        val rectF = android.graphics.RectF(rect)
        inverse.mapRect(rectF)

        // Calculate the scale factor
        val scale = (imageViewWidth / bitmapWidth).coerceAtMost(imageViewHeight / bitmapHeight)

        // Calculate the horizontal and vertical padding (centered image)
        val horizontalPadding = (imageViewWidth - (bitmapWidth * scale)) / 2
        val verticalPadding = (imageViewHeight - (bitmapHeight * scale)) / 2

        // Calculate the mapped rectangle coordinates
        val left =
            ((rect.left - horizontalPadding) / scale).toInt().coerceIn(0, bitmapWidth.toInt())
        val top = ((rect.top - verticalPadding) / scale).toInt().coerceIn(0, bitmapHeight.toInt())
        val right =
            ((rect.right - horizontalPadding) / scale).toInt().coerceIn(0, bitmapWidth.toInt())
        val bottom =
            ((rect.bottom - verticalPadding) / scale).toInt().coerceIn(0, bitmapHeight.toInt())

        // Return the mapped rectangle
        //return android.graphics.Rect(left, top, right, bottom)

        return android.graphics.Rect(
            rectF.left.coerceIn(0f, bitmap.width.toFloat()).toInt(),
            rectF.top.coerceIn(0f, bitmap.height.toFloat()).toInt(),
            rectF.right.coerceIn(0f, bitmap.width.toFloat()).toInt(),
            rectF.bottom.coerceIn(0f, bitmap.height.toFloat()).toInt()
        )
    }




    private fun stopCamera() {
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //sheet behaviour
        bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
        // set to behaviour to expanded and minimum height to parent
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.isDraggable = false

        //set min height to parent view
        binding.constraintLayout.minHeight = Resources.getSystem().displayMetrics.heightPixels
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}