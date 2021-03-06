package de.dhelleberg.spotifykidsplayer.ui.album

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adamratzman.spotify.models.PagingObject
import com.adamratzman.spotify.models.SimpleAlbum
import de.dhelleberg.spotifykidsplayer.data.SpotifyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AlbumViewModel(private val spotifyRepository : SpotifyRepository) : ViewModel() {

    private lateinit var albumResult: MutableLiveData<PagingObject<SimpleAlbum>>

    fun getAlbums(artistURI: String): LiveData<PagingObject<SimpleAlbum>> {
        if (!::albumResult.isInitialized ) {
            albumResult = MutableLiveData()
        }
        GlobalScope.launch(Dispatchers.Main) {
            val result = async(Dispatchers.IO) { loadAlbums(artistURI) }.await()
            albumResult.value = result
        }
        return albumResult
    }


    private suspend fun loadAlbums(artistURI: String) : PagingObject<SimpleAlbum> {
        return this.spotifyRepository.getAlbums(artistURI)
    }
}
