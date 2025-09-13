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
import androidx.compose.ui.res.stringResource
import com.kriptogan.cbt_app.R
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
                title = { 
                    Text(
                        when (currentStep) {
                            0 -> stringResource(R.string.step_event)
                            1 -> stringResource(R.string.step_thoughts)
                            2 -> stringResource(R.string.step_feelings)
                            3 -> stringResource(R.string.step_behavior)
                            4 -> stringResource(R.string.step_symptoms)
                            5 -> stringResource(R.string.step_review)
                            else -> stringResource(R.string.step_event)
                        } + " - ${currentStep + 1}/$totalSteps"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back_content_description)
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
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = null
                        )
                        Text(stringResource(R.string.back))
                    }
                }
                
                    // Next/Finish button
                    Button(
                        onClick = {
                            if (currentStep < totalSteps - 1) {
                                currentStep++
                            } else {
                                // Validate required fields before finishing
                                if (eventDescription.isBlank() || thoughts.isBlank() || behaviour.isBlank() || symptoms.isBlank()) {
                                    android.util.Log.w("TicketForm", "Cannot finish: required fields are empty")
                                    return@Button
                                }
                                
                                val validFeelings = feelings.filter { it.isValid() }
                                if (validFeelings.isEmpty()) {
                                    android.util.Log.w("TicketForm", "Cannot finish: no valid feelings")
                                    return@Button
                                }
                                
                                // Save ticket and go back
                                android.util.Log.d("TicketForm", "Finishing ticket creation...")
                                android.util.Log.d("TicketForm", "Valid feelings count: ${validFeelings.size}")
                            
                            try {
                                val creationTime = LocalDateTime.now()
                                android.util.Log.d("TicketForm", "Creating ticket with creation time: $creationTime")
                                
                                val ticket = Ticket(
                                    creationTime = creationTime,
                                    eventDescription = eventDescription,
                                    thoughts = thoughts,
                                    feelings = validFeelings,
                                    behaviour = behaviour,
                                    symptoms = symptoms
                                )
                                android.util.Log.d("TicketForm", "Ticket created successfully with title: ${ticket.getTitle()}")
                                
                                val savedTicket = TicketRepository.addTicket(ticket)
                                android.util.Log.d("TicketForm", "Ticket saved to repository: ${savedTicket.getTitle()}")
                                
                                onBack()
                            } catch (e: Exception) {
                                android.util.Log.e("TicketForm", "Error creating/saving ticket", e)
                                // Still go back to prevent user from being stuck
                                onBack()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (currentStep < totalSteps - 1) stringResource(R.string.next) else stringResource(R.string.finish)
                    )
                    if (currentStep < totalSteps - 1) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
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
        label = { Text(stringResource(R.string.event_description_label)) },
        placeholder = { Text(stringResource(R.string.event_description_hint)) },
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
        label = { Text(stringResource(R.string.thoughts_label)) },
        placeholder = { Text(stringResource(R.string.thoughts_hint)) },
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
            text = stringResource(R.string.add_feelings_description),
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
            Text(stringResource(R.string.add_feeling))
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
                    label = { Text(stringResource(R.string.feeling_description_label)) },
                    placeholder = { Text(stringResource(R.string.feeling_description_hint)) },
                    modifier = Modifier.weight(1f)
                )
                
                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.remove_feeling_content_description)
                    )
                }
            }
            
            Text(stringResource(R.string.feeling_intensity, feeling.intensity))
            
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
        label = { Text(stringResource(R.string.behavior_label)) },
        placeholder = { Text(stringResource(R.string.behavior_hint)) },
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
        label = { Text(stringResource(R.string.symptoms_label)) },
        placeholder = { Text(stringResource(R.string.symptoms_hint)) },
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
        ReviewCard(stringResource(R.string.review_event_description), eventDescription)
        ReviewCard(stringResource(R.string.review_thoughts), thoughts)
        ReviewCard(stringResource(R.string.review_feelings), feelings.joinToString(", ") { "${it.description} (${it.intensity}%)" })
        ReviewCard(stringResource(R.string.review_behaviour), behaviour)
        ReviewCard(stringResource(R.string.review_symptoms), symptoms)
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

@Composable
private fun getStepTitle(step: Int): String {
    return when (step) {
        0 -> stringResource(R.string.step_event)
        1 -> stringResource(R.string.step_thoughts)
        2 -> stringResource(R.string.step_feelings)
        3 -> stringResource(R.string.step_behavior)
        4 -> stringResource(R.string.step_symptoms)
        5 -> stringResource(R.string.step_review)
        else -> "Unknown Step"
    }
}

@Composable
private fun getStepDescription(step: Int): String {
    return when (step) {
        0 -> stringResource(R.string.step_description_event)
        1 -> stringResource(R.string.step_description_thoughts)
        2 -> stringResource(R.string.step_description_feelings)
        3 -> stringResource(R.string.step_description_behavior)
        4 -> stringResource(R.string.step_description_symptoms)
        5 -> stringResource(R.string.step_description_review)
        else -> "Unknown step description"
    }
}