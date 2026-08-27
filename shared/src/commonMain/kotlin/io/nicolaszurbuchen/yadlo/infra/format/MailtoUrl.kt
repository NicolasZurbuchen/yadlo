package io.nicolaszurbuchen.yadlo.infra.format

/**
 * An address, as the URI a platform's mail app answers to.
 *
 * Five screens hand an address to `openUri` and every one of them was prefixing it with its own
 * `private const val MAIL_SCHEME`. One copy is a constant; five is a convention held by nothing,
 * and the day one of them needs a subject line the other four would silently keep the old shape.
 *
 * No subject and no body. Prefilling either means percent-encoding a French sentence, which the
 * platforms disagree about often enough that the safe version is the one with nothing in it.
 */
fun mailtoUrl(address: String): String = "$MAIL_SCHEME$address"

private const val MAIL_SCHEME = "mailto:"
