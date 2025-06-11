package com.example.mobileanwendungvorlesung.network

import kotlinx.serialization.Serializable

@Serializable
data class RandomUserResponse(
    val results: List<RandomUserResult>
)

@Serializable
data class RandomUserResult(
    val gender: String,
    val name: RandomUserName,
    val location: RandomUserLocation,
    val email: String,
    val phone: String,
    val cell: String,
    val picture: RandomUserPicture
)

@Serializable
data class RandomUserName(
    val title: String,
    val first: String,
    val last: String
)

@Serializable
data class RandomUserLocation(
    val street: RandomUserStreet,
    val city: String,
    val state: String,
    val country: String,
    val postcode: String? = null // postcode kann manchmal null sein
)

@Serializable
data class RandomUserStreet(
    val number: Int,
    val name: String
)

@Serializable
data class RandomUserPicture(
    val large: String,
    val medium: String,
    val thumbnail: String
)