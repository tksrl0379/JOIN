package com.example.join.DTO

data class Activity_ContentDTO (
    var title: String? = null,
    var explain: String? = null,
    var time: ArrayList<Int>? = null,
    var date: String? = null,
    var timestamp: Long? = null,
    var distance: String? = null,
    var uid: String? = null,
    var userEmail: String? = null,
    var imageUrI: String? = null,
    var favoritesCount: Int = 0,
    var favorites: Map<String, Boolean>  = HashMap()) {
    data class Comment(
        var uid: String? = null,
        var userId: String? = null,
        var comment: String? = null,
        var timestamp: Long? = null)
}