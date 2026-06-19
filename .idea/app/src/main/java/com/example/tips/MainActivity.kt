package com.example.tips

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round

class MainActivity : AppCompatActivity() {

    private lateinit var etBillAmount: EditText
    private lateinit var etTipPercent: EditText
    private lateinit var etPeopleCount: EditText
    private lateinit var rgRounding: RadioGroup
    private lateinit var rbFloor: RadioButton
    private lateinit var rbCeil: RadioButton
    private lateinit var rbNearest: RadioButton
    private lateinit var btnCalculate: Button

    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvPerPersonAmount: TextView
    private lateinit var tvError: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        btnCalculate.setOnClickListener { calculate() }
    }

    private fun initViews() {
        etBillAmount = findViewById(R.id.etBillAmount)
        etTipPercent = findViewById(R.id.etTipPercent)
        etPeopleCount = findViewById(R.id.etPeopleCount)

        rgRounding = findViewById(R.id.rgRounding)
        rbFloor = findViewById(R.id.rbFloor)
        rbCeil = findViewById(R.id.rbCeil)
        rbNearest = findViewById(R.id.rbNearest)

        btnCalculate = findViewById(R.id.btnCalculate)

        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvPerPersonAmount = findViewById(R.id.tvPerPersonAmount)
        tvError = findViewById(R.id.tvError)
    }

    private fun calculate() {
        tvError.text = ""

        val billStr = etBillAmount.text.toString().replace(',', '.').trim()
        val tipStr = etTipPercent.text.toString().trim()
        val peopleStr = etPeopleCount.text.toString().trim()

        // Проверка суммы счета
        val bill = billStr.toDoubleOrNull()
        if (bill == null || bill <= 0.0) {
            showError("Некорректная сумма счета")
            return
        }

        // Процент чаевых: по умолчанию 15, если пусто
        val tipPercent = if (tipStr.isEmpty()) {
            15
        } else {
            tipStr.toIntOrNull() ?: run {
                showError("Некорректный процент чаевых")
                return
            }
        }

        if (tipPercent !in 5..30) {
            showError("Процент чаевых должен быть от 5 до 30")
            return
        }

        // Количество человек: по умолчанию 1, если пусто
        val peopleCount = if (peopleStr.isEmpty()) {
            1
        } else {
            peopleStr.toIntOrNull() ?: run {
                showError("Некорректное количество человек")
                return
            }
        }

        if (peopleCount !in 1..20) {
            showError("Количество человек должно быть от 1 до 20")
            return
        }

        // Расчёт чаевых
        val rawTip = bill * tipPercent / 100.0
        val roundedTip = when {
            rbFloor.isChecked -> floor(rawTip)
            rbCeil.isChecked -> ceil(rawTip)
            rbNearest.isChecked -> round(rawTip)
            else -> round(rawTip)
        }

        val total = bill + roundedTip
        val perPerson = total / peopleCount

        // Форматирование до двух знаков
        tvTipAmount.text = String.format("%.2f", roundedTip)
        tvTotalAmount.text = String.format("%.2f", total)
        tvPerPersonAmount.text = String.format("%.2f", perPerson)
    }

    private fun showError(message: String) {
        tvError.text = message
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        tvTipAmount.text = "0,00"
        tvTotalAmount.text = "0,00"
        tvPerPersonAmount.text = "0,00"
    }
}
