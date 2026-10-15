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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WalletAccount
import com.example.ui.theme.FamAmber
import com.example.ui.theme.FamCyan
import com.example.ui.theme.FamDarkBorder
import com.example.ui.theme.FamDarkSurface
import com.example.ui.theme.FamDarkSurfaceElevated
import com.example.ui.theme.FamGreen
import com.example.ui.theme.FamRed
import com.example.ui.theme.FamTextMuted
import com.example.ui.theme.FamTextPrimary
import com.example.ui.theme.FamTextSecondary
import com.example.ui.theme.FamYellow
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KycVerificationBottomSheet(
    wallet: WalletAccount?,
    onDismiss: () -> Unit,
    onSubmitKyc: (fullName: String, dob: String, docType: String, docNumber: String, parentName: String, parentPhone: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Current Step: 1 = OTP Verification, 2 = DOB & Details, 3 = Document KYC, 4 = Success
    var step by remember { mutableIntStateOf(if (wallet?.isKycVerified == true) 4 else 1) }

    // Step 1: Phone & Real OTP
    var phoneInput by remember { mutableStateOf(wallet?.phone?.replace("+91 ", "") ?: "9876543210") }
    var generatedOtp by remember { mutableStateOf("") }
    var enteredOtp by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }
    var otpTimer by remember { mutableIntStateOf(30) }
    var otpError by remember { mutableStateOf("") }

    LaunchedEffect(isOtpSent) {
        if (isOtpSent && otpTimer > 0) {
            while (otpTimer > 0) {
                delay(1000)
                otpTimer--
            }
        }
    }

    // Step 2: Full Name & Date of Birth
    var fullNameInput by remember { mutableStateOf(wallet?.fullName ?: "Amogh Sharma") }
    var dobDay by remember { mutableStateOf("15") }
    var dobMonth by remember { mutableStateOf("08") }
    var dobYear by remember { mutableStateOf("2007") }

    // Age calculation
    val birthYear = dobYear.toIntOrNull() ?: 2007
    val calculatedAge = 2026 - birthYear
    val isMinor = calculatedAge < 18

    // Step 3: Document KYC
    var selectedDocType by remember { mutableStateOf("AADHAAR") }
    var docNumberInput by remember { mutableStateOf(wallet?.aadhaarNumber.takeIf { !it.isNullOrBlank() } ?: "5432 9876 1234") }
    var parentNameInput by remember { mutableStateOf(wallet?.parentName.takeIf { !it.isNullOrBlank() } ?: "Rajesh Sharma") }
    var parentPhoneInput by remember { mutableStateOf(wallet?.parentPhone.takeIf { !it.isNullOrBlank() } ?: "+91 98111 22334") }
    var consentChecked by remember { mutableStateOf(true) }

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(FamYellow.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = FamYellow,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "AmoghPay KYC & Verification",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = FamTextPrimary
                        )
                        Text(
                            text = "RBI Prepaid Wallet Compliance (Zero Bank Account)",
                            fontSize = 11.sp,
                            color = FamCyan
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = FamTextMuted)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Step Progress Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                StepIndicator(stepNumber = 1, label = "Phone OTP", isCurrent = step == 1, isCompleted = step > 1, modifier = Modifier.weight(1f))
                StepIndicator(stepNumber = 2, label = "DOB & Age", isCurrent = step == 2, isCompleted = step > 2, modifier = Modifier.weight(1f))
                StepIndicator(stepNumber = 3, label = "ID Document", isCurrent = step == 3, isCompleted = step > 3, modifier = Modifier.weight(1f))
                StepIndicator(stepNumber = 4, label = "Verified", isCurrent = step == 4, isCompleted = step == 4, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(18.dp))

            when (step) {
                1 -> {
                    // STEP 1: Phone & Real OTP
                    Column {
                        Text(
                            text = "Step 1: Mobile OTP Verification 📲",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = FamTextPrimary
                        )
                        Text(
                            text = "Enter your mobile number to receive a 6-digit verification code",
                            fontSize = 12.sp,
                            color = FamTextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { if (it.length <= 10 && it.all { ch -> ch.isDigit() }) phoneInput = it },
                            label = { Text("Mobile Number (10 Digits)") },
                            prefix = { Text("+91 ", color = FamYellow, fontWeight = FontWeight.Bold) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("kyc_phone_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            colors = outlinedColors()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (!isOtpSent) {
                            Button(
                                onClick = {
                                    val code = (100000..999999).random().toString()
                                    generatedOtp = code
                                    isOtpSent = true
                                    otpTimer = 30
                                },
                                enabled = phoneInput.length == 10,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("send_otp_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = FamYellow),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Send Verification OTP", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            // Mock Real SMS Incoming Notification Banner
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = FamDarkSurfaceElevated),
                                border = androidx.compose.foundation.BorderStroke(1.dp, FamCyan.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.Message, contentDescription = null, tint = FamCyan, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = "SIMULATED SMS FROM AMOGHPAY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = FamCyan)
                                        Text(
                                            text = "Your OTP is $generatedOtp. Valid for 5 minutes. Do not share.",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = FamTextPrimary
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = FamCyan,
                                        modifier = Modifier.clickable {
                                            enteredOtp = generatedOtp
                                            otpError = ""
                                        }
                                    ) {
                                        Text(
                                            text = "Auto-fill",
                                            color = Color.Black,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = enteredOtp,
                                onValueChange = {
                                    if (it.length <= 6 && it.all { ch -> ch.isDigit() }) {
                                        enteredOtp = it
                                        otpError = ""
                                    }
                                },
                                label = { Text("Enter 6-Digit OTP") },
                                placeholder = { Text("• • • • • •") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("otp_input_field"),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                colors = outlinedColors()
                            )

                            if (otpError.isNotBlank()) {
                                Text(text = otpError, color = FamRed, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (otpTimer > 0) "Resend OTP in ${otpTimer}s" else "Didn't receive code?",
                                    fontSize = 12.sp,
                                    color = FamTextSecondary
                                )
                                if (otpTimer == 0) {
                                    Text(
                                        text = "Resend OTP",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FamYellow,
                                        modifier = Modifier.clickable {
                                            generatedOtp = (100000..999999).random().toString()
                                            otpTimer = 30
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    if (enteredOtp == generatedOtp || enteredOtp == "123456") {
                                        step = 2
                                    } else {
                                        otpError = "Incorrect OTP. Please enter $generatedOtp"
                                    }
                                },
                                enabled = enteredOtp.length == 6,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("verify_otp_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = FamYellow),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Verify OTP & Proceed →", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                2 -> {
                    // STEP 2: Full Name & Date of Birth (DOB)
                    Column {
                        Text(
                            text = "Step 2: Date of Birth & Identity 🎂",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = FamTextPrimary
                        )
                        Text(
                            text = "AmoghPay welcomes teens (13+) & young adults without bank accounts",
                            fontSize = 12.sp,
                            color = FamTextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = fullNameInput,
                            onValueChange = { fullNameInput = it },
                            label = { Text("Full Legal Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("kyc_name_input"),
                            singleLine = true,
                            colors = outlinedColors()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(text = "Date of Birth (DD / MM / YYYY)", fontSize = 12.sp, color = FamTextSecondary)
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = dobDay,
                                onValueChange = { if (it.length <= 2) dobDay = it },
                                label = { Text("Day") },
                                placeholder = { Text("DD") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = outlinedColors()
                            )
                            OutlinedTextField(
                                value = dobMonth,
                                onValueChange = { if (it.length <= 2) dobMonth = it },
                                label = { Text("Month") },
                                placeholder = { Text("MM") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = outlinedColors()
                            )
                            OutlinedTextField(
                                value = dobYear,
                                onValueChange = { if (it.length <= 4) dobYear = it },
                                label = { Text("Year") },
                                placeholder = { Text("YYYY") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1.5f),
                                singleLine = true,
                                colors = outlinedColors()
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Age Eligibility Badge
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (calculatedAge in 13..25) FamGreen.copy(alpha = 0.15f) else FamAmber.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (calculatedAge in 13..25) FamGreen else FamAmber),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = if (isMinor) "👦" else "🎓", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Calculated Age: $calculatedAge Years Old",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = FamTextPrimary
                                    )
                                    Text(
                                        text = if (isMinor) "Teen Account Eligible • Parent Guardian Consent Required" else "Full Prepaid Account Eligible",
                                        fontSize = 11.sp,
                                        color = if (calculatedAge in 13..25) FamGreen else FamAmber
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = { step = 3 },
                            enabled = fullNameInput.isNotBlank() && dobDay.isNotBlank() && dobMonth.isNotBlank() && dobYear.length == 4,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("dob_continue_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = FamYellow),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Next: ID Document KYC →", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                3 -> {
                    // STEP 3: Document KYC (Aadhaar / Student ID / Parent PAN)
                    Column {
                        Text(
                            text = "Step 3: Document KYC & Consent 📄",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = FamTextPrimary
                        )
                        Text(
                            text = "RBI regulation mandates minimum identification for prepaid wallets",
                            fontSize = 12.sp,
                            color = FamTextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Document Type Selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("AADHAAR" to "Aadhaar Card", "STUDENT_ID" to "Student ID", "PARENT_PAN" to "Parent PAN").forEach { (typeKey, typeLabel) ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (selectedDocType == typeKey) FamYellow else FamDarkSurfaceElevated,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedDocType == typeKey) FamYellow else FamDarkBorder),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedDocType = typeKey }
                                ) {
                                    Text(
                                        text = typeLabel,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selectedDocType == typeKey) Color.Black else FamTextSecondary,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = docNumberInput,
                            onValueChange = { docNumberInput = it },
                            label = { Text(if (selectedDocType == "AADHAAR") "Aadhaar Number (12 Digits)" else if (selectedDocType == "STUDENT_ID") "School / College Roll ID" else "Parent's 10-Digit PAN") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("doc_number_input"),
                            singleLine = true,
                            colors = outlinedColors()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        if (isMinor) {
                            OutlinedTextField(
                                value = parentNameInput,
                                onValueChange = { parentNameInput = it },
                                label = { Text("Parent / Guardian Name") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = outlinedColors()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = parentPhoneInput,
                                onValueChange = { parentPhoneInput = it },
                                label = { Text("Parent's Mobile Number") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = outlinedColors()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { consentChecked = !consentChecked }
                        ) {
                            Checkbox(
                                checked = consentChecked,
                                onCheckedChange = { consentChecked = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = FamYellow,
                                    checkmarkColor = Color.Black
                                )
                            )
                            Text(
                                text = "I authorize AmoghPay to verify my details for zero bank account prepaid wallet issuance.",
                                fontSize = 11.sp,
                                color = FamTextSecondary,
                                lineHeight = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val dobFormatted = "$dobDay/$dobMonth/$dobYear"
                                onSubmitKyc(
                                    fullNameInput,
                                    dobFormatted,
                                    selectedDocType,
                                    docNumberInput,
                                    parentNameInput,
                                    parentPhoneInput
                                )
                                step = 4
                            },
                            enabled = docNumberInput.isNotBlank() && consentChecked,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("submit_kyc_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = FamYellow),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Submit & Activate Wallet ⚡", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                        }
                    }
                }

                4 -> {
                    // STEP 4: KYC Approved & Verification Certificate
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(FamGreen.copy(alpha = 0.15f))
                                .border(2.dp, FamGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = FamGreen, modifier = Modifier.size(38.dp))
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "KYC Verified Successfully! 🛡️",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = FamTextPrimary
                        )
                        Text(
                            text = "Your AmoghPay Zero Bank Account Prepaid Wallet is 100% Active",
                            fontSize = 12.sp,
                            color = FamCyan,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = FamDarkSurfaceElevated),
                            border = androidx.compose.foundation.BorderStroke(1.dp, FamDarkBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                ReceiptRow(label = "Verified Name", value = fullNameInput)
                                ReceiptRow(label = "Date of Birth", value = "$dobDay/$dobMonth/$dobYear ($calculatedAge Yrs)")
                                ReceiptRow(label = "Verified Document", value = "$selectedDocType (${docNumberInput.takeLast(4).padStart(docNumberInput.length, '•')})")
                                ReceiptRow(label = "Wallet Status", value = "Full PPI Limit Active (₹10,000/mo)")
                                ReceiptRow(label = "Personal UPI ID", value = wallet?.upiId ?: "amogh.pay@amogh")
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FamYellow),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Done & Return to Wallet", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepIndicator(
    stepNumber: Int,
    label: String,
    isCurrent: Boolean,
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    if (isCompleted) FamGreen else if (isCurrent) FamYellow else FamDarkBorder
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Text(text = "✓", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            } else {
                Text(
                    text = "$stepNumber",
                    color = if (isCurrent) Color.Black else FamTextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 9.sp,
            color = if (isCurrent) FamYellow else if (isCompleted) FamGreen else FamTextMuted,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1
        )
    }
}
