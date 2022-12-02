package com.projectd.util

import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.crocodic.core.helper.DateTimeHelper
import com.projectd.R
import com.projectd.data.model.Absent
import com.projectd.data.model.HomeMenu
import com.projectd.data.model.Project
import com.projectd.data.model.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import kotlin.math.abs

class ViewBindingAdapter {
    companion object {

        fun Activity.openUrl(url: String) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun getEmoji(unicode: Int): String {
            return try {
                String(Character.toChars(unicode))
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }

        @JvmStatic
        @BindingAdapter("imgCircleUrl")
        fun imgCircleUrl(view: ImageView, imgCircleUrl: String?) {
            imgCircleUrl?.let { Glide.with(view.context).load(it).circleCrop().into(view) }
        }

        @JvmStatic
        @BindingAdapter("bgInt")
        fun bgInt(view: View, bgInt: Int?) {
            view.setBackgroundResource(R.drawable.bg_round_12_blue)
            bgInt?.let { view.setBackgroundResource(it) }
        }

        @JvmStatic
        @BindingAdapter("greetingText")
        fun greetingText(view: TextView, greetingText: Boolean?) {
            if (greetingText == true) {
                val hour = DateTimeHelper().convert(DateTimeHelper().createAt(), "yyyy-MM-dd HH:mm:ss", "H").toInt()

                var greeting = ""

                val notes = when {
                    (hour < 11) -> {
                        greeting = "morning"
                        "have a good day! ${getEmoji(0x2615)}"
                    }
                    (hour < 15) -> {
                        greeting = "afternoon"
                        "still on fire? ${getEmoji(0x1F525)}"
                    }
                    (hour < 20) -> {
                        greeting = "evening"
                        "take some rest... ${getEmoji(0x1F3D3)}"
                    }
                    (hour < 23) -> {
                        greeting = "night"
                        "don\'t forget to sleep. ${getEmoji(0x1F634)}"
                    }
                    else -> {
                        greeting = "bye"
                        ""
                    }
                }

                val text1 = "Good $greeting"
                val text2 = ", $notes"
                val textAll = "$text1$text2"

                CoroutineScope(Dispatchers.Main).launch {
                    delay(1000)
                    var text = ""
                    while (text.length < textAll.length) {
                        text += textAll[text.length]
                        view.text = text
                        delay(if (text.length == text1.length) 2000 else 50)
                    }
                }
            }
        }

        @JvmStatic
        @BindingAdapter("textUpdated")
        fun textUpdated(view: TextView, textUpdated: Task?) {
            textUpdated?.let {
                val who = if (it.createdBy == "You") "have" else "has"

                var text = if (it.updatedAt.isNullOrEmpty()) {
                    "$who created new task."
                } else {
                    "$who updated the task."
                }

                view.text = text
            }
        }

        @JvmStatic
        @BindingAdapter("textMenu")
        fun textMenu(view: TextView, textMenu: HomeMenu?) {
            textMenu?.let {
                val unwrappedDrawable = AppCompatResources.getDrawable(view.context, it.icon)
                val textColor = ContextCompat.getColor(view.context, it.color)

                view.text = it.label
                view.setTextColor(textColor)

                unwrappedDrawable?.let { dwable ->
                    val wrappedDrawable = DrawableCompat.wrap(dwable)
                    DrawableCompat.setTint(wrappedDrawable, textColor)
                    view.setCompoundDrawablesWithIntrinsicBounds(it.icon, 0, 0, 0)
                }
            }
        }

        @JvmStatic
        @BindingAdapter("colorIntSrc")
        fun colorIntSrc(view: TextView, colorIntSrc: Int?) {
            var textColor = ContextCompat.getColor(view.context, R.color.text_bg_blue)
            colorIntSrc?.let {
                textColor = ContextCompat.getColor(view.context, it)
            }
            view.setTextColor(textColor)
        }

        @JvmStatic
        @BindingAdapter("absentApproval")
        fun absentApproval(view: TextView, absent: Absent?) {
            absent?.let {
                view.text = when (it.approved) {
                    Absent.APPROVE -> "${getEmoji(0x2705)} Approved by ${it.approvedBy}}"
                    Absent.REJECT -> "${getEmoji(0x274C)} Rejected by ${it.approvedBy}"
                    else -> "Pending Approval"
                }
            }
        }

        @JvmStatic
        @BindingAdapter("imgUrl")
        fun imgUrl(view: ImageView, imgUrl: String?) {
            imgUrl?.let { Glide.with(view.context).load(it).into(view) }
        }

        @JvmStatic
        @BindingAdapter("timeTo")
        fun timeTo(view: TextView, timeTo: String?) {
            timeTo?.let {
                val calendar = Calendar.getInstance().apply { time = DateTimeHelper().toDateTime(it) }
                val timeTarget = calendar.timeInMillis
                val timeCurrent = Calendar.getInstance().timeInMillis
                val timeDifferent = timeTarget - timeCurrent

                var diff = abs(timeDifferent)

                val diffDay = diff / (24 * 60 * 60 * 1000)
                diff -= (diffDay * 24 * 60 * 60 * 1000) //will give you remaining milli seconds relating to hours, minutes and seconds
                val diffHours = diff / (60 * 60 * 1000)
                diff -= (diffHours * 60 * 60 * 1000)
                val diffMinutes = diff / (60 * 1000)
                diff -= (diffMinutes * 60 * 1000)
                val diffSeconds = diff / 1000
                diff -= (diffSeconds * 1000)

                var text = ""

                if (diffDay > 0) {
                    text += "${diffDay}d"
                }

                if (diffHours > 0) {
                    if (diffDay > 0) {
                        text += " "
                    }
                    text += "${diffHours}h"
                }

                if (diffDay > 1 || diffHours > 2) {
                    text = DateTimeHelper().convert(timeTo, "yyyy-MM-dd HH:mm:ss", "HH:mm")
                    view.text = text
                    return@let
                }

                if (diffMinutes > 0) {
                    if (diffHours > 0 || diffDay > 0) {
                        text += " "
                    }
                    text += "${diffMinutes}m"
                }

                /*if (diffSeconds > 0) {
                    if (diffMinutes > 0) {
                        text += " "
                    }
                    text += "${diffSeconds}s"
                }*/

                text += if (timeDifferent > 0) {
                    " remaining"
                } else {
                    " ago"
                }

                if (text.trim().split(" ").size == 1) {
                    text = "just now"
                }

                view.text = text
            }
        }

        @JvmStatic
        @BindingAdapter("taskStatus")
        fun taskStatus(view: TextView, task: Task?) {
            task?.let {
                val text = when (it.status) {
                    Task.DONE -> "Done at ${it.prettyDone()}"
                    Task.HOLD -> "Hold"
                    Task.CANCEL -> "Cancel"
                    else -> "On Going"
                }
                view.text = text
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["webUrl", "progressBar"], requireAll = true)
        fun webUrl(view: WebView, webUrl: String?, progressBar: ProgressBar?) {
            webUrl?.let {
                if (!it.contains("https")) return

                view.webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        progressBar?.progress = newProgress
                        progressBar?.visibility = if (newProgress < 100) View.VISIBLE else View.GONE
                    }
                }

                view.webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                    }

                    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                        return if (request?.url?.authority?.contains("nuryaz", false) == true) {
                            false
                        } else {
                            val intent = Intent(Intent.ACTION_VIEW, request?.url)
                            view?.context?.startActivity(intent)
                            true
                        }
                    }
                }

                view.settings.useWideViewPort = true
                view.settings.loadWithOverviewMode = true

                view.loadUrl(it)
            }
        }

        @JvmStatic
        @BindingAdapter("tintColorInt")
        fun tintColorInt(view: ImageView, tintColorInt: Int?) {
            var color = ContextCompat.getColor(view.context, R.color.text_bg_blue)
            tintColorInt?.let {
                color = ContextCompat.getColor(view.context, it)
            }
            view.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }

        @JvmStatic
        @BindingAdapter("taskLevel")
        fun taskLevel(view: TextView, taskLevel: String?) {
            taskLevel?.let {
                val text = when (it) {
                    Task.HIGH -> "High"
                    Task.MEDIUM -> "Medium"
                    Task.LOW -> "Low"
                    else -> "Standby"
                }
                view.text = text
            }
        }

        @JvmStatic
        @BindingAdapter("textMenuCounter")
        fun textMenuCounter(view: TextView, textMenuCounter: HomeMenu?) {
            textMenuCounter?.let {
                val textColor = ContextCompat.getColor(view.context, it.color)

                view.text = "${it.count}"
                view.setTextColor(textColor)
                view.setBackgroundResource(it.countBackground)
            }
        }

        @JvmStatic
        @BindingAdapter("projectLevel")
        fun projectLevel(view: TextView, projectLevel: String?) {
            projectLevel?.let {
                val text = when (it) {
                    Project.HARD -> "Hard"
                    Project.MEDIUM -> "Medium"
                    else -> "Low"
                }
                view.text = text
            }
        }
    }
}