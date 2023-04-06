package com.lightningkite.rx.viewgenerators


import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.map
import com.badoo.reaktive.maybe.mapNotNull
import com.badoo.reaktive.single.filter
import com.badoo.reaktive.single.flatMap
import com.badoo.reaktive.single.mapNotNull
import java.io.*
import java.util.*

private fun ActivityAccess.requestSomethings(type: String, prompt: String): Maybe<List<Uri>> =
    requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        .flatMap {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = type
            getIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = type

            val chooserIntent = Intent.createChooser(getIntent, prompt)
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

            this.startActivityForResult(chooserIntent)
        }
        .filter { it.code == Activity.RESULT_OK }
        .mapNotNull {
            it.data?.clipData?.let { (0 until it.itemCount).map { index -> it.getItemAt(index).uri } }
                ?: it.data?.data?.let { listOf(it) }
        }

private fun ActivityAccess.requestSomething(type: String, prompt: String): Maybe<Uri> =
    requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        .flatMap {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = type

            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = type

            val chooserIntent = Intent.createChooser(getIntent, prompt)
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

            this.startActivityForResult(chooserIntent)
        }
        .filter { it.code == Activity.RESULT_OK }
        .mapNotNull { it.data?.data }

/**
 * Starts a new activity to get images from the gallery.
 * Handles permissions by itself.
 */
fun ActivityAccess.requestImagesGallery(): Maybe<List<Uri>> = requestSomethings("image/*", "Select Images")

/**
 * Starts a new activity to get an image from the gallery.
 * Handles permissions by itself.
 */
fun ActivityAccess.requestImageGallery(): Maybe<Uri> = requestSomething("image/*", "Select Image")

/**
 * Starts a new activity to get an image form the camera.
 * Handles permissions by itself.
 */
fun ActivityAccess.requestImageCamera(
    front: Boolean = false
): Maybe<Uri> {
    val fileProviderAuthority = context.packageName + ".fileprovider"
    val file = File(context.cacheDir, "images").also { it.mkdirs() }
        .let { File.createTempFile("image", ".jpg", it) }
        .let { FileProvider.getUriForFile(context, fileProviderAuthority, it) }

    return requestPermission(Manifest.permission.CAMERA)
        .flatMap {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, file)
            if (front) {
                intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
                intent.putExtra("android.intent.extras.CAMERA_FACING", 1)
                intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
            }
            this.startActivityForResult(intent)
        }
        .filter { it.code == Activity.RESULT_OK }
        .mapNotNull { it.data?.data ?: file }
}


/**
 * Starts a new activity to get a video from the gallery.
 * Handles permissions by itself.
 */
fun ActivityAccess.requestVideoGallery(): Maybe<Uri> = requestSomething("video/*", "Select Video")

/**
 * Starts a new activity to get videos from the gallery.
 * Handles permissions by itself.
 */
fun ActivityAccess.requestVideosGallery(): Maybe<List<Uri>> = requestSomethings("video/*", "Select Videos")


/**
 * Starts a new activity to get a video from the camera.
 * Handles permissions by itself.
 */
fun ActivityAccess.requestVideoCamera(front: Boolean = false): Maybe<Uri> {
    val fileProviderAuthority = context.packageName + ".fileprovider"
    val file = File(context.cacheDir, "videos").also { it.mkdirs() }
        .let { File.createTempFile("video", ".mp4", it) }
        .let { FileProvider.getUriForFile(context, fileProviderAuthority, it) }
    return requestPermission(Manifest.permission.CAMERA)
        .flatMap {
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, file)
            if (front) {
                intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
                intent.putExtra("android.intent.extras.CAMERA_FACING", 1)
                intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
            }

            this.startActivityForResult(intent)
        }
        .mapNotNull { it.data?.data ?: file }
}


/**
 * Starts a new activity to get images and videos from the gallery.
 * Handles permissions by itself.
 */
fun ActivityAccess.requestMediasGallery(): Maybe<List<Uri>> =
    requestSomethings("video/*,image/*", "Select videos and images")

/**
 * Starts a new activity to get an image or video from the gallery.
 * Handles permissions by itself.
 */
fun ActivityAccess.requestMediaGallery(): Maybe<Uri> = requestSomething("video/*,image/*", "Select video or image")

/**
 * Starts a new activity to get a file.
 * Handles permissions by itself.
 */
fun ActivityAccess.requestFile(type: String = "*/*"): Maybe<Uri> = requestSomething(type, "Select file")

/**
 * Starts a new activity to get files.
 * Handles permissions by itself.
 */
fun ActivityAccess.requestFiles(type: String = "*/*"): Maybe<List<Uri>> = requestSomethings(type, "Select files")

/**
 * Shortcut to get the MIME type of the given [uri].
 */
fun ActivityAccess.getMimeType(
    uri: Uri
): String? {
    val cr = context.contentResolver
    return cr.getType(uri)
}

/**
 * Shortcut to get the file name of the given [uri].
 */
@SuppressLint("Range")
fun ActivityAccess.getFileName(uri: Uri): String? {
    if (uri.scheme.equals("content")) {
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)).substringAfterLast('/')
            }
        } finally {
            cursor!!.close()
        }
    }
    return null
}

