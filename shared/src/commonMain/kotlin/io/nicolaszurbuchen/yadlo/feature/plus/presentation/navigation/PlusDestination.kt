package io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation

import androidx.navigation3.runtime.NavKey
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
 * *Nourriture & boissons* and *Créateurs* — one screen, reached by two keys.
 *
 * **Still one screen.** The stands differ by what they sell and by nothing else the screen does,
 * so there is one Route, one ViewModel and one Store behind both of these; which half to read is
 * a construction parameter, handed over in the entry below. Splitting the *screen* would be
 * splitting the label rather than the behaviour.
 *
 * **Two keys rather than one key with a value, because a NavKey is written down.** It was
 * `StandsDestination(kind: StandsKindUiModel)`, and a `@Serializable` NavKey argument is the
 * persisted format of the back stack: renaming that enum’s constants — an ordinary presentation
 * refactor, with nothing to warn you — changes what a restore after process death decodes. Every
 * other key in this file is a data object with nothing to break, and these two are now as well.
 * The kind still exists, it simply stops being something the system writes to disk.
 */
@Serializable
internal data object StandsFoodDestination : PlusDestination

@Serializable
internal data object StandsMakersDestination : PlusDestination

/** *Festival responsable* — the charters the association has signed. */
@Serializable
internal data object ResponsibleDestination : PlusDestination

@Serializable
internal data object PaymentDestination : PlusDestination

@Serializable
internal data object AccessDestination : PlusDestination

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
internal data object NotificationsDestination : PlusDestination

@Serializable
internal data object PrivacyDestination : PlusDestination

@Serializable
internal data object ClearDataDestination : PlusDestination
