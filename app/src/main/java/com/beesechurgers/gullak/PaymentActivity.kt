package com.beesechurgers.gullak

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beesechurgers.gullak.ui.theme.GullakTheme
import com.beesechurgers.gullak.ui.theme.backgroundColor
import com.beesechurgers.gullak.ui.theme.googleSansFont
import com.beesechurgers.gullak.ui.theme.monoFont
import com.beesechurgers.gullak.utils.DBConst
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
class PaymentActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                            Text(
                                text = "Setup Wallet",
                                fontFamily = googleSansFont,
                                fontSize = 28.sp,
                                modifier = Modifier.padding(start = 40.dp, top = 40.dp)
                            )
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
                                        text = "Enter the amount to fill in Wallet",
                                        fontFamily = googleSansFont,
                                        fontSize = 20.sp,
                                        modifier = Modifier
                                            .padding(bottom = 24.dp)
                                            .align(Alignment.CenterHorizontally)
                                    )

                                    var fieldEnabled by rememberSaveable { mutableStateOf(true) }
                                    var amount by rememberSaveable { mutableStateOf("") }
                                    val upiLauncher = rememberLauncherForActivityResult(
                                        contract = ActivityResultContracts.StartActivityForResult(),
                                        onResult = {
                                            val user = FirebaseAuth.getInstance().currentUser ?: return@rememberLauncherForActivityResult
                                            val intent = it.data ?: return@rememberLauncherForActivityResult

                                            val extra = intent.extras ?: return@rememberLauncherForActivityResult
                                            if (!extra.getBoolean("success", false)) {
                                                Toast.makeText(this@PaymentActivity, "Top up Failed", Toast.LENGTH_SHORT).show()
                                                startActivity(
                                                    Intent(
                                                        this@PaymentActivity,
                                                        MainActivity::class.java
                                                    ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                                )
                                                return@rememberLauncherForActivityResult
                                            }

                                            fieldEnabled = false
                                            Handler(mainLooper).postDelayed({
                                                FirebaseDatabase.getInstance().reference.child(DBConst.DATA_KEY).child(user.uid)
                                                    .updateChildren(HashMap<String, Any>().apply {
                                                        this[DBConst.WALLET_KEY] = amount.toDouble()
                                                    }).addOnSuccessListener {
                                                        Toast.makeText(this@PaymentActivity, "Top up Success", Toast.LENGTH_SHORT).show()
                                                        startActivity(
                                                            Intent(
                                                                this@PaymentActivity,
                                                                MainActivity::class.java
                                                            ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                                        )
                                                    }
                                            }, 1000)
                                        }
                                    )
                                    OutlinedTextField(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentHeight()
                                            .padding(start = 24.dp, end = 24.dp),
                                        value = amount,
                                        onValueChange = { amount = it },
                                        label = { Text(text = "Amount", fontFamily = googleSansFont) },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                        shape = RoundedCornerShape(24.dp),
                                        textStyle = TextStyle.Default.copy(fontFamily = monoFont, fontSize = 24.sp),
                                        enabled = fieldEnabled,
                                        leadingIcon = {
                                            Icon(painter = painterResource(id = R.drawable.ic_rupee), contentDescription = "")
                                        }, trailingIcon = {
                                            IconButton(onClick = {
                                                if (amount.isEmpty()) return@IconButton
                                                if (amount.endsWith(".")) return@IconButton

                                                val dec = amount.split(".")
                                                if (dec.size > 2) return@IconButton
                                                if (dec.size >= 2 && dec[1].length > 2) return@IconButton

                                                upiLauncher.launch(
                                                    Intent(
                                                        this@PaymentActivity,
                                                        DummyUPIScreen::class.java
                                                    ).putExtra("_to", "Gullak Wallet")
                                                        .putExtra("_amount", amount.toDouble())
                                                )
                                            }) {
                                                Icon(Icons.Rounded.ArrowForward, contentDescription = "")
                                            }
                                        }
                                    )

                                    AnimatedVisibility(
                                        visible = !fieldEnabled,
                                        enter = fadeIn(),
                                        exit = fadeOut(),
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .align(Alignment.CenterHorizontally)
                                    ) {
                                        LocalFocusManager.current.clearFocus()
                                        CircularProgressIndicator()
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