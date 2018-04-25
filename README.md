# gmusicapi
A port of [Simon Weber's Google Play Music Python API](https://github.com/simon-weber/gmusicapi) written in Kotlin.

This port is being developed to run on Android devices so third parties can implement a Google Play Music app.

**This project is not supported nor endorsed by Google**

This project is currently in its very early stages, so far only the MobileClient has been tested to work.

This library runs on Retrofit 2 and uses Gson for serialization.

At the moment, Google accounts that have 2-factor authentication enabled cannot log in using the master login flow, however the way to get around this is to create an [app password](https://myaccount.google.com/apppasswords). For accounts without 2-factor authentication, it seems to work fine.
