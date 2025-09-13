package com.kriptogan.cbt_app.data.persistence

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.kriptogan.cbt_app.data.model.Ticket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Simple JSON-based DataStore manager for tickets.
 * Uses Gson for reliable serialization/deserialization.
 */
class TicketJsonDataStoreManager(private val context: Context) {
    
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tickets")
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    
    private val gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer())
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeDeserializer())
        .create()
    
    private inner class LocalDateTimeSerializer : JsonSerializer<LocalDateTime> {
        override fun serialize(
            src: LocalDateTime?,
            typeOfSrc: Type?,
            context: JsonSerializationContext?
        ): JsonElement {
            return JsonPrimitive(src?.format(dateFormatter))
        }
    }
    
    private inner class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): LocalDateTime? {
            return json?.asString?.let { LocalDateTime.parse(it, dateFormatter) }
        }
    }
    
    companion object {
        private val TICKETS_KEY = stringPreferencesKey("saved_tickets")
    }
    
    /**
     * Saves a list of tickets to DataStore as JSON.
     */
    suspend fun saveTickets(tickets: List<Ticket>) {
        android.util.Log.d("datastore test", "=== SAVING TO DATASTORE (JSON) ===")
        android.util.Log.d("datastore test", "Number of tickets to save: ${tickets.size}")
        
        try {
            val json = gson.toJson(tickets)
            android.util.Log.d("datastore test", "JSON: $json")
            
            context.dataStore.edit { preferences ->
                preferences[TICKETS_KEY] = json
            }
            android.util.Log.d("datastore test", "✅ SUCCESS: Tickets saved to DataStore")
        } catch (e: Exception) {
            android.util.Log.e("datastore test", "❌ ERROR: Failed to save tickets to DataStore", e)
        }
        
        android.util.Log.d("datastore test", "=== END SAVE OPERATION ===")
    }
    
    /**
     * Loads tickets from DataStore as a Flow.
     */
    fun loadTicketsFlow(): Flow<List<Ticket>> {
        return context.dataStore.data.map { preferences ->
            val json = preferences[TICKETS_KEY] ?: ""
            if (json.isEmpty()) {
                emptyList()
            } else {
                try {
                    val type = object : TypeToken<List<Ticket>>() {}.type
                    gson.fromJson<List<Ticket>>(json, type) ?: emptyList()
                } catch (e: Exception) {
                    android.util.Log.e("datastore test", "Error parsing JSON", e)
                    emptyList()
                }
            }
        }
    }
    
    /**
     * Loads tickets from DataStore synchronously (for initialization).
     */
    suspend fun loadTickets(): List<Ticket> {
        android.util.Log.d("datastore test", "=== LOADING FROM DATASTORE (JSON) ===")
        
        try {
            val preferences = context.dataStore.data.first()
            val json = preferences[TICKETS_KEY] ?: ""
            
            android.util.Log.d("datastore test", "Raw JSON from DataStore: '$json'")
            android.util.Log.d("datastore test", "JSON length: ${json.length}")
            
            if (json.isEmpty()) {
                android.util.Log.d("datastore test", "⚠️ No tickets found in storage (empty JSON)")
                android.util.Log.d("datastore test", "=== END LOAD OPERATION (EMPTY) ===")
                return emptyList()
            }
            
            val type = object : TypeToken<List<Ticket>>() {}.type
            val loadedTickets = gson.fromJson<List<Ticket>>(json, type) ?: emptyList()
            
            android.util.Log.d("datastore test", "✅ SUCCESS: Loaded ${loadedTickets.size} tickets from DataStore")
            
            loadedTickets.forEachIndexed { index, ticket ->
                android.util.Log.d("datastore test", "Ticket $index: ${ticket.getTitle()} - ${ticket.eventDescription.take(50)}...")
            }
            
            android.util.Log.d("datastore test", "=== END LOAD OPERATION ===")
            return loadedTickets
            
        } catch (e: Exception) {
            android.util.Log.e("datastore test", "❌ ERROR: Failed to load tickets from DataStore", e)
            android.util.Log.d("datastore test", "=== END LOAD OPERATION (ERROR) ===")
            return emptyList()
        }
    }
    
    /**
     * Clears all saved tickets.
     */
    suspend fun clearTickets() {
        context.dataStore.edit { preferences ->
            preferences.remove(TICKETS_KEY)
        }
    }
}
