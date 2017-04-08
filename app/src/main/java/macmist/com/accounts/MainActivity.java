package macmist.com.accounts;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import macmist.com.accounts.Database.AccountsDbHelper;

public class MainActivity extends AppCompatActivity {
    public final static String KEY_EXTRA_CONTACT_ID = "KEY_EXTRA_CONTACT_ID";

    private ListView listView;
    AccountsDbHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new AccountsDbHelper(this);
        initView();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        initListView();
    }

    public void initView() {
        initButton();
        initListView();
    }

    public void initButton() {
        Button button = (Button) findViewById(R.id.addNew);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateAccountActivity.class);
                startActivity(intent);
            }
        });
    }

    public void initListView() {
        listView = (ListView)findViewById(R.id.listView1);
        initAdaptor();
        addListenersToList();
    }

    public void initAdaptor() {
        final Cursor cursor = helper.getAllAccounts();

        String [] columns = new String[] {
                AccountsDbHelper.ACCOUNT_COLUMN_ID,
                AccountsDbHelper.ACCOUNT_COLUMN_NAME,
                AccountsDbHelper.ACCOUNT_COLUMN_AMOUNT
        };
        int [] widgets = new int[] {
                R.id.accountId,
                R.id.accountName,
                R.id.accountAmount
        };
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.account_list, cursor, columns, widgets, 0);
        listView.setAdapter(cursorAdapter);
    }

    public void addListenersToList() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView listView, View view,
                                    int position, long id) {
                Cursor itemCursor = (Cursor) MainActivity.this.listView.getItemAtPosition(position);
                int accountID = itemCursor.getInt(itemCursor.getColumnIndex(AccountsDbHelper.ACCOUNT_COLUMN_ID));
                Intent intent = new Intent(getApplicationContext(), AccountDetailsActivity.class);
                intent.putExtra(KEY_EXTRA_CONTACT_ID, accountID);
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor itemCursor = (Cursor)MainActivity.this.listView.getItemAtPosition(position);
                final int accountId = itemCursor.getInt(itemCursor.getColumnIndex(AccountsDbHelper.ACCOUNT_COLUMN_ID));
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                helper.deleteAccount(accountId);
                                initAdaptor();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage(R.string.delete_account_dialog).setPositiveButton(R.string.yes, dialogClickListener)
                        .setNegativeButton(R.string.no, dialogClickListener).show();
                return true;
            }
        });

    }
}
