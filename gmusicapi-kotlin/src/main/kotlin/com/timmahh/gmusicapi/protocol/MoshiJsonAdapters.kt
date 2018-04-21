package com.timmahh.gmusicapi.protocol

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.ToJson
import se.ansman.kotshi.KotshiJsonAdapterFactory
import java.text.SimpleDateFormat
import java.util.*


@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class CredentialDate

class CredentialDateAdapter {
	
	@ToJson
	fun toJson(@CredentialDate date: Date): String =
			SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ", Locale.getDefault()).format(date)
	
	@FromJson
	@CredentialDate
	fun fromJson(dateString: String): Date = SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ", Locale.getDefault()).parse(dateString)
	
}

@KotshiJsonAdapterFactory
abstract class GpmJsonAdapterFactory : JsonAdapter.Factory {
	companion object {
		val INSTANCE: GpmJsonAdapterFactory = KotshiGpmJsonAdapterFactory()
	}
}