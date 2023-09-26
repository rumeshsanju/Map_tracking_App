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
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.karumi.dexter.Dexter
import java.io.IOException
import java.util.*

class Address : AppCompatActivity(),OnMapReadyCallback,LocationListener,GoogleMap.OnCameraMoveListener,GoogleMap.OnCameraMoveStartedListener,GoogleMap.OnCameraIdleListener {

    private  var mMap:GoogleMap?=null
    lateinit var mapView: MapView
    private val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"
    private val DEFAULT_ZOOM = 15f
    private var fusedLocationProviderClient : FusedLocationProviderClient?=null
    lateinit var tvCurrentAddress:TextView



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
        mMap!!.setOnCameraMoveListener(this)
        mMap!!.setOnCameraMoveListener(this)
        mMap!!.setOnCameraIdleListener(this)

    }


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)

        mapView =findViewById(R.id.map1)
        tvCurrentAddress = findViewById<TextView>(R.id.tv)
        val e1 = findViewById<Button>(R.id.e1)
        e1.setOnClickListener (View.OnClickListener {
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


    private fun getCurrentLocation(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this@Address)

        try {
            @SuppressLint("MissingPermission")
            val location = fusedLocationProviderClient!!.getLastLocation()
            location.addOnCompleteListener(object :OnCompleteListener<Location>{
                override fun onComplete(loc:Task<Location>){

                    if(loc.isSuccessful){
                        val currentLocation = loc.result as Location?
                        if (currentLocation != null){
                            moveCamera2(
                                LatLng(currentLocation.latitude,currentLocation.longitude),
                                DEFAULT_ZOOM
                            )
                        }
                    }else
                    {
                        Toast.makeText(this@Address,"current Location not Found",Toast.LENGTH_SHORT).show()
                    }
                }

            })
        }catch (se: Exception){
            Log.e("TAG","security Exception")
        }
    }

    private fun moveCamera2(latLng: LatLng,zoom:Float){
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom))
    }

    override fun onLocationChanged(location: Location) {
        val geocoder=Geocoder(this, Locale.getDefault())
        var addresses:List<Address>?=null
        try{
            addresses=geocoder.getFromLocation(location!!.latitude,location.longitude,1)
        } catch (e: IOException){
            e.printStackTrace()
        }
        setAddress(addresses!![0])
    }

    private fun setAddress(addresses: Address?) {
        if(addresses != null){

            if(addresses.getAddressLine(0)!=null){
                tvCurrentAddress!!.setText(addresses.getAddressLine(0))
            }
            if(addresses.getAddressLine(1)!=null){
                tvCurrentAddress!!.setText(
                    tvCurrentAddress.getText().toString() + addresses.getAddressLine(1)
                )

            }
        }


    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        super.onStatusChanged(p0, p1, p2)
    }

    override fun onProviderEnabled(p0: String) {
        super.onProviderEnabled(p0)
    }

    override fun onProviderDisabled(p0: String) {
        super.onProviderDisabled(p0)
    }

    override fun onCameraMove() {

    }

    override fun onCameraMoveStarted(p0: Int) {

    }

    override fun onCameraIdle() {
         var addresses:List<Address>?=null
        val geocoder = Geocoder(this,Locale.getDefault())

        try{

            addresses = geocoder.getFromLocation(mMap!!.cameraPosition.target.latitude,mMap!!.cameraPosition.target.longitude,1)
            setAddress(addresses!![0])
        }catch (e:java.lang.IndexOutOfBoundsException){
            e.printStackTrace()
        }catch(e:IOException){
            e.printStackTrace()
        }
    }
}