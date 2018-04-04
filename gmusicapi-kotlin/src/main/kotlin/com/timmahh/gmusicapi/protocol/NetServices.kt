import retrofit2.http.*

const val android_url: String = "https://android.clients.google.com/upsj/"
const val userAgent = "User-agent: Music Manager (1, 0, 55, 7425 HTTPS - Windows)"

data class OAuthInfo(
		val client_id: String = "652850857958.apps.googleusercontent.com",
		val client_secret: String = "ji1rklciNp2bfsFJnEH_i6al",
		val scope: String = "urn:ietf:wg:oauth:2.0:oob",
		val redirect: String = "https://www.googleapis.com/auth/musicmanager")

interface MusicManagerService {
	
	@Headers(userAgent)
	@POST("clientstate")
	fun getClientState()
	
	
	@Headers(userAgent)
	@POST("upauth")
	fun authenticateUploader()
	
	@Headers(userAgent)
	@POST("metadata?version=1")
	fun uploadMetadata(@FieldMap fields: Map<String, String> = mapOf("albumartist" to "album_artist", "bpm" to "beats_per_minute"))
	
	@Headers(userAgent)
	@POST("getjobs?version=1")
	fun getUploadJobs()
	
	@Headers(userAgent)
	@POST("https://uploadsj.clients.google.com/uploadsj/scottyagent")
	fun getUploadSession()
	
	@PUT
	fun uploadFile(@Url sessionUrl: String, @Header("CONTENT-TYPE") contentType: String)
	
	@Headers(userAgent)
	@POST("sample?version=1")
	fun provideSample()
	
	@Headers(userAgent)
	@POST("uploadstate?version=1")
	fun updateUploadState()
	
	@Headers(userAgent)
	@POST("deleteuploadrequested")
	fun cancelUploadJobs()
	
	@Headers(userAgent)
	@POST("https://music.google.com/music/exportids")
	fun listTracks(@Header("X-Device-ID") clientId: String)
	
	@GET("https://music.google.com/music/export?version=2")
	fun getDownloadLink(@Header("X-Device-ID") clientId: String, @Query("songid") songId: String)
	
	@Headers(userAgent)
	@GET
	fun downloadTrack(@Url url: String)
}

const val sj_url: String = "https://mclients.googleapis.com/sj/v2.5/"
const val sj_stream_url: String = "https://mclients.googleapis.com/music/"
const val contentType: String = "Content-Type: application/json"


interface MobileClientService {
	
	@GET("config")
	fun getConfig()
	
	@GET("query")
	fun search(@Query("q") query: String, @Query("max-results") maxResults: Int = 50, @Query("ct") resultTypes: String = "1,2,3,4,6,7,8,9")
	
	@Headers(contentType)
	@POST("trackfeed?alt=json&include-tracks=true")
	fun listTracks(@Query("updated-min") microseconds: Long = - 1)
	
	@GET(sj_stream_url + "mplay")
	fun getStreamUrl(@Header("X-Device-ID") deviceId: String, @QueryMap params: Map<String, String>)
	
	@Headers("X-Device-ID: ")
	@GET(sj_stream_url + "wplay")
	fun getStationTrackStreamUrl(@QueryMap params: Map<String, String>)
	
	@Headers(contentType)
	@POST("plentryfeed?alt=json&include-tracks=true")
	fun listPlaylists(@Query("updated-min") microseconds: Long = - 1)
	
	@Headers(contentType)
	@POST("plentries/shared?alt=json&include-tracks=true")
	fun listSharedPlaylistEntries(@Query("updated-min") microseconds: Long = - 1)
	
	@Headers(contentType)
	@POST("playlistbatch?alt=json")
	fun batchMutatePlaylists()
	
	@Headers(contentType)
	@POST("plentriesbatch?alt=json")
	fun batchMutatePlaylistEntries(@FieldMap add_deleteMap: Map<String, String>)
	
	@GET("devicemanagementinfo?alt=json")
	fun getDeviceManagementInfo()
	
	@DELETE("devicemanagementinfo")
	fun deauthDevice(@Query("delete-id") deviceId: String)
	
	@Headers(contentType)
	@POST("ephemeral/top?alt=json&include-tracks=true")
	fun listPromotedTracks()
	
	@GET("listennow/getlistennowitems?alt=json")
	fun listListenNowItems()
	
	@Headers(contentType)
	@POST("listennow/situations?alt=json")
	fun listListenNowSituations()
	
	@GET("podcast/browsehierarchy?alt=json")
	fun getBrowsePodcastHierarchy()
	
	@GET("podcast/browse?alt=json")
	fun listBrowsePodcastSeries(@Query("id") id: String)
	
	@Headers(contentType)
	@POST("podcastseries/batchmutate?alt=json")
	fun batchMutatePodcastSeries(@FieldMap updates: Map<String, String>)
	
	@Headers(contentType)
	@GET("podcastseries?alt=json&include-tracks=true")
	fun listPodcastSeries(@Header("X-Device-ID") deviceId: String, @Query("updated-min") microseconds: Long = - 1, @Query("start-token") startToken: String, @Query("max-results") maxResults: Int = 50)
	
	@Headers(contentType)
	@GET("podcastepisode?alt=json&include-tracks=true")
	fun listPodcastEpisodes(@Header("X-Device-ID") deviceId: String, @Query("updated-min") microseconds: Long = - 1, @Query("start-token") startToken: String, @Query("max-results") maxResults: Int = 50)
	
	@GET(sj_stream_url + "fplay")
	fun getPodcastEpisodeStreamUrl(@Header("X-Device-ID") deviceId: String, @QueryMap params: Map<String, String>)
	
	@Headers(contentType)
	@POST("radio/station?alt=json&include-tracks=true")
	fun listStations(@Query("updated-min") microseconds: Long = - 1)
	
	@Headers(contentType)
	@POST("radio/stationfeed?alt=json&include-tracks=true")
	fun listStationTracks(@Query("updated-min") microseconds: Long = - 1)
	
	@Headers(contentType)
	@POST("radio/editstation?alt=json")
	fun batchMutateStations(@FieldMap add_deleteMap: Map<String, String>)
	
	@Headers(contentType)
	@POST("trackbatch?alt=json")
	fun batchMutateTracks(@FieldMap add_deleteMap: Map<String, String>)
	
	@Headers(contentType)
	@GET("podcast/fetchseries?alt=json")
	fun getPodcastSeries(@Query("nid") podcastSeriesId: String, @Query("num") numEpisodes: Int)
	
	@Headers(contentType)
	@GET("podcast/fetchepisode?alt=json")
	fun getPodcastEpisode(@Query("nid") podcastEpisodeId: String)
	
	@Headers(contentType)
	@GET("fetchtrack?alt=json")
	fun getStoreTrack(@Query("nid") trackId: String)
	
	@GET("explore/genres?alt=json")
	fun getGenres(@Query("parent-genre") parentGenreId: String)
	
	@GET("fetchartist?alt=json")
	fun getArtist(@Query("nid") artistId: String, @Query("include-albums") includeAlbums: Boolean, @Query("num-top-tracks") numTopTracks: Int, @Query("num-related-artists") numRelatedArtists: Int)
	
	@GET("fetchalbum?alt=json")
	fun getAlbum(@Query("nid") albumId: String, @Query("include-tracks") includeTracks: Boolean)
	
	@Headers(contentType)
	@POST("trackstats?alt=json")
	fun incrementPlayCount()
	
}