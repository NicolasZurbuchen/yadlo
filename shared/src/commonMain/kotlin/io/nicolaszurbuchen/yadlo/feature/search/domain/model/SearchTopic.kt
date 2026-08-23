package io.nicolaszurbuchen.yadlo.feature.search.domain.model

/**
 * One practical answer the app holds — a screen behind a Plus row, and what somebody might type
 * looking for it.
 *
 * **The rows are indexed, not the prose inside them.** Matching a word buried three notes down a
 * payment page gives a result labelled *Paiement* with no honest way to show why it is there and
 * nowhere to scroll the reader to. A row plus its aliases is a dozen lines of vocabulary and it
 * answers the two queries story 9 names by name — `twint` and `parking`.
 *
 * [keywords] are matching vocabulary, never displayed: the title on the row comes from the same
 * string resource the Plus tab uses, so the two can never drift. That is also why French words sit
 * in a domain enum without breaking the resource rule — nothing here reaches a screen. They are
 * written unaccented and lowercase because the query is folded before it is compared; see
 * `foldForSearch`.
 *
 * The list includes the words of the title itself. *Horaires* has to find *Horaires d'ouverture*,
 * and reading the title back out of a resource to search it would mean the domain asking the
 * presentation layer what a screen is called.
 *
 * **What is deliberately not here: the dietary marks.** `vegan`, `sans-gluten` and the other four
 * are a closed set the content already models per dish, and somebody looking for vegan food wants
 * *every* vegan dish rather than a ranked guess. That is a filter on the stands list, and it is a
 * different feature — DECISIONS.md § Search is one corpus.
 */
enum class SearchTopic(
    val keywords: List<String>,
) {
    STANDS_FOOD(listOf("nourriture", "boissons", "manger", "boire", "restaurant", "food truck", "bar", "stand", "repas")),

    STANDS_MAKERS(listOf("createurs", "artisans", "artisanat", "boutique", "marche", "stand")),

    PAYMENT(listOf("paiement", "payer", "twint", "carte", "bancaire", "cash", "especes", "monnaie", "bancomat")),

    ACCESS(listOf("acces", "transports", "venir", "parking", "bus", "train", "gare", "velo", "voiture", "bateau")),

    HOURS(listOf("horaires", "ouverture", "fermeture", "heures", "ouvre", "ferme")),

    ASSISTANCE(listOf("besoin", "urgence", "secours", "samaritains", "objets perdus", "perdu", "sante", "police")),

    FAQ(listOf("questions", "frequentes", "faq", "entree", "gratuit", "chiens")),

    STORY(listOf("histoire", "association", "origine", "yadlo", "fondation")),

    RESPONSIBLE(listOf("responsable", "durable", "ecologie", "charte", "dechets", "tri", "environnement")),

    PARTNERS(listOf("partenaires", "sponsors", "soutiens", "communes")),

    VOLUNTEERING(listOf("benevole", "benevolat", "hotstaff", "staff", "aider", "rejoindre", "equipe")),

    CONTACT(listOf("contact", "ecrire", "email", "mail", "adresse", "joindre")),

    NOTIFICATIONS(listOf("notifications", "rappels", "alertes")),

    PRIVACY(listOf("confidentialite", "donnees", "vie privee", "rgpd")),

    ABOUT(listOf("a propos", "application", "version", "app")),
}
