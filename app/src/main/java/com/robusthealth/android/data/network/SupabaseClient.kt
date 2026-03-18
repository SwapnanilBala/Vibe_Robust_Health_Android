package com.robusthealth.android.data.network

import retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.robusthealth.android.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object SupabaseClient {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    private val authInterceptor = Interceptor { chain ->
        val request = chain.request()
        val builder = request.newBuilder()
            .addHeader("apikey", BuildConfig.SUPABASE_ANON_KEY)
            .addHeader("Authorization", "Bearer ${BuildConfig.SUPABASE_ANON_KEY}")
            .addHeader("Content-Type", "application/json")
        chain.proceed(builder.build())
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(authInterceptor)
        .addInterceptor(logging)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(normalizeBaseUrl(BuildConfig.SUPABASE_URL))
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    val api: SupabaseApi = retrofit.create(SupabaseApi::class.java)

    val isConfigured: Boolean
        get() {
            val urlSet = !BuildConfig.SUPABASE_URL.contains("YOUR-PROJECT", ignoreCase = true)
            val keySet = BuildConfig.SUPABASE_ANON_KEY.isNotBlank() &&
                !BuildConfig.SUPABASE_ANON_KEY.contains("YOUR_SUPABASE_ANON_KEY", ignoreCase = true)
            return urlSet && keySet
        }

    private fun normalizeBaseUrl(raw: String): String {
        val trimmed = raw.trim().trimEnd('/')
        return "$trimmed/"
    }
}
