package com.ecocollect.app.data.local

import androidx.room.TypeConverter
import com.ecocollect.app.data.model.Address
import com.ecocollect.app.data.model.EWasteItem
import com.ecocollect.app.data.model.PickupStatus
import com.google.firebase.Timestamp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    
    @TypeConverter
    fun fromEWasteItemList(items: List<EWasteItem>): String {
        return Gson().toJson(items)
    }
    
    @TypeConverter
    fun toEWasteItemList(itemsString: String): List<EWasteItem> {
        val listType = object : TypeToken<List<EWasteItem>>() {}.type
        return Gson().fromJson(itemsString, listType)
    }
    
    @TypeConverter
    fun fromStringList(strings: List<String>): String {
        return Gson().toJson(strings)
    }
    
    @TypeConverter
    fun toStringList(stringsString: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(stringsString, listType)
    }
    
    @TypeConverter
    fun fromAddress(address: Address?): String? {
        return address?.let { Gson().toJson(it) }
    }
    
    @TypeConverter
    fun toAddress(addressString: String?): Address? {
        return addressString?.let { Gson().fromJson(it, Address::class.java) }
    }
    
    @TypeConverter
    fun fromPickupStatus(status: PickupStatus): String {
        return status.name
    }
    
    @TypeConverter
    fun toPickupStatus(statusString: String): PickupStatus {
        return PickupStatus.valueOf(statusString)
    }
    
    @TypeConverter
    fun fromTimestamp(timestamp: Timestamp?): Long? {
        return timestamp?.seconds
    }
    
    @TypeConverter
    fun toTimestamp(seconds: Long?): Timestamp? {
        return seconds?.let { Timestamp(it, 0) }
    }
}
