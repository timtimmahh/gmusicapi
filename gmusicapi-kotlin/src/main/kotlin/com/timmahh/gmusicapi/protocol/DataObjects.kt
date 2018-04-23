package com.timmahh.gmusicapi.protocol

import com.google.gson.annotations.SerializedName
import java.util.*

//@JsonSerializable
data class ImageColorStyles(
		val primary: ImageColors,
		val scrim: ImageColors,
		val accent: ImageColors)

//@JsonSerializable
data class ImageColors(val red: Int, val green: Int, val blue: Int)

//@JsonSerializable
data class Image(val kind: String,
                 val url: String,
                 val aspectRatio: String? = null,
                 val autogen: Boolean? = null,
                 val colorStyles: ImageColorStyles? = null)

//@JsonSerializable
data class ImageUrl(val url: String)

//@JsonSerializable
data class Video(val kind: String, val id: String, val title: String? = null, val thumbnails: List<Thumbnail>)

//@JsonSerializable
data class Thumbnail(val url: String, val width: Int, val height: Int)

//@JsonSerializable
data class Track(val kind: String,
                 val title: String,
                 val artist: String,
                 val album: String,
		//@JsonDefaultValueString("")
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
		//@JsonDefaultValueString("")
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
		//@JsonDefaultValueString("")
                 val comment: String? = "",
                 val beatsPerMinute: Int? = null,
                 val recentTimestamp: String? = null,
                 val clientId: String? = null,
                 val id: String? = null)

//@JsonSerializable
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
		//@JsonDefaultValueString("")
                    val description: String? = "",
                    val explicitType: String? = null,
                    val contentType: String? = null)

//@JsonSerializable
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

//@JsonSerializable
data class Attribution(val kind: String,
                       @SerializedName("license_url")
                       val licenseUrl: String? = null,
                       @SerializedName("license_title")
                       val licenseTitle: String? = null,
                       @SerializedName("source_title") //@JsonDefaultValueString("")
                       val sourceTitle: String? = "",
                       @SerializedName("source_url") //@JsonDefaultValueString("")
                       val sourceUrl: String? = "")

//@JsonSerializable
data class Album(val kind: String,
                 val name: String,
                 val albumArtist: String,
                 val albumArtRef: String? = null,
                 val albumId: String,
		//@JsonDefaultValueString("")
		         val artist: String = "",
		         val artistId: List<String>,
		         val year: Int? = null,
		         val tracks: List<Track>? = null,
		         val description: String? = null,
		         @SerializedName("description_attribution")
                 val descriptionAttribution: Attribution? = null,
		         val explicitType: String? = null,
		         val contentType: String? = null)

//@JsonSerializable
data class Artist(val kind: String,
                  val name: String,
                  val artistArtRef: String? = null,
                  val artistArtRefs: List<Image>? = null,
                  val artistBio: String? = null,
		//@JsonDefaultValueString("")
		          val artistId: String? = "",
		          val albums: List<Album>? = null,
		          val topTracks: List<Track>? = null,
		          @SerializedName("total_albums")
                  val totalAlbums: Int? = null,
		          @SerializedName("artist_bio_attribution")
                  val artistBioAttribution: Attribution? = null,
		          @SerializedName("related_artists")
                  val relatedArtists: List<Artist>? = null)

//@JsonSerializable
data class Genre(val kind: String,
                 val id: String,
                 val name: String,
                 val children: List<String>? = null,
                 val parentId: String? = null,
                 val images: List<ImageUrl>? = null)

//@JsonSerializable
data class StationMetadataSeed(val kind: String,
                               val artist: Artist? = null,
                               val genre: Genre? = null)

//@JsonSerializable
data class StationSeed(val kind: String,
                       val seedType: String,
                       val albumId: String? = null,
                       val artistId: String? = null,
                       val genreId: String? = null,
                       val trackId: String? = null,
                       val trackLockerId: String? = null,
                       val curatedStationId: String? = null,
                       val metadataSeed: StationMetadataSeed? = null)

//@JsonSerializable
data class StationTrack(val track: Track,
                        @SerializedName("wentryid") val wentryId: String? = null)

//@JsonSerializable
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

//@JsonSerializable
data class AdTarget(val keyword: List<String>)

//@JsonSerializable
data class ListenNowAlbum(
		@SerializedName("artist_metajam_id") val artistMetajamId: String,
		@SerializedName("artist_name") val artistName: String,
		@SerializedName("artist_profile_image") val artistProfileImage: ImageUrl,
		//@JsonDefaultValueString("")
		val description: String = "",
		@SerializedName("description_attribution")
		val descriptionAttribution: Attribution? = null,
		val explicitType: String? = null,
		val id: ListenNowAlbumId,
		val title: String)

//@JsonSerializable
data class ListenNowAlbumId(val metajamCompactKey: String,
                            val artist: String,
                            val title: String)

//@JsonSerializable
data class ListenNowStation(
		@SerializedName("highlight_color") val highlightColor: String? = null,
		val id: ListenNowStationIdSeeds,
		@SerializedName("profile_image") val profileImage: ImageUrl? = null,
		val title: String)

//@JsonSerializable
data class ListenNowStationIdSeeds(val seeds: List<StationSeed>)

//@JsonSerializable
data class ListenNowItem(val kind: String,
                         val compositeArtRefs: List<Image>? = null,
                         val images: List<Image>? = null,
                         @SerializedName("suggestion_reason") val suggestionReason: String,
                         @SerializedName("suggestion_text") val suggestionText: String,
                         val type: String,
                         val album: ListenNowAlbum? = null,
                         @SerializedName("radio_station")
                         val radioStation: ListenNowStation? = null)

//@JsonSerializable
data class PodcastGenre(val id: String,
                        val displayName: String,
                        val subgroups: List<PodcastGenre>? = null)

//@JsonSerializable
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

//@JsonSerializable
data class PodcastSeries(val art: List<Image>? = null,
                         val author: String,
		//@JsonDefaultValueString("")
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

//@JsonSerializable
data class PodcastSeriesUserPreferences(val autoDownload: Boolean? = null,
                                        val notifyOnNewEpisode: Boolean? = null,
                                        val subscribed: Boolean)

//@JsonSerializable
data class Situation(val description: String,
                     val id: String,
                     val imageUrl: String? = null,
                     val title: String,
                     val wideImageUrl: String? = null,
                     val stations: List<Station>? = null,
                     val situations: List<Situation>? = null)

//@JsonSerializable
data class SearchResult(val score: Number? = null,
                        val type: String,
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

//@JsonSerializable
data class Config(val kind: String,
                  val key: String,
                  val value: String)

//@JsonSerializable
data class ConfigList<out T>(val kind: String,
                             val data: Entries<T>)

//@JsonSerializable
data class DeviceManagement(val id: String,
		//@JsonDefaultValueString("")
                            val friendlyName: String = "",
                            val type: String,
                            val lastAccessedTimeMs: Int,
                            val smartPhone: Boolean? = null)

//@JsonSerializable
data class SharedPlaylistEntry(val shareToken: String,
                               val responseCode: String,
                               val playlistEntry: List<PlaylistEntry>? = null)

//@JsonSerializable
data class Entries<out T>(val kind: String? = null,
                          val clusterOrder: List<String>? = null,
                          val entries: List<T>? = null,
                          val suggestedQuery: String? = null)

//@JsonSerializable
data class ListPager<out T>(val kind: String,
                            val nextPageToken: String? = null,
                            val data: ListItems<T>? = null)

//@JsonSerializable
data class ListItems<out T>(val items: List<T>)

//@JsonSerializable
data class ListenNowItems(val kind: String,
                          @SerializedName("listennow_items")
                          val listenNowItems: List<ListenNowItem>)

//@JsonSerializable
data class ListListenNowSituations(val distilledContextWrapper: DistilledContextWrapper,
                                   val primaryHeader: String,
                                   val subHeader: String,
                                   val situations: List<Situation>)

//@JsonSerializable
data class ListStationTracks(val kind: String,
                             val data: StationTracks)

//@JsonSerializable
data class StationTracks(val stations: List<Station>? = null)

//@JsonSerializable
data class ListGenres(val kind: String,
                      val genres: List<Genre>? = null)

//@JsonSerializable
data class BrowsePodcastHierarchy(val groups: List<PodcastGenre>? = null)

//@JsonSerializable
data class BrowsePodcastSeries(val series: List<PodcastSeries>)

//@JsonSerializable
data class DistilledContextWrapper(val distilledContextToken: String? = null)

//@JsonSerializable
data class BatchMutateCall(@SerializedName("mutate_response")
                           val mutateResponse: List<MutateCall>)

//@JsonSerializable
data class MutateCall(val id: String? = null,
                      @SerializedName("client_id") //@JsonDefaultValueString("")
                      val clientId: String? = "",
                      @SerializedName("response_code") val responseCode: String)

data class PlayCountData(val songId: String,
                         val playCount: Int,
                         val playTimestamp: Date,
                         val contextId: String? = null)

//@JsonSerializable
data class IncrementPlayCount(val responses: List<MutateCall>)

//@JsonSerializable
data class Credentials(@SerializedName("_module")
                       //@JsonDefaultValueString("oauth2client.client")
                       val module: String = "oauth2client.client",
                       @SerializedName("token_expiry")
                       val tokenExpiry: Date,
                       @SerializedName("access_token") //@JsonDefaultValueString("bogus")
                       val accessToken: String = "bogus",
                       @SerializedName("token_uri") //@JsonDefaultValueString("https://accounts.google.com/o/oauth2/token")
                       val tokenUri: String = "https://accounts.google.com/o/oauth2/token",
		//@JsonDefaultValueBoolean(false)
		               val invalid: Boolean = false,
		               @SerializedName("token_response")
                       val tokenResponse: TokenResponse,
		               @SerializedName("client_id")
                       //@JsonDefaultValueString("652850857958.apps.googleusercontent.com")
                       val clientId: String = "652850857958.apps.googleusercontent.com",
		               @SerializedName("id_token")
                       val idToken: String? = null,
		               @SerializedName("client_secret")
                       //@JsonDefaultValueString("ji1rklciNp2bfsFJnEH_i6al")
                       val clientSecret: String = "ji1rklciNp2bfsFJnEH_i6al",
		               @SerializedName("revoke_uri")
                       //@JsonDefaultValueString("https://accounts.google.com/o/oauth2/revoke")
                       val revokeUri: String = "https://accounts.google.com/o/oauth2/revoke",
		               @SerializedName("_class")
                       //@JsonDefaultValueString("OAuth2Credentials")
                       val _class: String = "OAuth2Credentials",
		               @SerializedName("refresh_token")
                       val refreshToken: Any,
		               @SerializedName("user_agent")
                       val userAgent: String? = null)

//@JsonSerializable
data class TokenResponse(@SerializedName("access_token")
                         //@JsonDefaultValueString("bogus")
                         val accessToken: String = "bogus",
                         @SerializedName("token_type")
                         //@JsonDefaultValueString("Bearer")
                         val tokenType: String = "bearer",
                         @SerializedName("expires_in")
                         //@JsonDefaultValueInt(3600)
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