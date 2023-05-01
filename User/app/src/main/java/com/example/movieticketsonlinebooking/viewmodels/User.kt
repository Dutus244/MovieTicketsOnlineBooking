package com.example.movieticketsonlinebooking.viewmodels

data class User(
    val userID: String?,
    val username: String?,
    val email: String?,
    val isLoggedIn: Boolean = false
)

object UserManager {
    private var currentUser: User = User(null,null, null, false)

    fun login(userID: String, username: String, email: String) {
        currentUser = User(userID, username, email, true)
    }

    fun logout() {
        currentUser = User(null,null, null, false)
    }

    fun getCurrentUser(): User {
        return currentUser
    }

    fun isLoggedIn(): Boolean {
        return currentUser.isLoggedIn
    }
}
