package com.example.movieticketsonlinebooking.viewmodels

data class User(
    val username: String?,
    val email: String?,
    val isLoggedIn: Boolean = false
)

object UserManager {
    private var currentUser: User = User(null, null, false)

    fun login(username: String, email: String) {
        currentUser = User(username, email, true)
    }

    fun logout() {
        currentUser = User(null, null, false)
    }

    fun getCurrentUser(): User {
        return currentUser
    }

    fun isLoggedIn(): Boolean {
        return currentUser.isLoggedIn
    }
}
