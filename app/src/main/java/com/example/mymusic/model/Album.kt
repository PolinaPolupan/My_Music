package com.example.mymusic.model

import com.example.mymusic.R

enum class AlbumType(typeName: Int) {
    Album(R.string.album_type_name),
    Single(R.string.single_type_name),
    Compilation(R.string.compilation_type_name)
}

/**
 * [Album] defines an album, which holds tracks.
 * [Album] and [SimplifiedAlbum] were divided in order to fit the UI data
 * (some screens don't need to have any information about tracks of albums)
 * and Spotify API response
 * https://developer.spotify.com/documentation/web-api/reference/get-an-album
 */
data class Album(
    val id: String,
    val type: AlbumType,
    val imageUrl: String,
    val name: String,
    val artists: List<SimplifiedArtist>,
    val tracks: List<SimplifiedTrack>
)
