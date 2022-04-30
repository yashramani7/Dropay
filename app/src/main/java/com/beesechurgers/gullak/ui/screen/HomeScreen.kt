package com.beesechurgers.gullak.ui.screen

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beesechurgers.gullak.R
import com.beesechurgers.gullak.ui.theme.GullakTheme
import com.beesechurgers.gullak.ui.theme.backgroundColor
import com.beesechurgers.gullak.ui.theme.googleSansFont

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(ctx: Context) {
    val descList = listOf("Pay to\nUPI ID", "Bank Transfer", "Pay contacts")
    val iconList = listOf(R.drawable.ic_upi_transfer, R.drawable.ic_round_account_balance_24, R.drawable.ic_round_account_circle_24)

    GullakTheme(ctx) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(color = backgroundColor()),
            color = backgroundColor(),
            contentColor = contentColorFor(backgroundColor = backgroundColor()),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Setup Wallet",
                    fontFamily = googleSansFont,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(56.dp)
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 56.dp), horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    items(descList.size, itemContent = {
                        Card(modifier = if (it == descList.size - 1) Modifier
                            .width(100.dp)
                            .height(100.dp) else Modifier
                            .padding(end = 8.dp)
                            .width(100.dp)
                            .height(100.dp), onClick = {

                        }) {
                            Image(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(top = 8.dp),
                                painter = painterResource(id = iconList[it]),
                                contentDescription = ""
                            )
                            Text(
                                text = descList[it],
                                modifier = Modifier
                                    .padding(8.dp)
                                    .align(Alignment.CenterHorizontally),
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                        }
                    })
                }
            }
        }
    }
}
