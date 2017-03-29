package macmist.com.accounts;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import macmist.com.accounts.Database.AccountsDbHelper;

/**
 * Created by quentin on 28/03/2017.
 */
public class AccountDetailsActivity extends AppCompatActivity {
    private AccountsDbHelper helper;
    int accountID;

    EditText nameEditText;
    EditText amountText;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_details_activity);

        accountID = getIntent().getIntExtra(MainActivity.KEY_EXTRA_CONTACT_ID, -1);
        nameEditText = (EditText) findViewById(R.id.editAccountName);
        amountText = (EditText) findViewById(R.id.accountDetailAmount);

        helper = new AccountsDbHelper(this);

        if (accountID > 0) {
            setAccountInfoToView();
            initSaveButton();
            initListView();
        }
    }

    public void setAccountInfoToView() {
        Cursor cursor = helper.getAccountById(accountID);
        cursor.moveToFirst();
        String accountName = cursor.getString(cursor.getColumnIndex(AccountsDbHelper.ACCOUNT_COLUMN_NAME));
        float accountAmount = cursor.getFloat(cursor.getColumnIndex(AccountsDbHelper.ACCOUNT_COLUMN_AMOUNT));
        if(!cursor.isClosed())
            cursor.close();

        nameEditText.setText(accountName);
        nameEditText.setFocusable(false);
        nameEditText.setClickable(false);

        amountText.setText((CharSequence)(accountAmount + ""));
        amountText.setFocusable(false);
        amountText.setClickable(false);
    }

    public void initSaveButton() {
        Button button = (Button) findViewById(R.id.addNewTransaction);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountDetailsActivity.this, CreateTransactionActivity.class);
                intent.putExtra(MainActivity.KEY_EXTRA_CONTACT_ID, accountID);
                startActivity(intent);
            }
        });
    }

    public void initListView() {

        listView = (ListView) findViewById(R.id.transactionList);
        //
        initAdaptor();
        addListenersToList();
    }

    public void initAdaptor() {
        final Cursor transactions = helper.getAccountTransactions(accountID);
        String[] columns = new String[] {
                AccountsDbHelper.TRANSACTION_COLUMN_ID,
                AccountsDbHelper.TRANSACTION_COLUMN_DATE,
                AccountsDbHelper.TRANSACTION_COLUMN_NAME,
                AccountsDbHelper.TRANSACTION_COLUMN_AMOUNT,
        };
        int[] widgets = new int[] {
                R.id.transactionId,
                R.id.transactionDate,
                R.id.transactionName,
                R.id.transactionAmount
        };

        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.transaction_list, transactions, columns, widgets, 0);
        listView.setAdapter(cursorAdapter);
    }

    public void addListenersToList() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView listView, View view, int position, long id) {
                Cursor itemCursor = (Cursor) AccountDetailsActivity.this.listView.getItemAtPosition(position);
                int accountID = itemCursor.getInt(itemCursor.getColumnIndex(AccountsDbHelper.TRANSACTION_COLUMN_ID));
                Toast.makeText(getApplicationContext(), "transaction " + accountID, Toast.LENGTH_SHORT).show();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor itemCursor = (Cursor) AccountDetailsActivity.this.listView.getItemAtPosition(position);
                final int transactionId = itemCursor.getInt(itemCursor.getColumnIndex(AccountsDbHelper.TRANSACTION_COLUMN_ID));
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                helper.deleteTransaction(transactionId);
                                setAccountInfoToView();
                                initAdaptor();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                return false;
            }
        });

    }

}
