package com.kriptogan.cbt_app.data.repository

import com.kriptogan.cbt_app.data.model.Ticket
import java.time.LocalDateTime

/**
 * Repository class for managing tickets in local memory storage.
 * Implements object-oriented approach with singleton pattern for global state management.
 */
object TicketRepository {
    private val tickets = mutableListOf<Ticket>()
    
    /**
     * Adds a new ticket to the repository.
     * 
     * @param ticket The ticket to add
     * @return The added ticket with updated creation time
     */
    fun addTicket(ticket: Ticket): Ticket {
        val ticketWithTime = ticket.copy(creationTime = LocalDateTime.now())
        tickets.add(ticketWithTime)
        return ticketWithTime
    }
    
    /**
     * Retrieves all tickets from the repository.
     * 
     * @return List of all tickets, sorted by creation time (newest first)
     */
    fun getAllTickets(): List<Ticket> {
        return tickets.sortedByDescending { it.creationTime }
    }
    
    /**
     * Retrieves a ticket by its creation time.
     * 
     * @param creationTime The creation time of the ticket to find
     * @return The ticket if found, null otherwise
     */
    fun getTicketByCreationTime(creationTime: LocalDateTime): Ticket? {
        return tickets.find { it.creationTime == creationTime }
    }
    
    /**
     * Updates an existing ticket.
     * 
     * @param updatedTicket The updated ticket
     * @return true if the ticket was updated, false if not found
     */
    fun updateTicket(updatedTicket: Ticket): Boolean {
        val index = tickets.indexOfFirst { it.creationTime == updatedTicket.creationTime }
        return if (index != -1) {
            tickets[index] = updatedTicket
            true
        } else {
            false
        }
    }
    
    /**
     * Removes a ticket from the repository.
     * 
     * @param creationTime The creation time of the ticket to remove
     * @return true if the ticket was removed, false if not found
     */
    fun removeTicket(creationTime: LocalDateTime): Boolean {
        return tickets.removeIf { it.creationTime == creationTime }
    }
    
    /**
     * Clears all tickets from the repository.
     */
    fun clearAllTickets() {
        tickets.clear()
    }
    
    /**
     * Gets the total count of tickets.
     * 
     * @return Number of tickets in the repository
     */
    fun getTicketCount(): Int {
        return tickets.size
    }
}
