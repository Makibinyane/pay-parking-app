package com.example.parking_payment_app.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.parking_payment_app.R
import com.example.parking_payment_app.databinding.FragmentPayTicketBinding
import com.example.parking_payment_app.viewmodel.PayTicketViewModel
import com.google.android.material.snackbar.Snackbar
import java.util.*

class PayTicketFragment : Fragment(R.layout.fragment_pay_ticket) {

    private lateinit var viewModel: PayTicketViewModel
    private lateinit var payTicketBinding: FragmentPayTicketBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentPayTicketBinding.bind(view)
        payTicketBinding = binding
        viewModel = ViewModelProvider(this).get(PayTicketViewModel::class.java)

        payTicketBinding.txtTicketNoValue.text = generateTicketNumber()
        payTicketBinding.txtParkingDurationValue.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val parkingAmount = viewModel.calculateParkingAmount(
                    Integer.parseInt(
                        payTicketBinding.txtParkingDurationValue.text.toString()
                    )
                )
                payTicketBinding.txtParkingCostValue.text =
                    getString(R.string.currency_symbol, parkingAmount.toString())
            }
        }

        payTicketBinding.btnPayTicket.setOnClickListener {

            val isInputValid = validateInput(
                payTicketBinding.txtParkingPaymentAmountValue.text.toString(),
                payTicketBinding.txtParkingCostValue.text.toString()
            )
            if (isInputValid) {
                val parkingCost =
                    Integer.parseInt(
                        payTicketBinding.txtParkingCostValue.text.toString().replace("R", "")
                            .replace(" ", "")
                    )
                val parkingPaymentAmount =
                    Integer.parseInt(payTicketBinding.txtParkingPaymentAmountValue.text.toString())
                val returnedAmount = viewModel.calculateReturnedAmount(
                    parkingCost,
                    parkingPaymentAmount
                )

                if (parkingPaymentAmount < parkingCost) {
                    payTicketBinding.txtPaymentAmountError.visibility = View.VISIBLE
                    payTicketBinding.txtParkingAmountReturnedValue.visibility = View.GONE
                    payTicketBinding.txtParkingAmountReturnedLabel.visibility = View.GONE
                    payTicketBinding.txtDenominations.visibility = View.GONE
                    payTicketBinding.txtDenominationsHeading.visibility = View.GONE

                    payTicketBinding.txtPaymentAmountError.text =
                        getString(R.string.less_payment_amount_error, getString(R.string.currency_symbol, parkingCost.toString()))

                } else {
                    payTicketBinding.txtPaymentAmountError.visibility = View.GONE
                    payTicketBinding.txtParkingAmountReturnedValue.visibility = View.VISIBLE
                    payTicketBinding.txtParkingAmountReturnedLabel.visibility = View.VISIBLE
                    payTicketBinding.txtDenominationsHeading.visibility = View.VISIBLE
                    payTicketBinding.txtDenominations.visibility = View.VISIBLE

                    payTicketBinding.txtParkingAmountReturnedValue.text =
                        getString(R.string.currency_symbol, returnedAmount.toString())


                    val currencyDenomination =
                        viewModel.getNumberOfNotesPerDenomination(Integer.parseInt(returnedAmount.toString()))
                    val currencyData =
                        viewModel.prepareDenominationDataForDisplay(currencyDenomination)
                    payTicketBinding.txtDenominations.text = currencyData
                }
            } else {
                payTicketBinding.txtParkingAmountReturnedValue.visibility = View.GONE
                payTicketBinding.txtParkingAmountReturnedLabel.visibility = View.GONE
                payTicketBinding.txtDenominations.visibility = View.GONE
                payTicketBinding.txtDenominationsHeading.visibility = View.GONE

            }
        }
    }

    private fun validateInput(parkingPaymentAmount: String, parkingCostTextView: String): Boolean {
        var isValid = true

        if (!viewModel.isInputFieldValid(parkingCostTextView) ||
            !viewModel.isInputFieldValid(parkingPaymentAmount)
        ) {
            Snackbar.make(
                payTicketBinding.txtParkingPaymentAmountValue,
                getString(R.string.parking_cost_error_message),
                Snackbar.LENGTH_LONG
            ).show()
            isValid = false
        }

        return isValid
    }

    private fun generateTicketNumber(): String {
        return getString(R.string.ticket_number, UUID.randomUUID().toString().substring(0, 6))
    }

}