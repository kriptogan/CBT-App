package com.kriptogan.cbt_app.data.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Data class representing a CBT ticket with all required attributes.
 * 
 * @param creationTime The date-time when the ticket was created
 * @param eventDescription Description of the event that triggered the ticket
 * @param thoughts Description of thoughts about the event
 * @param feelings List of feelings with their intensity levels
 * @param behaviour Description of behavior
 * @param symptoms Description of symptoms
 */
data class Ticket(
    val creationTime: LocalDateTime,
    val eventDescription: String,
    val thoughts: String,
    val feelings: List<Feelings>,
    val behaviour: String,
    val symptoms: String
) {
    init {
        require(eventDescription.isNotBlank()) { "Event description cannot be blank" }
        require(thoughts.isNotBlank()) { "Thoughts cannot be blank" }
        require(behaviour.isNotBlank()) { "Behaviour cannot be blank" }
        require(symptoms.isNotBlank()) { "Symptoms cannot be blank" }
        require(feelings.isNotEmpty()) { "At least one feeling must be specified" }
        require(feelings.all { it.isValid() }) { "All feelings must have valid descriptions" }
    }
    
    /**
     * Returns the formatted creation time as title (dd/MM/yyyy HH:mm:ss).
     */
    fun getTitle(): String {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        return creationTime.format(formatter)
    }
    
    /**
     * Returns a truncated version of the event description for display in the body.
     * Shows "..." if the description is too long.
     * 
     * @param maxLength Maximum length of the truncated description
     */
    fun getBodyPreview(maxLength: Int = 100): String {
        return if (eventDescription.length <= maxLength) {
            eventDescription
        } else {
            eventDescription.take(maxLength - 3) + "..."
        }
    }
    
    /**
     * Returns a formatted string of all feelings with their intensities.
     */
    fun getFeelingsSummary(): String {
        return feelings.joinToString(", ") { it.getFormattedString() }
    }
}
