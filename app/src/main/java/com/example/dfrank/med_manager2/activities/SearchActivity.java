package com.example.dfrank.med_manager2.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.dfrank.med_manager2.Adapter.MedAdapter;
import com.example.dfrank.med_manager2.Adapter.SearchAdapter;
import com.example.dfrank.med_manager2.Medication;
import com.example.dfrank.med_manager2.R;
import com.example.dfrank.med_manager2.data.MedManagerHelper;
import com.example.dfrank.med_manager2.data.MedManagerProvider;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dfrank on 4/12/18.
 */

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    List<Medication> medicationList;
    MedManagerHelper managerHelper;
    private SearchAdapter searchAdapter;
    @BindView(R.id.recyclerViewMed)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_searchable);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);

        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        initView();
    }

    private void initView(){
        medicationList = new ArrayList<>();
        searchAdapter = new SearchAdapter(medicationList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(searchAdapter);
        managerHelper = new MedManagerHelper(getApplicationContext());

        getDataDatabase();
    }

    private void getDataDatabase(){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                //Loading data to List
                medicationList.clear();
                medicationList.addAll(managerHelper.getAllMedication());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                searchAdapter.notifyDataSetChanged();
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchmenu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(this);
        searchItem.expandActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();
        ArrayList<Medication> newList = new ArrayList<>();
        for(Medication medication: medicationList){
            Log.d("Med: ", medicationList.toString());
            String name = medication.getTitle().toLowerCase();
            if (name.contains(newText)){
                newList.add(medication);
            }
        }
        searchAdapter.setFilter(newList);
        return true;
    }
}
