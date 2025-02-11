package com.example.sw2024bgr1_kgaa

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    fun mostrarSnackbar(texto:String){
        val snack = Snackbar.make(
            findViewById(R.id.main),
            texto,
            Snackbar.LENGTH_INDEFINITE
        )
        snack.show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Inicializar la base de datos en la primera actividad
        BaseDeDatos.tablaBD = BDHelper(this)

        //Tareas en el recycler view
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerTareas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Obtener las tareas del día de hoy desde la base de datos
        val tareasDelDia = BDHelper(this).obtenerTareasDelDia()

        // Configurar el adapter para el RecyclerView
        val adapter = TareaAdapter(tareasDelDia)
        recyclerView.adapter = adapter

    }

    fun irActividad(clase:Class<*>){
        startActivity(Intent(this, clase))
    }
}