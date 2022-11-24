package com.projectd.ui.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.projectd.R
import com.projectd.base.viewmodel.BaseViewModel
import com.projectd.databinding.DialogManagerChooserBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ManagerChooserDialog: BottomSheetDialogFragment() {

    private var binding: DialogManagerChooserBinding? = null
    private val viewModel: ManagerChooserViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_manager_chooser, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO: get managers user 
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

    }

    class ManagerChooserViewModel: BaseViewModel() {

        override fun onCleared() {
            super.onCleared()
        }

    }

}