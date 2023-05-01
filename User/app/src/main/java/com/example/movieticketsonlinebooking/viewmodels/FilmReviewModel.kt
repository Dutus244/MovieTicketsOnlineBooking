package com.example.movieticketsonlinebooking.viewmodels

class FilmReviewModel {
    var totalVoters = 0
    var totalRating = 0.0
    var star1 = 0
    var star2 = 0
    var star3 = 0
    var star4 = 0
    var star5 = 0

    constructor(
        totalVoters: Int,
        totalRating: Double,
        star1: Int,
        star2: Int,
        star3: Int,
        star4: Int,
        star5: Int,
    ) {
        this.totalVoters = totalVoters
        this.totalRating = totalRating
        this.star1 = star1
        this.star2 = star2
        this.star3 = star3
        this.star4 = star4
        this.star5 = star5
    }

    constructor() {}
}