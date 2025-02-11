package com.example.sw2024bgr1_kgaa

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.sw2024bgr1_kgaa.R
import android.widget.Button
import android.widget.TextView
import android.widget.ImageView
import java.util.*

class TaskManager(private val context: Context) {

    private val dbHelper = BDHelper(context)  // Instancia de BDHelper para gestionar la base de datos

    // Método para mostrar el diálogo y agregar la tarea
    // Dentro de TaskManager
    // Método actualizado para agregar tarea, que incluye tanto la selección de fecha como de hora
    fun showAddTaskDialog() {
        // Inflamos el layout del diálogo
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.agregar_tarea, null)

        // Creamos el AlertDialog sin título ni botón de guardar
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)  // Solo seteamos el layout sin título ni botón
            .setCancelable(true)  // Permitir que se cierre al tocar fuera
            .create()

        // Referencias a los campos del layout
        val edtNombreTarea = dialogView.findViewById<EditText>(R.id.edtNombreTarea)
        val txtFecha = dialogView.findViewById<TextView>(R.id.txtFecha)
        val txtHoraInicio = dialogView.findViewById<TextView>(R.id.txtHoraInicio)
        val txtHoraFin = dialogView.findViewById<TextView>(R.id.txtHoraFin)
        val edtDescripcion = dialogView.findViewById<EditText>(R.id.edtDescripcion)

        // Acción del botón "Crear tarea" dentro del layout
        dialogView.findViewById<Button>(R.id.btnGuardarTarea).setOnClickListener {
            val taskName = edtNombreTarea.text.toString()
            val taskDate = txtFecha.text.toString()
            val taskStartTime = txtHoraInicio.text.toString()
            val taskEndTime = txtHoraFin.text.toString()
            val taskDescription = edtDescripcion.text.toString()

            // Verificamos que el campo Nombre no esté vacío
            if (taskName.isNotEmpty()) {
                // Aquí llamamos al método de la base de datos para agregar la tarea
                val isSaved = agregarTarea(taskName, taskDate, taskStartTime, taskEndTime, taskDescription, 1)  // Pasamos el ID del miembro

                if (isSaved) {
                    // Si la tarea se guardó correctamente, mostramos un mensaje
                    Toast.makeText(context, "Tarea guardada: $taskName", Toast.LENGTH_SHORT).show()

                } else {
                    // Si no se guardó, mostramos un mensaje de error
                    Toast.makeText(context, "Error al guardar la tarea", Toast.LENGTH_SHORT).show()
                }
                // Cerramos el diálogo una vez que la tarea se haya agregado
                dialog.dismiss()
            } else {
                // Mostrar mensaje si no se ha ingresado el nombre de la tarea
                Toast.makeText(context, "Por favor ingrese el nombre de la tarea", Toast.LENGTH_SHORT).show()
            }
        }

        // Acción del botón de cancelar
        dialogView.findViewById<ImageView>(R.id.btnCerrar).setOnClickListener {
            dialog.dismiss() // Solo cerramos el diálogo
        }

        // Mostramos el diálogo
        dialog.show()

        // Acción de clic en el TextView para elegir la fecha
        txtFecha.setOnClickListener {
            showDatePickerDialog(txtFecha)
        }

        // Acción de clic en el TextView para seleccionar la hora de inicio
        txtHoraInicio.setOnClickListener {
            showTimePickerDialog(txtHoraInicio)
        }

        // Acción de clic en el TextView para seleccionar la hora de fin
        txtHoraFin.setOnClickListener {
            showTimePickerDialog(txtHoraFin)
        }
    }

    private fun showTimePickerDialog(txtHoraInicio: TextView?) {
        // Obtén la hora actual
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // Crea el TimePickerDialog
        val timePickerDialog = TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                // Establece la hora seleccionada en el TextView
                val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                txtHoraInicio?.text = selectedTime
            },
            hour,
            minute,
            false // Usar formato de 24 horas (cambia a true para 12 horas)
        )

        // Muestra el TimePickerDialog
        timePickerDialog.show()
    }



    // Método para agregar tarea a la base de datos
    fun agregarTarea(nombre: String, fecha: String, horaInicio: String, horaFin: String, descripcion: String, idMiembro: Int): Boolean {
        // Creamos la tarea con el estado por defecto "Por hacer"
        val tarea = Tarea(
            nombre_tarea = nombre,
            fecha = fecha,
            hora_inicio = horaInicio,
            hora_fin = horaFin,
            descripcion = descripcion
        )

        // Usamos el método agregarTarea de BDHelper (usando la instancia existente de dbHelper)
        val resultado = dbHelper.agregarTarea(tarea, idMiembro)
        return resultado
    }

    // Método para mostrar el DatePickerDialog
    private fun showDatePickerDialog(txtFecha: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Crear el DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            context,
            { view, selectedYear, selectedMonth, selectedDay ->
                // Mostrar la fecha seleccionada en el TextView
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                txtFecha.text = selectedDate
            },
            year, month, day
        )

        // Mostrar el DatePickerDialog
        datePickerDialog.show()
    }

}
