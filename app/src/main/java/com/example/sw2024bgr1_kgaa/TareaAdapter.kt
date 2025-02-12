package com.example.sw2024bgr1_kgaa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TareaAdapter(private var tareas: List<Tarea>) : RecyclerView.Adapter<TareaAdapter.TareaViewHolder>() {

    fun setTareas(tareas: List<Tarea>) {
        this.tareas = tareas
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tarea, parent, false)
        return TareaViewHolder(view)
    }

    override fun onBindViewHolder(holder: TareaViewHolder, position: Int) {
        val tarea = tareas[position]
        holder.bind(tarea)
    }

    override fun getItemCount(): Int = tareas.size

    inner class TareaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreTarea: TextView = itemView.findViewById(R.id.tarea_nombre)
        private val horaTarea: TextView = itemView.findViewById(R.id.tarea_hora)

        fun bind(tarea: Tarea) {
            nombreTarea.text = tarea.nombre_tarea
            horaTarea.text = "${tarea.hora_inicio} - ${tarea.hora_fin}"
        }
    }
}