package com.beesechurgers.gullak.ui.screen

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.beesechurgers.gullak.ui.theme.GullakTheme
import com.beesechurgers.gullak.ui.theme.backgroundColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestmentScreen(ctx: Context) {
    GullakTheme(ctx) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(color = backgroundColor()),
            color = backgroundColor(),
            contentColor = contentColorFor(backgroundColor = backgroundColor()),
        ) {
            Text(text = "Investment Screen")
        }
    }
}
