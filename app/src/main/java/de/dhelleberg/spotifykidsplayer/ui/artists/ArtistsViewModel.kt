package de.dhelleberg.spotifykidsplayer.ui.artists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adamratzman.spotify.models.Artist
import de.dhelleberg.spotifykidsplayer.data.SpotifyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ArtistsViewModel(private val spotifyRepository :SpotifyRepository) : ViewModel() {

    private lateinit var artistsResult: MutableLiveData<List<Artist>>

    fun getArtists(): LiveData<List<Artist>> {
        if (!::artistsResult.isInitialized ) {
            artistsResult = MutableLiveData()
        }
        GlobalScope.launch(Dispatchers.Main) {
            val result = async(Dispatchers.IO) { loadArtists() }.await()
            artistsResult.value = result
        }
        return artistsResult
    }


    private suspend fun loadArtists() : List<Artist> {
        return this.spotifyRepository.getArtists()
    }

}