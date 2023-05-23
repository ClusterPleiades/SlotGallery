package com.pleiades.pleione.slotgallery

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pleiades.pleione.slotgallery.data.repository.WindowRepositoryImpl
import com.pleiades.pleione.slotgallery.domain.usecase.window.GetWidthUseCase
import com.pleiades.pleione.slotgallery.extension.testAssertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WindowRepositoryImplTest {
    private val resources = ApplicationProvider.getApplicationContext<Context>().resources
    private val repository = WindowRepositoryImpl(resources)

    private val getWidthUseCase = GetWidthUseCase(repository)

    @Test
    fun testGetWidth() = runBlocking {
        val expectedWidth = 1080 // google pixel 6 portrait
        val actualWidth = getWidthUseCase()

        testAssertEquals(
            expected = expectedWidth,
            actual = actualWidth
        )
    }
}
