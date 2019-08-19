package com.example.join.DTO

data class UserInfoDTO (
    var userEmail: String? = null,
    var userId: String? = null,
    var photo: String? = null,
    var signUpDate: String? = null,
    var percentage: Int = 0, // 출석률(개근률)
    var continueDay: Int = 0 // 연속 일 수
)