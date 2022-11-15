package com.projectd.base.activity

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.crocodic.core.base.activity.CoreActivity
import com.projectd.base.viewmodel.BaseViewModel

open class BaseActivity<VB: ViewDataBinding, VM: BaseViewModel>(@LayoutRes layoutRes: Int): CoreActivity<VB, VM>(layoutRes)