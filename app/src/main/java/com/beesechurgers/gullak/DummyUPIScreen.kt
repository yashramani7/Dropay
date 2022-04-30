package com.beesechurgers.gullak

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beesechurgers.gullak.ui.theme.backgroundColor
import com.beesechurgers.gullak.ui.theme.googleSansFont
import com.beesechurgers.gullak.ui.theme.monoFont
import com.beesechurgers.gullak.utils.DBConst
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DummyUPIScreen : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var amount = 0.0
        var name = ""

        val data = intent.extras
        if (data != null) {
            amount = data.getDouble("_amount", 0.0)
            name = data.getString("_to", "")
        }

        setContent {
            window.statusBarColor = backgroundColor().toArgb()

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .animateContentSize(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Sample UPI Payment Screen",
                        fontFamily = googleSansFont,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(top = 56.dp)
                    )

                    Row {
                        Text(
                            text = "Paying ",
                            fontFamily = googleSansFont,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(top = 48.dp)
                        )
                        Text(
                            text = " $amount",
                            fontFamily = monoFont,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(top = 48.dp),
                            fontWeight = FontWeight.Bold,
                            color = Color(0xff35b276)
                        )
                    }
                    Row {
                        Text(text = "To ", fontFamily = googleSansFont, fontSize = 20.sp)
                        Text(
                            text = " $name",
                            fontFamily = monoFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xff35b276)
                        )
                    }

                    var pin by rememberSaveable { mutableStateOf("") }
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(top = 64.dp),
                        value = pin,
                        onValueChange = { pin = it },
                        label = { Text(text = "Pin", fontFamily = googleSansFont) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(24.dp),
                        textStyle = TextStyle.Default.copy(fontFamily = monoFont, fontSize = 24.sp, letterSpacing = 16.sp),
                        visualTransformation = PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = {
                                if (pin.isEmpty()) return@IconButton

                                setResult(0, Intent().putExtra("success", true))
                                finish()
                                val user = FirebaseAuth.getInstance().currentUser ?: return@IconButton


                            }) {
                                Icon(Icons.Rounded.ArrowForward, contentDescription = "")
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onBackPressed() {
        setResult(0, Intent().putExtra("success", false))
        finish()
    }
}