package com.example.appgestor

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "clientes.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_CLIENTES = "clientes"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NOMBRE = "nombre"
        private const val COLUMN_CORREO = "correo"
        private const val COLUMN_TELEFONO = "telefono"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
            CREATE TABLE $TABLE_CLIENTES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOMBRE TEXT NOT NULL,
                $COLUMN_CORREO TEXT NOT NULL,
                $COLUMN_TELEFONO TEXT NOT NULL
            )
        """.trimIndent()

        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CLIENTES")
        onCreate(db)
    }

    fun insertarCliente(cliente: Cliente): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, cliente.nombre)
            put(COLUMN_CORREO, cliente.correo)
            put(COLUMN_TELEFONO, cliente.telefono)
        }

        val id = db.insert(TABLE_CLIENTES, null, values)
        db.close()
        return id
    }

    fun obtenerTodosLosClientes(): List<Cliente> {
        val listaClientes = mutableListOf<Cliente>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_CLIENTES ORDER BY $COLUMN_NOMBRE ASC"
        val cursor: Cursor? = db.rawQuery(query, null)

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID))
                    val nombre = it.getString(it.getColumnIndexOrThrow(COLUMN_NOMBRE))
                    val correo = it.getString(it.getColumnIndexOrThrow(COLUMN_CORREO))
                    val telefono = it.getString(it.getColumnIndexOrThrow(COLUMN_TELEFONO))

                    val cliente = Cliente(id, nombre, correo, telefono)
                    listaClientes.add(cliente)
                } while (it.moveToNext())
            }
        }

        db.close()
        return listaClientes
    }

    fun obtenerClientePorId(id: Int): Cliente? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_CLIENTES,
            arrayOf(COLUMN_ID, COLUMN_NOMBRE, COLUMN_CORREO, COLUMN_TELEFONO),
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        var cliente: Cliente? = null
        cursor?.use {
            if (it.moveToFirst()) {
                val idCliente = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID))
                val nombre = it.getString(it.getColumnIndexOrThrow(COLUMN_NOMBRE))
                val correo = it.getString(it.getColumnIndexOrThrow(COLUMN_CORREO))
                val telefono = it.getString(it.getColumnIndexOrThrow(COLUMN_TELEFONO))

                cliente = Cliente(idCliente, nombre, correo, telefono)
            }
        }

        db.close()
        return cliente
    }

    fun actualizarCliente(cliente: Cliente): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, cliente.nombre)
            put(COLUMN_CORREO, cliente.correo)
            put(COLUMN_TELEFONO, cliente.telefono)
        }

        val filasActualizadas = db.update(
            TABLE_CLIENTES,
            values,
            "$COLUMN_ID = ?",
            arrayOf(cliente.id.toString())
        )

        db.close()
        return filasActualizadas
    }

    fun eliminarCliente(id: Int): Int {
        val db = this.writableDatabase
        val filasEliminadas = db.delete(
            TABLE_CLIENTES,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )

        db.close()
        return filasEliminadas
    }

    fun contarClientes(): Int {
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_CLIENTES"
        val cursor = db.rawQuery(query, null)

        var count = 0
        cursor?.use {
            if (it.moveToFirst()) {
                count = it.getInt(0)
            }
        }

        db.close()
        return count
    }

    // AÑADIR ESTA NUEVA FUNCIÓN AL FINAL
    fun buscarClientes(textoBusqueda: String): List<Cliente> {
        val listaClientes = mutableListOf<Cliente>()
        val db = this.readableDatabase

        // Búsqueda por nombre o correo (LIKE para búsqueda parcial)
        val query = """
            SELECT * FROM $TABLE_CLIENTES 
            WHERE $COLUMN_NOMBRE LIKE ? OR $COLUMN_CORREO LIKE ?
            ORDER BY $COLUMN_NOMBRE ASC
        """.trimIndent()

        val searchPattern = "%$textoBusqueda%"
        val cursor: Cursor? = db.rawQuery(query, arrayOf(searchPattern, searchPattern))

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID))
                    val nombre = it.getString(it.getColumnIndexOrThrow(COLUMN_NOMBRE))
                    val correo = it.getString(it.getColumnIndexOrThrow(COLUMN_CORREO))
                    val telefono = it.getString(it.getColumnIndexOrThrow(COLUMN_TELEFONO))

                    val cliente = Cliente(id, nombre, correo, telefono)
                    listaClientes.add(cliente)
                } while (it.moveToNext())
            }
        }

        db.close()
        return listaClientes
    }
}