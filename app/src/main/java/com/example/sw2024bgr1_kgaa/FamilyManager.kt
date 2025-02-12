package com.example.sw2024bgr1_kgaa

import android.content.Context
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import java.text.SimpleDateFormat
import java.util.*

class FamilyManager(private val context: Context) {
    private val dbHelper = BDHelper(context)

    fun showAddFamilyMemberDialog() {
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.agregar_familiar, null)

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        val edtNombre = dialogView.findViewById<EditText>(R.id.edtNombreFamiliar)
        val spinnerRol = dialogView.findViewById<Spinner>(R.id.spinnerRol)
        val edtCorreo = dialogView.findViewById<EditText>(R.id.edtCorreoFamiliar)

        // Configurar el spinner con roles predefinidos
        val roles = arrayOf("Padre", "Madre", "Hijo/a", "Abuelo/a", "Otro")
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRol.adapter = adapter

        dialogView.findViewById<android.widget.Button>(R.id.btnAgregarFamiliar).setOnClickListener {
            val nombre = edtNombre.text.toString()
            val rol = spinnerRol.selectedItem.toString()
            val correo = edtCorreo.text.toString()

            if (nombre.isNotEmpty() && correo.isNotEmpty()) {
                val fechaRegistro = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                val miembro = CMiembroFamilia(
                    id_miembro = 0, // La base de datos asignará el ID
                    nombre = nombre,
                    rol = rol,
                    correo_electronico = correo,
                    telefono = "", // Por ahora lo dejamos vacío
                    fecha_registro = fechaRegistro
                )

                val isSaved = agregarMiembroFamilia(miembro)

                if (isSaved) {
                    Toast.makeText(context, "Miembro familiar agregado: $nombre", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(context, "Error al guardar el miembro familiar", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        dialogView.findViewById<android.widget.ImageView>(R.id.btnCerrar).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun agregarMiembroFamilia(miembro: CMiembroFamilia): Boolean {
        return dbHelper.agregarMiembro(miembro)
    }
}