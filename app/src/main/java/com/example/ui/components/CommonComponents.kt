package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.PaymentTransaction
import com.example.ui.theme.FamAmber
import com.example.ui.theme.FamCyan
import com.example.ui.theme.FamDarkBorder
import com.example.ui.theme.FamDarkSurface
import com.example.ui.theme.FamDarkSurfaceElevated
import com.example.ui.theme.FamGreen
import com.example.ui.theme.FamPurple
import com.example.ui.theme.FamTextMuted
import com.example.ui.theme.FamTextPrimary
import com.example.ui.theme.FamTextSecondary
import com.example.ui.theme.FamYellow
import com.example.ui.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FamTopBar(
    name: String,
    upiId: String,
    coins: Int,
    streakDays: Int,
    isKycVerified: Boolean,
    onProfileClick: () -> Unit,
    onCoinsClick: () -> Unit,
    onCopyUpi: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // User Profile & UPI ID Pill
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .clickable { onProfileClick() }
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(FamYellow, FamAmber)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.take(1).uppercase(),
                    color = Color.Black,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = FamTextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    // KYC Status Pill
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isKycVerified) FamGreen.copy(alpha = 0.2f) else FamAmber.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, if (isKycVerified) FamGreen else FamAmber)
                    ) {
                        Text(
                            text = if (isKycVerified) "KYC ✓" else "KYC !",
                            color = if (isKycVerified) FamGreen else FamAmber,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(FamDarkSurfaceElevated)
                        .clickable {
                            clipboardManager.setText(AnnotatedString(upiId))
                            onCopyUpi()
                        }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = upiId,
                        style = MaterialTheme.typography.bodySmall,
                        color = FamCyan,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy UPI ID",
                        tint = FamCyan,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }

        // Streak & FamCoins Badges
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Streak Pill
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = FamDarkSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, FamAmber.copy(alpha = 0.4f)),
                modifier = Modifier.testTag("streak_pill")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🔥", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${streakDays}d",
                        color = FamAmber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            // FamCoins Badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = FamDarkSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, FamYellow.copy(alpha = 0.5f)),
                modifier = Modifier
                    .clickable { onCoinsClick() }
                    .testTag("coins_badge")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🪙", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$coins",
                        color = FamYellow,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun FamBottomNavBar(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    onScanClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars),
        color = FamDarkSurface,
        shadowElevation = 16.dp,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, FamDarkBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = Icons.Default.Home,
                label = "Home",
                isSelected = currentScreen == Screen.Home,
                onClick = { onNavigate(Screen.Home) }
            )
            NavItem(
                icon = Icons.Default.CreditCard,
                label = "FamCard",
                isSelected = currentScreen == Screen.Card,
                onClick = { onNavigate(Screen.Card) }
            )

            // Centered Scan & Pay Action Button
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(FamYellow, FamAmber))
                    )
                    .clickable { onScanClick() }
                    .testTag("scan_pay_fab"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = "Scan any UPI QR",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }

            NavItem(
                icon = Icons.Default.EmojiEvents,
                label = "Rewards",
                isSelected = currentScreen == Screen.Rewards,
                onClick = { onNavigate(Screen.Rewards) }
            )
            NavItem(
                icon = Icons.Default.History,
                label = "Passbook",
                isSelected = currentScreen == Screen.Passbook,
                onClick = { onNavigate(Screen.Passbook) }
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) FamYellow else FamTextMuted,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) FamYellow else FamTextMuted
        )
    }
}

@Composable
fun PaymentSuccessDialog(
    transaction: PaymentTransaction,
    coinsEarned: Int,
    onDismiss: () -> Unit
) {
    val scaleAnim = remember { Animatable(0.2f) }

    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 400)
        )
    }

    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .scale(scaleAnim.value)
                .testTag("payment_success_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = FamDarkSurfaceElevated),
            border = androidx.compose.foundation.BorderStroke(1.dp, FamGreen.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Success animated icon
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(FamGreen.copy(alpha = 0.15f))
                        .border(2.dp, FamGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Payment Successful",
                        tint = FamGreen,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Payment Successful! ⚡",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = FamTextPrimary
                )

                Text(
                    text = "Zero Bank Account UPI Transfer",
                    fontSize = 12.sp,
                    color = FamCyan,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Big Amount Display
                Text(
                    text = "₹${"%.2f".format(transaction.amount)}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = FamYellow
                )

                Text(
                    text = "Paid to ${transaction.title}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FamTextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // FamCoins Earned Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = FamYellow.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FamYellow.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🪙", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "+$coinsEarned AmoghCoins Earned!",
                            color = FamYellow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Receipt details card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = FamDarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FamDarkBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        ReceiptRow(label = "UPI ID", value = transaction.upiId)
                        ReceiptRow(label = "Ref / UTR ID", value = transaction.referenceId)
                        ReceiptRow(label = "Timestamp", value = dateFormatter.format(Date(transaction.timestamp)))
                        if (transaction.note.isNotBlank()) {
                            ReceiptRow(label = "Note", value = transaction.note)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Done & Share actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FamDarkBorder)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = FamTextSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share", color = FamTextSecondary, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("dismiss_success_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = FamYellow),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Done", color = Color.Black, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}

@Composable
fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = FamTextMuted)
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = FamTextPrimary,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
