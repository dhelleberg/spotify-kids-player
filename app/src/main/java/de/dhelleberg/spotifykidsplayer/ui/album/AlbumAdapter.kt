package de.dhelleberg.spotifykidsplayer.ui.album

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.adamratzman.spotify.utils.LinkedResult
import com.adamratzman.spotify.utils.SimpleAlbum
import com.bumptech.glide.Glide
import de.dhelleberg.spotifykidsplayer.R

class AlbumAdapter(private val albums: LinkedResult<SimpleAlbum>,
                          private val fragment: Fragment,
                          private val onClick: (SimpleAlbum) -> Unit ) : RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return albums.items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val simpleAlbum = albums.items[position]
        holder.bind(simpleAlbum, onClick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumAdapter.ViewHolder {
        return AlbumAdapter.ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.album_item, parent, false), fragment
        )
    }


    class ViewHolder(private val view: View, private val fragment: Fragment) : RecyclerView.ViewHolder(view) {
        val albumTitle = view.findViewById<TextView>(R.id.album_title)
        val albumCover = view.findViewById<ImageView>(R.id.album_cover)

        fun bind(simpleAlbum: SimpleAlbum, onClick: (SimpleAlbum) -> Unit) {
            view.setOnClickListener {onClick(simpleAlbum)}
            albumTitle.text = simpleAlbum.name
            Glide.with(fragment).load(simpleAlbum.images[0].url).into(albumCover)


        }

    }


}
