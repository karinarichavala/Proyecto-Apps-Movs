package com.example.sw2024bgr1_kgaa

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TareaAdapter
    private lateinit var dbHelper: BDHelper

    fun mostrarSnackbar(texto: String) {
        val snack = Snackbar.make(
            findViewById(R.id.main),
            texto,
            Snackbar.LENGTH_INDEFINITE
        )
        snack.show()
    }

    fun actualizarTareas() {
        val tareas = dbHelper.obtenerTareasDelDia()
        adapter.setTareas(tareas)
        adapter.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Configurar el padding del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar la base de datos
        dbHelper = BDHelper(this)
        BaseDeDatos.tablaBD = dbHelper

        // Configurar RecyclerView para las tareas
        recyclerView = findViewById(R.id.recyclerTareas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicializar el adaptador con las tareas del día
        val tareasDelDia = dbHelper.obtenerTareasDelDia()
        adapter = TareaAdapter(tareasDelDia)
        recyclerView.adapter = adapter

        // Configurar el botón de agregar tarea
        val btnAgregarTarea = findViewById<FloatingActionButton>(R.id.btnAgregarTarea)
        btnAgregarTarea.setOnClickListener {
            val taskManager = TaskManager(this)
            taskManager.showAddTaskDialog()
        }

        // Configurar navegación básica
        val btnFamilia = findViewById<View>(R.id.menu_familia)
        btnFamilia?.setOnClickListener {
            startActivity(Intent(this, MiFamiliaActivity::class.java))
        }

        val btnCalendario = findViewById<View>(R.id.menu_calendario)
        btnCalendario?.setOnClickListener {
            startActivity(Intent(this, CalendarioActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        actualizarTareas()
    }

    fun irActividad(clase: Class<*>) {
        startActivity(Intent(this, clase))
    }
}