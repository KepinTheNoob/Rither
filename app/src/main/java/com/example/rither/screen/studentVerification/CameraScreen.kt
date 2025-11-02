package com.example.rither.screen.studentVerification

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.example.rither.data.Screen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.roundToInt

private val REL_NAME = android.graphics.RectF(0.25f, 0.74f, 0.60f, 0.80f)
private val REL_BINUSIAN = android.graphics.RectF(0.25f, 0.80f, 0.50f, 0.86f)
private val REL_STUDENT_ID = android.graphics.RectF(0.25f, 0.86f, 0.50f, 0.92f)
private val REL_UNIVERSITY = android.graphics.RectF(0.25f, 0.92f, 0.60f, 0.98f)


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val imageCapture = remember { ImageCapture.Builder().build() }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    var nim by remember { mutableStateOf("") }
    var studentName by remember { mutableStateOf("") }
    var university by remember { mutableStateOf("") }
    var binusianId by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }

    var previewSize by remember { mutableStateOf(IntSize(1, 1)) }

    LaunchedEffect(Unit) {
        cameraPermissionState.launchPermissionRequest()
    }

    if (cameraPermissionState.status.isGranted) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(16.dp))
                        .onGloballyPositioned { coordinates ->
                            previewSize = coordinates.size
                        }
                ) {
                    AndroidView(
                        factory = { context ->
                            val previewView = PreviewView(context).apply {
                                scaleType = PreviewView.ScaleType.FILL_CENTER
                            }
                            startCamera(context, lifecycleOwner, previewView, imageCapture)
                            previewView
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        drawScannerOverlay(this, previewSize)
                    }
                }
            }


            // Capture Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        if (!isProcessing) {
                            isProcessing = true
                            captureImage(context, imageCapture, cameraExecutor) { file ->
                                // on capture success: crop regions based on preview coords & run OCR
                                try {
                                    processCapturedFileForBINUSFields(
                                        context,
                                        file,
                                        previewSize,
                                        // relative rectangles that define the holes -- MUST match overlay used in drawScannerOverlay
                                        onResult = { map ->
                                            binusianId = map["binusianId"] ?: ""
                                            nim = map["studentId"] ?: ""
                                            studentName = map["name"] ?: ""
                                            university = map["university"] ?: ""
                                            isProcessing = false
                                        }
                                    )
                                } catch (e: Exception) {
                                    Log.e(
                                        "CameraScreen",
                                        "Error processing captured image: ${e.message}",
                                        e
                                    )
                                    isProcessing = false
                                }
                            }
                        }
                    },
                    enabled = !isProcessing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(WindowInsets.navigationBars.asPaddingValues())
                ) {
                    Text(if (isProcessing) "Processing..." else "Capture Student Card")
                }
            }

            // Display extracted fields
            if (nim.isNotEmpty() || studentName.isNotEmpty() || university.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "🔍 Extracted Information",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    InfoRow(label = "Student ID", value = nim)
                    InfoRow(label = "Binusian ID", value = binusianId)
                    InfoRow(label = "Name", value = studentName)
                    InfoRow(label = "University", value = university)

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            navController.navigate(
                                Screen.Signup.name +
                                        "?name=${Uri.encode(studentName)}&studentId=${Uri.encode(nim)}&binusianId=${
                                            Uri.encode(
                                                binusianId
                                            )
                                        }&university=${Uri.encode(university)}"
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Confirm & Save")
                    }
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Camera permission required.")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                Text("Grant Permission")
            }
        }
    }
}

private fun drawScannerOverlay(drawScope: DrawScope, previewSize: IntSize) {
    val widthPx = previewSize.width.toFloat().coerceAtLeast(1f)
    val heightPx = previewSize.height.toFloat().coerceAtLeast(1f)

    // semi-transparent full screen
    drawScope.drawRect(
        color = Color(0x88000000)
    )

    // helper to clear a relative rect
    fun clearRelRect(rel: android.graphics.RectF) {
        val left = rel.left * widthPx
        val top = rel.top * heightPx
        val right = (rel.left + rel.width()) * widthPx
        val bottom = (rel.top + rel.height()) * heightPx
        // Clear (make transparent)
        drawScope.drawContext.canvas.saveLayer(
            bounds = Rect(0f, 0f, widthPx, heightPx),
            paint = Paint()
        )
        drawScope.drawRect(
            color = Color.Transparent,
            topLeft = Offset(left, top),
            size = Size(right - left, bottom - top),
            blendMode = BlendMode.Clear
        )
        drawScope.drawContext.canvas.restore()
        // draw border around hole
        drawScope.drawRect(
            color = Color.White.copy(alpha = 0.65f),
            topLeft = Offset(left, top),
            size = Size(right - left, bottom - top),
            style = Stroke(width = 3f)
        )
    }

    clearRelRect(REL_UNIVERSITY)
    clearRelRect(REL_NAME)
    clearRelRect(REL_BINUSIAN)
    clearRelRect(REL_STUDENT_ID)
}

private fun captureImage(
    context: Context,
    imageCapture: ImageCapture,
    executor: ExecutorService,
    onCaptureSuccess: (File) -> Unit
) {
    val photoFile = File(
        context.externalCacheDir,
        "student_card_${System.currentTimeMillis()}.jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                Log.e("CameraScreen", "Photo capture failed: ${exc.message}", exc)
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                Log.d("CameraScreen", "Photo saved at: ${photoFile.absolutePath}")
                onCaptureSuccess(photoFile)
            }
        }
    )
}

private fun startCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    imageCapture: ImageCapture
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (exc: Exception) {
            Log.e("CameraScreen", "Use case binding failed", exc)
        }
    }, ContextCompat.getMainExecutor(context))
}

/* Helper: load bitmap respecting EXIF orientation */
private fun loadBitmapWithCorrectOrientation(path: String): Bitmap? {
    try {
        // decode with bounds first to avoid OOM for large images if needed
        val opts = BitmapFactory.Options().apply { inPreferredConfig = Bitmap.Config.ARGB_8888 }
        var bmp = BitmapFactory.decodeFile(path, opts) ?: return null

        val exif = ExifInterface(path)
        val orientation =
            exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            else -> { /* no rotation */
            }
        }
        return Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix, true)
    } catch (e: IOException) {
        Log.e("CameraScreen", "Failed to load bitmap: ${e.message}", e)
        return null
    }
}

private fun cropBitmapRelativeToPreview(
    bitmap: Bitmap,
    previewSize: IntSize,
    relRect: android.graphics.RectF
): Bitmap {
    val bmpW = bitmap.width.toFloat()
    val bmpH = bitmap.height.toFloat()
    val previewW = previewSize.width.toFloat().coerceAtLeast(1f)
    val previewH = previewSize.height.toFloat().coerceAtLeast(1f)

    // 1) Compute scale used by FILL_CENTER (center-crop): image scaled to cover preview
    val scale = maxOf(previewW / bmpW, previewH / bmpH)

    // 2) Size of the bitmap as it's displayed inside the preview (after scaling)
    val displayedW = bmpW * scale
    val displayedH = bmpH * scale

    // 3) Offsets (how much displayed image is shifted relative to preview top-left)
    //    If displayed > preview, image is larger and gets cropped; offset is positive.
    val offsetX = (displayedW - previewW) / 2f
    val offsetY = (displayedH - previewH) / 2f

    // 4) Compute the rectangle in preview pixels
    val previewLeftPx = relRect.left * previewW
    val previewTopPx = relRect.top * previewH
    val previewWpx = relRect.width() * previewW
    val previewHpx = relRect.height() * previewH

    // 5) Map preview coordinates into displayed-image coordinates by adding offset
    val displayedLeftPx = previewLeftPx + offsetX
    val displayedTopPx = previewTopPx + offsetY

    // 6) Convert displayed-image coordinates back to bitmap coordinates by dividing by scale
    val bmpLeft = (displayedLeftPx / scale).roundToInt()
    val bmpTop = (displayedTopPx / scale).roundToInt()
    val bmpWpx = (previewWpx / scale).roundToInt()
    val bmpHpx = (previewHpx / scale).roundToInt()

    // 7) Bound-check and coerce to valid area
    val l = bmpLeft.coerceIn(0, bitmap.width - 1)
    val t = bmpTop.coerceIn(0, bitmap.height - 1)
    val rw = if (l + bmpWpx > bitmap.width) bitmap.width - l else bmpWpx
    val rh = if (t + bmpHpx > bitmap.height) bitmap.height - t else bmpHpx

    // Fallback minimal sizes
    val cropW = rw.coerceAtLeast(1)
    val cropH = rh.coerceAtLeast(1)

    return Bitmap.createBitmap(bitmap, l, t, cropW, cropH)
}

private fun processCapturedFileForBINUSFields(
    context: Context,
    imageFile: File,
    previewSize: IntSize,
    onResult: (Map<String, String>) -> Unit
) {
    // load with orientation fix
    val bitmap = loadBitmapWithCorrectOrientation(imageFile.absolutePath) ?: run {
        onResult(emptyMap())
        return
    }

    // Crop regions using relative rects (REL_*)
    val nameBmp = cropBitmapRelativeToPreview(bitmap, previewSize, REL_NAME)
    val idBmp = cropBitmapRelativeToPreview(bitmap, previewSize, REL_BINUSIAN)
    val studentIdBmp = cropBitmapRelativeToPreview(bitmap, previewSize, REL_STUDENT_ID)
    val uniBmp = cropBitmapRelativeToPreview(bitmap, previewSize, REL_UNIVERSITY)

    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    val result = mutableMapOf<String, String>()

    // helper to process a bitmap synchronously via listeners
    fun ocrBitmap(bmp: Bitmap, onDone: (String) -> Unit) {
        val input = InputImage.fromBitmap(bmp, 0)
        recognizer.process(input)
            .addOnSuccessListener { visionText ->
                onDone(visionText.text.trim())
            }
            .addOnFailureListener { e ->
                Log.e("OCR", "region OCR failed: ${e.message}", e)
                onDone("")
            }
    }

    // chain OCR calls
    ocrBitmap(nameBmp) { nameText ->
        result["name"] = nameText

        // combine id region (binusian) and studentId region to increase chance of detection
        ocrBitmap(idBmp) { idRegionText ->
            ocrBitmap(studentIdBmp) { stuIdRegionText ->
                val combinedIds = (idRegionText + "\n" + stuIdRegionText).trim()
                val bnRegex = "\\bBN\\d{9}\\b".toRegex()
                val nimRegex = "\\b\\d{10}\\b".toRegex()

                result["binusianId"] = bnRegex.find(combinedIds)?.value ?: ""
                result["studentId"] = nimRegex.find(combinedIds)?.value ?: ""

                ocrBitmap(uniBmp) { uniText ->
                    result["university"] = if (uniText.contains(
                            "Binus",
                            ignoreCase = true
                        )
                    ) "Binus University" else uniText
                    onResult(result)
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "$label:", modifier = Modifier.weight(1f))
        Text(text = value, modifier = Modifier.weight(2f))
    }
}
