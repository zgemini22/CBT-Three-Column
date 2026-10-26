package com.threecolumn.cbt.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.threecolumn.cbt.R
import com.threecolumn.cbt.ui.theme.NotebookColors
import com.threecolumn.cbt.util.SearchQuery

/** A simple always-visible search field, shared by the thought record and journal lists. */
@Composable
fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        placeholder = { Text(stringResource(R.string.search_hint), color = NotebookColors.inkFaded) },
        singleLine = true,
        // Results filter live as you type, so the keyboard's search key only has to get out of the way.
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = NotebookColors.line,
            focusedContainerColor = NotebookColors.paperAlt,
            unfocusedContainerColor = NotebookColors.paperAlt,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedLeadingIconColor = NotebookColors.inkFaded,
            unfocusedLeadingIconColor = NotebookColors.inkFaded
        ),
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.search_clear_desc))
                }
            }
        }
    )
}

/** "3 matches", shown under the search field while a search is active. */
@Composable
fun SearchResultCount(count: Int, modifier: Modifier = Modifier) {
    Text(
        text = LocalContext.current.resources.getQuantityString(R.plurals.search_result_count, count, count),
        style = MaterialTheme.typography.labelMedium,
        color = NotebookColors.inkFaded,
        modifier = modifier.padding(horizontal = 20.dp, vertical = 2.dp)
    )
}

/**
 * Dismisses the keyboard once the user starts scrolling the results: by then they're reading,
 * and the keyboard is only hiding part of the list.
 */
@Composable
fun DismissKeyboardOnScroll(listState: LazyListState) {
    val focusManager = LocalFocusManager.current
    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress) focusManager.clearFocus()
    }
}

/** The search highlighter: the same yellow as a real one on paper. */
@Composable
fun searchHighlightStyle(): SpanStyle =
    SpanStyle(background = NotebookColors.highlighter, color = NotebookColors.onHighlighter)

/** [text] with every match of [query] marked in [style]. */
fun highlightMatches(text: String, query: SearchQuery, style: SpanStyle): AnnotatedString =
    buildAnnotatedString {
        append(text)
        query.matchRanges(text).forEach { addStyle(style, it.first, it.last + 1) }
    }
