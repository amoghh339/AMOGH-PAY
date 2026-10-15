package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.MoneyRequest
import com.example.data.model.PaymentTransaction
import com.example.data.model.RewardItem
import com.example.data.model.SavedContact
import com.example.data.model.WalletAccount
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {
    @Query("SELECT * FROM wallet_account WHERE id = 1 LIMIT 1")
    fun getWallet(): Flow<WalletAccount?>

    @Query("SELECT * FROM wallet_account WHERE id = 1 LIMIT 1")
    suspend fun getWalletDirect(): WalletAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(wallet: WalletAccount)

    @Update
    suspend fun updateWallet(wallet: WalletAccount)

    @Query("UPDATE wallet_account SET balance = balance + :amount, currentMonthSpend = CASE WHEN :isDebit = 1 THEN currentMonthSpend + :amount ELSE currentMonthSpend END WHERE id = 1")
    suspend fun adjustBalance(amount: Double, isDebit: Boolean)

    @Query("UPDATE wallet_account SET famCoins = famCoins + :coins WHERE id = 1")
    suspend fun addCoins(coins: Int)

    @Query("UPDATE wallet_account SET famCoins = famCoins - :coins WHERE id = 1")
    suspend fun deductCoins(coins: Int)

    @Query("UPDATE wallet_account SET cardFrozen = :isFrozen WHERE id = 1")
    suspend fun setCardFrozen(isFrozen: Boolean)

    @Query("UPDATE wallet_account SET onlineTxnEnabled = :enabled WHERE id = 1")
    suspend fun setOnlineTxnEnabled(enabled: Boolean)

    @Query("UPDATE wallet_account SET contactlessEnabled = :enabled WHERE id = 1")
    suspend fun setContactlessEnabled(enabled: Boolean)

    @Query("UPDATE wallet_account SET monthlySpendLimit = :limit WHERE id = 1")
    suspend fun setMonthlySpendLimit(limit: Double)

    @Query("UPDATE wallet_account SET cardTheme = :theme WHERE id = 1")
    suspend fun setCardTheme(theme: String)

    @Query("UPDATE wallet_account SET fullName = :fullName, dob = :dob, kycDocumentType = :docType, aadhaarNumber = :docNumber, parentName = :parentName, parentPhone = :parentPhone, isKycVerified = :isVerified, kycTier = 'FULL_KYC' WHERE id = 1")
    suspend fun updateKyc(
        fullName: String,
        dob: String,
        docType: String,
        docNumber: String,
        parentName: String,
        parentPhone: String,
        isVerified: Boolean
    )
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM payment_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<PaymentTransaction>>

    @Query("SELECT * FROM payment_transactions ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentTransactions(limit: Int): Flow<List<PaymentTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: PaymentTransaction): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<PaymentTransaction>)
}

@Dao
interface ContactDao {
    @Query("SELECT * FROM saved_contacts ORDER BY isFavorite DESC, name ASC")
    fun getAllContacts(): Flow<List<SavedContact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: SavedContact): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(contacts: List<SavedContact>)
}

@Dao
interface RewardDao {
    @Query("SELECT * FROM rewards ORDER BY isRedeemed ASC, coinCost ASC")
    fun getAllRewards(): Flow<List<RewardItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rewards: List<RewardItem>)

    @Query("UPDATE rewards SET isRedeemed = 1 WHERE id = :rewardId")
    suspend fun redeemReward(rewardId: Long)
}

@Dao
interface MoneyRequestDao {
    @Query("SELECT * FROM money_requests ORDER BY timestamp DESC")
    fun getAllRequests(): Flow<List<MoneyRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: MoneyRequest): Long

    @Query("UPDATE money_requests SET status = :status WHERE id = :requestId")
    suspend fun updateStatus(requestId: Long, status: String)
}
