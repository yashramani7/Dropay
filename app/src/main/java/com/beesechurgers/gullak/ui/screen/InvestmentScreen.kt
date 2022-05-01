package com.beesechurgers.gullak.ui.screen

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beesechurgers.gullak.R
import com.beesechurgers.gullak.ui.theme.GullakTheme
import com.beesechurgers.gullak.ui.theme.backgroundColor
import com.beesechurgers.gullak.ui.theme.googleSansFont
import com.beesechurgers.gullak.utils.DBConst
import com.beesechurgers.gullak.utils.DBListeners
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

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
            AnimatedVisibility(visible = DBListeners.pendingInvestedFunds.value != 0.0 || DBListeners.investedFunds.value != 0.0) {
                InvestmentContentScreen()
            }
            AnimatedVisibility(visible = DBListeners.pendingInvestedFunds.value == 0.0 && DBListeners.investedFunds.value == 0.0) {
                InvestmentEmptyScreen()
            }
        }
    }
}

@Composable
fun InvestmentEmptyScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Nothing Invested Yet :/",
                fontFamily = googleSansFont,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically),
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestmentContentScreen() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .animateContentSize()
    ) {
        AnimatedVisibility(visible = DBListeners.investedFunds.value > 0.0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), Arrangement.SpaceAround
            ) {
                Column(modifier = Modifier.padding(end = 72.dp)) {
                    Text(
                        text = "Invested Funds",
                        fontFamily = googleSansFont,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "₹ ${
                            String.format(
                                "%.2f",
                                DBListeners.investedFunds.value
                            )
                        }", fontFamily = googleSansFont, fontSize = 16.sp
                    )
                }
                Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                    Icon(
                        painter = if (DBListeners.investedFunds.value == 0.0) painterResource(id = R.drawable.ic_round_horizontal_rule_24) else painterResource(
                            id = R.drawable.ic_round_profit
                        ),
                        contentDescription = "",
                        tint = Color(0xff35b276),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "₹ +0.45",
                        fontFamily = googleSansFont,
                        fontSize = 16.sp,
                        color = Color(0xff35b276),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }

        var showDialog by remember { mutableStateOf(false) }
        AnimatedVisibility(visible = DBListeners.pendingInvestedFunds.value != 0.0) {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), onClick = {
                showDialog = true
            }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp), Arrangement.SpaceAround
                ) {
                    Column(modifier = Modifier.padding(end = 72.dp)) {
                        Text(
                            text = "Pending Funds",
                            fontFamily = googleSansFont,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "₹ ${
                                String.format(
                                    "%.2f",
                                    DBListeners.pendingInvestedFunds.value
                                )
                            }", fontFamily = googleSansFont, fontSize = 16.sp
                        )
                    }

                    Icon(
                        painter = painterResource(id = R.drawable.ic_round_timelapse_24),
                        contentDescription = "",
                        tint = Color(0xFFF5DD00),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }

        if (showDialog) {
            AlertDialog(onDismissRequest = { showDialog = false }, title = {
                Text(text = "Invest Funds ?")
            }, confirmButton = {
                Button(onClick = {
                    showDialog = false

                    val user = FirebaseAuth.getInstance().currentUser ?: return@Button
                    FirebaseDatabase.getInstance().reference.child(DBConst.DATA_KEY).child(user.uid).child(DBConst.INVESTMENTS_KEY)
                        .updateChildren(HashMap<String, Any>().apply {
                            this[DBConst.INVESTED_FUNDS_KEY] = DBListeners.investedFunds.value + DBListeners.pendingInvestedFunds.value
                            this[DBConst.PENDING_INVESTED_FUNDS] = 0.0
                        })
                }) {
                    Text(text = "Yes")
                }
            }, text = {
                Text(text = "Do you want to continue investing this fund ?")
            })
        }
    }
}
