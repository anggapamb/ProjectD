package com.projectd.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.base.adapter.CoreListAdapter
import com.crocodic.core.extension.tos
import com.crocodic.core.helper.DateTimeHelper
import com.projectd.R
import com.projectd.base.fragment.BaseFragment
import com.projectd.data.Cons
import com.projectd.data.Session
import com.projectd.data.model.AdditionalMenu
import com.projectd.data.model.HomeMenu
import com.projectd.data.model.Prayer
import com.projectd.data.model.Task
import com.projectd.databinding.FragmentHomeBinding
import com.projectd.databinding.ItemMainMenuBinding
import com.projectd.databinding.ItemSecondMenuBinding
import com.projectd.databinding.ItemUpdateBinding
import com.projectd.service.AudioHelper
import com.projectd.service.fcm.FirebaseMsgService
import com.projectd.ui.dialog.AbsentDialog
import com.projectd.ui.dialog.TaskReportDialog
import com.projectd.ui.task.add.TaskAddFragment
import com.projectd.util.ViewBindingAdapter.Companion.openUrl
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModel()
    private val session: Session by inject()

    private val menus = ArrayList<HomeMenu?>()
    private val listTask = ArrayList<Task?>()
    private val listAdditionalMenu = ArrayList<AdditionalMenu?>()

    private val audioHelper: AudioHelper by inject()
    private var currentPrayer: Prayer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        if (session.getUser() == null) navigateTo(R.id.actionLoginFragment)

        // TODO: ini besok dihapus
        Timber.d("CekUser: ${session.getUser()}")
        binding?.ivAvatar?.setOnClickListener {
            FirebaseMsgService.createNotificationUpdateTask(
                requireContext(),
                "Apakah task anda sudah selesai ?",
                "Silahkan update task anda terlebih dahulu"
            )
//            session.clearAll()
//            requireActivity().tos("Logout")
//            navigateTo(R.id.actionLoginFragment)
        }

        initView()
        initData()
        observe()
        getTaskToday()

        viewModel.allMenus()
        viewModel.getAbsent()

        initPrayer()
        dataDummy()
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.dataTasks.collect {
                        binding?.apply {
                            listTask.clear()
                            rvUpdate.adapter?.notifyDataSetChanged()
                            listTask.addAll(it)
                            rvUpdate.adapter?.notifyItemInserted(0)
                            vEmpty.isVisible = listTask.isEmpty()
                            progressRvUpdate.isVisible = false
                            swipeRefresh.isRefreshing = false
                        }
                    }
                }

                launch {
                    viewModel.dataMenus.collect {
                        binding?.apply {
                            listAdditionalMenu.clear()
                            rvMenuAdditional.adapter?.notifyDataSetChanged()
                            listAdditionalMenu.addAll(it)
                            rvMenuAdditional.adapter?.notifyItemInserted(0)
                            vMenuAdditional.isVisible = it.isNotEmpty()
                        }
                    }
                }

                launch {
                    viewModel.dataAbsent.collect {
                        binding?.absent = it
                    }
                }

                launch {
                    viewModel.prayer.collect {
                        currentPrayer = it
                        binding?.prayer = it

                        invalidateButtonPlay(homeActivity.isPlayPrayer())

                        if (!session.getBoolean(Cons.BUNDLE.PRAYER_COUNT)) {

                            if (homeActivity.isPlayPrayer()) {
                                invalidateButtonPlay()
                            } else {
                                Handler(Looper.getMainLooper()).postDelayed({
                                    homeActivity.playPrayer(it)
                                    invalidateButtonPlay()
                                }, 300)
                            }
                        }
                        /*
                        if (currentPrayer?.like?.contains(viewModel.user?.username) == true) {
                            binding?.btnLikeDoa?.setImageResource(R.drawable.ic_baseline_favorite_24)
                        }
                         */
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

                    if (viewModel.user?.devision == Cons.DIVISION.MANAGER || viewModel.user?.devision == Cons.DIVISION.PSDM) {
                        holder.binding.btnMore.isVisible = true
                    } else if (viewModel.user?.isLeader == "true") {
                        holder.binding.btnMore.isVisible = true
                    }

                    holder.itemView.setOnClickListener {
                        if (data?.idLogin == viewModel.user?.id.toString() && data.load != TaskAddFragment.Companion.LOAD.STANDBY) {
                            if ( data.status != Task.DONE) {
                                TaskReportDialog(data) { getTaskToday() }.show(childFragmentManager, "report")
                            }
                        } else if (viewModel.user?.devision == Cons.DIVISION.MANAGER || viewModel.user?.devision == Cons.DIVISION.PSDM) {
                            val bundle = bundleOf(Cons.BUNDLE.DATA to data)
                            navigateTo(R.id.actionTaskPovFragment, bundle)
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

        binding?.btnPlayDoa?.setOnClickListener {

            if (!session.getBoolean(Cons.BUNDLE.PRAYER_COUNT)) {
                Toast.makeText(requireContext(), "Hope you always pray before.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (binding?.btnPlayDoa?.tag == null) {
                //homeActivity.playPrayer(currentPrayer ?: return@setOnClickListener)
                audioHelper.playMedia(currentPrayer ?: return@setOnClickListener)
                invalidateButtonPlay()
            } else {
                //homeActivity.stopPrayer()
                audioHelper.pauseMedia()
                invalidateButtonPlay(false)
            }
        }

        binding?.swipeRefresh?.setOnRefreshListener {
            viewModel.apply {
                taskToday()
                allMenus()
                getAbsent()
            }
        }
    }

    private fun invalidateButtonPlay(playing: Boolean = true) {
        binding?.btnPlayDoa?.setImageResource(if (playing) R.drawable.ic_baseline_pause_circle_filled_24 else R.drawable.ic_baseline_play_circle_filled_24)
        binding?.btnPlayDoa?.tag = if (playing) true else null
    }

    private fun initPrayer() {

        val date = DateTimeHelper().dateNow()

        if (session.getString(Session.LAST_DATE_SEEK) != date) {
            session.setValue(Session.LAST_DATE_SEEK, date)
            session.setValue(Cons.BUNDLE.PRAYER_SEEK, 0)
            session.setValue(Cons.BUNDLE.PRAYER_COUNT, false)

            Timber.d("reset seek")
        }

        val hour = DateTimeHelper().convert(DateTimeHelper().createAt(), "yyyy-MM-dd HH:mm:ss", "H").toInt()
        if (hour in 8..9) {
            viewModel.preparePrayer()
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