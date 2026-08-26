package io.nicolaszurbuchen.yadlo.feature.plus.di

import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ClearImageCacheUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ClearSavedUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveAssistanceGuideUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveContactRouterUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveFaqUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveOpeningDaysUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObservePartnerTiersUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObservePaymentUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObservePlusOverviewUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveResponsiblePageUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveSavedCountUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveStandDirectoryUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveStoryPageUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveTransportUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveVolunteeringOfferUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ReadImageCacheSizeUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.access.AccessStoreFactory
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.access.AccessViewModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.assistance.AssistanceStoreFactory
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.assistance.AssistanceViewModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.cleardata.ClearDataStoreFactory
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.cleardata.ClearDataViewModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.contact.ContactStoreFactory
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.contact.ContactViewModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.faq.FaqStoreFactory
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.faq.FaqViewModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.hours.HoursStoreFactory
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.hours.HoursViewModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.notifications.NotificationsStoreFactory
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.notifications.NotificationsViewModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.partners.PartnersStoreFactory
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.partners.PartnersViewModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.payment.PaymentStoreFactory
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.payment.PaymentViewModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus.PlusStoreFactory
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus.PlusViewModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.responsible.ResponsibleStoreFactory
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.responsible.ResponsibleViewModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.StandsKindUiModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.StandsStoreFactory
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.StandsViewModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.story.StoryStoreFactory
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.story.StoryViewModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.volunteering.VolunteeringStoreFactory
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.volunteering.VolunteeringViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val plusModule =
    module {
        factoryOf(::ObservePlusOverviewUseCase)
        factoryOf(::ObserveStandDirectoryUseCase)
        factoryOf(::ObservePaymentUseCase)
        factoryOf(::ObserveTransportUseCase)
        factoryOf(::ObserveOpeningDaysUseCase)
        factoryOf(::ObserveAssistanceGuideUseCase)
        factoryOf(::ObserveFaqUseCase)
        factoryOf(::ObserveStoryPageUseCase)
        factoryOf(::ObservePartnerTiersUseCase)
        factoryOf(::ObserveContactRouterUseCase)
        factoryOf(::ObserveResponsiblePageUseCase)
        factoryOf(::ObserveVolunteeringOfferUseCase)
        factoryOf(::ObserveSavedCountUseCase)
        factoryOf(::ClearSavedUseCase)
        factoryOf(::ReadImageCacheSizeUseCase)
        factoryOf(::ClearImageCacheUseCase)

        factoryOf(::PlusStoreFactory)
        viewModelOf(::PlusViewModel)

        factoryOf(::PaymentStoreFactory)
        viewModelOf(::PaymentViewModel)

        factoryOf(::AccessStoreFactory)
        viewModelOf(::AccessViewModel)

        factoryOf(::HoursStoreFactory)
        viewModelOf(::HoursViewModel)

        factoryOf(::AssistanceStoreFactory)
        viewModelOf(::AssistanceViewModel)

        factoryOf(::FaqStoreFactory)
        viewModelOf(::FaqViewModel)

        factoryOf(::StoryStoreFactory)
        viewModelOf(::StoryViewModel)

        factoryOf(::PartnersStoreFactory)
        viewModelOf(::PartnersViewModel)

        factoryOf(::ContactStoreFactory)
        viewModelOf(::ContactViewModel)

        factoryOf(::VolunteeringStoreFactory)
        viewModelOf(::VolunteeringViewModel)

        factoryOf(::ResponsibleStoreFactory)
        viewModelOf(::ResponsibleViewModel)

        factoryOf(::NotificationsStoreFactory)
        viewModelOf(::NotificationsViewModel)

        factoryOf(::ClearDataStoreFactory)
        viewModelOf(::ClearDataViewModel)

        // Parameterised rather than declared with viewModelOf: which half of the stands is being
        // read arrives from the NavKey, so the kind is a construction parameter rather than a
        // dependency. The same shape the fiche uses for its Happening id.
        viewModel { (kind: StandsKindUiModel) ->
            StandsViewModel(StandsStoreFactory(get(), get(), kind))
        }
    }
