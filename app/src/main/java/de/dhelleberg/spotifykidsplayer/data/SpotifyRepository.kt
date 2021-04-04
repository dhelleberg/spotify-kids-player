package de.dhelleberg.spotifykidsplayer.data

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.adamratzman.spotify.SpotifyAPI
import com.adamratzman.spotify.models.Artist
import com.adamratzman.spotify.models.PagingObject
import com.adamratzman.spotify.models.SimpleAlbum
import com.adamratzman.spotify.spotifyApi
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import de.dhelleberg.spotifykidsplayer.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.Semaphore


interface SpotifyRepository {
    fun getArtists(): List<Artist>
    fun getAlbums(artistURI: String): PagingObject<SimpleAlbum>
    fun getSpotifyAppRemote(): SpotifyAppRemote
}

class SpotifyRepositoryImpl(private val context : Context) : SpotifyRepository {


    private val TAG by lazy { SpotifyRepositoryImpl::class.java.simpleName }

    companion object {
        const val REDIRECT_URI: String = "kidsplayer://callback"
        const val CLIENT_ID: String = BuildConfig.SPOTIFY_CLIENT_ID
        const val CLIENT_SECRET: String = BuildConfig.SPOTIFY_CLIENT_SECRET
        const val REQUEST_CODE  = 7777
    }

    private var lock: Semaphore = Semaphore(1)

    init {
        lock.acquire() //lock until login is done
    }

    private lateinit var spotifAccessToken: String
    private lateinit var spotifyAppRemote: SpotifyAppRemote
    private var spotifLoginOK: Boolean = false
    private lateinit var api: SpotifyAPI

    var artistList: MutableList<Artist> = ArrayList()

    private val artistURIs = arrayOf(
                "spotify:artist:7zNPHpOB4uBduLjmEZzzpy", //Was ist was
                "spotify:artist:0vLsqW05dyLvjuKKftAEGA", //Die ??? kids
                "spotify:artist:5kfO0hrJ2B9B27G1fUbzVJ", //Robin Hood
                //"spotify:artist:1zo1vxkpoT8JMJYdHv2zsa", //die reiter von Berk
                //"spotify:artist:4flLF0QDCVMtQCc7JJ94gt", //Die wächter von Berk
                "spotify:artist:1z8ytficgBWsoYigwE2QVM",  // Auf zu neuen Ufern
                //"spotify:artist:3Mr28PUoMrOvS8KshvBXa8") // Lasse Maja
                "spotify:artist:1hD52edfn6aNsK3fb5c2OT", // Fünf Freunde
                "spotify:artist:0jRC2uXx4hEpqNrBDJdY7l") //conni

    override fun getArtists(): List<Artist> {
        checkAndBlockUntilLogin()
        if(artistList.isEmpty()) {
            try {
                artistURIs.forEach {
                    artistList.addAll(api.artists.getArtists(it).complete().filterNotNull())
                }
                //artists = api.search.searchArtist("Dragons" ).complete().filterNotNull() //genre:\"hoerspiel\""
            } catch (e: Exception) {
                Log.e(TAG, "Error query for artist: ${e.printStackTrace()}", e)
            }
        }
        Log.d(TAG, "returning artistList size: "+artistList.size)
        return artistList
    }

    override fun getAlbums(artistURI: String): PagingObject<SimpleAlbum> {
        checkAndBlockUntilLogin()
        return api.artists.getArtistAlbums(artistURI,50,0).complete()
    }


    @Synchronized
    private fun checkAndBlockUntilLogin() {
        Log.d(TAG, "check login entry " + lock.availablePermits() + " loginStatus: "+spotifLoginOK)
        if(!spotifLoginOK) {
            lock.acquire()
            lock.release()
        }
        Log.d(TAG, "check login exit " + lock.availablePermits() + " loginStatus: "+spotifLoginOK)
    }


    private fun loginToSpotify(activity: Activity) {
        Log.d(TAG, "login to spotify")
        val builder = AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)

        builder.setScopes(arrayOf("streaming", "app-remote-control"))
        val request = builder.build()

        AuthenticationClient.openLoginActivity(activity, REQUEST_CODE, request)
    }

    private fun connectToSpotify() {
        if(::spotifyAppRemote.isInitialized)
            SpotifyAppRemote.disconnect(spotifyAppRemote)

        val connectionListener = object: Connector.ConnectionListener {
            override fun onFailure(p0: Throwable?) {
                Log.e(TAG, "connection-error",p0)
            }

            override fun onConnected(p0: SpotifyAppRemote?) {
                spotifyAppRemote = p0!!
                Log.d(TAG, "connected to Spotify!")
                GlobalScope.launch(Dispatchers.IO) {
                    loginAPI()
                }
            }

        }
        SpotifyAppRemote.connect(
            context,
            ConnectionParams.Builder(CLIENT_ID).setRedirectUri(REDIRECT_URI).showAuthView(true).build(),
            connectionListener
        )
    }

    private fun loginAPI() {
        api = spotifyApi {
            credentials {
                clientId = CLIENT_ID
                clientSecret = CLIENT_SECRET
            }
        }.buildCredentialed()
        spotifLoginOK = true
        Log.d(TAG, "login done, success, release lock")
        lock.release()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_CODE) {
            val response = AuthenticationClient.getResponse(resultCode, data)
            Log.d(TAG, "response: ${response.type}")
            when (response.type) {
                // Response was successful and contains auth token
                AuthenticationResponse.Type.TOKEN -> {
                    Log.d(TAG,"spotify login token ok!")
                    spotifAccessToken = response.accessToken
                    spotifLoginOK = true
                    connectToSpotify()
                }

                // Auth flow returned an error
                AuthenticationResponse.Type.ERROR -> {
                    spotifLoginOK = false
                    Log.e(TAG, "login to spotify failed")
                }
            }
        }
    }

    fun onStop(activity: Activity) {
        Log.d(TAG, "stop -> acquire LOCK!")
        lock.acquire()
        spotifLoginOK = false
        if(::spotifyAppRemote.isInitialized)
            SpotifyAppRemote.disconnect(spotifyAppRemote)
        Log.d(TAG, "stop -> DONE!")
    }

    fun onStart(activity: Activity) {
        Log.d(TAG, "onSTART")
        //if(spotifLoginOK)
            loginToSpotify(activity)
        //else
        //    connectToSpotify()

    }

    override fun getSpotifyAppRemote(): SpotifyAppRemote {
        Log.d(TAG, "getSpotifyAppRemote")
        checkAndBlockUntilLogin()
        return spotifyAppRemote
    }

}