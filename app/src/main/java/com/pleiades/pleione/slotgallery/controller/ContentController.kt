package com.pleiades.pleione.slotgallery.controller

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import com.pleiades.pleione.slotgallery.Config.Companion.COUNT_DEFAULT_DIRECTORY
import com.pleiades.pleione.slotgallery.info.DirectoryInfo
import java.util.*

class ContentController(private val context: Context) {
    companion object {
        var directoryInfoLinkedList: LinkedList<DirectoryInfo> = LinkedList()
    }

    fun initializeContents() {
        val selectedSlotInfo = SlotController(context).getSelectedSlotInfo()

        for (i in selectedSlotInfo!!.directoryPathInfoLinkedList.indices) {
            val directoryPathInfo = selectedSlotInfo.directoryPathInfoLinkedList[i]

            // case default directory info
            if (i < COUNT_DEFAULT_DIRECTORY) {
                if (directoryPathInfo.isVisible)
                    addDefaultDirectoryInfo(directoryPathInfo.lastPathSegment)
            }
            // case user directory info
            else {
                addUserDirectoryInfo()
            }
        }
    }

    private fun addDefaultDirectoryInfo(relativePath: String) {
        val directoryInfo = DirectoryInfo(true, relativePath.substringAfterLast("/"))

        // case image
        val imageCursor = context.contentResolver.query(
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
            arrayOf("$relativePath%"),
            null
        )!!
        while (imageCursor.moveToNext()) {
            val name = imageCursor.getString(0)
            val path = imageCursor.getString(1)
            val size = imageCursor.getString(2)
            val width = imageCursor.getString(3)
            val height = imageCursor.getString(4)
            val date = imageCursor.getString(5)

            directoryInfo.contentInfoLinkedList.add(DirectoryInfo.ContentInfo(false, name, path, size, width, height, date))
        }
        imageCursor.close()

        // case video
        val videoCursor = context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.RELATIVE_PATH,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.WIDTH,
                MediaStore.Video.Media.HEIGHT,
                MediaStore.Video.Media.DATE_MODIFIED
            ),
            "${MediaStore.Video.Media.RELATIVE_PATH} LIKE ?",
            arrayOf("$relativePath%"),
            null
        )!!
        while (videoCursor.moveToNext()) {
            val name = videoCursor.getString(0)
            val path = videoCursor.getString(1)
            val size = videoCursor.getString(2)
            val width = videoCursor.getString(3)
            val height = videoCursor.getString(4)
            val date = videoCursor.getString(5)

            directoryInfo.contentInfoLinkedList.add(DirectoryInfo.ContentInfo(true, name, path, size, width, height, date))
        }
        videoCursor.close()

        // add directory info
        if (directoryInfo.contentInfoLinkedList.size > 0)
            directoryInfoLinkedList.add(directoryInfo)
    }

    private fun addUserDirectoryInfo() {
        // TODO
    }

    fun getContentData(isDefaultDirectory: Boolean, contentInfo: DirectoryInfo.ContentInfo): String {
        if (isDefaultDirectory) {
            val cursor: Cursor
            if (contentInfo.isVideo) {
                cursor = context.contentResolver.query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(MediaStore.Video.Media.DATA),
                    "${MediaStore.Video.Media.RELATIVE_PATH} = ? and ${MediaStore.Video.Media.DISPLAY_NAME} = ?",
                    arrayOf(contentInfo.path, contentInfo.name),
                    null
                )!!
            } else {
                cursor = context.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(MediaStore.Images.Media.DATA),
                    "${MediaStore.Images.Media.RELATIVE_PATH} = ? and ${MediaStore.Images.Media.DISPLAY_NAME} = ?",
                    arrayOf(contentInfo.path, contentInfo.name),
                    null
                )!!
            }
            cursor.moveToNext()
            val data = cursor.getString(0)
            cursor.close()
            return data
        } else {
            // TODO
            return ""
        }
    }

//    fun getDefaultDirectoryContentData(isVideo: Boolean) {
//        val imageCursor = context.contentResolver.query(
//            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//            arrayOf(
//                MediaStore.Images.Media.DISPLAY_NAME,
//                MediaStore.Images.Media.RELATIVE_PATH,
//                MediaStore.Images.Media.SIZE,
//                MediaStore.Images.Media.WIDTH,
//                MediaStore.Images.Media.HEIGHT,
//                MediaStore.Images.Media.DATE_MODIFIED
//            ),
//            "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?",
//            arrayOf("$relativePath%"),
//            null
//        )!!
//        while (imageCursor.moveToNext()) {
//            val name = imageCursor.getString(0)
//            val path = imageCursor.getString(1)
//            val size = imageCursor.getString(2)
//            val width = imageCursor.getString(3)
//            val height = imageCursor.getString(4)
//            val date = imageCursor.getString(5)
//
//            directoryInfo.contentInfoLinkedList.add(DirectoryInfo.ContentInfo(false, name, path, size, width, height, date))
//        }
//        imageCursor.close()
//
//        // case video
//        val videoCursor = context.contentResolver.query(
//            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
//            arrayOf(
//                MediaStore.Video.Media.DISPLAY_NAME,
//                MediaStore.Video.Media.RELATIVE_PATH,
//                MediaStore.Video.Media.SIZE,
//                MediaStore.Video.Media.WIDTH,
//                MediaStore.Video.Media.HEIGHT,
//                MediaStore.Video.Media.DATE_MODIFIED
//            ),
//            "${MediaStore.Video.Media.RELATIVE_PATH} LIKE ?",
//            arrayOf("$relativePath%"),
//            null
//        )!!
//        while (videoCursor.moveToNext()) {
//            val name = videoCursor.getString(0)
//            val path = videoCursor.getString(1)
//            val size = videoCursor.getString(2)
//            val width = videoCursor.getString(3)
//            val height = videoCursor.getString(4)
//            val date = videoCursor.getString(5)
//
//            directoryInfo.contentInfoLinkedList.add(DirectoryInfo.ContentInfo(true, name, path, size, width, height, date))
//        }
//        videoCursor.close()
//    }

}