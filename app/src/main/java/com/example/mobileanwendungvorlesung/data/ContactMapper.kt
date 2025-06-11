package com.example.mobileanwendungvorlesung.data

import com.example.mobileanwendungvorlesung.network.RandomUserResult
import com.example.mobileanwendungvorlesung.data.Contact

fun RandomUserResult.toContact(): Contact {
    return Contact(
        name = "${name.first} ${name.last}",
        phone = phone,
        email = email,
        birthday = "", // RandomUserResult enthält das nur, wenn du es ergänzt
        street = location.street.name,
        houseNr = location.street.number.toString(),
        postcode = location.postcode ?: "", // postcode kann null sein
        city = location.city,
        imageRes = picture.large
    )
}