package com.example.mymusic.feature.home


import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.example.mymusic.R
import com.example.mymusic.core.designSystem.component.BlurredImageHeader
import com.example.mymusic.core.designSystem.component.MyMusicGradientBackground
import com.example.mymusic.core.designSystem.component.NetworkImage
import com.example.mymusic.core.designSystem.component.ScreenHeader
import com.example.mymusic.core.designSystem.theme.DynamicThemePrimaryColorsFromImage
import com.example.mymusic.core.designSystem.theme.MyMusicTheme
import com.example.mymusic.core.designSystem.theme.rememberDominantColorState
import com.example.mymusic.core.designSystem.util.contrastAgainst
import com.example.mymusic.core.model.Artist
import com.example.mymusic.core.model.Track
import com.example.mymusic.core.ui.FeaturedTrack
import com.example.mymusic.core.ui.PreviewParameterData
import com.example.mymusic.core.ui.TrackCard
import com.example.mymusic.core.ui.TracksPreviewParameterProvider
import kotlin.math.absoluteValue


@Composable
internal fun HomeScreen(
    onTrackClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = HomeViewModel()
) {
    HomeContent(
        topPicks = viewModel.topPicks,
        recentlyPlayed = viewModel.recentlyPlayed,
        moreLikeArtists = viewModel.moreLikeArtists,
        onTrackClick = onTrackClick,
        modifier = modifier
            .verticalScroll(rememberScrollState())
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun HomeContent(
    topPicks: List<Track>,
    moreLikeArtists: Map<Artist, List<Track>>,
    recentlyPlayed: List<Track>,
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
        // When the selected image url changes, call updateColorsFromImageUrl() or reset()
        LaunchedEffect(pagerState.currentPage) {
            dominantColorState.updateColorsFromImageUrl(topPicks[pagerState.currentPage % topPicks.size].imageUrl)
        }

        MyMusicGradientBackground(
            modifier = modifier,
            contentAlignment = Alignment.TopCenter
        ) {
            BlurredImageHeader(
                imageUrl = topPicks[pagerState.currentPage % topPicks.size].imageUrl,
                alpha = 0.5f
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()

            ) {
                ScreenHeader(
                    titleRes = R.string.listen_now,
                    onAvatarClick = { /*TODO*/ },
                    avatarImageRes = R.drawable.ic_launcher_background
                )
                Spacer(modifier = Modifier.height(16.dp))
                TopPicks(
                    onTrackClick = onTrackClick,
                    topPicks = topPicks,
                    pagerState = pagerState
                )
                Spacer(modifier = Modifier.height(16.dp))
                RecentlyPlayed(
                    recentlyPlayed = recentlyPlayed,
                    onTrackClick = onTrackClick
                )
                for (artist in moreLikeArtists) {
                    MoreLikeArtist(
                        artist = artist.key,
                        tracks = artist.value,
                        onTrackClick = onTrackClick
                    )
                }
                Spacer(modifier = Modifier.height(160.dp))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun TopPicks(
    topPicks: List<Track>,
    pagerState: PagerState,
    onTrackClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
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
                coverUrl = topPicks[page % topPicks.size].imageUrl,
                name = topPicks[page % topPicks.size].name,
                artists = topPicks[page % topPicks.size].artists,
                onClick = { onTrackClick(topPicks[page % topPicks.size].id) },
                modifier = Modifier
                    .graphicsLayer {
                        // Calculate the absolute offset for the current page from the
                        // scroll position. We use the absolute value which allows us to mirror
                        // any effects for both directions
                        val pageOffset = (
                                (pagerState.currentPage - page) + pagerState
                                    .currentPageOffsetFraction
                                ).absoluteValue

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

            )
        }
    }
}

@Composable
internal fun RecentlyPlayed(
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
internal fun TrackCarousel(
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
            artists = tracks[page].artists,
            imageUrl = tracks[page].imageUrl,
            onClick = { onTrackClick(tracks[page].id) },
            modifier = Modifier.padding(4.dp)
        )
    }
}

@Composable
private fun MoreLikeArtist(
    artist: Artist,
    tracks: List<Track>,
    onTrackClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(vertical = 16.dp)) {
        ArtistHeader(
            name = artist.name,
            pictureUrl = artist.imageUrl,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        TrackCarousel(tracks = tracks, onTrackClick = onTrackClick)
    }
}

@Composable
private fun ArtistHeader(
    name: String,
    pictureUrl: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        NetworkImage(
            imageUrl = pictureUrl,
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
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
                pictureUrl = ""
        )
    }
}
@Preview
@Composable
fun HomePreview(
    @PreviewParameter(TracksPreviewParameterProvider::class)
    tracks : List<Track>
) {
    MyMusicTheme {
        HomeContent(
            onTrackClick = {},
            topPicks = tracks,
            recentlyPlayed = tracks,
            moreLikeArtists = PreviewParameterData.moreLikeArtists
        )
    }
}