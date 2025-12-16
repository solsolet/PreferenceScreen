package es.ua.eps.preferencescreen

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import es.ua.eps.preferencescreen.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // INIT UI
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.settings_title)

        drawerToggle = ActionBarDrawerToggle(
            this@SettingsActivity,
            binding.drawerLayout,
            binding.toolbar,
            R.string.abrir,
            R.string.cerrar
        )

        binding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this@SettingsActivity)

        // Insert preference fragment
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings_container, SettingsFragment())
                .commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_main -> {
                //finish() // Volver a MainActivity
                val ir = Intent(this@SettingsActivity, MainActivity::class.java)
                startActivity(ir)
            }
            R.id.nav_settings -> {
                // Ya estamos aquí
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}