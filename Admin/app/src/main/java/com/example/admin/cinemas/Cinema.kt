package com.example.admin.cinemas

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Cinema(
    var img_url: String = "",
    var name: String = "",
    var address: String = "",
    var auditoriums_no: Int = 0,
    var status: String = "Open",
    @field:JvmField
    var is_deleted: Boolean = false,
    @DocumentId
    val id: String = "",
) : java.io.Serializable {

}