package com.example.admin.users

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class User(
    val username: String = "",
    val name: String = "",
    val hashpassword: String = "",
    val sex: String = "",
    val tel: String = "",
    val dob: Date = Date(),
    val email: String = "",
    @field:JvmField
    val is_banned: Boolean = false,
    @field:JvmField
    val is_deleted: Boolean = false,
    @DocumentId
    val id: String = "",
) : java.io.Serializable {

}