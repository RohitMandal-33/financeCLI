package com.example.cliproj

import java.math.BigDecimal
import java.math.RoundingMode

// Loan Calculator
object LoanCalculator {
    fun calculateMonthlyPayment(
        principal: BigDecimal,
        annualRate: BigDecimal,
        years: Int
    ): BigDecimal {
        val monthlyRate = annualRate.divide(BigDecimal(12), 6, RoundingMode.HALF_UP)
        val numberOfPayments = years * 12

        if (monthlyRate == BigDecimal.ZERO) {
            return principal.divide(BigDecimal(numberOfPayments), 2, RoundingMode.HALF_UP)
        }

        val onePlusR = BigDecimal.ONE + monthlyRate
        val factor = onePlusR.pow(numberOfPayments)

        return (principal * monthlyRate * factor).divide(
            factor - BigDecimal.ONE,
            2,
            RoundingMode.HALF_UP
        )
    }

    fun calculateTotalInterest(
        principal: BigDecimal,
        monthlyPayment: BigDecimal,
        years: Int
    ): BigDecimal {
        val totalPayments = monthlyPayment * BigDecimal(years * 12)
        return totalPayments - principal
    }
}

// Investment Calculator
object InvestmentCalculator {
    fun calculateFutureValue(
        principal: BigDecimal,
        annualRate: BigDecimal,
        years: Int,
        monthlyContribution: BigDecimal = BigDecimal.ZERO
    ): BigDecimal {
        val monthlyRate = annualRate.divide(BigDecimal(12), 6, RoundingMode.HALF_UP)
        val months = years * 12

        var futureValue = principal

        for (i in 1..months) {
            futureValue = (futureValue + monthlyContribution) * (BigDecimal.ONE + monthlyRate)
        }

        return futureValue.setScale(2, RoundingMode.HALF_UP)
    }

    fun calculateCompoundInterest(
        principal: BigDecimal,
        annualRate: BigDecimal,
        years: Int,
        compoundFrequency: Int = 12
    ): BigDecimal {
        val rate = annualRate.divide(BigDecimal(compoundFrequency), 6, RoundingMode.HALF_UP)
        val times = compoundFrequency * years

        var result = principal
        repeat(times) {
            result *= (BigDecimal.ONE + rate)
        }

        return result.setScale(2, RoundingMode.HALF_UP)
    }
}