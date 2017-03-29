package macmist.com.accounts.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by quentin on 27/03/2017.
 */
public class AccountsDbHelper extends SQLiteOpenHelper {
    // Database definition
    public static final String DATABASE_NAME = "Accounts.db";
    private static final int DATABASE_VERSION = 1;

    // Account table definition
    public static final String ACCOUNT_TABLE_NAME = "account";
    public static final String ACCOUNT_COLUMN_ID = "_id";
    public static final String ACCOUNT_COLUMN_NAME = "name";
    public static final String ACCOUNT_COLUMN_INITIAL_AMOUNT = "initial_amount";
    public static final String ACCOUNT_COLUMN_AMOUNT = "amount";

    // Transaction table definition
    public static final String TRANSACTION_TABLE_NAME = "transaction_table";
    public static final String TRANSACTION_COLUMN_ID = "_id";
    public static final String TRANSACTION_COLUMN_ACCOUNT_ID = "account_id";
    public static final String TRANSACTION_COLUMN_DATE = "date";
    public static final String TRANSACTION_COLUMN_NAME = "name";
    public static final String TRANSACTION_COLUMN_AMOUNT = "amount";


    public  AccountsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public  void onCreate(SQLiteDatabase db) {
        // Create the account table
        String accountCreation = "CREATE TABLE IF NOT EXISTS " + ACCOUNT_TABLE_NAME + "("
                                + ACCOUNT_COLUMN_ID + " INTEGER PRIMARY KEY, "
                                + ACCOUNT_COLUMN_NAME + " TEXT NOT NULL, "
                                + ACCOUNT_COLUMN_INITIAL_AMOUNT + " REAL NOT NULL, "
                                + ACCOUNT_COLUMN_AMOUNT + " REAL NOT NULL)";
        db.execSQL(accountCreation);

        // Then the transaction table with account foreign key
        String transactionCreation = "CREATE TABLE IF NOT EXISTS " + TRANSACTION_TABLE_NAME + "("
                                    + TRANSACTION_COLUMN_ID + " INTEGER PRIMARY KEY, "
                                    + TRANSACTION_COLUMN_ACCOUNT_ID + " INTEGER, "
                                    + TRANSACTION_COLUMN_DATE + " TEXT NOT NULL, "
                                    + TRANSACTION_COLUMN_NAME + " TEXT, "
                                    + TRANSACTION_COLUMN_AMOUNT + " REAL NOT NULL, "
                                    + "FOREIGN KEY(" +  TRANSACTION_COLUMN_ACCOUNT_ID
                                    +") REFERENCES " + ACCOUNT_TABLE_NAME + "(" + ACCOUNT_COLUMN_ID + "));";

        db.execSQL(transactionCreation);

        String triggerTransaction = "CREATE TRIGGER IF NOT EXISTS TRANSACTION_TRIGGER  AFTER INSERT ON " + TRANSACTION_TABLE_NAME
                                    + " BEGIN "
                                    + "UPDATE " + ACCOUNT_TABLE_NAME + " SET " + ACCOUNT_COLUMN_AMOUNT
                                    + " = " + ACCOUNT_COLUMN_AMOUNT + " + new." + TRANSACTION_COLUMN_AMOUNT
                                    + " WHERE " + ACCOUNT_COLUMN_ID + " = new." + TRANSACTION_COLUMN_ACCOUNT_ID + ";"
                                    + " END";
        db.execSQL(triggerTransaction);

        String triggerDeleteTransaction = "CREATE TRIGGER IF NOT EXISTS DELETE_TRANSACTION_TRIGGER  AFTER DELETE ON " + TRANSACTION_TABLE_NAME
                + " BEGIN "
                + "UPDATE " + ACCOUNT_TABLE_NAME + " SET " + ACCOUNT_COLUMN_AMOUNT
                + " = " + ACCOUNT_COLUMN_AMOUNT + " - old." + TRANSACTION_COLUMN_AMOUNT
                + " WHERE " + ACCOUNT_COLUMN_ID + " = old." + TRANSACTION_COLUMN_ACCOUNT_ID + ";"
                + " END";
        db.execSQL(triggerDeleteTransaction);
    }

    @Override
    public  void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // For now we delete but maybe we'll do something else on upgrade
        db.execSQL("DROP TABLE IF EXISTS " + TRANSACTION_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ACCOUNT_COLUMN_ID);
        onCreate(db);
    }

    // # Operations on account table

    // Insertion
    public boolean insertAccount(String name, float amount) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ACCOUNT_COLUMN_NAME, name);
        contentValues.put(ACCOUNT_COLUMN_INITIAL_AMOUNT, amount);
        contentValues.put(ACCOUNT_COLUMN_AMOUNT, amount);
        long res = db.insert(ACCOUNT_TABLE_NAME, null, contentValues);
        return res != -1;
    }

    // Update
    public boolean updateAccount(int id, String name, float initialAmount, float amount) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ACCOUNT_COLUMN_NAME, name);
        contentValues.put(ACCOUNT_COLUMN_INITIAL_AMOUNT, initialAmount);
        contentValues.put(ACCOUNT_COLUMN_AMOUNT, amount);
        long res = db.update(ACCOUNT_TABLE_NAME, contentValues, ACCOUNT_COLUMN_ID + " = ? ",  new String[] { Integer.toString(id) } );
        return res != -1;
    }

    public Cursor getAccountById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + ACCOUNT_TABLE_NAME + " WHERE " +
                ACCOUNT_COLUMN_ID + "=?", new String[] { Integer.toString(id) } );
        return res;
    }

    public Cursor getAllAccounts() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + ACCOUNT_TABLE_NAME, null );
        return res;
    }


    // # Operations on transaction table

    // Insertion
    public boolean insertTransaction(int account, String name, float amount) {
        SQLiteDatabase db = getWritableDatabase();

        DateFormat sdf = SimpleDateFormat.getDateInstance();
        String date = sdf.format(new Date());


        ContentValues contentValues = new ContentValues();
        contentValues.put(TRANSACTION_COLUMN_ACCOUNT_ID, account);
        contentValues.put(TRANSACTION_COLUMN_DATE, date);
        contentValues.put(TRANSACTION_COLUMN_NAME, name);
        contentValues.put(TRANSACTION_COLUMN_AMOUNT, amount);
        long res = db.insert(TRANSACTION_TABLE_NAME, null, contentValues);
        return res != -1;
    }

    // Update
    public boolean updateTransaction(int id, int account, String name, float amount) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TRANSACTION_COLUMN_ACCOUNT_ID, account);
        contentValues.put(TRANSACTION_COLUMN_NAME, name);
        contentValues.put(TRANSACTION_COLUMN_AMOUNT, amount);
        long res = db.update(TRANSACTION_TABLE_NAME, contentValues, TRANSACTION_COLUMN_ID + " = ? ",  new String[] { Integer.toString(id) } );
        return res != -1;
    }

    public Cursor getTransactionById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + TRANSACTION_TABLE_NAME + " WHERE " +
                TRANSACTION_COLUMN_ID + "=?", new String[] { Integer.toString(id) } );
        return res;
    }

    public Cursor getAllTransactions() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + TRANSACTION_TABLE_NAME, null );
        return res;
    }

    public Cursor getAccountTransactions(int account) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + TRANSACTION_TABLE_NAME + " WHERE " +
                TRANSACTION_COLUMN_ACCOUNT_ID + "=?", new String[] { Integer.toString(account) }  );
        return res;
    }

    public Integer deleteTransaction(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.delete(TRANSACTION_TABLE_NAME, TRANSACTION_COLUMN_ID + " = ? ",
                new String[] {Integer.toString(id)});
    }
}
