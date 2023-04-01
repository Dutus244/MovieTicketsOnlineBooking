package com.example.admin.cinemas

class Cinema() {
    var img_url: String = ""
    var name: String = ""
    var address: String = ""
    var auditoriums_no: Int = 0
    var status: String = ""
    var is_deleted: Boolean = false

    constructor(
        img_url: String,
        name: String,
        address: String,
        auditoriums_no: Int,
        status: String,
        is_deleted: Boolean
    ) : this() {
        this.img_url = img_url
        this.name = name
        this.address = address
        this.auditoriums_no = auditoriums_no
        this.status = status
        this.is_deleted = is_deleted
    }
}