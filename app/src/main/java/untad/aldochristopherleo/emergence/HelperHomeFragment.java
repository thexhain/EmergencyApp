package untad.aldochristopherleo.emergence;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.EventListener;
import java.util.Locale;

public class HelperHomeFragment extends Fragment implements OnMapReadyCallback {

    Marker mMarker;
    RecyclerView recyclerView;
    FirebaseDatabase fbDb;
    DatabaseReference dbRf;
    FirebaseAuth mAuth;
    GoogleMap gMap;
    Location curLoc;
    Post selPos;
    String key;
    FusedLocationProviderClient fusedLocationProviderClient;
    FirebaseRecyclerAdapter<Post, RecyclerViewHolder> adapter;
    private static final int REQUEST_CODE = 101;
    private boolean doubleclick;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_helperhome, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Log.d(null, "Haloha1");

        mAuth = FirebaseAuth.getInstance();
        final String uid = mAuth.getCurrentUser().getUid();
        fbDb = FirebaseDatabase.getInstance();
        Log.d(null, "Haloha2");
        dbRf = fbDb.getReference().child("User")
                .child(mAuth.getCurrentUser().getUid().trim()).child("tipe");
        Log.d(null, "Haloha3");
        dbRf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String tipe = dataSnapshot.getValue(String.class);
                Log.d(null, tipe);
                if (tipe.equals("Polisi")) {
                    dbRf = fbDb.getReference("Post").child("Polisi");
                } else if (tipe.equals("Pemadam Kebakaran")) {
                    dbRf = fbDb.getReference("Post").child("Damkar");
                } else if (tipe.equals("Rumah Sakit")) {
                    dbRf = fbDb.getReference("Post").child("Rumsak");
                } else {
                    Toast.makeText(getContext(), "LOL", Toast.LENGTH_SHORT);
                    return;
                }
                Log.d(null, "Haloha4");
                displayPost();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.helpersMaps);
//if (mapFragment == null) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            ft.replace(R.id.helpersMaps, mapFragment).commit();
//}
        Log.d(null, "4");
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fetchLastLocation();
        return view;
    }


    private void displayPost() {
        FirebaseRecyclerOptions<Post> options = new FirebaseRecyclerOptions.Builder<Post>().setQuery(dbRf, Post.class).build();
        adapter = new FirebaseRecyclerAdapter<Post, RecyclerViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RecyclerViewHolder holder, final int position, @NonNull final Post model) {
                Log.d(null, "Haloha6");
                holder.email.setText(model.getEmail());
                holder.desc.setText(model.getDesc());
                holder.lokasi.setText(model.getPosLat() + "," + model.getPosLong());
                holder.telp.setText(model.getTelp());

                holder.setIcl(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int post) {
                        if (doubleclick){
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:"+ model.getTelp()));
                            startActivity(intent);
                        } else {
                            doubleclick = true;
                            selPos = model;
                            key = getSnapshots().getSnapshot(position).toString();

                            if (mMarker != null){
                                mMarker.remove();
                            }
                            LatLng satu = new LatLng(Double.parseDouble(selPos.getPosLat()), Double.parseDouble(selPos.getPosLong()));
                            Log.d(null,satu.toString());
                            MarkerOptions markerOptions = new MarkerOptions().position(satu).title(selPos.getDesc());
                            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(satu, 15));
                            mMarker = gMap.addMarker(markerOptions);
                            Toast.makeText(getActivity(), "Tekan sekali lagi untuk menghubungi", Toast.LENGTH_SHORT).show();
                            new android.os.Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    doubleclick = false;
                                }
                            }, 1000);
                        }
                    }
                });
            }

            @NonNull
            @Override
            public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                Log.d(null, "Kalo");
                View itemView = LayoutInflater.from(getContext()).inflate(R.layout.result_helper, viewGroup, false);
                return new RecyclerViewHolder(itemView);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStop() {
        if (adapter != null) {
            adapter.stopListening();
        }
        super.onStop();
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
            if (mMarker != null){
                mMarker.remove();
            }
            LatLng first = new LatLng(curLoc.getLatitude(), curLoc.getLongitude());
            Log.d(null,first.toString());
            MarkerOptions markerOptions = new MarkerOptions().position(first).title("Anda disini");
            gMap.moveCamera(CameraUpdateFactory.newLatLng(first));
            mMarker = (gMap.addMarker(markerOptions));
            gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Membuka Google Maps")
                            .setMessage("Apakah anda yakin?")
                            .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    clickMarker();
                                }
                            });
                    AlertDialog alertDialog =builder.create();
                    alertDialog.show();
                    return true;
                }
            });
        }
    }

    void clickMarker(){
        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", curLoc.getLatitude(), curLoc.getLongitude());
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        getContext().startActivity(intent);
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.d(null, "6");
                if (location != null) {

                    Log.d(null, "7");
                    curLoc = location;
                    SupportMapFragment supportMapFragment = (SupportMapFragment)
                            getFragmentManager().findFragmentById(R.id.helpersMaps);
                    supportMapFragment.getMapAsync(HelperHomeFragment.this);
                }
            }
        });
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
}
