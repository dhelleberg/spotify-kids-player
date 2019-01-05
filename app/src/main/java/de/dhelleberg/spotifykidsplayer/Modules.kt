package de.dhelleberg.spotifykidsplayer

import de.dhelleberg.spotifykidsplayer.data.SpotifyRepository
import de.dhelleberg.spotifykidsplayer.data.SpotifyRepositoryImpl
import de.dhelleberg.spotifykidsplayer.ui.album.AlbumViewModel
import de.dhelleberg.spotifykidsplayer.ui.artists.ArtistsViewModel
import de.dhelleberg.spotifykidsplayer.ui.play.PlayViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

object Modules {
    val appModule = module {
        single<SpotifyRepository> { SpotifyRepositoryImpl(androidContext()) }
        viewModel{ ArtistsViewModel(get()) }
        viewModel{ AlbumViewModel(get()) }
        viewModel{ PlayViewModel(get()) }
    }
}