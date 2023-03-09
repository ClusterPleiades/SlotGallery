package com.pleiades.pleione.slotgallery.data.repository

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaScannerConnection
import android.net.Uri
import android.provider.MediaStore
import androidx.documentfile.provider.DocumentFile
import com.pleiades.pleione.slotgallery.Config.Companion.KEY_MEDIA_SORT_ORDER
import com.pleiades.pleione.slotgallery.Config.Companion.KEY_DIRECTORY_SORT_ORDER
import com.pleiades.pleione.slotgallery.Config.Companion.MIME_TYPE_IMAGE
import com.pleiades.pleione.slotgallery.Config.Companion.MIME_TYPE_VIDEO
import com.pleiades.pleione.slotgallery.Config.Companion.SORT_POSITION_BY_NAME
import com.pleiades.pleione.slotgallery.Config.Companion.SORT_POSITION_BY_NEWEST
import com.pleiades.pleione.slotgallery.Config.Companion.SORT_POSITION_BY_OLDEST
import com.pleiades.pleione.slotgallery.Config.Companion.URI_DEFAULT_DIRECTORY
import com.pleiades.pleione.slotgallery.domain.model.Directory
import com.pleiades.pleione.slotgallery.domain.model.DirectoryOverview
import com.pleiades.pleione.slotgallery.domain.model.Media
import com.pleiades.pleione.slotgallery.domain.model.Slot
import com.pleiades.pleione.slotgallery.domain.repository.MediaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.IOException
import java.text.CharacterIterator
import java.text.StringCharacterIterator
import javax.inject.Inject

class DefaultMediaRepository @Inject constructor(
    private val applicationContext: Context,
    private val sharedPreferences: SharedPreferences,
    private val contentResolver: ContentResolver
) : MediaRepository {
    override fun getDirectoryList(selectedSlot: Slot): List<Directory> {
        val directoryMutableList = mutableListOf<Directory>()
        selectedSlot.directoryOverviewMutableList.forEach {
            if (it.uri == URI_DEFAULT_DIRECTORY) {
                if (it.isVisible)
                    addDirectory(
                        directoryMutableList = directoryMutableList,
                        isSubDirectoryAllowed = false,
                        directoryOverview = it
                    )
            } else {
                addDirectory(
                    directoryMutableList = directoryMutableList,
                    isSubDirectoryAllowed = true,
                    directoryOverview = it
                )
            }
        }
        sortDirectoryList(directoryMutableList)
        sortMediaList(directoryMutableList)
        return directoryMutableList
    }

    override suspend fun copyDirectory(
        fromDirectorySet: Set<Directory>,
        toDirectory: Directory
    ) {
        // TODO set progressbar attributes

        val toDirectoryPath = toDirectory.directoryOverview
        val toDirectoryRootUri = Uri.parse(toDirectoryPath.uri)
        val toDirectoryRootLastPath = toDirectoryRootUri.lastPathSegment ?: return
        var toDirectoryDocumentFile = DocumentFile.fromTreeUri(applicationContext, toDirectoryRootUri) ?: return
        if (toDirectoryPath.lastPath != toDirectoryRootLastPath) {
            val toDirectoryRelativePathList = toDirectoryPath.lastPath.substringAfter("$toDirectoryRootLastPath/").split("/")
            for (toDirectoryRelativePath in toDirectoryRelativePathList) {
                toDirectoryDocumentFile =
                    toDirectoryDocumentFile.findFile(toDirectoryRelativePath)
                        ?: toDirectoryDocumentFile.createDirectory(toDirectoryRelativePath)
                                ?: return
            }
        }
        val toDirectoryFileNameMutableSet = mutableSetOf<String>()
        for (documentFile in toDirectoryDocumentFile.listFiles()) {
            documentFile.name?.let { toDirectoryFileNameMutableSet.add(it) }
        }

        for (fromDirectory in fromDirectorySet) {
            for (media in fromDirectory.mediaMutableList) {
                // TODO break if progress dialog canceled

                val preName = media.name.substringBeforeLast(".")
                val postName = media.name.substringAfterLast(".")
                val isValidFormat = preName != postName
                var toName = media.name
                var index = 1
                while (toDirectoryFileNameMutableSet.contains(toName)) {
                    toName =
                        if (isValidFormat) "$preName ($index).$postName"
                        else "${media.name} ($index)"
                    index++
                }
                toDirectoryFileNameMutableSet.add(toName)

                val mimeType =
                    if (media.isVideo) MIME_TYPE_VIDEO
                    else MIME_TYPE_IMAGE
                val mediaDocumentFile = toDirectoryDocumentFile.createFile(mimeType, toName) ?: return

                withContext(Dispatchers.IO) {
                    try {
                        val inputStream = contentResolver.openInputStream(media.uri) ?: return@withContext
                        val bufferedInputStream = BufferedInputStream(inputStream)
                        val outputStream = contentResolver.openOutputStream(mediaDocumentFile.uri) ?: return@withContext
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

                        MediaScannerConnection.scanFile(
                            /* context = */ applicationContext,
                            /* paths = */ arrayOf(mediaDocumentFile.uri.toString()),
                            /* mimeTypes = */ arrayOf(mimeType),
                            /* callback = */ null
                        )
                    } catch (ioException: IOException) {
                        ioException.printStackTrace()
                    }
                }

                // TODO improve progress
            }
        }

        // TODO
//        progressDialogFragment.setFragmentResult()
//        progressDialogFragment.dismiss()
    }

    override suspend fun copyMedia(
        fromDirectory: Directory,
        toDirectory: Directory,
        mediaSet: Set<Media>
    ) {
        // TODO set progressbar attributes

        val toDirectoryPath = toDirectory.directoryOverview
        val toDirectoryRootUri = Uri.parse(toDirectoryPath.uri)
        val toDirectoryRootLastPath = toDirectoryRootUri.lastPathSegment ?: return
        var toDirectoryDocumentFile = DocumentFile.fromTreeUri(applicationContext, toDirectoryRootUri) ?: return
        if (toDirectoryPath.lastPath != toDirectoryRootLastPath) {
            val toDirectoryRelativePathList = toDirectoryPath.lastPath.substringAfter("$toDirectoryRootLastPath/").split("/")
            for (toDirectoryRelativePath in toDirectoryRelativePathList)
                toDirectoryDocumentFile =
                    toDirectoryDocumentFile.findFile(toDirectoryRelativePath)
                        ?: toDirectoryDocumentFile.createDirectory(toDirectoryRelativePath)
                                ?: return
        }
        val toDirectoryFileNameMutableSet = mutableSetOf<String>()
        for (documentFile in toDirectoryDocumentFile.listFiles()) {
            documentFile.name?.let { toDirectoryFileNameMutableSet.add(it) }
        }

        for (media in fromDirectory.mediaMutableList) {
            // TODO break if progress dialog canceled

            val preName = media.name.substringBeforeLast(".")
            val postName = media.name.substringAfterLast(".")
            val isValidFormat = preName != postName
            var toName = media.name
            var index = 1
            while (toDirectoryFileNameMutableSet.contains(toName)) {
                toName =
                    if (isValidFormat) "$preName ($index).$postName"
                    else "${media.name} ($index)"
                index++
            }
            toDirectoryFileNameMutableSet.add(toName)

            val mimeType =
                if (media.isVideo) MIME_TYPE_VIDEO
                else MIME_TYPE_IMAGE
            val mediaDocumentFile = toDirectoryDocumentFile.createFile(mimeType, toName) ?: return

            withContext(Dispatchers.IO) {
                try {
                    val inputStream = contentResolver.openInputStream(media.uri) ?: return@withContext
                    val bufferedInputStream = BufferedInputStream(inputStream)
                    val outputStream = contentResolver.openOutputStream(mediaDocumentFile.uri) ?: return@withContext
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

                    MediaScannerConnection.scanFile(
                        /* context = */ applicationContext,
                        /* paths = */ arrayOf(mediaDocumentFile.uri.toString()),
                        /* mimeTypes = */ arrayOf(mimeType),
                        /* callback = */ null
                    )
                } catch (ioException: IOException) {
                    ioException.printStackTrace()
                }
            }

            // TODO improve progress
        }

        // TODO
//        progressDialogFragment.setFragmentResult()
//        progressDialogFragment.dismiss()
    }

    private fun addDirectory(
        directoryMutableList: MutableList<Directory>,
        isSubDirectoryAllowed: Boolean,
        directoryOverview: DirectoryOverview
    ) {
        val directory = Directory(directoryOverview)
        val directoryRelativePath = directoryOverview.lastPath.substringAfter(":") + "/"
        val subDirectoryLastPathMutableSet = mutableSetOf<String>()

        // case image
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
            if (isSubDirectoryAllowed) "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
            else "${MediaStore.Images.Media.RELATIVE_PATH} = ?"
        val imageSelectionArgs =
            if (isSubDirectoryAllowed) arrayOf("$directoryRelativePath%")
            else arrayOf(directoryRelativePath)

        contentResolver.query(
            /* uri = */ imageUri,
            /* projection = */ imageProjection,
            /* selection = */ imageSelection,
            /* selectionArgs = */ imageSelectionArgs,
            /* sortOrder = */ null
        )?.let {
            while (it.moveToNext()) {
                val id = it.getString(0)
                val name = it.getString(1)
                val size =
                    if (it.getString(2) == null) "-"
                    else getByteCountSI(it.getString(2))
                val width =
                    if (it.getString(3) == null) 0
                    else it.getString(3).toInt()
                val height =
                    if (it.getString(4) == null) 0
                    else it.getString(4).toInt()
                val date = it.getString(5).toLong()
                val relativePath = it.getString(6)
                val uri = Uri.withAppendedPath(imageUri, id.toString())

                if (isSubDirectoryAllowed && relativePath != directoryRelativePath)
                    subDirectoryLastPathMutableSet.add(directoryOverview.lastPath.substringBefore(":") + ":" + relativePath.substringBeforeLast("/"))
                else {
                    directory.mediaMutableList.add(
                        Media(
                            isVideo = false,
                            id = id,
                            name = name,
                            size = size,
                            width = width,
                            height = height,
                            date = date,
                            relativePath = relativePath,
                            uri = uri,
                            duration = 0L
                        )
                    )
                }
            }
            it.close()
        }

        // case video
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
            if (isSubDirectoryAllowed) "${MediaStore.Video.Media.RELATIVE_PATH} LIKE ?"
            else "${MediaStore.Video.Media.RELATIVE_PATH} = ?"
        val videoSelectionArgs =
            if (isSubDirectoryAllowed) arrayOf("$directoryRelativePath%")
            else arrayOf(directoryRelativePath)

        contentResolver.query(
            /* uri = */ videoUri,
            /* projection = */ videoProjection,
            /* selection = */ videoSelection,
            /* selectionArgs = */ videoSelectionArgs,
            /* sortOrder = */ null
        )?.let {
            while (it.moveToNext()) {
                val id = it.getString(0)
                val name = it.getString(1)
                val size =
                    if (it.getString(2) == null) "-"
                    else getByteCountSI(it.getString(2))
                val width =
                    if (it.getString(3) == null) 0
                    else it.getString(3).toInt()
                val height =
                    if (it.getString(4) == null) 0
                    else it.getString(4).toInt()
                val date = it.getString(5).toLong()
                val relativePath = it.getString(6)
                val uri = Uri.withAppendedPath(videoUri, id.toString())
                val duration =
                    if (it.getString(7) == null) 0L
                    else it.getString(7).toLong()

                if (isSubDirectoryAllowed && relativePath != directoryRelativePath)
                    subDirectoryLastPathMutableSet.add(directoryOverview.lastPath.substringBefore(":") + ":" + relativePath.substringBeforeLast("/"))
                else {
                    directory.mediaMutableList.add(
                        Media(
                            isVideo = true,
                            id = id,
                            name = name,
                            size = size,
                            width = width,
                            height = height,
                            date = date,
                            relativePath = relativePath,
                            uri = uri,
                            duration = duration
                        )
                    )
                }
            }
            it.close()
        }

        if (directory.mediaMutableList.size > 0) directoryMutableList.add(directory)
        subDirectoryLastPathMutableSet.forEach {
            addDirectory(
                directoryMutableList = directoryMutableList,
                isSubDirectoryAllowed = false,
                directoryOverview = DirectoryOverview(directoryOverview.uri, it)
            )
        }
    }

    private fun getByteCountSI(size: String): String {
        var bytes = size.toLong()
        if (-1000 < bytes && bytes < 1000) return "$bytes B"

        val characterIterator: CharacterIterator = StringCharacterIterator("kMGTPE")
        while (bytes <= -999950 || bytes >= 999950) {
            bytes /= 1000
            characterIterator.next()
        }
        return String.format("%.1f %cB", bytes / 1000.0, characterIterator.current())
    }

    private fun sortDirectoryList(directoryMutableList: MutableList<Directory>) {
        when (sharedPreferences.getInt(KEY_DIRECTORY_SORT_ORDER, 0)) {
            SORT_POSITION_BY_NAME -> directoryMutableList.sortBy { it.name }
            SORT_POSITION_BY_NEWEST -> directoryMutableList.sortByDescending { it.date }
            SORT_POSITION_BY_OLDEST -> directoryMutableList.sortBy { it.date }
        }
    }

    private fun sortMediaList(directoryMutableList: MutableList<Directory>) {
        when (sharedPreferences.getInt(KEY_MEDIA_SORT_ORDER, 0)) {
            SORT_POSITION_BY_NAME -> for (directory in directoryMutableList) directory.mediaMutableList.sortBy { it.name }
            SORT_POSITION_BY_NEWEST -> for (directory in directoryMutableList) directory.mediaMutableList.sortByDescending { it.date }
            SORT_POSITION_BY_OLDEST -> for (directory in directoryMutableList) directory.mediaMutableList.sortBy { it.date }
        }
    }
}