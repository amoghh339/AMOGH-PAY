package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AddMoneyBottomSheet
import com.example.ui.components.FamBottomNavBar
import com.example.ui.components.FamTopBar
import com.example.ui.components.KycVerificationBottomSheet
import com.example.ui.components.PaymentSuccessDialog
import com.example.ui.components.RequestMoneyBottomSheet
import com.example.ui.components.SendPaymentBottomSheet
import com.example.ui.screens.CardScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PassbookScreen
import com.example.ui.screens.RewardsScreen
import com.example.ui.screens.ScanScreen
import com.example.ui.theme.FamDarkBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.FamPayViewModel
import com.example.ui.viewmodel.Screen
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                FamPayApp()
            }
        }
    }
}

@Composable
fun FamPayApp(viewModel: FamPayViewModel = viewModel()) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val wallet by viewModel.wallet.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val contacts by viewModel.contacts.collectAsStateWithLifecycle()
    val rewards by viewModel.rewards.collectAsStateWithLifecycle()
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val paymentSuccess by viewModel.paymentSuccess.collectAsStateWithLifecycle()

    val showSendSheet by viewModel.showSendSheet.collectAsStateWithLifecycle()
    val showAddMoneySheet by viewModel.showAddMoneySheet.collectAsStateWithLifecycle()
    val showRequestMoneySheet by viewModel.showRequestMoneySheet.collectAsStateWithLifecycle()
    val showKycSheet by viewModel.showKycSheet.collectAsStateWithLifecycle()
    val prefilledRecipient by viewModel.prefilledRecipient.collectAsStateWithLifecycle()

    // Handle back button to return to Home screen
    BackHandler(enabled = currentScreen != Screen.Home) {
        viewModel.navigateTo(Screen.Home)
    }

    LaunchedEffect(Unit) {
        viewModel.toastMessage.collectLatest { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(FamDarkBackground),
        containerColor = FamDarkBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Box(modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)) {
                FamTopBar(
                    name = wallet?.fullName ?: "Amogh Sharma",
                    upiId = wallet?.upiId ?: "amogh.pay@amogh",
                    coins = wallet?.famCoins ?: 850,
                    streakDays = wallet?.streakDays ?: 6,
                    isKycVerified = wallet?.isKycVerified ?: false,
                    onProfileClick = { viewModel.openKyc() },
                    onCoinsClick = { viewModel.navigateTo(Screen.Rewards) },
                    onCopyUpi = {
                        Toast.makeText(context, "UPI ID copied! Share with parents or friends.", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        },
        bottomBar = {
            FamBottomNavBar(
                currentScreen = currentScreen,
                onNavigate = { screen -> viewModel.navigateTo(screen) },
                onScanClick = { viewModel.navigateTo(Screen.Scan) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                is Screen.Home -> {
                    HomeScreen(
                        wallet = wallet,
                        transactions = transactions,
                        contacts = contacts,
                        onScanClick = { viewModel.navigateTo(Screen.Scan) },
                        onSendPaymentClick = { viewModel.openSendPayment() },
                        onAddMoneyClick = { viewModel.openAddMoney() },
                        onRequestMoneyClick = { viewModel.openRequestMoney() },
                        onContactClick = { contact ->
                            viewModel.openSendPayment(name = contact.name, upiId = contact.upiId)
                        },
                        onViewCardClick = { viewModel.navigateTo(Screen.Card) },
                        onViewAllTransactions = { viewModel.navigateTo(Screen.Passbook) },
                        onClaimStreak = { viewModel.claimDailyStreak() },
                        onOpenKyc = { viewModel.openKyc() }
                    )
                }

                is Screen.Card -> {
                    CardScreen(
                        wallet = wallet,
                        onToggleFreeze = { isFrozen -> viewModel.toggleCardFreeze(isFrozen) },
                        onToggleOnline = { enabled -> viewModel.toggleOnlineTxn(enabled) },
                        onToggleContactless = { enabled -> viewModel.toggleContactless(enabled) },
                        onUpdateLimit = { limit -> viewModel.updateSpendLimit(limit) },
                        onChangeTheme = { theme -> viewModel.changeCardTheme(theme) }
                    )
                }

                is Screen.Scan -> {
                    ScanScreen(
                        onScanSuccess = { name, upiId, suggestedAmount, category ->
                            viewModel.openSendPayment(name, upiId)
                        }
                    )
                }

                is Screen.Rewards -> {
                    RewardsScreen(
                        wallet = wallet,
                        rewards = rewards,
                        onRedeemReward = { reward -> viewModel.redeemReward(reward) },
                        onSpinWheel = { onResult -> viewModel.spinWheel(onResult) },
                        onClaimStreak = { viewModel.claimDailyStreak() }
                    )
                }

                is Screen.Passbook -> {
                    PassbookScreen(
                        wallet = wallet,
                        transactions = transactions
                    )
                }
            }
        }
    }

    // Modal Bottom Sheets for Payments, Add Money, and Request Money
    if (showSendSheet) {
        SendPaymentBottomSheet(
            prefilledRecipient = prefilledRecipient,
            contacts = contacts,
            walletBalance = wallet?.balance ?: 0.0,
            onDismiss = { viewModel.closeSendPayment() },
            onSendPayment = { name, upiId, amount, category, note ->
                viewModel.processPayment(name, upiId, amount, category, note)
            }
        )
    }

    if (showAddMoneySheet) {
        AddMoneyBottomSheet(
            onDismiss = { viewModel.closeAddMoney() },
            onAddMoney = { amount, source, note ->
                viewModel.addMoney(amount, source, note)
            }
        )
    }

    if (showRequestMoneySheet) {
        RequestMoneyBottomSheet(
            onDismiss = { viewModel.closeRequestMoney() },
            onRequestMoney = { fromName, upiId, amount, reason ->
                viewModel.requestMoney(fromName, upiId, amount, reason)
            }
        )
    }

    if (showKycSheet) {
        KycVerificationBottomSheet(
            wallet = wallet,
            onDismiss = { viewModel.closeKyc() },
            onSubmitKyc = { fullName, dob, docType, docNumber, parentName, parentPhone ->
                viewModel.submitKyc(fullName, dob, docType, docNumber, parentName, parentPhone)
            }
        )
    }

    // Payment Success Celebration Dialog
    paymentSuccess?.let { successInfo ->
        PaymentSuccessDialog(
            transaction = successInfo.transaction,
            coinsEarned = successInfo.coinsEarned,
            onDismiss = { viewModel.dismissPaymentSuccess() }
        )
    }
}
