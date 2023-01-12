package com.example.bitcoinwallet.ui.mempool

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*

interface MempoolApiService {

    companion object {
        fun create(baseUrl: String) : MempoolApiService {
            return MempoolApiServiceImpl(
                client = HttpClient(Android) {
                    install(Logging) {
                        level = LogLevel.ALL
                    }
                    install(JsonFeature) {
                        serializer = KotlinxSerializer(json)
                    }
                    install(HttpTimeout) {
                        requestTimeoutMillis = 5000L
                        connectTimeoutMillis = 5000L
                        socketTimeoutMillis = 5000L
                    }
                    defaultRequest {
                        contentType(ContentType.Application.Json)
                        accept(ContentType.Application.Json)
                    }
                },
                baseUrl = baseUrl
            )
        }

        private val json = kotlinx.serialization.json.Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = false
        }
    }

    suspend fun getDifficultyAdjustment(): DifficultyAdjustment?
}