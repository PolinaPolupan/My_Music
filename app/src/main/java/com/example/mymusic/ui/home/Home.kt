package com.example.mymusic.ui.home


import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.example.mymusic.R
import com.example.mymusic.data.TempArtist
import com.example.mymusic.data.Track
import com.example.mymusic.designSystem.component.MyMusicHomeBackground
import com.example.mymusic.designSystem.component.ScreenHeader
import com.example.mymusic.designSystem.theme.MyMusicTheme
import com.example.mymusic.designSystem.theme.DynamicThemePrimaryColorsFromImage
import com.example.mymusic.designSystem.util.advancedShadow
import com.example.mymusic.designSystem.util.contrastAgainst
import com.example.mymusic.designSystem.util.darker
import com.example.mymusic.designSystem.util.linearGradientScrim
import com.example.mymusic.designSystem.theme.rememberDominantColorState
import com.example.mymusic.designSystem.util.saturation
import kotlin.math.absoluteValue


@Composable
fun Home(
    onTrackClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = HomeViewModel()
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
    ) {
        HomeContent(
            topPicks = viewModel.topPicks,
            recentlyPlayed = viewModel.recentlyPlayed,
            artists = viewModel.artists,
            onTrackClick = onTrackClick,
            modifier = modifier
                .verticalScroll(rememberScrollState())
        )
    }

}

@Composable
fun HomeContent(
    topPicks: List<Track>,
    artists: List<TempArtist>,
    recentlyPlayed: List<Track>,
    onTrackClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    MyMusicHomeBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()

        ) {
            ScreenHeader(
                titleRes = R.string.listen_now,
                onAvatarClick = { /*TODO*/ },
                avatarImageRes = R.drawable.ic_launcher_background
            )
            TopPicks(
                onTrackClick = onTrackClick,
                topPicks = topPicks
            )
            Spacer(modifier = Modifier.height(16.dp))
            RecentlyPlayed(
                recentlyPlayed = recentlyPlayed,
                onTrackClick = onTrackClick
            )
            for (artist in artists) {
                MoreLikeArtist(artist = artist, onTrackClick = onTrackClick)
            }
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.bottom_bar_height)))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TopPicks(
    topPicks: List<Track>,
    onTrackClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val dominantColorState = rememberDominantColorState { color ->
        // We want a color which has sufficient contrast against the surface color
        color.contrastAgainst(surfaceColor) >= 3f
    }
    DynamicThemePrimaryColorsFromImage(dominantColorState) {
        val pageCount = topPicks.size * 100
        val pagerState = rememberPagerState(
            initialPage = pageCount / 2,
            pageCount = { pageCount }
        )
        val selectedImageUrl = topPicks.getOrNull(pagerState.currentPage)
            ?.cover

        // When the selected image url changes, call updateColorsFromImageUrl() or reset()
        LaunchedEffect(pagerState.currentPage) {
            dominantColorState.updateColorsFromImageUrl(topPicks[pagerState.currentPage % topPicks.size].cover)
        }

        Column(modifier = modifier) {
            Text(
                text = stringResource(id = R.string.top_picks),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(
                        horizontal = dimensionResource(id = R.dimen.padding_large),
                        vertical = dimensionResource(id = R.dimen.padding_small)
                    )
            )
            HorizontalPager(
                pageSpacing = 30.dp,
                pageSize = PageSize.Fixed(dimensionResource(id = R.dimen.top_picks_card_min_size)),
                contentPadding = PaddingValues(
                    horizontal = dimensionResource(id = R.dimen.top_picks_card_min_size) / 2
                ),
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.top_picks_card_max_size))
            ) { page ->
                FeaturedTrack(
                    cover = topPicks[page % topPicks.size].cover,
                    name = topPicks[page % topPicks.size].name,
                    artist = topPicks[page % topPicks.size].artist,
                    onClick = { onTrackClick(topPicks[page % topPicks.size].id) },
                    modifier = Modifier
                        .size(
                            dimensionResource(id = R.dimen.top_picks_card_min_size)
                        )
                        .graphicsLayer {
                            // Calculate the absolute offset for the current page from the
                            // scroll position. We use the absolute value which allows us to mirror
                            // any effects for both directions
                            val pageOffset = (
                                    (pagerState.currentPage - page) + pagerState
                                        .currentPageOffsetFraction
                                    ).absoluteValue

                            // We animate the alpha, between 50% and 100%
                            alpha = lerp(
                                start = 0.5f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                            scaleX = lerp(
                                start = 1f,
                                stop = 1.25f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1.25f)
                            )
                            scaleY = lerp(
                                start = 1f,
                                stop = 1.25f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1.25f)
                            )

                        }
                        .advancedShadow(
                            color = MaterialTheme.colorScheme.primary
                                .saturation(6f)
                                .darker(0.7f),
                            shadowBlurRadius = 16.dp
                        )
                )
            }
        }
    }
}

@Composable
fun RecentlyPlayed(
    recentlyPlayed: List<Track>,
    onTrackClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.recently_played),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(
                    horizontal = dimensionResource(id = R.dimen.padding_large),
                    vertical = dimensionResource(id = R.dimen.padding_small)
                )
        )
        TrackCarousel(
            tracks = recentlyPlayed,
            onTrackClick = onTrackClick
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrackCarousel(
    tracks: List<Track>,
    onTrackClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val pageCount = tracks.size
    val pagerState = rememberPagerState(
        pageCount = { pageCount }
    )
    HorizontalPager(
        pageSize = PageSize.Fixed(dimensionResource(id = R.dimen.track_card_size)),
        state = pagerState,
        modifier = modifier
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen.track_card_size))
            .padding(start = 8.dp)
    ) {page ->
        TrackCard(
            name = tracks[page].name,
            artist = tracks[page].artist,
            cover = tracks[page].cover,
            onClick = { onTrackClick(tracks[page].id) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeaturedTrack(
    name: String,
    artist: String,
    @DrawableRes cover: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Image(
                    contentScale = ContentScale.Crop,
                    painter = painterResource(id = cover),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .linearGradientScrim(
                            color = MaterialTheme.colorScheme.primary
                                .saturation(6f)
                                .darker(0.5f),
                            start = Offset(0f, 0f),
                            end = Offset(0f, 500f)
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = artist,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackCard(
    name: String,
    artist: String,
    @DrawableRes cover: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        shape = RoundedCornerShape(2.dp),
        modifier = modifier
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                contentScale = ContentScale.Crop,
                painter = painterResource(id = cover),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .linearGradientScrim(
                        color = Color.Black,
                        start = Offset(0f, 0f),
                        end = Offset(0f, 500f)
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = artist,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun MoreLikeArtist(
    artist: TempArtist,
    onTrackClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(vertical = 16.dp)) {
        ArtistHeader(
            name = artist.name,
            picture = artist.picture,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        TrackCarousel(tracks = artist.topTracks, onTrackClick = onTrackClick)
    }
}

@Composable
fun ArtistHeader(
    name: String,
    @DrawableRes picture: Int,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = picture),
            contentDescription = null,
            modifier = Modifier
                .clip(RoundedCornerShape(100.dp))
                .size(45.dp)
        )
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(
                text = stringResource(id = R.string.more_like),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Preview
@Composable
fun ArtistHeaderPreview() {
    MyMusicTheme {
        ArtistHeader(
                name = "Dua Lipa",
                picture = R.drawable.dua_lipa___future_nostalgia__official_album_cover_
        )
    }
}
@Preview
@Composable
fun HomePreview() {
    MyMusicTheme {
        HomeContent(
            onTrackClick = {},
            topPicks = listOf(
                Track("0", R.drawable.dua_lipa___future_nostalgia__official_album_cover_),
                Track("0",R.drawable.images),
                Track("0",R.drawable.screenshot_2024_02_18_at_15_27_40_todays_top_hits)
            ),
            recentlyPlayed = listOf(
                Track("0",R.drawable.dua_lipa___future_nostalgia__official_album_cover_),
                Track("0",R.drawable.images),
                Track("0",R.drawable.screenshot_2024_02_18_at_15_27_40_todays_top_hits)
            ),
            artists = listOf(
                TempArtist(
                name = "Dua Lipa",
                picture = R.drawable.dua_lipa___future_nostalgia__official_album_cover_,
                    topTracks = listOf(
                        Track("0",R.drawable.dua_lipa___future_nostalgia__official_album_cover_),
                        Track("0",R.drawable.images),
                        Track("0",R.drawable.screenshot_2024_02_18_at_15_27_40_todays_top_hits)
                    )),
                TempArtist(
                    name = "Dua Lipa",
                    picture = R.drawable.dua_lipa___future_nostalgia__official_album_cover_,
                    topTracks = listOf(
                        Track("0", R.drawable.dua_lipa___future_nostalgia__official_album_cover_),
                        Track("0", R.drawable.images),
                        Track("0", R.drawable.screenshot_2024_02_18_at_15_27_40_todays_top_hits)
                    )),
                )
        )
    }
}