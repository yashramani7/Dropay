package com.beesechurgers.gullak.utils

import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object DBConst {
    const val USER_KEY = "Users"
    const val DATA_KEY = "Data"

    const val WALLET_KEY = "Wallet_balance"
    const val INVESTMENTS_KEY = "Investments"
    const val TOTAL_INVEST_KEY = "Total_Invest"
    const val RISK_KEY = "risk_factor"
    const val PERCENTAGE_KEY = "percentage_amount"
}

object DBListeners {

    private var isListenerAssigned = false
    var isWalletSetup = mutableStateOf(false)
    var walletBalance = mutableStateOf(-1.0)

    fun enableWalletListener() {
        if (isListenerAssigned) return

        isListenerAssigned = true
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            FirebaseDatabase.getInstance().reference.child(DBConst.DATA_KEY).child(user.uid).addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val rB = snapshot.child(DBConst.WALLET_KEY).value
                        if (rB != null) {
                            walletBalance.value = rB.toString().toDouble()
                            isWalletSetup.value = walletBalance.value != -1.0
                        }
                    }

                    override fun onCancelled(error: DatabaseError) = Unit
                })
        }
    }
}