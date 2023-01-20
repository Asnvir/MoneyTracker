package com.example.moneytracker;



import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;


public class DashBoardActivity extends AppCompatActivity {


    CardView btn_add;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        findViews();
        registerListeners();
        initializeDataFlow();
    }

    private void findViews() {
        btn_add = findViewById(R.id.dashboard_BTN_add);
        recyclerView = findViewById(R.id.dashboard_VIEW_recyclerview);
    }

    private void registerListeners() {
        btn_add.setOnClickListener(v -> openAddTransactionActivity());
    }

    private void openAddTransactionActivity() {
        Intent intent = new Intent(DashBoardActivity.this, AddTransactionActivity.class);
        finish();
        startActivity(intent);
    }

//    private void initializeDataFlow() {
//        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
//        DataModifier dataModifier = new DataModifier();
//        UiUpdater uiUpdater = new UiUpdater(this, recyclerView);
//
//        databaseHandler.downloadData(dataSnapshot -> {
//            ModifiedData modifiedData = dataModifier.modifyData(dataSnapshot);
//            uiUpdater.updateUI(modifiedData);
//        });
//    }

    private void initializeDataFlow() {
        DataRetriever dataRetriever = new DataRetriever();
        DataModifier dataModifier = new DataModifier();
        UiUpdater uiUpdater = new UiUpdater(this, recyclerView);

        dataRetriever.downloadData(new DataRetriever.Callback() {
            @Override
            public void onDataReceived(DataSnapshot dataSnapshot) {
                ModifiedData modifiedData = dataModifier.modifyData(dataSnapshot);
                uiUpdater.updateUI(modifiedData);
            }
        });
    }


}


