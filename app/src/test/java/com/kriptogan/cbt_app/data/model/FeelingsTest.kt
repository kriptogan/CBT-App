package com.kriptogan.cbt_app.data.model

import org.junit.Test
import org.junit.Assert.*

class FeelingsTest {
    
    @Test
    fun `create valid feelings with description and intensity`() {
        // Given
        val description = "Anxious"
        val intensity = 75
        
        // When
        val feelings = Feelings(description, intensity)
        
        // Then
        assertEquals(description, feelings.description)
        assertEquals(intensity, feelings.intensity)
    }
    
    @Test
    fun `getFormattedString returns correct format`() {
        // Given
        val feelings = Feelings("Happy", 80)
        
        // When
        val formatted = feelings.getFormattedString()
        
        // Then
        assertEquals("Happy (80%)", formatted)
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun `throws exception when intensity is negative`() {
        // When
        Feelings("Sad", -1)
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun `throws exception when intensity is over 100`() {
        // When
        Feelings("Excited", 101)
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun `throws exception when description is blank`() {
        // When
        Feelings("", 50)
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun `throws exception when description is whitespace only`() {
        // When
        Feelings("   ", 50)
    }
    
    @Test
    fun `allows intensity at boundary values`() {
        // Test minimum intensity
        val minFeelings = Feelings("Calm", 0)
        assertEquals(0, minFeelings.intensity)
        
        // Test maximum intensity
        val maxFeelings = Feelings("Intense", 100)
        assertEquals(100, maxFeelings.intensity)
    }
}
