package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.FamPayDatabase
import com.example.data.model.MoneyRequest
import com.example.data.model.PaymentTransaction
import com.example.data.model.RewardItem
import com.example.data.model.SavedContact
import com.example.data.model.WalletAccount
import com.example.data.repository.FamPayRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Home")
    object Card : Screen("card", "FamCard")
    object Scan : Screen("scan", "Scan & Pay")
    object Rewards : Screen("rewards", "Rewards")
    object Passbook : Screen("passbook", "Passbook")
}

data class PaymentSuccessInfo(
    val transaction: PaymentTransaction,
    val coinsEarned: Int
)

class FamPayViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FamPayRepository

    init {
        val database = FamPayDatabase.getDatabase(application, viewModelScope)
        repository = FamPayRepository(database)
    }

    val wallet: StateFlow<WalletAccount?> = repository.walletFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val transactions: StateFlow<List<PaymentTransaction>> = repository.transactionsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val contacts: StateFlow<List<SavedContact>> = repository.contactsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rewards: StateFlow<List<RewardItem>> = repository.rewardsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val moneyRequests: StateFlow<List<MoneyRequest>> = repository.moneyRequestsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _paymentSuccess = MutableStateFlow<PaymentSuccessInfo?>(null)
    val paymentSuccess: StateFlow<PaymentSuccessInfo?> = _paymentSuccess.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    // Interactive Dialogs
    private val _showSendSheet = MutableStateFlow(false)
    val showSendSheet: StateFlow<Boolean> = _showSendSheet.asStateFlow()

    private val _showAddMoneySheet = MutableStateFlow(false)
    val showAddMoneySheet: StateFlow<Boolean> = _showAddMoneySheet.asStateFlow()

    private val _showRequestMoneySheet = MutableStateFlow(false)
    val showRequestMoneySheet: StateFlow<Boolean> = _showRequestMoneySheet.asStateFlow()

    private val _showKycSheet = MutableStateFlow(false)
    val showKycSheet: StateFlow<Boolean> = _showKycSheet.asStateFlow()

    // Pre-filled send target (when clicking a contact or scanning QR)
    private val _prefilledRecipient = MutableStateFlow<Pair<String, String>?>(null) // Pair(name, upiId)
    val prefilledRecipient: StateFlow<Pair<String, String>?> = _prefilledRecipient.asStateFlow()

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun openSendPayment(name: String = "", upiId: String = "") {
        if (name.isNotEmpty() || upiId.isNotEmpty()) {
            _prefilledRecipient.value = Pair(name, upiId)
        } else {
            _prefilledRecipient.value = null
        }
        _showSendSheet.value = true
    }

    fun closeSendPayment() {
        _showSendSheet.value = false
        _prefilledRecipient.value = null
    }

    fun openAddMoney() {
        _showAddMoneySheet.value = true
    }

    fun closeAddMoney() {
        _showAddMoneySheet.value = false
    }

    fun openRequestMoney() {
        _showRequestMoneySheet.value = true
    }

    fun closeRequestMoney() {
        _showRequestMoneySheet.value = false
    }

    fun dismissPaymentSuccess() {
        _paymentSuccess.value = null
    }

    fun processPayment(
        recipientName: String,
        upiId: String,
        amount: Double,
        category: String,
        note: String
    ) {
        viewModelScope.launch {
            val result = repository.sendPayment(recipientName, upiId, amount, category, note)
            result.onSuccess { tx ->
                val coins = maxOf(10, (amount * 0.1).toInt())
                _paymentSuccess.value = PaymentSuccessInfo(tx, coins)
                _showSendSheet.value = false
                _prefilledRecipient.value = null
            }.onFailure { err ->
                _toastMessage.emit(err.message ?: "Payment failed")
            }
        }
    }

    fun addMoney(amount: Double, source: String, note: String) {
        viewModelScope.launch {
            repository.addMoneyToWallet(amount, source, note)
            _showAddMoneySheet.value = false
            _toastMessage.emit("₹${amount.toInt()} added to your FamPay Wallet!")
        }
    }

    fun requestMoney(fromName: String, upiId: String, amount: Double, reason: String) {
        viewModelScope.launch {
            repository.createMoneyRequest(fromName, upiId, amount, reason)
            _showRequestMoneySheet.value = false
            _toastMessage.emit("Money request sent to $fromName!")
        }
    }

    fun toggleCardFreeze(freeze: Boolean) {
        viewModelScope.launch {
            repository.toggleCardFreeze(freeze)
            _toastMessage.emit(if (freeze) "FamCard Frozen 🔒" else "FamCard Active & Ready ⚡")
        }
    }

    fun toggleOnlineTxn(enable: Boolean) {
        viewModelScope.launch {
            repository.toggleOnlineTxn(enable)
        }
    }

    fun toggleContactless(enable: Boolean) {
        viewModelScope.launch {
            repository.toggleContactless(enable)
        }
    }

    fun updateSpendLimit(limit: Double) {
        viewModelScope.launch {
            repository.updateMonthlySpendLimit(limit)
        }
    }

    fun changeCardTheme(theme: String) {
        viewModelScope.launch {
            repository.updateCardTheme(theme)
            _toastMessage.emit("Card theme updated!")
        }
    }

    fun redeemReward(reward: RewardItem) {
        viewModelScope.launch {
            val res = repository.redeemReward(reward)
            res.onSuccess { code ->
                _toastMessage.emit("Unlocked! Code: $code")
            }.onFailure { err ->
                _toastMessage.emit(err.message ?: "Could not redeem")
            }
        }
    }

    fun spinWheel(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val (cash, coins) = repository.spinWheelReward()
            if (cash > 0) {
                onResult("🎉 Won ₹${cash.toInt()} Cashback added to wallet!")
            } else {
                onResult("⚡ Won $coins FamCoins!")
            }
        }
    }

    fun claimDailyStreak() {
        viewModelScope.launch {
            val coins = repository.claimDailyStreakReward()
            _toastMessage.emit("🔥 Streak Claimed! +$coins FamCoins")
        }
    }

    fun addContact(name: String, upiId: String, phone: String) {
        viewModelScope.launch {
            repository.addContact(name, upiId, phone)
            _toastMessage.emit("Contact $name added!")
        }
    }

    fun openKyc() {
        _showKycSheet.value = true
    }

    fun closeKyc() {
        _showKycSheet.value = false
    }

    fun submitKyc(
        fullName: String,
        dob: String,
        docType: String,
        docNumber: String,
        parentName: String,
        parentPhone: String
    ) {
        viewModelScope.launch {
            repository.updateKycVerification(
                fullName = fullName,
                dob = dob,
                docType = docType,
                docNumber = docNumber,
                parentName = parentName,
                parentPhone = parentPhone
            )
            _toastMessage.emit("KYC Verified! Zero Bank Account Wallet Active 🛡️")
        }
    }
}
