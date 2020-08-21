package com.nickolay.android2ver2

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.internal.NavigationMenuView
import com.google.android.material.navigation.NavigationView
import com.nickolay.android2ver2.main.CitySelect
import com.nickolay.android2ver2.main.MapFragment
import kotlinx.android.synthetic.main.fragment_bottom_navigation_drawer.*

class BottomNavigationDrawerFragment(val main: MainActivity): BottomSheetDialogFragment() {

    fun showToastMessage(text: CharSequence) {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return  inflater.inflate(R.layout.fragment_bottom_navigation_drawer, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navigationView.menu.findItem(R.id.mi_sing_out).isVisible = main.getSigned()

        navigationView.setNavigationItemSelectedListener { menuItem ->
            // Bottom Navigation Drawer menu item clicks
            when (menuItem.itemId) {
                R.id.mi_city_list -> {// Переопределить список городов
                    main.changeFragment(CitySelect.newInstance(0))
                    this.dismiss()}
                R.id.mi_map -> {// Открываем карту
                    main.changeFragment(MapFragment())
                    this.dismiss()}
                R.id.mi_sing_out -> {//разлогониться
                    main.signOut()
                }
                R.id.mi_email -> showToastMessage(resources.getText(R.string.s_mi_email))
                else -> showToastMessage(menuItem.title)
            }

            true
        }

        val onFeedBackCall: View.OnClickListener = View.OnClickListener {
            showToastMessage("ABOUT")
        }
        tvEmail.setOnClickListener(onFeedBackCall)
        tvName.setOnClickListener(onFeedBackCall)
        ivAvatar.setOnClickListener(onFeedBackCall)

        closeImageview.setOnClickListener {
            this.dismiss()
        }
        disableNavigationViewScrollbars(navigationView)

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener {
            val d = it as BottomSheetDialog

            val bottomSheet = d.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet!!)
            bottomSheetBehavior.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    if (slideOffset > 0.5) {
                        closeImageview.visibility = View.VISIBLE
                    } else {
                        closeImageview.visibility = View.GONE
                    }
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN)
                        dismiss()
//                    when (newState) {
//                        BottomSheetBehavior.STATE_HIDDEN-> dismiss()
//                        //else -> closeImageview.visibility = View.GONE
//                    }
                }
            })
        }

        return dialog
    }

    private fun disableNavigationViewScrollbars(navigationView: NavigationView?) {
        val navigationMenuView = navigationView?.getChildAt(0) as NavigationMenuView
        navigationMenuView.isVerticalScrollBarEnabled = false
    }

}