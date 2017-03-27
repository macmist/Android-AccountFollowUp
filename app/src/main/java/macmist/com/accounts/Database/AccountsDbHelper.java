package macmist.com.accounts.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by quentin on 27/03/2017.
 */
public class AccountsDbHelper extends SQLiteOpenHelper {
    // Database definition
    public static final String DATABASE_NAME = "Accounts.db";
    private static final int DATABASE_VERSION = 1;

    // Account table definition
    public static final String ACCOUNT_TABLE_NAME = "account";
    public static final String ACCOUNT_COLUMN_ID = "id";
    public static final String ACCOUNT_COLUMN_NAME = "name";
    public static final String ACCOUNT_COLUMN_INITIAL_AMOUNT = "initial_amount";
    public static final String ACCOUNT_COLUMN_AMOUNT = "amount";

    // Transaction table definition
    public static final String TRANSACTION_TABLE_NAME = "account";
    public static final String TRANSACTION_COLUMN_ID = "id";
    public static final String TRANSACTION_COLUMN_ACCOUNT_ID = "account_id";
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
                                + ACCOUNT_COLUMN_INITIAL_AMOUNT + " INTEGER NOT NULL, "
                                + ACCOUNT_COLUMN_AMOUNT + " INTEGER NOT NULL)";
        db.execSQL(accountCreation);

        // Then the transaction table with account foreign key
        String transactionCreation = "CREATE TABLE IF NOT EXISTS " + TRANSACTION_TABLE_NAME + "("
                                    + TRANSACTION_COLUMN_ID + " INTEGER PRIMARY KEY, "
                                    + TRANSACTION_COLUMN_ACCOUNT_ID + " INTEGER, "
                                    + TRANSACTION_COLUMN_NAME + " TEXT, "
                                    + TRANSACTION_COLUMN_AMOUNT + " INTEGER NOT NULL, "
                                    + "FOREIGN KEY(" +  TRANSACTION_COLUMN_ACCOUNT_ID
                                    +") REFERENCES " + ACCOUNT_TABLE_NAME + "(" + ACCOUNT_COLUMN_ID + "))";

        db.execSQL(transactionCreation);
    }

    @Override
    public  void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // For now we delete but maybe we'll do something else on upgrade
        db.execSQL("DROP TABLE IF EXISTS " + TRANSACTION_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ACCOUNT_COLUMN_ID);
        onCreate(db);
    }


}
