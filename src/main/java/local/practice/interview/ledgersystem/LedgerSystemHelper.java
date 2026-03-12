package local.practice.interview.ledgersystem;

public class LedgerSystemHelper {

    public static boolean isValidAmount(double amount) {
        return amount >= 0.0;
    }

    public static boolean isEnoughBalance(double balance, double amountToWithdraw) {
        return balance - amountToWithdraw >= 0.0;
    }

//A = P(1 + r/n) ^ (n * t), where A is the final amount, P is the principal, r is the annual interest rate (as a decimal),
// n is the number of times interest is compounded per year, and t is the time in years.
// Return the interest earned (A - P)
    public class SavingsAccount {
        private final Object dedicatedLock = new Object();
        public double calculateCompoundInterest(double principal, double annualRate, int years, int compoundsPerYear) {
            synchronized (dedicatedLock) {
                if (compoundsPerYear == 0)
                    throw new IllegalArgumentException("The number of times interest is compounded per year can not be 0");
                if (principal < 0 || annualRate < 0 || years < 0 || compoundsPerYear < 0)
                    throw new IllegalArgumentException("The parameters can not be negative numbers.");
                double finalAmount = Math.pow(principal * (1 + annualRate/compoundsPerYear), (compoundsPerYear * years));
                return finalAmount - principal;
            }
        }
    }

}
