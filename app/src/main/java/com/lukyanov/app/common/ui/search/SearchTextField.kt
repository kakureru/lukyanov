package com.lukyanov.app.common.ui.search

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.lukyanov.app.R
import com.lukyanov.app.common.ui.PreviewWrapper

@Composable
fun SearchTextField(
    query: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val textStyle = TextStyle(fontSize = 20.sp)
    TextField(
        value = query,
        onValueChange = onSearchQueryChange,
        modifier = modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Unspecified,
            unfocusedContainerColor = Color.Unspecified,
            unfocusedIndicatorColor = Color.Unspecified,
            focusedIndicatorColor = Color.Unspecified,
        ),
        placeholder = {
            Text(
                text = stringResource(id = R.string.search),
                style = textStyle,
            )
        },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Search,
        ),
        singleLine = true,
        textStyle = textStyle,
    )
}

@Preview
@Composable
private fun SearchTextFieldPrev() {
    PreviewWrapper {
        SearchTextField(query = "", onSearchQueryChange = {})
    }
}