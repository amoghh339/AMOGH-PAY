package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Fireplace
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.RewardItem
import com.example.data.model.WalletAccount
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
import kotlinx.coroutines.launch

@Composable
fun RewardsScreen(
    wallet: WalletAccount?,
    rewards: List<RewardItem>,
    onRedeemReward: (RewardItem) -> Unit,
    onSpinWheel: ((String) -> Unit) -> Unit,
    onClaimStreak: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()
    var spinResultText by remember { mutableStateOf<String?>(null) }
    var isSpinning by remember { mutableStateOf(false) }
    val wheelRotation = remember { Animatable(0f) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "AmoghRewards 🏆",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = FamTextPrimary
                )
                Text(
                    text = "Earn 2x AmoghCoins on every UPI payment & unlock teen perks",
                    fontSize = 12.sp,
                    color = FamCyan
                )
            }
        }

        // Hero Rewards Banner Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.8f)
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = FamDarkSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, FamPurple.copy(alpha = 0.5f))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_rewards_banner),
                        contentDescription = "Rewards banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // FamCoins Balance Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("famcoins_balance_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = FamDarkSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, FamYellow.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Available AmoghCoins",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = FamTextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🪙", fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${wallet?.famCoins ?: 850}",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = FamYellow
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = FamYellow.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FamYellow)
                    ) {
                        Text(
                            text = "2x Booster Active ⚡",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = FamYellow,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Interactive "Spin & Win" Mini-Game
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("spin_wheel_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF141724)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, FamCyan.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🎡 Lucky Spin & Win",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = FamTextPrimary
                    )
                    Text(
                        text = "Spin to win real wallet cashback or up to 250 FamCoins!",
                        fontSize = 11.sp,
                        color = FamTextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Spinning Disc Graphic
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .rotate(wheelRotation.value)
                            .clip(CircleShape)
                            .background(
                                Brush.sweepGradient(
                                    listOf(FamYellow, FamCyan, FamPurple, FamAmber, FamGreen, FamYellow)
                                )
                            )
                            .border(3.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🎯", fontSize = 16.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (spinResultText != null) {
                        Text(
                            text = spinResultText ?: "",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = FamGreen,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Button(
                        onClick = {
                            if (!isSpinning) {
                                isSpinning = true
                                coroutineScope.launch {
                                    val randomTarget = wheelRotation.value + 1440f + (60..300).random().toFloat()
                                    wheelRotation.animateTo(
                                        targetValue = randomTarget,
                                        animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing)
                                    )
                                    onSpinWheel { result ->
                                        spinResultText = result
                                        isSpinning = false
                                    }
                                }
                            }
                        },
                        enabled = !isSpinning,
                        colors = ButtonDefaults.buttonColors(containerColor = FamCyan),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(44.dp)
                            .testTag("spin_btn")
                    ) {
                        Text(
                            text = if (isSpinning) "Spinning..." else "Spin Wheel (FREE)",
                            color = Color.Black,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // 7-Day Streak Timeline
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🔥", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Daily Payment Streak",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = FamTextPrimary
                            )
                        }
                        Text(
                            text = "${wallet?.streakDays ?: 6}/7 Days",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = FamAmber
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val currentStreak = wallet?.streakDays ?: 6
                        (1..7).forEach { day ->
                            val isCompleted = day <= currentStreak
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(if (isCompleted) FamAmber else FamDarkSurface)
                                        .border(1.dp, if (isCompleted) FamAmber else FamDarkBorder, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isCompleted) {
                                        Text(text = "🔥", fontSize = 14.sp)
                                    } else {
                                        Text(text = "$day", fontSize = 12.sp, color = FamTextMuted)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "D$day",
                                    fontSize = 10.sp,
                                    color = if (isCompleted) FamAmber else FamTextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // Teen Exclusive Vouchers & Deals
        item {
            Text(
                text = "Redeem FamCoins for Teen Deals",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = FamTextPrimary
            )
        }

        items(rewards) { reward ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reward_item_${reward.id}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = FamDarkSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, FamDarkBorder)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                when (reward.category) {
                                    "FOOD" -> FamAmber.copy(alpha = 0.2f)
                                    "ENTERTAINMENT" -> FamGreen.copy(alpha = 0.2f)
                                    "SHOPPING" -> FamPurple.copy(alpha = 0.2f)
                                    else -> FamCyan.copy(alpha = 0.2f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (reward.category) {
                                "FOOD" -> "🍕"
                                "ENTERTAINMENT" -> "🎵"
                                "SHOPPING" -> "👕"
                                else -> "🎮"
                            },
                            fontSize = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = reward.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = FamTextPrimary
                        )
                        Text(
                            text = reward.description,
                            fontSize = 11.sp,
                            color = FamTextSecondary,
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        if (reward.isRedeemed) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(FamGreen.copy(alpha = 0.15f))
                                    .clickable {
                                        clipboardManager.setText(AnnotatedString(reward.couponCode))
                                    }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Code: ${reward.couponCode}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FamGreen
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy code",
                                    tint = FamGreen,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🪙", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${reward.coinCost} Coins",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FamYellow
                                )
                            }
                        }
                    }

                    if (!reward.isRedeemed) {
                        val canAfford = (wallet?.famCoins ?: 0) >= reward.coinCost
                        Button(
                            onClick = { onRedeemReward(reward) },
                            enabled = canAfford,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (canAfford) FamYellow else FamDarkBorder
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(
                                text = "Unlock",
                                color = if (canAfford) Color.Black else FamTextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
