package com.pleiades.pleione.slotgallery.controller

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import com.pleiades.pleione.slotgallery.Config.Companion.COUNT_DEFAULT_DIRECTORY
import com.pleiades.pleione.slotgallery.info.Directory
import java.util.*

class ContentController(private val context: Context) {
    companion object {
        var directoryLinkedList: LinkedList<Directory> = LinkedList()
    }

    fun initializeContents() {
        val selectedSlot = SlotController(context).getSelectedSlot()

        for (i in selectedSlot!!.directoryPathLinkedList.indices) {
            val directoryPath = selectedSlot.directoryPathLinkedList[i]
            if (i < COUNT_DEFAULT_DIRECTORY) {
                if (selectedSlot.isVisible[i])
                    addDirectory(directoryPath)
            } else
                addDirectory(directoryPath)
        }
    }

    private fun addDirectory(directoryPath: String) {
        val directory = Directory(directoryPath)
        val relativePath = directoryPath.substringAfter(":") + "/"

        // case image
        val imageCursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
                MediaStore.Images.Media.DATE_MODIFIED
            ),
            "${MediaStore.Images.Media.RELATIVE_PATH} = ?",
            arrayOf(relativePath),
            null
        )!!
        while (imageCursor.moveToNext()) {
            val bucketId = imageCursor.getString(0)
            val name = imageCursor.getString(1)
            val size = imageCursor.getString(2)
            val width = imageCursor.getString(3)
            val height = imageCursor.getString(4)
            val date = imageCursor.getString(5)

            directory.contentLinkedList.add(Directory.Content(false, bucketId, name, size, width, height, date))
        }
        imageCursor.close()

        // case video
        val videoCursor = context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                MediaStore.Video.Media.BUCKET_ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.WIDTH,
                MediaStore.Video.Media.HEIGHT,
                MediaStore.Video.Media.DATE_MODIFIED
            ),
            "${MediaStore.Video.Media.RELATIVE_PATH} = ?",
            arrayOf(relativePath),
            null
        )!!
        while (videoCursor.moveToNext()) {
            val bucketId = videoCursor.getString(0)
            val name = videoCursor.getString(1)
            val size = videoCursor.getString(2)
            val width = videoCursor.getString(3)
            val height = videoCursor.getString(4)
            val date = videoCursor.getString(5)

            directory.contentLinkedList.add(Directory.Content(true, bucketId, name, size, width, height, date))
        }
        videoCursor.close()

        // add directory
        if (directory.contentLinkedList.size > 0)
            directoryLinkedList.add(directory)
    }

    fun getContentData(content: Directory.Content): String {
        val cursor: Cursor
        if (content.isVideo) {
            cursor = context.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Video.Media.DATA),
                "${MediaStore.Video.Media.BUCKET_ID} = ? and ${MediaStore.Video.Media.DISPLAY_NAME} = ?",
                arrayOf(content.bucketId, content.name),
                null
            )!!
        } else {
            cursor = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Images.Media.DATA),
                "${MediaStore.Images.Media.BUCKET_ID} = ? and ${MediaStore.Images.Media.DISPLAY_NAME} = ?",
                arrayOf(content.bucketId, content.name),
                null
            )!!
        }
        cursor.moveToNext()
        val data = cursor.getString(0)
        cursor.close()
        return data
    }
}