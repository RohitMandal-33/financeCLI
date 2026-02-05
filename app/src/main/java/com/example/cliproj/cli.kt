package com.example.cliproj

import java.math.BigDecimal

// CLI Interface
class FintechCLI {
    private val financeManager = PersonalFinanceManager()
    private val budgetManager = BudgetManager()
    private var isRunning = true

    fun start() {
        println("""
            ===================================
            PERSONAL FINANCE MANAGER CLI   
            ===================================     
            
        """.trimMargin())

        while (isRunning) {
            displayMainMenu()
            handleUserInput()
        }
    }

    private fun displayMainMenu() {
        println("""
            
            ========== MAIN MENU ==========
            1. Account Managemen
            2. Transactions
            3. Budget Management
            4. Financial Calculators
            5. Reports & Analytics
            6. Exit
            ==============================
            Select an option: 
        """.trimMargin())
    }

    private fun handleUserInput() {
        when (readLine()?.trim()) {
            "1" -> accountManagementMenu()
            "2" -> transactionMenu()
            "3" -> budgetManagementMenu()
            "4" -> calculatorsMenu()
            "5" -> reportsMenu()
            "6" -> {
                isRunning = false
                println("Thank you for using Personal Finance Manager!")
            }
            else -> println("Invalid option. Please try again.")
        }
    }

    private fun accountManagementMenu() {
        println("""
            
            ========== ACCOUNT MANAGEMENT ==========
            1. Create New Account
            2. View All Accounts
            3. Select Account
            4. Delete Account
            5. View Current Account Details
            6. Back to Main Menu
            =======================================
            Select an option: 
        """.trimMargin())

        when (readLine()?.trim()) {
            "1" -> createAccount()
            "2" -> viewAllAccounts()
            "3" -> selectAccount()
            "4" -> deleteAccount()
            "5" -> viewCurrentAccount()
            "6" -> return
            else -> println("Invalid option.")
        }
    }

    private fun createAccount() {
        println("""
           Select Account Type:
           1. Savings (3% interest)
           2. Checking (1% interest)
           3. Investment (Custom interest)
        """.trimMargin())

        val accountType = when (readLine()?.trim()) {
            "1" -> AccountType.Savings
            "2" -> AccountType.Checking
            "3" -> {
                print("Enter annual interest rate (e.g., 0.05 for 5%): ")
                val rate = readLine()?.toBigDecimalOrNull() ?: BigDecimal.ZERO
                AccountType.Investment(rate)
            }
            else -> {
                println("Invalid type. Creating Checking account by default.")
                AccountType.Checking
            }
        }

        val account = financeManager.createAccount(accountType)
        println("✓ Account created successfully!")
        println("Account Number: ${account.accountNumber}")
        println("Type: ${account.accountType}")
    }

    private fun viewAllAccounts() {
        val accounts = financeManager.getAllAccounts()

        if (accounts.isEmpty()) {
            println("No accounts found. Create one first!")
            return
        }

        println("\n========== ALL ACCOUNTS ==========")
        for (account in accounts) {
            println("""
                Account: ${account.accountNumber}
                Type: ${account.accountType}
                Balance: ${account.getBalance().toFormattedCurrency()}
                -----------------------------------
            """.trimMargin())
        }
    }

    private fun selectAccount() {
        print("Enter account number: ")
        val accountNumber = readLine()?.trim() ?: return

        if (financeManager.selectAccount(accountNumber)) {
            println(" Account $accountNumber selected.")
        } else {
            println(" Account not found.")
        }
    }

    private fun deleteAccount() {
        print("Enter account number to delete: ")
        val accountNumber = readLine()?.trim() ?: return

        if (financeManager.deleteAccount(accountNumber)) {
            println(" Account deleted successfully.")
        } else {
            println(" Cannot delete account. Either it doesn't exist or has non-zero balance.")
        }
    }

    private fun viewCurrentAccount() {
        val account = financeManager.getCurrentAccount()

        if (account == null) {
            println("No account selected. Please select an account first.")
            return
        }

        println("""
            ========== ACCOUNT DETAILS ==========
            Account Number: ${account.accountNumber}
            Type: ${account.accountType}
            Balance: ${account.getBalance().toFormattedCurrency()}
            Estimated Annual Interest: ${account.calculateInterest().toFormattedCurrency()}
            =====================================
        """.trimMargin())
    }

    private fun transactionMenu() {
        val account = financeManager.getCurrentAccount()

        if (account == null) {
            println("Please select an account first!")
            return
        }

        println("""
            
            ========== TRANSACTIONS ==========
            Current Account: ${account.accountNumber}
            Balance: ${account.getBalance().toFormattedCurrency()}
            
            1. Deposit
            2. Withdraw
            3. Transfer
            4. View Transaction History
            5. Back to Main Menu
            =================================
            Select an option: 
        """.trimMargin())

        when (readLine()?.trim()) {
            "1" -> deposit(account)
            "2" -> withdraw(account)
            "3" -> transfer()
            "4" -> viewTransactionHistory(account)
            "5" -> return
            else -> println("Invalid option.")
        }
    }

    private fun deposit(account: Account) {
        print("Enter amount to deposit: $")
        val amount = readLine()?.toBigDecimalOrNull()

        if (amount == null || amount <= BigDecimal.ZERO) {
            println("Invalid amount.")
            return
        }

        print("Enter description: ")
        val description = readLine() ?: "Deposit"

        try {
            if (account.deposit(amount, description)) {
                println("Deposited ${amount.toFormattedCurrency()} successfully!")
                println("New balance: ${account.getBalance().toFormattedCurrency()}")
            }
        } catch (e: IllegalArgumentException) {
            println(" Error: ${e.message}")
        }
    }

    private fun withdraw(account: Account) {
        print("Enter amount to withdraw: $")
        val amount = readLine()?.toBigDecimalOrNull()

        if (amount == null || amount <= BigDecimal.ZERO) {
            println("Invalid amount.")
            return
        }

        print("Enter description: ")
        val description = readLine() ?: "Withdrawal"

        try {
            if (account.withdraw(amount, description)) {
                println(" Withdrew ${amount.toFormattedCurrency()} successfully!")
                println("New balance: ${account.getBalance().toFormattedCurrency()}")
            } else {
                println(" Insufficient funds!")
            }
        } catch (e: IllegalArgumentException) {
            println(" Error: ${e.message}")
        }
    }

    private fun transfer() {
        print("Enter source account number: ")
        val from = readLine()?.trim() ?: return

        print("Enter destination account number: ")
        val to = readLine()?.trim() ?: return

        print("Enter amount to transfer: $")
        val amount = readLine()?.toBigDecimalOrNull()

        if (amount == null || amount <= BigDecimal.ZERO) {
            println("Invalid amount.")
            return
        }

        if (financeManager.transfer(from, to, amount)) {
            println(" Transfer completed successfully!")
        } else {
            println(" Transfer failed. Check account numbers and balance.")
        }
    }

    private fun viewTransactionHistory(account: Account) {
        val history = account.getTransactionHistory()

        if (history.isEmpty()) {
            println("No transactions found.")
            return
        }

        println("\n========== TRANSACTION HISTORY ==========")
        for (transaction in history.reversed().take(10)) {
            println(transaction)
            println("-----------------------------------")
        }
    }

    private fun budgetManagementMenu() {
        println("""
            |
            |========== BUDGET MANAGEMENT ==========
            |1. Create Budget
            |2. Add Expense to Budget
            |3. View Budget Status
            |4. View All Budgets
            |5. Back to Main Menu
            |======================================
            |Select an option: 
        """.trimMargin())

        when (readLine()?.trim()) {
            "1" -> createBudget()
            "2" -> addExpenseToBudget()
            "3" -> viewBudgetStatus()
            "4" -> viewAllBudgets()
            "5" -> return
            else -> println("Invalid option.")
        }
    }

    private fun createBudget() {
        print("Enter budget category (e.g., Food, Transport): ")
        val category = readLine()?.trim() ?: return

        print("Enter budget limit: $")
        val limit = readLine()?.toBigDecimalOrNull()

        if (limit == null || limit <= BigDecimal.ZERO) {
            println("Invalid limit.")
            return
        }

        try {
            budgetManager.createBudget(category, limit)
            println(" Budget for '$category' created with limit ${limit.toFormattedCurrency()}")
        } catch (e: IllegalArgumentException) {
            println(" Error: ${e.message}")
        }
    }

    private fun addExpenseToBudget() {
        print("Enter budget category: ")
        val category = readLine()?.trim() ?: return

        print("Enter expense amount: $")
        val amount = readLine()?.toBigDecimalOrNull()

        if (amount == null || amount <= BigDecimal.ZERO) {
            println("Invalid amount.")
            return
        }

        if (budgetManager.addExpenseToBudget(category, amount)) {
            println(" Expense added to budget.")
        } else {
            println("Budget not found or expense exceeds limit!")
        }
    }

    private fun viewBudgetStatus() {
        print("Enter budget category: ")
        val category = readLine()?.trim() ?: return

        val status = budgetManager.getBudgetStatus(category)
        if (status != null) {
            println("\n$status")
        } else {
            println("Budget not found.")
        }
    }

    private fun viewAllBudgets() {
        val budgets = budgetManager.getAllBudgets()

        if (budgets.isEmpty()) {
            println("No budgets found.")
            return
        }

        println("\n========== ALL BUDGETS ==========")
        for ((_, budget) in budgets) {
            println(budgetManager.getBudgetStatus(budget.category))
            println("-----------------------------------")
        }
    }

    private fun calculatorsMenu() {
        println("""
            
            ========== FINANCIAL CALCULATORS ==========
            1. Loan Payment Calculator
            2. Investment Future Value Calculator
            3. Compound Interest Calculator
            4. Back to Main Menu
            ==========================================
            Select an option: 
        """.trimMargin())

        when (readLine()?.trim()) {
            "1" -> loanCalculator()
            "2" -> investmentCalculator()
            "3" -> compoundInterestCalculator()
            "4" -> return
            else -> println("Invalid option.")
        }
    }

    private fun loanCalculator() {
        print("Enter loan principal: $")
        val principal = readLine()?.toBigDecimalOrNull() ?: return

        print("Enter annual interest rate (e.g., 0.05 for 5%): ")
        val rate = readLine()?.toBigDecimalOrNull() ?: return

        print("Enter loan term in years: ")
        val years = readLine()?.toIntOrNull() ?: return

        val monthlyPayment = LoanCalculator.calculateMonthlyPayment(principal, rate, years)
        val totalInterest = LoanCalculator.calculateTotalInterest(principal, monthlyPayment, years)

        println("""
            
            ========== LOAN CALCULATION RESULTS ==========
            Principal: ${principal.toFormattedCurrency()}
            Annual Rate: ${(rate * BigDecimal(100))}%
            Term: $years years
            Monthly Payment: ${monthlyPayment.toFormattedCurrency()}
            Total Interest: ${totalInterest.toFormattedCurrency()}
            Total Payment: ${(monthlyPayment * BigDecimal(years * 12)).toFormattedCurrency()}
            =============================================
        """.trimMargin())
    }

    private fun investmentCalculator() {
        print("Enter initial investment: $")
        val principal = readLine()?.toBigDecimalOrNull() ?: return

        print("Enter expected annual return rate (e.g., 0.07 for 7%): ")
        val rate = readLine()?.toBigDecimalOrNull() ?: return

        print("Enter investment period in years: ")
        val years = readLine()?.toIntOrNull() ?: return

        print("Enter monthly contribution (0 for none): $")
        val monthly = readLine()?.toBigDecimalOrNull() ?: BigDecimal.ZERO

        val futureValue = InvestmentCalculator.calculateFutureValue(principal, rate, years, monthly)
        val totalContributions = principal + (monthly * BigDecimal(years * 12))
        val totalEarnings = futureValue - totalContributions

        println("""
            
            ========== INVESTMENT PROJECTION ==========
            Initial Investment: ${principal.toFormattedCurrency()}
            Monthly Contribution: ${monthly.toFormattedCurrency()}
            Annual Return: ${(rate * BigDecimal(100))}%
            Time Period: $years year
            
            Total Contributions: ${totalContributions.toFormattedCurrency()}
            Total Earnings: ${totalEarnings.toFormattedCurrency()}
            Future Value: ${futureValue.toFormattedCurrency()}
            ==========================================
        """.trimMargin())
    }

    private fun compoundInterestCalculator() {
        print("Enter principal amount: $")
        val principal = readLine()?.toBigDecimalOrNull() ?: return

        print("Enter annual interest rate (e.g., 0.05 for 5%): ")
        val rate = readLine()?.toBigDecimalOrNull() ?: return

        print("Enter time period in years: ")
        val years = readLine()?.toIntOrNull() ?: return

        print("Enter compound frequency per year (12 for monthly): ")
        val frequency = readLine()?.toIntOrNull() ?: 12

        val finalAmount = InvestmentCalculator.calculateCompoundInterest(principal, rate, years, frequency)
        val interest = finalAmount - principal

        println("""
            
            ========== COMPOUND INTEREST RESULTS ==========
            Principal: ${principal.toFormattedCurrency()}
            Rate: ${(rate * BigDecimal(100))}% per year
            Time: $years years
            Compounding: $frequency times per year
            
            Interest Earned: ${interest.toFormattedCurrency()}
            Final Amount: ${finalAmount.toFormattedCurrency()}
            ==============================================
        """.trimMargin())
    }

    private fun reportsMenu() {
        println("""
            
            ========== REPORTS & ANALYTICS ==========
            1. Financial Summary Report
            2. Account Performance
            3. Budget Analysi
            4. Back to Main Menu
            ========================================
            Select an option: 
        """.trimMargin())

        when (readLine()?.trim()) {
            "1" -> println(financeManager.generateFinancialReport())
            "2" -> accountPerformanceReport()
            "3" -> budgetAnalysisReport()
            "4" -> return
            else -> println("Invalid option.")
        }
    }

    private fun accountPerformanceReport() {
        val accounts = financeManager.getAllAccounts()

        if (accounts.isEmpty()) {
            println("No accounts found.")
            return
        }

        println("\n========== ACCOUNT PERFORMANCE ==========")
        for (account in accounts) {
            val transactions = account.getTransactionHistory()
            println("""
                Account: ${account.accountNumber}
                Current Balance: ${account.getBalance().toFormattedCurrency()}
                Total Transactions: ${transactions.size}
                Total Income: ${transactions.totalIncome().toFormattedCurrency()}
                Total Expenses: ${transactions.totalExpenses().toFormattedCurrency()}
                Net Cash Flow: ${transactions.netCashFlow().toFormattedCurrency()}
                Projected Annual Interest: ${account.calculateInterest().toFormattedCurrency()}
                -----------------------------------
            """.trimMargin())
        }
    }

    private fun budgetAnalysisReport() {
        val budgets = budgetManager.getAllBudgets()

        if (budgets.isEmpty()) {
            println("No budgets found.")
            return
        }

        println("\n========== BUDGET ANALYSIS ==========")
        var totalLimit = BigDecimal.ZERO
        var totalSpent = BigDecimal.ZERO

        for ((_, budget) in budgets) {
            totalLimit += budget.limit
            totalSpent += budget.getSpent()

            val status = if (budget.isOverBudget()) "⚠ OVER BUDGET" else "✓ On Track"
            println("""
                ${budget.category}: ${status}
                  Limit: ${budget.limit.toFormattedCurrency()}
                  Spent: ${budget.getSpent().toFormattedCurrency()}
                  Remaining: ${budget.getRemaining().toFormattedCurrency()}
                  Utilization: ${budget.getUtilization()}%
            """.trimMargin())
        }

        println("""
            -----------------------------------
            OVERALL BUDGET SUMMARY:
            Total Budget Limit: ${totalLimit.toFormattedCurrency()}
            Total Spent: ${totalSpent.toFormattedCurrency()}
            Total Remaining: ${(totalLimit - totalSpent).toFormattedCurrency()}
            =====================================
        """.trimMargin())
    }
}