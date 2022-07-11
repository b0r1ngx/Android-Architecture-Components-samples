package github.apibuilder

import github.api.GithubAuthService
import github.api.GithubService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class ApiBuilder @Inject constructor(
    private val authenticationInterceptor: AuthenticationInterceptor
) {
    private val GITHUB = "https://github.com/"
    private val GITHUB_API = "https://api.github.com/"

    fun provideGithubService(
        logLevel: Level
    ): GithubService {
        val client = OkHttpClient.Builder()
            .addNetworkInterceptor(HttpLoggingInterceptor()
                .apply { level = logLevel })
            .addInterceptor(authenticationInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(GITHUB_API)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .client(client)
            .build()
            .create(GithubService::class.java)
    }

    fun provideGithubAuthService(): GithubAuthService =
        Retrofit.Builder()
            .baseUrl(GITHUB)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GithubAuthService::class.java)
}