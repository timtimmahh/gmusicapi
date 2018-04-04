/*
abstract class ClientBase(loggerBasename: String, debugLogging: Boolean, val validate: Boolean, val verifySsl: Boolean) {
	
	companion object {
		@JvmField
		var numClients = 0
	}
	
	val session: Base
	val cache: LruCache<String, String> = LruCache(100)
	val logger: Logger
	
	init {
		ClientBase.numClients ++
		val loggerName = String.format("gmusicapi.%s%s", loggerBasename, ClientBase.numClients)
		this.logger = Logger.getLogger(loggerName)
		
		fun setupSession(): (clientConfig: HttpClientConfig) -> Unit = {  }
		
		this.session = getSession(setupSession(), listOf(), Hashtable<String, Any>(0))
		
		if (debugLogging) {
			configureDebugLogHandlers(logger)
		}
		
		this.logger.info("initialized")
		this.logout()
	}
	
	abstract fun getSession(setupSession: (clientConfig: HttpClientConfig) -> Unit, args: List<Any>, kwargs: Dictionary<String, Any>): Base
	
	fun makeCall(protocol: Call, args: List<Any>, kwargs: Dictionary<String, Any>) = protocol.perform(session.client, this.validate, args, kwargs)
	
	fun isAuthenticated() = this.session.isAuthenticated
	
	fun logout(): Boolean {
		this.session.logout()
		this.cache.evictAll()
		this.logger.info("logged out")
		return true
	}
	
}*/
