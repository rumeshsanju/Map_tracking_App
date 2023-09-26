package com.example.googlemap

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
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
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.karumi.dexter.Dexter

class MainActivity : AppCompatActivity(),OnMapReadyCallback {

    private  var mMap:GoogleMap?=null
    lateinit var mapView: MapView
    private val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"
    private val DEFAULT_ZOOM = 15f
    private var fusedLoctionProviderClient : FusedLocationProviderClient?=null
    


    override fun onMapReady(googleMap: GoogleMap) {
        mapView.onResume()
        mMap=googleMap
        
        if(ActivityCompat.checkSelfPermission(
                this, ACCESS_FINE_LOCATION
                        
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED

            ){
                return

           }
        mMap!!.setMyLocationEnabled(true)

    }


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapView =findViewById(R.id.map1)

        val e2 = findViewById<Button>(R.id.e2)
        e2.setOnClickListener (View.OnClickListener {
            super.onDestroy()
            val intent = Intent(this,Home::class.java)
            startActivity(intent)
            finish()
        })


        getCurrentLocation()
        var mapViewBundle:Bundle? =null
        if(savedInstanceState != null)
        {
            mapViewBundle= savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
        }

        mapView=findViewById(R.id.map1)
        mapView.onCreate(mapViewBundle)
        mapView.getMapAsync(this)

    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)


        var mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY)
        if(mapViewBundle == null){
            mapViewBundle= Bundle()
            outState.putBundle(MAP_VIEW_BUNDLE_KEY,mapViewBundle)
        }

        mapView.onSaveInstanceState(mapViewBundle)
    }

    /*private fun askGallaryPermissonLocation()
    {
        askPermission(
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION

        ) {

        }.onDecliend{ e ->
            if(e.hasDenied()){
                e.denied.forEach{

                }
                AlertDialog.Builder(this)
                    .setMessage("Accept permission to use Importantn Features")
                    .setPositiveButton("yes"){_,_ ->
                        e.askAgain()
                    }
                    .setNegativeButton("no"){dialog,_ ->
                        dialog.dismiss()
                    }
                    .show()
            }
            if(e.hasForeverDenied()){
                e.foreverDenied.forEach{}

            }
            e.goToSettings();
        }
    }*/

    private fun getCurrentLocation(){
        fusedLoctionProviderClient = LocationServices.getFusedLocationProviderClient(this@MainActivity)

        try {
            @SuppressLint("MissingPermission")
            val location = fusedLoctionProviderClient!!.getLastLocation()
            location.addOnCompleteListener(object :OnCompleteListener<Location>{
                override fun onComplete(loc:Task<Location>){

                    if(loc.isSuccessful){
                        val currentLocation = loc.result as Location?
                        if (currentLocation != null){
                            moveCamera(
                                LatLng(currentLocation.latitude,currentLocation.longitude),
                                DEFAULT_ZOOM
                            )
                        }
                    }else
                    {
                        Toast.makeText(this@MainActivity,"current Location not Found",Toast.LENGTH_SHORT).show()
                    }
                }

            })
        }catch (se: Exception){
            Log.e("TAG","security Exception")
        }
    }

    private fun moveCamera(latLng: LatLng,zoom:Float){
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom))
    }
}