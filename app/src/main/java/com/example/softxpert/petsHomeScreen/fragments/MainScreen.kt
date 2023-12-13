package com.example.softxpert.petsHomeScreen.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.domain.apiStates.CategoriesApiStates
import com.example.domain.apiStates.PetsApiStates
import com.example.softxpert.databinding.PetsHomeScreenFragmentBinding
import com.example.softxpert.petsHomeScreen.adapters.OnItemClickListener
import com.example.softxpert.petsHomeScreen.adapters.PetsAdapter
import com.example.softxpert.petsHomeScreen.viewModels.PetsViewModel
import com.example.softxpert.sharedViews.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PetsHomeScreen : Fragment(), View.OnClickListener ,OnItemClickListener{
    private lateinit var binding: PetsHomeScreenFragmentBinding
    private var currentType: String = ""
    private val petsViewModel: PetsViewModel by viewModels()
    private lateinit var adapter: PetsAdapter
    private var lastPage = 1
    private var currentPage = 1
    private var loading = false
    private lateinit var prevTypeView: TextView
    private  var petModels:List<Pets>?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = PetsHomeScreenFragmentBinding.inflate(layoutInflater)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        observePetsListChanges()
        observeMorePets()

    }

    private fun observeMorePets() {
        lifecycleScope.launch {
            petsViewModel.morePets.collect {
                loading = false
                when (val result = it) {
                    is CategoriesApiStates.Success -> {
                        petModels=result.data?.pets
                        adapter.addData(petModels)
                        lastPage = result.data?.pagination?.total_pages ?: 1
                        currentPage = result.data?.pagination?.current_page ?: 1
                    }

                    is CategoriesApiStates.Failure -> {
                        showSnackBar(binding.root, result.error.message, loadPets)
                        adapter.setData(result.offlineData?.pets)


                    }

                    is CategoriesApiStates.Idle -> {

                    }

                    is CategoriesApiStates.Loading -> {

                    }
                }
            }
        }
    }

    //for instantiation
    private fun init() {
        adapter = PetsAdapter(this)
        binding.petsAdapter = adapter


    }

    private fun loadMorePets() {

        petsViewModel.loadMorePets(currentPage, lastPage, currentType)

    }

    private fun observePetsListChanges() {
        lifecycleScope.launch {

            petsViewModel.pets.collect {
                when (val result = it) {
                    is PetsApiStates.Success -> {


                        adapter.setData(result.data?.pets)
                        lastPage = result.data?.pagination?.total_pages ?: 1
                        currentPage = result.data?.pagination?.current_page ?: 1
                    }

                    is PetsApiStates.Failure -> {


                        showSnackBar(binding.root, result.error.message, loadPets)
                        adapter.setData(result.offlineData?.pets)


                    }

                    is PetsApiStates.Idle -> {

                    }

                    is PetsApiStates.Loading -> {

                    }
                }
            }


        }
    }

    private val loadPets = {
        petsViewModel.getPets(currentType)
    }


    override fun onClick(p0: View?) {

    }

    override fun onItemClicked(item: Int) {



    }

}