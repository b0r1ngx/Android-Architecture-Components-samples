package github.login

import android.content.Intent
import android.net.Uri
import github.api.AccessTokenParameter
import github.api.GithubAuthService
import github.env.Env
import github.repository.AccessTokenRepository
import github.model.AccessToken
import timber.log.Timber
import javax.inject.Inject

class LoginHelper @Inject constructor(
    private val githubAuthService: GithubAuthService,
    private val accessTokenRepository: AccessTokenRepository,
    private val env: Env
) {
    fun generateAuthorizationUrl(): Uri = Uri.Builder().apply {
        scheme("https")
        authority("github.com")
        appendPath("login")
        appendPath("oauth")
        appendPath("authorize")
        appendQueryParameter("client_id", env.GITHUB_CLIENT_ID)
    }.build()

    suspend fun handleAuthRedirect(intent: Intent): Boolean {
        val uri = intent.data ?: return false
        if (!uri.toString().startsWith("dgbs://login"))
            return false

        val tempCode =
            uri.getQueryParameter("code") ?: return false

        Timber.i("code: $tempCode")

        val param = AccessTokenParameter(
            clientId = env.GITHUB_CLIENT_ID,
            clientSecret = env.GITHUB_CLIENT_SECRET,
            code = tempCode
        )

        return runCatching {
            val resp =
                githubAuthService.createAccessToken(param)
            accessTokenRepository.save(AccessToken(resp.accessToken))
        }.onFailure {
            Timber.e(it, "createAccessToken failed!")
        }.isSuccess
    }
}
