package com.spyrakis.calculator.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import com.spyrakis.calculator.R
import com.spyrakis.calculator.adapters.CustomSpinnerAdapter
import kotlinx.android.synthetic.main.activity_calculator.*

class CalculatorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        setUpSpinner()

    }

    private fun setUpSpinner() {
        // Setup spinner
        spinner.adapter = CustomSpinnerAdapter(
                toolbar.context,
                arrayOf(applicationContext.getString(R.string.number),
                        applicationContext.getString(R.string.usd),
                        applicationContext.getString(R.string.euro)))

        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // When the given dropdown item is selected, check if should convert currency
                // and if true convert.(if chooses the same currency or just Number we dont convert)

            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}
