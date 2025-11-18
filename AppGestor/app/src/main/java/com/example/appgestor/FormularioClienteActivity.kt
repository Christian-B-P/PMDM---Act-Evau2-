package com.example.appgestor

import android.os.Bundle
import android.util.Patterns
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class FormularioClienteActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etEmail: EditText
    private lateinit var etTelefono: EditText
    private lateinit var btnGuardar: Button
    private lateinit var dbHelper: DatabaseHelper

    // AÑADIR ESTA LÍNEA
    private lateinit var tvTituloFormulario: TextView

    private var clienteId: Int = 0
    private var modoEdicion: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_cliente)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        dbHelper = DatabaseHelper(this)

        inicializarVistas()
        verificarModoEdicion()
        configurarBotonGuardar()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // MODIFICAR ESTA FUNCIÓN
    private fun inicializarVistas() {
        etNombre = findViewById(R.id.etNombre)
        etEmail = findViewById(R.id.etEmail)
        etTelefono = findViewById(R.id.etTelefono)
        btnGuardar = findViewById(R.id.btnGuardar)
        tvTituloFormulario = findViewById(R.id.tvTituloFormulario) // AÑADIR ESTA LÍNEA
    }

    // MODIFICAR ESTA FUNCIÓN
    private fun verificarModoEdicion() {
        clienteId = intent.getIntExtra("cliente_id", 0)

        if (clienteId > 0) {
            modoEdicion = true

            // Cambiar el título de la Activity (ActionBar)
            title = "Editar Cliente"

            // AÑADIR ESTA LÍNEA - Cambiar el título en el layout
            tvTituloFormulario.text = "Editar Cliente"

            // Cambiar el texto del botón
            btnGuardar.text = "Actualizar Cliente"

            // Cargar los datos del cliente
            cargarDatosCliente()
        } else {
            modoEdicion = false

            // AÑADIR ESTA LÍNEA - Asegurar que dice "Nuevo Cliente"
            title = "Nuevo Cliente"
            tvTituloFormulario.text = "Nuevo Cliente"
            btnGuardar.text = "Guardar Cliente"
        }
    }

    private fun cargarDatosCliente() {
        val cliente = dbHelper.obtenerClientePorId(clienteId)

        if (cliente != null) {
            etNombre.setText(cliente.nombre)
            etEmail.setText(cliente.correo)
            etTelefono.setText(cliente.telefono)
        } else {
            Toast.makeText(
                this,
                "Error al cargar los datos del cliente",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }

    private fun configurarBotonGuardar() {
        btnGuardar.setOnClickListener {
            if (validarCampos()) {
                if (modoEdicion) {
                    actualizarCliente()
                } else {
                    guardarCliente()
                }
            }
        }
    }

    private fun validarCampos(): Boolean {
        val nombre = etNombre.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()

        if (nombre.isEmpty()) {
            etNombre.error = "El nombre es obligatorio"
            etNombre.requestFocus()
            return false
        }

        if (email.isEmpty()) {
            etEmail.error = "El correo es obligatorio"
            etEmail.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "El formato del correo no es válido"
            etEmail.requestFocus()
            return false
        }

        if (telefono.isEmpty()) {
            etTelefono.error = "El teléfono es obligatorio"
            etTelefono.requestFocus()
            return false
        }

        if (telefono.length < 9) {
            etTelefono.error = "El teléfono debe tener al menos 9 dígitos"
            etTelefono.requestFocus()
            return false
        }

        if (!telefono.matches(Regex("^[0-9]+$"))) {
            etTelefono.error = "El teléfono solo debe contener números"
            etTelefono.requestFocus()
            return false
        }

        return true
    }

    private fun guardarCliente() {
        val nombre = etNombre.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()

        val nuevoCliente = Cliente(0, nombre, email, telefono)
        val id = dbHelper.insertarCliente(nuevoCliente)

        if (id > 0) {
            Toast.makeText(
                this,
                "Cliente guardado exitosamente",
                Toast.LENGTH_SHORT
            ).show()
            limpiarCampos()
            finish()
        } else {
            Toast.makeText(
                this,
                "Error al guardar el cliente",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun actualizarCliente() {
        val nombre = etNombre.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()

        val clienteActualizado = Cliente(clienteId, nombre, email, telefono)
        val filasActualizadas = dbHelper.actualizarCliente(clienteActualizado)

        if (filasActualizadas > 0) {
            Toast.makeText(
                this,
                "Cliente actualizado exitosamente",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        } else {
            Toast.makeText(
                this,
                "Error al actualizar el cliente",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun limpiarCampos() {
        etNombre.text.clear()
        etEmail.text.clear()
        etTelefono.text.clear()
        etNombre.requestFocus()
    }
}