package com.gamopy.sudoku.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import com.googlecode.tesseract.android.TessBaseAPI
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class SudokuImageProcessor {
    companion object {
        private const val TAG = "SudokuImageProcessor"
        fun processImage(image: ImageView, context: Context): List<Int> {
            val bitmap = imageToBitmap(image) ?: return emptyList()
            val mat = Mat()
            Utils.bitmapToMat(bitmap, mat)

            // 1. Preprocess image
            val preprocessedMat: Mat = preprocessImage(mat)
            //saveMatAsImage(preprocessedMat, "step1_Preprocess")

            // 2. Find contours and corners
            val largestContour: MatOfPoint = findLargestContour(preprocessedMat)
            setMatOfPointAsImage(largestContour, preprocessedMat.size(), "step2_largestContour")
            val corners: List<Point> = getCornersFromContour(largestContour)
            //saveMatAsImage(largestContour, "step2_blurred")

            // 3. Warp original image
            val warpedMat: Mat = warpImage(mat, corners)
            //saveMatAsImage(warpedMat, "step3_warp")

            // 4. Preprocess warped image
            val warpedPreprocessedMat = preprocessImage(warpedMat)
            //saveMatAsImage(warpedPreprocessedMat, "step4_preprocess_warp")

            // 5. Get grid lines
            val gridLinesMat = getGridLines(warpedPreprocessedMat)
            //saveMatAsImage(gridLinesMat, "step5_grid_lines")

            // 6. Combine lines and threshold
            val combinedLinesMat = combineGridLines(gridLinesMat)
            //saveMatAsImage(combinedLinesMat, "step6_combined_lines")

            // 7. Use HoughLines to find endpoints and draw grid
            val gridMat = drawGridLines(combinedLinesMat, warpedMat)
            //saveMatAsImage(gridMat, "step7_grid_mat")

            // 8. Bitwise NOT with warped image to mask grid lines
//            val maskedMat = maskGridLines(warpedMat, gridMat, image)
//            saveMatAsImage(maskedMat, "step8_masked_mat")

            // 9. Split into 81 squares
//            val squares: List<Mat> = splitIntoSquares(maskedMat)
            val squares: List<Mat> = splitIntoSquares(gridMat)

            // 10. Clean each square
            //val cleanedSquares: List<Mat> = cleanSquares(squares)

            // 11. Resize each image to 32x32
            val resizedSquares: List<Bitmap> = resizeSquares(squares)


            // 12. Run OCR on each cleaned image
            return runOCROnSquares(resizedSquares, context)
        }

        private fun invertImage(image: Mat): Mat {
            val mat = Mat()
            Core.bitwise_not(image, mat)
            return mat
        }

        private fun binarization(src: Mat): Mat {
            val grayImage = Mat()
            val thresh = Mat()
            val imBw = Mat()

            Imgproc.cvtColor(src, grayImage, Imgproc.COLOR_BGR2GRAY)
            Imgproc.threshold(grayImage, thresh, 140.0, 255.0, Imgproc.THRESH_BINARY)
            Imgproc.cvtColor(thresh, imBw, Imgproc.COLOR_GRAY2BGR)
            return imBw
        }

        private fun noiseRemoval() {

        }

        private fun removeBorder(image: Mat): Mat {
            // Convert the image to grayscale
            val grayImage = Mat()
            Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY)

            // Threshold the grayscale image
            val thresh = Mat()
            Imgproc.threshold(grayImage, thresh, 140.0, 255.0, Imgproc.THRESH_BINARY)

            // Find contours
            val contours = mutableListOf<MatOfPoint>()
            val hierarchy = Mat()
            Imgproc.findContours(
                thresh,
                contours,
                hierarchy,
                Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_SIMPLE
            )

            // Sort contours by area and get the largest contour
            val cntsSorted = contours.sortedBy { Imgproc.contourArea(it) }
            val cnt = cntsSorted.last()

            // Get bounding rectangle coordinates
            val boundingRect = Imgproc.boundingRect(cnt)
            val x = boundingRect.x
            val y = boundingRect.y
            val w = boundingRect.width
            val h = boundingRect.height

            // Crop the image using the bounding rectangle
            val crop = Mat(image, Rect(x, y, w, h))
            return crop
        }

        private fun imageToBitmap(image: ImageView): Bitmap? {
            val drawable = image.drawable as BitmapDrawable
            val bitmap = drawable.bitmap
            return bitmap
        }

        /***
         *  Makes digits and grid lines stands out clearly. Makes them thick. Removes lighting noise and small variations
         *  @return Mat with bright white thick  GRID lines and digits
         */
        private fun preprocessImage(mat: Mat): Mat {
            val gray = Mat()
            Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY)

            val blurred = Mat()
            Imgproc.GaussianBlur(gray, blurred, Size(5.0, 5.0), 0.0)

            //Convert into pure black and white
            val binary = Mat()
            Imgproc.adaptiveThreshold(
                blurred,
                binary,
                255.0,
                Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                Imgproc.THRESH_BINARY_INV,
                11,
                2.0
            )

            // Thickens white region, connect broken digit strokes, strengthen grid lines
            val dilated = Mat()
            Imgproc.dilate(
                binary,
                dilated,
                Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(2.0, 2.0))
            )

            return dilated
        }

        /***
         * Looks for all white shapes in image, measure area of each shape and return largest one
         * @return outside boundary of sudoku grid (having largest area)
         */
        private fun findLargestContour(mat: Mat): MatOfPoint {
            val contours = mutableListOf<MatOfPoint>()
            Imgproc.findContours(
                mat, // white object on balck background
                contours,
                Mat(),
                Imgproc.RETR_EXTERNAL, // only outermost shapes
                Imgproc.CHAIN_APPROX_SIMPLE // compresses points to save memory
            )

            var maxArea = 0.0
            var largestContour: MatOfPoint? = null
            for (contour in contours) {
                val area = Imgproc.contourArea(contour)
                if (area > maxArea) {
                    maxArea = area
                    largestContour = contour
                }
            }
            return largestContour ?: MatOfPoint()
        }

        /***
         * Extract 4 corner points from image by converting it to polygon
         */
        private fun getCornersFromContour(contour: MatOfPoint): List<Point> {
            val points = contour.toList()
            val approx = MatOfPoint2f()
            Imgproc.approxPolyDP(
                MatOfPoint2f(*points.toTypedArray()),
                approx,
                0.02 * Imgproc.arcLength(MatOfPoint2f(*points.toTypedArray()), true),
                true
            )

            val sortedCorners = sortCorners(approx.toList())
            return sortedCorners
        }

        /***
         * Takes tilted photo and convert into perfect top down square.
         * @param mat
         * @param corners
         * @return mat with perfect top down square
         */
        private fun warpImage(mat: Mat, corners: List<Point>): Mat {
            val srcPoints = MatOfPoint2f(*corners.toTypedArray())
            val dstPoints = MatOfPoint2f(
                Point(0.0, 0.0),
                Point(450.0, 0.0),
                Point(450.0, 450.0),
                Point(0.0, 450.0)
            )
            val perspectiveTransform = Imgproc.getPerspectiveTransform(srcPoints, dstPoints)
            val warpedMat = Mat()
            Imgproc.warpPerspective(mat, warpedMat, perspectiveTransform, Size(450.0, 450.0))
            return warpedMat
        }

        private fun getGridLines(mat: Mat): Mat {
            val horizontal = Mat()
            val vertical = Mat()
            val size = 450 / 9
            val horizontalStructure =
                Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(size.toDouble(), 1.0))
            Imgproc.erode(mat, horizontal, horizontalStructure)
            Imgproc.dilate(horizontal, horizontal, horizontalStructure)

            val verticalStructure =
                Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(1.0, size.toDouble()))
            Imgproc.erode(mat, vertical, verticalStructure)
            Imgproc.dilate(vertical, vertical, verticalStructure)

            val gridLinesMat = Mat()
            Core.add(horizontal, vertical, gridLinesMat)
            Imgproc.threshold(gridLinesMat, gridLinesMat, 128.0, 255.0, Imgproc.THRESH_BINARY)
            Imgproc.dilate(
                gridLinesMat,
                gridLinesMat,
                Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(3.0, 3.0))
            )
            return gridLinesMat
        }

        private fun combineGridLines(gridLinesMat: Mat): Mat {
            val lines = Mat()
            Imgproc.HoughLinesP(gridLinesMat, lines, 1.0, Math.PI / 180, 100, 100.0, 10.0)
            val combinedLinesMat = Mat.zeros(gridLinesMat.size(), CvType.CV_8UC1)
            for (i in 0 until lines.rows()) {
                val l = lines[i, 0]
                Imgproc.line(
                    combinedLinesMat,
                    Point(l[0], l[1]),
                    Point(l[2], l[3]),
                    Scalar(255.0, 0.0, 0.0),
                    2
                )
            }
            return combinedLinesMat
        }

        private fun drawGridLines(combinedLinesMat: Mat, warpedMat: Mat): Mat {
            val resultMat = warpedMat.clone()
            val lines = Mat()
            Imgproc.HoughLinesP(combinedLinesMat, lines, 1.0, Math.PI / 180, 100, 100.0, 10.0)
            for (i in 0 until lines.rows()) {
                val l = lines[i, 0]
                Imgproc.line(
                    resultMat,
                    Point(l[0], l[1]),
                    Point(l[2], l[3]),
                    Scalar(255.0, 0.0, 0.0),
                    2
                )
            }
            return resultMat
        }

        private fun maskGridLines(warpedMat: Mat, gridMat: Mat, image: ImageView): Mat {
            val mask = Mat()
            Core.bitwise_not(gridMat, mask)
            saveMatAsImage(mask, "mask_inverted")

            val maskedMat = Mat()
            Core.bitwise_and(warpedMat, mask, maskedMat)
            saveMatAsImage(maskedMat, "masked_image")

            val bitmap =
                Bitmap.createBitmap(maskedMat.cols(), maskedMat.rows(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(maskedMat, bitmap)
            image.setImageBitmap(bitmap)
            return maskedMat
        }


        private fun splitIntoSquares(maskedMat: Mat): List<Mat> {
            val cellSize = maskedMat.rows() / 9
            val squares = mutableListOf<Mat>()
            for (i in 0 until 9) {
                for (j in 0 until 9) {
                    val cell = maskedMat.submat(
                        i * cellSize,
                        (i + 1) * cellSize,
                        j * cellSize,
                        (j + 1) * cellSize
                    )
                    squares.add(cell)
                }
            }
            return squares
        }

        private fun cleanSquares(squares: List<Mat>): List<Mat> {
            return squares.map { cell ->
                // Convert cell to grayscale
                val grayCell = Mat()
                Imgproc.cvtColor(cell, grayCell, Imgproc.COLOR_BGR2GRAY)
//                saveMatAsImage(grayCell, "grayCell")

                // Apply Gaussian Blur
                val blurredCell = Mat()
                Imgproc.GaussianBlur(grayCell, blurredCell, Size(5.0, 5.0), 0.0)
//                saveMatAsImage(blurredCell, "blurredCell")

                // Apply Adaptive Threshold
                val binaryCell = Mat()
                Imgproc.adaptiveThreshold(
                    blurredCell,
                    binaryCell,
                    255.0,
                    Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                    Imgproc.THRESH_BINARY_INV,
                    11,
                    2.0
                )

                // Ensure binaryCell is of type CV_8UC1
                val correctFormatBinaryCell = Mat()
                binaryCell.convertTo(correctFormatBinaryCell, CvType.CV_8UC1)

                // Return the thresholded image directly
                correctFormatBinaryCell
            }
        }


        private fun resizeSquares(squares: List<Mat>): List<Bitmap> {
            return squares.map { cell ->
                val resized = Mat()
                val preProcessed = removeBorder((cell))
                val binarized = binarization(invertImage(preProcessed))
                Imgproc.resize(binarized, resized, Size(32.0, 32.0))
                val bitmap = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888)
                Utils.matToBitmap(resized, bitmap)
                bitmap
            }
        }

        private fun runOCROnSquares(
            squares: List<Bitmap>,
            context: Context
        ): List<Int> {
            val tess = initTesseract(context)
            val results = mutableListOf<Int>()

            squares.forEachIndexed { index, bitmap ->
                tess.setImage(bitmap)
                val ocrResult = tess.utF8Text.trim()
                if (ocrResult.length == 1 && ocrResult[0].isDigit()) {
                    Log.d("Digit at $index", ocrResult)

                    // Convert the OCR result to an integer
                    val digit = ocrResult.toIntOrNull() ?: 0

                    // Add the digit to the results list
                    results.add(digit)
                } else {
                    Log.d("Digit at $index", "Invalid digit: $ocrResult")
                    results.add(0) // Optionally, add 0 for invalid digits
                }
            }

            return results
        }

        private fun initTesseract(context: Context): TessBaseAPI {
            val tessBaseAPI = TessBaseAPI()
            // Path to the storage directory
            val datapath = "${context.filesDir.absolutePath}/"

            // Ensure the directory exists
            val dir = File(datapath + "tessdata/")
            if (!dir.exists()) {
                dir.mkdirs()
                copyTrainedDataFiles(datapath, context)
            }
            if (!tessBaseAPI.init(datapath, "eng")) {
                Log.e("Tesseract", "Could not initialize Tesseract.")
            }
            tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "0123456789")
            return tessBaseAPI
        }

        private fun copyTrainedDataFiles(datapath: String, context: Context) {
            try {
                val assetManager = context.assets
                val inputStream = assetManager.open("tessdata/eng.traineddata")
                val outputFile = File(datapath + "tessdata/eng.traineddata")
                val outputStream = FileOutputStream(outputFile)

                val buffer = ByteArray(1024)
                var read: Int

                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }

                inputStream.close()
                outputStream.flush()
                outputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        /**
         * To save Mat as Image. Use for debugging.
         * @param mat
         * Mat to be converted into Image
         * @param name
         * Name of image that will be saved in internal storage
         */
        private fun saveMatAsImage(mat: Mat, name: String) {
            val timestamp = System.currentTimeMillis()
            val path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath
            val file = File(path, "$name$timestamp.png")
            val bmp = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(mat, bmp)
            val outputStream = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        }

        /**
         * To save bitmap as image in internal storage. Use for debugging of processed images
         * @param bitmap
         * Bitmap to be converted into Image
         * @param name
         * Name of image that will be saved in internal storage
         */
        private fun saveBitmapAsImage(bitmap: Bitmap, name: String) {
            val timestamp = System.currentTimeMillis()
            val path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath
            val file = File(path, "$name$timestamp.png")
            if (file.exists()) {
                Log.w("saveBitmapAsImage", "File already exists at $name, overwriting.")
            } else {
                // Ensure the parent directories exist
                file.parentFile?.mkdirs()
            }

            var fileOutputStream: FileOutputStream? = null
            try {
                fileOutputStream = FileOutputStream(file)
                // Compress the bitmap and write to the output stream
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
                fileOutputStream.flush()
                Log.d("saveBitmapAsImage", "Saved bitmap to $name")
            } catch (e: IOException) {
                Log.e("saveBitmapAsImage", "Failed to save bitmap", e)
            } finally {
                try {
                    fileOutputStream?.close()
                } catch (e: IOException) {
                    Log.e("saveBitmapAsImage", "Failed to close FileOutputStream", e)
                }
            }
        }

        private fun sortCorners(corners: List<Point>): List<Point> {
            val sortedCorners = corners.toMutableList()
            sortedCorners.sortBy { it.x + it.y } // Top-left corner will have the smallest sum
            val topLeft = sortedCorners[0]

            sortedCorners.sortBy { it.x - it.y } // Bottom-left corner will have the smallest difference
            val bottomLeft = sortedCorners[0]

            sortedCorners.sortBy { it.y - it.x } // Top-right corner will have the smallest difference
            val topRight = sortedCorners[0]

            sortedCorners.sortBy { it.x + it.y } // Bottom-right corner will have the largest sum
            val bottomRight = sortedCorners[3]

            return listOf(topLeft, topRight, bottomRight, bottomLeft)
        }

        /***
         * Creates a black image, draws a contour in white color and saves it to disk
         */
        private fun setMatOfPointAsImage(
            matOfPoint: MatOfPoint,
            imageSize: Size,
            fileName: String
        ) {
            // Create a blank Mat with the same dimensions as the original image
            val contourImage = Mat.zeros(imageSize, CvType.CV_8UC3)

            // Draw the contour on the blank image
            val contours = listOf(matOfPoint)
            Imgproc.drawContours(contourImage, contours, -1, Scalar(255.0, 255.0, 255.0), 1)

            // Save the image to file
            saveMatAsImage(contourImage, fileName)
        }


        fun matToBitmap(mat: Mat): Bitmap {
            val bmp = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(mat, bmp)
            return bmp
        }
    }
}