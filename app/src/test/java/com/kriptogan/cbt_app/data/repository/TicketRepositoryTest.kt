package com.kriptogan.cbt_app.data.repository

import com.kriptogan.cbt_app.data.model.Feelings
import com.kriptogan.cbt_app.data.model.Ticket
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDateTime

class TicketRepositoryTest {
    
    private val sampleFeelings = listOf(
        Feelings("Anxious", 75),
        Feelings("Frustrated", 60)
    )
    
    private val sampleTicket = Ticket(
        creationTime = LocalDateTime.of(2024, 1, 15, 14, 30, 0),
        eventDescription = "Had a difficult conversation with my boss",
        thoughts = "I'm not good enough for this job",
        feelings = sampleFeelings,
        behaviour = "Avoided eye contact",
        symptoms = "Racing heart, sweaty palms"
    )
    
    @Before
    fun setUp() {
        TicketRepository.clearAllTickets()
    }
    
    @After
    fun tearDown() {
        TicketRepository.clearAllTickets()
    }
    
    @Test
    fun `addTicket adds ticket with current time`() {
        // Given
        val ticket = sampleTicket
        
        // When
        val addedTicket = TicketRepository.addTicket(ticket)
        
        // Then
        assertNotEquals(ticket.creationTime, addedTicket.creationTime)
        assertTrue(addedTicket.creationTime.isAfter(LocalDateTime.now().minusMinutes(1)))
        assertEquals(1, TicketRepository.getTicketCount())
    }
    
    @Test
    fun `getAllTickets returns tickets sorted by creation time descending`() {
        // Given
        val ticket1 = sampleTicket.copy(eventDescription = "First event")
        val ticket2 = sampleTicket.copy(eventDescription = "Second event")
        
        // When
        TicketRepository.addTicket(ticket1)
        Thread.sleep(100) // Ensure different creation times
        TicketRepository.addTicket(ticket2)
        val allTickets = TicketRepository.getAllTickets()
        
        // Then
        assertEquals(2, allTickets.size)
        assertEquals("Second event", allTickets[0].eventDescription)
        assertEquals("First event", allTickets[1].eventDescription)
    }
    
    @Test
    fun `getTicketByCreationTime returns correct ticket`() {
        // Given
        val addedTicket = TicketRepository.addTicket(sampleTicket)
        
        // When
        val foundTicket = TicketRepository.getTicketByCreationTime(addedTicket.creationTime)
        
        // Then
        assertNotNull(foundTicket)
        assertEquals(addedTicket.creationTime, foundTicket?.creationTime)
        assertEquals(sampleTicket.eventDescription, foundTicket?.eventDescription)
    }
    
    @Test
    fun `getTicketByCreationTime returns null for non-existent ticket`() {
        // Given
        val nonExistentTime = LocalDateTime.of(2024, 1, 1, 12, 0, 0)
        
        // When
        val foundTicket = TicketRepository.getTicketByCreationTime(nonExistentTime)
        
        // Then
        assertNull(foundTicket)
    }
    
    @Test
    fun `updateTicket updates existing ticket`() {
        // Given
        val addedTicket = TicketRepository.addTicket(sampleTicket)
        val updatedTicket = addedTicket.copy(eventDescription = "Updated event description")
        
        // When
        val updateResult = TicketRepository.updateTicket(updatedTicket)
        
        // Then
        assertTrue(updateResult)
        val retrievedTicket = TicketRepository.getTicketByCreationTime(addedTicket.creationTime)
        assertEquals("Updated event description", retrievedTicket?.eventDescription)
    }
    
    @Test
    fun `updateTicket returns false for non-existent ticket`() {
        // Given
        val nonExistentTicket = sampleTicket.copy(creationTime = LocalDateTime.of(2024, 1, 1, 12, 0, 0))
        
        // When
        val updateResult = TicketRepository.updateTicket(nonExistentTicket)
        
        // Then
        assertFalse(updateResult)
    }
    
    @Test
    fun `removeTicket removes existing ticket`() {
        // Given
        val addedTicket = TicketRepository.addTicket(sampleTicket)
        
        // When
        val removeResult = TicketRepository.removeTicket(addedTicket.creationTime)
        
        // Then
        assertTrue(removeResult)
        assertEquals(0, TicketRepository.getTicketCount())
        assertNull(TicketRepository.getTicketByCreationTime(addedTicket.creationTime))
    }
    
    @Test
    fun `removeTicket returns false for non-existent ticket`() {
        // Given
        val nonExistentTime = LocalDateTime.of(2024, 1, 1, 12, 0, 0)
        
        // When
        val removeResult = TicketRepository.removeTicket(nonExistentTime)
        
        // Then
        assertFalse(removeResult)
    }
    
    @Test
    fun `clearAllTickets removes all tickets`() {
        // Given
        TicketRepository.addTicket(sampleTicket)
        TicketRepository.addTicket(sampleTicket.copy(eventDescription = "Another event"))
        
        // When
        TicketRepository.clearAllTickets()
        
        // Then
        assertEquals(0, TicketRepository.getTicketCount())
        assertTrue(TicketRepository.getAllTickets().isEmpty())
    }
    
    @Test
    fun `getTicketCount returns correct count`() {
        // Given & When
        assertEquals(0, TicketRepository.getTicketCount())
        
        TicketRepository.addTicket(sampleTicket)
        assertEquals(1, TicketRepository.getTicketCount())
        
        TicketRepository.addTicket(sampleTicket.copy(eventDescription = "Another event"))
        assertEquals(2, TicketRepository.getTicketCount())
    }
}
