package com.mdshahriar.uthenbhai.ui.locale

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import java.util.Locale

enum class AppLanguage { ENGLISH, BANGLA }

val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.ENGLISH }

@Composable
fun tr(english: String, bangla: String): String =
    if (LocalAppLanguage.current == AppLanguage.BANGLA) bangla else english

object LanguageStore {
    private const val PREFS = "app_preferences"
    private const val KEY = "language"

    fun load(context: Context): AppLanguage {
        val saved = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, null)
        return when (saved) {
            AppLanguage.ENGLISH.name -> AppLanguage.ENGLISH
            AppLanguage.BANGLA.name -> AppLanguage.BANGLA
            else -> if (Locale.getDefault().language == "bn") AppLanguage.BANGLA else AppLanguage.ENGLISH
        }
    }

    fun save(context: Context, language: AppLanguage) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY, language.name).apply()
    }
}
