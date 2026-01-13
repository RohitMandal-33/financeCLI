package com.example.cliproj

import java.math.BigDecimal
import java.math.RoundingMode

// Extension functions for financial calculations
fun BigDecimal.toFormattedCurrency(): String =
    "$${this.setScale(2, RoundingMode.HALF_UP)}"

fun List<Transaction>.totalIncome(): BigDecimal =
    this.filter { it.type == TransactionType.INCOME }
        .sumOf { it.amount }

fun List<Transaction>.totalExpenses(): BigDecimal =
    this.filter { it.type == TransactionType.EXPENSE }
        .sumOf { it.amount }

fun List<Transaction>.netCashFlow(): BigDecimal =
    this.totalIncome() - this.totalExpenses()