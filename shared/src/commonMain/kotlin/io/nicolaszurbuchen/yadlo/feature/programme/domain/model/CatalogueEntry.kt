package io.nicolaszurbuchen.yadlo.feature.programme.domain.model

/**
 * One thing the festival offers, with no hour on it anywhere — the Catalogue's unit.
 *
 * **This is a Happening, where [ProgrammeSlot] is an occurrence of one.** The Programme answers
 * "what is on at four o'clock" and so it is built from Slots; the Catalogue answers "what is there
 * to do here at all", which is a question about the thing rather than about when it runs. SUP Yoga
 * is one entry here and six Slots over two days there, and neither number is wrong.
 *
 * That is also why it is read off `Edition.happenings` rather than folded out of the Slots: an
 * entry is not a deduplicated row, it is the record the Slots point at, and deriving it from them
 * would make the Catalogue quietly depend on a timetable it is defined to ignore.
 *
 * **Stands are not in it.** They are browsed in *Plus › Nourriture & boissons* and *Créateurs*,
 * with the same card this screen draws — a second door onto the same eight stalls is the one thing
 * "one place to browse a thing, one place to see what you kept" actually forbids.
 *
 * [genres] is the fact a Programme row has no width for and the reason the card is worth its
 * height: *Techno-house* is what says whether a name on a poster is worth walking towards, and
 * every one of the edition's Artists declares it. Most Activities declare none, which is why the
 * band it is drawn in is absent rather than empty.
 */
data class CatalogueEntry(
    val id: String,
    val name: String,
    val categoryId: String,
    val categoryName: String,
    val description: String?,
    val imageUrl: String?,
    val genres: List<String>,
)
