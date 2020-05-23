package com.b07.salesandroid.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.b07.database.helper.DatabaseAndroidHelper;
import com.b07.salesandroid.R;
import com.b07.salesandroid.models.AccountModel;
import com.b07.users.Admin;
import com.b07.users.Customer;
import com.b07.users.User;

import java.util.ArrayList;
import java.util.List;

public class AccountChoose extends AppCompatActivity implements View.OnClickListener {
    User customer;
    DatabaseAndroidHelper databaseAndroidHelper;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("AccountChoose");
        customer = (Customer) getIntent().getSerializableExtra("User");
        System.out.println(customer.getName());
        setContentView(R.layout.account_choose);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Button selectAccount = findViewById(R.id.selectAccount);
        selectAccount.setOnClickListener(this);
        databaseAndroidHelper = new DatabaseAndroidHelper(this);
        List<Integer> ids = databaseAndroidHelper.getUserActiveAccountIdsHelper(customer);
        List<CharSequence> customers = new ArrayList<CharSequence>();
        for (Integer i: ids){
            customers.add(i.toString());
            System.out.println(i);

        }
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, customers);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner drop = findViewById(R.id.avaliableAccounts);
        drop.setAdapter(adapter);
    }

    public void onClick(View view){
        Spinner option = findViewById(R.id.avaliableAccounts);
        String account = option.getSelectedItem().toString();
        Intent intent;
        intent = new Intent(this, CustomerView.class);
        System.out.println(customer.getName() + "iiiiii");
        intent.putExtra("User", customer);
        intent.putExtra("Account", Integer.parseInt(account));
        this.startActivity(intent);
//        String text = getResources().getStringArray(DatabaseAndroidHelper.getUserActiveAccountIdsHelper(user));
    }
}
