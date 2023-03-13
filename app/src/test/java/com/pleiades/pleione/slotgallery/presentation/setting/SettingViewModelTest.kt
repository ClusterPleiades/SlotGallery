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

class SettingViewModelTest : StringSpec({
    "선택한 슬롯이 없다" {
        val settingViewModel = SettingViewModel(
            SlotUseCaseBundle(
                putSlotListUseCase = PutSlotListUseCase(TestingSlotRepository()),
                getSlotListUseCase = GetSlotListUseCase(TestingSlotRepository()),
                putSelectedSlotPositionUseCase = PutSelectedSlotPositionUseCase(
                    TestingSlotRepository()
                ),
                getSelectedSlotPositionUseCase = GetSelectedSlotPositionUseCase(
                    TestingSlotRepository()
                )
            )
        )
        settingViewModel.getSelectedSlot().shouldBeNull()
    }
})

class TestingSlotRepository : SlotRepository {
    override fun putSlotList(slotList: List<Slot>) {
        TODO("Not yet implemented")
    }

    override fun getSlotList(): List<Slot> {
        TODO("Not yet implemented")
    }

    override fun putSelectedSlotPosition(position: Int) {
        TODO("Not yet implemented")
    }

    override fun getSelectedSlotPosition(): Int {
        TODO("Not yet implemented")
    }
}
