package com.example.data.repository

import com.example.data.local.FamPayDatabase
import com.example.data.model.MoneyRequest
import com.example.data.model.PaymentTransaction
import com.example.data.model.RewardItem
import com.example.data.model.SavedContact
import com.example.data.model.WalletAccount
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class FamPayRepository(private val database: FamPayDatabase) {

    private val walletDao = database.walletDao()
    private val transactionDao = database.transactionDao()
    private val contactDao = database.contactDao()
    private val rewardDao = database.rewardDao()
    private val moneyRequestDao = database.moneyRequestDao()

    val walletFlow: Flow<WalletAccount?> = walletDao.getWallet()
    val transactionsFlow: Flow<List<PaymentTransaction>> = transactionDao.getAllTransactions()
    val contactsFlow: Flow<List<SavedContact>> = contactDao.getAllContacts()
    val rewardsFlow: Flow<List<RewardItem>> = rewardDao.getAllRewards()
    val moneyRequestsFlow: Flow<List<MoneyRequest>> = moneyRequestDao.getAllRequests()

    suspend fun sendPayment(
        recipientName: String,
        upiId: String,
        amount: Double,
        category: String,
        note: String
    ): Result<PaymentTransaction> {
        val currentWallet = walletDao.getWalletDirect() ?: return Result.failure(Exception("Wallet not initialized"))

        if (currentWallet.cardFrozen) {
            return Result.failure(Exception("FamCard / Wallet is currently frozen. Please unfreeze in Card tab to pay."))
        }

        if (currentWallet.balance < amount) {
            return Result.failure(Exception("Insufficient wallet balance. Please add money or request allowance."))
        }

        if (currentWallet.currentMonthSpend + amount > currentWallet.monthlySpendLimit) {
            return Result.failure(Exception("Monthly spending limit of ₹${currentWallet.monthlySpendLimit.toInt()} reached."))
        }

        // Deduct balance & add coins (e.g. 10% of amount in FamCoins, min 10 coins)
        walletDao.adjustBalance(-amount, isDebit = true)
        val coinsEarned = maxOf(10, (amount * 0.1).toInt())
        walletDao.addCoins(coinsEarned)

        val refId = "UPI/" + System.currentTimeMillis().toString().takeLast(12)
        val transaction = PaymentTransaction(
            title = recipientName,
            subtitle = "Paid via FamPay UPI",
            upiId = upiId,
            amount = amount,
            type = "DEBIT",
            category = category,
            timestamp = System.currentTimeMillis(),
            status = "SUCCESS",
            referenceId = refId,
            note = note.ifBlank { "Paid from FamPay Wallet" }
        )

        val id = transactionDao.insertTransaction(transaction)
        return Result.success(transaction.copy(id = id))
    }

    suspend fun addMoneyToWallet(
        amount: Double,
        source: String, // "MOM_ALLOWANCE", "DAD_ALLOWANCE", "PREPAID_TOPUP", "DEBIT_CARD"
        note: String
    ): PaymentTransaction {
        walletDao.adjustBalance(amount, isDebit = false)
        // Bonus 25 coins for adding money
        walletDao.addCoins(25)

        val refId = "ADD/" + System.currentTimeMillis().toString().takeLast(12)
        val title = when (source) {
            "MOM_ALLOWANCE" -> "Mom (Allowance Received)"
            "DAD_ALLOWANCE" -> "Dad (Allowance Received)"
            "PREPAID_TOPUP" -> "Wallet Loaded (Zero Bank Account)"
            else -> "Money Added to Wallet"
        }

        val transaction = PaymentTransaction(
            title = title,
            subtitle = "Credited to Prepaid Balance",
            upiId = "wallet.load@fam",
            amount = amount,
            type = "CREDIT",
            category = "POCKET_MONEY",
            timestamp = System.currentTimeMillis(),
            status = "SUCCESS",
            referenceId = refId,
            note = note
        )

        val id = transactionDao.insertTransaction(transaction)
        return transaction.copy(id = id)
    }

    suspend fun toggleCardFreeze(isFrozen: Boolean) {
        walletDao.setCardFrozen(isFrozen)
    }

    suspend fun toggleOnlineTxn(enabled: Boolean) {
        walletDao.setOnlineTxnEnabled(enabled)
    }

    suspend fun toggleContactless(enabled: Boolean) {
        walletDao.setContactlessEnabled(enabled)
    }

    suspend fun updateMonthlySpendLimit(limit: Double) {
        walletDao.setMonthlySpendLimit(limit)
    }

    suspend fun updateCardTheme(theme: String) {
        walletDao.setCardTheme(theme)
    }

    suspend fun redeemReward(reward: RewardItem): Result<String> {
        val currentWallet = walletDao.getWalletDirect() ?: return Result.failure(Exception("Wallet not found"))
        if (currentWallet.famCoins < reward.coinCost) {
            return Result.failure(Exception("Need ${reward.coinCost - currentWallet.famCoins} more FamCoins to unlock this!"))
        }

        walletDao.deductCoins(reward.coinCost)
        rewardDao.redeemReward(reward.id)
        return Result.success(reward.couponCode)
    }

    suspend fun createMoneyRequest(
        targetName: String,
        upiId: String,
        amount: Double,
        reason: String
    ): MoneyRequest {
        val request = MoneyRequest(
            requestedFrom = targetName,
            upiId = upiId,
            amount = amount,
            reason = reason,
            status = "PENDING",
            timestamp = System.currentTimeMillis()
        )
        val id = moneyRequestDao.insertRequest(request)
        return request.copy(id = id)
    }

    suspend fun claimDailyStreakReward(): Int {
        val coins = (50..120).random()
        walletDao.addCoins(coins)
        return coins
    }

    suspend fun spinWheelReward(): Pair<Double, Int> {
        // Either cash bonus or coins bonus
        val isCash = (0..1).random() == 1
        if (isCash) {
            val cashAmount = listOf(10.0, 25.0, 50.0).random()
            addMoneyToWallet(cashAmount, "REWARD_CASHBACK", "Spin the Wheel Cashback 🎁")
            return Pair(cashAmount, 0)
        } else {
            val coins = listOf(100, 150, 250).random()
            walletDao.addCoins(coins)
            return Pair(0.0, coins)
        }
    }

    suspend fun addContact(name: String, upiId: String, phone: String) {
        val colors = listOf("#FFC727", "#00E5FF", "#9D4EDD", "#00E676", "#FF9F1C")
        contactDao.insertContact(
            SavedContact(
                name = name,
                upiId = upiId,
                phone = phone,
                avatarColorHex = colors.random(),
                isFavorite = false
            )
        )
    }

    suspend fun updateKycVerification(
        fullName: String,
        dob: String,
        docType: String,
        docNumber: String,
        parentName: String,
        parentPhone: String
    ) {
        walletDao.updateKyc(
            fullName = fullName,
            dob = dob,
            docType = docType,
            docNumber = docNumber,
            parentName = parentName,
            parentPhone = parentPhone,
            isVerified = true
        )
        // Bonus 100 coins for completing KYC
        walletDao.addCoins(100)
    }
}
