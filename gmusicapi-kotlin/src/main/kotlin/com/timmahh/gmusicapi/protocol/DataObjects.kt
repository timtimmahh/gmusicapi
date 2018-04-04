package com.timmahh.gmusicapi.protocol

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonDefaultValueString
import se.ansman.kotshi.JsonSerializable

/*
@JsonSerializable
abstract class JsonBase(type: String,
                    additionalProperties: Boolean? = null,
                    properties: JsonProperties? = null)
@JsonSerializable
data class JsonType(val type: String) : JsonBase(type)

@JsonSerializable
data class JsonRequiredType(val type: String,
                            @JsonDefaultValueBoolean(false)
                            val required: Boolean = false) : JsonBase(type)

@JsonSerializable
open class JsonProperties

@JsonSerializable
data class ImageColorStyles(
		@JsonDefaultValueString("object") val type: String = "object",
		@JsonDefaultValueBoolean(false) val additionalProperties: Boolean = false,
		val properties: ImageColorStyleProperties = ImageColorStyleProperties()) : JsonBase(type, additionalProperties, properties)
	
@JsonSerializable
data class ImageColorStyleProperties(
		val primary: ImageColorStyleProperty = ImageColorStyleProperty(),
		val scrim: ImageColorStyleProperty = ImageColorStyleProperty(),
		val accent: ImageColorStyleProperty = ImageColorStyleProperty()) : JsonProperties()

@JsonSerializable
data class ImageColorStyleProperty(
		@JsonDefaultValueString("object") val type: String = "object",
		@JsonDefaultValueBoolean(false) val additionalProperties: Boolean = false,
		val properties: ImageColorStylePropertyProperties = ImageColorStylePropertyProperties()) : JsonBase(type, additionalProperties, properties)

@JsonSerializable
data class ImageColorStylePropertyProperties(
		val red: JsonType = JsonType("integer"),
		val green: JsonType = JsonType("integer"),
		val blue: JsonType = JsonType("integer")) : JsonProperties()

@JsonSerializable
data class Image(
		@JsonDefaultValueString("object") val type: String = "object",
		@JsonDefaultValueBoolean(false) val additionalProperties: Boolean = false,
		val properties: ImageProperties = ImageProperties()) : JsonBase(type, additionalProperties, properties)

@JsonSerializable
data class ImageProperties(
		val kind: JsonType = JsonType("string"),
		val url: JsonType = JsonType("string"),
		val aspectRatio: JsonRequiredType = JsonRequiredType("string"),
		val autogen: JsonRequiredType = JsonRequiredType("boolean"),
		val colorStyles: RequiredImageColorStyles = RequiredImageColorStyles()) : JsonProperties()

@JsonSerializable
data class RequiredImageColorStyles(
		val imageColorStyles: ImageColorStyles = ImageColorStyles(),
		val required: Boolean = false)

@JsonSerializable
data class Video(
		@JsonDefaultValueString("object") val type: String = "object",
		@JsonDefaultValueBoolean(false) val additionalProperties: Boolean = false,
		val properties: ) : JsonBase()

@JsonSerializable
data class VideoProperties(val kind: JsonType = JsonType("string"),
                           val id: JsonType = JsonType("string"),
                           val title: JsonRequiredType = JsonRequiredType("string"),
                           val thumbnails: )*/

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
data class ImageUrls(val url: String)

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
                 val albumArtRef: List<ImageUrls>? = null,
                 val artistArtRef: List<ImageUrls>? = null,
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
                    val type: String? = null, //pattern = r'MAGIC|SHARED|USER_GENERATED'
                    val lastModifiedTimestamp: String? = null,
                    val recentTimestamp: String? = null,
                    val shareToken: String,
                    val ownerProfilePhotoUrl: String? = null,
                    val ownerName: String? = null,
                    val accessControlled: Boolean? = null,
                    val shareState: String? = null, //pattern = r'PRIVATE|PUBLIC'
                    val creationTimestamp: String? = null,
                    val id: String? = null,
                    val albumArtRef: List<ImageUrls>? = null,
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

