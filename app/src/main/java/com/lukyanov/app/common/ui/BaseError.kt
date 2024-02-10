package com.lukyanov.app.common.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lukyanov.app.R

@Composable
fun GenericError(
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        BaseError(
            text = stringResource(R.string.error_generic),
            buttonText = stringResource(id = R.string.action_repeat),
            onButtonClick = onButtonClick,
            modifier = modifier,
        )
    }
}

@Composable
fun BaseError(
    text: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.ic_no_connection),
            contentDescription = null,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = text, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.primary)

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = onButtonClick,
            modifier = Modifier.heightIn(min = 45.dp),
        ) {
            Text(text = buttonText)
        }
    }
}

@Preview
@Composable
private fun BaseErrorPreview() {
    PreviewWrapper {
        GenericError(onButtonClick = {}, modifier = Modifier.padding(20.dp))
    }
}