package com.example.movieticketsonlinebooking.activities

import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.movieticketsonlinebooking.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.movieticketsonlinebooking.databinding.ActivityMapCinemaBinding

class MapCinemaActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapCinemaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapCinemaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        var sydney = LatLng(-34.0, 151.0)
        val geocoder = Geocoder(applicationContext)
        val results = geocoder.getFromLocationName("Galaxy Nguyễn Du", 1)
        if (results != null) {
            if (results.isNotEmpty()) {
                val latitude = results[0].latitude
                val longitude = results[0].longitude
                // Do something with the latitude and longitude
                sydney = LatLng(latitude, longitude)
            }
        }

        // Add a marker in Sydney and move the camera

        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Galaxy Nguyễn Du"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,20f))
    }
}