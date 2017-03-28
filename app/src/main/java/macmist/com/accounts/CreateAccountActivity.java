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
 * Created by quentin on 28/03/2017.
 */
public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener {
    private AccountsDbHelper helper;
    int accountID;

    EditText nameEditText;
    EditText amountText;

    Button saveButton;

    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_account_activity);
        accountID = getIntent().getIntExtra(MainActivity.KEY_EXTRA_CONTACT_ID, -1);
        nameEditText = (EditText) findViewById(R.id.editTextName);
        amountText = (EditText) findViewById(R.id.editTextAmount);

        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);

        scrollView = (ScrollView) findViewById(R.id.scrollView1);

        helper = new AccountsDbHelper(this);

    }

    @Override
    public void onClick(View view) {
        if(helper.insertAccount(nameEditText.getText().toString(),
                Float.parseFloat(amountText.getText().toString()))) {
            Toast.makeText(getApplicationContext(), getString(R.string.account_inserted_ok), Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(), getString(R.string.account_inserted_error), Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
