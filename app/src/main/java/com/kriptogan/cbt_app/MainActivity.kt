package com.kriptogan.cbt_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kriptogan.cbt_app.R
import kotlinx.coroutines.launch
import com.kriptogan.cbt_app.data.model.Ticket
import com.kriptogan.cbt_app.data.repository.TicketRepository
import com.kriptogan.cbt_app.ui.components.TicketList
import com.kriptogan.cbt_app.ui.screens.TicketFormScreen
import android.content.Intent
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
    
    // Force RTL layout direction
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
    
    // Initialize repository and load tickets
    LaunchedEffect(Unit) {
        tickets = TicketRepository.initialize(context)
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFE3F2FD), // Light blue
                            Color(0xFFBBDEFB), // Medium blue
                            Color(0xFF90CAF9)  // Darker blue
                        )
                    )
                )
        ) {
            // Ticket list
            TicketList(
                tickets = tickets,
                onTicketClick = { ticket ->
                    val intent = Intent(context, TicketDetailsActivity::class.java)
                    intent.putExtra("ticket", ticket)
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxSize()
            )
            
            // Enhanced Plus button positioned at top-left (RTL)
            FloatingActionButton(
                onClick = { showForm = true },
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.TopStart)
                    .offset(x = 20.dp, y = 40.dp)
                    .clip(RoundedCornerShape(16.dp)),
                containerColor = Color(0xFF1976D2),
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_feeling),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
    } // Close RTL CompositionLocalProvider
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    CBTappTheme {
        MainScreen()
    }
}