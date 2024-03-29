package com.example.mymusic.feature.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    Surface(
        modifier = modifier
            .fillMaxSize()
    ) {
        LibraryContent(
            playlists = viewModel.usersPlaylists,
            currentSortOption = viewModel.currentSortOption.value,
            onSortOptionChanged =  { viewModel.currentSortOption.value = it }
        )
    }
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
        ) {
            ScreenHeader(
                titleRes = R.string.your_library,
                onAvatarClick = { /*TODO*/ },
                avatarImageRes = R.drawable.images
            )

            Sort(
                sortOption = currentSortOption,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(16.dp)
                    .clickable {
                        showBottomSheet = true
                    }
            )
            for (playlist in playlists) {
                PlaylistCard(name = playlist.name, ownerName = playlist.ownerName, imageUrl = playlist.imageUrl, onClick = {})
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