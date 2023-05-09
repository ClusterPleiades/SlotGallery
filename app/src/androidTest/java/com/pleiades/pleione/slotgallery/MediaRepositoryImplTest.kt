package com.pleiades.pleione.slotgallery

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pleiades.pleione.slotgallery.data.repository.MediaRepositoryImpl
import com.pleiades.pleione.slotgallery.domain.model.Directory
import com.pleiades.pleione.slotgallery.domain.model.DirectoryOverview
import com.pleiades.pleione.slotgallery.domain.model.Slot
import com.pleiades.pleione.slotgallery.domain.usecase.media.CopyDirectoryUseCase
import com.pleiades.pleione.slotgallery.domain.usecase.media.CopyMediaUseCase
import com.pleiades.pleione.slotgallery.domain.usecase.media.GetDirectoryListUseCase
import com.pleiades.pleione.slotgallery.domain.usecase.media.bundle.MediaUseCaseBundle
import com.pleiades.pleione.slotgallery.extension.testAssertEquals
import com.pleiades.pleione.slotgallery.extension.testAssertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MediaRepositoryImplTest {
    private val applicationContext = ApplicationProvider.getApplicationContext<Context>()
    private val contentResolver = applicationContext.contentResolver
    private val sharedPreferences =
        applicationContext.getSharedPreferences(
            /* name = */ "testSharedPreference",
            /* mode = */ Context.MODE_PRIVATE
        )
    private val repository = MediaRepositoryImpl(applicationContext, sharedPreferences, contentResolver)
    private val mediaUseCaseBundle = MediaUseCaseBundle(
        getDirectoryListUseCase = GetDirectoryListUseCase(repository),
        copyDirectoryUseCase = CopyDirectoryUseCase(repository),
        copyMediaUseCase = CopyMediaUseCase(repository)
    )

    @Test
    fun `사진이 한 장도 없다`() = runBlocking { // test on new emulator
        val expectedDirectoryList = emptyList<Directory>()
        val actualDirectoryList = mediaUseCaseBundle.getDirectoryListUseCase(Slot("test slot"))

        testAssertEquals(
            expected = expectedDirectoryList,
            actual = actualDirectoryList
        )
    }

    @Test
    fun `모든 기본 디렉토리에 사진이 존재한다`() = runBlocking { // test on physical device
        val expectedDirectoryListSize = 4
        val actualDirectoryListSize = mediaUseCaseBundle.getDirectoryListUseCase(Slot("test slot")).size

        testAssertEquals(
            expected = expectedDirectoryListSize,
            actual = actualDirectoryListSize
        )
    }

    @Test
    fun `디렉토리를 복사한다`() = runBlocking { // test on physical device, need toDirectory permission
        val fromDirectoryList = mediaUseCaseBundle.getDirectoryListUseCase(Slot("test slot"))
        val toDirectory = Directory(
            DirectoryOverview(
                uri = "content://com.android.externalstorage.documents/tree/primary%3ASnapseed",
                lastPath = "primary:Snapseed"
            )
        )
        val prevSize = fromDirectoryList.sumOf { it.mediaMutableList.size }

        mediaUseCaseBundle.copyDirectoryUseCase(fromDirectoryList, toDirectory, {}, {})
        val currSize = mediaUseCaseBundle.getDirectoryListUseCase(Slot("test slot"))
            .sumOf { it.mediaMutableList.size }

        testAssertTrue(
            condition = currSize > prevSize
        )
    }
}
