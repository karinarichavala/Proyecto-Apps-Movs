package com.example.sw2024bgr1_kgaa

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class MiFamiliaActivity : AppCompatActivity() {
    private lateinit var familyManager: FamilyManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mi_familia)

        familyManager = FamilyManager(this)

        // Configurar el botón de agregar miembro
        findViewById<MaterialButton>(R.id.btnAgregarMiembro).setOnClickListener {
            familyManager.showAddFamilyMemberDialog()
        }

        // Configurar el botón de retroceso
        findViewById<ImageView>(R.id.btnAtras).setOnClickListener {
            finish()
        }

        // Configurar RecyclerView si existe en el layout
        findViewById<RecyclerView>(R.id.recyclerFamilia)?.let { recyclerView ->
            recyclerView.layoutManager = LinearLayoutManager(this)
            val miembros = BDHelper(this).obtenerMiembrosFamilia()
            val adapter = TareaAdapter(obtenerTareasPorMiembro(miembros))
            recyclerView.adapter = adapter
        }
    }

    private fun obtenerTareasPorMiembro(miembros: List<CMiembroFamilia>): List<Tarea> {
        // Por ahora retornamos una lista vacía o las tareas del día
        return BDHelper(this).obtenerTareasDelDia()
    }
}