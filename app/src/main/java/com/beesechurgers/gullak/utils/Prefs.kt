package com.beesechurgers.gullak.utils

import android.content.Context
import com.ironz.binaryprefs.BinaryPreferencesBuilder
import com.ironz.binaryprefs.Preferences

object Prefs {
    private const val APP_PREFS = "Gullak_prefs"

    // Keys
    const val USER_PROFILE_PIC_URL = "profile_pic_url"
    const val USER_NAME = "name"

    @Volatile
    private var preference: Preferences? = null

    private fun getPreference(context: Context): Preferences {
        if (preference == null) {
            preference = BinaryPreferencesBuilder(context.applicationContext)
                .name(APP_PREFS)
                .build()
        }
        return preference!!
    }

    fun Context.putBoolean(key: String, bool: Boolean) =
        getPreference(this).edit().putBoolean(key, bool).apply()

    fun Context.getBoolean(key: String, def: Boolean): Boolean = getPreference(this).getBoolean(key, def)

    fun Context.putInt(key: String, int: Int) =
        getPreference(this).edit().putInt(key, int).apply()

    fun Context.getInt(key: String, def: Int): Int = getPreference(this).getInt(key, def)

    fun Context.putString(key: String, string: String) =
        getPreference(this).edit().putString(key, string).apply()

    fun Context.getString(key: String, def: String): String = getPreference(this).getString(key, def) ?: def
}
