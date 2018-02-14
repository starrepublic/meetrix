package com.starrepublic.meetrix.utils

import android.R
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.annotation.IdRes
import android.support.design.widget.Snackbar
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsets
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import com.starrepublic.meetrix.App
import com.starrepublic.meetrix.injections.AppComponent
import java.util.*

//@Suppress("UNCHECKED_CAST")
//fun <T : View> View.findViewByIdTyped(@IdRes id: Int): T {
//    return findViewById(id) as T
//}
fun DialogFragment.setImmersiveMode(immersive: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        activity?.window?.decorView?.rootView?.setOnApplyWindowInsetsListener { _, insets ->
            val bottomInset = insets.systemWindowInsetBottom

            context?.let {
                if (bottomInset > it.getNavigationBarHeight()) {
                    val screenHeight = it.resources.displayMetrics.heightPixels.toFloat()
                    val statusBarHeight = it.getStatusBarHeight()
                    val availableSpace = screenHeight - (bottomInset - it.getNavigationBarHeight()) - statusBarHeight
                    val window = dialog.window
                    val dialogRootView = window.decorView.findViewById<View>(android.R.id.content)
                    if (availableSpace > dialogRootView.height) {
                        val p = window.attributes
                        val targetOffset = (((availableSpace / 2f)) / (screenHeight))
                        val va = ValueAnimator.ofFloat(0f, -0.5f + targetOffset)
                        va.duration = 300
                        va.interpolator = DecelerateInterpolator()
                        va.addUpdateListener { animation ->
                            val value = animation.animatedValue as Float
                            p.verticalMargin = value
                            window.attributes = p
                        }
                        va.start()
                    }
                }
            }

            insets
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun <T : Fragment> FragmentActivity.addFragment(@IdRes frameId: Int, creator: () -> T): T {
    var fragment: T? = supportFragmentManager.findFragmentById(R
            .id.content) as T?
    if (fragment == null) {
        fragment = creator()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(frameId, fragment)
        transaction.commit()
    }
    return fragment
}

fun ContextCompat.checkSelfPermission(context: Context, permission: String): Boolean {
    return ContextCompat.checkSelfPermission(context,
            permission) == PackageManager.PERMISSION_GRANTED
}

fun Context.getAppComponent(): AppComponent = (applicationContext as App).appComponent!!

fun View.getLayoutInflater(): LayoutInflater = LayoutInflater.from(context)

fun View.showToast(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun View.showSnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
}

fun Context.dpToPx(dp: Float): Int = (this.resources.displayMetrics.density * dp).toInt()

fun Context.getStatusBarHeight(): Int {
    val resources = this.resources
    var result = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = resources.getDimensionPixelSize(resourceId)
    }
    return result
}

fun Context.getNavigationBarHeight(): Int {
    val resources = this.resources
    val id = resources.getIdentifier(
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) "navigation_bar_height" else "navigation_bar_height_landscape",
            "dimen", "android")
    if (id > 0) {
        return resources.getDimensionPixelSize(id)
    }
    return 0
}

fun Context.getActionBarHeight(): Int {
    val styledAttributes = this.theme.obtainStyledAttributes(
            intArrayOf(android.R.attr.actionBarSize))
    val actionBarSize = styledAttributes.getDimension(0, 0f).toInt()
    styledAttributes.recycle()
    return actionBarSize
}

fun Color.parseColor(value: String?): Int {
    if (value == null) {
        return -1
    } else {
        try {
            return Color.parseColor(value)
        } catch (e: IllegalArgumentException) {
            return -1
        }
    }
}

fun Date.millisToString(l: Long): String {
    val h: Long
    val m: Long
    h = l / 3600000
    m = l % 3600000 / 60000
    if (h == 0L) {
        return m.toString() + "m"
    } else {
        return h.toString() + "h " + m + "m"
    }
}

fun Date.dateToRelativeString(strTomorrow: String, strYesterday: String): String {
    var today = Date().time
    today -= today % 86400000
    var due = this.time
    due -= due % 86400000
    val diff = (due - today) / 86400000
    var relative = ""

    if (diff == 1L) {
        relative = strTomorrow + " "
    } else if (diff == -1L) {
        relative = strYesterday + " "
    } else if (diff != 0L) {
        //relative = Constants.DATE_FORMAT_DATE.format(due) + " ";
    }

    return relative
}

fun Date.removeTime(): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.time
}

fun Context.launchAppDetail() {
    val packageName = this.applicationContext.packageName

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.data = Uri.parse("package:" + packageName!!)
        startActivity(intent)
        return
    } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.FROYO) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.setClassName("com.android.settings",
                "com.android.settings.InstalledAppDetails")
        intent.putExtra("pkg", packageName)
        startActivity(intent)
        return
    }
    val intent = Intent(Intent.ACTION_VIEW)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    intent.setClassName("com.android.settings",
            "com.android.settings.InstalledAppDetails")
    intent.putExtra("com.android.settings.ApplicationPkgName", packageName)
    startActivity(intent)
}
