package com.starrepublic.meetrix.mvp

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.starrepublic.meetrix.R
import com.starrepublic.meetrix.injections.AppComponent
import com.starrepublic.meetrix.utils.getAppComponent
import timber.log.Timber
import java.util.*

/**
 * Created by richard on 2016-11-08.
 */
abstract class BaseFragment<V : BaseViewModel, P : BasePresenter<V>> : Fragment(), BaseViewModel {

    companion object {
        val REQUEST_GOOGLE_PLAY_SERVICES = 0xFFF0
        val REQUEST_PERMISSIONS = 0xFFF1
    }

    var root: View? = null
    var presenter: P? = null
    private var requiredPermissions: Array<String> = arrayOf()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = createView(inflater, container, savedInstanceState)

        if (root == null) {
            Timber.i("skipping bindings as now binding method is provided...")
            root = inflater.inflate(getViewId(), container, false)
        }

        return root;
    }

    abstract fun createView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View?
    abstract fun getViewId(): Int
    override fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun showSnackbar(message: String) {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        root = null
        presenter = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = createPresenter(context!!.getAppComponent())
    }

    override fun onResume() {
        requiredPermissions = requirePermissions()

        if (requiredPermissions.isEmpty()) {
            presenter?.start(getViewModel())
        } else {
            requestPermissions(*requiredPermissions)
        }


        super.onResume()
    }

    override fun onPause() {
        presenter?.stop()
        super.onPause()
    }

    open fun requirePermissions(): Array<String> {
        return arrayOf()
    }

    abstract fun getViewModel(): V
    abstract fun createPresenter(appComponent: AppComponent): P?
    override fun hasPermission(permission: String): Boolean {
        val info = context?.packageManager?.getPermissionInfo(permission, 0)
        //workaround for checkSelfPermission returns false for "normal permissions" not needing to be requested
        if (info?.protectionLevel == PermissionInfo.PROTECTION_DANGEROUS) {
            return ContextCompat.checkSelfPermission(context!!,
                    permission) == PackageManager.PERMISSION_GRANTED
        }

        return true
    }

    fun requestPermissions(vararg permissions: String) {
        if (permissions.all { hasPermission(it) }) {
            onPermissionsGranted()
            presenter?.start(getViewModel())
        } else {
            requestPermissions(permissions, REQUEST_PERMISSIONS)
            //FragmentCompat.requestPermissions((this as Fragment,permissions, REQUEST_PERMISSIONS);
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSIONS -> {
                if (grantResults.isNotEmpty()) {
                    val allOk: Boolean = !grantResults.any { it == PackageManager.PERMISSION_DENIED }
                    val denied: ArrayList<String> = ArrayList<String>(grantResults.size)

                    if (allOk) {
                        onPermissionsGranted()
                        presenter?.start(getViewModel())
                        return
                    }

                    grantResults.forEachIndexed { index, result ->
                        if (result == PackageManager.PERMISSION_DENIED) {
                            denied.add(permissions[index])
                        }
                    }


                    onPermissionsDenied(denied)
                }
            }
        }
    }

    open fun onPermissionsDenied(permissions: ArrayList<String>) {
        requestPermissions(*permissions.toArray(arrayOf("")))
    }

    open fun onPermissionsGranted() {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_GOOGLE_PLAY_SERVICES -> if (resultCode != Activity.RESULT_OK) {
                onPlayServicesUnavailable()
            } else {
                onPlayServicesInstalled()
            }
        }
    }

    open fun onPlayServicesUnavailable() {
        AlertDialog.Builder(context!!).setMessage(R.string.common_google_play_services_unsupported_text).setCancelable(false).setNeutralButton("OK", { _, _ ->
            activity?.finish()
        }).create().show()
    }

    open fun onPlayServicesInstalled() {
    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(context)
        return connectionStatusCode == ConnectionResult.SUCCESS
    }

    private fun acquireGooglePlayServices() {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(context)
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode)
        }
    }

    fun showGooglePlayServicesAvailabilityErrorDialog(
            connectionStatusCode: Int) {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val dialog = apiAvailability.getErrorDialog(
                activity,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES)
        dialog.show()
    }
}
