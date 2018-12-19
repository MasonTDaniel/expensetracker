import java.sql.*;

public class ExpenseTrackerRun {

	public static void main(String[] args) {
		ExpenseTracker demo = new ExpenseTracker();
		ResultSet rs;
//		ExpenseTracker.createExpenseTable();
//		ExpenseTracker.createCategoryTable();
		ExpenseTracker.displayAllExpenses();
		ExpenseTracker.addExpense();
		ExpenseTracker.displayAllExpenses();
	}

}


//TODO add delete expenses, fix bug when adding blank expense, fix double decimal rounding