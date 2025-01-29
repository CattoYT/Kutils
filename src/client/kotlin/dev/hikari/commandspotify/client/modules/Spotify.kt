package dev.hikari.commandspotify.client.modules

import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.models.Token
import com.adamratzman.spotify.spotifyClientPkceApi
import dev.hikari.commandspotify.client.CommandSpotifyClient
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import kotlinx.coroutines.runBlocking
import java.net.BindException

class Spotify {

    var spotifyApi: SpotifyClientApi? = null
    var clientID: String? = "a9e2b8d829d648f7ac6fac3dce2567cd"
    var codeVerifier: String? = "thisisaveryrandomalphanumericcodeverifierandisgreaterthan43characters"
    init {
        restoreSpotifyApi()
    }
    // Spotify credentials


    // Redirect URI and token storage

    fun initialize(clientID: String, codeVerifier: String) {
        this.clientID = clientID
        this.codeVerifier = codeVerifier
        CommandSpotifyClient.logger.info("Initializing Spotify module")
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
            CommandSpotifyClient.logger.error("Failed to create Spotify API: ${e.message}")
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
                CommandSpotifyClient.logger.info(queryParams)
                if (queryParams != null && queryParams.contains("code")) {
                    val authorizationCode = queryParams.split("=")[1]
                    val response = "Code received! You may now close this tab."
                    CommandSpotifyClient.logger.info(authorizationCode)
                    exchange.sendResponseHeaders(200, response.length.toLong())
                    exchange.responseBody.write(response.toByteArray())
                    exchange.responseBody.close()


                    // Now that we have the code, initialize the Spotify API
                    runBlocking{
                        spotifyApi = createSpotifyApi(clientID!!, authorizationCode)
                        CommandSpotifyClient.ConfigManager.writeToken(spotifyApi?.token?.refreshToken.toString())

                    }
                    if (spotifyApi != null) {
                        CommandSpotifyClient.logger.info("Successfully created Spotify API")
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

        catch (e: BindException) {
            CommandSpotifyClient.logger.error("This port is already in use!")
            return
        }
    }
    private fun restoreSpotifyApi() {
        CommandSpotifyClient.logger.info("Restoring Spotify API")
        var code = CommandSpotifyClient.ConfigManager.readEncryptedToken()
            println(code)
            if (code != null) {
                CommandSpotifyClient.logger.info("Found existing authorization code")
                runBlocking {
                    try {
                        val token = Token(
                            accessToken = "skibidi", // optional, use an empty string if unknown
                            tokenType = "Bearer",
                            refreshToken = code,
                            scopeString = "user-read-playback-state user-modify-playback-state app-remote-control",
                            expiresIn = 1, // time-to-live in seconds, can be arbitrary initially
                        )



                        spotifyApi = spotifyClientPkceApi(
                            clientId = clientID,
                            redirectUri  = "http://localhost:8080",
                            token = token
                        ) {
                            automaticRefresh = true
                        }.build()


                        if (spotifyApi != null) {
                            if (spotifyApi?.isTokenValid()?.isValid == false) {
                                CommandSpotifyClient.logger.info("Successfully restored Spotify API")
                                CommandSpotifyClient.ConfigManager.writeToken(spotifyApi?.token?.refreshToken.toString())
                                return@runBlocking
                            }
                        }
                        return@runBlocking
                    } catch (e: Exception) {
                        CommandSpotifyClient.logger.error("Failed to create Spotify API: ${e.message}. Rehosting for new code.")

                    }
                }
            } else {
                CommandSpotifyClient.logger.info("No existing authorization code found")
            }

    }
    fun WhatIsPlaying() {
        CommandSpotifyClient.logger.info("Pause Spotify command executed")
        runBlocking {

            var currentlyPlaying = CommandSpotifyClient.Spotify.spotifyApi?.player?.getCurrentlyPlaying()?.item?.asTrack
            if (currentlyPlaying == null) {
                CommandSpotifyClient.Log("No song is currently playing")
            } else {
                CommandSpotifyClient.Log("Now Playing: ${currentlyPlaying.name} - ${currentlyPlaying.artists[0].name}")
            }

        }
    }
}