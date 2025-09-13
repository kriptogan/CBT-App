package com.kriptogan.cbt_app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kriptogan.cbt_app.data.model.Feelings
import com.kriptogan.cbt_app.data.model.Ticket
import com.kriptogan.cbt_app.data.repository.TicketRepository
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketFormScreen(
    onBack: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }
    val totalSteps = 6
    
    // Form state
    var eventDescription by remember { mutableStateOf("") }
    var thoughts by remember { mutableStateOf("") }
    var feelings by remember { mutableStateOf(listOf<Feelings>()) }
    var behaviour by remember { mutableStateOf("") }
    var symptoms by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Ticket - Step ${currentStep + 1}/$totalSteps") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Step content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = getStepTitle(currentStep),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = getStepDescription(currentStep),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Form content based on current step
                when (currentStep) {
                    0 -> EventDescriptionStep(
                        eventDescription = eventDescription,
                        onEventDescriptionChange = { eventDescription = it }
                    )
                    1 -> ThoughtsStep(
                        thoughts = thoughts,
                        onThoughtsChange = { thoughts = it }
                    )
                    2 -> FeelingsStep(
                        feelings = feelings,
                        onFeelingsChange = { feelings = it }
                    )
                    3 -> BehaviourStep(
                        behaviour = behaviour,
                        onBehaviourChange = { behaviour = it }
                    )
                    4 -> SymptomsStep(
                        symptoms = symptoms,
                        onSymptomsChange = { symptoms = it }
                    )
                    5 -> ReviewStep(
                        eventDescription = eventDescription,
                        thoughts = thoughts,
                        feelings = feelings,
                        behaviour = behaviour,
                        symptoms = symptoms
                    )
                }
            }
            
            // Navigation buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Back button
                if (currentStep > 0) {
                    OutlinedButton(
                        onClick = { currentStep-- },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = null
                        )
                        Text("Back")
                    }
                }
                
                // Next/Finish button
                Button(
                    onClick = {
                        if (currentStep < totalSteps - 1) {
                            currentStep++
                        } else {
                            // Save ticket and go back
                            val validFeelings = feelings.filter { it.isValid() }
                            val ticket = Ticket(
                                creationTime = LocalDateTime.now(),
                                eventDescription = eventDescription,
                                thoughts = thoughts,
                                feelings = validFeelings,
                                behaviour = behaviour,
                                symptoms = symptoms
                            )
                            TicketRepository.addTicket(ticket)
                            onBack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (currentStep < totalSteps - 1) "Next" else "Finish"
                    )
                    if (currentStep < totalSteps - 1) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = null
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EventDescriptionStep(
    eventDescription: String,
    onEventDescriptionChange: (String) -> Unit
) {
    OutlinedTextField(
        value = eventDescription,
        onValueChange = onEventDescriptionChange,
        label = { Text("Event Description") },
        placeholder = { Text("Describe what happened...") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 4,
        maxLines = 8
    )
}

@Composable
private fun ThoughtsStep(
    thoughts: String,
    onThoughtsChange: (String) -> Unit
) {
    OutlinedTextField(
        value = thoughts,
        onValueChange = onThoughtsChange,
        label = { Text("Your Thoughts") },
        placeholder = { Text("What were you thinking?") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 4,
        maxLines = 8
    )
}

@Composable
private fun FeelingsStep(
    feelings: List<Feelings>,
    onFeelingsChange: (List<Feelings>) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Add your feelings with intensity levels:",
            style = MaterialTheme.typography.bodyMedium
        )
        
        feelings.forEachIndexed { index, feeling ->
            FeelingItem(
                feeling = feeling,
                onFeelingChange = { newFeeling ->
                    val newFeelings = feelings.toMutableList()
                    newFeelings[index] = newFeeling
                    onFeelingsChange(newFeelings)
                },
                onRemove = {
                    val newFeelings = feelings.toMutableList()
                    newFeelings.removeAt(index)
                    onFeelingsChange(newFeelings)
                }
            )
        }
        
        Button(
            onClick = {
                val newFeelings = feelings.toMutableList()
                newFeelings.add(Feelings("", 50))
                onFeelingsChange(newFeelings)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
            Text("Add Feeling")
        }
    }
}

@Composable
private fun FeelingItem(
    feeling: Feelings,
    onFeelingChange: (Feelings) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = feeling.description,
                    onValueChange = { onFeelingChange(feeling.copy(description = it)) },
                    label = { Text("Feeling") },
                    placeholder = { Text("e.g., Anxious, Happy, Sad") },
                    modifier = Modifier.weight(1f)
                )
                
                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove feeling"
                    )
                }
            }
            
            Text("Intensity: ${feeling.intensity}%")
            
            Slider(
                value = feeling.intensity.toFloat(),
                onValueChange = { onFeelingChange(feeling.copy(intensity = it.toInt())) },
                valueRange = 0f..100f,
                steps = 9
            )
        }
    }
}

@Composable
private fun BehaviourStep(
    behaviour: String,
    onBehaviourChange: (String) -> Unit
) {
    OutlinedTextField(
        value = behaviour,
        onValueChange = onBehaviourChange,
        label = { Text("Your Behaviour") },
        placeholder = { Text("How did you act or respond?") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 4,
        maxLines = 8
    )
}

@Composable
private fun SymptomsStep(
    symptoms: String,
    onSymptomsChange: (String) -> Unit
) {
    OutlinedTextField(
        value = symptoms,
        onValueChange = onSymptomsChange,
        label = { Text("Physical/Emotional Symptoms") },
        placeholder = { Text("What symptoms did you notice?") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 4,
        maxLines = 8
    )
}

@Composable
private fun ReviewStep(
    eventDescription: String,
    thoughts: String,
    feelings: List<Feelings>,
    behaviour: String,
    symptoms: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ReviewCard("Event Description", eventDescription)
        ReviewCard("Thoughts", thoughts)
        ReviewCard("Feelings", feelings.joinToString(", ") { "${it.description} (${it.intensity}%)" })
        ReviewCard("Behaviour", behaviour)
        ReviewCard("Symptoms", symptoms)
    }
}

@Composable
private fun ReviewCard(title: String, content: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

private fun getStepTitle(step: Int): String {
    return when (step) {
        0 -> "Event Description"
        1 -> "Thoughts"
        2 -> "Feelings"
        3 -> "Behaviour"
        4 -> "Symptoms"
        5 -> "Review"
        else -> "Unknown Step"
    }
}

private fun getStepDescription(step: Int): String {
    return when (step) {
        0 -> "Describe the event that triggered this ticket"
        1 -> "What thoughts did you have about this event?"
        2 -> "What feelings did you experience? (with intensity)"
        3 -> "How did you behave in response to this event?"
        4 -> "What physical or emotional symptoms did you notice?"
        5 -> "Review all information before creating the ticket"
        else -> "Unknown step description"
    }
}