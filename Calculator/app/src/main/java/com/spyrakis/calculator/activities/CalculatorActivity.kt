package com.spyrakis.calculator.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Toast
import com.spyrakis.calculator.R
import com.spyrakis.calculator.adapters.CustomSpinnerAdapter
import com.spyrakis.calculator.interfaces.CalculatorService
import com.spyrakis.calculator.interfaces.CustomClickListener
import com.spyrakis.calculator.models.Rates
import com.spyrakis.calculator.models.RatesResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_calculator.*
import java.text.DecimalFormat

class CalculatorActivity : AppCompatActivity() {

    /** string presented in calculator screen **/
    private var screenText: String = "0"

    /** flag used to keep record weather the last digit is  operator **/
    private var lastDigitIsOperator = false

    /**flag used to keep track if the number on screen is result **/
    private var isResult = false

    /** if [operator] == '!' means that [operator] is unset **/
    private var operator: Char = '!'

    private lateinit var rates: Rates
    private var selectedRate: Double = 1.0

    private var firstNumber: Double = 0.0
    private var secondNumber: Double = 0.0
    private lateinit var df: DecimalFormat


    private val apiService by lazy {
        val baseUrl = resources.getString(R.string.base_url)
        CalculatorService.create(baseUrl)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_calculator)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        getRates()
        setUpSpinner()
        setUpButtons()
        df = DecimalFormat("###############.###############")
    }

    /**
     * gets the rates
     */

    private fun getRates() {
        disposable = apiService.getRates()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { ratesResponse ->
                            val task = Runnable {
                                consumeRatesResult(ratesResponse)
                            }
                            Thread(task).start()
                        },
                        { error ->
                            showError(error)
                        }
                )
    }

    private fun consumeRatesResult(ratesResponse: RatesResponse) {
        if (ratesResponse.rates != null ){
            rates = ratesResponse.rates
        }else{
            rates = Rates()
        }
    }

    private fun showError(error: Throwable?) {
        if (error != null) {
            Log.d(error.message, error.stackTrace.toString())
        }
        rates = Rates()
    }

    /**
     * adds items to spinner and a [OnItemSelectedListener]
     **/
    private fun setUpSpinner() {
        // Setup spinner
        spinner.adapter = CustomSpinnerAdapter(
                toolbar.context,
                arrayOf(applicationContext.getString(R.string.euro),
                        applicationContext.getString(R.string.usd),
                        applicationContext.getString(R.string.gpb),
                        applicationContext.getString(R.string.yen)))

        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // When the given dropdown item is selected, check if should convert currency
                // and if true convert.(if chooses the same currency or just Number we dont convert)

                val rateToSelect:Double = when (position){
                    1-> rates.uSD?:0.0
                    2-> rates.gBP?:0.0
                    3-> rates.jPY?:0.0
                    else-> 1.0
                }
                if (rateToSelect == 0.0){
                    Toast.makeText(applicationContext,R.string.error_message,Toast.LENGTH_LONG).show()
                    return
                }

                if (!lastDigitIsOperator){
                    var screenValue = screenText.toDouble()
                    screenValue /= selectedRate
                    screenValue *= rateToSelect
                    screenText = df.format(screenValue).replace(',','.')
                    calculatorText.text = screenText
                }
                firstNumber /= selectedRate
                firstNumber  *= rateToSelect
                secondNumber /= selectedRate
                secondNumber  *= rateToSelect
                selectedRate = rateToSelect
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    /**
     * adds listeners to buttons
     * for operators and numbers [CustomClickListener] interface is used
     **/

    private fun setUpButtons() {
        one.setOnClickListener {
            numberButtonListener.onItemClick(ONE)
        }
        two.setOnClickListener {
            numberButtonListener.onItemClick(TWO)
        }
        three.setOnClickListener {
            numberButtonListener.onItemClick(THREE)
        }
        four.setOnClickListener {
            numberButtonListener.onItemClick(FOUR)
        }
        five.setOnClickListener {
            numberButtonListener.onItemClick(FIVE)
        }
        six.setOnClickListener {
            numberButtonListener.onItemClick(SIX)
        }
        seven.setOnClickListener {
            numberButtonListener.onItemClick(SEVEN)
        }
        eight.setOnClickListener {
            numberButtonListener.onItemClick(EIGHT)
        }
        nine.setOnClickListener {
            numberButtonListener.onItemClick(NINE)
        }
        zero.setOnClickListener {
            numberButtonListener.onItemClick(ZERO)
        }
        divide.setOnClickListener {
            operatorButtonListener.onItemClick(DIVIDE)
        }
        plus.setOnClickListener {

            operatorButtonListener.onItemClick(ADD)
        }
        minus.setOnClickListener {
            operatorButtonListener.onItemClick(MINUS)
        }
        multiply.setOnClickListener {
            operatorButtonListener.onItemClick(MULTIPLY)
        }
        dot.setOnClickListener {
            if (screenText.contains(DOT, true)) {
                return@setOnClickListener
            }
            if (lastDigitIsOperator) {
                return@setOnClickListener
            }
            addDigit(DOT)
        }

        equality.setOnClickListener {
            //if operator is not set or the second number is not set wet we return
            if (this@CalculatorActivity.operator == '!') return@setOnClickListener
            if (lastDigitIsOperator) return@setOnClickListener

            secondNumber = screenText.toDouble()
            screenText = df.format(execute()).replace(',','.')
            calculatorText.text = screenText
            this@CalculatorActivity.operator = '!'
            isResult = true
        }
        //deletes last inserted char
        backspace.setOnClickListener {
            if (isResult) {
                clearEverything()
                return@setOnClickListener
            }
            if (lastDigitIsOperator) {
                screenText = df.format(firstNumber).replace(',','.')
                calculatorText.text = screenText
                operator = '!'
                lastDigitIsOperator = false
            } else {
                removeLast()
                if (screenText.isBlank()) {
                    screenText = if (this@CalculatorActivity.operator == '!') {
                        "0"
                    } else {
                        lastDigitIsOperator = true
                        operator.toString()
                    }
                }
                calculatorText.text = screenText
            }
        }

        //clears only the value on the screen and shows on screen the previous value
        clear.setOnClickListener {
            if (isResult) {
                clearEverything()
                return@setOnClickListener
            }
            if (lastDigitIsOperator) {
                screenText = df.format(firstNumber).replace(',','.')
                calculatorText.text = screenText
                operator = '!'
                lastDigitIsOperator = false
            } else {
                if (this@CalculatorActivity.operator == '!') {
                    firstNumber = 0.0
                    screenText = "0"
                    calculatorText.text = screenText
                } else {
                    secondNumber = 0.0
                    screenText = operator.toString()
                    lastDigitIsOperator = true
                    calculatorText.text = screenText
                }
            }
        }
        //resets everything
        clearAll.setOnClickListener {
            clearEverything()
        }

        // compute 1/x
        divideOne.setOnClickListener{
            if (!lastDigitIsOperator){
                var value = screenText.toDouble()
                value = 1/value
                screenText = df.format(value).replace(',','.')
                calculatorText.text = screenText
                isResult = true
            }
        }
    }

    /**
     * resets calculator
     */
    private fun clearEverything() {
        firstNumber = 0.0
        secondNumber = 0.0
        operator = '!'
        screenText = "0"
        lastDigitIsOperator = false
        isResult = false
        calculatorText.text = screenText
    }

    /**
     * listener for operators
     * if [lastDigitIsOperator] = true we must remove the operator form the screen and write a new one
     * else if an [operator] is already set means that the user has already inserted two arithmetic values and
     * we must do the math with two previous arithmetic values the user added and assign the result to the first number
     * else we assign the screen value to first number and prepare the app for the second number
     */
    private val operatorButtonListener = object : CustomClickListener {
        override fun onItemClick(operator: Char) {
            if (lastDigitIsOperator) {
                removeLast()
            } else {
                if (this@CalculatorActivity.operator == '!') {
                    firstNumber = screenText.toDouble()
                } else {
                    secondNumber = screenText.toDouble()
                    firstNumber = execute()
                }
            }
            lastDigitIsOperator = true

            addOperator(operator)

            this@CalculatorActivity.operator = operator
        }
    }

    /**
     * do the math
     */
    private fun execute(): Double {
        return when (operator) {
            MULTIPLY -> firstNumber * secondNumber
            DIVIDE -> firstNumber / secondNumber
            MINUS -> firstNumber - secondNumber
            else -> firstNumber + secondNumber
        }
    }

    /**
     * removes last char in [screenText]
     */
    private fun removeLast() {
        screenText = screenText.dropLast(1)
    }

    /**
     * adds operator in [screenText]
     */
    private fun addOperator(operator: Char) {
        screenText = operator.toString()

        calculatorText.text = screenText
    }

    /**
     * listener for numbers
     */
    private val numberButtonListener = object : CustomClickListener {
        override fun onItemClick(digit: Char) {
            addDigit(digit)
            lastDigitIsOperator = false
        }
    }

    /**
     * adds digit in screen text
     */
    private fun addDigit(digit: Char) {
        if (isResult) {
            isResult = false
            screenText = ""
        }
        if (screenText.length >= 15) return
        if (screenText == "0") screenText = ""
        if (lastDigitIsOperator) screenText = ""
        screenText += digit
        calculatorText.text = screenText
    }

    companion object {
        private const val ADD = '+'
        private const val MINUS = '-'
        private const val MULTIPLY = '*'
        private const val DIVIDE = '/'
        private const val DOT = '.'
        private const val ONE = '1'
        private const val TWO = '2'
        private const val THREE = '3'
        private const val FOUR = '4'
        private const val FIVE = '5'
        private const val SIX = '6'
        private const val SEVEN = '7'
        private const val EIGHT = '8'
        private const val NINE = '9'
        private const val ZERO = '0'
        private var disposable: Disposable? = null

    }
}
