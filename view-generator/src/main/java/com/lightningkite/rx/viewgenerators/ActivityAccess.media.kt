package com.lightningkite.rx.viewgenerators


import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.ContentValues
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import com.lightningkite.rx.viewgenerators.ActivityAccess
import com.lightningkite.rx.viewgenerators.post
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import java.io.*
import java.util.*

/**
 * Starts a new activity to get images from the gallery.
 * Handles permissions by itself.
 */
fun ActivityAccess.requestImagesGallery(): Maybe<List<Uri>> = Maybe.create { em ->
    requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE) { hasPermission ->
        if (hasPermission) {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "image/*"
            getIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "image/*"

            val chooserIntent = Intent.createChooser(getIntent, "Select Image")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

            this.startIntent(chooserIntent) { code, result ->
                if (code == Activity.RESULT_OK) {
                    result?.clipData?.let { clipData ->
                        em.onSuccess((0 until clipData.itemCount).map { index -> clipData.getItemAt(index).uri })
                    } ?: result?.data?.let { em.onSuccess(listOf(it)) }
                } else if(code == Activity.RESULT_CANCELED) {
                    em.onComplete()
                } else {
                    em.onError(Exception("Got result code $code"))
                }
            }
        } else {
            em.onError(SecurityException("User has rejected permission to read external storage"))
        }
    }
}

/**
 * Starts a new activity to get an image from the gallery.
 * Handles permissions by itself.
 */
fun ActivityAccess.requestImageGallery(): Maybe<Uri> = Maybe.create { em ->
    requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE) {
        if (it) {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "image/*"

            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "image/*"

            val chooserIntent = Intent.createChooser(getIntent, "Select Image")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

            this.startIntent(chooserIntent) { code, result ->
                val uri = result?.data
                if (code == Activity.RESULT_OK && uri != null) {
                    em.onSuccess(uri)
                } else if(code == Activity.RESULT_CANCELED) {
                    em.onComplete()
                } else {
                    em.onError(Exception("Got result code $code"))
                }
            }
        } else {
            em.onError(SecurityException("User has rejected permission to read external storage"))
        }
    }
}

/**
 * Starts a new activity to get an image form the camera.
 * Handles permissions by itself.
 */
fun ActivityAccess.requestImageCamera(
    front: Boolean = false
): Maybe<Uri> = Maybe.create { em ->
    val fileProviderAuthority = context.packageName + ".fileprovider"
    val file = File(context.cacheDir, "images").also { it.mkdirs() }
        .let { File.createTempFile("image", ".jpg", it) }
        .let { FileProvider.getUriForFile(context, fileProviderAuthority, it) }
    requestPermission(Manifest.permission.CAMERA) {
        if (it) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, file)
            if (front) {
                intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
                intent.putExtra("android.intent.extras.CAMERA_FACING", 1)
                intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
            }
            startIntent(intent) { code, result ->
                val uri = result?.data ?: file
                if (code == Activity.RESULT_OK) {
                    em.onSuccess(uri)
                } else if(code == Activity.RESULT_CANCELED) {
                    em.onComplete()
                } else {
                    em.onError(Exception("Got result code $code"))
                }
            }
        } else {
            em.onError(SecurityException("User has rejected permission to read external storage"))
        }
    }
}


/**
 * Starts a new activity to get a video from the gallery.
 * Handles permissions by itself.
 */
fun ActivityAccess.requestVideoGallery(): Maybe<Uri> = Maybe.create { em ->
    requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE) {
        if (it) {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "video/*"

            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "video/*"

            val chooserIntent = Intent.createChooser(getIntent, "Select Video")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

            this.startIntent(chooserIntent) { code, result ->
                val uri = result?.data
                if (code == Activity.RESULT_OK && uri != null) {
                    em.onSuccess(uri)
                } else if(code == Activity.RESULT_CANCELED) {
                    em.onComplete()
                } else {
                    em.onError(Exception("Got result code $code"))
                }
            }
        } else {
            em.onError(SecurityException("User has rejected permission to read external storage"))
        }
    }
}

/**
 * Starts a new activity to get videos from the gallery.
 * Handles permissions by itself.
 */
fun ActivityAccess.requestVideosGallery(): Maybe<List<Uri>> = Maybe.create { em ->
    requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE) {
        if (it) {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "video/*"
            getIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "video/*"

            val chooserIntent = Intent.createChooser(getIntent, "Select Video")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

            this.startIntent(chooserIntent) { code, result ->
                if (code == Activity.RESULT_OK) {
                    result?.clipData?.let { clipData ->
                        em.onSuccess((0 until clipData.itemCount).map { index -> clipData.getItemAt(index).uri })
                    } ?: result?.data?.let { em.onSuccess(listOf(it)) }
                }else if(code == Activity.RESULT_CANCELED) {
                    em.onComplete()
                } else {
                    em.onError(Exception("Got result code $code"))
                }
            }
        } else {
            em.onError(SecurityException("User has rejected permission to read external storage"))
        }
    }
}


/**
 * Starts a new activity to get a video from the camera.
 * Handles permissions by itself.
 */
fun ActivityAccess.requestVideoCamera(front: Boolean = false): Maybe<Uri> = Maybe.create { em ->
    val fileProviderAuthority = context.packageName + ".fileprovider"
    val file = File(context.cacheDir, "videos").also { it.mkdirs() }
        .let { File.createTempFile("video", ".mp4", it) }
        .let { FileProvider.getUriForFile(context, fileProviderAuthority, it) }
    requestPermission(Manifest.permission.CAMERA) {
        if (it) {
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, file)
            //TODO:Test this on an older device. This works on newest, but we need to make sure it works/doesn't crash a newer one.
            if (front) {
                intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
                intent.putExtra("android.intent.extras.CAMERA_FACING", 1)
                intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
            }
            startIntent(intent) { code, result ->
                val uri = result?.data ?: file
                if (code == Activity.RESULT_OK ) {
                    em.onSuccess(uri)
                } else if(code == Activity.RESULT_CANCELED) {
                    em.onComplete()
                } else {
                    em.onError(Exception("Got result code $code"))
                }
            }
        } else {
            em.onError(SecurityException("User has rejected permission to read external storage"))
        }
    }
}


/**
 * Starts a new activity to get images and videos from the gallery.
 * Handles permissions by itself.
 */
fun ActivityAccess.requestMediasGallery(): Maybe<List<Uri>> = Maybe.create { em ->
    requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE) {
        if (it) {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "video/*,image/*"
            getIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "video/*,image/*"

            val chooserIntent = Intent.createChooser(getIntent, "Select Media")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

            this.startIntent(chooserIntent) { code, result ->
                if (code == Activity.RESULT_OK) {
                    result?.clipData?.let { clipData ->
                        em.onSuccess((0 until clipData.itemCount).map { index -> clipData.getItemAt(index).uri })
                    } ?: result?.data?.let { em.onSuccess(listOf(it)) }
                } else if(code == Activity.RESULT_CANCELED) {
                    em.onComplete()
                } else {
                    em.onError(Exception("Got result code $code"))
                }
            }
        } else {
            em.onError(SecurityException("User has rejected permission to read external storage"))
        }
    }
}

/**
 * Starts a new activity to get an image or video from the gallery.
 * Handles permissions by itself.
 */
fun ActivityAccess.requestMediaGallery(): Maybe<Uri> = Maybe.create { em ->
    requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE) {
        if (it) {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "video/*,image/*"

            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "video/*,image/*"

            val chooserIntent = Intent.createChooser(getIntent, "Select Media")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

            this.startIntent(chooserIntent) { code, result ->
                val uri = result?.data
                if (code == Activity.RESULT_OK && uri != null) {
                    em.onSuccess(uri)
                } else if(code == Activity.RESULT_CANCELED) {
                    em.onComplete()
                } else {
                    em.onError(Exception("Got result code $code"))
                }
            }
        } else {
            em.onError(SecurityException("User has rejected permission to read external storage"))
        }
    }
}

/**
 * Starts a new activity to get a file.
 * Handles permissions by itself.
 */
fun ActivityAccess.requestFile(): Maybe<Uri> = Maybe.create { em ->
    requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE) {
        if (it) {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "*/*"

            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "*/*"

            val chooserIntent = Intent.createChooser(getIntent, "Select File")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

            this.startIntent(chooserIntent) { code, result ->
                val uri = result?.data
                if (code == Activity.RESULT_OK && uri != null) {
                    em.onSuccess(uri)
                } else if(code == Activity.RESULT_CANCELED) {
                    em.onComplete()
                } else {
                    em.onError(Exception("Got result code $code"))
                }
            }
        } else {
            em.onError(SecurityException("User has rejected permission to read external storage"))
        }
    }
}

/**
 * Starts a new activity to get files.
 * Handles permissions by itself.
 */
fun ActivityAccess.requestFiles(): Maybe<List<Uri>> = Maybe.create { em ->
    requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE) {
        if (it) {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            getIntent.type = "*/*"

            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "*/*"

            val chooserIntent = Intent.createChooser(getIntent, "Select Files")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

            this.startIntent(chooserIntent) { code, result ->
                if (code == Activity.RESULT_OK) {
                    result?.clipData?.let { clipData ->
                        em.onSuccess((0 until clipData.itemCount).map { index -> clipData.getItemAt(index).uri })
                    } ?: result?.data?.let { em.onSuccess(listOf(it)) }
                } else if(code == Activity.RESULT_CANCELED) {
                    em.onComplete()
                } else {
                    em.onError(Exception("Got result code $code"))
                }
            }
        } else {
            em.onError(SecurityException("User has rejected permission to read external storage"))
        }
    }
}

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

