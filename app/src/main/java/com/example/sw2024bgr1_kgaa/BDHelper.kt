package com.example.sw2024bgr1_kgaa

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.icu.text.SimpleDateFormat
import java.util.*

class BDHelper(contexto: Context?) : SQLiteOpenHelper(
    contexto,
    "GestorTareasFamiliaDB",
    null,
    1
) {
    override fun onCreate(db: SQLiteDatabase?) {
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

    // Métodos para Tareas
    fun agregarTarea(tarea: Tarea, idMiembro: Int): Boolean {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre_tarea", tarea.nombre_tarea)
            put("fecha", tarea.fecha)
            put("hora_inicio", tarea.hora_inicio)
            put("hora_fin", tarea.hora_fin)
            put("descripcion", tarea.descripcion)
            put("estado_tarea", tarea.estado_tarea)
        }

        val idTarea = db.insert("Tareas", null, valores)

        if (idTarea != -1L) {
            val valoresResponsable = ContentValues().apply {
                put("id_tarea", idTarea)
                put("id_miembro", idMiembro)
                put("notificaciones_enviadas", false)
            }
            val idResponsable = db.insert("Responsables", null, valoresResponsable)
            db.close()
            return idResponsable != -1L
        }

        db.close()
        return false
    }

    fun obtenerTareasDelDia(): List<Tarea> {
        val db = readableDatabase
        val fechaActual = SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(Date())

        val cursor = db.query(
            "Tareas",
            null,
            "fecha = ?",
            arrayOf(fechaActual),
            null,
            null,
            "hora_inicio ASC"
        )

        val tareas = mutableListOf<Tarea>()

        if (cursor.moveToFirst()) {
            do {
                val tarea = Tarea(
                    id_tarea = cursor.getInt(cursor.getColumnIndexOrThrow("id_tarea")),
                    nombre_tarea = cursor.getString(cursor.getColumnIndexOrThrow("nombre_tarea")),
                    fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
                    hora_inicio = cursor.getString(cursor.getColumnIndexOrThrow("hora_inicio")),
                    hora_fin = cursor.getString(cursor.getColumnIndexOrThrow("hora_fin")),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                    estado_tarea = cursor.getString(cursor.getColumnIndexOrThrow("estado_tarea"))
                )
                tareas.add(tarea)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return tareas
    }

    // Métodos para Miembros de Familia
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

    fun obtenerMiembrosFamilia(): List<CMiembroFamilia> {
        val db = readableDatabase
        val miembros = mutableListOf<CMiembroFamilia>()

        val cursor = db.query(
            "MiembrosFamilia",
            null,
            null,
            null,
            null,
            null,
            "nombre ASC"
        )

        if (cursor.moveToFirst()) {
            do {
                val miembro = CMiembroFamilia(
                    id_miembro = cursor.getInt(cursor.getColumnIndexOrThrow("id_miembro")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    rol = cursor.getString(cursor.getColumnIndexOrThrow("rol")),
                    correo_electronico = cursor.getString(cursor.getColumnIndexOrThrow("correo_electronico")),
                    telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono")),
                    fecha_registro = cursor.getString(cursor.getColumnIndexOrThrow("fecha_registro"))
                )
                miembros.add(miembro)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return miembros
    }

    fun obtenerTareasPorMiembro(idMiembro: Int): List<Tarea> {
        val db = readableDatabase
        val tareas = mutableListOf<Tarea>()

        val query = """
            SELECT t.* FROM Tareas t
            INNER JOIN Responsables r ON t.id_tarea = r.id_tarea
            WHERE r.id_miembro = ?
            ORDER BY t.fecha ASC, t.hora_inicio ASC
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(idMiembro.toString()))

        if (cursor.moveToFirst()) {
            do {
                val tarea = Tarea(
                    id_tarea = cursor.getInt(cursor.getColumnIndexOrThrow("id_tarea")),
                    nombre_tarea = cursor.getString(cursor.getColumnIndexOrThrow("nombre_tarea")),
                    fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
                    hora_inicio = cursor.getString(cursor.getColumnIndexOrThrow("hora_inicio")),
                    hora_fin = cursor.getString(cursor.getColumnIndexOrThrow("hora_fin")),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                    estado_tarea = cursor.getString(cursor.getColumnIndexOrThrow("estado_tarea"))
                )
                tareas.add(tarea)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return tareas
    }

    fun obtenerTareasPorFechaYMiembro(fecha: String, idMiembro: Int): List<Tarea> {
        val db = readableDatabase
        val tareas = mutableListOf<Tarea>()

        val query = """
            SELECT t.* FROM Tareas t
            INNER JOIN Responsables r ON t.id_tarea = r.id_tarea
            WHERE t.fecha = ? AND r.id_miembro = ?
            ORDER BY t.hora_inicio ASC
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(fecha, idMiembro.toString()))

        if (cursor.moveToFirst()) {
            do {
                val tarea = Tarea(
                    id_tarea = cursor.getInt(cursor.getColumnIndexOrThrow("id_tarea")),
                    nombre_tarea = cursor.getString(cursor.getColumnIndexOrThrow("nombre_tarea")),
                    fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
                    hora_inicio = cursor.getString(cursor.getColumnIndexOrThrow("hora_inicio")),
                    hora_fin = cursor.getString(cursor.getColumnIndexOrThrow("hora_fin")),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                    estado_tarea = cursor.getString(cursor.getColumnIndexOrThrow("estado_tarea"))
                )
                tareas.add(tarea)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return tareas
    }

    fun obtenerTareasPorFecha(fecha: String): List<Tarea> {
        val db = readableDatabase
        val cursor = db.query(
            "Tareas",
            null,
            "fecha = ?",
            arrayOf(fecha),
            null,
            null,
            "hora_inicio ASC"
        )

        val tareas = mutableListOf<Tarea>()

        if (cursor.moveToFirst()) {
            do {
                val tarea = Tarea(
                    id_tarea = cursor.getInt(cursor.getColumnIndexOrThrow("id_tarea")),
                    nombre_tarea = cursor.getString(cursor.getColumnIndexOrThrow("nombre_tarea")),
                    fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
                    hora_inicio = cursor.getString(cursor.getColumnIndexOrThrow("hora_inicio")),
                    hora_fin = cursor.getString(cursor.getColumnIndexOrThrow("hora_fin")),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                    estado_tarea = cursor.getString(cursor.getColumnIndexOrThrow("estado_tarea"))
                )
                tareas.add(tarea)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return tareas
    }
}
