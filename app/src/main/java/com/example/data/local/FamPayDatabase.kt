package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.MoneyRequest
import com.example.data.model.PaymentTransaction
import com.example.data.model.RewardItem
import com.example.data.model.SavedContact
import com.example.data.model.WalletAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        WalletAccount::class,
        PaymentTransaction::class,
        SavedContact::class,
        RewardItem::class,
        MoneyRequest::class
    ],
    version = 2,
    exportSchema = false
)
abstract class FamPayDatabase : RoomDatabase() {

    abstract fun walletDao(): WalletDao
    abstract fun transactionDao(): TransactionDao
    abstract fun contactDao(): ContactDao
    abstract fun rewardDao(): RewardDao
    abstract fun moneyRequestDao(): MoneyRequestDao

    companion object {
        @Volatile
        private var INSTANCE: FamPayDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): FamPayDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FamPayDatabase::class.java,
                    "amoghpay_wallet.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }

        private suspend fun populateInitialData(database: FamPayDatabase) {
            val walletDao = database.walletDao()
            val transactionDao = database.transactionDao()
            val contactDao = database.contactDao()
            val rewardDao = database.rewardDao()
            val requestDao = database.moneyRequestDao()

            // 1. Initial Wallet Account (Zero Bank Account, Prepaid Model)
            walletDao.insertOrUpdate(
                WalletAccount(
                    id = 1,
                    fullName = "Amogh Sharma",
                    upiId = "amogh.pay@amogh",
                    phone = "+91 98765 43210",
                    balance = 2450.00,
                    famCoins = 850,
                    cardFrozen = false,
                    onlineTxnEnabled = true,
                    contactlessEnabled = true,
                    monthlySpendLimit = 10000.0,
                    currentMonthSpend = 1835.0,
                    cardTheme = "cyber_gold",
                    streakDays = 6,
                    cardNumber = "4321 8890 1234 5678",
                    cardExpiry = "08/30",
                    cardCvv = "742",
                    isKycVerified = true,
                    dob = "15/08/2007",
                    aadhaarNumber = "5432 9876 1234",
                    kycDocumentType = "AADHAAR",
                    parentName = "Rajesh Sharma",
                    parentPhone = "+91 98111 22334",
                    kycTier = "FULL_KYC"
                )
            )

            // 2. Initial realistic transactions
            val now = System.currentTimeMillis()
            val transactions = listOf(
                PaymentTransaction(
                    title = "Starbucks Coffee",
                    subtitle = "Paid via FamPay UPI",
                    upiId = "starbucks@icici",
                    amount = 180.0,
                    type = "DEBIT",
                    category = "FOOD",
                    timestamp = now - (1000 * 60 * 45), // 45 mins ago
                    status = "SUCCESS",
                    referenceId = "UPI/329841209384",
                    note = "Caramel Frappuccino ☕"
                ),
                PaymentTransaction(
                    title = "Mom (Monthly Allowance)",
                    subtitle = "Received into Wallet",
                    upiId = "mom.sharma@fam",
                    amount = 1500.0,
                    type = "CREDIT",
                    category = "POCKET_MONEY",
                    timestamp = now - (1000 * 60 * 60 * 5), // 5 hours ago
                    status = "SUCCESS",
                    referenceId = "UPI/329810948301",
                    note = "For books & weekend treat ❤️"
                ),
                PaymentTransaction(
                    title = "PVR Cinemas",
                    subtitle = "Paid via FamCard Tap",
                    upiId = "pvrcinemas@hdfc",
                    amount = 340.0,
                    type = "DEBIT",
                    category = "ENTERTAINMENT",
                    timestamp = now - (1000 * 60 * 60 * 26), // Yesterday
                    status = "SUCCESS",
                    referenceId = "UPI/329701928374",
                    note = "Dune Part 2 tickets 🍿"
                ),
                PaymentTransaction(
                    title = "Rohan Verma",
                    subtitle = "Split pizza bill",
                    upiId = "rohan@fam",
                    amount = 250.0,
                    type = "DEBIT",
                    category = "TRANSFER",
                    timestamp = now - (1000 * 60 * 60 * 48), // 2 days ago
                    status = "SUCCESS",
                    referenceId = "UPI/329598201948",
                    note = "Pizza party split 🍕"
                ),
                PaymentTransaction(
                    title = "Blinkit Instant Mart",
                    subtitle = "Paid via QR Scan",
                    upiId = "blinkit@axis",
                    amount = 65.0,
                    type = "DEBIT",
                    category = "SHOPPING",
                    timestamp = now - (1000 * 60 * 60 * 72),
                    status = "SUCCESS",
                    referenceId = "UPI/329481928301",
                    note = "Energy drink & chips"
                )
            )
            transactionDao.insertAll(transactions)

            // 3. Contacts
            val contacts = listOf(
                SavedContact(name = "Mom (Allowance)", upiId = "mom.sharma@fam", phone = "+91 98111 22334", avatarColorHex = "#FF5252", isFavorite = true),
                SavedContact(name = "Dad", upiId = "dad.sharma@fam", phone = "+91 98111 22335", avatarColorHex = "#00E5FF", isFavorite = true),
                SavedContact(name = "Rohan Verma", upiId = "rohan@fam", phone = "+91 98222 33445", avatarColorHex = "#FFC727", isFavorite = true),
                SavedContact(name = "Priya Patel", upiId = "priya.p@fam", phone = "+91 98333 44556", avatarColorHex = "#9D4EDD", isFavorite = false),
                SavedContact(name = "Kabir Singh", upiId = "kabir@fam", phone = "+91 98444 55667", avatarColorHex = "#00E676", isFavorite = false),
                SavedContact(name = "Sneha Roy", upiId = "sneha.roy@okhdfc", phone = "+91 98555 66778", avatarColorHex = "#FF9F1C", isFavorite = false)
            )
            contactDao.insertAll(contacts)

            // 4. Rewards
            val rewards = listOf(
                RewardItem(title = "Flat ₹100 OFF on Zomato", brandName = "Zomato", description = "No min order required. Valid on all restaurants.", coinCost = 200, couponCode = "FAMZOMATO100", category = "FOOD"),
                RewardItem(title = "Spotify Student 1-Month Free", brandName = "Spotify", description = "Ad-free music listening with unlimited skips.", coinCost = 350, couponCode = "FAMSPOTIFYPREM", category = "ENTERTAINMENT"),
                RewardItem(title = "25% OFF Myntra GenZ Collection", brandName = "Myntra", description = "Exclusive teen styles, hoodies & sneakers.", coinCost = 300, couponCode = "FAMSTYLE25", category = "SHOPPING"),
                RewardItem(title = "Google Play ₹150 Gift Code", brandName = "Google Play", description = "Redeem for games, apps & in-game currency.", coinCost = 500, couponCode = "PLAYFAM150GIFT", category = "GAMING"),
                RewardItem(title = "Domino's Pizza 30% Discount", brandName = "Domino's", description = "On orders above ₹300. Fresh cheese burst!", coinCost = 250, couponCode = "FAMPIZZA30", category = "FOOD")
            )
            rewardDao.insertAll(rewards)

            // 5. Initial Pocket Money Request
            requestDao.insertRequest(
                MoneyRequest(
                    requestedFrom = "Mom",
                    upiId = "mom.sharma@fam",
                    amount = 500.0,
                    reason = "School Project Materials & Notebooks",
                    status = "PENDING",
                    timestamp = now - (1000 * 60 * 120)
                )
            )
        }
    }
}
