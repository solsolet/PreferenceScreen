package es.ua.eps.preferencescreen

import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen

class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener{

    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?
    ) {
        // Cargar el XML de preferencias
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        // Configurar el EditTextPreference para alpha (solo decimales)
        val alphaPreference = findPreference<EditTextPreference>("text_alpha")
        alphaPreference?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            editText.selectAll()
        }
    }

    // Navigation to "subscreen"
    override fun onPreferenceTreeClick(preference: androidx.preference.Preference): Boolean {
        if (preference is PreferenceScreen) {
            // Crear un nuevo fragment con el rootKey de la sub-pantalla
            val fragment = SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PREFERENCE_ROOT, preference.key)
                }
            }

            // Navegar a la sub-pantalla
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.settings_container, fragment)
                .addToBackStack(preference.key)
                .commit()

            return true
        }
        return super.onPreferenceTreeClick(preference)
    }

    override fun onResume() {
        super.onResume()
        // Registrar listener
        preferenceScreen.sharedPreferences
            ?.registerOnSharedPreferenceChangeListener(this@SettingsFragment)
    }

    override fun onPause() {
        super.onPause()
        // IMPORTANTE: Desregistrar listener para evitar fugas de memoria
        preferenceScreen.sharedPreferences
            ?.unregisterOnSharedPreferenceChangeListener(this@SettingsFragment)
    }

    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences?,
        key: String?
    ) {
        when (key) {
            "dark_mode" -> {
                val darkMode = sharedPreferences?.getBoolean(key, false) ?: false
                applyDarkMode(darkMode)
            }
            "font_size" -> {
                updateFontSizePreference()
            }
        }
    }

    private fun applyDarkMode(enabled: Boolean) {
        // Aplicar tema oscuro
        val mode = if (enabled) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    private fun updateFontSizePreference() {
        val preference = findPreference<ListPreference>("font_size")
        preference?.summary = preference?.entry
    }
}