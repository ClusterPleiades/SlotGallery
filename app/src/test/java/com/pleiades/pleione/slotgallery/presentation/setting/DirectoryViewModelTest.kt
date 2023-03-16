package com.pleiades.pleione.slotgallery.presentation.setting

import com.pleiades.pleione.slotgallery.presentation.main.directory.DirectoryViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class DirectoryViewModelTest : StringSpec({
    "디렉토리 선택을 시작한다" {
        val directoryViewModel = DirectoryViewModel()

        directoryViewModel.startSelect(0)

        directoryViewModel.isSelecting shouldBe true
        directoryViewModel.state.value.selectedPositionSet shouldBe setOf(0)
    }

    "디렉토리를 선택한다" {
        val directoryViewModel = DirectoryViewModel()

        directoryViewModel.startSelect(0)
        directoryViewModel.toggleSelect(1)

        directoryViewModel.isSelecting shouldBe true
        directoryViewModel.state.value.selectedPositionSet shouldBe setOf(0, 1)
    }

    "디렉토리 선택을 취소한다" {
        val directoryViewModel = DirectoryViewModel()

        directoryViewModel.startSelect(0)
        directoryViewModel.toggleSelect(0)

        directoryViewModel.isSelecting shouldBe true
        directoryViewModel.state.value.selectedPositionSet shouldBe setOf()
    }

    "디렉토리를 범위로 선택한다" {
        val directoryViewModel = DirectoryViewModel()
        val startPosition = 0
        val endPosition = 10

        directoryViewModel.startSelect(0)
        directoryViewModel.selectRange(startPosition, endPosition)

        directoryViewModel.isSelecting shouldBe true
        directoryViewModel.state.value.selectedPositionSet shouldBe (startPosition..endPosition).toSet()
    }

    "디렉토리를 모두 선택한다" {
        val directoryViewModel = DirectoryViewModel()
        val size = 10

        directoryViewModel.startSelect(0)
        directoryViewModel.selectAll(size)

        directoryViewModel.isSelecting shouldBe true
        directoryViewModel.state.value.selectedPositionSet shouldBe (0 until size).toSet()
    }
})
