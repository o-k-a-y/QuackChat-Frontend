package edu.ramapo.btunney.quackchat

import android.content.Context
import android.hardware.camera2.CameraManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        testCamera()
    }


    fun testCamera() : Unit {
        Log.d(object: Any() {

        }.javaClass.enclosingMethod?.name, "oh")

        val cameraManager = this.getSystemService(Context.CAMERA_SERVICE)
        
    }
}
