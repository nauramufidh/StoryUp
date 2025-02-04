package com.example.storyup

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.database.Cursor as AndroidCursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

fun getImageUri(context: Context): Uri {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        getUriForAndroidQAndAbove(context)
    } else {
        getImageUriForPreQ(context)
    }
}

private fun getUriForAndroidQAndAbove(context: Context): Uri {
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpg")
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/")
    }
    return context.contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    ) ?: throw IllegalStateException("Failed to create MediaStore entry")
}

private fun getImageUriForPreQ(context: Context): Uri {
    val filesDir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MyCamera")
    if (!filesDir.exists()) filesDir.mkdirs()
    val imageFile = File(filesDir, "$timeStamp.jpg")

    return FileProvider.getUriForFile(
        context,
        "com.example.storyup.fileprovider",
        imageFile
    )
}

fun getFileFromUri(context: Context, uri: Uri): File {
    val contentResolver: ContentResolver = context.contentResolver
    val fileName = getFileName(context, uri) ?: "temp_file.jpg"
    val tempFile = File(context.cacheDir, fileName)
    tempFile.outputStream().use { outputStream ->
        contentResolver.openInputStream(uri)?.copyTo(outputStream)
    }
    return tempFile
}

private fun getFileName(context: Context, uri: Uri): String? {
    var name: String? = null
    val cursor: AndroidCursor? = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0) {
                name = cursor.getString(nameIndex)
            }
        }
    }
    return name
}

fun compressImage(file: File, maxFileSize: Int = 1 * 1024 * 1024): File {
    val bitmap = BitmapFactory.decodeFile(file.path)
    var compressQuality = 100
    var streamLength: Int
    do {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, stream)
        streamLength = stream.toByteArray().size
        compressQuality -= 5
    } while (streamLength > maxFileSize)

    val compressedFile = File(file.parent, "compressed_${file.name}")
    compressedFile.outputStream().use {
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, it)
    }
    return compressedFile
}



