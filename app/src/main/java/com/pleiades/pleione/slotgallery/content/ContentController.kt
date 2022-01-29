package com.pleiades.pleione.slotgallery.content

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import com.pleiades.pleione.slotgallery.content.info.ContentInfo
import java.io.File

class ContentController(private val context: Context) {
    companion object {
        var directoryInfoHashSet: HashSet<ContentInfo> = HashSet()
        var contentInfoHashSet: HashSet<ContentInfo> = HashSet()
    }

    /*
    *                 val cursor = requireContext().contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(
                        MediaStore.Images.Media.DISPLAY_NAME,
                        MediaStore.Images.Media.RELATIVE_PATH,
                        MediaStore.Images.Media.SIZE,
                        MediaStore.Images.Media.WIDTH,
                        MediaStore.Images.Media.HEIGHT,
                        MediaStore.Images.Media.DATE_MODIFIED
                    ),
                    "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?",
                    arrayOf("Download%"),
                    null
                )!!
                cursor.moveToFirst()
                while (cursor.moveToNext()) {

                    var index: Int = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
                    val name = if (index >= 0) cursor.getString(index) else continue

                    index = cursor.getColumnIndex(MediaStore.Images.Media.SIZE)
                    val size = if (index >= 0) cursor.getString(index) else continue

                    index = cursor.getColumnIndex(MediaStore.Images.Media.WIDTH)
                    val width = if (index >= 0) cursor.getString(index) else continue

                    index = cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT)
                    val height = if (index >= 0) cursor.getString(index) else continue

                    index = cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)
                    val date = if (index >= 0) cursor.getString(index) else continue
                }
                cursor.close()
    * */

    fun initialize() {
        putContentInfo(
            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf(
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.SIZE,
                    MediaStore.Images.Media.WIDTH,
                    MediaStore.Images.Media.HEIGHT,
                    MediaStore.Images.Media.DATE_MODIFIED
                ),
                null,
                null,
                null
            )!!, false
        )
    }

    fun initializeContentInfoHashSet() {
        // clear content info hash set
        contentInfoHashSet.clear()

        // load images
        putContentInfo(
            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf(
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.SIZE,
                    MediaStore.Images.Media.WIDTH,
                    MediaStore.Images.Media.HEIGHT,
                    MediaStore.Images.Media.DATE_MODIFIED
                ),
                null,
                null,
                null
            )!!, false
        )

        // load videos
        putContentInfo(
            context.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, arrayOf(
                    MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media.SIZE,
                    MediaStore.Video.Media.WIDTH,
                    MediaStore.Video.Media.HEIGHT,
                    MediaStore.Video.Media.DATE_MODIFIED
                ),
                null,
                null,
                null
            )!!, true
        )
    }

    private fun putContentInfo(cursor: Cursor, isVideo: Boolean) {
        cursor.moveToFirst()
        while (cursor.moveToNext()) {

            var index: Int = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
            val name = if (index >= 0) cursor.getString(index) else continue

            index = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            val path = if (index >= 0) cursor.getString(index) else continue

            index = cursor.getColumnIndex(MediaStore.Images.Media.SIZE)
            val size = if (index >= 0) cursor.getString(index) else continue

            index = cursor.getColumnIndex(MediaStore.Images.Media.WIDTH)
            val width = if (index >= 0) cursor.getString(index) else continue

            index = cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT)
            val height = if (index >= 0) cursor.getString(index) else continue

            index = cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)
            val date = if (index >= 0) cursor.getString(index) else continue

            if (File(path).canRead()) contentInfoHashSet.add(ContentInfo(isVideo, name, path, size, width, height, date))
        }
        cursor.close()
    }
}