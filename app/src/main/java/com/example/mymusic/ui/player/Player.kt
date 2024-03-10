package com.example.mymusic.ui.player

import android.os.Build
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mymusic.R
import com.example.mymusic.data.Track
import com.example.mymusic.designSystem.component.CroppedShape
import com.example.mymusic.designSystem.icon.MyMusicIcons
import com.example.mymusic.designSystem.theme.DynamicThemePrimaryColorsFromImage
import com.example.mymusic.designSystem.theme.MyMusicTheme
import com.example.mymusic.designSystem.theme.rememberDominantColorState
import com.example.mymusic.designSystem.util.contrastAgainst
import com.example.mymusic.designSystem.util.linearGradientScrim
import com.example.mymusic.designSystem.util.saturation
import java.time.Duration

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Player(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = PlayerViewModel()
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
    ) {
        PlayerContent(
            track = viewModel.playingTrack,
            onBackClick = onBackClick
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PlayerContent(
    track: Track,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val dominantColorState = rememberDominantColorState { color ->
        // We want a color which has sufficient contrast against the surface color
        color.contrastAgainst(surfaceColor) >= 3f
    }
    DynamicThemePrimaryColorsFromImage(dominantColorState) {
        // When the selected image url changes, call updateColorsFromImageUrl() or reset()
        LaunchedEffect(track) {
            dominantColorState.updateColorsFromImageUrl(track.cover)
        }
        Box(
            modifier = modifier
                .fillMaxSize()
        ) {
            Box(modifier = Modifier.size(540.dp)) {
                Image(
                    painter = painterResource(id = track.cover),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
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
                            .saturation(3f),
                        start = Offset(0f, 0f),
                        end = Offset(300f, 1700f),
                        decay = 2f
                    )
            )
            Image(
                painter = painterResource(id = track.cover),
                contentDescription = null,
                contentScale = ContentScale.Crop,
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
                            .saturation(2f)
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
                Column {
                    TrackPlayer(
                        trackName = track.name,
                        artistName = track.artist,
                        trackDuration = Duration.ZERO,
                        onPlayClick = { /*TODO*/ },
                        onSkipPreviousClick = { /*TODO*/ },
                        onSkipNextClick = { /*TODO*/ },
                        onAddToPlaylistClick = { /*TODO*/ },
                        modifier = Modifier.padding(32.dp)
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
    playerButtonSize: Dp = 100.dp,
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
        IconButton(
            onClick = onSkipPreviousClick,
            modifier = buttonsModifier
        ) {
            Image(
                imageVector = MyMusicIcons.SkipPrevious,
                contentDescription = stringResource(R.string.skip_previous),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(LocalContentColor.current),
                modifier = Modifier.fillMaxSize()
            )
        }
        IconButton(
            onClick = onPlayClick,
            modifier = buttonsModifier
        ) {
            Image(
                imageVector = MyMusicIcons.Play,
                contentDescription = stringResource(R.string.play),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(LocalContentColor.current),
                modifier = Modifier.fillMaxSize()
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
                modifier = Modifier.fillMaxSize()
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
    trackName: String,
    artistName: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = trackName,
            style = MaterialTheme.typography.headlineSmall,
            maxLines = 1,
            modifier = Modifier.basicMarquee()
        )
        Text(
            text = artistName,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1
        )
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TrackPlayer(
    trackName: String,
    artistName: String,
    trackDuration: Duration,
    onPlayClick: () -> Unit,
    onSkipPreviousClick: () -> Unit,
    onSkipNextClick: () -> Unit,
    onAddToPlaylistClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            TrackDescription(trackName = trackName, artistName = artistName)
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
                modifier = Modifier.size(50.dp)
            ) {
                Icon(
                    imageVector = MyMusicIcons.ArrowDropDown,
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
                    imageVector = Icons.Default.MoreVert,
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
            trackName = "New Rules",
            artistName = "Dua Lipa",
            trackDuration = Duration.ZERO,
            onPlayClick = { /*TODO*/ },
            onSkipPreviousClick = { /*TODO*/ },
            onSkipNextClick = { /*TODO*/ },
            onAddToPlaylistClick = { /*TODO*/ })
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
        PlayerContent(onBackClick = {}, track = Track("0", cover = R.drawable.screenshot_2024_02_18_at_15_27_40_todays_top_hits))
    }
}

@Preview
@Composable
fun TrackDescriptionPreview() {
    MyMusicTheme {
        TrackDescription(trackName = "Name", artistName = "artist")
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
            onPlayClick = { /*TODO*/ },
            onSkipPreviousClick = { /*TODO*/ },
            onSkipNextClick = { /*TODO*/ })
    }
}