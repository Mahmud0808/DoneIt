package com.drdisagree.doneit

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform