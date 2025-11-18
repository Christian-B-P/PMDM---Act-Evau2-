package com.example.appgestor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ClienteAdapter(
    private var listaClientes: List<Cliente>,
    // AÑADIR ESTOS DOS PARÁMETROS
    private val onClienteClick: (Cliente) -> Unit,
    private val onClienteLongClick: (Cliente) -> Boolean
) : RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder>() {

    class ClienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvCorreo: TextView = itemView.findViewById(R.id.tvCorreo)
        val tvTelefono: TextView = itemView.findViewById(R.id.tvTelefono)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cliente, parent, false)
        return ClienteViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        val cliente = listaClientes[position]
        holder.tvNombre.text = cliente.nombre
        holder.tvCorreo.text = cliente.correo
        holder.tvTelefono.text = cliente.telefono

        // AÑADIR ESTOS LISTENERS
        // Click normal para editar
        holder.itemView.setOnClickListener {
            onClienteClick(cliente)
        }

        // Click largo para eliminar
        holder.itemView.setOnLongClickListener {
            onClienteLongClick(cliente)
        }
    }

    override fun getItemCount(): Int = listaClientes.size

    fun actualizarLista(nuevaLista: List<Cliente>) {
        listaClientes = nuevaLista
        notifyDataSetChanged()
    }
}