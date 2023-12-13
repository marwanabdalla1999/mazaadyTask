package com.example.softxpert.petsHomeScreen.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.models.categories.Category
import com.example.domain.models.categories.Data
import com.example.domain.models.categories.categories
import com.example.softxpert.R
import com.example.softxpert.databinding.ItemListBinding


class CategoriesAdapter(private val onItemClickListener: OnItemClickListener , private var cat : List<Category>?) :
    RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

     val data=cat


   /* @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<String>?) {
        if (data != null) {
            this._data = data
           notifyDataSetChanged()

        }

    }*/




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.item_list, parent, false
        )
        return ViewHolder(binding, onItemClickListener)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.name= cat?.get(position)?.slug
        System.out.println("fmdskfmdsklmfkmlsdfklmsmdlkf"+ cat?.get(position)?.slug)

    }

    override fun getItemCount(): Int {
        return cat?.size?:0
    }


    class ViewHolder(
        val binding: ItemListBinding,

        private val onItemClickListener: OnItemClickListener,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onItemClickListener.onItemClicked(adapterPosition)
            }
        }
    }


}