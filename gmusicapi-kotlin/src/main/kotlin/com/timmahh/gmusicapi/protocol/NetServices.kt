import com.google.gson.Gson
import com.timmahh.gmusicapi.protocol.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.IOException

/* the base URL for MusicManager */
const val android_url: String = "https://android.clients.google.com/upsj/"

/* default user agent for MusicManager */
const val userAgent = "User-agent: Music Manager (1, 0, 55, 7425 HTTPS - Windows)"

/* authentication info for MusicManager */
data class OAuthInfo(
		val client_id: String = "652850857958.apps.googleusercontent.com",
		val client_secret: String = "ji1rklciNp2bfsFJnEH_i6al",
		val scope: String = "urn:ietf:wg:oauth:2.0:oob",
		val redirect: String = "https://www.googleapis.com/auth/musicmanager")

/**
 * The request interceptor that will add the header with OAuth
 * token to every request made with the wrapper.
 */
class ApiAuthenticator(val mAccessToken: String?, val locale: String = "en_US", val isSubscribed: Boolean = false) : Interceptor {
	
	@Throws(IOException::class)
	override fun intercept(chain: Interceptor.Chain): Response {
		val request = chain.request()
		if (mAccessToken != null) {
			val authRequest = request.newBuilder()
					.url(request.url().newBuilder()
							.addQueryParameter("hl", locale)
							.addQueryParameter("dv", "0")
							.addQueryParameter("tier", if (isSubscribed) "aa" else "fr")
							.build())
					.addHeader("Authorization", "GoogleLogin auth=" + mAccessToken)
					.build()
			
			return chain.proceed(authRequest)
		}
		return chain.proceed(request)
	}
}

/**
 * Creates a Retrofit instance with necessary JSON conversion adapters.
 */
fun createRetrofit(url: String,
                   httpClient: OkHttpClient = createAuthenticatedHttpClient()): Retrofit =
		Retrofit.Builder()
				.client(httpClient)
				.baseUrl(url)
				.addConverterFactory(GsonConverterFactory.create(Gson()))
				.build()

/**
 * Creates the OkHttpClient with an authenticated interceptor.
 */
fun createAuthenticatedHttpClient(authToken: String? = null): OkHttpClient =
		OkHttpClient.Builder()
				.addInterceptor(ApiAuthenticator(authToken))
				.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
				.build()

/**
 * Creates a Retrofit service instance for MusicManagerService
 */
fun createMusicManagerService(httpClient: OkHttpClient = createAuthenticatedHttpClient()): MusicManagerService =
		createRetrofit(android_url, httpClient).create(MusicManagerService::class.java)

/**
 * Creates a Retrofit service instance for MobileClientService
 */
fun createMobileClientService(httpClient: OkHttpClient = createAuthenticatedHttpClient()): MobileClientService =
		createRetrofit(sj_url, httpClient).create(MobileClientService::class.java)

/**
 * A Retrofit service implementation of Simon Weber's MusicManager protocol
 */
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

/* the base URL for MobileClient */
const val sj_url: String = "https://mclients.googleapis.com/sj/v2.5/"

/* the URL for streaming */
const val sj_stream_url: String = "https://mclients.googleapis.com/music/"

/* default Content-Type for MobileClient */
const val contentType: String = "Content-Type: application/json"

/**
 * A Retrofit service implementation of Simon Weber's MobilClient protocol
 */
interface MobileClientService {
	
	@GET("config")
	fun getConfig(): Call<ConfigList<Config>>
	
	@Headers(contentType)
	@POST("trackstats?alt=json")
	fun incrementPlayCount(@Body trackData: Pair<String, List<Map<String, Any>>>): Call<IncrementPlayCount>
	
	@GET("query")
	fun search(@Query("q") query: String, @Query("max-results") maxResults: Int = 1000, @Query("ct") resultTypes: String = "1,2,3,4,6,7,8,9"): Call<Entries<SearchResult>>
	
	@Headers(contentType)
	@POST("trackfeed?alt=json&include-tracks=true")
	fun listTracks(@Query("updated-min") microseconds: Long = - 1,
	               @Body data: Map<String, String>): Call<ListPager<Track>>
	
	@Headers(contentType)
	@POST("playlistfeed?alt=json&include-tracks=true")
	fun listPlaylists(@Query("updated-min") microseconds: Long = - 1,
	                  @Body data: Map<String, String>): Call<ListPager<Playlist>>
	
	@Headers(contentType)
	@POST("playlistfeed?alt=json&include-tracks=true")
	fun listPlaylistEntries(@Query("updated-min") microseconds: Long = - 1,
	                        @Body data: Map<String, String>): Call<ListPager<PlaylistEntry>>
	
	@Headers(contentType)
	@POST("plentries/shared?alt=json&include-tracks=true")
	fun listSharedPlaylistEntries(@Query("updated-min") microseconds: Long = - 1,
	                              @Body entries: List<Map<String, String>>): Call<Entries<SharedPlaylistEntry>>
	
	@Headers(contentType)
	@POST("ephemeral/top?alt=json&include-tracks=true")
	fun listPromotedTracks(@Query("updated-min") microseconds: Long = - 1,
	                       @Body data: Map<String, String>): Call<ListPager<Track>>
	
	@GET("listennow/getlistennowitems?alt=json")
	fun listListenNowItems(): Call<ListenNowItems>
	
	@Headers(contentType)
	@POST("listennow/situations?alt=json")
	fun listListenNowSituations(@Body requestSignals: Pair<String, Pair<String, Int>>):
			Call<ListListenNowSituations>
	
	@GET("podcast/browse?alt=json")
	fun listBrowsePodcastSeries(@Query("id") id: String): Call<BrowsePodcastSeries>
	
	@Headers(contentType)
	@GET("podcastseries?alt=json&include-tracks=true")
	fun listPodcastSeries(@Header("X-Device-ID") deviceId: String, @Query("updated-min") microseconds: Long = - 1, @Query("start-token") startToken: String, @Query("max-results") maxResults: Int = 1000): Call<ListPager<PodcastSeries>>
	
	@Headers(contentType)
	@GET("podcastepisode?alt=json&include-tracks=true")
	fun listPodcastEpisodes(@Header("X-Device-ID") deviceId: String, @Query("updated-min") microseconds: Long = - 1, @Query("start-token") startToken: String, @Query("max-results") maxResults: Int = 1000): Call<ListPager<PodcastEpisode>>
	
	@Headers(contentType)
	@POST("radio/station?alt=json&include-tracks=true")
	fun listStations(@Query("updated-min") microseconds: Long = - 1,
	                 @Body data: Map<String, String>): Call<ListPager<Station>>
	
	@Headers(contentType)
	@POST("radio/stationfeed?alt=json&include-tracks=true")
	fun listStationTracks(@Query("updated-min") microseconds: Long = - 1,
	                      @Body data: Map<String, Any>): Call<ListStationTracks>
	
	@Headers(contentType)
	@POST("playlistbatch?alt=json")
	fun batchMutatePlaylists(@Body deletePlaylists: List<Pair<String, String>>? = null,
	                         @Body updatePlaylists: List<Pair<String, Map<String, String?>>>? = null,
	                         @Body addPlaylists: List<Pair<String, Map<String, String?>>>? = null): Call<BatchMutateCall>
	
	@Headers(contentType)
	@POST("plentriesbatch?alt=json")
	fun batchMutatePlaylistEntries(
			@Body deleteEntries: List<Pair<String, String>>? = null,
			@Body update: Pair<String, Map<String, String>>? = null,
			@Body addEntries: List<Pair<String, Map<String, String>>>? = null): Call<BatchMutateCall>
	
	@Headers(contentType)
	@POST("podcastseries/batchmutate?alt=json")
	fun batchMutatePodcastSeries(@Body updates: List<Pair<String, Map<String, Any>>>): Call<BatchMutateCall>
	
	@Headers(contentType)
	@POST("radio/editstation?alt=json")
	fun batchMutateStations(@Body deleteStations: List<Map<String, String>>? = null,
	                        @Body addStations: Map<String, Any>? = null): Call<BatchMutateCall>
	
	@Headers(contentType)
	@POST("trackbatch?alt=json")
	fun batchMutateTracks(@Body deleteTracks: List<Pair<String, String>>? = null,
	                      @Body updateTracks: List<Pair<String, Track>>? = null,
	                      @Body addTracks: Pair<String, Map<String, Any?>>? = null): Call<BatchMutateCall>
	
	@GET("devicemanagementinfo?alt=json")
	fun getDeviceManagementInfo(): Call<ListPager<DeviceManagement>>
	
	@DELETE("devicemanagementinfo")
	fun deauthDevice(@Query("delete-id") deviceId: String): Call<Boolean>
	
	@GET("podcast/browsehierarchy?alt=json")
	fun getBrowsePodcastHierarchy(): Call<BrowsePodcastHierarchy>
	
	@Headers(contentType)
	@GET("podcast/fetchseries?alt=json")
	fun getPodcastSeries(@Query("nid") podcastSeriesId: String, @Query("num") numEpisodes: Int): Call<PodcastSeries>
	
	@Headers(contentType)
	@GET("podcast/fetchepisode?alt=json")
	fun getPodcastEpisode(@Query("nid") podcastEpisodeId: String): Call<PodcastEpisode>
	
	@Headers(contentType)
	@GET("fetchtrack?alt=json")
	fun getStoreTrack(@Query("nid") trackId: String): Call<Track>
	
	@GET("explore/genres?alt=json")
	fun getGenres(@Query("parent-genre") parentGenreId: String): Call<ListGenres>
	
	@GET("fetchartist?alt=json")
	fun getArtist(@Query("nid") artistId: String, @Query("include-albums") includeAlbums: Boolean, @Query("num-top-tracks") numTopTracks: Int, @Query("num-related-artists") numRelatedArtists: Int): Call<Artist>
	
	@GET("fetchalbum?alt=json")
	fun getAlbum(@Query("nid") albumId: String, @Query("include-tracks") includeTracks: Boolean): Call<Album>
	
	@GET(sj_stream_url + "mplay")
	fun getStreamUrl(@Header("X-Device-ID") deviceId: String, @QueryMap params: Map<String, String>): Call<String>
	
	@GET(sj_stream_url + "fplay")
	fun getPodcastEpisodeStreamUrl(@Header("X-Device-ID") deviceId: String, @QueryMap params: Map<String, String>): Call<String>
	
	@Headers("X-Device-ID: ")
	@GET(sj_stream_url + "wplay")
	fun getStationTrackStreamUrl(@QueryMap params: Map<String, String>): Call<String>
	
}