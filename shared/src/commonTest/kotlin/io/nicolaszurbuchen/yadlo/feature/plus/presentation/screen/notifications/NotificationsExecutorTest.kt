package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.notifications

import app.cash.turbine.test
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import io.nicolaszurbuchen.yadlo.common.reminder.domain.fake.FakeReminderSettingsRepository
import io.nicolaszurbuchen.yadlo.common.reminder.domain.usecase.ObserveRemindersEnabledUseCase
import io.nicolaszurbuchen.yadlo.common.reminder.domain.usecase.SetRemindersEnabledUseCase
import io.nicolaszurbuchen.yadlo.infra.notification.NotificationPermissionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationsExecutorTest {
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun onCreate_readsBothHalves() =
        runTest {
            val store = createStore(settings = FakeReminderSettingsRepository(), isGranted = true)
            testDispatcher.scheduler.runCurrent()

            assertEquals(true, store.state.isEnabled)
            assertEquals(true, store.state.isPermissionGranted)
            store.dispose()
        }

    @Test
    fun onCreate_nothingStored_readsTheDefaultRatherThanOff() =
        runTest {
            // The upgrade path reaching the screen: somebody who granted the permission before this
            // switch existed must find it on, because their reminders are.
            val store = createStore(settings = FakeReminderSettingsRepository(), isGranted = true)
            testDispatcher.scheduler.runCurrent()

            assertEquals(true, store.state.isEnabled)
            store.dispose()
        }

    @Test
    fun enabledChanged_toOff_writesItAndAsksForNothing() =
        runTest {
            val settings = FakeReminderSettingsRepository()
            val store = createStore(settings = settings, isGranted = true)
            testDispatcher.scheduler.runCurrent()

            store.accept(NotificationsIntent.EnabledChanged(false))
            testDispatcher.scheduler.runCurrent()

            assertEquals(false, settings.observeEnabled().first())
            assertEquals(false, store.state.isEnabled)
            store.dispose()
        }

    @Test
    fun enabledChanged_toOn_writesItAndThenAsksForThePermission() =
        runTest {
            val settings = FakeReminderSettingsRepository()
            settings.emitEnabled(false)
            val store = createStore(settings = settings, isGranted = false)
            testDispatcher.scheduler.runCurrent()

            store.labels.test {
                store.accept(NotificationsIntent.EnabledChanged(true))
                testDispatcher.scheduler.runCurrent()

                assertEquals(NotificationsLabel.RequestPermission, awaitItem())
            }

            // Written whether or not the prompt is granted: what is recorded is that they asked.
            assertTrue(settings.observeEnabled().first())
            store.dispose()
        }

    @Test
    fun permissionAnswered_refused_leavesTheStoredAnswerOn() =
        runTest {
            // So the switch comes back on by itself if they later allow it in system settings —
            // and so the screen can say the system is blocking something they wanted.
            val settings = FakeReminderSettingsRepository()
            val store = createStore(settings = settings, isGranted = false)
            testDispatcher.scheduler.runCurrent()

            store.accept(NotificationsIntent.EnabledChanged(true))
            testDispatcher.scheduler.runCurrent()
            store.accept(NotificationsIntent.PermissionAnswered(false))
            testDispatcher.scheduler.runCurrent()

            assertEquals(true, store.state.isEnabled)
            assertEquals(false, store.state.isPermissionGranted)
            store.dispose()
        }

    @Test
    fun permissionRechecked_picksUpAnAnswerChangedOutsideTheApp() =
        runTest {
            // The return leg from system settings, which is the only thing that makes the button on
            // this screen feel like it worked.
            var granted = false
            val store = createStore(settings = FakeReminderSettingsRepository(), isGranted = { granted })
            testDispatcher.scheduler.runCurrent()
            assertEquals(false, store.state.isPermissionGranted)

            granted = true
            store.accept(NotificationsIntent.PermissionRechecked)
            testDispatcher.scheduler.runCurrent()

            assertEquals(true, store.state.isPermissionGranted)
            store.dispose()
        }

    @Test
    fun systemSettingsClicked_asksTheRouteToLeaveTheApp() =
        runTest {
            val store = createStore(settings = FakeReminderSettingsRepository(), isGranted = false)
            testDispatcher.scheduler.runCurrent()

            store.labels.test {
                store.accept(NotificationsIntent.SystemSettingsClicked)
                assertEquals(NotificationsLabel.OpenSystemSettings, awaitItem())
            }
            store.dispose()
        }

    @Test
    fun theStoredAnswerIsObserved_soAWriteElsewhereReachesTheSwitch() =
        runTest {
            val settings = FakeReminderSettingsRepository()
            val store = createStore(settings = settings, isGranted = true)
            testDispatcher.scheduler.runCurrent()

            settings.emitEnabled(false)
            testDispatcher.scheduler.runCurrent()

            assertEquals(false, store.state.isEnabled)
            store.dispose()
        }

    private fun createStore(
        settings: FakeReminderSettingsRepository,
        isGranted: Boolean,
    ): NotificationsStore = createStore(settings) { isGranted }

    private fun createStore(
        settings: FakeReminderSettingsRepository,
        isGranted: () -> Boolean,
    ): NotificationsStore =
        NotificationsStoreFactory(
            storeFactory = DefaultStoreFactory(),
            observeRemindersEnabled = ObserveRemindersEnabledUseCase(settings),
            setRemindersEnabled = SetRemindersEnabledUseCase(settings),
            permissionStatus = NotificationPermissionStatus { isGranted() },
        ).create()
}
