package com.mohamedkhan.economymanagercompose.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mohamedkhan.economymanagercompose.R
import com.mohamedkhan.economymanagercompose.signin.GoogleAuthClient
import com.mohamedkhan.economymanagercompose.viewModel.DataViewModel

@Composable
fun HomeScreen(googleAuthClient: GoogleAuthClient, viewModel: DataViewModel) {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .verticalScroll(scrollState)
    ) {
        HeaderComponent(googleAuthClient)
        Spacer(modifier = Modifier.size(16.dp))
        IncomeExpenseCard(viewModel)
        Spacer(modifier = Modifier.size(16.dp))
    }
}

@Composable
fun IncomeExpenseCard(viewModel: DataViewModel) {
    val income = viewModel.incomeLiveData.observeAsState()
    val expense = viewModel.expenseLiveData.observeAsState()
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        CardIncomeExpense(Icons.Filled.KeyboardArrowUp, stringResource(id = R.string.net_income), income.value.toString())
        CardIncomeExpense(Icons.Filled.KeyboardArrowDown, stringResource(R.string.net_expense), expense.value.toString())
    }
}

@Composable
fun CardIncomeExpense(icon: ImageVector, name: String, value: String) {
    Card(modifier = Modifier.padding(10.dp)) {
        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .size(50.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(imageVector = icon, contentDescription = "")
            }
            Spacer(modifier = Modifier.size(10.dp))
            Column(verticalArrangement = Arrangement.Center) {
                Text(text = name)
                Text(text = value, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.size(10.dp))
        }
    }
}

@Composable
fun HeaderComponent(googleAuthClient: GoogleAuthClient) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = stringResource(R.string.welcome))
            Text(
                text = googleAuthClient.getSignedInUser()?.displayName.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
        AsyncImage(
            model = googleAuthClient.getSignedInUser()?.photoUrl,
            contentDescription = stringResource(R.string.profile_picture),
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}