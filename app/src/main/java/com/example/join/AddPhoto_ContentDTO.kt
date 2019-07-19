package com.example.join

data class AddPhoto_ContentDTO (
    var explain: String? = null,
    var imageUrI: String? = null,
    var uid: String? = null,
    var userId: String? = null,
    var timestamp: Long? = null,
    var favoriteCount: Int = 0
)