package com.kriptogan.cbt_app.data.model

/**
 * Data class representing a feeling with its description and intensity level.
 * 
 * @param description The description of the feeling
 * @param intensity The intensity level as a percentage (0-100)
 */
data class Feelings(
    val description: String,
    val intensity: Int
) {
    init {
        require(intensity in 0..100) { "Intensity must be between 0 and 100" }
    }
    
    /**
     * Validates that the feeling has a non-blank description.
     * This should be called before saving the ticket.
     */
    fun isValid(): Boolean {
        return description.isNotBlank()
    }
    
    /**
     * Returns a formatted string representation of the feeling with intensity.
     */
    fun getFormattedString(): String {
        return "$description (${intensity}%)"
    }
}
