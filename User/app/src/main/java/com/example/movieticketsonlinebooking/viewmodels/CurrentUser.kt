package com.example.movieticketsonlinebooking.viewmodels

data class CurrentUser(
    val userID: String?,
    val username: String?,
    val email: String?,
    val typeAccount: Int?,
    val isLoggedIn: Boolean = false,
)

object UserManager {
    private var currentUser: CurrentUser = CurrentUser(null,null, null, null, false)

    fun login(userID: String, username: String, email: String, typeAccount: Int?) {
        currentUser = CurrentUser(userID, username, email, typeAccount, true)
    }

    fun logout() {
        currentUser = CurrentUser(null,null, null, null, false)
    }

    fun getCurrentUser(): CurrentUser {
        return currentUser
    }

    fun isLoggedIn(): Boolean {
        return currentUser.isLoggedIn
    }
}
