package com.example.cliproj

import java.math.BigDecimal
import java.math.RoundingMode

// Main Financial Manager class
class PersonalFinanceManager {
    private val accounts = mutableMapOf<String, Account>()
    private var currentAccount: Account? = null

    companion object {
        private var accountCounter = 1000

        fun generateAccountNumber(): String {
            return "ACC${accountCounter++}"
        }
    }

    fun createAccount(type: AccountType): Account {
        val accountNumber = generateAccountNumber()
        val account = Account(accountNumber, type)
        accounts[accountNumber] = account
        currentAccount = account
        return account
    }

    fun selectAccount(accountNumber: String): Boolean {
        currentAccount = accounts[accountNumber]
        return currentAccount != null
    }

    fun getCurrentAccount(): Account? = currentAccount

    fun getAllAccounts(): List<Account> = accounts.values.toList()

    fun deleteAccount(accountNumber: String): Boolean {
        return if (accounts[accountNumber]?.getBalance() == BigDecimal.ZERO) {
            accounts.remove(accountNumber)
            if (currentAccount?.accountNumber == accountNumber) {
                currentAccount = null
            }
            true
        } else {
            false
        }
    }

    fun transfer(fromAccount: String, toAccount: String, amount: BigDecimal): Boolean {
        val from = accounts[fromAccount] ?: return false
        val to = accounts[toAccount] ?: return false

        return if (from.withdraw(amount, "Transfer to $toAccount")) {
            to.deposit(amount, "Transfer from $fromAccount")
            true
        } else {
            false
        }
    }

    fun generateFinancialReport(): String {
        val allTransactions = accounts.values
            .flatMap { it.getTransactionHistory() }

        val totalBalance = accounts.values.sumOf { it.getBalance() }
        val totalIncome = allTransactions.totalIncome()
        val totalExpenses = allTransactions.totalExpenses()
        val netCashFlow = allTransactions.netCashFlow()

        return """
              ========== FINANCIAL REPORT ==========
              Total Balance: ${totalBalance.toFormattedCurrency()}
              Total Income: ${totalIncome.toFormattedCurrency()}
              Total Expenses: ${totalExpenses.toFormattedCurrency()}
              Net Cash Flow: ${netCashFlow.toFormattedCurrency()}
              Number of Accounts: ${accounts.size}
              Number of Transactions: ${allTransactions.size}
              ======================================
        """.trimMargin()
    }
}

// Budget Tracker
data class Budget(
    val category: String,
    val limit: BigDecimal,
    private var spent: BigDecimal = BigDecimal.ZERO
) {
    fun addExpense(amount: BigDecimal): Boolean {
        return if (spent + amount <= limit) {
            spent += amount
            true
        } else {
            false
        }
    }

    fun getRemaining(): BigDecimal = limit - spent
    fun getSpent(): BigDecimal = spent
    fun getUtilization(): BigDecimal =
        (spent.divide(limit, 4, RoundingMode.HALF_UP)) * BigDecimal(100)

    fun isOverBudget(): Boolean = spent > limit
}

class BudgetManager {
    private val budgets = mutableMapOf<String, Budget>()

    fun createBudget(category: String, limit: BigDecimal) {
        require(limit > BigDecimal.ZERO) { "Budget limit must be positive" }
        budgets[category] = Budget(category, limit)
    }

    fun addExpenseToBudget(category: String, amount: BigDecimal): Boolean {
        return budgets[category]?.addExpense(amount) ?: false
    }

    fun getBudgetStatus(category: String): String? {
        return budgets[category]?.let { budget ->
            """
            Category: ${budget.category}
            Limit: ${budget.limit.toFormattedCurrency()}
            Spent: ${budget.getSpent().toFormattedCurrency()}
            Remaining: ${budget.getRemaining().toFormattedCurrency()}
            Utilization: ${budget.getUtilization()}%
            Status: ${if (budget.isOverBudget()) "OVER BUDGET!" else "Within Budget"}
            """.trimMargin()
        }
    }

    fun getAllBudgets(): Map<String, Budget> = budgets.toMap()
}