package untad.aldochristopherleo.emergence;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HelperFragment extends Fragment implements OnMapReadyCallback, AdapterView.OnItemSelectedListener, View.OnClickListener {
    EditText edtDesc;
    Button btnPost;
    Spinner spinner;
    NavigationView navigationView;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    GoogleMap gMap;
    Location curLoc;
    String telp;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_helper, container, false);
        spinner = view.findViewById(R.id.spTujuan);
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(getActivity(), R.array.spTipe2, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (adapter.isEmpty()) {
            Log.d(null, "0");
        }
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        btnPost = view.findViewById(R.id.btnLaporan);
        edtDesc = view.findViewById(R.id.edtDesc);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("User"). child(mAuth.getCurrentUser().getUid()).child("telp");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                telp = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(null, "9");
                if (curLoc != null) {
                    postHelp();
                } else {
                    Log.d(null, "NULL");
                }

            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.helperMap);
        Log.d(null, "1");
//if (mapFragment == null) {
        Log.d(null, "2");
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Log.d(null, "3");
        mapFragment = SupportMapFragment.newInstance();
        ft.replace(R.id.helperMap, mapFragment).commit();
//}
        Log.d(null, "4");
        mapFragment.getMapAsync(this);

        navigationView = getActivity().findViewById(R.id.navView);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fetchLastLocation();
        return view;
    }

    private void postHelp() {
        String posLat = String.valueOf(curLoc.getLatitude());
        String posLng = String.valueOf(curLoc.getLongitude());
        String desc = edtDesc.getText().toString();
        String tipe = spinner.getSelectedItem().toString();
        String email = mAuth.getCurrentUser().getEmail();

        Post post = new Post(posLat, posLng, desc, email, telp);
        Log.d(null, post.getTelp());
        if (tipe.equals("Polisi")) {
            databaseReference = firebaseDatabase.getReference("Post").child("Polisi");
            Log.d(null, "11");
        } else if (tipe.equals("Pemadam Kebakaran")) {
            databaseReference = firebaseDatabase.getReference("Post").child("Damkar");
            Log.d(null, "11");
        } else if (tipe.equals("Rumah Sakit")) {
            databaseReference = firebaseDatabase.getReference("Post").child("Rumsak");
            Log.d(null, "11");
        } else {
            return;
        }
        databaseReference.push().setValue(post);
        navigationView.setCheckedItem(R.id.menuBeranda);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragLayout, new UserFragment()).commit();
        Toast.makeText(getContext(),"Laporan telah berhasil dikirimkan. Tunggu petugas beberapa saat.", Toast.LENGTH_LONG).show();

    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        Log.d(null, "5");
        Task<Location> task = fusedLocationProviderClient.getLastLocation();

        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                Log.d(null, "6");
                if (location != null) {

                    Log.d(null, "7");
                    curLoc = location;
                    SupportMapFragment supportMapFragment = (SupportMapFragment)
                            getFragmentManager().findFragmentById(R.id.helperMap);
                    supportMapFragment.getMapAsync(HelperFragment.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        gMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        gMap.setMyLocationEnabled(true);
        if (curLoc!=null) {
            LatLng first = new LatLng(curLoc.getLatitude(), curLoc.getLongitude());
            Log.d(null,first.toString());
            MarkerOptions markerOptions = new MarkerOptions().position(first).title("You are here");
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(first, 16.0f));
            gMap.addMarker(markerOptions);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    fetchLastLocation();
                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {

    }
}
