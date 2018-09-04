package com.timmahh.gmusicapi.protocol

import com.google.gson.annotations.SerializedName
import com.timmahh.gmusicapi.clients.SearchType
import java.util.*


data class ImageColorStyles(
		val primary: ImageColors,
		val scrim: ImageColors,
		val accent: ImageColors)


data class ImageColors(val red: Int, val green: Int, val blue: Int)


data class Image(val kind: String,
                 val url: String,
                 val aspectRatio: String? = null,
                 val autogen: Boolean? = null,
                 val colorStyles: ImageColorStyles? = null)


data class ImageUrl(val url: String)


data class Video(val kind: String, val id: String, val title: String? = null, val thumbnails: List<Thumbnail>)


data class Thumbnail(val url: String, val width: Int, val height: Int)


data class Track(val kind: String,
                 val title: String,
                 val artist: String,
                 val album: String,
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
                 val composer: String = "",
                 val playCount: Int? = null,
                 val year: Int? = null,
                 var rating: String? = null,
                 val genre: String? = null,
                 val trackAvailableForSubscription: Boolean? = null,
                 val lastRatingChangeTimestamp: String? = null,
                 val primaryVideo: Video? = null,
                 val lastModifiedTimestamp: String? = null,
                 val explicitType: String? = null,
                 val contentType: String? = null,
                 val deleted: String? = null,
                 val creationTimestamp: String? = null,
                 val comment: String? = "",
                 val beatsPerMinute: Int? = null,
                 val recentTimestamp: String? = null,
                 val clientId: String? = null,
                 val id: String? = null)


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
                    val description: String? = "",
                    val explicitType: String? = null,
                    val contentType: String? = null,
                    var tracks: List<PlaylistEntry>? = null)


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


data class Attribution(val kind: String,
                       @SerializedName("license_url")
                       val licenseUrl: String? = null,
                       @SerializedName("license_title")
                       val licenseTitle: String? = null,
                       @SerializedName("source_title") val sourceTitle: String? = "",
                       @SerializedName("source_url") val sourceUrl: String? = "")


data class Album(val kind: String,
                 val name: String,
                 val albumArtist: String,
                 val albumArtRef: String? = null,
                 val albumId: String,
                 val artist: String = "",
                 val artistId: List<String>,
                 val year: Int? = null,
                 val tracks: List<Track>? = null,
                 val description: String? = null,
                 @SerializedName("description_attribution")
                 val descriptionAttribution: Attribution? = null,
                 val explicitType: String? = null,
                 val contentType: String? = null)


data class Artist(val kind: String,
                  val name: String,
                  val artistArtRef: String? = null,
                  val artistArtRefs: List<Image>? = null,
                  val artistBio: String? = null,
                  val artistId: String? = "",
                  val albums: List<Album>? = null,
                  val topTracks: List<Track>? = null,
                  @SerializedName("total_albums")
                  val totalAlbums: Int? = null,
                  @SerializedName("artist_bio_attribution")
                  val artistBioAttribution: Attribution? = null,
                  @SerializedName("related_artists")
                  val relatedArtists: List<Artist>? = null)


data class Genre(val kind: String,
                 val id: String,
                 val name: String,
                 val children: List<String>? = null,
                 val parentId: String? = null,
                 val images: List<ImageUrl>? = null)


data class StationMetadataSeed(val kind: String,
                               val artist: Artist? = null,
                               val genre: Genre? = null)


data class StationSeed(val kind: String,
                       val seedType: String,
                       val albumId: String? = null,
                       val artistId: String? = null,
                       val genreId: String? = null,
                       val trackId: String? = null,
                       val trackLockerId: String? = null,
                       val curatedStationId: String? = null,
                       val metadataSeed: StationMetadataSeed? = null)


data class StationTrack(val track: Track,
                        @SerializedName("wentryid") val wentryId: String? = null)


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


data class AdTarget(val keyword: List<String>)


data class ListenNowAlbum(
		@SerializedName("artist_metajam_id") val artistMetajamId: String,
		@SerializedName("artist_name") val artistName: String,
		@SerializedName("artist_profile_image") val artistProfileImage: ImageUrl,
		val description: String = "",
		@SerializedName("description_attribution")
		val descriptionAttribution: Attribution? = null,
		val explicitType: String? = null,
		val id: ListenNowAlbumId,
		val title: String)


data class ListenNowAlbumId(val metajamCompactKey: String,
                            val artist: String,
                            val title: String)


data class ListenNowStation(
		@SerializedName("highlight_color") val highlightColor: String? = null,
		val id: ListenNowStationIdSeeds,
		@SerializedName("profile_image") val profileImage: ImageUrl? = null,
		val title: String)


data class ListenNowStationIdSeeds(val seeds: List<StationSeed>)


data class ListenNowItem(val kind: String,
                         val compositeArtRefs: List<Image>? = null,
                         val images: List<Image>? = null,
                         @SerializedName("suggestion_reason") val suggestionReason: String,
                         @SerializedName("suggestion_text") val suggestionText: String,
                         val type: String,
                         val album: ListenNowAlbum? = null,
                         @SerializedName("radio_station")
                         val radioStation: ListenNowStation? = null)


data class PodcastGenre(val id: String,
                        val displayName: String,
                        val subgroups: List<PodcastGenre>? = null)


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


data class PodcastSeries(val art: List<Image>? = null,
                         val author: String,
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


data class PodcastSeriesUserPreferences(val autoDownload: Boolean? = null,
                                        val notifyOnNewEpisode: Boolean? = null,
                                        val subscribed: Boolean)


data class Situation(val description: String,
                     val id: String,
                     val imageUrl: String? = null,
                     val title: String,
                     val wideImageUrl: String? = null,
                     val stations: List<Station>? = null,
                     val situations: List<Situation>? = null)


data class SearchResult(val score: Number? = null,
                        val type: SearchType = SearchType.None,
                        @SerializedName("best_result") val bestResult: Boolean? = null,
                        @SerializedName("navigational_result")
                        val navigationalResult: Boolean? = null,
                        @SerializedName("navigational_confidence")
                        val navigationalConfidence: Number? = null,
                        val artist: Artist? = null,
                        val album: Album? = null,
                        val track: Track? = null,
                        val playlist: Playlist? = null,
                        val series: PodcastSeries? = null,
                        val station: Station? = null,
                        val situation: Situation? = null,
                        @SerializedName("youtube_video") val youtubeVideo: Video? = null)


data class Config(val kind: String,
                  val key: String,
                  val value: String)


data class ConfigList<out T>(val kind: String,
                             val data: Entries<T>)


data class DeviceManagement(val id: String,
                            val friendlyName: String = "",
                            val type: String,
                            val lastAccessedTimeMs: Int,
                            val smartPhone: Boolean? = null)


data class SharedPlaylistEntry(val shareToken: String,
                               val responseCode: String,
                               val playlistEntry: List<PlaylistEntry>? = null)


data class Entries<out T>(val kind: String? = null,
                          val clusterOrder: List<String>? = null,
                          val entries: List<T>? = null,
                          val suggestedQuery: String? = null)


data class ListPager<out T>(val kind: String,
                            val nextPageToken: String? = null,
                            val items: List<T>? = null)


//data class ListItems<out T>(val items: List<T>)


data class ListenNowItems(val kind: String,
                          @SerializedName("listennow_items")
                          val listenNowItems: List<ListenNowItem>)


data class ListListenNowSituations(val distilledContextWrapper: DistilledContextWrapper,
                                   val primaryHeader: String,
                                   val subHeader: String,
                                   val situations: List<Situation>)


data class ListStationTracks(val kind: String,
                             val data: StationTracks)


data class StationTracks(val stations: List<Station>? = null)


data class ListGenres(val kind: String,
                      val genres: List<Genre>? = null)


data class BrowsePodcastHierarchy(val groups: List<PodcastGenre>? = null)


data class BrowsePodcastSeries(val series: List<PodcastSeries>)


data class DistilledContextWrapper(val distilledContextToken: String? = null)


data class BatchMutateCall(@SerializedName("mutate_response")
                           val mutateResponse: List<MutateCall>)


data class MutateCall(val id: String? = null,
                      @SerializedName("client_id") val clientId: String? = "",
                      @SerializedName("response_code") val responseCode: String)

data class PlayCountData(val songId: String,
                         val playCount: Int,
                         val playTimestamp: Date,
                         val contextId: String? = null)


data class IncrementPlayCount(val responses: List<MutateCall>)


data class Credentials(@SerializedName("_module")
                       val module: String = "oauth2client.client",
                       @SerializedName("token_expiry")
                       val tokenExpiry: Date,
                       @SerializedName("access_token") val accessToken: String = "bogus",
                       @SerializedName("token_uri") val tokenUri: String = "https://accounts.google.com/o/oauth2/token",
                       val invalid: Boolean = false,
                       @SerializedName("token_response")
                       val tokenResponse: TokenResponse,
                       @SerializedName("client_id")
                       val clientId: String = "652850857958.apps.googleusercontent.com",
                       @SerializedName("id_token")
                       val idToken: String? = null,
                       @SerializedName("client_secret")
                       val clientSecret: String = "ji1rklciNp2bfsFJnEH_i6al",
                       @SerializedName("revoke_uri")
                       val revokeUri: String = "https://accounts.google.com/o/oauth2/revoke",
                       @SerializedName("_class")
                       val _class: String = "OAuth2Credentials",
                       @SerializedName("refresh_token")
                       val refreshToken: Any,
                       @SerializedName("user_agent")
                       val userAgent: String? = null)


data class TokenResponse(@SerializedName("access_token")
                         val accessToken: String = "bogus",
                         @SerializedName("token_type")
                         val tokenType: String = "bearer",
                         @SerializedName("expires_in")
                         val expiresIn: Int = 3600,
                         @SerializedName("refresh_token")
                         val refreshToken: String)

data class MasterLogin(@SerializedName("Auth") val auth: String,
                       @SerializedName("Email") val email: String,
                       @SerializedName("GooglePlusUpgrade") val googlePlusUpgrade: String = "1",
                       @SerializedName("LSID") val lsid: String,
                       @SerializedName("PicasaUser") val picasaUser: String = "",
                       @SerializedName("RopRevision") val ropRevision: String = "1",
                       @SerializedName("RopText") val ropText: String = "",
                       @SerializedName("SID") val sid: String,
                       @SerializedName("Token") val token: String,
                       @SerializedName("firstName") val firstName: String,
                       @SerializedName("lastName") val lastName: String,
                       @SerializedName("services") val services: String)

data class OAuthLogin(@SerializedName("Auth") val auth: String,
                      @SerializedName("LSID") val lsid: String,
                      @SerializedName("SID") val sid: String,
                      @SerializedName("issueAdvice") val issueAdvice: String = "auto",
                      @SerializedName("services") val services: String,
                      @SerializedName("firstName") val firstName: String,
                      @SerializedName("lastName") val lastName: String)