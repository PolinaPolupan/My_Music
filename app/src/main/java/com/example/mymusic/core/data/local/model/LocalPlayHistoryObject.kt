package com.example.mymusic.core.data.local.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.mymusic.model.Track

data class LocalPlayHistoryWithArtists(
    @Embedded val trackHistory: LocalPlayHistory,
    @Relation(
        parentColumn = "trackId",
        entityColumn = "artistId",
        associateBy = Junction(TrackArtistCrossRef::class)
    )
    val artists: List<LocalArtist>,
    @Relation(
        parentColumn = "albumId",
        entityColumn = "simplifiedArtistId",
        associateBy = Junction(AlbumArtistCrossRef::class)
    )
    val albumArtists: List<LocalSimplifiedArtist>
)

fun LocalPlayHistoryWithArtists.toExternal() = Track(
    id = trackHistory.track.id,
    album = trackHistory.track.album.toExternal(albumArtists.toExternal()),
    name = trackHistory.track.name,
    artists = artists.toExternal()
)

fun List<LocalPlayHistoryWithArtists>.toExternal() = map(LocalPlayHistoryWithArtists::toExternal)
