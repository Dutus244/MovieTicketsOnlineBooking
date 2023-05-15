package com.example.admin.reviews

data class MovieReviewModel (
    var totalVoters: Int = 0,
    var totalRating: Double = 0.0,
    var star1: Int = 0,
    var star2: Int = 0,
    var star3: Int = 0,
    var star4: Int  = 0,
    var star5: Int = 0,
) {}