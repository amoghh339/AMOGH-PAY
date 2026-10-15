package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Contactless
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
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

@Composable
fun CardScreen(
    wallet: WalletAccount?,
    onToggleFreeze: (Boolean) -> Unit,
    onToggleOnline: (Boolean) -> Unit,
    onToggleContactless: (Boolean) -> Unit,
    onUpdateLimit: (Double) -> Unit,
    onChangeTheme: (String) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var isCardFlipped by remember { mutableStateOf(false) }
    var showFullDetails by remember { mutableStateOf(false) }

    // 3D Flip animation
    val rotation by animateFloatAsState(
        targetValue = if (isCardFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "card_flip"
    )

    val currentTheme = wallet?.cardTheme ?: "cyber_gold"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "AmoghCard 💳",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = FamTextPrimary
                )
                Text(
                    text = "Zero bank account numberless card for teens & young adults • Tap to flip",
                    fontSize = 12.sp,
                    color = FamCyan
                )
            }
        }

        // 3D Flippable FamCard
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.58f)
                    .graphicsLayer {
                        rotationY = rotation
                        cameraDistance = 12f * density
                    }
                    .clickable { isCardFlipped = !isCardFlipped }
                    .testTag("famcard_3d_view")
            ) {
                if (rotation <= 90f) {
                    // FRONT OF CARD (Numberless)
                    CardFront(
                        wallet = wallet,
                        theme = currentTheme
                    )
                } else {
                    // BACK OF CARD (Flipped - CVV & Card Number)
                    Box(modifier = Modifier.graphicsLayer { rotationY = 180f }) {
                        CardBack(
                            wallet = wallet,
                            showFullDetails = showFullDetails,
                            onToggleReveal = { showFullDetails = !showFullDetails },
                            onCopyNumber = {
                                clipboardManager.setText(AnnotatedString(wallet?.cardNumber ?: ""))
                            }
                        )
                    }
                }
            }
        }

        // Freeze Status Warning
        if (wallet?.cardFrozen == true) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = FamRed.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FamRed.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🔒", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Card is FROZEN. All online and offline transactions are paused.",
                            color = FamRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Quick Flip / Copy Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { isCardFlipped = !isCardFlipped },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FamDarkSurfaceElevated)
                ) {
                    Text(
                        text = if (isCardFlipped) "Show Front 🔄" else "View Details (CVV) 🔄",
                        color = FamTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(wallet?.cardNumber ?: ""))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FamDarkSurfaceElevated)
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = FamYellow, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy Card No.", color = FamYellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Card Skin / Theme Picker
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FamDarkSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, FamDarkBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Palette, contentDescription = null, tint = FamYellow, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Choose Card Doodle / Skin",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = FamTextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ThemePill(
                            label = "Cyber Gold",
                            color = FamYellow,
                            isSelected = currentTheme == "cyber_gold",
                            onClick = { onChangeTheme("cyber_gold") }
                        )
                        ThemePill(
                            label = "Neon Cyan",
                            color = FamCyan,
                            isSelected = currentTheme == "neon_cyan",
                            onClick = { onChangeTheme("neon_cyan") }
                        )
                        ThemePill(
                            label = "Midnight Purple",
                            color = FamPurple,
                            isSelected = currentTheme == "midnight_purple",
                            onClick = { onChangeTheme("midnight_purple") }
                        )
                    }
                }
            }
        }

        // Spending Limit Control
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FamDarkSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, FamDarkBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Monthly Spending Limit",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = FamTextPrimary
                        )
                        Text(
                            text = "₹${(wallet?.monthlySpendLimit ?: 10000.0).toInt()}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = FamYellow
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Spent this month: ₹${(wallet?.currentMonthSpend ?: 0.0).toInt()}",
                        fontSize = 12.sp,
                        color = FamTextSecondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    var sliderValue by remember(wallet?.monthlySpendLimit) {
                        mutableStateOf((wallet?.monthlySpendLimit ?: 10000.0).toFloat())
                    }

                    Slider(
                        value = sliderValue,
                        onValueChange = { sliderValue = it },
                        onValueChangeFinished = { onUpdateLimit(sliderValue.toDouble()) },
                        valueRange = 1000f..25000f,
                        steps = 23,
                        colors = SliderDefaults.colors(
                            thumbColor = FamYellow,
                            activeTrackColor = FamYellow,
                            inactiveTrackColor = FamDarkBorder
                        )
                    )
                }
            }
        }

        // Card Security Controls Toggles
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FamDarkSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, FamDarkBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Security & Transaction Controls",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = FamTextPrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    CardControlToggle(
                        title = "Freeze AmoghCard",
                        subtitle = "Temporarily block all card & wallet spends",
                        icon = Icons.Default.Lock,
                        isChecked = wallet?.cardFrozen ?: false,
                        onCheckedChange = onToggleFreeze,
                        testTag = "freeze_toggle"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    CardControlToggle(
                        title = "Online Transactions",
                        subtitle = "Enable for Amazon, Swiggy, Steam, etc.",
                        icon = Icons.Default.Language,
                        isChecked = wallet?.onlineTxnEnabled ?: true,
                        onCheckedChange = onToggleOnline,
                        testTag = "online_toggle"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    CardControlToggle(
                        title = "Contactless / Tap to Pay",
                        subtitle = "NFC payments at point of sale terminals",
                        icon = Icons.Default.Contactless,
                        isChecked = wallet?.contactlessEnabled ?: true,
                        onCheckedChange = onToggleContactless,
                        testTag = "nfc_toggle"
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CardFront(
    wallet: WalletAccount?,
    theme: String
) {
    val gradientColors = when (theme) {
        "neon_cyan" -> listOf(Color(0xFF003B46), Color(0xFF07575B), Color(0xFF0A1118))
        "midnight_purple" -> listOf(Color(0xFF3C096C), Color(0xFF240046), Color(0xFF10002B))
        else -> listOf(Color(0xFF2D2505), Color(0xFF1F1A07), Color(0xFF0F0E0A))
    }

    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            when (theme) {
                "neon_cyan" -> FamCyan
                "midnight_purple" -> FamPurple
                else -> FamYellow
            }.copy(alpha = 0.7f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(gradientColors))
        ) {
            // Subtle card texture overlay
            Image(
                painter = painterResource(id = R.drawable.img_famcard_bg),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = 0.35f }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Row: FamPay branding + Contactless Wave
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Amogh",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = FamYellow
                        )
                        Text(
                            text = "Card",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Light,
                            color = FamTextPrimary
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Contactless,
                        contentDescription = "Contactless",
                        tint = FamTextPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Middle: EMV Chip Graphic + Numberless Teen Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Chip graphic
                    Box(
                        modifier = Modifier
                            .size(width = 44.dp, height = 34.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFD4AF37))
                            .border(1.dp, Color(0xFFFFF275), RoundedCornerShape(6.dp))
                    )

                    // Numberless Security Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "NUMBERLESS FOR SECURITY 🔒",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = FamCyan,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Bottom Row: Cardholder Name + RuPay / Visa logo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = (wallet?.fullName ?: "ALEX SHARMA").uppercase(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = FamTextPrimary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "PREPAID WALLET DEBIT",
                            fontSize = 9.sp,
                            color = FamTextSecondary,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Text(
                        text = "RuPay",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = FamYellow,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }
        }
    }
}

@Composable
private fun CardBack(
    wallet: WalletAccount?,
    showFullDetails: Boolean,
    onToggleReveal: () -> Unit,
    onCopyNumber: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF13151F)),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, FamCyan.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Magnetic Stripe
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .background(Color.Black)
            )

            // Middle: Card Number & Expiry & CVV
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CARD DETAILS FOR ONLINE SPENDS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = FamCyan
                    )

                    IconButton(onClick = onToggleReveal, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = if (showFullDetails) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Reveal",
                            tint = FamYellow,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 16 Digit Number
                Text(
                    text = if (showFullDetails) wallet?.cardNumber ?: "4321 8890 1234 5678" else "4321 •••• •••• 5678",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = FamTextPrimary,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Column {
                        Text(text = "VALID THRU", fontSize = 9.sp, color = FamTextMuted)
                        Text(
                            text = wallet?.cardExpiry ?: "08/30",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = FamTextPrimary
                        )
                    }

                    Column {
                        Text(text = "CVV", fontSize = 9.sp, color = FamTextMuted)
                        Text(
                            text = if (showFullDetails) wallet?.cardCvv ?: "742" else "•••",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = FamYellow
                        )
                    }
                }
            }

            // Bottom Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Zero Bank Account • PPI License",
                    fontSize = 9.sp,
                    color = FamTextMuted
                )

                Text(
                    text = "Tap to flip back",
                    fontSize = 10.sp,
                    color = FamYellow,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CardControlToggle(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(FamDarkSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isChecked) FamYellow else FamTextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = FamTextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = FamTextSecondary
                )
            }
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag),
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = FamYellow,
                uncheckedThumbColor = FamTextMuted,
                uncheckedTrackColor = FamDarkSurface
            )
        )
    }
}

@Composable
private fun ThemePill(
    label: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) color.copy(alpha = 0.2f) else FamDarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) color else FamDarkBorder),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) color else FamTextSecondary
            )
        }
    }
}
