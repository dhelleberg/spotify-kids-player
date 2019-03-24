package de.dhelleberg.spotifykidsplayer.ui.artists

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.adamratzman.spotify.utils.Artist
import com.bumptech.glide.Glide
import de.dhelleberg.spotifykidsplayer.R

class ArtistAdapter(private val artists: List<Artist>,
                    private val fragment: Fragment,
                    private val onClick: (Artist) -> Unit ) : RecyclerView.Adapter<ArtistAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
            .inflate(R.layout.artist_item, parent, false), fragment
        )
    }

    override fun getItemCount(): Int {
        return artists.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val artist = artists[position]
        holder.bind(artist, onClick)
    }


    class ViewHolder(private val view: View, private val fragment: Fragment) : RecyclerView.ViewHolder(view) {
        val artistTitle = view.findViewById<TextView>(R.id.artist_title)
        val artistCover = view.findViewById<ImageView>(R.id.artist_cover)

        fun bind(artist: Artist, onClick:(Artist) -> Unit) {
            view.setOnClickListener {onClick(artist)}
            artistTitle.text = artist.name
            if(artist.images.isNotEmpty())
                Glide.with(fragment).load(artist.images[0].url).into(artistCover)
        }

    }

}
