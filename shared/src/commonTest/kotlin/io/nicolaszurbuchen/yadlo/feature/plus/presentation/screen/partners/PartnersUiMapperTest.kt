package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.partners

import io.nicolaszurbuchen.yadlo.core.content.domain.model.Partner
import io.nicolaszurbuchen.yadlo.core.content.domain.model.PartnerTier
import io.nicolaszurbuchen.yadlo.core.content.domain.model.Provenance
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.partners_empty
import yadlo.shared.generated.resources.partners_no_website
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PartnersUiMapperTest {
    @Test
    fun toUiModel_beforeAnythingIsRead_isLoading() {
        assertTrue(PartnersState().toUiModel().isLoading)
    }

    @Test
    fun toUiModel_readAndEmpty_saysSo() {
        assertEquals(
            UiText.Resource(Res.string.partners_empty),
            PartnersState(tiers = emptyList()).toUiModel().emptyMessage,
        )
    }

    @Test
    fun toUiModel_tiersKeepTheOrderTheDomainSortedThemInto() {
        val model = PartnersState(tiers = tiers()).toUiModel()

        assertEquals(listOf("sponsors", "cygnes-or"), model.tiers.map { it.id })
    }

    @Test
    fun toUiModel_aPartnerWithNoWebsite_keepsItsNullUrl() {
        val member = PartnersState(tiers = tiers()).toUiModel().tiers.last().members.last()

        // The screen needs to be able to tell "no site" from "not linked yet", because tapping the
        // first has to say something and tapping the second is a bug.
        assertNull(member.url)
        assertEquals("Edifice", member.name)
    }

    @Test
    fun toUiModel_beforeAnyTap_thereIsNoNotice() {
        assertNull(PartnersState(tiers = tiers()).toUiModel().noWebsiteNotice)
    }

    @Test
    fun toUiModel_afterATapOnAPartnerWithNoSite_thereIsAMessage() {
        val notice = PartnersState(tiers = tiers(), noWebsiteTaps = 1).toUiModel().noWebsiteNotice

        assertEquals(UiText.Resource(Res.string.partners_no_website), notice?.message)
    }

    @Test
    fun toUiModel_asecondTap_changesTheTokenSoTheScreenSaysItAgain() {
        val first = PartnersState(tiers = tiers(), noWebsiteTaps = 1).toUiModel().noWebsiteNotice
        val second = PartnersState(tiers = tiers(), noWebsiteTaps = 2).toUiModel().noWebsiteNotice

        // The whole reason this is a counter rather than a boolean: tapping twice must say it
        // twice, and a Label would have made that untestable.
        assertEquals(1, first?.token)
        assertEquals(2, second?.token)
    }

    @Test
    fun toUiModel_aTapBeforeTheContentLands_isStillAnnounced() {
        // Not reachable by a real tap, and it costs nothing to be right about: the notice does not
        // depend on the list having arrived.
        assertEquals(1, PartnersState(noWebsiteTaps = 1).toUiModel().noWebsiteNotice?.token)
    }

    private fun tiers() =
        listOf(
            PartnerTier(
                id = "sponsors",
                name = "Sponsors généraux",
                order = 1,
                provenance = Provenance.CONFIRMED,
                members = listOf(Partner(id = "mbc", name = "MBC", url = "https://mbc.ch", logo = null)),
            ),
            PartnerTier(
                id = "cygnes-or",
                name = "Cygnes d'or",
                order = 2,
                provenance = Provenance.CONFIRMED,
                members =
                    listOf(
                        Partner(id = "totem", name = "Totem Escalade", url = "https://totem.ch", logo = null),
                        Partner(id = "edifice", name = "Edifice", url = null, logo = null),
                    ),
            ),
        )
}
