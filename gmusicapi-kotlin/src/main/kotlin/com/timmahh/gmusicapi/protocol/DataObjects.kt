package com.timmahh.gmusicapi.protocol

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonDefaultValueBoolean
import se.ansman.kotshi.JsonDefaultValueInt
import se.ansman.kotshi.JsonDefaultValueString
import se.ansman.kotshi.JsonSerializable
import java.util.*

@JsonSerializable
data class ImageColorStyles(
		val primary: ImageColors,
		val scrim: ImageColors,
		val accent: ImageColors)

@JsonSerializable
data class ImageColors(val red: Int, val green: Int, val blue: Int)

@JsonSerializable
data class Image(val kind: String,
                 val url: String,
                 val aspectRatio: String? = null,
                 val autogen: Boolean? = null,
                 val colorStyles: ImageColorStyles? = null)

@JsonSerializable
data class ImageUrl(val url: String)

@JsonSerializable
data class Video(val kind: String, val id: String, val title: String? = null, val thumbnails: List<Thumbnail>)

@JsonSerializable
data class Thumbnail(val url: String, val width: Int, val height: Int)

@JsonSerializable
data class Track(val kind: String,
                 val title: String,
                 val artist: String,
                 val album: String,
                 @JsonDefaultValueString("")
                 val albumArtist: String = "",
                 val trackNumber: Int,
                 val totalTrackCount: Int? = null,
                 val durationMillis: String,
                 val albumArtRef: List<ImageUrl>? = null,
                 val artistArtRef: List<ImageUrl>? = null,
                 val discNumber: Int,
                 val totalDiscCount: Int? = null,
                 val estimatedSize: String? = null,
                 val trackType: String? = null,
                 val storeId: String? = null,
                 val albumId: String,
                 val artistId: List<String>? = null,
                 val nid: String? = null,
                 val trackAvailableForPurchase: Boolean? = null,
                 val albumAvailableForPurchase: Boolean? = null,
                 @JsonDefaultValueString("")
                 val composer: String = "",
                 val playCount: Int? = null,
                 val year: Int? = null,
                 val rating: String? = null,
                 val genre: String? = null,
                 val trackAvailableForSubscription: Boolean? = null,
                 val lastRatingChangeTimestamp: String? = null,
                 val primaryVideo: Video? = null,
                 val lastModifiedTimestamp: String? = null,
                 val explicitType: String? = null,
                 val contentType: String? = null,
                 val deleted: String? = null,
                 val creationTimestamp: String? = null,
                 @JsonDefaultValueString("")
                 val comment: String? = "",
                 val beatsPerMinute: Int? = null,
                 val recentTimestamp: String? = null,
                 val clientId: String? = null,
                 val id: String? = null)

@JsonSerializable
data class Playlist(val kind: String,
                    val name: String,
                    val deleted: Boolean? = null,
                    val type: String? = null, /* pattern = r'MAGIC|SHARED|USER_GENERATED' */
                    val lastModifiedTimestamp: String? = null,
                    val recentTimestamp: String? = null,
                    val shareToken: String,
                    val ownerProfilePhotoUrl: String? = null,
                    val ownerName: String? = null,
                    val accessControlled: Boolean? = null,
                    val shareState: String? = null, /* pattern = r'PRIVATE|PUBLIC' */
                    val creationTimestamp: String? = null,
                    val id: String? = null,
                    val albumArtRef: List<ImageUrl>? = null,
                    @JsonDefaultValueString("")
                    val description: String? = "",
                    val explicitType: String? = null,
                    val contentType: String? = null)

@JsonSerializable
data class PlaylistEntry(val kind: String,
                         val id: String,
                         val clientId: String,
                         val playlistId: String,
                         val absolutePosition: String,
                         val trackId: String,
                         val creationTimestamp: String,
                         val lastModifiedTimestamp: String,
                         val deleted: Boolean,
                         val source: String,
                         val track: Track? = null)

@JsonSerializable
data class Attribution(val kind: String,
                       @Json(name = "license_url")
                       val licenseUrl: String? = null,
                       @Json(name = "license_title")
                       val licenseTitle: String? = null,
                       @Json(name = "source_title") @JsonDefaultValueString("")
                       val sourceTitle: String? = "",
                       @Json(name = "source_url") @JsonDefaultValueString("")
                       val sourceUrl: String? = "")

@JsonSerializable
data class Album(val kind: String,
                 val name: String,
                 val albumArtist: String,
                 val albumArtRef: String? = null,
                 val albumId: String,
                 @JsonDefaultValueString("")
                 val artist: String = "",
                 val artistId: List<String>,
                 val year: Int? = null,
                 val tracks: List<Track>? = null,
                 val description: String? = null,
                 @Json(name = "description_attribution")
                 val descriptionAttribution: Attribution? = null,
                 val explicitType: String? = null,
                 val contentType: String? = null)

@JsonSerializable
data class Artist(val kind: String,
                  val name: String,
                  val artistArtRef: String? = null,
                  val artistArtRefs: List<Image>? = null,
                  val artistBio: String? = null,
                  @JsonDefaultValueString("")
                  val artistId: String? = "",
                  val albums: List<Album>? = null,
                  val topTracks: List<Track>? = null,
                  @Json(name = "total_albums")
                  val totalAlbums: Int? = null,
                  @Json(name = "artist_bio_attribution")
                  val artistBioAttribution: Attribution? = null,
                  @Json(name = "related_artists")
                  val relatedArtists: List<Artist>? = null)

@JsonSerializable
data class Genre(val kind: String,
                 val id: String,
                 val name: String,
                 val children: List<String>? = null,
                 val parentId: String? = null,
                 val images: List<ImageUrl>? = null)

@JsonSerializable
data class StationMetadataSeed(val kind: String,
                               val artist: Artist? = null,
                               val genre: Genre? = null)

@JsonSerializable
data class StationSeed(val kind: String,
                       val seedType: String,
                       val albumId: String? = null,
                       val artistId: String? = null,
                       val genreId: String? = null,
                       val trackId: String? = null,
                       val trackLockerId: String? = null,
                       val curatedStationId: String? = null,
                       val metadataSeed: StationMetadataSeed? = null)

@JsonSerializable
data class StationTrack(val track: Track,
                        @Json(name = "wentryid") val wentryId: String? = null)

@JsonSerializable
data class Station(val imageUrl: String? = null,
                   val kind: String,
                   val name: String,
                   val deleted: Boolean? = null,
                   val lastModifiedTimestamp: String? = null,
                   val recentTimestamp: String? = null,
                   val clientId: String? = null,
                   val sessionToken: String? = null,
                   val skipEventHistory: List<Any>,
                   val seed: StationSeed,
                   val stationSeeds: List<StationSeed>,
                   val id: String? = null,
                   val description: String? = null,
                   val tracks: List<StationTrack>? = null,
                   val imageUrls: List<Image>? = null,
                   val compositeArtRefs: List<Image>? = null,
                   val contentTypes: List<String>? = null,
                   val byline: String? = null,
                   val adTargeting: AdTarget? = null)

@JsonSerializable
data class AdTarget(val keyword: List<String>)

@JsonSerializable
data class ListenNowAlbum(
		@Json(name = "artist_metajam_id") val artistMetajamId: String,
		@Json(name = "artist_name") val artistName: String,
		@Json(name = "artist_profile_image") val artistProfileImage: ImageUrl,
		@JsonDefaultValueString("")
		val description: String = "",
		@Json(name = "description_attribution")
		val descriptionAttribution: Attribution? = null,
		val explicitType: String? = null,
		val id: ListenNowAlbumId,
		val title: String)

@JsonSerializable
data class ListenNowAlbumId(val metajamCompactKey: String,
                            val artist: String,
                            val title: String)

@JsonSerializable
data class ListenNowStation(
		@Json(name = "highlight_color") val highlightColor: String? = null,
		val id: ListenNowStationIdSeeds,
		@Json(name = "profile_image") val profileImage: ImageUrl? = null,
		val title: String)

@JsonSerializable
data class ListenNowStationIdSeeds(val seeds: List<StationSeed>)

@JsonSerializable
data class ListenNowItem(val kind: String,
                         val compositeArtRefs: List<Image>? = null,
                         val images: List<Image>? = null,
                         @Json(name = "suggestion_reason") val suggestionReason: String,
                         @Json(name = "suggestion_text") val suggestionText: String,
                         val type: String,
                         val album: ListenNowAlbum? = null,
                         @Json(name = "radio_station")
                         val radioStation: ListenNowStation? = null)

@JsonSerializable
data class PodcastGenre(val id: String,
                        val displayName: String,
                        val subgroups: List<PodcastGenre>? = null)

@JsonSerializable
data class PodcastEpisode(val art: List<Image>? = null,
                          val author: String? = null,
                          val deleted: String? = null,
                          val description: String? = null,
                          val durationMillis: String,
                          val episodeId: String,
                          val explicitType: String,
                          val fileSize: String,
                          val playbackPositionMillis: String? = null,
                          val publicationTimestampMillis: String? = null,
                          val seriesId: String,
                          val seriesTitle: String,
                          val title: String)

@JsonSerializable
data class PodcastSeries(val art: List<Image>? = null,
                         val author: String,
                         @JsonDefaultValueString("")
                         val continuationToken: String? = "",
                         val copyright: String? = null,
                         val description: String? = null,
                         val episodes: List<PodcastEpisode>? = null,
                         val explicitType: String,
                         val link: String? = null,
                         val seriesId: String,
                         val title: String,
                         val totalNumEpisodes: Int,
                         val userPreferences: PodcastSeriesUserPreferences? = null)

@JsonSerializable
data class PodcastSeriesUserPreferences(val autoDownload: Boolean? = null,
                                        val notifyOnNewEpisode: Boolean? = null,
                                        val subscribed: Boolean)

@JsonSerializable
data class Situation(val description: String,
                     val id: String,
                     val imageUrl: String? = null,
                     val title: String,
                     val wideImageUrl: String? = null,
                     val stations: List<Station>? = null,
                     val situations: List<Situation>? = null)

@JsonSerializable
data class SearchResult(val score: Number? = null,
                        val type: String,
                        @Json(name = "best_result") val bestResult: Boolean? = null,
                        @Json(name = "navigational_result")
                        val navigationalResult: Boolean? = null,
                        @Json(name = "navigational_confidence")
                        val navigationalConfidence: Number? = null,
                        val artist: Artist? = null,
                        val album: Album? = null,
                        val track: Track? = null,
                        val playlist: Playlist? = null,
                        val series: PodcastSeries? = null,
                        val station: Station? = null,
                        val situation: Situation? = null,
                        @Json(name = "youtube_video") val youtubeVideo: Video? = null)

@JsonSerializable
data class Config(val kind: String,
                  val key: String,
                  val value: String)

@JsonSerializable
data class ConfigList<out T>(val kind: String,
                             val data: Entries<T>)

@JsonSerializable
data class DeviceManagement(val id: String,
                            @JsonDefaultValueString("")
                            val friendlyName: String = "",
                            val type: String,
                            val lastAccessedTimeMs: Int,
                            val smartPhone: Boolean? = null)

@JsonSerializable
data class SharedPlaylistEntry(val shareToken: String,
                               val responseCode: String,
                               val playlistEntry: List<PlaylistEntry>? = null)

@JsonSerializable
data class Entries<out T>(val kind: String? = null,
                          val clusterOrder: List<String>? = null,
                          val entries: List<T>? = null,
                          val suggestedQuery: String? = null)

@JsonSerializable
data class ListPager<out T>(val kind: String,
                            val nextPageToken: String? = null,
                            val data: ListItems<T>? = null)

@JsonSerializable
data class ListItems<out T>(val items: List<T>)

@JsonSerializable
data class ListenNowItems(val kind: String,
                          @Json(name = "listennow_items")
                          val listenNowItems: List<ListenNowItem>)

@JsonSerializable
data class ListListenNowSituations(val distilledContextWrapper: DistilledContextWrapper,
                                   val primaryHeader: String,
                                   val subHeader: String,
                                   val situations: List<Situation>)

@JsonSerializable
data class ListStationTracks(val kind: String,
                             val data: StationTracks)

@JsonSerializable
data class StationTracks(val stations: List<Station>? = null)

@JsonSerializable
data class ListGenres(val kind: String,
                      val genres: List<Genre>? = null)

@JsonSerializable
data class BrowsePodcastHierarchy(val groups: List<PodcastGenre>? = null)

@JsonSerializable
data class BrowsePodcastSeries(val series: List<PodcastSeries>)

@JsonSerializable
data class DistilledContextWrapper(val distilledContextToken: String? = null)

@JsonSerializable
data class BatchMutateCall(@Json(name = "mutate_response")
                           val mutateResponse: List<MutateCall>)

@JsonSerializable
data class MutateCall(val id: String? = null,
                      @Json(name = "client_id") @JsonDefaultValueString("")
                      val clientId: String? = "",
                      @Json(name = "response_code") val responseCode: String)

data class PlayCountData(val songId: String,
                         val playCount: Int,
                         val playTimestamp: Date,
                         val contextId: String? = null)

@JsonSerializable
data class IncrementPlayCount(val responses: List<MutateCall>)

@JsonSerializable
data class Credentials(@Json(name = "_module")
                       @JsonDefaultValueString("oauth2client.client")
                       val module: String = "oauth2client.client",
                       @Json(name = "token_expiry") @CredentialDate
                       val tokenExpiry: Date,
                       @Json(name = "access_token") @JsonDefaultValueString("bogus")
                       val accessToken: String = "bogus",
                       @Json(name = "token_uri") @JsonDefaultValueString("https://accounts.google" +
		                       ".com/o/oauth2/token")
                       val tokenUri: String = "https://accounts.google.com/o/oauth2/token",
                       @JsonDefaultValueBoolean(false)
                       val invalid: Boolean = false,
                       @Json(name = "token_response")
                       val tokenResponse: TokenResponse,
                       @Json(name = "client_id")
                       @JsonDefaultValueString("652850857958.apps.googleusercontent.com")
                       val clientId: String = "652850857958.apps.googleusercontent.com",
                       @Json(name = "id_token")
                       val idToken: String? = null,
                       @Json(name = "client_secret")
                       @JsonDefaultValueString("ji1rklciNp2bfsFJnEH_i6al")
                       val clientSecret: String = "ji1rklciNp2bfsFJnEH_i6al",
                       @Json(name = "revoke_uri")
                       @JsonDefaultValueString("https://accounts.google.com/o/oauth2/revoke")
                       val revokeUri: String = "https://accounts.google.com/o/oauth2/revoke",
                       @Json(name = "_class")
                       @JsonDefaultValueString("OAuth2Credentials")
                       val _class: String = "OAuth2Credentials",
                       @Json(name = "refresh_token")
                       val refreshToken: Any,
                       @Json(name = "user_agent")
                       val userAgent: String? = null)

@JsonSerializable
data class TokenResponse(@Json(name = "access_token")
                         @JsonDefaultValueString("bogus")
                         val accessToken: String = "bogus",
                         @Json(name = "token_type")
                         @JsonDefaultValueString("Bearer")
                         val tokenType: String = "bearer",
                         @Json(name = "expires_in")
                         @JsonDefaultValueInt(3600)
                         val expiresIn: Int = 3600,
                         @Json(name = "refresh_token")
                         val refreshToken: Any)