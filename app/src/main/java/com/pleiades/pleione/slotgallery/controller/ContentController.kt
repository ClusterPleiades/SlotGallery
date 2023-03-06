package com.pleiades.pleione.slotgallery.controller

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaScannerConnection
import android.net.Uri
import android.provider.MediaStore
import androidx.documentfile.provider.DocumentFile
import com.pleiades.pleione.slotgallery.Config
import com.pleiades.pleione.slotgallery.Config.Companion.COUNT_DEFAULT_DIRECTORY
import com.pleiades.pleione.slotgallery.Config.Companion.KEY_CONTENT_SORT_ORDER
import com.pleiades.pleione.slotgallery.Config.Companion.KEY_DIRECTORY_SORT_ORDER
import com.pleiades.pleione.slotgallery.Config.Companion.MIME_TYPE_IMAGE
import com.pleiades.pleione.slotgallery.Config.Companion.MIME_TYPE_VIDEO
import com.pleiades.pleione.slotgallery.Config.Companion.PATH_SNAPSEED
import com.pleiades.pleione.slotgallery.Config.Companion.SORT_POSITION_BY_NAME
import com.pleiades.pleione.slotgallery.Config.Companion.SORT_POSITION_BY_NEWEST
import com.pleiades.pleione.slotgallery.Config.Companion.SORT_POSITION_BY_OLDEST
import com.pleiades.pleione.slotgallery.domain.model.Content
import com.pleiades.pleione.slotgallery.domain.model.Directory
import com.pleiades.pleione.slotgallery.domain.model.DirectoryOverview
import com.pleiades.pleione.slotgallery.presentation.dialog.ProgressDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.IOException
import java.io.InputStream
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

        for (i in selectedSlot!!.directoryOverviewLists.indices) {
            val directoryPath = selectedSlot.directoryOverviewLists[i]
            if (i < COUNT_DEFAULT_DIRECTORY) {
                if (selectedSlot.directoryOverviewLists[i].isVisible) {
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

    private fun addDirectory(allowSubDirectory: Boolean, directoryOverView: DirectoryOverview) {
        val directory = Directory(directoryOverView)
        val directoryRelativePath = directoryOverView.lastPath.substringAfter(":") + "/"
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
        val imageSelection =
            if (allowSubDirectory) "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?" else "${MediaStore.Images.Media.RELATIVE_PATH} = ?"
        val imageSelectionArgs = if (allowSubDirectory) arrayOf("$directoryRelativePath%") else arrayOf(directoryRelativePath)

        // initialize image cursor
        val imageCursor = context.contentResolver.query(imageUri, imageProjection, imageSelection, imageSelectionArgs, null)!!
        while (imageCursor.moveToNext()) {
            val id = imageCursor.getString(0)
            val name = imageCursor.getString(1)
            val size = if (imageCursor.getString(2) == null) "-" else getByteCountSI(imageCursor.getString(2))
            val width = if (imageCursor.getString(3) == null) 0 else imageCursor.getString(3).toInt()
            val height = if (imageCursor.getString(4) == null) 0 else imageCursor.getString(4).toInt()
            val date = imageCursor.getString(5).toLong()
            val relativePath = imageCursor.getString(6)
            val uri = Uri.withAppendedPath(imageUri, id.toString())

            // update directory date
            directory.date = date.coerceAtLeast(directory.date)

            // case allow sub directory
            if (allowSubDirectory) {
                if (relativePath == directoryRelativePath) {
                    directory.contentArrayList.add(Content(false, id, name, size, width, height, date, relativePath, uri, 0L))
                } else {
                    // case sub directory
                    subDirectoryLastPathHashSet.add(directoryOverView.lastPath.substringBefore(":") + ":" + relativePath.substringBeforeLast("/"))
                }
            } else {
                // add image
                directory.contentArrayList.add(Content(false, id, name, size, width, height, date, relativePath, uri, 0L))
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
        val videoSelection =
            if (allowSubDirectory) "${MediaStore.Video.Media.RELATIVE_PATH} LIKE ?" else "${MediaStore.Video.Media.RELATIVE_PATH} = ?"
        val videoSelectionArgs = if (allowSubDirectory) arrayOf("$directoryRelativePath%") else arrayOf(directoryRelativePath)

        // initialize video cursor
        val videoCursor = context.contentResolver.query(videoUri, videoProjection, videoSelection, videoSelectionArgs, null)!!
        while (videoCursor.moveToNext()) {
            val id = videoCursor.getString(0)
            val name = videoCursor.getString(1)
            val size = if (videoCursor.getString(2) == null) "-" else getByteCountSI(videoCursor.getString(2))
            val width = if (videoCursor.getString(3) == null) 0 else videoCursor.getString(3).toInt()
            val height = if (videoCursor.getString(4) == null) 0 else videoCursor.getString(4).toInt()
            val date = videoCursor.getString(5).toLong()
            val relativePath = videoCursor.getString(6)
            val uri = Uri.withAppendedPath(videoUri, id.toString())
            val duration = if (videoCursor.getString(7) == null) 0L else videoCursor.getString(7).toLong()

            // update directory date
            directory.date = date.coerceAtLeast(directory.date)

            // case allow sub directory
            if (allowSubDirectory) {
                if (relativePath == directoryRelativePath) {
                    // add image
                    directory.contentArrayList.add(Content(true, id, name, size, width, height, date, relativePath, uri, duration))
                } else {
                    // case sub directory
                    subDirectoryLastPathHashSet.add(directoryOverView.lastPath.substringBefore(":") + ":" + relativePath.substringBeforeLast("/"))
                }
            } else {
                // add image
                directory.contentArrayList.add(Content(true, id, name, size, width, height, date, relativePath, uri, duration))
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
            addDirectory(false, DirectoryOverview(directoryOverView.uri, subDirectoryLastPath))
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

    suspend fun copyDirectories(
        toDirectoryPosition: Int,
        fromDirectoryPositionHashSet: HashSet<Int>,
        progressDialogFragment: ProgressDialogFragment
    ) {
        // set progressbar attributes
        var max = 0
        for (fromDirectoryPosition in fromDirectoryPositionHashSet)
            max += directoryArrayList[fromDirectoryPosition].contentArrayList.size
        progressDialogFragment.progressBar.max = max

        // initialize to directory document file
        var toDirectory = directoryArrayList[toDirectoryPosition]
        val toDirectoryPath = toDirectory.directoryOverView
        val toDirectoryRootUri = Uri.parse(toDirectoryPath.uri)
        val toDirectoryRootLastPath = toDirectoryRootUri.lastPathSegment!!
        var toDirectoryDocumentFile = DocumentFile.fromTreeUri(context, toDirectoryRootUri)!!
        if (toDirectoryPath.lastPath != toDirectoryRootLastPath) {
            val toDirectoryRelativePathList = toDirectoryPath.lastPath.substringAfter("$toDirectoryRootLastPath/").split("/")
            for (toDirectoryRelativePath in toDirectoryRelativePathList)
                toDirectoryDocumentFile = toDirectoryDocumentFile.findFile(toDirectoryRelativePath) ?: toDirectoryDocumentFile.createDirectory(
                    toDirectoryRelativePath
                )!!
        }

        // initialize to directory file name hash set
        val toDirectoryFileNameHashSet: HashSet<String> = HashSet()
        for (documentFile in toDirectoryDocumentFile.listFiles())
            if (documentFile.name != null)
                toDirectoryFileNameHashSet.add(documentFile.name!!)

        // copy directories
        for (fromDirectoryPosition in fromDirectoryPositionHashSet) {
            // case is canceled
            if (progressDialogFragment.isCanceled)
                break

            // copy contents
            val fromDirectory = directoryArrayList[fromDirectoryPosition]
            for (content in fromDirectory.contentArrayList) {
                // case is canceled
                if (progressDialogFragment.isCanceled)
                    break

                // initialize to name
                val preName = content.name.substringBeforeLast(".")
                val postName = content.name.substringAfterLast(".")
                val isValidFormat = preName != postName
                var toName = content.name
                var index = 1
                while (toDirectoryFileNameHashSet.contains(toName)) {
                    toName =
                        if (isValidFormat)
                            "$preName ($index).$postName"
                        else
                            "${content.name} ($index)"
                    index++
                }
                toDirectoryFileNameHashSet.add(toName)

                // initialize content document file
                val mimeType = if (content.isVideo) MIME_TYPE_VIDEO else MIME_TYPE_IMAGE
                val contentDocumentFile = toDirectoryDocumentFile.createFile(mimeType, toName)!!

                withContext(Dispatchers.IO) {
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
                    } catch (ioException: IOException) {
                        ioException.printStackTrace()
                    } finally {
                        progressDialogFragment.progressBar.progress++
                    }
                }
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
            toDirectory.contentArrayList.add(Content(false, id, name, "-", 0, 0, date, relativePath, uri, 0L))
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
            toDirectory.contentArrayList.add(Content(true, id, name, "-", 0, 0, date, relativePath, uri, 0L))
        }
        videoCursor.close()

        // sort content array list
        directoryArrayList[toDirectoryPosition] = toDirectory
        sortContentArrayList(toDirectoryPosition)

        // set progress dialog fragment result
        progressDialogFragment.setFragmentResult()

        // on complete
        progressDialogFragment.dismiss()
    }

    suspend fun copyContents(
        fromDirectoryPosition: Int,
        toDirectoryPosition: Int,
        contentPositionSet: Collection<Int>,
        progressDialogFragment: ProgressDialogFragment
    ) {
        // set progressbar attributes
        progressDialogFragment.progressBar.max = contentPositionSet.size

        // initialize to directory document file
        var toDirectory = directoryArrayList[toDirectoryPosition]
        val toDirectoryPath = toDirectory.directoryOverView
        val toDirectoryRootUri = Uri.parse(toDirectoryPath.uri)
        val toDirectoryRootLastPath = toDirectoryRootUri.lastPathSegment!!
        var toDirectoryDocumentFile = DocumentFile.fromTreeUri(context, toDirectoryRootUri)!!
        if (toDirectoryPath.lastPath != toDirectoryRootLastPath) {
            val toDirectoryRelativePathList = toDirectoryPath.lastPath.substringAfter("$toDirectoryRootLastPath/").split("/")
            for (toDirectoryRelativePath in toDirectoryRelativePathList)
                toDirectoryDocumentFile = toDirectoryDocumentFile.findFile(toDirectoryRelativePath) ?: toDirectoryDocumentFile.createDirectory(
                    toDirectoryRelativePath
                )!!
        }

        // initialize to directory file name hash set
        val toDirectoryFileNameHashSet: HashSet<String> = HashSet()
        for (documentFile in toDirectoryDocumentFile.listFiles())
            if (documentFile.name != null)
                toDirectoryFileNameHashSet.add(documentFile.name!!)

        // copy contents
        val fromDirectory = directoryArrayList[fromDirectoryPosition]
        for (contentPosition in contentPositionSet) {
            // case is canceled
            if (progressDialogFragment.isCanceled)
                break

            // initialize content
            val content = fromDirectory.contentArrayList[contentPosition]

            // initialize to name
            val preName = content.name.substringBeforeLast(".")
            val postName = content.name.substringAfterLast(".")
            val isValidFormat = preName != postName
            var toName = content.name
            var index = 1
            while (toDirectoryFileNameHashSet.contains(toName)) {
                toName =
                    if (isValidFormat)
                        "$preName ($index).$postName"
                    else
                        "${content.name} ($index)"
                index++
            }
            toDirectoryFileNameHashSet.add(toName)

            // initialize content document file
            val mimeType = if (content.isVideo) MIME_TYPE_VIDEO else MIME_TYPE_IMAGE
            val contentDocumentFile = toDirectoryDocumentFile.createFile(mimeType, toName)!!

            withContext(Dispatchers.IO) {
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
                } catch (ioException: IOException) {
                    ioException.printStackTrace()
                } finally {
                    progressDialogFragment.progressBar.progress++
                }
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
            toDirectory.contentArrayList.add(Content(false, id, name, "-", 0, 0, date, relativePath, uri, 0L))
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
            toDirectory.contentArrayList.add(Content(true, id, name, "-", 0, 0, date, relativePath, uri, 0L))
        }
        videoCursor.close()

        // sort content array list
        directoryArrayList[toDirectoryPosition] = toDirectory
        sortContentArrayList(toDirectoryPosition)

        // sort directory array list
        sortDirectoryArrayList()

        // set progress dialog fragment result
        progressDialogFragment.setFragmentResult(directoryArrayList.indexOf(fromDirectory))

        // on complete
        progressDialogFragment.dismiss()
    }

    fun refreshSnapseed() {
        val directoryOverView = DirectoryOverview(null, PATH_SNAPSEED)
        val directory = Directory(directoryOverView)
        val directoryRelativePath = directoryOverView.lastPath.substringAfter(":") + "/"

        val imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val imageProjection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.RELATIVE_PATH
        )
        val imageSelection = "${MediaStore.Images.Media.RELATIVE_PATH} = ?"
        val imageSelectionArgs = arrayOf(directoryRelativePath)
        val imageCursor = context.contentResolver.query(imageUri, imageProjection, imageSelection, imageSelectionArgs, null)!!
        while (imageCursor.moveToNext()) {
            val id = imageCursor.getString(0)
            val name = imageCursor.getString(1)
            val date = imageCursor.getString(2).toLong()
            val relativePath = imageCursor.getString(3)
            val uri = Uri.withAppendedPath(imageUri, id.toString())

            directory.date = date.coerceAtLeast(directory.date)
            directory.contentArrayList.add(Content(false, id, name, "-", 0, 0, date, relativePath, uri, 0L))
        }
        imageCursor.close()

        // sort content array list
        for (i in directoryArrayList.indices) {
            if (directoryArrayList[i].name == directory.name && directoryArrayList[i].directoryOverView == directory.directoryOverView) {
                directoryArrayList[i] = directory
                sortContentArrayList(i)
                break
            }
        }

        // sort directory array list
        sortDirectoryArrayList()
    }
}