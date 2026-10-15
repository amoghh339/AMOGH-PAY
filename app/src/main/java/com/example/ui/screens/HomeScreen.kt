package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.PaymentTransaction
import com.example.data.model.SavedContact
import com.example.data.model.WalletAccount
import com.example.ui.theme.FamAmber
import com.example.ui.theme.FamCyan
import com.example.ui.theme.FamDarkBorder
import com.example.ui.theme.FamDarkSurface
import com.example.ui.theme.FamDarkSurfaceElevated
import com.example.ui.theme.FamGreen
import com.example.ui.theme.FamPurple
import com.example.ui.theme.FamRed
import com.example.ui.theme.FamTextMuted
import com.example.ui.theme.FamTextPrimary
import com.example.ui.theme.FamTextSecondary
import com.example.ui.theme.FamYellow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    wallet: WalletAccount?,
    transactions: List<PaymentTransaction>,
    contacts: List<SavedContact>,
    onScanClick: () -> Unit,
    onSendPaymentClick: () -> Unit,
    onAddMoneyClick: () -> Unit,
    onRequestMoneyClick: () -> Unit,
    onContactClick: (SavedContact) -> Unit,
    onViewCardClick: () -> Unit,
    onViewAllTransactions: () -> Unit,
    onClaimStreak: () -> Unit,
    onOpenKyc: () -> Unit
) {
    var isBalanceVisible by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Zero Bank Account & KYC Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenKyc() }
                    .testTag("zero_bank_account_banner"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FamDarkSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (wallet?.isKycVerified == true) FamGreen.copy(alpha = 0.4f) else FamYellow.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (wallet?.isKycVerified == true) FamGreen.copy(alpha = 0.15f) else FamYellow.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Zero Bank Account",
                            tint = if (wallet?.isKycVerified == true) FamGreen else FamYellow,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "AmoghPay • Zero Bank Account",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = FamTextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (wallet?.isKycVerified == true) FamGreen.copy(alpha = 0.2f) else FamAmber.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (wallet?.isKycVerified == true) "KYC VERIFIED ✓" else "ACTION REQUIRED",
                                    color = if (wallet?.isKycVerified == true) FamGreen else FamAmber,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (wallet?.isKycVerified == true) "DOB: ${wallet.dob} • Full PPI Limit Active • Tap to view KYC ID" else "Verify Mobile OTP & Date of Birth to activate full wallet limits.",
                            fontSize = 11.sp,
                            color = FamTextSecondary,
                            lineHeight = 15.sp
                        )
                    }

                    Text(
                        text = if (wallet?.isKycVerified == true) "Details →" else "Verify →",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = FamYellow
                    )
                }
            }
        }

        // Wallet Balance Main Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("wallet_balance_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = FamDarkSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, FamDarkBorder)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF1B1F2C),
                                    Color(0xFF12141D)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Prepaid Wallet Balance",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = FamTextSecondary
                            )

                            IconButton(
                                onClick = { isBalanceVisible = !isBalanceVisible },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = if (isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle Balance",
                                    tint = FamTextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Large Balance Text
                        Text(
                            text = if (isBalanceVisible) "₹${"%.2f".format(wallet?.balance ?: 0.0)}" else "₹ ••••••",
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Black,
                            color = FamYellow
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // UPI ID Subtext
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "UPI ID: ",
                                fontSize = 12.sp,
                                color = FamTextMuted
                            )
                            Text(
                                text = wallet?.upiId ?: "loading...",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = FamCyan
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Quick Action Buttons Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            QuickActionButton(
                                icon = Icons.Default.QrCodeScanner,
                                label = "Scan QR",
                                accentColor = FamYellow,
                                onClick = onScanClick
                            )
                            QuickActionButton(
                                icon = Icons.Default.Payment,
                                label = "Pay UPI",
                                accentColor = FamCyan,
                                onClick = onSendPaymentClick
                            )
                            QuickActionButton(
                                icon = Icons.Default.Add,
                                label = "Add Money",
                                accentColor = FamGreen,
                                onClick = onAddMoneyClick
                            )
                            QuickActionButton(
                                icon = Icons.Default.RequestQuote,
                                label = "Allowance",
                                accentColor = FamPurple,
                                onClick = onRequestMoneyClick
                            )
                        }
                    }
                }
            }
        }

        // FamCard Mini Preview Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onViewCardClick() }
                    .testTag("famcard_teaser_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = FamDarkSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, FamYellow.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(listOf(FamYellow, FamAmber))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = "FamCard",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Your FamCard",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = FamTextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (wallet?.cardFrozen == true) FamRed.copy(alpha = 0.2f) else FamGreen.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (wallet?.cardFrozen == true) "FROZEN" else "ACTIVE",
                                    color = if (wallet?.cardFrozen == true) FamRed else FamGreen,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Numberless RuPay virtual card • Tap to view CVV & controls",
                            fontSize = 11.sp,
                            color = FamTextSecondary
                        )
                    }

                    Text(
                        text = "View →",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = FamYellow
                    )
                }
            }
        }

        // Quick Pay Contacts Row
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pay Friends & Family",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = FamTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Send to new user "+" item
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { onSendPaymentClick() }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(FamDarkSurfaceElevated)
                                    .border(1.dp, FamDarkBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "New Payment",
                                    tint = FamYellow,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "Pay New", fontSize = 11.sp, color = FamTextSecondary)
                        }
                    }

                    items(contacts) { contact ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { onContactClick(contact) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(FamCyan, Color(0xFF005F73))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = contact.name.take(1),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = contact.name.split(" ").first(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = FamTextPrimary
                            )
                        }
                    }
                }
            }
        }

        // Streak & Rewards Highlight
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClaimStreak() }
                    .testTag("streak_banner"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FamDarkSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, FamAmber.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🔥", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${wallet?.streakDays ?: 1} Day Streak Active!",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = FamAmber
                        )
                        Text(
                            text = "Tap to claim today's streak reward and earn FamCoins",
                            fontSize = 11.sp,
                            color = FamTextSecondary
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = FamAmber
                    ) {
                        Text(
                            text = "Claim",
                            color = Color.Black,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Recent Transactions Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Transactions",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = FamTextPrimary
                )
                Text(
                    text = "See All",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FamYellow,
                    modifier = Modifier.clickable { onViewAllTransactions() }
                )
            }
        }

        // Recent Transaction Items
        val recentList = transactions.take(4)
        if (recentList.isEmpty()) {
            item {
                Text(
                    text = "No transactions yet. Scan a QR or pay a friend to start!",
                    color = FamTextMuted,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        } else {
            items(recentList) { tx ->
                TransactionListItem(transaction = tx)
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(FamDarkSurface)
                .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = accentColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = FamTextPrimary
        )
    }
}

@Composable
fun TransactionListItem(transaction: PaymentTransaction) {
    val isCredit = transaction.type == "CREDIT"
    val dateFormatter = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = FamDarkSurfaceElevated),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, FamDarkBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon Emoji
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (isCredit) FamGreen.copy(alpha = 0.15f) else FamYellow.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (transaction.category) {
                        "FOOD" -> "🍔"
                        "SHOPPING" -> "🛍️"
                        "ENTERTAINMENT" -> "🎮"
                        "POCKET_MONEY" -> "💰"
                        "BILL" -> "📄"
                        else -> if (isCredit) "⬇️" else "⬆️"
                    },
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = FamTextPrimary
                )
                Text(
                    text = transaction.subtitle,
                    fontSize = 11.sp,
                    color = FamTextSecondary
                )
                Text(
                    text = dateFormatter.format(Date(transaction.timestamp)),
                    fontSize = 10.sp,
                    color = FamTextMuted
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isCredit) "+" else "-"}₹${"%.2f".format(transaction.amount)}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isCredit) FamGreen else FamTextPrimary
                )
                Text(
                    text = transaction.status,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.status == "SUCCESS") FamGreen else FamRed
                )
            }
        }
    }
}
