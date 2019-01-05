package de.dhelleberg.spotifykidsplayer


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import de.dhelleberg.spotifykidsplayer.data.SpotifyRepository
import de.dhelleberg.spotifykidsplayer.data.SpotifyRepositoryImpl
import org.koin.android.ext.android.inject


class PlayActivity : AppCompatActivity() {
    private val TAG by lazy { PlayActivity::class.java.simpleName }

    private lateinit var navController: NavController

    private val spotifyRepository : SpotifyRepository by inject()
    private val spotifyRepositoryImpl : SpotifyRepositoryImpl = spotifyRepository as SpotifyRepositoryImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.play_activity)
        navController = Navigation.findNavController(this, R.id.fragment_navigation_host)

    }


    override fun onStart() {
        Log.d(TAG, "onSTART")
        spotifyRepositoryImpl.onStart(this)
        super.onStart()
    }

    override fun onStop() {
        spotifyRepositoryImpl.onStop(this)
        super.onStop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "on activity result called requestCode $requestCode")
        spotifyRepositoryImpl.onActivityResult(requestCode, resultCode , data)
    }
}
