package com.kriptogan.cbt_app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kriptogan.cbt_app.R
import com.kriptogan.cbt_app.data.model.Ticket

@Composable
fun TicketList(
    tickets: List<Ticket>,
    modifier: Modifier = Modifier
) {
    if (tickets.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 80.dp), // Extra top padding to account for status bar and button
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.no_tickets_yet),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = stringResource(R.string.tap_plus_to_create),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                top = 140.dp, // Extra top padding to account for status bar and button
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            )
        ) {
            items(tickets) { ticket ->
                TicketCard(ticket = ticket)
            }
        }
    }
}

@Composable
private fun TicketCard(
    ticket: Ticket,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Title - showing creation time
            Text(
                text = ticket.getTitle(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Body - showing part of event description
            Text(
                text = ticket.getBodyPreview(150),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
