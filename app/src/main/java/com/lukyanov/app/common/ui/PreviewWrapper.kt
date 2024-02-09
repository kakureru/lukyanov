package com.lukyanov.app.common.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.lukyanov.app.common.ui.theme.LukyanovTheme

@Composable
fun PreviewWrapper(
    content: @Composable () -> Unit,
) {
    LukyanovTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            content()
        }
    }
}