package com.starrepublic.meetrix2.mvp

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.starrepublic.meetrix2.injections.AppComponent
import com.starrepublic.meetrix2.utils.getAppComponent

/**
 * Created by richard on 2016-11-08.
 */
abstract class BaseFragment2<V:BaseViewModel,P:BasePresenter2<V>> : Fragment(), BaseViewModel {

    var root: View? = null
    var presenter: P? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        root = inflater!!.inflate(getViewId(), container,false);



        return root;
    }

    abstract fun  getViewId(): Int



    override fun showToast(message: String) {

    }

    override fun showSnackbar(message: String) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        root = null

    }

    override fun onResume() {
        super.onResume()
        if (view != null) {
            presenter = createPresenter(context.getAppComponent())
            presenter?.start(getViewModel())
        }
    }

    abstract fun getViewModel(): V

    abstract fun createPresenter(appComponent: AppComponent): P?

    override fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context,
                permission)== PackageManager.PERMISSION_GRANTED
    }


    override fun onPause() {
        super.onPause()
        presenter = null
    }
}