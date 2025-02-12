package com.example.sw2024bgr1_kgaa

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class CalendarioActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TareaAdapter
    private lateinit var dbHelper: BDHelper
    private var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calendario)

        dbHelper = BDHelper(this)

        // Configurar fecha actual
        val sdf = SimpleDateFormat("d/M/yyyy", Locale("es"))
        selectedDate = sdf.format(Date())

        // Configurar título
        setupHeader()

        // Configurar días del mes
        setupCalendarDays()

        // Configurar RecyclerView
        setupRecyclerView()

        // Configurar botones
        setupButtons()

        // Configurar navegación inferior
        setupBottomNavigation()
    }

    private fun setupHeader() {
        val txtMesAnio = findViewById<TextView>(R.id.txtMesAnio)
        val sdfHeader = SimpleDateFormat("MMMM yyyy", Locale("es"))
        txtMesAnio.text = sdfHeader.format(Date()).capitalize()

        findViewById<ImageView>(R.id.btnAtras).setOnClickListener {
            finish()
        }
    }

    private fun setupCalendarDays() {
        val linearDias = findViewById<LinearLayout>(R.id.linearDias)
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (day in 1..lastDay) {
            val dayView = layoutInflater.inflate(R.layout.item_tarea, linearDias, false).apply {
                findViewById<TextView>(R.id.tarea_nombre).text = day.toString()
                setOnClickListener {
                    calendar.set(Calendar.DAY_OF_MONTH, day)
                    selectedDate = SimpleDateFormat("d/M/yyyy", Locale("es")).format(calendar.time)
                    updateTareas()
                }
            }
            linearDias.addView(dayView)
        }
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerTareas)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TareaAdapter(emptyList())
        recyclerView.adapter = adapter
        updateTareas()
    }

    private fun setupButtons() {
        findViewById<FloatingActionButton>(R.id.btnAgregarTarea).setOnClickListener {
            TaskManager(this).showAddTaskDialog()
        }
    }

    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_inicio -> {
                    finish()
                    true
                }
                R.id.menu_familia -> {
                    // Filtrar tareas por miembro de familia seleccionado
                    showFamilyMemberSelector()
                    true
                }
                R.id.menu_calendario -> {
                    // Ya estamos en el calendario
                    true
                }
                else -> false
            }
        }
    }

    private fun updateTareas() {
        val tareas = dbHelper.obtenerTareasPorFecha(selectedDate)
        adapter.setTareas(tareas)
    }

    private fun showFamilyMemberSelector() {
        val miembros = dbHelper.obtenerMiembrosFamilia()
        val nombresMiembros = miembros.map { it.nombre }.toTypedArray()

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Seleccionar miembro de familia")
            .setItems(nombresMiembros) { _, which ->
                val miembroSeleccionado = miembros[which]
                // Aquí filtrarías las tareas por el miembro seleccionado
                val tareasMiembro = dbHelper.obtenerTareasDelDia().filter { tarea ->
                    // Aquí deberías implementar la lógica para filtrar por miembro
                    true // Por ahora mostramos todas
                }
                adapter.setTareas(tareasMiembro)
            }
            .show()
    }
}