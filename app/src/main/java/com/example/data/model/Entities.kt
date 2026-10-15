package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallet_account")
data class WalletAccount(
    @PrimaryKey
    val id: Int = 1,
    val fullName: String = "Amogh Sharma",
    val upiId: String = "amogh.pay@amogh",
    val phone: String = "+91 98765 43210",
    val balance: Double = 2450.0,
    val famCoins: Int = 850,
    val cardFrozen: Boolean = false,
    val onlineTxnEnabled: Boolean = true,
    val contactlessEnabled: Boolean = true,
    val monthlySpendLimit: Double = 10000.0,
    val currentMonthSpend: Double = 1835.0,
    val cardTheme: String = "cyber_gold", // cyber_gold, neon_cyan, midnight_purple
    val streakDays: Int = 6,
    val cardNumber: String = "4321 8890 1234 5678",
    val cardExpiry: String = "08/30",
    val cardCvv: String = "742",
    val isKycVerified: Boolean = false,
    val dob: String = "15/08/2007",
    val aadhaarNumber: String = "",
    val kycDocumentType: String = "AADHAAR",
    val parentName: String = "",
    val parentPhone: String = "",
    val kycTier: String = "MIN_KYC" // MIN_KYC, FULL_KYC
)

@Entity(tableName = "payment_transactions")
data class PaymentTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val subtitle: String,
    val upiId: String,
    val amount: Double,
    val type: String, // DEBIT, CREDIT
    val category: String, // FOOD, SHOPPING, ENTERTAINMENT, TRANSFER, POCKET_MONEY, BILL, REWARD
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "SUCCESS",
    val referenceId: String,
    val note: String = ""
)

@Entity(tableName = "saved_contacts")
data class SavedContact(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val upiId: String,
    val phone: String,
    val avatarColorHex: String = "#FFC727",
    val isFavorite: Boolean = false
)

@Entity(tableName = "rewards")
data class RewardItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val brandName: String,
    val description: String,
    val coinCost: Int,
    val couponCode: String,
    val isRedeemed: Boolean = false,
    val category: String = "FOOD"
)

@Entity(tableName = "money_requests")
data class MoneyRequest(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val requestedFrom: String,
    val upiId: String,
    val amount: Double,
    val reason: String,
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val timestamp: Long = System.currentTimeMillis()
)
