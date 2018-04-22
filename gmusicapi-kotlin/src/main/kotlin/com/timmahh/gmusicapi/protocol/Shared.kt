sealed class AuthTypes(@Suppress("UNUSED_PARAMETER") authType: Boolean) {
	class xt(authType: Boolean = false) : AuthTypes(authType)
	class sso(authType: Boolean = false) : AuthTypes(authType)
	class oauth(authType: Boolean = false) : AuthTypes(authType)
	class none(authType: Boolean = false) : AuthTypes(authType)
}