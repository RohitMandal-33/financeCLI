package com.example.cliproj

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Data classes for modeling financial entities
data class Transaction(
    val id: String,
    val type: TransactionType,
    val amount: BigDecimal,
    val category: String,
    val description: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
) {
    override fun toString(): String = """
        |Transaction #$id
        |Type: $type
        |Amount: ${amount.setScale(2, RoundingMode.HALF_UP)}
        |Category: $category
        |Description: $description
        |Date: ${timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}
    """.trimMargin()
}

enum class TransactionType {
    INCOME,
    EXPENSE,
    TRANSFER
}

sealed class AccountType {
    abstract val interestRate: BigDecimal

    object Savings : AccountType() {
        override val interestRate = BigDecimal("0.03")
    }

    object Checking : AccountType() {
        override val interestRate = BigDecimal("0.01")
    }

    data class Investment(override val interestRate: BigDecimal) : AccountType()
}

data class Account(
    val accountNumber: String,
    val accountType: AccountType,
    private var balance: BigDecimal = BigDecimal.ZERO
) {
    private val transactionHistory = mutableListOf<Transaction>()

    fun getBalance(): BigDecimal = balance

    fun deposit(amount: BigDecimal, description: String): Boolean {
        require(amount > BigDecimal.ZERO) { "Amount must be positive" }

        balance += amount
        val transaction = Transaction(
            id = generateTransactionId(),
            type = TransactionType.INCOME,
            amount = amount,
            category = "Deposit",
            description = description
        )
        transactionHistory.add(transaction)
        return true
    }

    fun withdraw(amount: BigDecimal, description: String): Boolean {
        require(amount > BigDecimal.ZERO) { "Amount must be positive" }

        return if (balance >= amount) {
            balance -= amount
            val transaction = Transaction(
                id = generateTransactionId(),
                type = TransactionType.EXPENSE,
                amount = amount,
                category = "Withdrawal",
                description = description
            )
            transactionHistory.add(transaction)
            true
        } else {
            false
        }
    }

    fun getTransactionHistory(): List<Transaction> = transactionHistory.toList()

    fun calculateInterest(): BigDecimal {
        return balance * accountType.interestRate
    }

    private fun generateTransactionId(): String {
        return "TXN${System.currentTimeMillis()}"
    }
}