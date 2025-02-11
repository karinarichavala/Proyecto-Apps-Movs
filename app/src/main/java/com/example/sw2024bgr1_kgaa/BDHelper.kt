package com.example.sw2024bgr1_kgaa

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.icu.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BDHelper(contexto: Context?) : SQLiteOpenHelper(
    contexto,
    "GestorTareasFamiliaDB", // Nombre de la base de datos
    null,
    1
) {
    override fun onCreate(db: SQLiteDatabase?) {
        // Crear tabla Tareas
        val scriptCrearTablaTareas = """
            CREATE TABLE Tareas (
                id_tarea INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre_tarea TEXT NOT NULL,
                fecha TEXT NOT NULL,
                hora_inicio TEXT NOT NULL,
                hora_fin TEXT NOT NULL,
                descripcion TEXT,
                estado_tarea TEXT NOT NULL
            )
        """.trimIndent()
        db?.execSQL(scriptCrearTablaTareas)

        // Crear tabla MiembrosFamilia
        val scriptCrearTablaMiembros = """
            CREATE TABLE MiembrosFamilia (
                id_miembro INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                rol TEXT NOT NULL,
                correo_electronico TEXT,
                telefono TEXT,
                fecha_registro TEXT
            )
        """.trimIndent()
        db?.execSQL(scriptCrearTablaMiembros)

        // Crear tabla Responsables
        val scriptCrearTablaResponsables = """
            CREATE TABLE Responsables (
                id_responsable INTEGER PRIMARY KEY AUTOINCREMENT,
                id_tarea INTEGER NOT NULL,
                id_miembro INTEGER NOT NULL,
                notificaciones_enviadas BOOLEAN NOT NULL,
                FOREIGN KEY (id_tarea) REFERENCES Tareas(id_tarea) ON DELETE CASCADE,
                FOREIGN KEY (id_miembro) REFERENCES MiembrosFamilia(id_miembro) ON DELETE CASCADE
            )
        """.trimIndent()
        db?.execSQL(scriptCrearTablaResponsables)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS Tareas")
        db?.execSQL("DROP TABLE IF EXISTS MiembrosFamilia")
        db?.execSQL("DROP TABLE IF EXISTS Responsables")
        onCreate(db)
    }

    // ---------------- MÉTODOS PARA AGREGAR DATOS ----------------

    // Método para agregar una tarea
    fun agregarTarea(tarea: CTarea, idMiembro: Int): Boolean {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre_tarea", tarea.nombre_tarea)
            put("fecha", tarea.fecha)
            put("hora_inicio", tarea.hora_inicio)
            put("hora_fin", tarea.hora_fin)
            put("descripcion", tarea.descripcion)
            put("estado_tarea", tarea.estado_tarea)
        }

        // Insertar la tarea
        val idTarea = db.insert("Tareas", null, valores)

        // Si la tarea se inserta correctamente, asignamos un responsable
        if (idTarea != -1L) {
            val valoresResponsable = ContentValues().apply {
                put("id_tarea", idTarea)
                put("id_miembro", idMiembro)
                put("notificaciones_enviadas", false)  // Inicializamos como falso
            }
            db.insert("Responsables", null, valoresResponsable)
        }

        db.close()
        return idTarea != -1L
    }

    // Método para agregar un miembro de la familia
    fun agregarMiembro(miembro: CMiembroFamilia): Boolean {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", miembro.nombre)
            put("rol", miembro.rol)
            put("correo_electronico", miembro.correo_electronico)
            put("telefono", miembro.telefono)
            put("fecha_registro", miembro.fecha_registro)
        }
        val idMiembro = db.insert("MiembrosFamilia", null, valores)
        db.close()
        return idMiembro != -1L
    }

    //CONSULTAR
    fun obtenerTareasDelDia(): List<CTarea> {
        val db = readableDatabase
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) // Formato de fecha
        val cursor = db.rawQuery("SELECT * FROM Tareas WHERE fecha = ?", arrayOf(today))
        val tareas = mutableListOf<CTarea>()

        if (cursor.moveToFirst()) {
            do {
                val tarea = CTarea(
                    cursor.getInt(0), // id_tarea
                    cursor.getString(1), // nombre_tarea
                    cursor.getString(2), // fecha
                    cursor.getString(3), // hora_inicio
                    cursor.getString(4), // hora_fin
                    cursor.getString(5), // descripcion
                    cursor.getString(6)  // estado_tarea
                )
                tareas.add(tarea)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return tareas
    }

}
