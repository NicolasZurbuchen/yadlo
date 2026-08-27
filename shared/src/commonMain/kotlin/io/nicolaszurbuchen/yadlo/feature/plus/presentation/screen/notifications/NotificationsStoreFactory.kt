package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.notifications

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import io.nicolaszurbuchen.yadlo.core.reminder.domain.usecase.ObserveRemindersEnabledUseCase
import io.nicolaszurbuchen.yadlo.core.reminder.domain.usecase.SetRemindersEnabledUseCase
import io.nicolaszurbuchen.yadlo.infra.notification.NotificationPermissionStatus
import kotlinx.coroutines.launch

interface NotificationsStore : Store<NotificationsIntent, NotificationsState, NotificationsLabel>

class NotificationsStoreFactory(
    private val storeFactory: StoreFactory,
    private val observeRemindersEnabled: ObserveRemindersEnabledUseCase,
    private val setRemindersEnabled: SetRemindersEnabledUseCase,
    private val permissionStatus: NotificationPermissionStatus,
) {
    fun create(): NotificationsStore =
        object :
            NotificationsStore,
            Store<NotificationsIntent, NotificationsState, NotificationsLabel> by storeFactory.create(
                name = "NotificationsStore",
                initialState = NotificationsState(),
                bootstrapper = BootstrapperImpl(),
                executorFactory = { ExecutorImpl() },
                reducer = ReducerImpl,
            ) {}

    private class BootstrapperImpl : CoroutineBootstrapper<NotificationsAction>() {
        override fun invoke() {
            dispatch(NotificationsAction.ObserveSettings)
            dispatch(NotificationsAction.CheckPermission)
        }
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<NotificationsIntent, NotificationsAction, NotificationsState, NotificationsMessage, NotificationsLabel>() {
        override fun executeAction(action: NotificationsAction) {
            when (action) {
                NotificationsAction.ObserveSettings -> observeSettings()
                NotificationsAction.CheckPermission -> checkPermission()
            }
        }

        override fun executeIntent(intent: NotificationsIntent) {
            when (intent) {
                is NotificationsIntent.EnabledChanged -> setEnabled(intent.enabled)
                is NotificationsIntent.PermissionAnswered -> dispatch(NotificationsMessage.PermissionUpdated(intent.granted))
                NotificationsIntent.PermissionRechecked -> checkPermission()
                NotificationsIntent.SystemSettingsClicked -> publish(NotificationsLabel.OpenSystemSettings)
            }
        }

        /**
         * Observed rather than read once, because this screen is not the only writer: the switch
         * reflects a store the rest of the app reads on every sync, and reading it once would leave
         * the screen holding a stale answer if anything else ever writes one.
         */
        private fun observeSettings() {
            scope.launch {
                observeRemindersEnabled().collect { enabled ->
                    dispatch(NotificationsMessage.EnabledUpdated(enabled))
                }
            }
        }

        private fun checkPermission() {
            scope.launch {
                dispatch(NotificationsMessage.PermissionUpdated(permissionStatus.isGranted()))
            }
        }

        /**
         * **The write happens before the prompt, and it happens whether or not the prompt succeeds.**
         * What is being recorded is that the visitor asked for reminders, which stays true even when
         * the operating system refuses — and it is what makes the switch come on by itself if they
         * later allow notifications in system settings and come back.
         */
        private fun setEnabled(enabled: Boolean) {
            scope.launch {
                setRemindersEnabled(enabled)

                if (enabled) publish(NotificationsLabel.RequestPermission)
            }
        }
    }

    // internal (not private) so NotificationsReducerTest can exercise it directly
    internal object ReducerImpl : Reducer<NotificationsState, NotificationsMessage> {
        override fun NotificationsState.reduce(msg: NotificationsMessage): NotificationsState =
            when (msg) {
                is NotificationsMessage.EnabledUpdated -> {
                    copy(isEnabled = msg.enabled)
                }

                is NotificationsMessage.PermissionUpdated -> {
                    copy(isPermissionGranted = msg.granted)
                }
            }
    }
}
