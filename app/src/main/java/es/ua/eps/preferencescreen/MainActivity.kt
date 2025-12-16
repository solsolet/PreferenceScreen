package es.ua.eps.preferencescreen

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import es.ua.eps.preferencescreen.databinding.MainActivityBinding
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationView
import kotlin.system.exitProcess
import androidx.core.graphics.toColorInt

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding : MainActivityBinding
    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        // INIT UI
        // Configure Toolbar
        setSupportActionBar(binding.toolbar)
        // Configure Navigation Drawer
        drawerToggle = ActionBarDrawerToggle(
            this@MainActivity,
            binding.drawerLayout,
            binding.toolbar,
            R.string.abrir,
            R.string.cerrar
        )
        binding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this@MainActivity)

        initListeners()
    }

    // Necesario para que funcione el botón hamburguesa
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.nav_main -> {
                // Ya estamos aquí
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
            R.id.nav_settings -> {
                val ir = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(ir)
            }
            else -> super.onOptionsItemSelected(item)
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun initListeners(){
        // MARK: Buttons
        binding.btVisualizar.setOnClickListener {
            actualizarVistaPrevia()
        }
        binding.btCerrar.setOnClickListener { // closes totally the app
            finishAffinity()
            exitProcess(0)
        }
    }

    private fun actualizarVistaPrevia() {
        val texto = binding.etTexto.text.toString()
        if (texto.isEmpty()) {
            binding.tvResultado.text = getString(R.string.introduceTexto)
            return
        }

        // Get Preferences
        val allPrefs = PreferenceManager.getDefaultSharedPreferences(this@MainActivity).all
        val prefs = PreferenceManager.getDefaultSharedPreferences(this@MainActivity)

        // Read PReferences
        allPrefs.forEach {
            Log.d("Preferences", "${it.key} -> ${it.value}")
        }

        val textSize = prefs.getString("font_size", "18")?.toFloatOrNull() ?: 18f
        val textColor = prefs.getString("font_color", "#FF000000") ?: "#FF000000"
        val bgColor = prefs.getString("background_color", "#00000000") ?: "#00000000"
        val isBold = prefs.getBoolean("text_bold", false)
        val isItalic = prefs.getBoolean("text_italic", false)
        val alpha = prefs.getString("text_alpha", "1.0")?.toFloatOrNull() ?: 1.0f
        val rotation = prefs.getInt("text_rotation", 0)

        // Aplicar formato al TextView
        with(binding.tvResultado) {
            text = texto

            // Tamaño
            setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)

            // Colores
            try {
                setTextColor(textColor.toColorInt())
                setBackgroundColor(bgColor.toColorInt())
            } catch (e: IllegalArgumentException) {
                Log.d("error", e.toString())
                // Color inválido, usar valores por defecto
                setTextColor(Color.BLACK)
                setBackgroundColor(Color.TRANSPARENT)
            }

            // Estilo (negrita/cursiva)
            val style = when {
                isBold && isItalic -> Typeface.BOLD_ITALIC
                isBold -> Typeface.BOLD
                isItalic -> Typeface.ITALIC
                else -> Typeface.NORMAL
            }
            setTypeface(typeface, style)

            // Transparencia (alpha) - validar rango
            this.alpha = alpha.coerceIn(0f, 1f)

            // Rotación
            this.rotation = rotation.toFloat()
        }
    }
}