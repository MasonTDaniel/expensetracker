import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExpenseTracker {
	private static Connection con;
	private static Scanner userInput;

	/**
	 * This method connects to the database
	 * @return - a connection to the database
	 */
	public static Connection getConnection() {
		try {
			Class.forName("org.sqlite.JDBC");				//find our SQL-connector
			con = DriverManager.getConnection("jdbc:sqlite:ExpenseTrackerTest.db");		//set the connection to a new database
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println("Could not connect to database.");
			e.printStackTrace();			//otherwise, throw an error
		} 
		return con;


	}

	/**
	 * This will perform a SQL statemenet that creates a new expense table
	 */
	public static void createExpenseTable() {
		//connect to database if not already
		if (con == null) {
			getConnection();
		}
		//try to send a query to SQL
		try {
			PreparedStatement createStmnt;	//will store the SQL query we want to perform
			createStmnt = con.prepareStatement("CREATE TABLE IF NOT EXISTS expense (id INTEGER PRIMARY KEY, "
					+ "expenseName varchar(60), "
					+ "expenseAmount double, "
					+ "expenseDate varchar(60), "
					+ "expenseCategory int);");			//store the query to our ref var createStmnt
			createStmnt.executeUpdate();			//execute the query
		} catch (SQLException e) {
			e.printStackTrace();		//otherwise, throw error
		} finally{
			System.out.println("Expense table created");	//display in console that expense table was created
		}

	}

	/**
	 * This method will perform an SQL statement that creates a new category table
	 */
	public static void createCategoryTable() {
		//connect to database if not already
		if (con == null) {
			getConnection();
		}
		//try to send a query to SQL
		try {
			PreparedStatement createStmnt;		//will store the SQL query we want to perform
			createStmnt = con.prepareStatement("CREATE TABLE IF NOT EXISTS category (id INTEGER PRIMARY KEY, "
					+ "categoryName varchar(60));"); //store the query to our ref var createStmnt
			createStmnt.executeUpdate();		//execute the query
		} catch (SQLException e) {
			e.printStackTrace();		//otherwise, throw error
		} finally{
			System.out.println("Category table created");
		}
	}

	public static void addExpense() {
		final String expenseName;
		final double expenseAmount;
		final String expenseDate;
		final String expenseCategory;
		//connect to database if not already
		if (con == null) {
			getConnection();
		}

		expenseName = setExpenseName();
		expenseAmount = setExpenseAmount();
		expenseDate = setExpenseDate();
		expenseCategory = setExpenseCategory();

		//try to query database to insert new values to expense table
		try {
			PreparedStatement createStmnt;		//var to hold the query
			createStmnt = con.prepareStatement("INSERT INTO expense (expenseName, expenseAmount, expenseDate, expenseCategory) "
					+ "VALUES ('"+expenseName+"', "+expenseAmount+", '"+expenseDate+"', '"+expenseCategory+"')");	//query for insertion
			createStmnt.executeUpdate();		//execute insertion query
		} catch (SQLException e) {
			e.printStackTrace();		//otherwise, throw error
		} finally {
			System.out.println("Expense added."); 
		}
	}

	private static String setExpenseName() {
		userInput = new Scanner(System.in);
		for(;;) {
			System.out.print("Enter the expense name and press enter when done: ");
			if(userInput.hasNextLine()) {
				String input = userInput.nextLine();
				return input;
			} 
			System.out.println("Please provide an expense name.");
		}
	}

	private static double setExpenseAmount() {
		for(;;) {
			System.out.print("Enter the expense amount (X.XX) and press enter when done: ");
			if(userInput.hasNextLine()) {
				String input = userInput.nextLine();
				if(isDouble(input)) {
					double value = Double.parseDouble(input);
					NumberFormat formatter = new DecimalFormat("#.00"); 
					formatter.format(value);
					return value;
				}
			}
			System.out.println("Please answer in the form of X.XX.");
		}	
	}

	private static boolean isDouble(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private static String setExpenseDate() {
		for(;;) {
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			sdf.setLenient(false);
			System.out.print("Please enter a date (mm/dd/yyyy): ");
			String dateInput = userInput.nextLine();
			try{
				sdf.parse(dateInput);
				return dateInput;
			}
			catch(Exception e)
			{
				System.out.println(dateInput+" is not a valid Date");
			}	
		}
	}


	private static String setExpenseCategory() {
		System.out.println("Please enter the category number the expense falls under.");
		for(;;) {
			System.out.println("Categories:\n1. Food\n2. Transportation\n3. School Supplies\n4. Clothes\n5. Fun");
			System.out.println("Enter category number here: ");
			if (userInput.hasNextInt()) {
				int input = userInput.nextInt();
				switch (input) {
				case 1:
					return "Food";
				case 2:
					return "Transportation";
				case 3:
					return "School Supplies";
				case 4:
					return "Clothes";
				case 5:
					return "Fun";
				default:
					userInput.nextLine();
					break;
				}
				System.out.println("Please enter a category number (e.g. 3)"); 
			}
		}
	}

	/**
	 * This method takes the results from a query(which is performed in another method called by this method) 
	 * and prints out the individual fields(name, amount, date, and category) for each expense
	 */
	public static void displayAllExpenses() {
		try {
			ResultSet rs;		//will store the results of the query
			System.out.println("Listing all current expenses...");
			System.out.println("All current expenses:");
			rs = displayAllExpensesQuery();			//store the results of the query to rs
			//while rs still has results to show
			while(rs.next()) {
				System.out.println(rs.getString("id") + ": " 
						+ rs.getString("expenseName") 
						+ " " + rs.getString("expenseAmount") 
						+ " " + rs.getString("expenseDate") 
						+ " " + rs.getString("expenseCategory"));	//print out an expense
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();				//otherwise, throw error
		} 
		System.out.println("All expenses listed.");
	}


	/**
	 * Method used by displayAllExpenses that simply performs the SELECT query
	 * @return - a set of results from the query (expenses)
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private static ResultSet displayAllExpensesQuery() throws ClassNotFoundException, SQLException {
		//connect to database if not already
		if (con == null) {
			getConnection();
		}

		Statement state = con.createStatement();
		ResultSet res = state.executeQuery("SELECT id, expenseName, expenseAmount, expenseDate, expenseCategory FROM expense");	//perform query and store in res
		return res;  //return res, which holds results from the query
	}



}
