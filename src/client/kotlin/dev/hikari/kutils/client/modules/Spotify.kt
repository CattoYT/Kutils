package dev.hikari.kutils.client.modules

import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.spotifyClientPkceApi
import dev.hikari.kutils.client.KutilsClient
import io.ktor.http.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Spotify {

    var spotifyApi: SpotifyClientApi? = null

    // Spotify credentials
    var clientID: String? = null
    var clientSecret: String? = null
    var codeVerifier: String? = null
    private var server: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine. Configuration>? = null

    // Redirect URI and token storage
    var authorizationCode: String? = null

    fun initialize(clientID: String, clientSecret: String, codeVerifier: String) {
        this.clientID = clientID
        this.clientSecret = clientSecret
        this.codeVerifier = codeVerifier
        KutilsClient.logger.info("Initializing Spotify module")
        // Open the localhost server to capture the authorization code
        Thread ({
            openLocalServer()
        }).start()
    }

    private suspend fun createSpotifyApi(clientID: String, clientSecret: String, code: String): SpotifyClientApi? {
        return try {
            spotifyClientPkceApi(
                clientID,
                clientSecret,
                code,
                this.codeVerifier.toString()
            ) {
                automaticRefresh = false
            }.build()
        } catch (e: Exception) {
            KutilsClient.logger.error("Failed to create Spotify API: ${e.message}")
            null
        }
    }

    private fun openLocalServer() {
        // Open a local web server to handle the redirect URI
        server = embeddedServer(Netty, port = 48266) {
            routing {
                get("/callback") {
                    // Retrieve the authorization code from the query parameters
                    authorizationCode = call.request.queryParameters["code"]

                    if (authorizationCode != null) {
                        call.respondText("Authorization code received. You can now close this window.")
                        GlobalScope.launch {
                            // Now that we have the code, initialize the Spotify API
                            spotifyApi = createSpotifyApi(clientID!!, clientSecret!!, authorizationCode!!)
                            server?.stop(1000, 1000)
                        }
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Authorization code not found.")
                    }
                }
            }
        }.start(wait = false)
    }
}