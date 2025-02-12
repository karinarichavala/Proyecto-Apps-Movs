package com.example.sw2024bgr1_kgaa

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import java.util.*

class TaskManager(private val context: Context) {
    private val dbHelper = BDHelper(context)

    fun showAddTaskDialog() {
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.agregar_tarea, null)

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        // Referencias a los campos del layout
        val edtNombreTarea = dialogView.findViewById<EditText>(R.id.edtNombreTarea)
        val txtFecha = dialogView.findViewById<TextView>(R.id.txtFecha)
        val txtHoraInicio = dialogView.findViewById<TextView>(R.id.txtHoraInicio)
        val txtHoraFin = dialogView.findViewById<TextView>(R.id.txtHoraFin)
        val edtDescripcion = dialogView.findViewById<EditText>(R.id.edtDescripcion)
        val txtAsignar = dialogView.findViewById<TextView>(R.id.txtAsignar)

        // Crear y configurar el Spinner para miembros
        val spinnerMiembros = Spinner(context)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        spinnerMiembros.layoutParams = params

        // Obtener la lista de miembros y configurar el adapter
        val miembros = dbHelper.obtenerMiembrosFamilia()
        val miembrosAdapter = ArrayAdapter(
            context,
            android.R.layout.simple_spinner_item,
            miembros.map { "${it.nombre} - ${it.rol}" }
        )
        miembrosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMiembros.adapter = miembrosAdapter

        // Reemplazar el TextView con el Spinner
        val parentLayout = txtAsignar.parent as LinearLayout
        val index = parentLayout.indexOfChild(txtAsignar)
        parentLayout.removeView(txtAsignar)
        parentLayout.addView(spinnerMiembros, index)

        // Configurar botón de guardar
        dialogView.findViewById<Button>(R.id.btnGuardarTarea).setOnClickListener {
            val taskName = edtNombreTarea.text.toString()
            val taskDate = txtFecha.text.toString()
            val taskStartTime = txtHoraInicio.text.toString()
            val taskEndTime = txtHoraFin.text.toString()
            val taskDescription = edtDescripcion.text.toString()

            if (taskName.isNotEmpty() && taskDate != "Elegir la fecha" && miembros.isNotEmpty()) {
                val selectedMemberPosition = spinnerMiembros.selectedItemPosition
                val selectedMember = miembros[selectedMemberPosition]

                val isSaved = agregarTarea(
                    taskName,
                    taskDate,
                    taskStartTime,
                    taskEndTime,
                    taskDescription,
                    selectedMember.id_miembro
                )

                if (isSaved) {
                    Toast.makeText(
                        context,
                        "Tarea asignada a: ${selectedMember.nombre}",
                        Toast.LENGTH_SHORT
                    ).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(
                        context,
                        "Error al guardar la tarea",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if (miembros.isEmpty()) {
                Toast.makeText(
                    context,
                    "Debe agregar al menos un miembro de familia primero",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    context,
                    "Por favor complete los campos requeridos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Configurar botón de cerrar
        dialogView.findViewById<ImageView>(R.id.btnCerrar).setOnClickListener {
            dialog.dismiss()
        }

        // Configurar selección de fecha
        txtFecha.setOnClickListener {
            showDatePickerDialog(txtFecha)
        }

        // Configurar selección de horas
        txtHoraInicio.setOnClickListener {
            showTimePickerDialog(txtHoraInicio)
        }

        txtHoraFin.setOnClickListener {
            showTimePickerDialog(txtHoraFin)
        }

        dialog.show()
    }

    private fun showTimePickerDialog(textView: TextView) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                textView.text = selectedTime
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }

    private fun showDatePickerDialog(txtFecha: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                txtFecha.text = selectedDate
            },
            year, month, day
        )

        datePickerDialog.show()
    }

    private fun agregarTarea(
        nombre: String,
        fecha: String,
        horaInicio: String,
        horaFin: String,
        descripcion: String,
        idMiembro: Int
    ): Boolean {
        val tarea = Tarea(
            nombre_tarea = nombre,
            fecha = fecha,
            hora_inicio = horaInicio,
            hora_fin = horaFin,
            descripcion = descripcion,
            estado_tarea = "Por hacer"
        )

        val resultado = dbHelper.agregarTarea(tarea, idMiembro)
        if (resultado) {
            // Actualizar la vista principal
            (context as? MainActivity)?.actualizarTareas()
        }
        return resultado
    }
}