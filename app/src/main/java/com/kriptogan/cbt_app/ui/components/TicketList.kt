package com.kriptogan.cbt_app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kriptogan.cbt_app.R
import com.kriptogan.cbt_app.data.model.Ticket

@Composable
fun TicketList(
    tickets: List<Ticket>,
    onTicketClick: (Ticket) -> Unit = {},
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
                TicketCard(
                    ticket = ticket,
                    onClick = { onTicketClick(ticket) }
                )
            }
        }
    }
}

@Composable
private fun TicketCard(
    ticket: Ticket,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header with icon and title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = ticket.getTitle(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
            }
            
            // Body with icon and description
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFF666666),
                    modifier = Modifier.padding(end = 8.dp, top = 2.dp)
                )
                Text(
                    text = ticket.getBodyPreview(150),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Subtle click indicator
            Text(
                text = "לחץ לצפייה בפרטים",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF999999),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
