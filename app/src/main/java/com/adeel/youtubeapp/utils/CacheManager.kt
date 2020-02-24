package com.adeel.youtubeapp.utils

object CacheManager {

    private val mCache: MutableMap<String, Any> = mutableMapOf()

    fun put(key: String, value: Any) {
        when {
            key.isNotEmpty() -> mCache[key] = value
            else -> throw IllegalArgumentException("Key cannot be empty")
        }
    }

    fun get(key: String): Any? {
        return when {
            mCache.containsKey(key) -> mCache[key]
            else -> null
        }
    }

}