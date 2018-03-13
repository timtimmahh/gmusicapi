class CallFailure : Exception {
	
	private val callName: String
	
	constructor() : this("", "")
	
	constructor(message: String, callName: String) : super(message) {
		this.callName = callName
	}
	
	override fun toString(): String =
			String.format("%s: %s\n", this.callName, super.toString())
}

class ParseException : Exception()

class ValidationException : Exception()

class AlreadyLoggedIn : Exception()

class NotLoggedIn : Exception()

class NotSubscribed(args: Array<String>
                    = arrayOf("Subscription required."))
	: Exception({
	args[0] += " (https://goo.gl/v1wVHT)"
	args.joinToString { "%s" }
}.invoke())

class GMusicAPIWarning : Exception()

class InvalidDeviceId(message: String, ids: Array<String>
= arrayOf("It looks like your account does not have any valid device IDs.")) : Exception(message
		+ if (! ids[0].startsWith("It looks")) "Your valid device IDs are:\n" else ""
		+ ids.joinToString { "%s" })