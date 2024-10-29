package com.mayank.ivr_voice_app_v3

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform