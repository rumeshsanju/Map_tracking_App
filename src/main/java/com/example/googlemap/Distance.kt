package com.example.googlemap

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.karumi.dexter.Dexter
import java.io.IOException
import java.util.*

class Distance : AppCompatActivity(),OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    lateinit var mapView: MapView
    private val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"
    private val DEFAULT_ZOOM = 15f
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    var end_latitude=0.0
    var end_longitude=0.0
    var origin:MarkerOptions? = null
    var destination:MarkerOptions?= null
    var latitude=0.0
    var longitude=0.0

    lateinit var tv :TextView




    override fun onMapReady(googleMap: GoogleMap) {
        mapView.onResume()
        mMap = googleMap

        if (ActivityCompat.checkSelfPermission(
                this, ACCESS_FINE_LOCATION

            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED

        ) {
            return

        }
        mMap!!.setMyLocationEnabled(true)


    }


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_distance)

        val btn = findViewById<Button>(R.id.btnS)
        tv=findViewById(R.id.tv)

        val e3 = findViewById<Button>(R.id.e3)
        e3.setOnClickListener (View.OnClickListener {
            super.onDestroy()
            val intent = Intent(this,Home::class.java)
            startActivity(intent)
            finish()
        })


        getCurrentLocation()
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
        }

        mapView = findViewById(R.id.map1)
        mapView.onCreate(mapViewBundle)
        mapView.getMapAsync(this)

        btn.setOnClickListener{
            searachArea()
        }

    }

     fun searachArea() {
        val tf =findViewById<View>(R.id.tf) as EditText

        val location = tf.text.toString()
        var addressLists: List<Address>?= null
        val markerOptions = MarkerOptions()

        if(location != ""){
            val geocoder = Geocoder(applicationContext)
            try{
                addressLists=geocoder.getFromLocationName(location,5)

            }catch(e:IOException){
                e.printStackTrace()
            }
            if (addressLists != null){
                for(i in addressLists.indices){
                    val myAddress = addressLists[i]
                    val latLng = LatLng(myAddress.latitude,myAddress.longitude)
                    markerOptions.position(latLng)
                    mMap!!.addMarker(markerOptions)
                    end_latitude=myAddress.latitude
                    end_longitude=myAddress.longitude
                    mMap!!.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                    val mo =MarkerOptions()
                    mo.title("Distance")
                    val results = FloatArray(10)
                    Location.distanceBetween(latitude,longitude,end_latitude,end_longitude,results)

                    val s= String.format("%.1f",results[0]/1000)

                    origin = MarkerOptions().position(LatLng(latitude,longitude)).title("HSR Layout").snippet("origin")
                    destination= MarkerOptions().position(LatLng(end_latitude,end_longitude)).title(tf.text.toString()).snippet("Distance = $s KM")

                    mMap!!.addMarker(destination!!)
                    mMap!!.addMarker(origin!!)

                    Toast.makeText(this@Distance,"Distance = $s KM",Toast.LENGTH_SHORT).show()

                    tv!!.setText("Distance = $s KM")

                }
            }
        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)


        var mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle)
        }

        mapView.onSaveInstanceState(mapViewBundle)
    }


    private fun getCurrentLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this@Distance)

        try {
            @SuppressLint("MissingPermission")
            val location = fusedLocationProviderClient!!.getLastLocation()
            location.addOnCompleteListener(object : OnCompleteListener<Location> {
                override fun onComplete(loc: Task<Location>) {

                    if (loc.isSuccessful) {
                        val currentLocation = loc.result as Location?
                        if (currentLocation != null) {
                            moveCamera1(
                                LatLng(currentLocation.latitude, currentLocation.longitude),
                                DEFAULT_ZOOM
                            )

                            latitude = currentLocation.latitude
                            longitude= currentLocation.longitude

                        }
                    } else {
                        Toast.makeText(
                            this@Distance,
                            "current Location not Found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            })
        } catch (se: Exception) {
            Log.e("TAG", "security Exception")
        }
    }

    private fun moveCamera1(latLng: LatLng, zoom: Float) {
         mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
    }

}