package com.pleiades.pleione.slotgallery

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pleiades.pleione.slotgallery.data.repository.DefaultWindowRepository
import com.pleiades.pleione.slotgallery.domain.usecase.window.GetWidthUseCase
import com.pleiades.pleione.slotgallery.util.testAssertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WindowTest {
    private val resources = ApplicationProvider.getApplicationContext<Context>().resources
    private val repository = DefaultWindowRepository(resources)

    private val getWidthUseCase = GetWidthUseCase(repository)

    @Test
    fun testGetWidth() = runBlocking {
        val width = getWidthUseCase()

        testAssertEquals(
            expected = 1080,
            actual = width
        )
    }
}
