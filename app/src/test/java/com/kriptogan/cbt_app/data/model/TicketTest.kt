package com.kriptogan.cbt_app.data.model

import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDateTime

class TicketTest {
    
    private val sampleFeelings = listOf(
        Feelings("Anxious", 75),
        Feelings("Frustrated", 60)
    )
    
    private val sampleTicket = Ticket(
        creationTime = LocalDateTime.of(2024, 1, 15, 14, 30, 0),
        eventDescription = "Had a difficult conversation with my boss about project deadlines",
        thoughts = "I'm not good enough for this job and they're going to fire me",
        feelings = sampleFeelings,
        behaviour = "Avoided eye contact and left the meeting early",
        symptoms = "Racing heart, sweaty palms, difficulty concentrating"
    )
    
    @Test
    fun `create valid ticket with all required fields`() {
        // Given & When
        val ticket = sampleTicket
        
        // Then
        assertEquals("Had a difficult conversation with my boss about project deadlines", ticket.eventDescription)
        assertEquals("I'm not good enough for this job and they're going to fire me", ticket.thoughts)
        assertEquals(2, ticket.feelings.size)
        assertEquals("Avoided eye contact and left the meeting early", ticket.behaviour)
        assertEquals("Racing heart, sweaty palms, difficulty concentrating", ticket.symptoms)
    }
    
    @Test
    fun `getTitle returns formatted creation time`() {
        // Given
        val ticket = sampleTicket
        
        // When
        val title = ticket.getTitle()
        
        // Then
        assertEquals("15/01/2024 14:30:00", title)
    }
    
    @Test
    fun `getBodyPreview returns full description when short enough`() {
        // Given
        val shortDescription = "Short event"
        val ticket = sampleTicket.copy(eventDescription = shortDescription)
        
        // When
        val preview = ticket.getBodyPreview(100)
        
        // Then
        assertEquals(shortDescription, preview)
    }
    
    @Test
    fun `getBodyPreview truncates long description with ellipsis`() {
        // Given
        val longDescription = "This is a very long event description that should be truncated when displayed in the ticket body preview"
        val ticket = sampleTicket.copy(eventDescription = longDescription)
        
        // When
        val preview = ticket.getBodyPreview(50)
        
        // Then
        assertEquals("This is a very long event description th...", preview)
        assertTrue(preview.endsWith("..."))
        assertTrue(preview.length <= 50)
    }
    
    @Test
    fun `getFeelingsSummary returns formatted feelings list`() {
        // Given
        val ticket = sampleTicket
        
        // When
        val summary = ticket.getFeelingsSummary()
        
        // Then
        assertEquals("Anxious (75%), Frustrated (60%)", summary)
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun `throws exception when event description is blank`() {
        // When
        sampleTicket.copy(eventDescription = "")
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun `throws exception when thoughts is blank`() {
        // When
        sampleTicket.copy(thoughts = "")
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun `throws exception when behaviour is blank`() {
        // When
        sampleTicket.copy(behaviour = "")
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun `throws exception when symptoms is blank`() {
        // When
        sampleTicket.copy(symptoms = "")
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun `throws exception when feelings list is empty`() {
        // When
        sampleTicket.copy(feelings = emptyList())
    }
}
