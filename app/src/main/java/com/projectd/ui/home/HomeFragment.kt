package com.projectd.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.base.adapter.CoreListAdapter
import com.crocodic.core.data.CoreSession
import com.crocodic.core.extension.tos
import com.crocodic.core.helper.DateTimeHelper
import com.projectd.R
import com.projectd.base.fragment.BaseFragment
import com.projectd.data.Cons
import com.projectd.data.model.*
import com.projectd.databinding.FragmentHomeBinding
import com.projectd.databinding.ItemMainMenuBinding
import com.projectd.databinding.ItemSecondMenuBinding
import com.projectd.databinding.ItemUpdateBinding
import com.projectd.ui.dialog.AbsentDialog
import com.projectd.ui.dialog.TaskReportDialog
import com.projectd.util.ViewBindingAdapter.Companion.openUrl
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by inject()
    private val menus = ArrayList<HomeMenu?>()
    private val listTask = ArrayList<Task?>()
    private val listAdditionalMenu = ArrayList<AdditionalMenu?>()
    private val session: CoreSession by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)

        // TODO: ini besok dihapus
        binding?.ivAvatar?.setOnClickListener {
            session.clearAll()
            requireActivity().tos("Logout")
            navigateTo(R.id.actionLoginFragment)
        }

        initView()
        initData()
        observe()
        getTaskToday()

        viewModel.allMenus()
        viewModel.getAbsent()
        dataDummy()
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.dataTasks.collect {
                        listTask.clear()
                        binding?.rvUpdate?.adapter?.notifyDataSetChanged()
                        listTask.addAll(it)
                        binding?.rvUpdate?.adapter?.notifyItemInserted(0)
                        binding?.vEmpty?.isVisible = listTask.isEmpty()
                        binding?.progressRvUpdate?.isVisible = false
                    }
                }

                launch {
                    viewModel.dataMenus.collect {
                        listAdditionalMenu.clear()
                        binding?.rvMenuAdditional?.adapter?.notifyDataSetChanged()
                        listAdditionalMenu.addAll(it)
                        binding?.rvMenuAdditional?.adapter?.notifyItemInserted(0)
                        binding?.vMenuAdditional?.isVisible = it.isNotEmpty()
                    }
                }

                launch {
                    viewModel.dataAbsent.collect {
                        binding?.absent = it
                    }
                }
            }
        }
    }

    private fun initView() {
        binding?.tvDateNow?.text = DateTimeHelper().datePrettyNow()
        binding?.user = viewModel.user

        listTask.let { tasks ->
            binding?.rvUpdate?.adapter = object : CoreListAdapter<ItemUpdateBinding, Task>(R.layout.item_update) {
                override fun onBindViewHolder(
                    holder: ItemViewHolder<ItemUpdateBinding, Task>,
                    position: Int
                ) {
                    val data = tasks[position]
                    holder.binding.data = data
                    holder.binding.yourName = viewModel.user?.shortName()
                    holder.binding.btnMore.isVisible = (viewModel.user?.devision == Cons.DIVISION.MANAGER)

                    holder.itemView.setOnClickListener {
                        if (data?.createdBy == viewModel.user?.shortName()) {
                            TaskReportDialog(data) { getTaskToday() }.show(childFragmentManager, "report")
                        }
                    }

                    holder.binding.btnMore.setOnClickListener {
                        val moreDialogItems = arrayOf("Verify Task")
                        AlertDialog.Builder(requireContext()).apply {
                            setItems(moreDialogItems) { dialog, which ->
                                dialog.dismiss()
                                when (which) {
                                    0 -> {
                                        if (data?.verified == "0") {
                                            viewModel.verifyTask(data.id.toString(), viewModel.user?.token) { getTaskToday() }
                                        } else {
                                            requireActivity().tos("Task has been verified by ${data?.verifiedBy}")
                                        }
                                    }
                                }
                            }
                        }.show()
                    }

                }
            }.initItem(tasks)
        }
    }

    private fun initData() {
        binding?.rvMenu?.adapter = CoreListAdapter<ItemMainMenuBinding, HomeMenu>(R.layout.item_main_menu)
            .initItem(menus) { _, data ->
                when (data?.key) {
                    "task" -> navigateTo(R.id.actionTaskFragment)
                    "project" -> navigateTo(R.id.actionProjectFragment)
                }
            }

        binding?.rvMenuAdditional?.adapter = CoreListAdapter<ItemSecondMenuBinding, AdditionalMenu>(R.layout.item_second_menu)
            .initItem(listAdditionalMenu) { _, data ->
                when (data?.key) {
                    "today_check" -> navigateTo(R.id.actionTodayFragment)
                    "absent" -> AbsentDialog { viewModel.getAbsent() }.show(childFragmentManager, "absent")
                    else -> requireActivity().openUrl(data?.link!!)
                }
            }
    }

    private fun getTaskToday() {
        viewModel.taskToday()
    }

    private fun dataDummy() {
        /* prayer */
        val prayer = Prayer(
            image = "https://c-fa.cdn.smule.com/rs-s-sg-1/sing_google/performance/cover/db/76/244786ea-1547-4341-95f2-3f0951ac2598.jpg",
            title = "Pak Haji Jamaludin",
            description = "Bismillah"
        )
        binding?.prayer = prayer

        /* homeMenu */
        val homeMenu = arrayOf(
            HomeMenu(
                icon = R.drawable.ic_baseline_playlist_add_check_24,
                color = R.color.text_bg_green,
                background = AppCompatResources.getDrawable(requireContext(), R.drawable.bg_round_12_green),
                label = "Today\'s Task",
                key = "task",
                count = 0,
                countBackground = R.drawable.bg_circle_green
            ),
            HomeMenu(
                icon = R.drawable.ic_baseline_architecture_24,
                color = R.color.text_bg_yellow,
                background = AppCompatResources.getDrawable(requireContext(), R.drawable.bg_round_12_yellow),
                label = "Project",
                key = "project",
                count = 0,
                countBackground = R.drawable.bg_circle_yellow
            )
        )
        menus.clear()
        menus.addAll(homeMenu)
    }
}