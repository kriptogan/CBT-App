package com.kriptogan.cbt_app.data.repository

import android.content.Context
import com.kriptogan.cbt_app.data.model.Ticket
import com.kriptogan.cbt_app.data.persistence.TicketJsonDataStoreManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDateTime

/**
 * Repository class for managing tickets with persistent storage.
 * Implements object-oriented approach with singleton pattern for global state management.
 */
object TicketRepository {
    private val tickets = mutableListOf<Ticket>()
    private var dataStoreManager: TicketJsonDataStoreManager? = null
    private var isInitialized = false
    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    /**
     * Initializes the repository with a context for persistent storage.
     * This should be called once when the app starts.
     * @return List of loaded tickets
     */
    suspend fun initialize(context: Context): List<Ticket> {
        if (!isInitialized) {
            dataStoreManager = TicketJsonDataStoreManager(context)
            loadTicketsFromStorage()
            isInitialized = true
        }
        return tickets.toList()
    }
    
    /**
     * Adds a new ticket to the repository.
     * 
     * @param ticket The ticket to add
     * @return The added ticket
     */
    fun addTicket(ticket: Ticket): Ticket {
        android.util.Log.d("TicketRepository", "Adding ticket: ${ticket.getTitle()}")
        android.util.Log.d("TicketRepository", "Creation time: ${ticket.creationTime}")
        
        // Use the ticket as-is without overriding creation time
        tickets.add(ticket)
        android.util.Log.d("TicketRepository", "Ticket added to memory, total tickets: ${tickets.size}")
        android.util.Log.d("TicketRepository", "Final ticket title: ${ticket.getTitle()}")
        
        repositoryScope.launch {
            saveTicketsToStorage()
        }
        
        android.util.Log.d("TicketRepository", "Ticket save operation initiated")
        return ticket
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
            repositoryScope.launch {
                saveTicketsToStorage()
            }
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
        val removed = tickets.removeIf { it.creationTime == creationTime }
        if (removed) {
            repositoryScope.launch {
                saveTicketsToStorage()
            }
        }
        return removed
    }
    
    /**
     * Clears all tickets from the repository.
     */
    fun clearAllTickets() {
        tickets.clear()
        repositoryScope.launch {
            saveTicketsToStorage()
        }
    }
    
    /**
     * Gets the total count of tickets.
     * 
     * @return Number of tickets in the repository
     */
    fun getTicketCount(): Int {
        return tickets.size
    }
    
    /**
     * Loads tickets from persistent storage.
     */
    private suspend fun loadTicketsFromStorage() {
        dataStoreManager?.let { manager ->
           val savedTickets = manager.loadTickets()
            tickets.clear()
            tickets.addAll(savedTickets)
           } ?: run {
            android.util.Log.e("datastore test", "Repository: ❌ DataStore manager is null during load!")
        }
    }
    
    /**
     * Saves tickets to persistent storage.
     */
    private suspend fun saveTicketsToStorage() {
        dataStoreManager?.let { manager ->
            manager.saveTickets(tickets)
        } ?: run {
            android.util.Log.e("datastore test", "Repository: ❌ DataStore manager is null!")
        }
    }
}
