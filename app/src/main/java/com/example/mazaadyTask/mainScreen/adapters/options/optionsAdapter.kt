package com.example.mazaadyTask.mainScreen.adapters.options

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.models.properties.Option
import com.example.mazaadyTask.R
import com.example.mazaadyTask.databinding.ItemListBinding
import java.util.Locale


class optionsAdapter(private val onItemClickListener: OnOptionClickListener, private var property : ArrayList<Option>?) :
    RecyclerView.Adapter<optionsAdapter.ViewHolder>() {

     val data=property

    private val filteredItems: ArrayList<Option>? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.item_list, parent, false
        )
        return ViewHolder(binding, onItemClickListener)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.name= property?.get(position)?.slug

    }

    override fun getItemCount(): Int {

        return filteredItems?.size?:property?.size?:0

    }

    fun filter(query: String) {
        var txt = query
        filteredItems?.clear()
        if (TextUtils.isEmpty(txt)) {
            property?.let { filteredItems?.addAll(it) }
        } else {
            txt = txt.lowercase(Locale.getDefault()).trim { it <= ' ' }
            for (item in property!!) {
                if (item.slug.lowercase(Locale.getDefault()).contains(txt)) {
                    filteredItems?.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }


    class ViewHolder(
        val binding: ItemListBinding,

        private val onItemClickListener: OnOptionClickListener,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onItemClickListener.onPropertyClickListener(adapterPosition)

            }
        }
    }

    interface OnOptionClickListener {

        fun onPropertyClickListener(item: Int)
    }


}