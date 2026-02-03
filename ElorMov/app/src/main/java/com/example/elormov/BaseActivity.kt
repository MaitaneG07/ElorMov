package com.example.elormov

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

open class BaseActivity : AppCompatActivity() {

    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private var layoutSinConexion: LinearLayout? = null

    private val handler = Handler(Looper.getMainLooper())
    private var runnableSinConexion: Runnable? = null

    private var idiomaOriginal: String? = null

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val lenguaje = prefs.getString("idioma", "es") ?: "es"

        super.attachBaseContext(LocaleHelper.wrap(newBase, lenguaje))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        idiomaOriginal = prefs.getString("idioma", "es")
    }

    override fun onResume() {
        super.onResume()

        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val idiomaActual = prefs.getString("idioma", "es")

        if (idiomaOriginal != idiomaActual) {
            recreate()
            return
        }

        layoutSinConexion = findViewById(R.id.layoutSinConexion)
        inicializarRed()
    }

    override fun onPause() {
        super.onPause()
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun inicializarRed() {
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        networkCallback = object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                runnableSinConexion?.let { handler.removeCallbacks(it) }

                runOnUiThread {
                    if (layoutSinConexion?.visibility == View.VISIBLE) {
                        layoutSinConexion?.visibility = View.GONE
                        Toast.makeText(this@BaseActivity, "Conexión recuperada", Toast.LENGTH_SHORT).show()
                        onConexionRecuperada()
                    }
                }
            }

            override fun onLost(network: Network) {
                runnableSinConexion = Runnable {
                    runOnUiThread {
                        layoutSinConexion?.visibility = View.VISIBLE
                        Toast.makeText(this@BaseActivity, "Se ha perdido la conexión", Toast.LENGTH_LONG).show()
                    }
                }

                handler.postDelayed(runnableSinConexion!!, 1000)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        try {
            connectivityManager.registerNetworkCallback(request, networkCallback)
            verificarEstadoInicial()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun verificarEstadoInicial() {
        val activeNetwork = connectivityManager.activeNetwork
        val caps = connectivityManager.getNetworkCapabilities(activeNetwork)
        val isConnected = caps != null && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

        if (!isConnected) {
            layoutSinConexion?.visibility = View.VISIBLE
        } else {
            layoutSinConexion?.visibility = View.GONE
        }
    }

    open fun onConexionRecuperada() {}
}

object LocaleHelper {
    fun wrap(context: Context, language: String): Context {
        val config = context.resources.configuration
        val sysLocale = config.locales.get(0)

        if (language != "" && !sysLocale.language.equals(language)) {
            val locale = Locale(language)
            Locale.setDefault(locale)
            config.setLocale(locale)
            config.setLayoutDirection(locale)
            return context.createConfigurationContext(config)
        }
        return context
    }
}