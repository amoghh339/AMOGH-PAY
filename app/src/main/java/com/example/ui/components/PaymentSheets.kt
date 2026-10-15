package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SavedContact
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendPaymentBottomSheet(
    prefilledRecipient: Pair<String, String>?,
    contacts: List<SavedContact>,
    walletBalance: Double,
    onDismiss: () -> Unit,
    onSendPayment: (name: String, upiId: String, amount: Double, category: String, note: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var recipientName by remember(prefilledRecipient) { mutableStateOf(prefilledRecipient?.first ?: "") }
    var upiId by remember(prefilledRecipient) { mutableStateOf(prefilledRecipient?.second ?: "") }
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("FOOD") }

    // Security PIN step
    var isPinStep by remember { mutableStateOf(false) }
    var enteredPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf("") }

    val categories = listOf(
        "FOOD" to "🍔 Food",
        "SHOPPING" to "🛍️ Shopping",
        "ENTERTAINMENT" to "🎮 Gaming/Movies",
        "TRANSFER" to "👥 Friends",
        "BILL" to "📄 Bills"
    )

    val quickAmounts = listOf("50", "100", "200", "500", "1000")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = FamDarkSurface,
        dragHandle = null,
        modifier = Modifier.windowInsetsPadding(WindowInsets.ime)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isPinStep) "Enter AmoghPIN" else "Send Money ⚡",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = FamTextPrimary
                    )
                    Text(
                        text = if (isPinStep) "Confirm payment with your 4-digit PIN" else "Pay any UPI ID or phone from your wallet",
                        fontSize = 12.sp,
                        color = FamTextSecondary
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = FamTextMuted)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!isPinStep) {
                // Step 1: Payment Details
                if (prefilledRecipient == null && contacts.isNotEmpty()) {
                    Text(
                        text = "Quick Contacts",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = FamTextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(contacts) { contact ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = FamDarkSurfaceElevated,
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, FamDarkBorder),
                                modifier = Modifier.clickable {
                                    recipientName = contact.name
                                    upiId = contact.upiId
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(FamYellow),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = contact.name.take(1),
                                            color = Color.Black,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = contact.name,
                                        fontSize = 12.sp,
                                        color = FamTextPrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Recipient Name
                OutlinedTextField(
                    value = recipientName,
                    onValueChange = { recipientName = it },
                    label = { Text("Recipient / Shop Name") },
                    placeholder = { Text("e.g. Starbucks, Rohan, Kirana") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("send_recipient_name"),
                    singleLine = true,
                    colors = outlinedColors()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // UPI ID or Phone
                OutlinedTextField(
                    value = upiId,
                    onValueChange = { upiId = it },
                    label = { Text("UPI ID or Phone Number") },
                    placeholder = { Text("e.g. friend@fam or 9876543210") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("send_recipient_upi"),
                    singleLine = true,
                    colors = outlinedColors()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Amount
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { if (it.all { ch -> ch.isDigit() || ch == '.' }) amountText = it },
                    label = { Text("Amount (₹)") },
                    placeholder = { Text("0.00") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("send_amount_input"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    leadingIcon = {
                        Text(
                            text = "₹",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = FamYellow,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    },
                    colors = outlinedColors()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Amount Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickAmounts.forEach { amt ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = FamDarkSurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(1.dp, FamDarkBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { amountText = amt }
                        ) {
                            Text(
                                text = "₹$amt",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = FamCyan,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Note
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Add a note / emoji (Optional)") },
                    placeholder = { Text("e.g. Movie tickets 🍿, Burgers 🍔") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = outlinedColors()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Category Chips
                Text(
                    text = "Category",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FamTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { (catKey, catLabel) ->
                        FilterChip(
                            selected = selectedCategory == catKey,
                            onClick = { selectedCategory = catKey },
                            label = { Text(catLabel, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FamYellow,
                                selectedLabelColor = Color.Black,
                                containerColor = FamDarkSurfaceElevated,
                                labelColor = FamTextSecondary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                val parsedAmt = amountText.toDoubleOrNull() ?: 0.0
                val canProceed = recipientName.isNotBlank() && upiId.isNotBlank() && parsedAmt > 0 && parsedAmt <= walletBalance

                if (parsedAmt > walletBalance) {
                    Text(
                        text = "⚠️ Amount exceeds wallet balance (₹${"%.2f".format(walletBalance)})",
                        color = FamRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = { isPinStep = true },
                    enabled = canProceed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("send_proceed_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = FamYellow),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "Pay ₹${if (parsedAmt > 0) parsedAmt.toInt() else 0} with AmoghPIN",
                        color = Color.Black,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                }
            } else {
                // Step 2: FamPIN Numeric Keypad
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = FamDarkSurfaceElevated)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Paying $recipientName", fontSize = 14.sp, color = FamTextSecondary)
                            Text(
                                text = "₹${amountText.toDoubleOrNull() ?: 0.0}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = FamYellow
                            )
                            Text(text = upiId, fontSize = 12.sp, color = FamCyan)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // PIN Dots (4 digits)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        repeat(4) { idx ->
                            val filled = idx < enteredPin.length
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(if (filled) FamYellow else FamDarkBorder)
                                    .border(1.dp, if (filled) FamYellow else FamDarkBorder, CircleShape)
                            )
                        }
                    }

                    if (pinError.isNotBlank()) {
                        Text(
                            text = pinError,
                            color = FamRed,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Numeric Keypad
                    FamNumericKeypad(
                        onDigitClick = { digit ->
                            if (enteredPin.length < 4) {
                                enteredPin += digit
                                pinError = ""
                                if (enteredPin.length == 4) {
                                    // Verify PIN (default teen test pin: any 4 digits or e.g. 1234)
                                    onSendPayment(
                                        recipientName,
                                        upiId,
                                        amountText.toDoubleOrNull() ?: 0.0,
                                        selectedCategory,
                                        note
                                    )
                                }
                            }
                        },
                        onBackspace = {
                            if (enteredPin.isNotEmpty()) {
                                enteredPin = enteredPin.dropLast(1)
                                pinError = ""
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FamNumericKeypad(
    onDigitClick: (String) -> Unit,
    onBackspace: () -> Unit
) {
    val keys = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("C", "0", "⌫")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        keys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                row.forEach { key ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(FamDarkSurfaceElevated)
                            .border(1.dp, FamDarkBorder, RoundedCornerShape(12.dp))
                            .clickable {
                                when (key) {
                                    "⌫" -> onBackspace()
                                    "C" -> {
                                        repeat(4) { onBackspace() }
                                    }
                                    else -> onDigitClick(key)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = key,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (key == "C" || key == "⌫") FamCyan else FamTextPrimary
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMoneyBottomSheet(
    onDismiss: () -> Unit,
    onAddMoney: (amount: Double, source: String, note: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var amountText by remember { mutableStateOf("500") }
    var selectedSource by remember { mutableStateOf("MOM_ALLOWANCE") }

    val presetAmounts = listOf("200", "500", "1000", "2000")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = FamDarkSurface,
        dragHandle = null,
        modifier = Modifier.windowInsetsPadding(WindowInsets.ime)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Add Money to Wallet 💳",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = FamTextPrimary
                    )
                    Text(
                        text = "No bank account needed • Powered by PPI Wallet",
                        fontSize = 12.sp,
                        color = FamCyan
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = FamTextMuted)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Amount field
            OutlinedTextField(
                value = amountText,
                onValueChange = { if (it.all { ch -> ch.isDigit() }) amountText = it },
                label = { Text("Deposit Amount (₹)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_money_input"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                leadingIcon = {
                    Text(
                        text = "₹",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = FamYellow,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                },
                colors = outlinedColors()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presetAmounts.forEach { amt ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = FamDarkSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, FamDarkBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { amountText = amt }
                    ) {
                        Text(
                            text = "+₹$amt",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = FamYellow,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Select Funding Source",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = FamTextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            FundingOptionCard(
                title = "Parent Allowance (Mom)",
                subtitle = "Instant allowance transfer to teen wallet",
                icon = Icons.Default.FamilyRestroom,
                isSelected = selectedSource == "MOM_ALLOWANCE",
                onClick = { selectedSource = "MOM_ALLOWANCE" }
            )

            Spacer(modifier = Modifier.height(8.dp))

            FundingOptionCard(
                title = "Parent Allowance (Dad)",
                subtitle = "Pocket money credited from family pool",
                icon = Icons.Default.FamilyRestroom,
                isSelected = selectedSource == "DAD_ALLOWANCE",
                onClick = { selectedSource = "DAD_ALLOWANCE" }
            )

            Spacer(modifier = Modifier.height(8.dp))

            FundingOptionCard(
                title = "Any UPI App (GPay / PhonePe / Paytm)",
                subtitle = "Load wallet balance using external UPI",
                icon = Icons.Default.QrCode,
                isSelected = selectedSource == "PREPAID_TOPUP",
                onClick = { selectedSource = "PREPAID_TOPUP" }
            )

            Spacer(modifier = Modifier.height(8.dp))

            FundingOptionCard(
                title = "Debit / Credit Card (Any Bank)",
                subtitle = "Zero convenience fee top-up",
                icon = Icons.Default.AccountBalanceWallet,
                isSelected = selectedSource == "DEBIT_CARD",
                onClick = { selectedSource = "DEBIT_CARD" }
            )

            Spacer(modifier = Modifier.height(20.dp))

            val amt = amountText.toDoubleOrNull() ?: 0.0
            Button(
                onClick = {
                    onAddMoney(
                        amt,
                        selectedSource,
                        "Prepaid wallet loaded successfully (Zero Bank Account)"
                    )
                },
                enabled = amt > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("confirm_add_money_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = FamYellow),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Deposit ₹${amt.toInt()} to Wallet",
                    color = Color.Black,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun FundingOptionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) FamYellow.copy(alpha = 0.12f) else FamDarkSurfaceElevated
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) FamYellow else FamDarkBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) FamYellow else FamDarkBorder),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.Black else FamTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) FamYellow else FamTextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = FamTextSecondary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestMoneyBottomSheet(
    onDismiss: () -> Unit,
    onRequestMoney: (fromName: String, upiId: String, amount: Double, reason: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTarget by remember { mutableStateOf("Mom") }
    var amountText by remember { mutableStateOf("300") }
    var reason by remember { mutableStateOf("School books & stationery") }

    val reasons = listOf(
        "School Books & Project",
        "Weekend Outing with Friends",
        "Bus / Metro Recharge",
        "Snacks & Treat",
        "Monthly Pocket Money"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = FamDarkSurface,
        dragHandle = null,
        modifier = Modifier.windowInsetsPadding(WindowInsets.ime)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Request Pocket Money 📩",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = FamTextPrimary
                    )
                    Text(
                        text = "Send an allowance request to parents or split with friends",
                        fontSize = 12.sp,
                        color = FamTextSecondary
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = FamTextMuted)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Request From",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = FamTextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("Mom", "Dad", "Rohan", "Priya").forEach { target ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (selectedTarget == target) FamYellow else FamDarkSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedTarget == target) FamYellow else FamDarkBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTarget = target }
                    ) {
                        Text(
                            text = target,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTarget == target) Color.Black else FamTextPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = { if (it.all { ch -> ch.isDigit() }) amountText = it },
                label = { Text("Requested Amount (₹)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = outlinedColors()
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                label = { Text("What is it for?") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = outlinedColors()
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(reasons) { r ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = FamDarkSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, FamDarkBorder),
                        modifier = Modifier.clickable { reason = r }
                    ) {
                        Text(
                            text = r,
                            fontSize = 11.sp,
                            color = FamTextSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            val amt = amountText.toDoubleOrNull() ?: 0.0
            Button(
                onClick = {
                    val targetUpi = if (selectedTarget == "Mom") "mom.sharma@fam" else if (selectedTarget == "Dad") "dad.sharma@fam" else "${selectedTarget.lowercase()}@fam"
                    onRequestMoney(selectedTarget, targetUpi, amt, reason)
                },
                enabled = amt > 0 && reason.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("send_request_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = FamYellow),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Send Request for ₹${amt.toInt()}",
                    color = Color.Black,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun outlinedColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = FamYellow,
    unfocusedBorderColor = FamDarkBorder,
    focusedLabelColor = FamYellow,
    unfocusedLabelColor = FamTextMuted,
    focusedTextColor = FamTextPrimary,
    unfocusedTextColor = FamTextPrimary,
    cursorColor = FamYellow
)
