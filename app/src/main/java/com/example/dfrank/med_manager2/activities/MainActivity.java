package com.example.dfrank.med_manager2.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.dfrank.med_manager2.Adapter.MedCursorAdapter;
import com.example.dfrank.med_manager2.Medication;
import com.example.dfrank.med_manager2.Profile;
import com.example.dfrank.med_manager2.R;
import com.example.dfrank.med_manager2.User;
import com.example.dfrank.med_manager2.data.MedManagerContract;
import com.example.dfrank.med_manager2.data.MedManagerHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import reminder.AlarmScheduler;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>
         {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAustatelistener;
    private GoogleSignInClient mGoogleSignIn;
    private MedCursorAdapter medCursorAdapter;
    private List<Medication> medicationList;
    private MedManagerHelper managerHelper;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.recyclerViewMed)
    RecyclerView recyclerView;

    private static final int MED_LOADER_ID = 0;
    private static final String TAG = MainActivity.class.getSimpleName();

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        managerHelper = new MedManagerHelper(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start new Activity
                Intent intent = new Intent(MainActivity.this, AddMedication.class);
                startActivity(intent);
            }
        });
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("User");

        User user = new User(getName(), getEmail(), getPhoto());

        mDatabase.child(getUid()).setValue(user);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        loadImage();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder()
                .build();
        mGoogleSignIn = GoogleSignIn.getClient(this, gso);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_logout:
                        alertDialog();
                        break;
                    case R.id.nav_addMed:
                        Intent intent = new Intent(getApplicationContext(), AddMedication.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_searchMed:
                        Intent intent1 = new Intent(getApplicationContext(),SearchActivity.class);
                        startActivity(intent1);
                        break;

                }

                item.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });

        medicationList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        medCursorAdapter = new MedCursorAdapter(this);
        recyclerView.setAdapter(medCursorAdapter);

        getLoaderManager().initLoader(MED_LOADER_ID, null, this);


        //Implementing swipe for delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //getting the row Id
                int id = (int) viewHolder.itemView.getTag();
                String stringId = Integer.toString(id);
                Uri uri =MedManagerContract.MedManagerEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();
                //deleting medication
                new AlarmScheduler().cancelAlarm(getApplicationContext(), uri);
                getContentResolver().delete(uri, null, null);
                getLoaderManager().restartLoader(MED_LOADER_ID, null, MainActivity.this);
            }


        }).attachToRecyclerView(recyclerView);



    }

    private void alertDialog(){
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Do you really want to Log out");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                signOut();
                finish();
            }
        });
        alert.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Restarting loader to update view
    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(MED_LOADER_ID, null, this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getLoaderManager().restartLoader(MED_LOADER_ID, null, this);
    }

    @SuppressLint("RestrictedApi")
    private void signOut(){
//        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//
//            }
//        });
        mAuth.signOut();
        mGoogleSignIn.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }

    private String getUid(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
    private String getName(){
        return mAuth.getCurrentUser().getDisplayName();
    }

    private Uri getPhoto(){
        return mAuth.getCurrentUser().getPhotoUrl();
    }
    private String getEmail(){
        return mAuth.getCurrentUser().getEmail();
    }

    private void loadImage(){
        View view = navigationView.getHeaderView(0);
        CircleImageView profileImage = view.findViewById(R.id.profile_image);
        if (getPhoto()!=null){
            Glide.with(this)
                    .load(getPhoto())
                    .into(profileImage);
        }

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Profile.class);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logOut:
                alertDialog();
                break;
            case R.id.profile:
                startActivity(new Intent(this,Profile.class));
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.search:
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.refresh:
                getLoaderManager().restartLoader(MED_LOADER_ID, null, this);
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // This loader will execute the ContentProvider's query method on a background thread
//        return new CursorLoader(this,   // Parent activity context
//                MedManagerContract.MedManagerEntry.CONTENT_URI,         // Query the content URI for the current reminder
//                projection,             // Columns to include in the resulting Cursor
//                null,                   // No selection clause
//                null,                   // No selection arguments
//                null);
        return new AsyncTaskLoader<Cursor>(this) {
            Cursor mTaskData;

            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mTaskData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }


            @Override
            public Cursor loadInBackground() {

                try {
                    return getContentResolver().query(MedManagerContract.MedManagerEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }
            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //Change the cursor
        medCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        medCursorAdapter.swapCursor(null);
    }

}
