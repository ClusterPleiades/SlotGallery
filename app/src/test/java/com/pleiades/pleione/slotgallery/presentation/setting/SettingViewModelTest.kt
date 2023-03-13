package com.pleiades.pleione.slotgallery.presentation.setting

import com.pleiades.pleione.slotgallery.domain.model.Slot
import com.pleiades.pleione.slotgallery.domain.repository.SlotRepository
import com.pleiades.pleione.slotgallery.domain.usecase.slot.GetSelectedSlotPositionUseCase
import com.pleiades.pleione.slotgallery.domain.usecase.slot.GetSlotListUseCase
import com.pleiades.pleione.slotgallery.domain.usecase.slot.PutSelectedSlotPositionUseCase
import com.pleiades.pleione.slotgallery.domain.usecase.slot.PutSlotListUseCase
import com.pleiades.pleione.slotgallery.domain.usecase.slot.bundle.SlotUseCaseBundle
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class SettingViewModelTest : StringSpec({
    "선택한 슬롯이 없다" {
        val settingViewModel = SettingViewModel(SlotUseCaseBundle(TestingSlotRepository()))
        settingViewModel.getSelectedSlot().shouldBeNull()
    }

    "슬롯을 선택한다" {
        val repository = TestingSlotRepository(_slotList = listOf(Slot("test slot")))
        val settingViewModel = SettingViewModel(SlotUseCaseBundle(repository))
        settingViewModel.getSelectedSlot()!!.name shouldBe "test slot"
    }
})

private fun SlotUseCaseBundle(repository: TestingSlotRepository) =
        SlotUseCaseBundle(
                putSlotListUseCase = PutSlotListUseCase(repository),
                getSlotListUseCase = GetSlotListUseCase(repository),
                putSelectedSlotPositionUseCase = PutSelectedSlotPositionUseCase(
                        repository
                ),
                getSelectedSlotPositionUseCase = GetSelectedSlotPositionUseCase(
                        repository
                )
        )

class TestingSlotRepository(var selectedSlot: Int = 0, private val _slotList: List<Slot> = emptyList()) : SlotRepository {
    override fun putSlotList(slotList: List<Slot>) {
        TODO("Not yet implemented")
    }

    override fun getSlotList(): List<Slot> {
        return _slotList
    }

    override fun putSelectedSlotPosition(position: Int) {
        selectedSlot = position
    }

    override fun getSelectedSlotPosition(): Int {
        return selectedSlot
    }
}
