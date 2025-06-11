package com.example.mobileanwendungvorlesung.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val phone: String,
    val email: String,
    val birthday: String,
    val street: String,
    val houseNr: String,
    val postcode: String,
    val city: String,
    val imageRes: String
) : Parcelable
