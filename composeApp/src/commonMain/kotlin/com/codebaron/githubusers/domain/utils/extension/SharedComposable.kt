package com.codebaron.githubusers.domain.utils.extension

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.codebaron.githubusers.presentation.theme.LightTextPrimary
import com.codebaron.githubusers.presentation.theme.LocalIsDarkTheme

@Composable
fun CustomLoadingDialog(
    showDialog: Boolean,
    message: String
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = { /* Prevent dialog dismissal */ }) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
                content = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize(),
                        content = {
                            CircularProgressIndicator(
                                color = if (LocalIsDarkTheme.current) Color.White else MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = message,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = if (LocalIsDarkTheme.current) Color.White else LightTextPrimary
                            )
                        }
                    )
                }
            )
        }
    }
}