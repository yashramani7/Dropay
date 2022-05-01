package com.beesechurgers.gullak

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beesechurgers.gullak.ui.theme.GullakTheme
import com.beesechurgers.gullak.ui.theme.backgroundColor
import com.beesechurgers.gullak.ui.theme.googleSansFont
import com.beesechurgers.gullak.ui.theme.monoFont
import com.beesechurgers.gullak.utils.DBConst
import com.beesechurgers.gullak.utils.DBListeners
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
class SetupPortfolioActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            window.statusBarColor = backgroundColor().toArgb()

            val riskList = listOf("Low Risk\nLow Return", "Intermediate Risk\nIntermediate Return", "High Risk\nHigh Return")

            GullakTheme(this) {
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
                            Text(
                                text = "Setup Profile",
                                fontSize = 24.sp,
                                fontFamily = googleSansFont,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, top = 32.dp)
                            )
                        }
                    },
                    content = {
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color = backgroundColor())
                                .animateContentSize()
                                .verticalScroll(rememberScrollState()),
                            color = backgroundColor(),
                            contentColor = contentColorFor(backgroundColor = backgroundColor()),
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "You need to select some preferences to get started.",
                                    fontFamily = googleSansFont,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(top = 18.dp)
                                )

                                Text(
                                    text = "Risk Factor",
                                    fontFamily = googleSansFont,
                                    fontSize = 20.sp,
                                    modifier = Modifier
                                        .padding(top = 56.dp)
                                )
                                Text(
                                    text = "Select the risk factor you can sustain",
                                    fontFamily = googleSansFont,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(top = 8.dp)
                                )

                                var sliderPosition by remember { mutableStateOf(DBListeners.riskFactor.value * 50f) }
                                Slider(
                                    value = sliderPosition,
                                    onValueChange = { sliderPosition = it },
                                    valueRange = 0f..100f,
                                    steps = 1
                                )

                                Text(
                                    text = riskList[(sliderPosition / 50f).toInt()],
                                    fontFamily = googleSansFont,
                                    fontSize = 16.sp,
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally),
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    text = "Investment Percentage Amount",
                                    fontFamily = googleSansFont,
                                    fontSize = 20.sp,
                                    modifier = Modifier
                                        .padding(top = 56.dp)
                                )
                                Text(
                                    text = "Enter the percentage to be invested from every transaction",
                                    fontFamily = googleSansFont,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(top = 8.dp)
                                )

                                val bringInView = BringIntoViewRequester()
                                val coroutineScope = rememberCoroutineScope()
                                var percentage by rememberSaveable { mutableStateOf(DBListeners.percentageInvest.value.toString()) }
                                OutlinedTextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .padding(top = 16.dp)
                                        .onFocusEvent {
                                            if (it.isFocused) {
                                                coroutineScope.launch {
                                                    delay(200)
                                                    bringInView.bringIntoView()
                                                }
                                            }
                                        }
                                        .bringIntoViewRequester(bringInView),
                                    value = percentage,
                                    onValueChange = { percentage = it.replace(",", "") },
                                    label = { Text(text = "Percentage", fontFamily = googleSansFont) },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(24.dp),
                                    textStyle = TextStyle.Default.copy(fontFamily = monoFont, fontSize = 24.sp)
                                )

                                OutlinedButton(
                                    onClick = {
                                        val user = FirebaseAuth.getInstance().currentUser ?: return@OutlinedButton
                                        FirebaseDatabase.getInstance().reference.child(DBConst.DATA_KEY).child(user.uid)
                                            .updateChildren(HashMap<String, Any>().apply {
                                                this[DBConst.PERCENTAGE_KEY] = percentage.toFloatOrNull() ?: 0f
                                                this[DBConst.RISK_KEY] = (sliderPosition / 50f).toInt()
                                            })

                                        startActivity(Intent(this@SetupPortfolioActivity, MainActivity::class.java))
                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                                        finish()
                                    }, modifier = Modifier
                                        .align(Alignment.End)
                                        .padding(top = 16.dp)
                                ) {
                                    Text(text = "Done", fontFamily = googleSansFont)
                                    Icon(Icons.Rounded.ArrowForward, contentDescription = "", modifier = Modifier.padding(start = 8.dp))
                                }
                            }
                        }
                    })
            }
        }
    }
}