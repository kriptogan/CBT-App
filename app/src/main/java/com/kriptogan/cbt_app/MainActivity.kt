package com.kriptogan.cbt_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.kriptogan.cbt_app.data.model.Ticket
import com.kriptogan.cbt_app.data.repository.TicketRepository
import com.kriptogan.cbt_app.ui.components.TicketList
import com.kriptogan.cbt_app.ui.screens.TicketFormScreen
import com.kriptogan.cbt_app.ui.theme.CBTappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            CBTappTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    var showForm by remember { mutableStateOf(false) }
    var tickets by remember { mutableStateOf<List<Ticket>>(emptyList()) }
    val context = LocalContext.current
    
    // Initialize repository and load tickets
    LaunchedEffect(Unit) {
        android.util.Log.d("datastore test", "MainActivity: Starting repository initialization...")
        tickets = TicketRepository.initialize(context)
        android.util.Log.d("datastore test", "MainActivity: Repository initialized, loaded ${tickets.size} tickets")
    }
    
    // Refresh tickets when returning from form
    LaunchedEffect(showForm) {
        if (!showForm) {
            tickets = TicketRepository.getAllTickets()
        }
    }
    
    if (showForm) {
        TicketFormScreen(
            onBack = { showForm = false }
        )
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            // Ticket list
            TicketList(
                tickets = tickets,
                modifier = Modifier.fillMaxSize()
            )
            
            // Plus button positioned at top-right
            FloatingActionButton(
                onClick = { showForm = true },
                modifier = Modifier
                    .size(72.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = (-20).dp, y = 40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add new ticket"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    CBTappTheme {
        MainScreen()
    }
}