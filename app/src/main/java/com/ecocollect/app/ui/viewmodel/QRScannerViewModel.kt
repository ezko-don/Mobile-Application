package com.ecocollect.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QRScannerViewModel @Inject constructor() : ViewModel() {
    
    private val _scanResult = MutableStateFlow<String?>(null)
    val scanResult: StateFlow<String?> = _scanResult.asStateFlow()
    
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()
    
    private val _scanHistory = MutableStateFlow<List<String>>(emptyList())
    val scanHistory: StateFlow<List<String>> = _scanHistory.asStateFlow()
    
    fun onQRCodeScanned(qrData: String) {
        _scanResult.value = qrData
        _isScanning.value = false
        
        // Add to scan history
        val currentHistory = _scanHistory.value.toMutableList()
        currentHistory.add(0, qrData) // Add to beginning
        if (currentHistory.size > 10) { // Keep only last 10 scans
            currentHistory.removeAt(currentHistory.size - 1)
        }
        _scanHistory.value = currentHistory
        
        // Process the QR code data for pickup confirmation
        processQRCodeData(qrData)
    }
    
    fun startScanning() {
        _isScanning.value = true
        _scanResult.value = null
    }
    
    fun clearScanResult() {
        _scanResult.value = null
    }
    
    fun resetScanner() {
        _isScanning.value = true
        _scanResult.value = null
    }
    
    private fun processQRCodeData(qrData: String) {
        viewModelScope.launch {
            try {
                // Parse QR code data - this could be a JSON string, URL, or simple text
                when {
                    qrData.startsWith("http") -> {
                        // Handle URL-based QR codes
                        handleURLQRCode(qrData)
                    }
                    qrData.startsWith("{") -> {
                        // Handle JSON-based QR codes
                        handleJSONQRCode(qrData)
                    }
                    else -> {
                        // Handle simple text-based QR codes
                        handleTextQRCode(qrData)
                    }
                }
            } catch (e: Exception) {
                // Handle parsing errors
                _scanResult.value = "Error processing QR code: ${e.message}"
            }
        }
    }
    
    private fun handleURLQRCode(url: String) {
        // Extract pickup information from URL
        // Example: https://ecocollect.app/pickup?id=12345&location=warehouse1
        val pickupId = extractPickupIdFromURL(url)
        if (pickupId != null) {
            confirmPickup(pickupId)
        }
    }
    
    private fun handleJSONQRCode(jsonData: String) {
        // Parse JSON data for pickup confirmation
        // Example: {"pickupId": "12345", "location": "warehouse1", "timestamp": "2024-01-01T10:00:00Z"}
        try {
            // You would use a JSON parser here
            // For now, we'll do simple string parsing
            if (jsonData.contains("pickupId")) {
                val pickupId = extractPickupIdFromJSON(jsonData)
                if (pickupId != null) {
                    confirmPickup(pickupId)
                }
            }
        } catch (e: Exception) {
            // Handle JSON parsing errors
        }
    }
    
    private fun handleTextQRCode(text: String) {
        // Handle simple text-based QR codes
        // Example: "PICKUP_12345_WAREHOUSE1"
        if (text.startsWith("PICKUP_")) {
            val parts = text.split("_")
            if (parts.size >= 2) {
                val pickupId = parts[1]
                confirmPickup(pickupId)
            }
        }
    }
    
    private fun extractPickupIdFromURL(url: String): String? {
        // Simple URL parameter extraction
        return try {
            val queryIndex = url.indexOf("?")
            if (queryIndex != -1) {
                val query = url.substring(queryIndex + 1)
                val params = query.split("&")
                for (param in params) {
                    if (param.startsWith("id=")) {
                        return param.substring(3)
                    }
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }
    
    private fun extractPickupIdFromJSON(jsonData: String): String? {
        // Simple JSON parsing for pickupId
        return try {
            val pickupIdPattern = "\"pickupId\"\\s*:\\s*\"([^\"]+)\"".toRegex()
            val match = pickupIdPattern.find(jsonData)
            match?.groupValues?.get(1)
        } catch (e: Exception) {
            null
        }
    }
    
    private fun confirmPickup(pickupId: String) {
        viewModelScope.launch {
            try {
                // Here you would integrate with your backend service
                // to confirm the pickup
                // For now, we'll just update the scan result
                _scanResult.value = "Pickup confirmed: $pickupId"
                
                // You could also:
                // 1. Send confirmation to backend
                // 2. Update local database
                // 3. Show success notification
                // 4. Navigate to pickup details screen
                
            } catch (e: Exception) {
                _scanResult.value = "Failed to confirm pickup: ${e.message}"
            }
        }
    }
    
    fun getScanStatistics(): Map<String, Any> {
        val history = _scanHistory.value
        return mapOf(
            "totalScans" to history.size,
            "lastScan" to history.firstOrNull() ?: "No scans yet",
            "scanFrequency" to if (history.size > 1) "Active user" else "New user"
        )
    }
}
