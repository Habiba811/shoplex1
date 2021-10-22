package com.alpha.shoplex.room.data

import android.net.Uri
import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.alpha.shoplex.model.pojo.Property
import com.alpha.shoplex.model.pojo.*
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList

class Converter {
    //Special Discount
    @TypeConverter
    fun sdToString(specialDiscount: SpecialDiscount?): String? {
        if (specialDiscount != null)
            return Gson().toJson(specialDiscount)
        return null
    }

    @TypeConverter
    fun stringToSd(string: String?): SpecialDiscount? =
        Gson().fromJson(string, SpecialDiscount::class.java)

    //Product
    @TypeConverter
    fun productToString(product: Product): String = Gson().toJson(product)

    @TypeConverter
    fun stringToProduct(string: String): Product = Gson().fromJson(string, Product::class.java)

    //ArrayList Property
    @TypeConverter
    fun fromProperty(value: String?): ArrayList<Property?>? {
        val listType: Type = object : TypeToken<ArrayList<Property?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun toProperty(list: ArrayList<Property?>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }

    //Date
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    //ArrayList String
    @TypeConverter
    fun fromImage(value: String?): ArrayList<String?>? {
        val listType: Type = object : TypeToken<ArrayList<String?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun toImage(list: ArrayList<String?>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }

    //ArrayList Uri
    @TypeConverter
    fun fromUri(value: String?): ArrayList<Uri?>? {
        val listType: Type = object : TypeToken<ArrayList<Uri?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun toUri(list: ArrayList<Uri?>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun toLocation(locationString: String?): Location? {
        return try {
            Gson().fromJson(locationString, Location::class.java)
        } catch (e: Exception) {
            null
        }
    }

    @TypeConverter
    fun toLocationString(location: Location?): String? {
        return Gson().toJson(location)
    }

    //LatLng
    @TypeConverter
    fun stringToModel(json: String?): LatLng? {
        val gson = Gson()
        val type = object : com.google.common.reflect.TypeToken<LatLng?>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun modelToString(position: LatLng?): String? {
        val gson = Gson()
        val type = object : com.google.common.reflect.TypeToken<LatLng?>() {}.type
        return gson.toJson(position, type)
    }

    //ArrayList ProductCart
    @TypeConverter
    fun fromProductCart(value: String?): ArrayList<ProductCart?>? {
        val listType: Type = object : TypeToken<ArrayList<ProductCart?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun toProductCart(list: ArrayList<ProductCart?>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun premiumToString(premium: Premium?): String? {
        if (premium != null)
            return Gson().toJson(premium)
        return null
    }

    @TypeConverter
    fun stringToPremium(string: String?): Premium? = Gson().fromJson(string, Premium::class.java)
}