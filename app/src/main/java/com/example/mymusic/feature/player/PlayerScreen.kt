package com.example.mymusic.feature.player

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mymusic.R
import com.example.mymusic.core.designSystem.component.CroppedShape
import com.example.mymusic.core.designSystem.component.NetworkImage
import com.example.mymusic.core.designSystem.component.MyMusicIcons
import com.example.mymusic.core.designSystem.theme.DynamicThemePrimaryColorsFromImage
import com.example.mymusic.core.designSystem.theme.MyMusicTheme
import com.example.mymusic.core.designSystem.theme.rememberDominantColorState
import com.example.mymusic.core.designSystem.util.contrastAgainst
import com.example.mymusic.core.designSystem.component.linearGradientScrim
import com.example.mymusic.core.designSystem.util.saturation
import com.example.mymusic.model.Track
import com.example.mymusic.core.ui.PreviewParameterData
import java.time.Duration

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PlayerScreen(
    onBackClick: () -> Unit,
    onAddToPlaylistClick: (String) -> Unit,
    onNavigateToAlbum: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        /*TODO: Create loading screen*/
        PlayerUiState.Loading -> Unit
        is PlayerUiState.Success ->
            PlayerContent(
                track = (uiState as PlayerUiState.Success).track,
                onBackClick = onBackClick,
                onAddToPlaylistClick = onAddToPlaylistClick,
                onNavigateToAlbum = onNavigateToAlbum,
                onAlbumClick = viewModel::onAlbumClick,
                modifier = modifier
                    .fillMaxSize(),
            )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PlayerContent(
    track: Track,
    onBackClick: () -> Unit,
    onAddToPlaylistClick: (String) -> Unit,
    onNavigateToAlbum: (String) -> Unit,
    modifier: Modifier = Modifier,
    onAlbumClick: (String) -> Unit
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val dominantColorState = rememberDominantColorState { color ->
        // We want a color which has sufficient contrast against the surface color
        color.contrastAgainst(surfaceColor) >= 3f
    }
    DynamicThemePrimaryColorsFromImage(dominantColorState) {
        // When the selected image url changes, call updateColorsFromImageUrl() or reset()
        LaunchedEffect(track) {
            dominantColorState.updateColorsFromImageUrl(track.album.imageUrl)
        }
        Box(
            modifier = modifier
                .fillMaxSize()
        ) {
            Box(modifier = Modifier.size(540.dp)) {
                NetworkImage(
                    track.album.imageUrl,
                    modifier = Modifier.fillMaxSize()
                )
            }
            TopAppBar(onBackPress = onBackClick, modifier = Modifier.padding(horizontal = 8.dp))
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.BottomCenter)
                    .linearGradientScrim(
                        color = MaterialTheme.colorScheme.primary
                            .saturation(5f),
                        start = Offset(0f, 0f),
                        end = Offset(300f, 1700f),
                        decay = 2f
                    )
            )
            NetworkImage(
                track.album.imageUrl,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(50.dp)
                    .align(Alignment.BottomCenter)
                    .clip(CroppedShape(heightPart = 0.5f, reverseHeight = true))
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.BottomCenter)
                    .linearGradientScrim(
                        color = MaterialTheme.colorScheme.primary
                            .saturation(3f)
                            .copy(alpha = 0.7f),
                        start = Offset(0f, 0f),
                        end = Offset(600f, 1400f),
                        decay = 2f
                    )
                    .linearGradientScrim(
                        color = Color.Black.copy(alpha = 0.8f),
                        start = Offset(0f, 0f),
                        end = Offset(0f, 2500f),
                        decay = 3f
                    ),
                contentAlignment = Alignment.BottomCenter
            ) {
                var artistsString = ""
                for (artist in track.artists) {
                    artistsString += artist.name + ", "
                }
                artistsString = artistsString.substring(0, artistsString.length - 2)
                Column {
                    TrackPlayer(
                        albumId = track.album.id,
                        trackName = track.name,
                        artistName = artistsString,
                        trackDuration = Duration.ZERO,
                        onPlayClick = { /*TODO*/ },
                        onSkipPreviousClick = { /*TODO*/ },
                        onSkipNextClick = { /*TODO*/ },
                        onAddToPlaylistClick = { onAddToPlaylistClick(track.id) },
                        onNavigateToAlbum = onNavigateToAlbum,
                        onAlbumClick = onAlbumClick,
                        modifier = Modifier.padding(32.dp),
                    )

                    Spacer(modifier = Modifier.height(64.dp))
                }
            }
        }
    }
}

@Composable
private fun PlayerButtons(
    onPlayClick: () -> Unit,
    onSkipPreviousClick: () -> Unit,
    onSkipNextClick: () -> Unit,
    modifier: Modifier = Modifier,
    playerButtonSize: Dp = 80.dp,
    sideButtonSize: Dp = 70.dp
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val buttonsModifier = Modifier
            .size(sideButtonSize)
            .semantics { role = Role.Button }
        val playerButtonModifier = Modifier
            .size(playerButtonSize)
            .semantics { role = Role.Button }
        IconButton(
            onClick = onSkipPreviousClick,
            modifier = buttonsModifier
        ) {
            Image(
                imageVector = MyMusicIcons.SkipPrevious,
                contentDescription = stringResource(R.string.skip_previous),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(LocalContentColor.current),
                modifier = buttonsModifier
            )
        }
        IconButton(
            onClick = onPlayClick,
            modifier = playerButtonModifier
        ) {
            Image(
                imageVector = MyMusicIcons.Play,
                contentDescription = stringResource(R.string.play),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(LocalContentColor.current),
                modifier = playerButtonModifier
            )
        }
        IconButton(
            onClick = onSkipNextClick,
            modifier = buttonsModifier
        ) {
            Image(
                imageVector = MyMusicIcons.SkipNext,
                contentDescription = stringResource(R.string.skip_next),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(LocalContentColor.current),
                modifier = buttonsModifier
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun PlayerSlider(trackDuration: Duration?) {
    if (trackDuration != null) {
        Column(Modifier.fillMaxWidth()) {
            Slider(value = 0f, onValueChange = { })
            Row(Modifier.fillMaxWidth()) {
                Text(text = "0s")
                Spacer(modifier = Modifier.weight(1f))
                Text("${trackDuration.seconds}s")
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TrackDescription(
    albumId: String,
    trackName: String,
    artists: String,
    onNavigateToAlbum: (String) -> Unit,
    modifier: Modifier = Modifier,
    onAlbumClick: (String) -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = trackName,
            style = MaterialTheme.typography.headlineSmall,
            maxLines = 1,
            modifier = Modifier
                .basicMarquee()
                .clickable {
                    onAlbumClick(albumId)
                    onNavigateToAlbum(albumId)
                }
        )
        Text(
            text = artists,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            modifier = Modifier.basicMarquee()
        )
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TrackPlayer(
    albumId: String,
    trackName: String,
    artistName: String,
    trackDuration: Duration,
    onPlayClick: () -> Unit,
    onSkipPreviousClick: () -> Unit,
    onSkipNextClick: () -> Unit,
    onAddToPlaylistClick: () -> Unit,
    onNavigateToAlbum: (String) -> Unit,
    modifier: Modifier = Modifier,
    onAlbumClick: (String) -> Unit
) {
    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            TrackDescription(
                albumId = albumId,
                trackName = trackName,
                artists = artistName,
                onNavigateToAlbum = onNavigateToAlbum,
                onAlbumClick = onAlbumClick
            )
            IconButton(
                onClick = onAddToPlaylistClick,
                modifier = Modifier.size(50.dp)
            ) {
                Icon(
                    imageVector = MyMusicIcons.Add,
                    contentDescription = stringResource(id = R.string.add_to_playlist),
                    modifier = Modifier.size(50.dp)
                )
            }
        }
        PlayerSlider(trackDuration = trackDuration)
        PlayerButtons(
            onPlayClick = onPlayClick,
            onSkipPreviousClick = onSkipPreviousClick,
            onSkipNextClick = onSkipNextClick
        )
    }
}

/*TODO: too bright background color problem (buttons won't be visible)*/
@Composable
private fun TopAppBar(
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onBackPress,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = MyMusicIcons.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = { /* TODO */ },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = MyMusicIcons.More,
                    contentDescription = stringResource(R.string.more),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun TrackPlayerPreview() {
    MyMusicTheme {
        TrackPlayer(
            albumId = "0",
            trackName = "New Rules",
            artistName = "Dua Lipa",
            trackDuration = Duration.ZERO,
            onPlayClick = {},
            onSkipPreviousClick = {},
            onSkipNextClick = {},
            onAddToPlaylistClick = {},
            onNavigateToAlbum = {},
            onAlbumClick = {}
        )
    }
}

@Preview
@Composable
fun TopAppBarPreview() {
    MyMusicTheme {
        TopAppBar(onBackPress = {})
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun PlayerPreview() {
    MyMusicTheme {
        PlayerContent(
            track = PreviewParameterData.tracks[0],
            onBackClick = {},
            onAddToPlaylistClick = {},
            onNavigateToAlbum = {},
            onAlbumClick = {}
        )
    }
}

@Preview
@Composable
fun TrackDescriptionPreview() {
    MyMusicTheme {
        TrackDescription(
            albumId ="0",
            trackName = "Name",
            artists = "artist",
            onNavigateToAlbum = {},
            onAlbumClick = {}
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun PlayerSliderPreview() {
    MyMusicTheme {
        PlayerSlider(trackDuration = Duration.ZERO)
    }
}

@Preview
@Composable
fun PlayerButtonsPreview() {
    MyMusicTheme {
        PlayerButtons(
            onPlayClick = {},
            onSkipPreviousClick = {},
            onSkipNextClick = {})
    }
}