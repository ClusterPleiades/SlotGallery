package com.pleiades.pleione.slotgallery.controller

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.provider.MediaStore
import com.pleiades.pleione.slotgallery.Config
import com.pleiades.pleione.slotgallery.Config.Companion.COUNT_DEFAULT_DIRECTORY
import com.pleiades.pleione.slotgallery.Config.Companion.KEY_CONTENT_SORT_ORDER
import com.pleiades.pleione.slotgallery.Config.Companion.KEY_DIRECTORY_SORT_ORDER
import com.pleiades.pleione.slotgallery.Config.Companion.SORT_POSITION_BY_NAME
import com.pleiades.pleione.slotgallery.Config.Companion.SORT_POSITION_BY_NEWEST
import com.pleiades.pleione.slotgallery.Config.Companion.SORT_POSITION_BY_OLDEST
import com.pleiades.pleione.slotgallery.info.Directory
import com.pleiades.pleione.slotgallery.info.Slot
import java.text.CharacterIterator
import java.text.StringCharacterIterator

class ContentController(private val context: Context) {
    companion object {
        val directoryArrayList: ArrayList<Directory> = ArrayList()
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(Config.PREFS, Context.MODE_PRIVATE)

    fun initializeContents() {
        // clear directory array list
        directoryArrayList.clear()

        // initialize selected slot
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

        // sort directory array list
        sortDirectoryArrayList()

        // sort content array list
        sortContentArrayList()
    }

    private fun addDirectory(allowSubDirectory: Boolean, directoryPath: Slot.DirectoryPath) {
        val directory = Directory(directoryPath)
        val directoryRelativePath = directoryPath.lastPath.substringAfter(":") + "/"
        val subDirectoryLastPathHashSet: HashSet<String> = HashSet()

        // case image
        // initialize image parameters
        val imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val imageProjection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.RELATIVE_PATH
        )
        val imageSelection = if (allowSubDirectory) "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?" else "${MediaStore.Images.Media.RELATIVE_PATH} = ?"
        val imageSelectionArgs = if (allowSubDirectory) arrayOf("$directoryRelativePath%") else arrayOf(directoryRelativePath)

        // initialize image cursor
        val imageCursor = context.contentResolver.query(imageUri, imageProjection, imageSelection, imageSelectionArgs, null)!!
        while (imageCursor.moveToNext()) {
            val id = imageCursor.getString(0)
            val name = imageCursor.getString(1)
            val size = getByteCountSI(imageCursor.getString(2))
            val width = imageCursor.getString(3).toInt()
            val height = imageCursor.getString(4).toInt()
            val date = imageCursor.getString(5).toLong()
            val relativePath = imageCursor.getString(6)
            val uri = Uri.withAppendedPath(imageUri, id.toString())

            // update directory date
            directory.date = date.coerceAtLeast(directory.date)

            // case allow sub directory
            if (allowSubDirectory) {
                if (relativePath == directoryRelativePath) {
                    directory.contentArrayList.add(Directory.Content(false, id, name, size, width, height, date, relativePath, uri, 0L))
                } else {
                    // case sub directory
                    subDirectoryLastPathHashSet.add(directoryPath.lastPath.substringBefore(":") + ":" + relativePath.substringBeforeLast("/"))
                }
            } else {
                // add image
                directory.contentArrayList.add(Directory.Content(false, id, name, size, width, height, date, relativePath, uri, 0L))
            }
        }

        // close image cursor
        imageCursor.close()

        // case video
        // initialize video parameters
        val videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val videoProjection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.HEIGHT,
            MediaStore.Video.Media.DATE_MODIFIED,
            MediaStore.Video.Media.RELATIVE_PATH,
            MediaStore.Video.Media.DURATION
        )
        val videoSelection = if (allowSubDirectory) "${MediaStore.Video.Media.RELATIVE_PATH} LIKE ?" else "${MediaStore.Video.Media.RELATIVE_PATH} = ?"
        val videoSelectionArgs = if (allowSubDirectory) arrayOf("$directoryRelativePath%") else arrayOf(directoryRelativePath)

        // initialize video cursor
        val videoCursor = context.contentResolver.query(videoUri, videoProjection, videoSelection, videoSelectionArgs, null)!!
        while (videoCursor.moveToNext()) {
            val id = videoCursor.getString(0)
            val name = videoCursor.getString(1)
            val size = getByteCountSI(videoCursor.getString(2))
            val width = videoCursor.getString(3).toInt()
            val height = videoCursor.getString(4).toInt()
            val date = videoCursor.getString(5).toLong()
            val relativePath = videoCursor.getString(6)
            val uri = Uri.withAppendedPath(videoUri, id.toString())
            val duration = videoCursor.getString(7).toLong()

            // update directory date
            directory.date = date.coerceAtLeast(directory.date)

            // case allow sub directory
            if (allowSubDirectory) {
                if (relativePath == directoryRelativePath) {
                    // add image
                    directory.contentArrayList.add(Directory.Content(true, id, name, size, width, height, date, relativePath, uri, duration))
                } else {
                    // case sub directory
                    subDirectoryLastPathHashSet.add(directoryPath.lastPath.substringBefore(":") + ":" + relativePath.substringBeforeLast("/"))
                }
            } else {
                // add image
                directory.contentArrayList.add(Directory.Content(true, id, name, size, width, height, date, relativePath, uri, duration))
            }
        }

        // close video cursor
        videoCursor.close()

        // case contents exist
        if (directory.contentArrayList.size > 0) {
            // add directory
            directoryArrayList.add(directory)
        }

        // add sub directory
        for (subDirectoryLastPath in subDirectoryLastPathHashSet) {
            addDirectory(false, Slot.DirectoryPath(directoryPath.rootUriString, subDirectoryLastPath))
        }
    }

    private fun getByteCountSI(size: String): String {
        // initialize bytes
        var bytes = size.toLong()
        if (-1000 < bytes && bytes < 1000) return "$bytes B"

        val characterIterator: CharacterIterator = StringCharacterIterator("kMGTPE")
        while (bytes <= -999950 || bytes >= 999950) {
            bytes /= 1000
            characterIterator.next()
        }
        return String.format("%.1f %cB", bytes / 1000.0, characterIterator.current())
    }

    fun sortDirectoryArrayList() {
        when (prefs.getInt(KEY_DIRECTORY_SORT_ORDER, 0)) {
            SORT_POSITION_BY_NAME -> directoryArrayList.sortBy { it.name }
            SORT_POSITION_BY_NEWEST -> directoryArrayList.sortByDescending { it.date }
            SORT_POSITION_BY_OLDEST -> directoryArrayList.sortBy { it.date }
        }
    }

    fun sortContentArrayList() {
        when (prefs.getInt(KEY_CONTENT_SORT_ORDER, 0)) {
            SORT_POSITION_BY_NAME -> for (directory in directoryArrayList) directory.contentArrayList.sortBy { it.name }
            SORT_POSITION_BY_NEWEST -> for (directory in directoryArrayList) directory.contentArrayList.sortByDescending { it.date }
            SORT_POSITION_BY_OLDEST -> for (directory in directoryArrayList) directory.contentArrayList.sortBy { it.date }
        }
    }

    fun sortContentArrayList(directoryPosition: Int) {
        when (prefs.getInt(KEY_CONTENT_SORT_ORDER, 0)) {
            SORT_POSITION_BY_NAME -> directoryArrayList[directoryPosition].contentArrayList.sortBy { it.name }
            SORT_POSITION_BY_NEWEST -> directoryArrayList[directoryPosition].contentArrayList.sortByDescending { it.date }
            SORT_POSITION_BY_OLDEST -> directoryArrayList[directoryPosition].contentArrayList.sortBy { it.date }
        }
    }

    // TODO FIX
//    fun insertContent(content: Directory.Content, targetDirectoryPosition: Int) {
//        val relativePath = directoryArrayList[targetDirectoryPosition].contentArrayList[0].relativePath
//
//        // case video
//        if (content.isVideo) {
//            // TODO
//        }
//        // case image
//        else {
//            val externalContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//
//            val projection = arrayOf(MediaStore.Images.Media.DATA)
//            val selection = "${MediaStore.Images.Media._ID} = ?"
//            val selectionArgs = arrayOf(content.id)
//
//            val cursor = context.contentResolver.query(externalContentUri, projection, selection, selectionArgs, null)!!
//            cursor.moveToNext()
//            val contentData = cursor.getString(0)
//            cursor.close()
//
//            val contentValues = ContentValues().apply {
//                put(MediaStore.Images.Media.DISPLAY_NAME, content.name)
////                put(MediaStore.Images.Media.RELATIVE_PATH, relativePath)
//                put(MediaStore.Images.Media.MIME_TYPE, "image/" + content.name.substringAfterLast("."))
//                put(MediaStore.Images.Media.DATA, contentData)
//                put(MediaStore.Images.Media.IS_PENDING, 1)
//            }
//            try {
//                val targetContentUri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)!!
//
//                contentValues.clear()
//                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
//                context.contentResolver.update(targetContentUri, contentValues, null, null)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
}