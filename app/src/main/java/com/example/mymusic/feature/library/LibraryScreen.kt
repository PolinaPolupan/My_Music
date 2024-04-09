package com.example.mymusic.feature.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.isPopupLayout
import com.example.mymusic.R
import com.example.mymusic.core.designSystem.component.MyMusicGradientBackground
import com.example.mymusic.core.designSystem.component.ScreenHeader
import com.example.mymusic.core.designSystem.component.Sort
import com.example.mymusic.core.designSystem.component.SortBottomSheet
import com.example.mymusic.core.designSystem.component.SortOption
import com.example.mymusic.core.designSystem.theme.MyMusicTheme
import com.example.mymusic.core.model.Playlist
import com.example.mymusic.core.ui.PlaylistCard
import com.example.mymusic.core.ui.PreviewParameterData


@Composable
fun LibraryScreen(
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = LibraryViewModel()
) {
    LibraryContent(
        playlists = viewModel.usersPlaylists,
        currentSortOption = viewModel.currentSortOption.value,
        onSortOptionChanged =  { viewModel.currentSortOption.value = it },
        modifier = modifier
            .fillMaxSize()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryContent(
    playlists: List<Playlist>,
    onSortOptionChanged: (SortOption) -> Unit,
    currentSortOption: SortOption,
    modifier: Modifier = Modifier
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    if (showBottomSheet) {
        SortBottomSheet(
            currentOption = currentSortOption,
            onDismissRequest = { showBottomSheet = false },
            onSelection = onSortOptionChanged
        )
    }

    MyMusicGradientBackground(modifier = modifier) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 16.dp, start = 16.dp, bottom = dimensionResource(id = R.dimen.player_with_bottom_app_bar_height))
        ) {
            item {
                ScreenHeader(
                    titleRes = R.string.your_library,
                    onAvatarClick = { /*TODO*/ },
                    avatarImageRes = R.drawable.images
                )

                Sort(
                    sortOption = currentSortOption,
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable {
                            showBottomSheet = true
                        }
                )
            }
            items(items = playlists) { playlist ->
                PlaylistCard(
                    name = playlist.name,
                    ownerName = playlist.ownerName,
                    imageUrl = playlist.imageUrl,
                    onClick = {/*TODO*/},
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview
@Composable
fun LibraryPreview() {
    MyMusicTheme {
        LibraryContent(
            playlists = PreviewParameterData.playlists,
            currentSortOption = SortOption.RECENTLY_ADDED,
            onSortOptionChanged = {}
        )
    }
}