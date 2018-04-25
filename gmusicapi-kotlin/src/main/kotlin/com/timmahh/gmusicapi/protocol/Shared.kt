sealed class AuthTypes(@Suppress("UNUSED_PARAMETER") authType: Boolean) {
	class XT(authType: Boolean = false) : AuthTypes(authType)
	class SSO(authType: Boolean = false) : AuthTypes(authType)
	class OAuth(authType: Boolean = false) : AuthTypes(authType)
	class None(authType: Boolean = false) : AuthTypes(authType)
}