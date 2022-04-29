package com.beesechurgers.gullak.ui.theme

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource

@Composable
fun backgroundColor() =
    colorResource(id = if (isSystemInDarkTheme()) android.R.color.system_neutral1_900 else android.R.color.system_neutral1_10)

@Composable
fun strokeColor(): Color =
    if (isSystemInDarkTheme()) Color(0xFF444444) else Color(0xFFDADADA)

@Composable
fun navigationBarColor(ctx: Context) =
    if (isSystemInDarkTheme()) dynamicDarkColorScheme(ctx).secondaryContainer.copy(alpha = 0.31f)
    else dynamicLightColorScheme(ctx).secondaryContainer.copy(alpha = 0.31f)

@Composable
fun fabColor() =
    colorResource(id = if (isSystemInDarkTheme()) android.R.color.system_accent1_200 else android.R.color.system_accent1_600)
