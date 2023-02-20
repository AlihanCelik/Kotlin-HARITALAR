package com.example.googlemapskotlin

import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.GnssAntennaInfo.Listener
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.googlemapskotlin.databinding.ActivityMapsBinding
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(dinleyici)





        //Latitude -> enlem
        //longitude -> boylam
        /*
        val ankara = LatLng(39.9207231, 32.8520573)


        mMap.addMarker(MarkerOptions().position(ankara).title("Ankara kızılay"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ankara,15f))

         */

        // casting->as
        locationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener= object : LocationListener {
            override fun onLocationChanged(p0: Location) {
                // lokasyon,konum değişince yapılacak işlemler
                mMap.clear()
                val guncelKonum=LatLng(p0.latitude,p0.longitude)
                mMap.addMarker(MarkerOptions().position(guncelKonum).title("Güncel Konumunuz"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(guncelKonum,15f))

                val geocoder=Geocoder(this@MapsActivity, Locale.getDefault())
                try {
                    val adresListesi= geocoder.getFromLocation(p0.latitude,p0.longitude,1)

                    if (adresListesi!!.size > 0) {
                        println(adresListesi.get(0).toString())
                    }else{


                    }




                }catch (e : Exception){
                    e.printStackTrace()

                }



            }
        }
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            //izin verilmemiş
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),1)


        }else{
            //izin verilmiş
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locationListener)
            val sonBilinenKonum=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if(sonBilinenKonum!=null){
                val sonBilinenLatlng=LatLng(sonBilinenKonum.latitude,sonBilinenKonum.longitude)
                mMap.addMarker(MarkerOptions().position(sonBilinenLatlng).title("Son Bilinen Konumunuz"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sonBilinenLatlng,15f))
            }
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==1){
            if(grantResults.size > 0){
                if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locationListener)
                }


            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    val dinleyici=object: GoogleMap.OnMapLongClickListener{
        override fun onMapLongClick(p0: LatLng) {
            mMap.clear()
            val geocoder=Geocoder(this@MapsActivity, Locale.getDefault())

            if(p0!=null){
                var adres=""
                try{
                    val adresListesi=geocoder.getFromLocation(p0.latitude,p0.longitude,1)
                    if (adresListesi!!.size>0){
                        adres+=adresListesi.get(0).thoroughfare
                        if(adresListesi.get(0).subThoroughfare!=null){
                            adres+=adresListesi.get(0).subThoroughfare
                        }

                    }
                }catch(e:Exception){
                    e.printStackTrace()
                }
                mMap.addMarker(MarkerOptions().position(p0).title(adres))
            }
        }

    }
}