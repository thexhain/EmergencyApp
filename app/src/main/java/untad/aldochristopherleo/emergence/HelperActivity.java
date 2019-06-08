package untad.aldochristopherleo.emergence;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class HelperActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    String tipe;
    Toolbar toolbar = null;
    LinearLayout layHeader;
    ImageView imgProfile;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    EditText edtNama, edtEmail;
    private DrawerLayout drawer;
    private boolean doubleBackPressed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper);

        mAuth = FirebaseAuth.getInstance();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navView);
        View hView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        edtNama = hView.findViewById(R.id.idNama);
        edtEmail = hView.findViewById(R.id.idEmail);
        imgProfile = hView.findViewById(R.id.imgProf);
        layHeader = hView.findViewById(R.id.navHeader);


        if (mAuth.getCurrentUser() != null && edtEmail != null) {
            Log.d(null, mAuth.getCurrentUser().getEmail());
            edtEmail.setText(mAuth.getCurrentUser().getEmail());
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference("User").child(mAuth.getCurrentUser().getUid());
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    edtNama.setText(dataSnapshot.child("nama").getValue().toString());
                    tipe = dataSnapshot.child("tipe").getValue().toString();
                    if (tipe.equals("Pengguna")){
                        imgProfile.setImageResource(R.drawable.user);
                        toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        layHeader.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    } else if(tipe.equals("Polisi")){
                        imgProfile.setImageResource(R.drawable.amb);
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("Damkar");
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("Rumsak");
                        FirebaseMessaging.getInstance().subscribeToTopic("Polisi");
                        toolbar.setBackgroundColor(Color.parseColor("#3F48CC"));
                        layHeader.setBackgroundColor(Color.parseColor("#3F48CC"));
                    } else if(tipe.equals("Pemadam Kebakaran")){
                        imgProfile.setImageResource(R.drawable.firetruckicon);
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("Polisi");
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("Rumsak");
                        FirebaseMessaging.getInstance().subscribeToTopic("Damkar");
                        toolbar.setBackgroundColor(Color.parseColor("#C70000"));
                        layHeader.setBackgroundColor(Color.parseColor("#C70000"));
                    } else if(tipe.equals("Rumah Sakit")){
                        imgProfile.setImageResource(R.drawable.policecaricon);
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("Polisi");
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("Damkar");
                        FirebaseMessaging.getInstance().subscribeToTopic("Rumsak");
                        toolbar.setBackgroundColor(Color.parseColor("#86C64D"));
                        layHeader.setBackgroundColor(Color.parseColor("#86C64D"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        edtEmail.setEnabled(false);
        edtNama.setEnabled(false);

        toolbar.setTitle("Beranda");
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentLayout, new HelperHomeFragment()).commit();
            navigationView.setCheckedItem(R.id.menuBeranda2);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            if (doubleBackPressed){
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
            } else {
                doubleBackPressed = true;
                Toast.makeText(this, "Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackPressed = false;
                    }
                }, 2000);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.menuBeranda2:
                toolbar.setTitle("Beranda");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentLayout, new HelperHomeFragment()).commit();
                break;
            case R.id.menuLog:
                toolbar.setTitle("Log");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentLayout, new LogFragment()).commit();
                break;
            case R.id.menuLogout:
                mAuth.signOut();
                finish();
                startActivity(new Intent(HelperActivity.this, MainActivity.class));
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
