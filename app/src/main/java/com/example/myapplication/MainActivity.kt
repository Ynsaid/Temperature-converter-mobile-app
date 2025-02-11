package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btn.setOnClickListener {

            val kelvinText = binding.kedittext.text.toString()
            val celsiusText = binding.cedittext.text.toString()
            val fahrenheitText = binding.fedittext.text.toString()


            when {
                kelvinText.isNotEmpty() -> {
                    val kelvin = kelvinText.toDouble()
                    val celsius = kelvin - 273.15
                    val fahrenheit = (kelvin - 273.15) * 9 / 5 + 32
                    binding.cedittext.setText(celsius.toString())
                    binding.fedittext.setText(fahrenheit.toString())
                }
                celsiusText.isNotEmpty() -> {
                    val celsius = celsiusText.toDouble()
                    val kelvin = celsius + 273.15
                    val fahrenheit = (celsius * 9 / 5) + 32
                    binding.kedittext.setText(kelvin.toString())
                    binding.fedittext.setText(fahrenheit.toString())
                }
                fahrenheitText.isNotEmpty() -> {
                    val fahrenheit = fahrenheitText.toDouble()
                    val celsius = (fahrenheit - 32) * 5 / 9
                    val kelvin = (fahrenheit - 32) * 5 / 9 + 273.15
                    binding.cedittext.setText(celsius.toString())
                    binding.kedittext.setText(kelvin.toString())
                }
            }
        }
    }
}
