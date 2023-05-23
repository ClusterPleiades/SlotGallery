package com.pleiades.pleione.slotgallery.presentation.setting

import com.pleiades.pleione.slotgallery.domain.model.DirectoryOverview
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

        settingViewModel.getSelectedSlot() shouldBe Slot("test slot")
    }

    "슬롯을 추가한다" {
        val repository = TestingSlotRepository(_slotList = listOf(Slot("test slot")))
        val settingViewModel = SettingViewModel(SlotUseCaseBundle(repository))
        settingViewModel.addSlot("slot added")

        repository.getSlotList() shouldBe listOf(Slot("test slot"), Slot("slot added"))
        settingViewModel.state.value.slotList shouldBe listOf(Slot("test slot"), Slot("slot added"))
    }

    "슬롯을 제거한다" {
        val repository = TestingSlotRepository(_slotList = listOf(Slot("test slot"), Slot("test for remove")))
        val settingViewModel = SettingViewModel(SlotUseCaseBundle(repository))
        settingViewModel.removeSlot(1)

        repository.getSlotList() shouldBe listOf(Slot("test slot"))
        settingViewModel.state.value.slotList shouldBe listOf(Slot("test slot"))
    }

    "슬롯의 이름을 변경한다" {
        val repository = TestingSlotRepository(_slotList = listOf(Slot("test slot")))
        val settingViewModel = SettingViewModel(SlotUseCaseBundle(repository))
        settingViewModel.renameSlot(0, "renamed slot")

        repository.getSlotList() shouldBe listOf(Slot("renamed slot"))
        settingViewModel.state.value.slotList shouldBe listOf(Slot("renamed slot"))
    }

    "슬롯에 기본 디렉토리만 존재한다" {
        val repository = TestingSlotRepository(_slotList = listOf(Slot("test slot")))
        val settingViewModel = SettingViewModel(SlotUseCaseBundle(repository))

        repository.getSlotList()[0].directoryOverviewMutableList[0] shouldBe
            DirectoryOverview(lastPath = "Download")
        settingViewModel.state.value.slotList[0].directoryOverviewMutableList[0] shouldBe
            DirectoryOverview(lastPath = "Download")
    }

    "슬롯에 디렉토리를 추가한다" {
        val repository = TestingSlotRepository(_slotList = listOf(Slot("test slot")))
        val settingViewModel = SettingViewModel(SlotUseCaseBundle(repository))
        settingViewModel.addDirectoryOverView(DirectoryOverview(lastPath = "root"))
        // index: lastPath
        // 0: Download
        // 1: Snapseed
        // 2: DCIM/Camera
        // 3: Pictures/Screenshots
        // 4: root

        repository.getSlotList()[0].directoryOverviewMutableList[4] shouldBe
            DirectoryOverview(lastPath = "root")
        settingViewModel.state.value.slotList[0].directoryOverviewMutableList[4] shouldBe
            DirectoryOverview(lastPath = "root")
    }

    "슬롯에서 디렉토리를 삭제한다" {
        val repository = TestingSlotRepository(_slotList = listOf(Slot("test slot")))
        val settingViewModel = SettingViewModel(SlotUseCaseBundle(repository))
        settingViewModel.addDirectoryOverView(DirectoryOverview(lastPath = "root"))
        settingViewModel.removeDirectoryOverView(4)

        repository.getSlotList()[0].directoryOverviewMutableList.size shouldBe 4
        settingViewModel.state.value.slotList[0].directoryOverviewMutableList.size shouldBe 4
    }

    "디렉토리 visibility를 toggle한다" {
        val repository = TestingSlotRepository(_slotList = listOf(Slot("test slot")))
        val settingViewModel = SettingViewModel(SlotUseCaseBundle(repository))
        val isPrevVisible = settingViewModel.state.value.slotList[0].directoryOverviewMutableList[0].isVisible
        settingViewModel.toggleDirectoryOverViewVisibility(0)

        repository.getSlotList()[0].directoryOverviewMutableList[0].isVisible shouldBe !isPrevVisible
        settingViewModel.state.value.slotList[0].directoryOverviewMutableList[0].isVisible shouldBe !isPrevVisible
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

class TestingSlotRepository(var selectedSlot: Int = 0, private var _slotList: List<Slot> = emptyList()) : SlotRepository {
    override fun putSlotList(slotList: List<Slot>) {
        _slotList = slotList
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
