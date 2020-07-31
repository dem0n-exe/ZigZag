package com.example.zigzag.ui.camera.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zigzag.R
import java.util.*

class FilterAdapter() :
    RecyclerView.Adapter<FilterAdapter.ViewHolder>() {

    var onItemClick: ((FilterType) -> Unit)? = null
    private var filters = emptyList<FilterType>()

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val filterName: TextView = view.findViewById(R.id.text_filter_name)

        fun bind(filterType: FilterType) {
            filterName.text = filterType.name.toLowerCase(Locale.getDefault()).replace("_", " ")
            view.setOnClickListener {
                onItemClick?.invoke(filters[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_filter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val filterType = filters[position]
        holder.bind(filterType)
    }

    override fun getItemCount() = filters.size

    fun setFiltersList(filtersList: List<FilterType>) {
        filters = filtersList
        notifyDataSetChanged()
    }
}