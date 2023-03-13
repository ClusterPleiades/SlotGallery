package com.pleiades.pleione.slotgallery

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pleiades.pleione.slotgallery.data.repository.DefaultUtilRepository
import com.pleiades.pleione.slotgallery.domain.usecase.util.GetDirectorySortOrderPositionUseCase
import com.pleiades.pleione.slotgallery.domain.usecase.util.GetMediaSortOrderPositionUseCase
import com.pleiades.pleione.slotgallery.domain.usecase.util.PutDirectorySortOrderPositionUseCase
import com.pleiades.pleione.slotgallery.domain.usecase.util.PutMediaSortOrderPositionUseCase
import com.pleiades.pleione.slotgallery.domain.usecase.util.bundle.UtilUseCaseBundle
import com.pleiades.pleione.slotgallery.extension.testAssertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DefaultUtilRepositoryTest {
    private val applicationContext = ApplicationProvider.getApplicationContext<Context>()
    private val sharedPreferences = applicationContext.getSharedPreferences("testSharedPreference", MODE_PRIVATE)
    private val editor = sharedPreferences.edit()
    private val repository = DefaultUtilRepository(sharedPreferences, editor)

    private val utilUseCaseBundle =
        UtilUseCaseBundle(
            putDirectorySortOrderPositionUseCase = PutDirectorySortOrderPositionUseCase(repository),
            getDirectorySortOrderPositionUseCase = GetDirectorySortOrderPositionUseCase(repository),
            putMediaSortOrderPositionUseCase = PutMediaSortOrderPositionUseCase(repository),
            getMediaSortOrderPositionUseCase = GetMediaSortOrderPositionUseCase(repository)
        )

    @Test
    fun testPutGetDirectorySortOrderPosition() = runBlocking {
        val expectedPosition = 1004
        utilUseCaseBundle.putDirectorySortOrderPositionUseCase(expectedPosition)
        val actualPosition = utilUseCaseBundle.getDirectorySortOrderPositionUseCase()

        testAssertEquals(
            expected = expectedPosition,
            actual = actualPosition
        )
    }

    @Test
    fun testPutGetMediaSortOrderPosition() = runBlocking {
        val expectedPosition = 1004
        utilUseCaseBundle.putMediaSortOrderPositionUseCase(expectedPosition)
        val actualPosition = utilUseCaseBundle.getMediaSortOrderPositionUseCase()

        testAssertEquals(
            expected = expectedPosition,
            actual = actualPosition
        )
    }
}
