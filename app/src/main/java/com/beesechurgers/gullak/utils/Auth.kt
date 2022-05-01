package com.beesechurgers.gullak.utils

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.MutableState
import com.beesechurgers.gullak.MainActivity
import com.beesechurgers.gullak.SetupPortfolioActivity
import com.beesechurgers.gullak.utils.Prefs.putString
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

object Auth {

    private const val TAG = "Auth"
    var activityCallback: (activityResult: ActivityResult) -> Unit = {}

    @ExperimentalAnimationApi
    fun ComponentActivity.oneTapGoogleSignIn(
        oneTapClient: SignInClient,
        request: BeginSignInRequest,
        isLoading: MutableState<Boolean>,
        launcher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        oneTapClient.beginSignIn(request).addOnSuccessListener(this) { result ->
            Log.d("oneTapGoogleSignIn", "Success")
            performAuthentication(oneTapClient, result, isLoading, launcher)
        }.addOnFailureListener(this) {
            isLoading.value = false
            Log.d("oneTapGoogleSignIn", "ERROR: ${it.localizedMessage}")
        }
    }

    @ExperimentalAnimationApi
    fun ComponentActivity.performAuthentication(
        oneTapClient: SignInClient,
        result: BeginSignInResult,
        isLoading: MutableState<Boolean>,
        launcher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        activityCallback = {
            if (it.resultCode == ComponentActivity.RESULT_OK) {
                try {
                    val intentCred = oneTapClient.getSignInCredentialFromIntent(it.data)
                    val tokenID = intentCred.googleIdToken
                    val credential = GoogleAuthProvider.getCredential(tokenID, null)

                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { result ->
                        if (result.isSuccessful) {
                            val profilePicUrl = intentCred.profilePictureUri
                            if (profilePicUrl != null) putString(Prefs.USER_PROFILE_PIC_URL, profilePicUrl.toString())
                            putString(Prefs.USER_NAME, intentCred.givenName ?: "Anonymous")
                            handleUserDB(isLoading, intentCred.givenName)
                        }
                    }
                } catch (e: ApiException) {
                    isLoading.value = false
                    if (e.statusCode == CommonStatusCodes.NETWORK_ERROR) {
                        Toast.makeText(this@performAuthentication, "Network Error", Toast.LENGTH_SHORT).show()
                    }
                    Log.d("performAuthentication", "ActivityResult: ${e.message}")
                }
            } else {
                isLoading.value = false
                Log.d("performAuthentication", "ActivityResult: result NOT_OKAY")
            }
            activityCallback = { }
        }
        launcher.launch(IntentSenderRequest.Builder(result.pendingIntent.intentSender).build())
    }

    private fun ComponentActivity.handleUserDB(isLoading: MutableState<Boolean>, name: String?) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "Couldn't create user, please try again", Toast.LENGTH_SHORT).show()
            isLoading.value = false
            return
        }

        FirebaseDatabase.getInstance().reference.child(DBConst.USER_KEY).get().addOnSuccessListener {
            if (it.hasChild(user.uid)) {
                isLoading.value = false
                Toast.makeText(this, "Welcome ${name ?: "Anonymous"} !", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
                return@addOnSuccessListener
            }

            FirebaseDatabase.getInstance().reference.child(DBConst.USER_KEY).updateChildren(HashMap<String, Any>().apply {
                this[user.uid] = "nun"
            }).addOnSuccessListener {
                Log.d(TAG, "handleUserDB: User added")
                FirebaseDatabase.getInstance().reference.child(DBConst.DATA_KEY).child(user.uid)
                    .updateChildren(HashMap<String, Any>().apply {
                        this[DBConst.WALLET_KEY] = -1.0
                        this[DBConst.PERCENTAGE_KEY] = 0f
                        this[DBConst.RISK_KEY] = 0
                        this[DBConst.INVESTMENTS_KEY] = HashMap<String, Any>().apply {
                            this[DBConst.INVESTED_FUNDS_KEY] = 0
                            this[DBConst.PENDING_INVESTED_FUNDS] = 0
                        }
                    }).addOnSuccessListener {
                        Log.d(TAG, "handleUserDB: Data added")
                        isLoading.value = false
                        Toast.makeText(this, "Welcome ${name ?: "Anonymous"} !", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, SetupPortfolioActivity::class.java))
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        finish()
                    }
            }
        }
    }
}