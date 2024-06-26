package com.example.mymusic.core.data.network

import com.example.mymusic.core.data.network.model.AlbumTracksResponse
import com.example.mymusic.core.data.network.model.ErrorResponse
import com.example.mymusic.core.data.network.model.RecommendationsResponse
import com.example.mymusic.core.data.network.model.RecentlyPlayedTracksResponse
import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface MyMusicAPIService {

    @GET("https://api.spotify.com/v1/recommendations?limit=10&seed_genres=pop")
    suspend fun getRecommendations(): NetworkResponse<RecommendationsResponse, ErrorResponse>

    @GET("https://api.spotify.com/v1/me/player/recently-played")
    suspend fun getRecentlyPlayed(): NetworkResponse<RecentlyPlayedTracksResponse, ErrorResponse>

    @GET("https://api.spotify.com/v1/albums/{id}/tracks")
    suspend fun getAlbumTracks(@Path("id") id : String): NetworkResponse<AlbumTracksResponse, ErrorResponse>
}
