package com.kriptogan.cbt_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kriptogan.cbt_app.data.model.Ticket
import com.kriptogan.cbt_app.ui.screens.TicketDetailsScreen
import com.kriptogan.cbt_app.ui.theme.CBTappTheme

class TicketDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Get the ticket from intent extras
        val ticket = intent.getSerializableExtra("ticket") as? Ticket
        
        if (ticket != null) {
            setContent {
                CBTappTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        TicketDetailsScreen(
                            ticket = ticket,
                            onBack = { finish() }
                        )
                    }
                }
            }
        } else {
            // If no ticket provided, finish the activity
            finish()
        }
    }
}
