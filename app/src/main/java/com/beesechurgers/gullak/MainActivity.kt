package com.beesechurgers.gullak

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.beesechurgers.gullak.ui.screen.HomeScreen
import com.beesechurgers.gullak.ui.screen.InvestmentScreen
import com.beesechurgers.gullak.ui.theme.*
import com.beesechurgers.gullak.utils.Prefs
import com.beesechurgers.gullak.utils.Prefs.getString
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private enum class Screen { HOME, INVESTMENT }

    private var selectedScreen = mutableStateOf(Screen.HOME)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            window.statusBarColor = backgroundColor().toArgb()
            MainScreen()
        }
    }

    @Preview(showBackground = true, showSystemUi = true, name = "MainActivity")
    @Composable
    fun MainScreen() {
        GullakTheme(this) {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = backgroundColor()),
                contentColor = contentColorFor(backgroundColor = backgroundColor()),
                topBar = { TopBar() },
                content = {
                    AnimatedVisibility(
                        visible = selectedScreen.value == Screen.HOME,
                        enter = fadeIn(tween(0)),
                        exit = fadeOut(tween(500))
                    ) {
                        HomeScreen(ctx = this@MainActivity)
                    }
                    AnimatedVisibility(
                        visible = selectedScreen.value == Screen.INVESTMENT,
                        enter = fadeIn(tween(0)),
                        exit = fadeOut(tween(500))
                    ) {
                        InvestmentScreen(ctx = this@MainActivity)
                    }
                },
                floatingActionButton = {
                    ExtendedFloatingActionButton(
                        onClick = {

                        },
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.wrapContentSize(),
                        icon = {
                            Icon(painter = painterResource(id = R.drawable.ic_round_qr_code_scanner_24), "")
                        },
                        text = {
                            Text(
                                text = "Scan QR",
                                fontFamily = googleSansFont,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        containerColor = fabColor(),
                        contentColor = contentColorFor(backgroundColor = fabColor())
                    )
                },
                isFloatingActionButtonDocked = true,
                floatingActionButtonPosition = FabPosition.Center,
                bottomBar = { BottomBar() })
        }
    }

    @Composable
    fun TopBar() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(Alignment.Top)
                .background(color = backgroundColor()),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Card(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 8.dp)
                    .clickable {
                        Toast
                            .makeText(this@MainActivity, "Settings", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, strokeColor(), RoundedCornerShape(8.dp)), shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings, contentDescription = "", modifier = Modifier.padding(8.dp),
                )
            }

            Image(
                painter = rememberImagePainter(
                    data = getString(
                        Prefs.USER_PROFILE_PIC_URL, "https://picsum.photos/200"
                    ),
                    builder = { transformations(CircleCropTransformation()) }
                ), contentDescription = "", modifier = Modifier
                    .size(56.dp)
                    .align(Alignment.CenterVertically)
                    .padding(8.dp)
                    .clip(CircleShape)
                    .clickable {
                        val auth = FirebaseAuth.getInstance()
                        if (auth.currentUser != null) {
                            auth.signOut()
                            startActivity(Intent(this@MainActivity, LoadingActivity::class.java))
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                            finish()
                        }
                    }
            )

            Card(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 8.dp)
                    .clickable {
                        Toast
                            .makeText(this@MainActivity, "Notifications", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, strokeColor(), RoundedCornerShape(8.dp)), shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Outlined.Notifications, contentDescription = "", modifier = Modifier.padding(8.dp))
            }
        }
    }

    @Composable
    fun BottomBar() {
        NavigationBar(
            containerColor = navigationBarColor(this),
            contentColor = contentColorFor(backgroundColor = navigationBarColor(this))
        ) {
            window.navigationBarColor = navigationBarColor(this@MainActivity).toArgb()
            window.statusBarColor = backgroundColor().toArgb()
            NavigationBarItem(
                icon = { Icon(if (selectedScreen.value == Screen.HOME) Icons.Filled.Home else Icons.Outlined.Home, "") },
                label = { Text(text = "Home", fontFamily = googleSansFont, fontSize = 14.sp, fontWeight = FontWeight.Bold) },
                selected = selectedScreen.value == Screen.HOME,
                onClick = { selectedScreen.value = Screen.HOME },
                alwaysShowLabel = true
            )

            NavigationBarItem(
                icon = { Icon(painter = painterResource(id = R.drawable.ic_round_trending_up_24), "") },
                label = { Text(text = "Investment", fontFamily = googleSansFont, fontSize = 14.sp, fontWeight = FontWeight.Bold) },
                selected = selectedScreen.value == Screen.INVESTMENT,
                onClick = { selectedScreen.value = Screen.INVESTMENT },
                alwaysShowLabel = true
            )
        }
    }
}
