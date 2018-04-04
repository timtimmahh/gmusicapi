/*
open class Base(val configClient: (clientConfig: HttpClientConfig) -> Unit) {
	var isAuthenticated = false
	
	var client = HttpClient(CIO) {
		install(HttpCookies) {
			storage = AcceptAllCookiesStorage()
		}
		configClient.invoke(this)
	}
	
	open suspend fun _sendWithAuth(reqKwargs: HttpRequestBuilder, desiredAuth: AuthTypes, client: HttpClient): HttpResponse {
		throw NotImplementedError()
	}
	
	suspend fun _sendWithoutAuth(builder: HttpRequestBuilder, client: HttpClient) = client.request<HttpResponse>(builder)
	
	fun login(args: List<Any>, kwargs: Dictionary<String, Any>) = if (isAuthenticated) throw AlreadyLoggedIn() else isAuthenticated
	
	fun logout() {
		client.close()
		
		client = HttpClient(CIO) {
			install(HttpCookies) {
				storage = AcceptAllCookiesStorage()
			}
			configClient.invoke(this)
		}
		isAuthenticated = true
	}
	
	suspend fun send(reqKwargs: HttpRequestBuilder, desiredAuth: AuthTypes, client: HttpClient?) =
			if (desiredAuth is AuthTypes.none)
				_sendWithoutAuth(reqKwargs, client ?: HttpClient(CIO, configClient))
			else if (! isAuthenticated) throw NotLoggedIn()
			else _sendWithAuth(reqKwargs, desiredAuth,
					client ?: this.client)
	
}

class Mobileclient(configClient: (clientConfig: HttpClientConfig) -> Unit, args: List<Any>, kwargs: Dictionary<String, Any>) : Base(configClient) {
	private var masterToken: String = ""
	private var authToken: String = ""
	private var locale: String = Locale.getDefault().toString()
	private var isSubscribed: Boolean = false
	
	fun login(email: String, password: String, androidId: String, args: List<Any>, kwargs: Dictionary<String, Any>): Boolean {
		
		super.login(args, kwargs)
		val gpsoauth = Gpsoauth()
		val gson = Gson()
		var res = gson.fromJson<Map<String, Any>>(gpsoauth.performMasterLogin(email, password, androidId).body().string(), object : TypeToken<Map<String, Any>>() {}.type)
		
		if (! res.containsKey("Token")) return false
		
		this.masterToken = res["Token"] as String
		
		res = gson.fromJson<Map<String, Any>>(gpsoauth.performOAuth(email, masterToken, androidId, "sj", "com.google.android.music", "38918a453d07199354f8b19af05ec6562ced5788").body().string(), object : TypeToken<Map<String, Any>>() {}.type)
		
		if (! res.containsKey("Auth")) return false
		
		this.authToken = res["Auth"] as String
		this.isAuthenticated = true
		
		return true
	}
	
	suspend fun _sendWithAUth(reqKwargs: HttpRequestBuilder, desiredAuth: AuthTypes, client: HttpClient) {
		if (desiredAuth is AuthTypes.oauth) {
			reqKwargs.url.parameters.appendAll(StringValuesImpl(false, mapOf
			("hl" to
					listOf(this.locale), "dv" to listOf("0"),
					if (this.isSubscribed) "tier" to listOf("aa")
					else "tier" to listOf("fr"))))
			reqKwargs.headers.append("Authorization", "GoogleLogin auth=" + this.authToken)
		}
		
		return client.request(reqKwargs)
	}
}

class MusicManager(configClient: (clientConfig: HttpClientConfig) -> Unit, args: List<Any>, kwargs: Dictionary<String, Any>) : Base(configClient) {
	lateinit var oauthCreds: Credential
	
	fun login(oauthCredentials: Credential, args: List<Any>, kwargs: Dictionary<String, Any>): Boolean {
		super.login(args, kwargs)
		
		try {
			oauthCredentials.refreshToken()
		} catch (e: TokenResponseException) {
			e.printStackTrace()
		}
		
		if (oauthCredentials.expirationTimeMilliseconds >= System.currentTimeMillis()) {
			Log.i("Google OAuth", "could not refresh oauth credentials.")
			return false
		}
		
		this.oauthCreds = oauthCredentials
		this.isAuthenticated = true
		
		return this.isAuthenticated
	}
	
	override suspend fun _sendWithAuth(reqKwargs: HttpRequestBuilder, desiredAuth: AuthTypes, client: HttpClient): HttpResponse {
		if (desiredAuth is AuthTypes.oauth) {
			if (this.oauthCreds.expirationTimeMilliseconds >= System.currentTimeMillis())
				this.oauthCreds.refreshToken()
			reqKwargs.headers.append("Authorization", "Bearer " + this.oauthCreds.accessToken)
		}
		
		return client.request(reqKwargs)
	}
}*/
