package com.beesechurgers.gullak

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Switch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beesechurgers.gullak.ui.theme.GullakTheme
import com.beesechurgers.gullak.ui.theme.backgroundColor
import com.beesechurgers.gullak.ui.theme.googleSansFont
import com.beesechurgers.gullak.utils.DBConst
import com.beesechurgers.gullak.utils.DBListeners
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
class InvestmentTransactionActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var paymentAmount = ""

        val data = intent.extras
        if (data != null) {
            paymentAmount = data.getString("_amount", "")
        }

        val percentInvest = mutableStateOf(0f)
        val user = FirebaseAuth.getInstance().currentUser ?: return
        FirebaseDatabase.getInstance().reference.child(DBConst.DATA_KEY).child(user.uid).get().addOnSuccessListener {
            val rd = it.child(DBConst.PERCENTAGE_KEY).value
            if (rd != null) percentInvest.value = rd.toString().toFloatOrNull() ?: 0f
        }

        setContent {
            window.statusBarColor = backgroundColor().toArgb()

            GullakTheme(context = this) {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = backgroundColor()),
                    contentColor = contentColorFor(backgroundColor = backgroundColor()),
                    topBar = {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = backgroundColor())
                                .animateContentSize(),
                            color = backgroundColor(),
                            contentColor = contentColorFor(backgroundColor = backgroundColor()),
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Wallet Payment",
                                    fontFamily = googleSansFont,
                                    fontSize = 28.sp,
                                    modifier = Modifier
                                        .padding(top = 40.dp)
                                        .align(Alignment.CenterHorizontally),
                                    lineHeight = 36.sp
                                )
                            }
                        }
                    },
                    content = {
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color = backgroundColor())
                                .animateContentSize(),
                            color = backgroundColor(),
                            contentColor = contentColorFor(backgroundColor = backgroundColor()),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.CenterVertically)
                                ) {
                                    Text(
                                        text = "Amount to be paid",
                                        fontFamily = googleSansFont,
                                        fontSize = 20.sp,
                                        modifier = Modifier
                                            .padding(bottom = 16.dp)
                                            .align(Alignment.CenterHorizontally)
                                    )

                                    var investChecked by remember { mutableStateOf(true) }
                                    Text(
                                        text = "₹ ${
                                            if (investChecked) {
                                                String.format(
                                                    "%.2f",
                                                    paymentAmount.toFloat() + ((percentInvest.value / 100f) * paymentAmount.toFloat())
                                                )
                                            } else {
                                                paymentAmount
                                            }
                                        }",
                                        fontFamily = googleSansFont,
                                        fontSize = 42.sp,
                                        modifier = Modifier
                                            .padding(56.dp)
                                            .align(Alignment.CenterHorizontally),
                                        lineHeight = 36.sp
                                    )

                                    AnimatedVisibility(visible = percentInvest.value != 0f) {
                                        Card(modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 24.dp, start = 24.dp, end = 24.dp),
                                            shape = RoundedCornerShape(24.dp),
                                            onClick = {
                                                investChecked = !investChecked
                                            }
                                        ) {
                                            Row(modifier = Modifier.fillMaxWidth(), Arrangement.SpaceAround) {
                                                Text(
                                                    text = "${percentInvest.value}% of ₹ $paymentAmount to be invested",
                                                    fontFamily = googleSansFont,
                                                    fontSize = 16.sp,
                                                    modifier = Modifier
                                                        .align(Alignment.CenterVertically)
                                                        .padding(start = 8.dp)
                                                )

                                                Switch(
                                                    checked = investChecked, onCheckedChange = {
                                                        investChecked = it
                                                    }, modifier = Modifier
                                                        .align(Alignment.CenterVertically)
                                                )
                                            }
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            if (investChecked) {
                                                val deductAmount =
                                                    paymentAmount.toFloat() + ((percentInvest.value / 100f) * paymentAmount.toFloat())
                                                if (DBListeners.walletBalance.value - deductAmount.toDouble() < 0.0) {
                                                    Toast.makeText(
                                                        this@InvestmentTransactionActivity,
                                                        "Insufficient Balance",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    return@OutlinedButton
                                                }
                                                FirebaseDatabase.getInstance().reference.child(DBConst.DATA_KEY).child(user.uid)
                                                    .updateChildren(HashMap<String, Any>().apply {
                                                        this[DBConst.WALLET_KEY] = DBListeners.walletBalance.value - deductAmount
                                                        this[DBConst.INVESTMENTS_KEY] = HashMap<String, Any>().apply {
                                                            this[DBConst.TOTAL_INVEST_KEY] = DBListeners.investmentCount.value + 1
                                                        }
                                                    })
                                                Toast.makeText(this@InvestmentTransactionActivity, "Payment Success", Toast.LENGTH_SHORT)
                                                    .show()
                                                startActivity(
                                                    Intent(
                                                        this@InvestmentTransactionActivity,
                                                        MainActivity::class.java
                                                    ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                                )
                                            } else {
                                                val deductAmount = paymentAmount.toDouble()
                                                if (DBListeners.walletBalance.value - deductAmount < 0.0) {
                                                    Toast.makeText(
                                                        this@InvestmentTransactionActivity,
                                                        "Insufficient Balance",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    return@OutlinedButton
                                                }
                                                FirebaseDatabase.getInstance().reference.child(DBConst.DATA_KEY).child(user.uid)
                                                    .updateChildren(HashMap<String, Any>().apply {
                                                        this[DBConst.WALLET_KEY] = DBListeners.walletBalance.value - deductAmount
                                                    })
                                                Toast.makeText(this@InvestmentTransactionActivity, "Payment Success", Toast.LENGTH_SHORT)
                                                    .show()
                                                startActivity(
                                                    Intent(
                                                        this@InvestmentTransactionActivity,
                                                        MainActivity::class.java
                                                    ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                                )
                                            }
                                        }, modifier = Modifier
                                            .align(Alignment.End)
                                            .padding(top = 24.dp, end = 24.dp)
                                    ) {
                                        Icon(Icons.Rounded.ArrowForward, contentDescription = "")
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}