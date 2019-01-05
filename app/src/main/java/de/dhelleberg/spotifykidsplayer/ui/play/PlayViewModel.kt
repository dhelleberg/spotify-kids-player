package de.dhelleberg.spotifykidsplayer.ui.play

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.spotify.protocol.client.Subscription
import com.spotify.protocol.types.ImageUri
import com.spotify.protocol.types.PlayerState
import de.dhelleberg.spotifykidsplayer.data.SpotifyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class PlayViewModel(private val spotifyRepository : SpotifyRepository) : ViewModel() {

    private val playerState: MutableLiveData<PlayerState> = MutableLiveData()
    private lateinit var subscription: Subscription<PlayerState>
    fun getPlayerState(): LiveData<PlayerState> {
        if(!::subscription.isInitialized || (::subscription.isInitialized && subscription.isCanceled)) {
            GlobalScope.launch(Dispatchers.Main) {
                subscription = async(Dispatchers.IO) {
                    spotifyRepository.getSpotifyAppRemote().playerApi.subscribeToPlayerState()
                }.await()
                subscription.setEventCallback() {callback(it)}
            }
        }
        return playerState
    }

     private  fun callback(it: PlayerState?) {
        playerState.postValue(it)
    }

    fun play(id: String) {
        spotifyRepository.getSpotifyAppRemote().playerApi.play(id)
    }

    fun loadBitmapForCurrentTrack(uri: ImageUri, callback:(bitmap: Bitmap) -> Unit) {
       spotifyRepository.getSpotifyAppRemote().imagesApi.getImage(uri).setResultCallback { callback(it) }
    }

    fun togglePlayPause() {
        spotifyRepository.getSpotifyAppRemote().playerApi.playerState.setResultCallback {
            if(it.isPaused)
                spotifyRepository.getSpotifyAppRemote().playerApi.resume()
            else
                spotifyRepository.getSpotifyAppRemote().playerApi.pause()
        }
    }

    fun nextTrack() {
        spotifyRepository.getSpotifyAppRemote().playerApi.skipNext()
    }

    fun unSubscribe() {
        subscription.cancel()
    }

    fun prevTrack() {
        spotifyRepository.getSpotifyAppRemote().playerApi.skipPrevious()
    }
}
