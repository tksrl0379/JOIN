package com.example.join.DTO

import com.google.android.gms.maps.model.LatLng

data class Activity_ContentDTO (
    var title: String? = null,
    var explain: String? = null,
    var time: String? = null,
    var date: String? = null,
    var timeStamp: Long? = null,
    var distance: String? = null,
    //var distanceLatlng : LatLng? = null,    //목적지 latlng저장.
    var max_altitude: String? = null,
    var uid: String? = null,
    var userEmail: String? = null,
    var imageUrI: String? = null,
    var favoritesCount: Int = 0,
    var averSpeed: String? = null,
    var pedometer: String? = null,
    var favorites: Map<String, Boolean>  = HashMap()) {
    data class Comment(
        var uid: String? = null,
        var userId: String? = null,
        var comment: String? = null,
        var timestamp: Long? = null)
}