package com.projectd.ui.home

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.crocodic.core.base.adapter.CoreListAdapter
import com.crocodic.core.extension.snack
import com.crocodic.core.helper.DateTimeHelper
import com.projectd.R
import com.projectd.base.fragment.BaseFragment
import com.projectd.data.model.*
import com.projectd.databinding.FragmentHomeBinding
import com.projectd.databinding.ItemMainMenuBinding
import com.projectd.databinding.ItemSecondMenuBinding
import com.projectd.databinding.ItemUpdateBinding
import com.projectd.ui.dialog.AbsentDialog
import com.projectd.util.ViewBindingAdapter.Companion.openUrl
import org.koin.android.ext.android.inject

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by inject()
    private val menus = ArrayList<HomeMenu?>()
    private val addsMenus = ArrayList<AdditionalMenu?>()
    private val listTask = ArrayList<Task?>()
    private var backPressed: Long = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)

        initView()
        observe()
        getTaskToday()
        dataDummy()
        //onBackPressedHandle()
    }

    private fun observe() {
        viewModel.listTask.observe(viewLifecycleOwner) {
            listTask.clear()
            binding?.rvUpdate?.adapter?.notifyDataSetChanged()
            listTask.addAll(it)
            binding?.rvUpdate?.adapter?.notifyItemInserted(0)
            binding?.vEmpty?.isVisible = listTask.isEmpty()
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
                    holder.binding.data = tasks[position]
                    holder.binding.yourName = viewModel.user?.shortName()
                }
            }.initItem(tasks)
        }

        binding?.rvMenu?.adapter = CoreListAdapter<ItemMainMenuBinding, HomeMenu>(R.layout.item_main_menu)
            .initItem(menus) { position, data ->
                when (data?.key) {
                    "task" -> navigateTo(R.id.actionTaskFragment)
                    "project" -> navigateTo(R.id.actionProjectFragment)
                }
            }

        binding?.rvMenuAdditional?.adapter = CoreListAdapter<ItemSecondMenuBinding, AdditionalMenu>(R.layout.item_second_menu)
            .initItem(addsMenus) { position, data ->
                when (data?.key) {
                    "absent" -> AbsentDialog().show(childFragmentManager, "absent")
                    else -> requireActivity().openUrl(data?.link ?: return@initItem)
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

        /* menu */
        val addMenus = arrayOf(
            AdditionalMenu(
                image = "https://cdn-icons-png.flaticon.com/512/2302/2302658.png",
                label = "Absent",
                key = "absent"
            ), AdditionalMenu(
                image = "https://static.thenounproject.com/png/1049455-200.png",
                label = "Today Check",
                key = "today_check",
                link = "https://google.com"
            ), AdditionalMenu(
                image = "https://static.thenounproject.com/png/1049455-200.png",
                label = "Today Check",
                key = "today_check",
                link = "https://google.com"
            ), AdditionalMenu(
                image = "https://static.thenounproject.com/png/1049455-200.png",
                label = "Today Check",
                key = "today_check",
                link = "https://google.com"
            ),
        )
        addsMenus.addAll(addMenus)
        binding?.vMenuAdditional?.isVisible = addsMenus.isNotEmpty()

        /* absent */
        val absent = Absent(description = "Ijin Work From Home")
        binding?.absent = absent
    }

    fun popMsg(msg: String) = binding?.root?.snack(msg)

    private fun onBackPressedHandle() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(requireActivity(), object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (backPressed + 2000 > System.currentTimeMillis()) {
                        activity?.finishAffinity()
                    } else {
                        popMsg("Press back again to quit.")
                    }
                    backPressed = System.currentTimeMillis()
                }
            })
    }
}