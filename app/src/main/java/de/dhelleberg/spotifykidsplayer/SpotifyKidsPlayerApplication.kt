package de.dhelleberg.spotifykidsplayer

import android.app.Application
import de.dhelleberg.spotifykidsplayer.Modules.appModule
import org.koin.android.ext.android.startKoin

class SpotifyKidsPlayerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(appModule))
    }
}