package com.mohamedfayaskhan.economywear.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.Text
import com.mohamedfayaskhan.economywear.presentation.viewmodel.DataViewModel
import java.util.Locale

@Composable
fun PartyScreen(viewModel: DataViewModel) {
    val parties = viewModel.parties
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp)
    ) {
        items(parties) {party ->
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(33.dp))
                    .background(Color.Gray)
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(text = party.name, modifier = Modifier.padding(10.dp))
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    text = "Rs." + String.format(Locale.getDefault(), "%.2f", party.balance.toDouble()),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (party.receivable) Color.Green else Color.Red,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }

}