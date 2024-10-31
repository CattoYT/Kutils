package dev.hikari.kutils.client.modules

import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.spotifyClientPkceApi
import dev.hikari.kutils.client.KutilsClient
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import kotlinx.coroutines.runBlocking

class Spotify {

    var spotifyApi: SpotifyClientApi? = null

    // Spotify credentials
    var clientID: String? = null
    var codeVerifier: String? = null

    // Redirect URI and token storage

    fun initialize(clientID: String, codeVerifier: String) {
        this.clientID = clientID
        this.codeVerifier = codeVerifier
        KutilsClient.logger.info("Initializing Spotify module")
        // Open the localhost server to capture the authorization code
        Thread ({
            openLocalServer()
        }).start()
    }

    private suspend fun createSpotifyApi(clientID: String, code: String): SpotifyClientApi? {
        return try {
            spotifyClientPkceApi(
                clientID,
                "http://localhost:8080",
                code,
                this.codeVerifier.toString()
            ) {
                automaticRefresh = true
            }.build()
        } catch (e: Exception) {
            KutilsClient.logger.error("Failed to create Spotify API: ${e.message}")
            null
        }
    }

    private fun openLocalServer() {
        // Open a local web server to handle the redirect URI
        println("pretend this worked")

        try {
            val server = HttpServer.create(InetSocketAddress(8080), 0)
            server.createContext("/") { exchange ->
                val queryParams = exchange.requestURI.query
                KutilsClient.logger.info(queryParams)
                if (queryParams != null && queryParams.contains("code")) {
                    val authorizationCode = queryParams.split("=")[1]
                    val response = "Code received! You may now close this tab."
                    KutilsClient.logger.info(authorizationCode)
                    exchange.sendResponseHeaders(200, response.length.toLong())
                    exchange.responseBody.write(response.toByteArray())
                    exchange.responseBody.close()


                    // Now that we have the code, initialize the Spotify API
                    runBlocking{
                        spotifyApi = createSpotifyApi(clientID!!, authorizationCode)
                        server.stop(0)
                    }
                    // Handle the  code here (e.g., exchange it for a token)
                } else {
                    exchange.sendResponseHeaders(400, 0)
                    exchange.responseBody.close()
                }
            }
            server.start()
        }

        catch (e: java.net.BindException) {
            KutilsClient.logger.error("This port is already in use!")
            return
        }



    }
}