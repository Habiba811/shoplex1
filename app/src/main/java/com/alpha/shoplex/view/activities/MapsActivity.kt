package com.alpha.shoplex.view.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.alpha.shoplex.R
import com.alpha.shoplex.databinding.ActivityMapsBinding
import com.alpha.shoplex.model.enumurations.LocationAction
import com.alpha.shoplex.model.extra.UserInfo
import com.alpha.shoplex.model.maps.LocationManager

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val REQUEST_CODE = 101
        const val LOCATION_ACTION = "LOCATION_ACTION"
        const val ADDRESS = "ADDRESS"
        const val LOCATION = "LOCATION"
        const val STORE_LOCATIONS = "STORE_LOCATIONS"
        const val STORE_ADDRESSES = "STORE_ADDRESSES"
    }

    private lateinit var mGoogleMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager: LocationManager
    private var currentLocation: Location? = null
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private lateinit var storeName: String
    private lateinit var locationAction: LocationAction
    private lateinit var storeLocations: ArrayList<LatLng>
    private lateinit var storeAddresses: ArrayList<String>
    private lateinit var location: com.alpha.shoplex.model.pojo.Location

    override fun onCreate(savedInstanceState: Bundle?) {
        if (UserInfo.lang != this.resources.configuration.locale.language)
            UserInfo.setLocale(UserInfo.lang, this)
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestPermission()
        when {
            intent.getStringExtra(LOCATION_ACTION) == LocationAction.Add.toString() -> {
                locationAction = LocationAction.Add
            }
            intent.getStringExtra(LOCATION_ACTION) == LocationAction.ShowStores.toString() -> {
                locationAction = LocationAction.ShowStores
                storeLocations =
                    intent.getParcelableArrayListExtra<Location>(STORE_LOCATIONS) as ArrayList<LatLng>
                storeAddresses = intent.getStringArrayListExtra(STORE_ADDRESSES)!!
                binding.btnOK.text = getString(R.string.OK)
            }
            intent.getStringExtra(LOCATION_ACTION) == LocationAction.Change.toString() -> {
                locationAction = LocationAction.Change
                location =
                    intent.getParcelableExtra<com.alpha.shoplex.model.pojo.Location>(LOCATION) as com.alpha.shoplex.model.pojo.Location
            }
            else -> {
                storeName = intent.getStringExtra(getString(R.string.storename)).toString()
            }
        }

        binding.btnOK.setOnClickListener {
            when (locationAction) {
                LocationAction.Add, LocationAction.Change -> addNewLocation()
            }
            finish()

        }
    }

    private fun addNewLocation() {
        setResult(RESULT_OK, Intent().apply {
            val selectedLocation = com.alpha.shoplex.model.pojo.Location(
                locationManager.selectedLocation.latitude,
                locationManager.selectedLocation.longitude
            )
            val address = locationManager.getAddress(
                com.alpha.shoplex.model.pojo.Location(
                    selectedLocation.latitude,
                    selectedLocation.longitude
                )
            )
            putExtra(ADDRESS, address)
            putExtra(LOCATION, selectedLocation)
        })
    }

    fun showStoresLocations() {
        locationManager.addMarkers(storeLocations, storeAddresses)
    }

    private fun requestPermission() {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), REQUEST_CODE
            )
        } else {
            val task: Task<Location> = mFusedLocationClient.lastLocation
            task.addOnSuccessListener { location ->
                val mapFragment = supportFragmentManager
                    .findFragmentById(R.id.mapFragment) as SupportMapFragment
                mapFragment.getMapAsync(this)
                currentLocation = location
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        locationManager = LocationManager.getInstance(mGoogleMap, this)

        if (locationAction == LocationAction.Change) {
            currentLocation?.latitude = location.latitude
            currentLocation?.longitude = location.longitude
        }

        if (locationAction == LocationAction.ShowStores) {
            showStoresLocations()
        }
        else
            locationManager.addMarker(currentLocation, locationAction != LocationAction.ShowStores)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestPermission()
                }
                return
            }
        }
    }
}