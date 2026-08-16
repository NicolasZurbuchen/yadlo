package io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation

import androidx.navigation3.runtime.NavKey
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands.StandsKindUiModel
import kotlinx.serialization.Serializable

/**
 * Everywhere the Plus tab can go, in one file.
 *
 * A sealed root with its members beside it rather than a file each: fourteen one-line files say
 * nothing a reader could not see faster in one, and the hierarchy is what makes "this is the Plus
 * stack" a fact the compiler holds rather than a naming convention.
 */
@Serializable
sealed interface PlusDestination : NavKey

/** The tab root: the grouped list itself. */
@Serializable
internal data object PlusMainDestination : PlusDestination

/**
 * *Nourriture & boissons* or *Créateurs*. One screen, two entries — the stands differ by what they
 * sell and by nothing else the screen does, so splitting them into two files would be splitting the
 * label rather than the behaviour.
 */
@Serializable
internal data class StandsDestination(
    val kind: StandsKindUiModel,
) : PlusDestination

/** *Festival responsable* — the charters the association has signed. */
@Serializable
internal data object ResponsibleDestination : PlusDestination

@Serializable
internal data object PaymentDestination : PlusDestination

@Serializable
internal data object AccessDestination : PlusDestination

@Serializable
internal data object AccessibilityDestination : PlusDestination

@Serializable
internal data object HoursDestination : PlusDestination

@Serializable
internal data object AssistanceDestination : PlusDestination

@Serializable
internal data object FaqDestination : PlusDestination

@Serializable
internal data object StoryDestination : PlusDestination

@Serializable
internal data object PartnersDestination : PlusDestination

@Serializable
internal data object ContactDestination : PlusDestination

@Serializable
internal data object VolunteeringDestination : PlusDestination

@Serializable
internal data object AboutDestination : PlusDestination

@Serializable
internal data object PrivacyDestination : PlusDestination
