package com.timmahh.gmusicapi.clients

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.lang.reflect.Type

class SearchTypeSerializer : JsonSerializer<SearchType> {
	
	override fun serialize(src: SearchType?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
		return JsonPrimitive(src?.type ?: - 1)
	}
	
}

class SearchTypeDeserializer : JsonDeserializer<SearchType> {
	
	private fun getIntValue(json: JsonElement) =
			if (json.isJsonPrimitive)
				when {
					json.asJsonPrimitive.isNumber -> json.asInt
					json.asJsonPrimitive.isString -> json.asString.toIntOrNull() ?: - 1
					else -> - 1
				}
			else - 1
	
	override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): SearchType =
			when (getIntValue(json)) {
				SearchType.Song.type -> SearchType.Song
				SearchType.Artist.type -> SearchType.Artist
				SearchType.Album.type -> SearchType.Album
				SearchType.Playlist.type -> SearchType.Playlist
				SearchType.Station.type -> SearchType.Station
				SearchType.Situation.type -> SearchType.Situation
				SearchType.Video.type -> SearchType.Video
				SearchType.PodcastSeries.type -> SearchType.PodcastSeries
				else -> SearchType.None
			}
}

class ListPagerTypeAdapterFactory : TypeAdapterFactory {
	
	override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T> {
		val delegate = gson.getDelegateAdapter(this, type)
		val elementAdapter = gson.getAdapter(JsonElement::class.java)
		
		return object : TypeAdapter<T>() {
			
			override fun write(out: JsonWriter?, value: T) {
				delegate.write(out, value)
			}
			
			override fun read(`in`: JsonReader?): T {
				val jsonElement: JsonElement = elementAdapter.read(`in`)
				if (jsonElement.isJsonObject && jsonElement.asJsonObject.has("data") && jsonElement.asJsonObject.getAsJsonObject("data").has("items"))
					jsonElement.asJsonObject.add("items",
							jsonElement.asJsonObject.remove("data").asJsonObject.getAsJsonArray("items"))
				
				return delegate.fromJsonTree(jsonElement)
			}
			
		}.nullSafe()
	}
}