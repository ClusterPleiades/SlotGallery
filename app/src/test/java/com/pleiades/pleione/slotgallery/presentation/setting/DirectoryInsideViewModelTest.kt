package com.pleiades.pleione.slotgallery.presentation.setting

import androidx.lifecycle.SavedStateHandle
import com.pleiades.pleione.slotgallery.Config.Companion.REQUEST_RESULT_KEY_DIRECTORY_OVERVIEW
import com.pleiades.pleione.slotgallery.domain.model.DirectoryOverview
import com.pleiades.pleione.slotgallery.presentation.main.directory.inside.DirectoryInsideViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class DirectoryInsideViewModelTest : StringSpec({
    "디렉토리 선택을 시작한다" {
        val savedStateHandle = SavedStateHandle().apply {
            set(REQUEST_RESULT_KEY_DIRECTORY_OVERVIEW, DirectoryOverview(lastPath = "testLastPath"))
        }
        val directoryInsideViewModel = DirectoryInsideViewModel(savedStateHandle)

        directoryInsideViewModel.startSelect(0)

        directoryInsideViewModel.isSelecting shouldBe true
        directoryInsideViewModel.state.value.selectedPositionSet shouldBe setOf(0)
    }

    "디렉토리를 선택한다" {
        val savedStateHandle = SavedStateHandle().apply {
            set(REQUEST_RESULT_KEY_DIRECTORY_OVERVIEW, DirectoryOverview(lastPath = "testLastPath"))
        }
        val directoryInsideViewModel = DirectoryInsideViewModel(savedStateHandle)

        directoryInsideViewModel.startSelect(0)
        directoryInsideViewModel.toggleSelect(1)

        directoryInsideViewModel.isSelecting shouldBe true
        directoryInsideViewModel.state.value.selectedPositionSet shouldBe setOf(0, 1)
    }

    "디렉토리 선택을 취소한다" {
        val savedStateHandle = SavedStateHandle().apply {
            set(REQUEST_RESULT_KEY_DIRECTORY_OVERVIEW, DirectoryOverview(lastPath = "testLastPath"))
        }
        val directoryInsideViewModel = DirectoryInsideViewModel(savedStateHandle)

        directoryInsideViewModel.startSelect(0)
        directoryInsideViewModel.toggleSelect(0)

        directoryInsideViewModel.isSelecting shouldBe true
        directoryInsideViewModel.state.value.selectedPositionSet shouldBe setOf()
    }

    "디렉토리를 범위로 선택한다" {
        val savedStateHandle = SavedStateHandle().apply {
            set(REQUEST_RESULT_KEY_DIRECTORY_OVERVIEW, DirectoryOverview(lastPath = "testLastPath"))
        }
        val directoryInsideViewModel = DirectoryInsideViewModel(savedStateHandle)
        val startPosition = 0
        val endPosition = 10

        directoryInsideViewModel.startSelect(0)
        directoryInsideViewModel.selectRange(startPosition, endPosition)

        directoryInsideViewModel.isSelecting shouldBe true
        directoryInsideViewModel.state.value.selectedPositionSet shouldBe (startPosition..endPosition).toSet()
    }

    "디렉토리를 모두 선택한다" {
        val savedStateHandle = SavedStateHandle().apply {
            set(REQUEST_RESULT_KEY_DIRECTORY_OVERVIEW, DirectoryOverview(lastPath = "testLastPath"))
        }
        val directoryInsideViewModel = DirectoryInsideViewModel(savedStateHandle)
        val size = 10

        directoryInsideViewModel.startSelect(0)
        directoryInsideViewModel.selectAll(size)

        directoryInsideViewModel.isSelecting shouldBe true
        directoryInsideViewModel.state.value.selectedPositionSet shouldBe (0 until size).toSet()
    }
})
