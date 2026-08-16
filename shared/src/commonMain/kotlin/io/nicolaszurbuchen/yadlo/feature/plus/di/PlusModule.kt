package io.nicolaszurbuchen.yadlo.feature.plus.di

import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveAccessibilityGuideUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveAssistanceGuideUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveContactRouterUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveFaqUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveOpeningDaysUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObservePartnerTiersUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObservePaymentUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObservePlusOverviewUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObservePlusPageUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveStandDirectoryUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveStoryPageUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveTransportUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.domain.usecase.ObserveVolunteeringOfferUseCase
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.access.AccessStoreFactory
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.access.AccessViewModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.accessibility.AccessibilityStoreFactory
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.accessibility.AccessibilityViewModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.assistance.AssistanceStoreFactory
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.assistance.AssistanceViewModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.contact.ContactStoreFactory
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.contact.ContactViewModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.faq.FaqStoreFactory
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.faq.FaqViewModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.hours.HoursStoreFactory
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.hours.HoursViewModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.page.PageKindUiModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.page.PageStoreFactory
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.page.PageViewModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.partners.PartnersStoreFactory
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.partners.PartnersViewModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.payment.PaymentStoreFactory
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.payment.PaymentViewModel
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus.PlusStoreFactory
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.plus.PlusViewModel
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
        factoryOf(::ObserveAccessibilityGuideUseCase)
        factoryOf(::ObserveOpeningDaysUseCase)
        factoryOf(::ObserveAssistanceGuideUseCase)
        factoryOf(::ObserveFaqUseCase)
        factoryOf(::ObserveStoryPageUseCase)
        factoryOf(::ObservePartnerTiersUseCase)
        factoryOf(::ObserveContactRouterUseCase)
        factoryOf(::ObservePlusPageUseCase)
        factoryOf(::ObserveVolunteeringOfferUseCase)

        factoryOf(::PlusStoreFactory)
        factoryOf(::PaymentStoreFactory)
        factoryOf(::AccessStoreFactory)
        factoryOf(::AccessibilityStoreFactory)
        factoryOf(::HoursStoreFactory)
        factoryOf(::AssistanceStoreFactory)
        factoryOf(::FaqStoreFactory)
        factoryOf(::StoryStoreFactory)
        factoryOf(::PartnersStoreFactory)
        factoryOf(::ContactStoreFactory)
        factoryOf(::VolunteeringStoreFactory)

        viewModelOf(::PlusViewModel)
        viewModelOf(::PaymentViewModel)
        viewModelOf(::AccessViewModel)
        viewModelOf(::AccessibilityViewModel)
        viewModelOf(::HoursViewModel)
        viewModelOf(::AssistanceViewModel)
        viewModelOf(::FaqViewModel)
        viewModelOf(::StoryViewModel)
        viewModelOf(::PartnersViewModel)
        viewModelOf(::ContactViewModel)
        viewModelOf(::VolunteeringViewModel)

        // Parameterised rather than declared with viewModelOf: which page — and which half of the
        // stands — is being read arrives from the NavKey, so the kind is a construction parameter
        // rather than a dependency. The same shape the fiche uses for its Happening id.
        viewModel { (kind: PageKindUiModel) ->
            PageViewModel(PageStoreFactory(get(), get(), kind))
        }

        viewModel { (kind: StandsKindUiModel) ->
            StandsViewModel(StandsStoreFactory(get(), get(), kind))
        }
    }
