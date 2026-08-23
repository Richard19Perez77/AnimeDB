package com.rick.animedb.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink

private val UrlRegex = Regex(
    """((?:https?://|www\.)[^\s<>()\[\]"'<>]+)""",
    RegexOption.IGNORE_CASE,
)

@Composable
fun LinkifiedText(
    text: String,
    modifier: Modifier = Modifier,
    url: String? = null,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
) {
    val linkColor = MaterialTheme.colorScheme.primary
    val annotated = remember(text, url, linkColor) {
        buildLinkifiedString(text, url, linkColor)
    }
    Text(
        text = annotated,
        modifier = modifier,
        style = style,
    )
}

internal fun buildLinkifiedString(
    text: String,
    explicitUrl: String?,
    linkColor: Color,
): androidx.compose.ui.text.AnnotatedString {
    val linkStyles = TextLinkStyles(
        style = SpanStyle(
            color = linkColor,
            textDecoration = TextDecoration.Underline,
        ),
    )
    val matches = UrlRegex.findAll(text).mapNotNull { match ->
        val raw = match.value.trimEnd('.', ',', ';', ':', ')', ']', '!', '?')
        if (raw.isBlank()) return@mapNotNull null
        val url = if (raw.startsWith("www.", ignoreCase = true)) "https://$raw" else raw
        (match.range.first until match.range.first + raw.length) to url
    }.toList()

    return buildAnnotatedString {
        if (matches.isEmpty()) {
            val fallbackUrl = explicitUrl?.takeIf(::isHttpUrl)
            if (fallbackUrl != null) {
                withLink(LinkAnnotation.Url(fallbackUrl, linkStyles)) {
                    append(text)
                }
            } else {
                append(text)
            }
            return@buildAnnotatedString
        }

        var cursor = 0
        matches.forEach { (range, url) ->
            if (range.first > cursor) {
                append(text.substring(cursor, range.first))
            }
            withLink(LinkAnnotation.Url(url, linkStyles)) {
                append(text.substring(range))
            }
            cursor = range.last + 1
        }
        if (cursor < text.length) {
            append(text.substring(cursor))
        }
    }
}

private fun isHttpUrl(value: String): Boolean =
    value.startsWith("https://", ignoreCase = true) ||
        value.startsWith("http://", ignoreCase = true)
