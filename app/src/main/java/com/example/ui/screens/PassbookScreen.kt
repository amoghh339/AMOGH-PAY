package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PaymentTransaction
import com.example.data.model.WalletAccount
import com.example.ui.components.ReceiptRow
import com.example.ui.components.outlinedColors
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassbookScreen(
    wallet: WalletAccount?,
    transactions: List<PaymentTransaction>
) {
    val clipboardManager = LocalClipboardManager.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") }
    var selectedTxForDetails by remember { mutableStateOf<PaymentTransaction?>(null) }

    val filterOptions = listOf(
        "ALL" to "All",
        "FOOD" to "🍔 Food",
        "SHOPPING" to "🛍️ Shopping",
        "ENTERTAINMENT" to "🎮 Gaming",
        "POCKET_MONEY" to "💰 Allowance",
        "TRANSFER" to "👥 Transfers"
    )

    val filteredTransactions = transactions.filter { tx ->
        val matchesCategory = if (selectedFilter == "ALL") true else tx.category == selectedFilter
        val matchesSearch = if (searchQuery.isBlank()) true else {
            tx.title.contains(searchQuery, ignoreCase = true) ||
            tx.subtitle.contains(searchQuery, ignoreCase = true) ||
            tx.upiId.contains(searchQuery, ignoreCase = true) ||
            tx.note.contains(searchQuery, ignoreCase = true)
        }
        matchesCategory && matchesSearch
    }

    val totalSpent = transactions.filter { it.type == "DEBIT" }.sumOf { it.amount }
    val totalReceived = transactions.filter { it.type == "CREDIT" }.sumOf { it.amount }

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
                    text = "Passbook & Spends 📊",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = FamTextPrimary
                )
                Text(
                    text = "Zero Bank Account prepaid transaction ledger & insights",
                    fontSize = 12.sp,
                    color = FamCyan
                )
            }
        }

        // Monthly In/Out Summary
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("spends_summary_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = FamDarkSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, FamDarkBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Total Spent", fontSize = 12.sp, color = FamTextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "₹${"%.2f".format(totalSpent)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = FamRed
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(40.dp)
                            .background(FamDarkBorder)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Total Allowance Added", fontSize = 12.sp, color = FamTextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "₹${"%.2f".format(totalReceived)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = FamGreen
                        )
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by merchant, friend, or note...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("passbook_search"),
                singleLine = true,
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = FamTextMuted)
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = FamTextMuted)
                        }
                    }
                },
                colors = outlinedColors()
            )
        }

        // Filter Pills
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filterOptions) { (key, label) ->
                    FilterChip(
                        selected = selectedFilter == key,
                        onClick = { selectedFilter = key },
                        label = { Text(label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FamYellow,
                            selectedLabelColor = Color.Black,
                            containerColor = FamDarkSurfaceElevated,
                            labelColor = FamTextSecondary
                        )
                    )
                }
            }
        }

        // Transactions List
        if (filteredTransactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No matching transactions found",
                        fontSize = 13.sp,
                        color = FamTextMuted
                    )
                }
            }
        } else {
            items(filteredTransactions) { tx ->
                Box(modifier = Modifier.clickable { selectedTxForDetails = tx }) {
                    com.example.ui.screens.TransactionListItem(transaction = tx)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Transaction Details Sheet
    if (selectedTxForDetails != null) {
        val tx = selectedTxForDetails!!
        val dateFormatter = remember { SimpleDateFormat("dd MMMM yyyy, hh:mm:ss a", Locale.getDefault()) }

        ModalBottomSheet(
            onDismissRequest = { selectedTxForDetails = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = FamDarkSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Transaction Details 📄",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = FamTextPrimary
                    )
                    IconButton(onClick = { selectedTxForDetails = null }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = FamTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FamDarkSurfaceElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FamDarkBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = tx.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = FamTextPrimary
                        )
                        Text(
                            text = "${if (tx.type == "CREDIT") "+" else "-"}₹${"%.2f".format(tx.amount)}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = if (tx.type == "CREDIT") FamGreen else FamYellow
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        ReceiptRow(label = "Status", value = tx.status)
                        ReceiptRow(label = "Type", value = if (tx.type == "CREDIT") "Credit to Prepaid Wallet" else "Debit from Prepaid Wallet")
                        ReceiptRow(label = "Category", value = tx.category)
                        ReceiptRow(label = "UPI ID", value = tx.upiId)
                        ReceiptRow(label = "UTR / Reference No", value = tx.referenceId)
                        ReceiptRow(label = "Date & Time", value = dateFormatter.format(Date(tx.timestamp)))
                        if (tx.note.isNotBlank()) {
                            ReceiptRow(label = "Note", value = tx.note)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = FamDarkSurfaceElevated,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            clipboardManager.setText(AnnotatedString(tx.referenceId))
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = FamYellow, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Copy UTR Reference Number",
                            color = FamYellow,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
