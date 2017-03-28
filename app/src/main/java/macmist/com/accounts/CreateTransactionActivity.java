package macmist.com.accounts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import macmist.com.accounts.Database.AccountsDbHelper;

/**
 * Created by quentin on 29/03/2017.
 */
public class CreateTransactionActivity extends AppCompatActivity implements View.OnClickListener {
    private AccountsDbHelper helper;
    int accountId;
    EditText nameEditText;
    EditText amountText;

    Button saveButton;

    ScrollView scrollView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_transaction_activity);

        accountId = getIntent().getIntExtra(MainActivity.KEY_EXTRA_CONTACT_ID, -1);

        nameEditText = (EditText) findViewById(R.id.editTextTransactionName);
        amountText = (EditText) findViewById(R.id.editTextTransactionAmount);

        saveButton = (Button) findViewById(R.id.saveTransactionButton);
        saveButton.setOnClickListener(this);

        scrollView = (ScrollView) findViewById(R.id.scrollView1);

        helper = new AccountsDbHelper(this);

    }

    @Override
    public void onClick(View view) {
        if(helper.insertTransaction(accountId, nameEditText.getText().toString(),
                Float.parseFloat(amountText.getText().toString()))) {
            Toast.makeText(getApplicationContext(), getString(R.string.account_inserted_ok), Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(), getString(R.string.account_inserted_error), Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(getApplicationContext(), AccountDetailsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(MainActivity.KEY_EXTRA_CONTACT_ID, accountId);
        startActivity(intent);
    }
}
