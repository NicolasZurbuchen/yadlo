package io.nicolaszurbuchen.yadlo.feature.happening.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * The fiche. Deliberately has no tab of its own: it is pushed onto whichever tab opened it, so
 * the same Happening reached from Programme goes back to Programme, and reached from Plus ›
 * Nourriture goes back to Nourriture.
 */
@Serializable
data class HappeningDestination(
    val happeningId: String,
) : NavKey
