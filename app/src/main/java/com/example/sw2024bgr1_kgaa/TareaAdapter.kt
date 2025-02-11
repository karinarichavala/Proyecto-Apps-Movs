package com.example.sw2024bgr1_kgaa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TareaAdapter (private var tareas: List<Tarea>) : RecyclerView.Adapter<TareaAdapter.TareaViewHolder>() {
    // Este método es importante para actualizar las tareas en el RecyclerView
    fun setTareas(tareas: List<Tarea>) {
        this.tareas = tareas
        notifyDataSetChanged() // Notifica que los datos han cambiado
    }

    // Crear las vistas para cada elemento de la lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tarea, parent, false)
        return TareaViewHolder(view)
    }

    // Vincular los datos con las vistas
    override fun onBindViewHolder(holder: TareaViewHolder, position: Int) {
        val tarea = tareas[position]
        holder.bind(tarea)
    }

    // Retornar el número de elementos en la lista
    override fun getItemCount(): Int = tareas.size

    // Clase interna para manejar los elementos de cada tarea
    inner class TareaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreTarea: TextView = itemView.findViewById(R.id.tarea_nombre)  // Referencia al nombre de la tarea
        private val horaTarea: TextView = itemView.findViewById(R.id.tarea_hora) // Referencia a la hora de la tarea

        // Vincula los datos de la tarea a los elementos de la vista
        fun bind(tarea: Tarea) {
            nombreTarea.text = tarea.nombre_tarea
            horaTarea.text = "${tarea.hora_inicio} - ${tarea.hora_fin}" // Muestra el rango de hora
        }
    }
}