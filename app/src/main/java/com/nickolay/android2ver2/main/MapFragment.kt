package com.nickolay.android2ver2.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.textfield.TextInputEditText
import com.nickolay.android2ver2.R
import com.nickolay.android2ver2.model.GlobalViewModel
import com.nickolay.android2ver2.service.CommonWeather
import java.io.IOException

class MapFragment : Fragment(), LocationListener {

    private lateinit var viewModel: GlobalViewModel
    private lateinit var positionMarker: Marker
    private var locationManager: LocationManager? = null
    private lateinit var mMap: GoogleMap

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        positionMarker = mMap.addMarker(
            MarkerOptions().position(LatLng(1.0, 1.0)).title("Текущая позиция")
        )
        mMap.setOnMapLongClickListener { latLng ->
            getAddress(latLng)
            addMarker(latLng)
            CommonWeather.getData(latLng.latitude, latLng.longitude, viewModel)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(activity!!).get(GlobalViewModel::class.java)
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        initSearchByAddress()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        locationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_COARSE

            val provider = locationManager!!.getBestProvider(criteria, true)

            locationManager!!.requestLocationUpdates(provider!!, 10_000, 10F, this)

        }
    }

    override fun onPause() {
        super.onPause()
        locationManager!!.removeUpdates(this)
    }

    override fun onLocationChanged(location: Location) {
        // Переместить метку на текущую позицию
        val currentPosition = LatLng(location.latitude, location.longitude)
        positionMarker.position = currentPosition
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 12f))
    }

    // Получаем адрес по координатам
    private fun getAddress(location: LatLng?) {
        if (location != null)
            Thread {
                try {
                    val addresses =
                        Geocoder(context).getFromLocation(location.latitude, location.longitude, 1)

                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(context, addresses[0].getAddressLine(0), Toast.LENGTH_SHORT).show()
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()
    }

    //    // Добавление меток на карту
    private fun addMarker(location: LatLng?) {
        if (location != null)
            mMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title("Click")
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker))
            )
    }



    private fun initSearchByAddress() {
        val textSearch: TextInputEditText = view?.findViewById(R.id.tietSeachCity)!!
        textSearch.setOnKeyListener { view, p1, keyEvent ->
            if (keyEvent.keyCode == KEYCODE_ENTER){
                val searchText = (view as TextInputEditText).text.toString().trim()
                Thread {
                    try {
                        // Получить координаты по адресу
                        val addresses = Geocoder(context).getFromLocationName(searchText, 1)
                        if (addresses.isNotEmpty()) {
                            CommonWeather.getData(addresses[0].latitude, addresses[0].longitude, viewModel)
                            val location = LatLng(
                                addresses[0].latitude,
                                addresses[0].longitude
                            )
                            Handler(Looper.getMainLooper()).post {
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(location)
                                    .title(searchText)
                                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_search_marker))
                                )
                            mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    location,
                                    15f)
                                )
                            }
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }.start()
            }
            false
        }
    }
}