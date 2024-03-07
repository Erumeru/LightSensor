package com.example.lightsensor

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
     private var bright: Sensor?=null
    private lateinit var text: TextView
    private lateinit var squareDisplay: View
    private var colorBase: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        text=findViewById(R.id.lightTV)
        text.setText("en cuanto cambie la luz me cambio pa q veas")

        squareDisplay=findViewById<View>(R.id.squareView)
        colorBase=(squareDisplay.background as? ColorDrawable)?.color ?: Color.TRANSPARENT

        setSensor();
    }

    private fun brightness(brightness: Float): String {
        return when (brightness.toInt()) {
            0 -> "bien oscuro loco"
            in 1 downTo 10 -> "ta negro"
            in 11 downTo 50 -> "ta masomenos oscuro"
            in 51 downTo 500 -> "ya ta bien"
            in 501 downTo 2500 -> "AAAAAAAAAAAAAAA"
            else -> "esta onda es el sol loco"
        }
    }

    private fun setSensor(){
        sensorManager=getSystemService(SENSOR_SERVICE) as SensorManager
        bright=sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(event?.sensor?.type==Sensor.TYPE_LIGHT){
            val light=event.values[0]
            text.text = "Sensor: $light\n${brightness(light)}"
            val filtredColor=darkenColor(colorBase,(light/2500))
            squareDisplay.setBackgroundColor(filtredColor)
        }
    }

    // Function to darken a color
    private fun darkenColor(color: Int, factor: Float): Int {
        val adjustedFactor = if (factor > 1.0f) 1.0f else if (factor < 0.0f) 0.0f else factor

        val a = Color.alpha(color)
        val r = (Color.red(color) * adjustedFactor).toInt()
        val g = (Color.green(color) * adjustedFactor).toInt()
        val b = (Color.blue(color) * adjustedFactor).toInt()
        return Color.argb(a, r, g, b)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this,bright,SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}