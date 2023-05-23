package com.pleiades.pleione.slotgallery.presentation.setting

import com.pleiades.pleione.slotgallery.Config
import com.pleiades.pleione.slotgallery.domain.model.Directory
import com.pleiades.pleione.slotgallery.domain.model.DirectoryOverview
import com.pleiades.pleione.slotgallery.domain.model.Media
import com.pleiades.pleione.slotgallery.domain.model.Slot
import com.pleiades.pleione.slotgallery.domain.repository.MediaRepository
import com.pleiades.pleione.slotgallery.domain.repository.WindowRepository
import com.pleiades.pleione.slotgallery.domain.usecase.media.CopyDirectoryUseCase
import com.pleiades.pleione.slotgallery.domain.usecase.media.CopyMediaUseCase
import com.pleiades.pleione.slotgallery.domain.usecase.media.GetDirectoryListUseCase
import com.pleiades.pleione.slotgallery.domain.usecase.media.bundle.MediaUseCaseBundle
import com.pleiades.pleione.slotgallery.domain.usecase.slot.GetSelectedSlotPositionUseCase
import com.pleiades.pleione.slotgallery.domain.usecase.slot.GetSlotListUseCase
import com.pleiades.pleione.slotgallery.domain.usecase.slot.PutSelectedSlotPositionUseCase
import com.pleiades.pleione.slotgallery.domain.usecase.slot.PutSlotListUseCase
import com.pleiades.pleione.slotgallery.domain.usecase.slot.bundle.SlotUseCaseBundle
import com.pleiades.pleione.slotgallery.presentation.main.MainViewModel
import io.kotest.core.spec.style.AnnotationSpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest : AnnotationSpec() {
    lateinit var mainViewModel: MainViewModel

    private val mainThreadSurrogate = newSingleThreadContext(MainViewModelTest::class.java.simpleName)

    @Before
    fun setUp() {
        val testingMediaRepository = TestingMediaRepository(
            listOf(
                Directory(
                    DirectoryOverview(
                        lastPath = Config.PATH_DOWNLOAD,
                        isVisible = true
                    )
                ),
                Directory(
                    DirectoryOverview(
                        lastPath = Config.PATH_SNAPSEED,
                        isVisible = true
                    )
                ),
                Directory(
                    DirectoryOverview(
                        lastPath = Config.PATH_CAMERA,
                        isVisible = true
                    )
                ),
                Directory(
                    DirectoryOverview(
                        lastPath = Config.PATH_SCREENSHOTS,
                        isVisible = true
                    )
                )
            )
        )
        val testingSlotRepository = TestingSlotRepository(
            selectedSlot = 0,
            _slotList = listOf(Slot("test slot"))
        )
        val testingWindowRepository = TestingWindowRepository()

        mainViewModel = MainViewModel(
            mediaUseCaseBundle = this.MediaUseCaseBundle(testingMediaRepository),
            slotUseCaseBundle = this.SlotUseCaseBundle(testingSlotRepository),
            getWidthUseCase = this.GetWidthUseCase(testingWindowRepository)
        )

        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `슬롯이 비어 있지 않다`() = runBlocking {
        val expectedResult = false
        val actualResult = mainViewModel.isSlotListEmpty()

        testAssertEquals(
            expected = expectedResult,
            actual = actualResult
        )
    }

    @Test
    fun `디렉토리 리스트를 불러온다`() = runBlocking {
        mainViewModel.loadDirectoryList()

        testAssertTrue(
            condition = mainViewModel.state.value.directoryList.isNotEmpty()
        )
    }

    @Test
    fun `디렉토리 리스트를 복사한다`() = runBlocking {
        mainViewModel.loadDirectoryList()
        mainViewModel.copyDirectory(
            fromDirectoryList = mainViewModel.state.value.directoryList,
            toDirectory = mainViewModel.state.value.directoryList[0]
        )

        testAssertTrue(
            condition = mainViewModel.progressDialogState.value.job != null
        )
    }

    @Test
    fun `프로그레스를 취소한다`() = runBlocking {
        mainViewModel.loadDirectoryList()
        mainViewModel.copyDirectory(
            fromDirectoryList = mainViewModel.state.value.directoryList,
            toDirectory = mainViewModel.state.value.directoryList[0]
        )
        mainViewModel.cancelProgress()

        testAssertTrue(
            condition = mainViewModel.progressDialogState.value.isCanceled
        )
        testAssertTrue(
            condition = mainViewModel.progressDialogState.value.job == null
        )
    }

    @Test
    fun `프로그레스를 초기화한다`() = runBlocking {
        mainViewModel.loadDirectoryList()
        mainViewModel.copyDirectory(
            fromDirectoryList = mainViewModel.state.value.directoryList,
            toDirectory = mainViewModel.state.value.directoryList[0]
        )
        mainViewModel.resetProgress()

        testAssertTrue(
            condition = !mainViewModel.progressDialogState.value.isCanceled
        )
        testAssertTrue(
            condition = mainViewModel.progressDialogState.value.progress == 0
        )
        testAssertTrue(
            condition = mainViewModel.progressDialogState.value.maxProgress == 0
        )
        testAssertTrue(
            condition = mainViewModel.progressDialogState.value.job == null
        )
    }

    private fun MediaUseCaseBundle(repository: TestingMediaRepository) =
        MediaUseCaseBundle(
            getDirectoryListUseCase = GetDirectoryListUseCase(repository),
            copyDirectoryUseCase = CopyDirectoryUseCase(repository),
            copyMediaUseCase = CopyMediaUseCase(repository)
        )

    private fun SlotUseCaseBundle(repository: TestingSlotRepository) =
        SlotUseCaseBundle(
            putSlotListUseCase = PutSlotListUseCase(repository),
            getSlotListUseCase = GetSlotListUseCase(repository),
            putSelectedSlotPositionUseCase = PutSelectedSlotPositionUseCase(repository),
            getSelectedSlotPositionUseCase = GetSelectedSlotPositionUseCase(repository)
        )

    private fun GetWidthUseCase(repository: TestingWindowRepository) =
        com.pleiades.pleione.slotgallery.domain.usecase.window.GetWidthUseCase(repository)
}

private fun testAssertEquals(expected: Boolean, actual: Boolean) {
    assertEquals(expected, actual)
}

private fun testAssertTrue(condition: Boolean) {
    assertTrue(condition)
}

class TestingMediaRepository(
    private var _directoryList: List<Directory> = emptyList()
) : MediaRepository {
    override fun getDirectoryList(selectedSlot: Slot): List<Directory> {
        return _directoryList
    }

    override suspend fun copyDirectory(
        fromDirectoryList: List<Directory>,
        toDirectory: Directory,
        setMaxProgress: (Int) -> Unit,
        progress: () -> Unit
    ) {
        // physical copy test in DefaultMediaRepositoryTest
    }

    override suspend fun copyMedia(fromDirectory: Directory, toDirectory: Directory, mediaSet: Set<Media>) {
        // physical copy test in DefaultMediaRepositoryTest
    }
}

class TestingWindowRepository : WindowRepository {
    override fun getWidth(): Int = 1080
}
