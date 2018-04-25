package com.timmahh.gmusicapi.clients

/**
 * Sealed class enum for possible search types.
 * [type] the type of the search. An integer.
 * [name] the name of the type.
 */
sealed class SearchType(val type: Int, val name: String) {
	object Song : SearchType(1, "Song")
	object Artist : SearchType(2, "Artist")
	object Album : SearchType(3, "Album")
	object Playlist : SearchType(4, "Playlist")
	object Station : SearchType(6, "Station")
	object Situation : SearchType(7, "Situation")
	object Video : SearchType(8, "Video")
	object PodcastSeries : SearchType(9, "Podcast Series")
	object None : SearchType(- 1, "Invalid")
}

/* A list of all search types. */
val allSearchTypes =
		listOf(
				SearchType.Song,
				SearchType.Artist,
				SearchType.Album,
				SearchType.Playlist,
				SearchType.Station,
				SearchType.Situation,
				SearchType.Video,
				SearchType.PodcastSeries
		      )

/**
 * Sealed class enum for possible rating options.
 * [rating] the rating value.
 */
sealed class Rating(val rating: String) {
	object NoThumb : Rating("0")
	object DownThumb : Rating("1")
	object UpThumb : Rating("5")
}

/**
 * Possible quality options for streaming.
 * [quality] the name of the audio quality.
 * [kbps] kilobytes per second for the audio quality.
 */
sealed class Quality(val quality: String = "hi", private val kbps: Int) {
	object Hi : Quality("hi", 320)
	object Med : Quality("med", 160)
	object Low : Quality("low", 128)
}