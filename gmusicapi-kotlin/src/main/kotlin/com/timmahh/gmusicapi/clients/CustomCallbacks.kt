package com.timmahh.gmusicapi.clients

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class GpmCallback<T> : Callback<T> {
	
	
	abstract fun onFailure(call: Call<T>, gpmError: GpmError)
	
	abstract fun onResponse(call: Call<T>, response: Response<T>, payload: T)
	
	final override fun onResponse(call: Call<T>, response: Response<T>) {
		if (response.isSuccessful && response.body() != null) {
			onResponse(call, response, response.body() !!)
		} else {
			onFailure(call, GpmError.fromResponse(response))
		}
	}
	
	final override fun onFailure(call: Call<T>, t: Throwable) {
		onFailure(call, GpmError(t))
	}
	
}

abstract class BodyOnlyGpmCallback<T> : GpmCallback<T>() {
	
	abstract fun onResponse(call: Call<T>, payload: T)
	
	override fun onResponse(call: Call<T>, response: Response<T>, payload: T) {
		onResponse(call, payload)
	}
	
}

class GpmError : Exception {
	
	constructor(detailMessage: String) : super(detailMessage)
	
	constructor(cause: Throwable) : super(cause)
	
	companion object {
		
		fun <T> fromResponse(response: Response<T>): GpmError {
			
			val error: String? = response.errorBody()?.string()
			
			return if (error != null && error.isNotEmpty()) {
				GpmError(error)
			} else {
				GpmError(Throwable("Can't read error response"))
			}
		}
	}
}