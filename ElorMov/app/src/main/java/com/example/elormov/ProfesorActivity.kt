package com.example.elormov

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProfesorActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_profesor)


            //arreglar los botones
            val botonVolver: Button = findViewById(R.id.buttonVolverPerfil)

            botonVolver.setOnClickListener {
                finish()
            }

    }
}