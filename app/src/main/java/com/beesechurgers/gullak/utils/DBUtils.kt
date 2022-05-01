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
    const val HISTORY_KEY = "History"

    const val WALLET_KEY = "Wallet_balance"
    const val INVESTMENTS_KEY = "Investments"
    const val INVESTED_FUNDS_KEY = "Invested_Funds"
    const val PENDING_INVESTED_FUNDS = "Pending_Funds"
    const val RISK_KEY = "risk_factor"
    const val PERCENTAGE_KEY = "percentage_amount"

    const val AMOUNT_KEY = "amount"
    const val PAYMENT_DESC_KEY = "payment_desc"
}

object DBListeners {

    private var isListenerAssigned = false
    var isWalletSetup = mutableStateOf(false)
    var walletBalance = mutableStateOf(-1.0)
    var investedFunds = mutableStateOf(0.0)
    var pendingInvestedFunds = mutableStateOf(0.0)

    var percentageInvest = mutableStateOf(0f)
    var riskFactor = mutableStateOf(0)

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

                        investedFunds.value =
                            snapshot.child(DBConst.INVESTMENTS_KEY).child(DBConst.INVESTED_FUNDS_KEY).value?.toString()?.toDouble() ?: 0.0
                        pendingInvestedFunds.value =
                            snapshot.child(DBConst.INVESTMENTS_KEY).child(DBConst.PENDING_INVESTED_FUNDS).value?.toString()?.toDouble()
                                ?: 0.0

                        percentageInvest.value = snapshot.child(DBConst.PERCENTAGE_KEY).value?.toString()?.toFloat() ?: 0f
                        riskFactor.value = snapshot.child(DBConst.RISK_KEY).value?.toString()?.toInt() ?: 0
                    }

                    override fun onCancelled(error: DatabaseError) = Unit
                })
        }
    }
}