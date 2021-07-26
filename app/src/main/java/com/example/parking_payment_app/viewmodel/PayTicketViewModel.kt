package com.example.parking_payment_app.viewmodel

import androidx.lifecycle.ViewModel

class PayTicketViewModel : ViewModel() {

    fun calculateReturnedAmount(parkingCost: Int, paymentAmount: Int): Int {
        return paymentAmount - parkingCost
    }

    fun calculateParkingAmount(parkingDuration: Int): Int {
        //the charge per minute is R1.00
        val paymentAmountPerMinute = 1
        return parkingDuration * paymentAmountPerMinute
    }

    fun isInputFieldValid(inputData: String): Boolean {
        if (inputData.isNotEmpty()) {
            return true
        }
        return false
    }

    fun getNumberOfNotesPerDenomination(amount: Int): MutableMap<Int, Int> {
        var amount = amount

        //store the denominations that are available for ZAR
        val notes = intArrayOf(200, 100, 50, 20, 10, 5, 2, 1)
        val noteCounter = IntArray(8)
        val list: MutableMap<Int, Int> = mutableMapOf()

        // count values that are found from the amount
        for (i in 0..7) {
            if (amount >= notes[i]) {
                noteCounter[i] = amount / notes[i]
                amount -= noteCounter[i] * notes[i]
            }
        }
        // populate list
        for (i in 0..7) {
            list[notes[i]] = noteCounter[i]
        }
        return list
    }

    fun prepareDenominationDataForDisplay(currencyDenomination: MutableMap<Int, Int>) : String {
        var data = ""
        for ((k, v) in currencyDenomination) {
            data += "$k - $v\n"
        }
        return data
    }
}