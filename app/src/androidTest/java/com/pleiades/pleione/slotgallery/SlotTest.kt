package com.pleiades.pleione.slotgallery

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pleiades.pleione.slotgallery.data.repository.DefaultSlotRepository
import com.pleiades.pleione.slotgallery.domain.model.Slot
import com.pleiades.pleione.slotgallery.domain.usecase.slot.GetSelectedSlotPositionUseCase
import com.pleiades.pleione.slotgallery.domain.usecase.slot.GetSlotListUseCase
import com.pleiades.pleione.slotgallery.domain.usecase.slot.PutSelectedSlotPositionUseCase
import com.pleiades.pleione.slotgallery.domain.usecase.slot.PutSlotListUseCase
import com.pleiades.pleione.slotgallery.domain.usecase.slot.bundle.SlotUseCaseBundle
import com.pleiades.pleione.slotgallery.extension.testAssertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SlotTest {
    private val applicationContext = ApplicationProvider.getApplicationContext<Context>()
    private val sharedPreferences = applicationContext.getSharedPreferences("testSharedPreference", MODE_PRIVATE)
    private val editor = sharedPreferences.edit()
    private val repository = DefaultSlotRepository(sharedPreferences, editor)

    private val slotUseCaseBundle =
        SlotUseCaseBundle(
            putSlotListUseCase = PutSlotListUseCase(repository),
            getSlotListUseCase = GetSlotListUseCase(repository),
            putSelectedSlotPositionUseCase = PutSelectedSlotPositionUseCase(repository),
            getSelectedSlotPositionUseCase = GetSelectedSlotPositionUseCase(repository)
        )

    @Test
    fun testPutGetSlotList() = runBlocking {
        val expectedSlotList =
            listOf(
                Slot("forest"),
                Slot("jjoo"),
                Slot("kit"),
                Slot("kloong"),
            )
        slotUseCaseBundle.putSlotListUseCase(expectedSlotList)
        val actualSlotList = slotUseCaseBundle.getSlotListUseCase()

        testAssertEquals(
            expected = expectedSlotList,
            actual = actualSlotList
        )
    }

    @Test
    fun testPutGetSelectedSlotPosition() = runBlocking {
        val expectedSelectedSlotPosition = 1004
        slotUseCaseBundle.putSelectedSlotPositionUseCase(expectedSelectedSlotPosition)
        val actualSelectedSlotPosition = slotUseCaseBundle.getSelectedSlotPositionUseCase()

        testAssertEquals(
            expected = expectedSelectedSlotPosition,
            actual = actualSelectedSlotPosition
        )
    }
}
