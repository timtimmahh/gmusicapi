package com.timmahh.gmusicapi.clients

import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import com.timmahh.gmusicapi.protocol.*
import com.timmahh.gpsoauth.Gpsoauth
import createAuthHttpClient
import createMobileClientService
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import java.security.InvalidParameterException
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.xor

/**
 * Kotlin implementation of Simon Weber's MobileClient client
 */
@Suppress("unused")
class MobileClient {
	
	companion object {
		
		/* the http client used to make Retrofit calls */
		@JvmField
		var httpClient: OkHttpClient = createAuthHttpClient()
		
		/* the MobileClient Retrofit service to make the calls */
		@JvmField
		var mobileClientService = createMobileClientService(httpClient = httpClient)
		
		/* holds the login master token and auth token */
		private lateinit var masterToken: String
		private lateinit var authToken: String
		private var isAuthenticated: Boolean = false
		private lateinit var locale: Locale
		
		/**
		 * Saves auth token and master token to shared preferences file.
		 */
		fun saveCredentialsToFile(preferences: SharedPreferences) =
				preferences.edit()
						.putString("Master", masterToken)
						.putString("Token", authToken)
						.apply()
		
		/**
		 * Reads auth token and master token from shared preferences file.
		 */
		fun readCredentialsFromFile(preferences: SharedPreferences, locale: Locale): Boolean =
				if (preferences.contains("Master") && preferences.contains("Token")) {
					masterToken = preferences.getString("Master", "")
					authToken = preferences.getString("Token", "")
					isAuthenticated = masterToken.isNotEmpty() && authToken.isNotEmpty()
					MobileClient.locale = locale
					httpClient = createAuthHttpClient(authToken)
					mobileClientService = createMobileClientService(httpClient)
					isAuthenticated
				} else false
		
	}
	
	/**
	 * Helper method to determine whether to perform a synchronous or asynchronous method [call].
	 * Async calls return the result through [callback].
	 */
	private fun <R> executeOrEnqueue(call: Call<R>, callback: GpmCallback<R>?): Response<R>? =
			if (callback == null) call.execute()
			else {
				call.enqueue(callback); null
			}
	
	/**
	 * Converts login results into JSON format.
	 */
	private fun convertResponseStringToJson(response: String): String =
			"""{
				|   ${response.split('\n').joinToString(""",
				|   """.trimMargin()) { "\"" + it.replaceFirst("=", "\": \"") + "\"" }}
			  |}""".trimMargin()
	
	/**
	 * Helper function to load credentials from storage, or send a login request if
	 * they don't exist.
	 */
	fun loginWithSavedCredsOrGet(sharedPreferences: SharedPreferences, email: String, password: String, androidId: String, locale: Locale = Locale.US) =
			readCredentialsFromFile(sharedPreferences, locale)
					|| login(email, password, androidId, locale)
					.also { if (it) saveCredentialsToFile(sharedPreferences) }
	
	/**
	 * Performs a master login flow for Google accounts.
	 * [email] the email of the Google account.
	 * [password] the password of the Google account.
	 * [androidId] the current device's Android ID.
	 * [locale] the current device's locale.
	 */
	private fun login(email: String, password: String, androidId: String, locale: Locale = Locale.US): Boolean {
		if (androidId.isEmpty())
			throw InvalidParameterException("androidId cannot be empty.")
		
		
		MobileClient.masterToken = Gpsoauth(httpClient).performMasterLoginForToken(email, password, androidId)
		
		if (MobileClient.masterToken.isEmpty())
			return false
		
		val oauthLogin = Gson().fromJson<OAuthLogin>(
				convertResponseStringToJson(
						Gpsoauth(httpClient).performOAuth(
								email,
								MobileClient.masterToken,
								androidId,
								"sj",
								"com.google.android.music",
								"38918a453d07199354f8b19af05ec6562ced5788"
						                                 ).body()?.string() ?: ""),
				OAuthLogin::class.java)
		
		if (oauthLogin?.auth == null || oauthLogin.auth.isEmpty())
			return false
		
		MobileClient.authToken = oauthLogin.auth
		MobileClient.isAuthenticated = true
		MobileClient.locale = locale
		
		Log.d("OnLogin", "Auth: ${MobileClient.authToken}")
		
		MobileClient.httpClient = createAuthHttpClient(MobileClient.authToken)
		MobileClient.mobileClientService = createMobileClientService(MobileClient.httpClient)
		
		return true
	}
	
	/**
	 * Gets the users current GPM configuration. [callback] is optional for async calls.
	 */
	fun getConfig(callback: GpmCallback<ConfigList<Config>>? = null): Response<ConfigList<Config>>? = executeOrEnqueue(mobileClientService.getConfig(), callback)
	
	/**
	 * Increments the play count for the specified songs.
	 * [callback] is optional used for async calls.
	 */
	fun incrementPlayCount(songData: List<PlayCountData>, callback: GpmCallback<IncrementPlayCount>? = null): Response<IncrementPlayCount>? =
			executeOrEnqueue(mobileClientService.incrementPlayCount(
					trackData = "track_stats" to songData.map { (songId, playCount, playTimestamp, contextId) ->
						mapOf(
								"id" to songId,
								"incremental_plays" to playCount,
								"last_play_time_millis" to "${playTimestamp.time}",
								"type" to if (songId.startsWith("T")) 2 else 1,
								"track_events" to Array(playCount) { _ ->
									val event = mutableMapOf(
											"context_type" to 1,
											"event_timestamp_micros" to "${(playTimestamp.time * 1000)}",
											"event_type" to 2)
									if (contextId != null)
										event["context_id"] = contextId
									event
								})
					}), callback)
	
	/**
	 * Searches GPM based on the specified [query] and returning [maxResults] results which defaults to 1000 items.
	 * [resultTypes] defaults to all possible type where:
	 * 1 = Song, 2 = Artist, 3 = Album, 4 = Playlist, 6 = Station, 7 = Situation, 8 = Video, 9 = Podcast Series.
	 * [callback] is optional used for async calls.
	 */
	fun search(query: String,
	           maxResults: Int = 1000,
	           resultTypes: List<SearchType> = allSearchTypes,
	           callback: GpmCallback<Entries<SearchResult>>? = null): Response<Entries<SearchResult>>? =
			executeOrEnqueue(mobileClientService.search(query = query, maxResults = maxResults, resultTypes = resultTypes.joinToString(",") { "${it.type}" }), callback)
	
	/**
	 * Helper method to create request body data for list calls.
	 * [startToken] is the nextPageToken from a previous response.
	 * [maxResults] is a positive integer; if not provided, server defaults to 1000.
	 */
	private fun listData(startToken: String? = null, maxResults: Int = 1000): MutableMap<String, String> = mutableMapOf<String, String>()
			.also { if (startToken != null) it["start-token"] = startToken }
			.also { if (maxResults != - 1) it["max-results"] = "$maxResults" }
	
	/**
	 * Lists a users tracks.
	 * [updatedAfter] is a Date from epoch.
	 * [startToken] is the nextPageToken from a previous response.
	 * [maxResults] is a positive integer; if not provided, server defaults to 1000.
	 * [callback] is optional used for async calls.
	 */
	fun listTracks(updatedAfter: Date? = null,
	               startToken: String? = null,
	               maxResults: Int = 1000,
	               callback: GpmCallback<ListPager<Track>>? = null): Response<ListPager<Track>>? =
			executeOrEnqueue(mobileClientService.listTracks(
					microseconds = updatedAfter?.time?.times(1000) ?: - 1,
					data = listData(startToken, maxResults)), callback)
	
	/**
	 * Lists all user-created playlists.
	 * [updatedAfter] is a Date from epoch.
	 * [startToken] is the nextPageToken from a previous response.
	 * [maxResults] is a positive integer; if not provided, server defaults to 1000.
	 * [callback] is optional used for async calls.
	 */
	fun listPlaylists(updatedAfter: Date? = null,
	                  startToken: String? = null,
	                  maxResults: Int = 1000,
	                  callback: GpmCallback<ListPager<Playlist>>? = null): Response<ListPager<Playlist>>? =
			executeOrEnqueue(mobileClientService.listPlaylists(
					microseconds = updatedAfter?.time?.times(1000) ?: - 1,
					data = listData(startToken, maxResults)), callback)
	
	fun listPlaylistsWithEntries(updatedAfter: Date? = null,
	                             startToken: String? = null,
	                             maxResults: Int = 1000,
	                             callback: BodyOnlyGpmCallback<ListPager<Playlist>>? = null): ListPager<Playlist>? {
		
		fun modifyPlaylists(playlists: ListPager<Playlist>) =
				playlists.copy(items = playlists.items?.filter {
					it.type == null || it.type == "USER_GENERATED" || it.type == "SHARED"
				})
		
		
		fun putEntriesIntoPlaylist(playlists: ListPager<Playlist>, entries: List<PlaylistEntry>) =
				playlists.copy(items = playlists.items?.onEach { playlist ->
					playlist.tracks = entries.filter { entry ->
						entry.playlistId == playlist.id
					}.sortedBy { it.absolutePosition }
				})
		
		
		var playlists = listPlaylists(updatedAfter, startToken, maxResults, if (callback != null) object : GpmCallback<ListPager<Playlist>>() {
			
			override fun onFailure(call: Call<ListPager<Playlist>>, gpmError: GpmError) {
				callback.onFailure(call = call, gpmError = gpmError)
			}
			
			@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
			override fun onResponse(call: Call<ListPager<Playlist>>, response: Response<ListPager<Playlist>>, payload: ListPager<Playlist>) {
				listPlaylistEntries(updatedAfter, startToken, maxResults, object : GpmCallback<ListPager<PlaylistEntry>>() {
					
					override fun onFailure(plentryCall: Call<ListPager<PlaylistEntry>>, gpmError: GpmError) {
						callback.onFailure(call = call, gpmError = gpmError)
					}
					
					override fun onResponse(plentryCall: Call<ListPager<PlaylistEntry>>, plentryResponse: Response<ListPager<PlaylistEntry>>, plentryPayload: ListPager<PlaylistEntry>) {
						callback.onResponse(
								call = call,
								payload = putEntriesIntoPlaylist(modifyPlaylists(payload), plentryPayload.items
										?: return))
					}
					
				})
			}
			
		} else null)?.body() ?: return null
		
		playlists = modifyPlaylists(playlists)
		return putEntriesIntoPlaylist(playlists, listPlaylistEntries(updatedAfter, startToken, maxResults, null) !!.body()?.items
				?: return playlists)
	}
	
	/**
	 * Lists the contents of all user-created playlists.
	 * [updatedAfter] is a Date from epoch.
	 * [startToken] is the nextPageToken from a previous response.
	 * [maxResults] is a positive integer; if not provided, server defaults to 1000.
	 * [callback] is optional used for async calls.
	 */
	fun listPlaylistEntries(updatedAfter: Date? = null,
	                        startToken: String? = null,
	                        maxResults: Int = 1000,
	                        callback: GpmCallback<ListPager<PlaylistEntry>>? = null): Response<ListPager<PlaylistEntry>>? =
			executeOrEnqueue(mobileClientService.listPlaylistEntries(
					microseconds = updatedAfter?.time?.times(1000) ?: - 1,
					data = listData(startToken, maxResults)), callback)
	
	/**
	 * Lists the contents of public playlists.
	 * [updatedAfter] is a Date from epoch.
	 * [startToken] is the nextPageToken from a previous response.
	 * [maxResults] is a positive integer; if not provided, server defaults to 1000.
	 * [shareToken] is a token from a shared playlist.
	 * [callback] is optional used for async calls.
	 */
	fun listSharedPlaylistEntries(updatedAfter: Date? = null,
	                              startToken: String? = null,
	                              maxResults: Int = 1000,
	                              shareToken: String,
	                              callback: GpmCallback<Entries<SharedPlaylistEntry>>? = null): Response<Entries<SharedPlaylistEntry>>? =
			executeOrEnqueue(mobileClientService.listSharedPlaylistEntries(
					microseconds = updatedAfter?.time?.times(1000) ?: - 1,
					entries = listOf(listData(startToken, maxResults) + ("shareToken" to shareToken))), callback)
	
	/**
	 * Lists only promoted store tracks.
	 * [updatedAfter] is a Date from epoch.
	 * [startToken] is the nextPageToken from a previous response.
	 * [maxResults] is a positive integer; if not provided, server defaults to 1000.
	 * [callback] is optional used for async calls.
	 */
	fun listPromotedTracks(updatedAfter: Date? = null,
	                       startToken: String? = null,
	                       maxResults: Int = 1000,
	                       callback: GpmCallback<ListPager<Track>>? = null): Response<ListPager<Track>>? =
			executeOrEnqueue(mobileClientService.listPromotedTracks(microseconds = updatedAfter?.time?.times(1000)
					?: - 1, data = listData(startToken, maxResults)), callback)
	
	/**
	 * Lists Listen Now albums and stations.
	 * [callback] is optional used for async calls.
	 */
	fun listListenNowItems(callback: GpmCallback<ListenNowItems>? = null): Response<ListenNowItems>? = executeOrEnqueue(mobileClientService.listListenNowItems(), callback)
	
	/**
	 * Lists Listen Now situations which each contain a list of related stations or other situations.
	 * [callback] is optional used for async calls.
	 */
	fun listListenNowSituations(callback: GpmCallback<ListListenNowSituations>? = null): Response<ListListenNowSituations>? =
			executeOrEnqueue(mobileClientService.listListenNowSituations(
					requestSignals = "requestSignals" to ("timeZoneOffsetSecs" to GregorianCalendar.getInstance(Locale.getDefault()).timeZone.getOffset(System.currentTimeMillis()))), callback)
	
	/**
	 * Lists podcast series from browse podcasts by genre.
	 * [id] is the genre id.
	 * [callback] is optional used for async calls.
	 */
	fun listBrowsePodcastSeries(id: String, callback: GpmCallback<BrowsePodcastSeries>? = null): Response<BrowsePodcastSeries>? =
			executeOrEnqueue(mobileClientService.listBrowsePodcastSeries(id = id), callback)
	
	/**
	 * Lists user-subscribed podcast series.
	 * [updatedAfter] is a Date from epoch.
	 * [startToken] is the nextPageToken from a previous response.
	 * [maxResults] is a positive integer
	 * [callback] is optional used for async calls.
	 */
	fun listPodcastSeries(updatedAfter: Date? = null,
	                      startToken: String? = null,
	                      maxResults: Int = 1000,
	                      callback: GpmCallback<ListPager<PodcastSeries>>? = null): Response<ListPager<PodcastSeries>>? =
			executeOrEnqueue(mobileClientService.listPodcastSeries(
					deviceId = UUID.randomUUID().toString(),
					microseconds = updatedAfter?.time?.times(1000) ?: - 1,
					startToken = startToken,
					maxResults = maxResults), callback)
	
	/**
	 * Lists episodes from user-subscribed podcast series
	 * [updatedAfter] is a Date from epoch.
	 * [startToken] is the nextPageToken from a previous response.
	 * [maxResults] is a positive integer; if not provided, server defaults to 1000.
	 * [callback] is optional used for async calls.
	 */
	fun listPodcastEpisodes(updatedAfter: Date? = null,
	                        startToken: String? = null,
	                        maxResults: Int = 1000,
	                        callback: GpmCallback<ListPager<PodcastEpisode>>? = null): Response<ListPager<PodcastEpisode>>? =
			executeOrEnqueue(mobileClientService.listPodcastEpisodes(
					deviceId = UUID.randomUUID().toString(),
					microseconds = updatedAfter?.time?.times(1000) ?: - 1,
					startToken = startToken,
					maxResults = maxResults), callback)
	
	/**
	 * Lists stations in the users library.
	 * [updatedAfter] is a Date from epoch.
	 * [startToken] is the nextPageToken from a previous response.
	 * [maxResults] is a positive integer; if not provided, server defaults to 1000.
	 * [callback] is optional used for async calls.
	 */
	fun listStations(updatedAfter: Date? = null,
	                 startToken: String? = null,
	                 maxResults: Int = 1000,
	                 callback: GpmCallback<ListPager<Station>>? = null): Response<ListPager<Station>>? =
			executeOrEnqueue(mobileClientService.listStations(microseconds = updatedAfter?.time?.times(1000)
					?: - 1, data = listData(startToken, maxResults)), callback)
	
	/**
	 * Lists tracks from a specified [stationId].
	 * [updatedAfter] is a Date from epoch.
	 * [stationId] the id of the station to get tracks from.
	 * [numEntries] the number of tracks to receive.
	 * [recentlyPlayedSongIds] a list of recently played track ids retrieved from this station.
	 * [callback] is optional used for async calls.
	 */
	fun listStationTracks(updatedAfter: Date? = null,
	                      stationId: String,
	                      numEntries: Int,
	                      recentlyPlayedSongIds: List<String>,
	                      callback: GpmCallback<ListStationTracks>? = null): Response<ListStationTracks>? =
			executeOrEnqueue(mobileClientService.listStationTracks(
					microseconds = updatedAfter?.time?.times(1000) ?: - 1,
					data = mutableMapOf(
							"contentFilter" to 1,
							"stations" to listOf(mapOf(
									"numEntries" to numEntries)))
							.plus(
									if (stationId == "IFL") arrayOf(
											"recentlyPlayed" to recentlyPlayedSongIds,
											"seed" to ("seedType" to 6))
									else arrayOf(
											"radioId" to stationId,
											"recentlyPlayed" to recentlyPlayedSongIds)
							     )), callback)
	
	/**
	 * Deletes users-created playlists.
	 * [playlistIds] ids of the playlists to delete.
	 * [callback] is optional used for async calls.
	 */
	fun deletePlaylists(playlistIds: List<String>,
	                    callback: GpmCallback<BatchMutateCall>? = null): Response<BatchMutateCall>? =
			executeOrEnqueue(mobileClientService.batchMutatePlaylists(deletePlaylists = playlistIds.map { "delete" to it }), callback)
	
	/**
	 * Updates user-created playlists.
	 * [playlistUpdates] a list of playlists with their updated data. Only the playlist id, name, description, and whether it's shared is needed.
	 * [callback] is optional used for async calls.
	 */
	fun updatePlaylist(playlistUpdates: List<Playlist>,
	                   callback: GpmCallback<BatchMutateCall>? = null): Response<BatchMutateCall>? =
			executeOrEnqueue(mobileClientService.batchMutatePlaylists(
					updatePlaylists = playlistUpdates.map {
						"update" to mapOf(
								"id" to it.id,
								"name" to it.name,
								"description" to it.description,
								"shareState" to it.shareState)
					}), callback)
	
	/**
	 * Creates new playlists for the current user.
	 * [newPlaylists] the new playlists to create. Only the name, description, and whether it's shared is needed.
	 * [callback] is optional used for async calls.
	 */
	fun addPlaylist(newPlaylists: List<Playlist>,
	                callback: GpmCallback<BatchMutateCall>? = null): Response<BatchMutateCall>? =
			executeOrEnqueue(mobileClientService.batchMutatePlaylists(
					addPlaylists = newPlaylists.map {
						"create" to mapOf(
								"creationTimestamp" to "-1",
								"deleted" to false.toString(),
								"lastModifiedTimestamp" to "0",
								"name" to it.name,
								"description" to it.description,
								"type" to "USER_GENERATED",
								"shareState" to it.shareState)
					}), callback)
	
	/**
	 * Deletes entries from a user-created playlist.
	 * [entryIds] the ids of the items to delete.
	 * [callback] is optional used for async calls.
	 */
	fun deletePlaylistEntries(entryIds: List<String>,
	                          callback: GpmCallback<BatchMutateCall>? = null): Response<BatchMutateCall>? =
			executeOrEnqueue(mobileClientService.batchMutatePlaylistEntries(entryIds.map { "delete" to it }), callback)
	
	/**
	 * Reorders entries in a user-created playlist.
	 * [entry] the entry to reorder.
	 * [toFollow] the entry to be put after [entry].
	 * [toPrecede] the entry to put before [entry].
	 * [callback] is optional used for async calls.
	 */
	fun reorderPlaylistEntry(entry: PlaylistEntry,
	                         toFollow: PlaylistEntry? = null,
	                         toPrecede: PlaylistEntry? = null,
	                         callback: GpmCallback<BatchMutateCall>? = null): Response<BatchMutateCall>? {
		val mutations = hashMapOf(
				"clientId" to entry.clientId,
				"creationTimestamp" to entry.creationTimestamp,
				"deleted" to "${entry.deleted}",
				"id" to entry.id,
				"lastModifiedTimestamp" to entry.lastModifiedTimestamp,
				"playlistId" to entry.playlistId,
				"source" to entry.source,
				"trackId" to entry.trackId)
		
		if (toPrecede != null)
			mutations["precedingEntryId"] = Gson().toJson(toPrecede, PlaylistEntry::class.java)
		if (toFollow != null)
			mutations["followingEntryId"] = Gson().toJson(toFollow, PlaylistEntry::class.java)
		return executeOrEnqueue(mobileClientService.batchMutatePlaylistEntries(update = "update" to mutations), callback)
	}
	
	/**
	 * Adds new entries to a user-created playlist.
	 * [playlistId] the id of the playlist to add to.
	 * [songIds] the ids of the songs to add to the playlist.
	 * [callback] is optional used for async calls.
	 */
	fun addPlaylistEntries(playlistId: String,
	                       songIds: List<String>,
	                       callback: GpmCallback<BatchMutateCall>? = null): Response<BatchMutateCall>? {
		val mutations = ArrayList<Pair<String, Map<String, String>>>()
		
		var prevId = UUID.fromString("").toString()
		var curId = UUID.randomUUID().toString()
		var nextId = UUID.randomUUID().toString()
		
		songIds.forEachIndexed { index, s ->
			val mDetails = hashMapOf(
					"clientId" to curId,
					"creationTimestamp" to "-1",
					"deleted" to false.toString(),
					"lastModifiedTimestamp" to "0",
					"playlistId" to playlistId,
					"source" to "1",
					"trackId" to s
			                        )
			if (s.startsWith("T"))
				mDetails["source"] = "2"
			
			if (index > 0)
				mDetails["precedingEntryId"] = prevId
			if (index < songIds.size - 1)
				mDetails["followingEntryId"] = nextId
			
			mutations.add("create" to mDetails)
			prevId = curId
			curId = nextId
			nextId = UUID.randomUUID().toString()
		}
		return executeOrEnqueue(mobileClientService.batchMutatePlaylistEntries(addEntries = mutations), callback)
	}
	
	/**
	 * Edits a podcast series subscription.
	 * [updates] the podcast series to update with their new info.
	 * [callback] is optional used for async calls.
	 */
	fun updatePodcastSeries(updates: List<PodcastSeries>,
	                        callback: GpmCallback<BatchMutateCall>? = null): Response<BatchMutateCall>? =
			executeOrEnqueue(mobileClientService.batchMutatePodcastSeries(updates.map
			{
				"update" to mapOf(
						"seriesId" to it.seriesId,
						"subscribed" to if (it.userPreferences != null) "${it.userPreferences.subscribed}" else false.toString(),
						"userPreferences" to mapOf(
								"notifyOnNewEpisode" to if (it.userPreferences != null) "${it.userPreferences.notifyOnNewEpisode}" else false.toString(),
								"subscrubed" to if (it.userPreferences != null) "${it.userPreferences.subscribed}" else false.toString()))
			}), callback)
	
	/**
	 * Deletes radio stations and their ids.
	 * [stationIds] the ids of the stations to delete.
	 * [callback] is optional used for async calls.
	 */
	fun deleteStations(stationIds: List<String>,
	                   callback: GpmCallback<BatchMutateCall>? = null): Response<BatchMutateCall>? =
			executeOrEnqueue(mobileClientService.batchMutateStations(stationIds.map {
				mapOf(
						"delete" to it,
						"includeFeed" to false.toString(),
						"numEntries" to "0")
			}), callback)
	
	/**
	 * Creates a station and returns its id.
	 * [name] the name of the new station.
	 * [seedItemId] the id of the seed.
	 * [seedType] the type of the seed.
	 * [includeTracks] if true, return 'num_tracks' tracks in the response.
	 * [numTracks] the number of tracks to return in the response.
	 * [recentDateTime] purpose unknown. defaults to now.
	 * [callback] is optional used for async calls.
	 */
	fun addStation(name: String,
	               seedItemId: String,
	               seedType: Int,
	               includeTracks: Boolean,
	               numTracks: Int,
	               recentDateTime: Date? = null,
	               callback: GpmCallback<BatchMutateCall>? = null):
			Response<BatchMutateCall>? =
			executeOrEnqueue(mobileClientService.batchMutateStations(
					addStations = mapOf(
							"createOrGet" to mapOf(
									"clientId" to UUID.randomUUID().toString(),
									"deleted" to false,
									"imageType" to 1,
									"lastModifiedTimestamp" to "-1",
									"name" to name,
									"recentTimestamp" to if (recentDateTime == null) "${(System.currentTimeMillis() * 1000)}" else "${(recentDateTime.time * 1000)}",
									"seed" to mapOf(
											"itemId" to seedItemId,
											"seedType" to seedType),
									"tracks" to emptyList<Track>()),
							"includeFeed" to includeTracks,
							"numEntries" to numTracks,
							"params" to ("contentFilter" to 1))), callback)
	
	/**
	 * Deletes songs from the library.
	 * [trackIds] the ids of the songs to delete.
	 * [callback] is optional used for async calls.
	 */
	fun deleteTracks(trackIds: List<String>,
	                 callback: GpmCallback<BatchMutateCall>? = null): Response<BatchMutateCall>? =
			executeOrEnqueue(mobileClientService.batchMutateTracks(trackIds.map
			{ "delete" to it }), callback)
	
	/**
	 * Adds store tracks to the library.
	 * [storeTrackInfo] the track to add to the library.
	 * [callback] is optional used for async calls.
	 */
	fun addTrack(storeTrackInfo: Track,
	             callback: GpmCallback<BatchMutateCall>? = null): Response<BatchMutateCall>? =
			executeOrEnqueue(mobileClientService.batchMutateTracks(
					addTracks = "create" to mapOf(
							"title" to storeTrackInfo.title,
							"artist" to storeTrackInfo.artist,
							"album" to storeTrackInfo.album,
							"albumArtist" to storeTrackInfo.albumArtist,
							"trackNumber" to storeTrackInfo.trackNumber,
							"totalTrackCount" to storeTrackInfo.totalTrackCount,
							"durationMillis" to storeTrackInfo.durationMillis,
							"artistArtRef" to storeTrackInfo.artistArtRef,
							"discNumber" to storeTrackInfo.discNumber,
							"totalDiscCount" to (storeTrackInfo.totalDiscCount ?: 0),
							"estimatedSize" to storeTrackInfo.estimatedSize,
							"trackType" to 8,
							"storeId" to storeTrackInfo.storeId,
							"albumId" to storeTrackInfo.albumId,
							"nid" to storeTrackInfo.nid,
							"composer" to storeTrackInfo.composer,
							"playCount" to (storeTrackInfo.playCount ?: 0),
							"year" to storeTrackInfo.year,
							"rating" to (storeTrackInfo.rating ?: "0"),
							"genre" to (storeTrackInfo.genre ?: ""),
							"trackAvailableForSubscription" to storeTrackInfo.trackAvailableForSubscription,
							"lastRatingChangeTimestamp" to storeTrackInfo.lastRatingChangeTimestamp,
							"primaryVideo" to storeTrackInfo.primaryVideo,
							"lastModifiedTimestamp" to (storeTrackInfo.lastModifiedTimestamp
									?: "0"),
							"explicitType" to storeTrackInfo.explicitType,
							"contentType" to storeTrackInfo.contentType,
							"deleted" to (storeTrackInfo.deleted ?: false),
							"creationTimestamp" to (storeTrackInfo.creationTimestamp ?: "-1"),
							"comment" to storeTrackInfo.comment,
							"beatsPerMinute" to (storeTrackInfo.beatsPerMinute ?: - 1),
							"recentTimestamp" to storeTrackInfo.recentTimestamp,
							"clientId" to storeTrackInfo.clientId,
							"id" to storeTrackInfo.id)), callback)
	
	/**
	 * Sets a rating for the specified tracks.
	 * [songs] the tracks to rate.
	 * [rating] the rating for the tracks.
	 * [callback] is optional used for async calls.
	 */
	fun rateSongs(songs: List<Track>, rating: Rating = Rating.UpThumb, callback: GpmCallback<BatchMutateCall>? = null): Response<BatchMutateCall>? =
			executeOrEnqueue(mobileClientService.batchMutateTracks(updateTracks = songs.map { it.rating = rating.rating; ("update" to it) }), callback)
	
	/**
	 * Modifies the metadata of the specified tracks.
	 * [songs] the tracks to modify.
	 * [callback] is optional used for async calls.
	 */
	fun changeSongMetadata(songs: List<Track>, callback: GpmCallback<BatchMutateCall>? = null): Response<BatchMutateCall>? =
			executeOrEnqueue(mobileClientService.batchMutateTracks(updateTracks = songs.map { ("update" to it) }), callback)
	
	/**
	 * Returns a list of devices associated with the account.
	 * [callback] is optional used for async calls.
	 */
	fun getDeviceManagementInfo(callback: GpmCallback<ListPager<DeviceManagement>>? = null): Response<ListPager<DeviceManagement>>? =
			executeOrEnqueue(mobileClientService.getDeviceManagementInfo(), callback)
	
	/**
	 * Deauthorize a registered device.
	 * [deleteId] the id of the device to deauthorize.
	 * [callback] is optional used for async calls.
	 */
	fun deauthDevice(deleteId: String, callback: GpmCallback<Boolean>? = null): Response<Boolean>? =
			executeOrEnqueue(mobileClientService.deauthDevice(deleteId), callback)
	
	/**
	 * Retrieve the hierarchy of podcast browse genres.
	 * [callback] is optional used for async calls.
	 */
	fun getBrowsePodcastHierarchy(callback: GpmCallback<BrowsePodcastHierarchy>? = null): Response<BrowsePodcastHierarchy>? =
			executeOrEnqueue(mobileClientService.getBrowsePodcastHierarchy(), callback)
	
	/**
	 * Retrieves information about a podcast series.
	 * [podcastSeriesId] the id of the podcast series; they always start with 'I'.
	 * [numEpisodes] the number of episodes to retrieve.
	 * [callback] is optional used for async calls.
	 */
	fun getPodcastSeries(podcastSeriesId: String, numEpisodes: Int, callback: GpmCallback<PodcastSeries>? = null): Response<PodcastSeries>? =
			executeOrEnqueue(mobileClientService.getPodcastSeries(podcastSeriesId, numEpisodes), callback)
	
	/**
	 * Retrieve information about a podcast episode.
	 * [podcastEpisodeId] the id of the podcast episode; they always start with 'D'.
	 * [callback] is optional used for async calls.
	 */
	fun getPodcastEpisode(podcastEpisodeId: String, callback: GpmCallback<PodcastEpisode>? = null): Response<PodcastEpisode>? =
			executeOrEnqueue(mobileClientService.getPodcastEpisode(podcastEpisodeId), callback)
	
	/**
	 * Retrieves information about a store track.
	 * [trackId] the track id to get; they always start with 'T'.
	 * [callback] is optional used for async calls.
	 */
	fun getStoreTrack(trackId: String, callback: GpmCallback<Track>? = null): Response<Track>? =
			executeOrEnqueue(mobileClientService.getStoreTrack(trackId), callback)
	
	/**
	 * Retrieves information on Google Music genres.
	 * [parentGenreId] (optional) if provided, only child genres will be returned.
	 *      By default, all root genres are returned.
	 *      If this id is invalid, an empty list will be returned.
	 * [callback] is optional used for async calls.
	 */
	fun getGenres(parentGenreId: String, callback: GpmCallback<ListGenres>? = null): Response<ListGenres>? =
			executeOrEnqueue(mobileClientService.getGenres(parentGenreId), callback)
	
	/**
	 * Retrieves details on an artist.
	 * [artistId] the id of the artist to get; they always start with 'A'.
	 * [includeAlbums] when true, create the 'albums' substructure
	 * [numTopTracks] number of top tracks to retrieve.
	 * [numRelatedArtists] number of related artists to retrieve.
	 * [callback] is optional used for async calls.
	 */
	fun getArtist(artistId: String,
	              includeAlbums: Boolean = true,
	              numTopTracks: Int,
	              numRelatedArtists: Int,
	              callback: GpmCallback<Artist>? = null): Response<Artist>? =
			executeOrEnqueue(mobileClientService.getArtist(artistId, includeAlbums, numTopTracks, numRelatedArtists), callback)
	
	/**
	 * Retrieves details on an album.
	 * [albumId] the id of the album to get; they always start with 'B'.
	 * [includeTracks] when true, create the 'tracks' substructure.
	 * [callback] is optional used for async calls.
	 */
	fun getAlbum(albumId: String,
	             includeTracks: Boolean = true,
	             callback: GpmCallback<Album>? = null): Response<Album>? =
			executeOrEnqueue(mobileClientService.getAlbum(albumId, includeTracks), callback)
	
	/* part 1 of the streaming key */
	private val s1: ByteArray = Base64.decode("VzeC4H4h+T2f0VI180nVX8x+Mb5HiTtGnKgH52Otj8ZCGDz9jRW" +
			"yHb6QXK0JskSiOgzQfwTY5xgLLSdUSreaLVMsVVWfxfa8Rw==", Base64.DEFAULT)
	
	/* part 2 of the streaming key */
	private val s2: ByteArray = Base64.decode("ZAPnhUkYwQ6y5DdQxWThbvhJHN8msQ1rqJw0ggKdufQjelrKuiG" +
			"GJI30aswkgCWTDyHkTGK9ynlqTkJ5L4CiGGUabGeo8M6JTQ==", Base64.DEFAULT)
	
	/* both parts of the streaming key combined */
	private val key = String((s1 zip s2).map { it.first xor it.second }.toByteArray(), Charsets.US_ASCII)
	
	/**
	 * Obtains the signature in order to stream songs.
	 * [itemId] the id of the item to stream.
	 * [salt] the current time.
	 */
	private fun getStreamSignature(itemId: String, salt: String? = null): Pair<String, String> {
		val songSalt = salt ?: "${System.currentTimeMillis()}"
		
		val mac = Mac.getInstance("HmacSHA1")
		val keySpec = SecretKeySpec(key.toByteArray(), "HmacSHA1")
		mac.init(keySpec)
		mac.update(itemId.toByteArray(Charsets.UTF_8))
		mac.update(songSalt.toByteArray(Charsets.UTF_8))
		val sig = Base64.encode(mac.doFinal(), Base64.URL_SAFE).dropLast(1).toByteArray()
		return (String(sig) to songSalt)
	}
	
	/**
	 * Gets the stream URL for a track.
	 * [songId] the id of the song to stream.
	 * [deviceId] the id of the device being used to stream.
	 * [quality] the quality of the streaming audio, default is Hi.
	 * [callback] is optional used for async calls.
	 */
	fun getStreamUrl(songId: String,
	                 deviceId: String,
	                 quality: Quality = Quality.Hi,
	                 callback: GpmCallback<String>? = null): Response<String>? =
			executeOrEnqueue(mobileClientService.getStreamUrl(deviceId, getStreamSignature(songId).let
			{ (sig, salt) ->
				mutableMapOf(
						"opt" to quality.quality,
						"net" to "mob",
						"pt" to "e",
						"slt" to salt,
						"sig" to sig,
						if (songId.startsWith("T") || songId.startsWith("D")) "mick" to songId else "songid" to songId)
			}), callback)
	
	/**
	 * Gets the stream URL for a podcast episode.
	 * [episodeId] the id of the episode to stream.
	 * [deviceId] the id of the device being used to stream.
	 * [quality] the quality of the streaming audio, default is Hi.
	 * [callback] is optional used for async calls.
	 */
	fun getPodcastEpisodeStreamUrl(episodeId: String,
	                               deviceId: String,
	                               quality: Quality = Quality.Hi,
	                               callback: GpmCallback<String>? = null): Response<String>? =
			executeOrEnqueue(mobileClientService.getPodcastEpisodeStreamUrl(deviceId, getStreamSignature(episodeId).let
			{ (sig, salt) ->
				mutableMapOf(
						"opt" to quality.quality,
						"net" to "mob",
						"pt" to "e",
						"slt" to salt,
						"sig" to sig,
						if (episodeId.startsWith("T") || episodeId.startsWith("D")) "mick" to episodeId else "songid" to episodeId)
			}), callback)
	
	/**
	 * Gets the stream URL for a station track.
	 * [songId] the id of the song to stream.
	 * [wentryId] the id of the station.
	 * [sessionToken] the session token from a station.
	 * [quality] the quality of the streaming audio, default is Hi.
	 * [callback] is optional used for async calls.
	 */
	fun getStationTrackStreamUrl(songId: String,
	                             wentryId: String,
	                             sessionToken: String,
	                             quality: Quality = Quality.Hi,
	                             callback: GpmCallback<String>? = null): Response<String>? =
			executeOrEnqueue(mobileClientService.getStationTrackStreamUrl(getStreamSignature(songId).let
			{ (sig, salt) ->
				mutableMapOf(
						if (songId.startsWith("T")) "mick" to songId else "songid" to songId,
						"sesstok" to String(sessionToken.toByteArray(), Charsets.UTF_8),
						"wentryid" to String(wentryId.toByteArray(), Charsets.UTF_8),
						"tier" to "fr",
						"audio_formats" to "mp3",
						"opt" to quality.quality,
						"net" to "mob",
						"pt" to "a",
						"slt" to salt,
						"sig" to sig)
			}), callback)
	
}
