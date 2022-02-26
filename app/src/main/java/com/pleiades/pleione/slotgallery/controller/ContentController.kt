package com.pleiades.pleione.slotgallery.controller

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaScannerConnection
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import com.pleiades.pleione.slotgallery.Config
import com.pleiades.pleione.slotgallery.Config.Companion.COUNT_DEFAULT_DIRECTORY
import com.pleiades.pleione.slotgallery.Config.Companion.KEY_CONTENT_SORT_ORDER
import com.pleiades.pleione.slotgallery.Config.Companion.KEY_DIRECTORY_SORT_ORDER
import com.pleiades.pleione.slotgallery.Config.Companion.MIME_TYPE_IMAGE
import com.pleiades.pleione.slotgallery.Config.Companion.MIME_TYPE_VIDEO
import com.pleiades.pleione.slotgallery.Config.Companion.SORT_POSITION_BY_NAME
import com.pleiades.pleione.slotgallery.Config.Companion.SORT_POSITION_BY_NEWEST
import com.pleiades.pleione.slotgallery.Config.Companion.SORT_POSITION_BY_OLDEST
import com.pleiades.pleione.slotgallery.R
import com.pleiades.pleione.slotgallery.info.Directory
import com.pleiades.pleione.slotgallery.info.Slot
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.InputStream
import java.text.CharacterIterator
import java.text.StringCharacterIterator
import java.util.*

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

    fun copyContents(fromDirectoryPosition: Int, toDirectoryPosition: Int, contentPositionSet: Collection<Int>): Int {
        if (fromDirectoryPosition == toDirectoryPosition)
            return -1

        // initialize to directory document file
        var toDirectory = directoryArrayList[toDirectoryPosition]
        val toDirectoryPath = toDirectory.directoryPath
        val toDirectoryRootUri = Uri.parse(toDirectoryPath.rootUriString)
        val toDirectoryRootLastPath = toDirectoryRootUri.lastPathSegment!!
        var toDirectoryDocumentFile = DocumentFile.fromTreeUri(context, toDirectoryRootUri)!!
        if (toDirectoryPath.lastPath != toDirectoryRootLastPath) {
            val toDirectoryRelativePathList = toDirectoryPath.lastPath.substringAfter("$toDirectoryRootLastPath/").split("/")
            for (toDirectoryRelativePath in toDirectoryRelativePathList) {
                if (toDirectoryDocumentFile.findFile(toDirectoryRelativePath) == null)
                    toDirectoryDocumentFile.createDirectory(toDirectoryRelativePath)
                toDirectoryDocumentFile = toDirectoryDocumentFile.findFile(toDirectoryRelativePath)!!
            }
        }

        // copy contents
        val fromDirectory = directoryArrayList[fromDirectoryPosition]
        for (contentPosition in contentPositionSet) {
            // initialize content
            val content = fromDirectory.contentArrayList[contentPosition]

            // case already exists
            if (toDirectoryDocumentFile.findFile(content.name) != null) {
                // show toast
                Toast.makeText(context, R.string.message_error_exist, Toast.LENGTH_SHORT).show()

                // skip this content
                continue
            }

            // initialize content document file
            val mimeType = if (content.isVideo) MIME_TYPE_VIDEO else MIME_TYPE_IMAGE
            toDirectoryDocumentFile.createFile(mimeType, content.name)
            val contentDocumentFile = toDirectoryDocumentFile.findFile(content.name)!!

            try {
                // save content
                val inputStream: InputStream = context.contentResolver.openInputStream(content.uri)!!
                val bufferedInputStream = BufferedInputStream(inputStream)
                val outputStream = context.contentResolver.openOutputStream(contentDocumentFile.uri)!!
                val bufferedOutputStream = BufferedOutputStream(outputStream)

                var read: Int
                while (bufferedInputStream.read().also { read = it } != -1) {
                    bufferedOutputStream.write(read)
                }

                bufferedInputStream.close()
                bufferedOutputStream.flush()
                bufferedOutputStream.close()
                inputStream.close()
                outputStream.flush()
                outputStream.close()

                // scan media
                MediaScannerConnection.scanFile(context, arrayOf(contentDocumentFile.uri.toString()), arrayOf(mimeType), null)
            } catch (e: Exception) {
            }
        }

        // refresh to directory
        toDirectory = Directory(toDirectoryPath)
        val toDirectoryRelativePath = toDirectoryPath.lastPath.substringAfter(":") + "/"

        val imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val imageProjection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.RELATIVE_PATH
        )
        val imageSelection = "${MediaStore.Images.Media.RELATIVE_PATH} = ?"
        val imageSelectionArgs = arrayOf(toDirectoryRelativePath)
        val imageCursor = context.contentResolver.query(imageUri, imageProjection, imageSelection, imageSelectionArgs, null)!!
        while (imageCursor.moveToNext()) {
            val id = imageCursor.getString(0)
            val name = imageCursor.getString(1)
            val date = imageCursor.getString(2).toLong()
            val relativePath = imageCursor.getString(3)
            val uri = Uri.withAppendedPath(imageUri, id.toString())

            toDirectory.date = date.coerceAtLeast(toDirectory.date)
            toDirectory.contentArrayList.add(Directory.Content(false, id, name, "-", 0, 0, date, relativePath, uri, 0L))
        }
        imageCursor.close()

        val videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val videoProjection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATE_MODIFIED,
            MediaStore.Video.Media.RELATIVE_PATH
        )
        val videoSelection = "${MediaStore.Video.Media.RELATIVE_PATH} = ?"
        val videoSelectionArgs = arrayOf(toDirectoryRelativePath)
        val videoCursor = context.contentResolver.query(videoUri, videoProjection, videoSelection, videoSelectionArgs, null)!!
        while (videoCursor.moveToNext()) {
            val id = videoCursor.getString(0)
            val name = videoCursor.getString(1)
            val date = videoCursor.getString(2).toLong()
            val relativePath = videoCursor.getString(3)
            val uri = Uri.withAppendedPath(videoUri, id.toString())

            toDirectory.date = date.coerceAtLeast(toDirectory.date)
            toDirectory.contentArrayList.add(Directory.Content(true, id, name, "-", 0, 0, date, relativePath, uri, 0L))
        }
        videoCursor.close()

        // sort content array list
        directoryArrayList[toDirectoryPosition] = toDirectory
        sortContentArrayList(toDirectoryPosition)

        // sort directory array list
        sortDirectoryArrayList()

        return directoryArrayList.indexOf(fromDirectory)
    }
}