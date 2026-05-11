package com.kevinfreyap.core.data.source.local

interface DbProductRunner {
    suspend operator fun <T> invoke(block: suspend () -> T): T
}