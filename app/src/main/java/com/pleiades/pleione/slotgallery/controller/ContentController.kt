package com.pleiades.pleione.slotgallery.controller

import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.provider.MediaStore
import com.pleiades.pleione.slotgallery.Config
import com.pleiades.pleione.slotgallery.Config.Companion.COUNT_DEFAULT_DIRECTORY
import com.pleiades.pleione.slotgallery.Config.Companion.KEY_DIRECTORY_SORT_ORDER
import com.pleiades.pleione.slotgallery.Config.Companion.SORT_POSITION_BY_NAME
import com.pleiades.pleione.slotgallery.Config.Companion.SORT_POSITION_BY_NEWEST
import com.pleiades.pleione.slotgallery.Config.Companion.SORT_POSITION_BY_OLDEST
import com.pleiades.pleione.slotgallery.info.Directory
import java.util.*
import kotlin.collections.HashSet

class ContentController(private val context: Context) {
    companion object {
        val directoryLinkedList: LinkedList<Directory> = LinkedList()
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(Config.PREFS, Context.MODE_PRIVATE)

    fun initializeContents() {
        val selectedSlot = SlotController(context).getSelectedSlot()

        for (i in selectedSlot!!.directoryPathLinkedList.indices) {
            val directoryPath = selectedSlot.directoryPathLinkedList[i]
            if (i < COUNT_DEFAULT_DIRECTORY) {
                if (selectedSlot.isVisible[i]) {
                    // add default directory
                    addDirectory(false, directoryPath)
                }
            } else {
                // add user directory
                addDirectory(true, directoryPath)
            }
        }

        // sort directory linked list
        sortDirectoryLinkedList()
    }

    private fun addDirectory(allowSubDirectory: Boolean, directoryPath: String) {
        val directory = Directory(directoryPath)
        val directoryRelativePath = directoryPath.substringAfter(":") + "/"
        val subDirectoryPathHashSet: HashSet<String> = HashSet()

        // case image
        // initialize image parameters
        val imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val imageProjection = arrayOf(
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.RELATIVE_PATH,
        )
        val imageSelection = if (allowSubDirectory) "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?" else "${MediaStore.Images.Media.RELATIVE_PATH} = ?"
        val imageSelectionArgs = if (allowSubDirectory) arrayOf("$directoryRelativePath%") else arrayOf(directoryRelativePath)

        // initialize image cursor
        val imageCursor = context.contentResolver.query(imageUri, imageProjection, imageSelection, imageSelectionArgs, null)!!
        while (imageCursor.moveToNext()) {
            val bucketId = imageCursor.getString(0)
            val name = imageCursor.getString(1)
            val size = imageCursor.getString(2)
            val width = imageCursor.getString(3)
            val height = imageCursor.getString(4)
            val date = imageCursor.getString(5).toLong()
            val relativePath = imageCursor.getString(6)

            // update directory date
            directory.date = date.coerceAtLeast(directory.date)

            // case allow sub directory
            if (allowSubDirectory) {
                if (relativePath == directoryRelativePath) {
                    // add image
                    directory.contentLinkedList.add(Directory.Content(false, bucketId, name, size, width, height, date))
                } else {
                    // case sub directory
                    subDirectoryPathHashSet.add(directoryPath.substringBefore(":") + ":" + relativePath.substringBeforeLast("/"))
                }
            } else {
                // add image
                directory.contentLinkedList.add(Directory.Content(false, bucketId, name, size, width, height, date))
            }
        }

        // close image cursor
        imageCursor.close()

        // case video
        // initialize video parameters
        val videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val videoProjection = arrayOf(
            MediaStore.Video.Media.BUCKET_ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.HEIGHT,
            MediaStore.Video.Media.DATE_MODIFIED,
            MediaStore.Video.Media.RELATIVE_PATH,
        )
        val videoSelection = if (allowSubDirectory) "${MediaStore.Video.Media.RELATIVE_PATH} LIKE ?" else "${MediaStore.Video.Media.RELATIVE_PATH} = ?"
        val videoSelectionArgs = if (allowSubDirectory) arrayOf("$directoryRelativePath%") else arrayOf(directoryRelativePath)

        // initialize video cursor
        val videoCursor = context.contentResolver.query(videoUri, videoProjection, videoSelection, videoSelectionArgs, null)!!
        while (videoCursor.moveToNext()) {
            val bucketId = videoCursor.getString(0)
            val name = videoCursor.getString(1)
            val size = videoCursor.getString(2)
            val width = videoCursor.getString(3)
            val height = videoCursor.getString(4)
            val date = videoCursor.getString(5).toLong()
            val relativePath = videoCursor.getString(6)

            // update directory date
            directory.date = date.coerceAtLeast(directory.date)

            // case allow sub directory
            if (allowSubDirectory) {
                if (relativePath == directoryRelativePath) {
                    // add image
                    directory.contentLinkedList.add(Directory.Content(true, bucketId, name, size, width, height, date))
                } else {
                    // case sub directory
                    subDirectoryPathHashSet.add(directoryPath.substringBefore(":") + ":" + relativePath.substringBeforeLast("/"))
                }
            } else {
                // add image
                directory.contentLinkedList.add(Directory.Content(true, bucketId, name, size, width, height, date))
            }
        }

        // close video cursor
        videoCursor.close()

        // add directory
        if (directory.contentLinkedList.size > 0)
            directoryLinkedList.add(directory)

        // add sub directory
        for (subDirectoryPath in subDirectoryPathHashSet) {
            addDirectory(false, subDirectoryPath)
        }
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

    fun sortDirectoryLinkedList() {
        when (prefs.getInt(KEY_DIRECTORY_SORT_ORDER, 0)) {
            SORT_POSITION_BY_NAME -> directoryLinkedList.sortBy { it.name }
            SORT_POSITION_BY_NEWEST -> directoryLinkedList.sortBy { it.date }
            SORT_POSITION_BY_OLDEST -> directoryLinkedList.sortByDescending { it.date }
        }
    }
}