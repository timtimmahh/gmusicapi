package com.timmahh.gmusicapi.activity

import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.Secure.ANDROID_ID
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.timmahh.gmusicapi.R
import com.timmahh.gmusicapi.clients.MobileClient
import com.timmahh.gmusicapi.protocol.Config
import com.timmahh.gmusicapi.protocol.ConfigList
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity() {
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		
		val mobileClient = MobileClient()
		launch(CommonPool) {
			if (mobileClient.login("timtimmahh@gmail.com", "sfavmftqmztyqkof", Settings.Secure.getString(contentResolver, ANDROID_ID), Locale.getDefault()).await())
				Log.d("Google Play Music", "Login successfull")
			else Log.d("Google Play Music", "Login failed")
			
			launch(UI) {
				mobileClient.getConfig(object : Callback<ConfigList<Config>> {
					override fun onFailure(call: Call<ConfigList<Config>>?, t: Throwable?) {
						Log.e("ConfigFailure", t?.message ?: "Unknown error.")
					}
					
					override fun onResponse(call: Call<ConfigList<Config>>?, response: Response<ConfigList<Config>>?) {
						Log.d("ConfigResponse", response?.isSuccessful.toString())
					}
					
				})
			}
		}
		
		
	}
	
}
