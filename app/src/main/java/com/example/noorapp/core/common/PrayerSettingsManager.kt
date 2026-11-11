package com.example.noorapp.core.common

import android.content.Context
import androidx.annotation.StringRes
import com.example.noorapp.R
import com.example.noorapp.domain.model.PrayerType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

enum class PrayerNotificationMode(
    val key: String,
    @StringRes val displayNameRes: Int,
    @StringRes val descriptionRes: Int
) {
    ADHAN("adhan", R.string.notif_mode_adhan_title, R.string.notif_mode_adhan_desc),
    TONE("tone", R.string.notif_mode_tone_title, R.string.notif_mode_tone_desc),
    SILENT("silent", R.string.notif_mode_silent_title, R.string.notif_mode_silent_desc);

    companion object {
        fun fromKey(key: String?): PrayerNotificationMode {
            return entries.find { it.key.equals(key, ignoreCase = true) } ?: ADHAN
        }
    }
}

enum class PrayerCalculationMethod(
    val index: Int,
    @StringRes val displayNameRes: Int,
    @StringRes val shortNameRes: Int,
    val apiMethodId: Int,
    val fajrAngle: Double,
    val ishaAngle: Double,
    val ishaFixedMinutes: Int? = null,
    val defaultAsrSchool: Int = 0 // 0 = Standard (Shafi'i 1x), 1 = Hanafi (2x)
) {
    KARACHI(
        index = 0,
        displayNameRes = R.string.calc_method_karachi,
        shortNameRes = R.string.calc_method_karachi_short,
        apiMethodId = 1,
        fajrAngle = 18.0,
        ishaAngle = 18.0,
        defaultAsrSchool = 1
    ),
    ISNA(
        index = 1,
        displayNameRes = R.string.calc_method_isna,
        shortNameRes = R.string.calc_method_isna_short,
        apiMethodId = 2,
        fajrAngle = 15.0,
        ishaAngle = 15.0,
        defaultAsrSchool = 0
    ),
    MWL(
        index = 2,
        displayNameRes = R.string.calc_method_mwl,
        shortNameRes = R.string.calc_method_mwl_short,
        apiMethodId = 3,
        fajrAngle = 18.0,
        ishaAngle = 17.0,
        defaultAsrSchool = 0
    ),
    UMM_AL_QURA(
        index = 3,
        displayNameRes = R.string.calc_method_umm_al_qura,
        shortNameRes = R.string.calc_method_umm_al_qura_short,
        apiMethodId = 4,
        fajrAngle = 18.5,
        ishaAngle = 17.0,
        ishaFixedMinutes = 90,
        defaultAsrSchool = 0
    ),
    EGYPTIAN(
        index = 4,
        displayNameRes = R.string.calc_method_egyptian,
        shortNameRes = R.string.calc_method_egyptian_short,
        apiMethodId = 5,
        fajrAngle = 19.5,
        ishaAngle = 17.5,
        defaultAsrSchool = 0
    ),
    TEHRAN(
        index = 5,
        displayNameRes = R.string.calc_method_tehran,
        shortNameRes = R.string.calc_method_tehran_short,
        apiMethodId = 7,
        fajrAngle = 17.7,
        ishaAngle = 14.0,
        defaultAsrSchool = 0
    ),
    GULF(
        index = 6,
        displayNameRes = R.string.calc_method_gulf,
        shortNameRes = R.string.calc_method_gulf_short,
        apiMethodId = 8,
        fajrAngle = 19.5,
        ishaAngle = 17.5,
        ishaFixedMinutes = 90,
        defaultAsrSchool = 0
    ),
    TURKEY_DIYANET(
        index = 7,
        displayNameRes = R.string.calc_method_turkey,
        shortNameRes = R.string.calc_method_turkey_short,
        apiMethodId = 13,
        fajrAngle = 18.0,
        ishaAngle = 17.0,
        defaultAsrSchool = 0
    ),
    SINGAPORE_JAKIM(
        index = 8,
        displayNameRes = R.string.calc_method_jakim,
        shortNameRes = R.string.calc_method_jakim_short,
        apiMethodId = 11,
        fajrAngle = 20.0,
        ishaAngle = 18.0,
        defaultAsrSchool = 0
    );

    companion object {
        fun fromIndex(index: Int): PrayerCalculationMethod {
            return entries.find { it.index == index } ?: MWL
        }
    }
}

data class PrayerSettingsState(
    val methodIndex: Int,
    val asrJuristic: Int, // 0 = Standard (1x shadow), 1 = Hanafi (2x shadow)
    val hijriOffset: Int, // -2 to +2
    val notificationsEnabled: Boolean,
    val isMethodManuallySelected: Boolean,
    val isAsrManuallySelected: Boolean,
    val prayerNotificationModes: Map<PrayerType, PrayerNotificationMode> = emptyMap(),
    val prayerAlarmsEnabled: Map<PrayerType, Boolean> = emptyMap()
) {
    val method: PrayerCalculationMethod
        get() = PrayerCalculationMethod.fromIndex(methodIndex)

    val asrShadowFactor: Double
        get() = if (asrJuristic == 1) 2.0 else 1.0
}

@Singleton
class PrayerSettingsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _settingsState: MutableStateFlow<PrayerSettingsState>
    val settingsState: StateFlow<PrayerSettingsState>

    init {
        val isManualMethod = prefs.getBoolean(KEY_MANUAL_METHOD, false)
        val isManualAsr = prefs.getBoolean(KEY_MANUAL_ASR, false)

        val defaultDetected = detectMethodFromDemographyAndCoordinates(0.0, 0.0)
        val savedMethodIndex = if (prefs.contains(KEY_METHOD_INDEX)) {
            prefs.getInt(KEY_METHOD_INDEX, defaultDetected.index)
        } else {
            defaultDetected.index
        }

        val savedAsr = if (prefs.contains(KEY_ASR_JURISTIC)) {
            prefs.getInt(KEY_ASR_JURISTIC, defaultDetected.defaultAsrSchool)
        } else {
            PrayerCalculationMethod.fromIndex(savedMethodIndex).defaultAsrSchool
        }

        val savedHijriOffset = prefs.getInt(KEY_HIJRI_OFFSET, 0)
        val savedNotifications = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)

        _settingsState = MutableStateFlow(
            PrayerSettingsState(
                methodIndex = savedMethodIndex,
                asrJuristic = savedAsr,
                hijriOffset = savedHijriOffset,
                notificationsEnabled = savedNotifications,
                isMethodManuallySelected = isManualMethod,
                isAsrManuallySelected = isManualAsr,
                prayerNotificationModes = loadPrayerNotificationModes(),
                prayerAlarmsEnabled = loadPrayerAlarmsEnabled()
            )
        )
        settingsState = _settingsState.asStateFlow()
    }

    private fun loadPrayerNotificationModes(): Map<PrayerType, PrayerNotificationMode> {
        val mainPrayers = listOf(
            PrayerType.FAJR,
            PrayerType.DHUHR,
            PrayerType.ASR,
            PrayerType.MAGHRIB,
            PrayerType.ISHA
        )
        return mainPrayers.associateWith { type ->
            val raw = prefs.getString("notif_mode_${type.name}", PrayerNotificationMode.ADHAN.key)
            PrayerNotificationMode.fromKey(raw)
        }
    }

    private fun loadPrayerAlarmsEnabled(): Map<PrayerType, Boolean> {
        val mainPrayers = listOf(
            PrayerType.FAJR,
            PrayerType.DHUHR,
            PrayerType.ASR,
            PrayerType.MAGHRIB,
            PrayerType.ISHA
        )
        return mainPrayers.associateWith { type ->
            prefs.getBoolean("alarm_enabled_${type.name}", true)
        }
    }

    fun getPrayerNotificationMode(prayerType: PrayerType): PrayerNotificationMode {
        return _settingsState.value.prayerNotificationModes[prayerType]
            ?: PrayerNotificationMode.fromKey(prefs.getString("notif_mode_${prayerType.name}", PrayerNotificationMode.ADHAN.key))
    }

    fun setPrayerNotificationMode(prayerType: PrayerType, mode: PrayerNotificationMode) {
        prefs.edit().putString("notif_mode_${prayerType.name}", mode.key).apply()
        val updatedModes = _settingsState.value.prayerNotificationModes.toMutableMap().apply {
            put(prayerType, mode)
        }
        _settingsState.value = _settingsState.value.copy(prayerNotificationModes = updatedModes)
    }

    fun isPrayerAlarmEnabled(prayerType: PrayerType): Boolean {
        return _settingsState.value.prayerAlarmsEnabled[prayerType]
            ?: prefs.getBoolean("alarm_enabled_${prayerType.name}", true)
    }

    fun setPrayerAlarmEnabled(prayerType: PrayerType, enabled: Boolean) {
        prefs.edit().putBoolean("alarm_enabled_${prayerType.name}", enabled).apply()
        val updatedAlarms = _settingsState.value.prayerAlarmsEnabled.toMutableMap().apply {
            put(prayerType, enabled)
        }
        _settingsState.value = _settingsState.value.copy(prayerAlarmsEnabled = updatedAlarms)
    }

    /**
     * Automatically selects the regional Calculation Method and Asr Juristic school
     * based on the user's location and demography ONLY if the user has not manually locked in a choice.
     */
    fun autoDetectIfNotSelected(latitude: Double, longitude: Double) {
        val current = _settingsState.value
        if (current.isMethodManuallySelected && current.isAsrManuallySelected) {
            return
        }

        val detectedMethod = detectMethodFromDemographyAndCoordinates(latitude, longitude)
        val newMethodIndex = if (current.isMethodManuallySelected) {
            current.methodIndex
        } else {
            detectedMethod.index
        }
        val newAsr = if (current.isAsrManuallySelected) {
            current.asrJuristic
        } else {
            PrayerCalculationMethod.fromIndex(newMethodIndex).defaultAsrSchool
        }

        if (newMethodIndex != current.methodIndex || newAsr != current.asrJuristic) {
            prefs.edit().apply {
                if (!current.isMethodManuallySelected) {
                    putInt(KEY_METHOD_INDEX, newMethodIndex)
                }
                if (!current.isAsrManuallySelected) {
                    putInt(KEY_ASR_JURISTIC, newAsr)
                }
                apply()
            }
            _settingsState.value = current.copy(
                methodIndex = newMethodIndex,
                asrJuristic = newAsr
            )
        }
    }

    /**
     * Called when the user explicitly selects a Calculation Method.
     * Persists the choice and marks it as manually selected so it stays selected permanently.
     */
    fun setManualCalculationMethod(methodIndex: Int) {
        val method = PrayerCalculationMethod.fromIndex(methodIndex)
        val current = _settingsState.value
        val newAsr = if (current.isAsrManuallySelected) {
            current.asrJuristic
        } else {
            method.defaultAsrSchool
        }

        prefs.edit().apply {
            putInt(KEY_METHOD_INDEX, method.index)
            putBoolean(KEY_MANUAL_METHOD, true)
            if (!current.isAsrManuallySelected) {
                putInt(KEY_ASR_JURISTIC, newAsr)
            }
            apply()
        }

        _settingsState.value = current.copy(
            methodIndex = method.index,
            asrJuristic = newAsr,
            isMethodManuallySelected = true
        )
    }

    /**
     * Resets manual calculation method lock and re-detects calculation method based on user's location/region.
     * Preserves manual Asr Juristic selection if the user explicitly set it.
     */
    fun resetToAutoDetect(latitude: Double = 0.0, longitude: Double = 0.0) {
        val current = _settingsState.value
        val detected = detectMethodFromDemographyAndCoordinates(latitude, longitude)
        val effectiveAsr = if (current.isAsrManuallySelected) {
            current.asrJuristic
        } else {
            detected.defaultAsrSchool
        }
        prefs.edit().apply {
            putBoolean(KEY_MANUAL_METHOD, false)
            putInt(KEY_METHOD_INDEX, detected.index)
            if (!current.isAsrManuallySelected) {
                putInt(KEY_ASR_JURISTIC, effectiveAsr)
            }
            apply()
        }
        _settingsState.value = current.copy(
            methodIndex = detected.index,
            asrJuristic = effectiveAsr,
            isMethodManuallySelected = false
        )
    }

    /**
     * Called when the user explicitly selects an Asr Juristic method (0 = Standard, 1 = Hanafi).
     */
    fun setManualAsrJuristic(asrIndex: Int) {
        val validAsr = if (asrIndex == 1) 1 else 0
        prefs.edit().apply {
            putInt(KEY_ASR_JURISTIC, validAsr)
            putBoolean(KEY_MANUAL_ASR, true)
            apply()
        }
        _settingsState.value = _settingsState.value.copy(
            asrJuristic = validAsr,
            isAsrManuallySelected = true
        )
    }

    fun setHijriOffset(offset: Int) {
        val clamped = offset.coerceIn(-2, 2)
        prefs.edit().putInt(KEY_HIJRI_OFFSET, clamped).apply()
        _settingsState.value = _settingsState.value.copy(hijriOffset = clamped)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
        _settingsState.value = _settingsState.value.copy(notificationsEnabled = enabled)
    }

    fun getCurrentSettings(): PrayerSettingsState = _settingsState.value

    companion object {
        private const val PREFS_NAME = "prayer_settings_prefs"
        private const val KEY_METHOD_INDEX = "selected_method_index"
        private const val KEY_ASR_JURISTIC = "selected_asr_juristic"
        private const val KEY_HIJRI_OFFSET = "selected_hijri_offset"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_MANUAL_METHOD = "is_method_manually_selected"
        private const val KEY_MANUAL_ASR = "is_asr_manually_selected"

        fun detectMethodFromDemographyAndCoordinates(
            lat: Double,
            lng: Double
        ): PrayerCalculationMethod {
            val hasCoords = (lat != 0.0 || lng != 0.0) &&
                    !(lat in 21.40..21.45 && lng in 39.80..39.85) // Ignore hardcoded Makkah fallback unless timezone/locale is Saudi

            if (hasCoords) {
                // 1. South Asia & Central Asia (Pakistan, India, Bangladesh, Afghanistan, Sri Lanka, Nepal)
                if (lat in 5.0..43.0 && lng in 60.5..97.0) {
                    return PrayerCalculationMethod.KARACHI
                }
                // 2. Turkey
                if (lat in 35.8..42.2 && lng in 25.5..44.9) {
                    return PrayerCalculationMethod.TURKEY_DIYANET
                }
                // 3. Iran
                if (lat in 25.0..40.0 && lng in 44.0..60.5 && !(lat < 27.0 && lng < 56.5)) {
                    return PrayerCalculationMethod.TEHRAN
                }
                // 4. Saudi Arabia & Yemen
                if (lat in 12.0..32.5 && lng in 34.5..50.5) {
                    return PrayerCalculationMethod.UMM_AL_QURA
                }
                // 5. Gulf Region (UAE, Qatar, Kuwait, Bahrain, Oman)
                if (lat in 16.0..30.5 && lng in 47.0..60.0) {
                    return PrayerCalculationMethod.GULF
                }
                // 6. Egypt, Sudan, Libya, North/East Africa
                if (lat in -5.0..33.0 && lng in 10.0..35.0) {
                    return PrayerCalculationMethod.EGYPTIAN
                }
                // 7. North America (USA, Canada)
                if (lat in 14.0..75.0 && lng in -170.0..-50.0) {
                    return PrayerCalculationMethod.ISNA
                }
                // 8. Southeast Asia (Malaysia, Singapore, Indonesia, Brunei)
                if (lat in -11.5..20.5 && lng in 95.0..141.0) {
                    return PrayerCalculationMethod.SINGAPORE_JAKIM
                }
            }

            // Fallback to device demography: TimeZone + Country Locale
            val tzId = TimeZone.getDefault().id
            val country = Locale.getDefault().country.uppercase(Locale.US)

            return when {
                country in setOf("PK", "IN", "BD", "AF", "LK", "NP", "TJ", "UZ") ||
                        tzId in setOf(
                            "Asia/Karachi",
                            "Asia/Kolkata",
                            "Asia/Calcutta",
                            "Asia/Dhaka",
                            "Asia/Kabul",
                            "Asia/Colombo",
                            "Asia/Kathmandu",
                            "Asia/Tashkent",
                            "Asia/Dushanbe"
                        ) -> PrayerCalculationMethod.KARACHI

                country in setOf("SA", "YE") ||
                        tzId in setOf("Asia/Riyadh", "Asia/Aden") -> PrayerCalculationMethod.UMM_AL_QURA

                country in setOf("AE", "QA", "KW", "BH", "OM") ||
                        tzId in setOf(
                            "Asia/Dubai",
                            "Asia/Qatar",
                            "Asia/Kuwait",
                            "Asia/Bahrain",
                            "Asia/Muscat"
                        ) -> PrayerCalculationMethod.GULF

                country == "TR" || tzId == "Europe/Istanbul" -> PrayerCalculationMethod.TURKEY_DIYANET

                country == "IR" || tzId == "Asia/Tehran" -> PrayerCalculationMethod.TEHRAN

                country in setOf("EG", "SD", "LY") ||
                        tzId in setOf("Africa/Cairo", "Africa/Khartoum", "Africa/Tripoli") -> PrayerCalculationMethod.EGYPTIAN

                country in setOf("US", "CA") ||
                        tzId.startsWith("America/") -> PrayerCalculationMethod.ISNA

                country in setOf("MY", "SG", "ID", "BN") ||
                        tzId in setOf(
                            "Asia/Kuala_Lumpur",
                            "Asia/Singapore",
                            "Asia/Jakarta",
                            "Asia/Makassar",
                            "Asia/Jayapura",
                            "Asia/Brunei"
                        ) -> PrayerCalculationMethod.SINGAPORE_JAKIM

                else -> PrayerCalculationMethod.MWL
            }
        }
    }
}

