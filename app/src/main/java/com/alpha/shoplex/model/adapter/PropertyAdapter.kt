package com.alpha.shoplex.model.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.alpha.shoplex.model.pojo.Property
import com.alpha.shoplex.R

class PropertyAdapter(
    private val properties: ArrayList<Property>,
    private val context: Context
) :
    RecyclerView.Adapter<PropertyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.property_item_row, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = properties[position]

        viewHolder.nameProp.text = item.name
        val inflater = LayoutInflater.from(context)
        for ((index, value) in item.values.withIndex()) {
            val chipItem = inflater.inflate(R.layout.chip_item, null, false) as Chip
            chipItem.id = index + 1995
            chipItem.text = value
            chipItem.setOnClickListener {
                if (properties[position].selectedProperty == chipItem.text.toString())
                    properties[position].selectedProperty = null
                else
                    properties[position].selectedProperty = chipItem.text.toString()
            }
            viewHolder.chipValues.addView(chipItem)
        }
    }

    override fun getItemCount(): Int = properties.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameProp: TextView = view.findViewById(R.id.tvPropName)
        val chipValues: ChipGroup = view.findViewById(R.id.cgPropValue)
    }
}



