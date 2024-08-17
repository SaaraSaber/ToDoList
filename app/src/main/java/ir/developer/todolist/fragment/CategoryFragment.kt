package ir.developer.todolist.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import ir.developer.todolist.R
import ir.developer.todolist.adapter.ManageCategoryAdapter
import ir.developer.todolist.database.AppDataBase
import ir.developer.todolist.databinding.FragmentCategoryBinding
import ir.developer.todolist.datamodel.TabModel
import ir.developer.todolist.global.ClickOnCategory

@SuppressLint("MissingInflatedId")
class CategoryFragment : Fragment(), ClickOnCategory {
    private lateinit var binding: FragmentCategoryBinding
    private lateinit var adapterManageCategory: ManageCategoryAdapter
    private lateinit var listCategory: ArrayList<TabModel>
    private lateinit var dataBase: AppDataBase
    private lateinit var dialogAddCategory: Dialog
    private lateinit var dialogQuestion: Dialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerViewManageCategory()

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnAddCategory.setOnClickListener { dialogAddCategory() }
    }

    private lateinit var enterNewCategory: EditText
    private fun dialogAddCategory() {
        dialogAddCategory = Dialog(requireContext())
        dialogAddCategory.apply {
            setContentView(R.layout.layout_dialog_add_category)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window!!.setGravity(Gravity.CENTER)
            window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val lp = window!!.attributes
            lp.dimAmount = 0.7f

            enterNewCategory = findViewById(R.id.edittext_enter_category)
            val btnSave = findViewById<View>(R.id.btn_save)
            val btnCansel = findViewById<View>(R.id.btnCansel)

            btnCansel.setOnClickListener { dismiss() }
            btnSave.setOnClickListener {
                if (enterNewCategory.text.isNullOrEmpty()) {

                    Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.w_enter_category),
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    addNewCategory()
                    enterNewCategory.text.clear()
                    dismiss()
                }
            }
            show()
        }
    }

    private var idCategory = 0
    private fun addNewCategory() {
        val readData = dataBase.tab().readTabs()
        readData.forEach {
            idCategory = it.id + 1
        }

        dataBase.tab().insertTab(
            TabModel(
                id = idCategory,
                name = enterNewCategory.text.toString(),
                isSelected = false
            )
        )

        adapterManageCategory.differ.submitList(loadData())
        binding.recyclerManageCategory.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = adapterManageCategory
        }

    }

    private fun loadData(): MutableList<TabModel> {
        listCategory.add(
            TabModel(
                id = idCategory,
                name = enterNewCategory.text.toString(),
                isSelected = false
            )
        )
        return listCategory
    }

    private fun initRecyclerViewManageCategory() {
        initListCategory()
        adapterManageCategory = ManageCategoryAdapter(this)
        binding.recyclerManageCategory.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = adapterManageCategory
        }
        adapterManageCategory.differ.submitList(listCategory)
    }

    private fun initListCategory() {
        listCategory = ArrayList()
        dataBase = AppDataBase.getDatabase(requireContext())
        val readData = dataBase.tab().readTabs()

        readData.forEach {
            if (it.name != "همه")
                listCategory.add(it)
        }
    }

    override fun clickOnTab(id: Int, index: Int, name: String) {
        dialogQuestion(index)
    }

    private fun deleteCategory(index: Int) {
        dataBase.tab().deleteTab(
            TabModel(
                id = listCategory[index].id,
                name = listCategory[index].name, isSelected = listCategory[index].isSelected
            )
        )
        listCategory.removeAt(index)
        adapterManageCategory.differ.submitList(listCategory)
        adapterManageCategory.notifyItemRemoved(index)
    }

    private fun dialogQuestion(index: Int) {

        dialogQuestion = Dialog(requireContext())
        dialogQuestion.apply {
            setContentView(R.layout.layout_dialog_question)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window!!.setGravity(Gravity.CENTER)
            window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val lp = window!!.attributes
            lp.dimAmount = 0.7f

            val btnOk = findViewById<MaterialButton>(R.id.btn_ok)
            val btnCansel = findViewById<MaterialButton>(R.id.btn_cansel)
            val textQuestion = findViewById<TextView>(R.id.text_question)

            textQuestion.text = requireContext().getString(R.string.text_question_delete_category)

            btnOk.setOnClickListener {
                dismiss()
                deleteCategory(index)

            }
            btnCansel.setOnClickListener {
                dismiss()
            }

            show()
        }
    }
}