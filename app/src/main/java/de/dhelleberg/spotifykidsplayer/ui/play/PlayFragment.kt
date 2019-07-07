package de.dhelleberg.spotifykidsplayer.ui.play

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.spotify.protocol.types.ImageUri
import com.spotify.protocol.types.PlayerState
import de.dhelleberg.spotifykidsplayer.R
import kotlinx.android.synthetic.main.play_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel

class PlayFragment : Fragment() {

    private val TAG by lazy { PlayFragment::class.java.simpleName }

    private val viewModel: PlayViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.play_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        play_play_pause.setOnClickListener(this::togglePlayPause)
        play_prev.setOnClickListener(this::prevTrack)
        play_next.setOnClickListener(this::nextTrack)
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onSTART")
        viewModel.getPlayerState().observe(this, Observer { updatePlayerState(it) })

        val albumID = arguments?.let { PlayFragmentArgs.fromBundle(it).albumID }
        if (albumID != null && albumID.isNotEmpty()) {
            play(albumID)
            arguments?.clear()
        }

    }

    private fun togglePlayPause(view: View?) {
        viewModel.togglePlayPause()
    }

    private fun nextTrack(view: View?) {
        viewModel.nextTrack()
    }
    private fun prevTrack(view: View?) {
        viewModel.prevTrack()
    }

    override fun onPause() {
        super.onPause()
        viewModel.unSubscribe()
    }

    private var lastImageURI: String = ImageUri("").raw

    private fun updateViewState(playerState: PlayerState) {
        Log.d(TAG, "update view state")
        play_play_pause.isSelected = playerState.isPaused
        //only update image if needed
        if (playerState?.track?.imageUri != null) {
            Log.d(TAG, "check image update $lastImageURI uri: ${playerState.track.imageUri.raw}")
            if (lastImageURI != playerState.track.imageUri.raw) {
                Log.d(TAG, "updateing image")
                viewModel.loadBitmapForCurrentTrack(playerState.track.imageUri) {
                    Log.d(TAG, "image loaded")
                    play_cover.setImageBitmap(it)
                    lastImageURI = playerState.track.imageUri.raw
                }
            }
        }
        play_track.text = playerState?.track?.name
    }

    private fun updatePlayerState(playerState: PlayerState) {
        Log.d(TAG, "update player State: $playerState")
        updateViewState(playerState)
    }

    private fun play(id: String) {
        viewModel.play(id)
    }

}
