package macmist.com.accounts;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
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
    private static Point screenSize;

    public static Point getScreenSize() {
        return  screenSize;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Display display = getWindowManager().getDefaultDisplay();
        screenSize = new Point();
        display.getSize(screenSize);


        Button button = (Button) findViewById(R.id.addNew);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateOrEditActivity.class);
                intent.putExtra(KEY_EXTRA_CONTACT_ID, -1);
                startActivity(intent);
            }
        });

        helper = new AccountsDbHelper(this);

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
        listView = (ListView)findViewById(R.id.listView1);
        listView.setAdapter(cursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView listView, View view,
                                    int position, long id) {
                Cursor itemCursor = (Cursor) MainActivity.this.listView.getItemAtPosition(position);
                int accountID = itemCursor.getInt(itemCursor.getColumnIndex(AccountsDbHelper.ACCOUNT_COLUMN_ID));
                Intent intent = new Intent(getApplicationContext(), CreateOrEditActivity.class);
                intent.putExtra(KEY_EXTRA_CONTACT_ID, accountID);
                startActivity(intent);
            }
        });
    }
}
