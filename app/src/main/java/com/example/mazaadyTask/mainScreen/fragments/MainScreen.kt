package com.example.mazaadyTask.mainScreen.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope

import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.apiStates.CategoriesApiStates
import com.example.domain.apiStates.OptionsApiStates
import com.example.domain.apiStates.PropertiesApiStates
import com.example.domain.models.properties.Data
import com.example.domain.models.properties.Option
import com.example.mazaadyTask.R
import com.example.mazaadyTask.databinding.MainScreenFragmentBinding
import com.example.mazaadyTask.databinding.PropertiesItemBinding
import com.example.mazaadyTask.mainScreen.adapters.category.CategoriesAdapter
import com.example.mazaadyTask.mainScreen.adapters.properties.PropertiesAdapter
import com.example.mazaadyTask.mainScreen.adapters.options.optionsAdapter
import com.example.mazaadyTask.mainScreen.adapters.subCategory.SubCategoriesAdapter
import com.example.mazaadyTask.mainScreen.viewModels.MainViewModel
import com.example.mazaadyTask.sharedViews.showSnackBar
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainScreen : Fragment(),
    SubCategoriesAdapter.OnSubCatItemClickListener,
    CategoriesAdapter.OnCategoryClickListener,
    PropertiesAdapter.OnPropertyClickListener,
    optionsAdapter.OnOptionClickListener {


    private lateinit var binding: MainScreenFragmentBinding
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var subCategoriesAdapter: SubCategoriesAdapter
    private lateinit var propertiesAdapter: PropertiesAdapter
    private lateinit var optionsAdapter: optionsAdapter
    private lateinit var bottomSheetDialog: BottomSheetDialog

    private lateinit var propertyItemBinding: PropertiesItemBinding
    private lateinit var clickedProperty: Data
    private lateinit var enteredData: HashMap<String, String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = MainScreenFragmentBinding.inflate(layoutInflater)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        observeCategoriesChanges()
        loadCategories()
        observePropertiesChanges()
        observeOptionsChanges()
        binding.cat.setOnClickListener {
            bottomSheetDialog.show()
        }
        binding.next.setOnClickListener {


            val bundle = Bundle()
            bundle.putSerializable("dataHashMap", enteredData)

            findNavController(view).navigate(R.id.action_petsHomeScreen_to_petDetails, bundle)
        }


    }


    //for instantiation
    private fun init() {
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.bottom_sheet)
        enteredData = HashMap()
        bottomSheetDialog.findViewById<EditText>(R.id.search)?.addTextChangedListener { s ->
            if (bottomSheetDialog.findViewById<RecyclerView>(R.id.items)?.adapter == optionsAdapter) {

                optionsAdapter.filter(s.toString())
            }

        }
    }


    private fun observeCategoriesChanges() {
        lifecycleScope.launch {

            mainViewModel.categories.collect {
                when (val result = it) {
                    is CategoriesApiStates.Success -> {
                        categoriesAdapter =
                            CategoriesAdapter(this@MainScreen, result.data?.data?.categories)

                        bottomSheetDialog.findViewById<RecyclerView>(R.id.items)?.adapter =
                            categoriesAdapter


                    }


                    is CategoriesApiStates.Failure -> {


                        showSnackBar(binding.root, result.error.message, loadCategories)


                    }


                    else -> {}
                }
            }


        }
    }

    private fun observePropertiesChanges() {
        lifecycleScope.launch {

            mainViewModel.properties.collect {
                when (val result = it) {
                    is PropertiesApiStates.Success -> {
                        propertiesAdapter = PropertiesAdapter(this@MainScreen, result.data)
                        binding.properties.adapter = propertiesAdapter


                    }


                    is PropertiesApiStates.Failure -> {


                        showSnackBar(binding.root, result.error.message, loadCategories)


                    }

                    else -> {

                    }
                }
            }


        }
    }

    private fun observeOptionsChanges() {
        lifecycleScope.launch {

            mainViewModel.options.collect {
                when (val result = it) {
                    is OptionsApiStates.Success -> {
                        //   setData(result.data?.data)
                        propertiesAdapter = PropertiesAdapter(this@MainScreen, result.data)
                        propertyItemBinding.items.adapter = optionsAdapter
                        if (result.data?.data?.size!! > 0) {
                            propertyItemBinding.items.visibility = View.VISIBLE
                        }
                    }


                    is OptionsApiStates.Failure -> {


                        showSnackBar(binding.root, result.error.message, loadCategories)


                    }


                    else -> {}
                }
            }


        }
    }


    private val loadCategories = {
        mainViewModel.getMainCategories()
    }

    private fun loadProperties(id: Int) {

        mainViewModel.getProperties(id)

    }


    override fun onItemClicked(item: Int) {
        bottomSheetDialog.dismiss()
        binding.cat.text = categoriesAdapter.data?.get(item)?.slug
        enteredData["category"] = categoriesAdapter.data?.get(item)?.slug.toString()
        subCategoriesAdapter =
            SubCategoriesAdapter(this@MainScreen, this.categoriesAdapter.data?.get(item)?.children)
        binding.subCat.setOnClickListener {
            bottomSheetDialog.findViewById<RecyclerView>(R.id.items)?.adapter = subCategoriesAdapter
            bottomSheetDialog.show()
        }

    }

    override fun onSubCatItemClicked(item: Int) {
        bottomSheetDialog.dismiss()
        binding.subCat.text = subCategoriesAdapter.data?.get(item)?.slug
        enteredData["SubCategory"] = subCategoriesAdapter.data?.get(item)?.slug.toString()

        loadProperties(subCategoriesAdapter.data?.get(item)?.id ?: 0)

    }

    override fun onPropertiesClickListener(item: Data?, binding: PropertiesItemBinding) {
        val data = getList(item)
        optionsAdapter = optionsAdapter(
            this@MainScreen, data.options
        )
        bottomSheetDialog.findViewById<RecyclerView>(R.id.items)?.adapter = optionsAdapter
        bottomSheetDialog.show()
        propertyItemBinding = binding
        clickedProperty = data


    }

    private fun getList(item: Data?): Data {
        val list = ArrayList<Option>()
        list.add(Option(false, -1, "اخر", -1, "other"))
        item?.options?.let { list.addAll(it) }
        return Data(
            item?.description ?: "",
            item?.id ?: -1,
            item?.list == true,
            item?.name ?: "",
            list,
            item?.other_value ?: "",
            item?.parent ?: "",
            item?.slug ?: "",
            item?.type ?: "",
            item?.value ?: ""
        )

    }

    override fun onPropertyClickListener(item: Int) {
        val data = clickedProperty.options[item]
        bottomSheetDialog.dismiss()
        propertyItemBinding.name = data.slug
        enteredData[clickedProperty.slug] = data.slug
        if (data.slug == "other") {
            propertyItemBinding.other.visibility = View.VISIBLE
            propertyItemBinding.other.addTextChangedListener { s ->
                enteredData[clickedProperty.slug] = s.toString()
            }
        } else {
            propertyItemBinding.other.visibility = View.GONE
            if (data.child) {
                mainViewModel.getOptions(clickedProperty.options[item].id)
            }
        }

    }


}