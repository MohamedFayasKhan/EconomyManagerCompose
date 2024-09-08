package com.mohamedfayaskhan.economywear.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.Text
import com.mohamedfayaskhan.economywear.presentation.constant.Constant
import com.mohamedfayaskhan.economywear.presentation.viewmodel.DataViewModel
import java.util.Locale

@Composable
fun TransactionScreen(viewModel: DataViewModel) {
    val transactions = viewModel.transactions
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp)
    ) {
        items(transactions) {transaction ->
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
                    .padding(16.dp),
                onClick = { /*TODO*/ }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = transaction.subject,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(8.dp)
                    )
                    Text(
                        text = "Rs." + String.format(Locale.getDefault(), "%.2f", transaction.amount.toDouble()),
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(8.dp)
                    )
                    Text(
                        text = transaction.date,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(8.dp)
                    )
                    Text(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        text =
                        when (transaction.type) {
                            Constant.SPENT -> {
                                val from = viewModel.bankMap[transaction.from]
                                "Paid from " + from?.name
                            }

                            Constant.BANK_TO_BANK -> {
                                val from = viewModel.bankMap[transaction.from]
                                val to = viewModel.bankMap[transaction.to]
                                "Transfer from " + from?.name + " to " + to?.name
                            }

                            Constant.BANK_TO_PARTY -> {
                                val from = viewModel.bankMap[transaction.from]
                                val to = viewModel.partyMap[transaction.to]
                                "Sent from " + from?.name + " to " + to?.name
                            }

                            Constant.PARTY_TO_BANK -> {
                                val from = viewModel.partyMap[transaction.from]
                                val to = viewModel.bankMap[transaction.to]
                                "Received from ${from?.name} to ${to?.name}"
                            }

                            Constant.ADD_BALANCE_TO_BANK -> {
                                val to = viewModel.bankMap[transaction.to]
                                "Credited to " + to?.name
                            }

                            Constant.REDUCE_BALANCE_FROM_BANK -> {
                                val from = viewModel.bankMap[transaction.from]
                                "Debited from " + from?.name
                            }

                            Constant.ADD_BALANCE_TO_PARTY -> {
                                val to = viewModel.partyMap[transaction.to]
                                "Added to " + to?.name
                            }

                            Constant.REDUCE_BALANCE_FROM_PARTY -> {
                                val from = viewModel.partyMap[transaction.from]
                                "Reduced to " + from?.name
                            }

                            else -> {
                                "Something went wrong"
                            }
                        })
                }
            }
        }
    }
}