package com.example.mymusic.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mymusic.R
import com.example.mymusic.core.designSystem.component.ClippedShadowCard
import com.example.mymusic.core.designSystem.component.NetworkImage
import com.example.mymusic.core.designSystem.icon.MyMusicIcons
import com.example.mymusic.core.designSystem.theme.MyMusicTheme
import com.example.mymusic.core.model.Artist
import com.example.mymusic.core.model.SimplifiedArtist

@Composable
fun AlbumCard(
    name: String,
    artists: List<SimplifiedArtist>,
    imageUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ClippedShadowCard(
        elevation = 8.dp,
        onClick = onClick,
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.1f)),
        modifier = modifier
            .width(dimensionResource(id = R.dimen.player_card_width))
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            NetworkImage(
                imageUrl = imageUrl,
                modifier = Modifier
                    .size(70.dp)
            )
            Column(
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .weight(2f)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                )
                Text(
                    text = stringResource(id = R.string.album_label, artistsString(artists)),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}

@PreviewWithBackground
@Composable
fun AlbumCardPreview() {
    MyMusicTheme {
        val mockAlbum = PreviewParameterData.albums[0]
        AlbumCard(
            name = mockAlbum.name,
            artists = mockAlbum.artists,
            imageUrl = mockAlbum.imageUrl,
            onClick = { /*TODO*/ }
        )
    }
}

@PreviewWithBackground
@Composable
fun AlbumCardLongNamePreview() {
    MyMusicTheme {
        val mockAlbum = PreviewParameterData.albums[0]
        AlbumCard(
            name = "This is a very very very very long name",
            artists = mockAlbum.artists,
            imageUrl = mockAlbum.imageUrl,
            onClick = { /*TODO*/ }
        )
    }
}

