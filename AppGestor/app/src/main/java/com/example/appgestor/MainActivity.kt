package com.example.appgestor

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var rvClientes: RecyclerView
    private lateinit var fabAgregarCliente: FloatingActionButton
    private lateinit var clienteAdapter: ClienteAdapter
    private var listaClientes = mutableListOf<Cliente>()
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var searchView: SearchView
    private lateinit var tvContador: TextView

    // AÑADIR ESTAS NUEVAS VARIABLES
    private lateinit var layoutHeaderInstrucciones: LinearLayout
    private lateinit var layoutContenidoInstrucciones: LinearLayout
    private lateinit var tvIconoExpandir: TextView
    private var instruccionesExpandidas = false

    companion object {
        const val EXTRA_CLIENTE_ID = "cliente_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)

        inicializarVistas()
        configurarRecyclerView()
        configurarFAB()
        configurarBusqueda()
        configurarInstrucciones() // AÑADIR ESTA LÍNEA
        cargarClientes()
    }

    override fun onResume() {
        super.onResume()
        cargarClientes()
    }

    private fun inicializarVistas() {
        rvClientes = findViewById(R.id.rvClientes)
        fabAgregarCliente = findViewById(R.id.fabAgregarCliente)
        searchView = findViewById(R.id.searchView)
        tvContador = findViewById(R.id.tvContador)

        // AÑADIR ESTAS LÍNEAS
        layoutHeaderInstrucciones = findViewById(R.id.layoutHeaderInstrucciones)
        layoutContenidoInstrucciones = findViewById(R.id.layoutContenidoInstrucciones)
        tvIconoExpandir = findViewById(R.id.tvIconoExpandir)
    }

    private fun configurarRecyclerView() {
        clienteAdapter = ClienteAdapter(
            listaClientes,
            onClienteClick = { cliente ->
                abrirFormularioEdicion(cliente)
            },
            onClienteLongClick = { cliente ->
                mostrarDialogoEliminar(cliente)
                true
            }
        )
        rvClientes.layoutManager = LinearLayoutManager(this)
        rvClientes.adapter = clienteAdapter
    }

    private fun configurarFAB() {
        fabAgregarCliente.setOnClickListener {
            val intent = Intent(this, FormularioClienteActivity::class.java)
            startActivity(intent)
        }
    }

    private fun configurarBusqueda() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                realizarBusqueda(newText ?: "")
                return true
            }
        })
    }

    // AÑADIR ESTA NUEVA FUNCIÓN
    private fun configurarInstrucciones() {
        layoutHeaderInstrucciones.setOnClickListener {
            if (instruccionesExpandidas) {
                // Colapsar
                layoutContenidoInstrucciones.visibility = View.GONE
                tvIconoExpandir.text = "▼"
                instruccionesExpandidas = false
            } else {
                // Expandir
                layoutContenidoInstrucciones.visibility = View.VISIBLE
                tvIconoExpandir.text = "▲"
                instruccionesExpandidas = true
            }
        }
    }

    private fun realizarBusqueda(textoBusqueda: String) {
        if (textoBusqueda.isEmpty()) {
            cargarClientes()
        } else {
            listaClientes.clear()
            listaClientes.addAll(dbHelper.buscarClientes(textoBusqueda))
            clienteAdapter.actualizarLista(listaClientes)
            actualizarContador()
        }
    }

    private fun cargarClientes() {
        listaClientes.clear()
        listaClientes.addAll(dbHelper.obtenerTodosLosClientes())
        clienteAdapter.actualizarLista(listaClientes)
        actualizarContador()
    }

    private fun actualizarContador() {
        val totalClientes = listaClientes.size
        val totalEnBD = dbHelper.contarClientes()

        if (searchView.query.isEmpty()) {
            tvContador.text = "Total de clientes: $totalEnBD"
        } else {
            tvContador.text = "Clientes encontrados: $totalClientes de $totalEnBD"
        }
    }

    private fun abrirFormularioEdicion(cliente: Cliente) {
        val intent = Intent(this, FormularioClienteActivity::class.java)
        intent.putExtra(EXTRA_CLIENTE_ID, cliente.id)
        startActivity(intent)
    }

    private fun mostrarDialogoEliminar(cliente: Cliente) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Cliente")
            .setMessage("¿Estás seguro de que deseas eliminar a ${cliente.nombre}?")
            .setPositiveButton("Eliminar") { dialog, _ ->
                eliminarCliente(cliente)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }

    private fun eliminarCliente(cliente: Cliente) {
        val filasEliminadas = dbHelper.eliminarCliente(cliente.id)

        if (filasEliminadas > 0) {
            Toast.makeText(
                this,
                "Cliente ${cliente.nombre} eliminado",
                Toast.LENGTH_SHORT
            ).show()
            cargarClientes()
        } else {
            Toast.makeText(
                this,
                "Error al eliminar el cliente",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}