package de.dhelleberg.spotifykidsplayer.ui.album

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
import com.adamratzman.spotify.models.PagingObject
import com.adamratzman.spotify.models.SimpleAlbum
import de.dhelleberg.spotifykidsplayer.R
import kotlinx.android.synthetic.main.album_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel

class AlbumFragment : Fragment() {
    private val TAG by lazy { AlbumFragment::class.java.simpleName }

    val viewModel: AlbumViewModel by viewModel()
    private lateinit var viewAdapter: RecyclerView.Adapter<*>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.album_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        albums_rcv.layoutManager = LinearLayoutManager(context)
    }

    override fun onStart() {
        super.onStart()
        val artistID = arguments?.let { AlbumFragmentArgs.fromBundle(it).artistID }
        Log.d(TAG, "should get ID: "+artistID)
        if(artistID != null)
            viewModel.getAlbums(artistID).observe(this, Observer {  showAlbums(it) })

    }

    private fun showAlbums(albumList: PagingObject<SimpleAlbum>?) {
        if (albumList != null) {
            albumList.items.forEach {
                Log.d(TAG, "album: ${it.name}")
                viewAdapter = AlbumAdapter(albumList, this) {onListItemClicked(it)}
                albums_rcv.adapter = viewAdapter
            }
        }
    }

    private fun onListItemClicked(simpleAlbum: SimpleAlbum) {
        Log.d(TAG, "clicked on artist ${simpleAlbum.uri}")
        NavHostFragment.findNavController(this)
            .navigate(AlbumFragmentDirections.actionAlbumFragmentToPlayFragment().setAlbumID(simpleAlbum.id))
    }


}
