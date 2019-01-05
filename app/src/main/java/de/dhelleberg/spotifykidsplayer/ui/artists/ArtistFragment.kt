package de.dhelleberg.spotifykidsplayer.ui.artists

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adamratzman.spotify.utils.Artist
import de.dhelleberg.spotifykidsplayer.R
import kotlinx.android.synthetic.main.artist_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel


class ArtistFragment : Fragment() {
    private val TAG by lazy { ArtistFragment::class.java.simpleName }

    val artistsViewModel: ArtistsViewModel by viewModel()
    private lateinit var viewAdapter: RecyclerView.Adapter<*>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.artist_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        artists_rcv.layoutManager = LinearLayoutManager(context)

    }

    override fun onStart() {
        super.onStart()
        artistsViewModel.getArtists().observe(this, Observer { showArtists(it) })
    }

    private fun showArtists(items: List<Artist>) {
        Log.d(TAG, "artist: " + items.map { it.name })
        viewAdapter = ArtistAdapter(items, this) { onListItemClicked(it) }
        artists_rcv.adapter = viewAdapter

    }

    private fun onListItemClicked(artist: Artist) {
        Log.d(TAG, "clicked on artist ${artist.uri}")
        NavHostFragment.findNavController(this)
            .navigate(ArtistFragmentDirections.actionArtistFragmentToAlbumFragment(artist.uri))
    }


}
