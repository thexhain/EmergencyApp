package untad.aldochristopherleo.emergence;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    EditText edtEmail, edtPass;
    Button btr, btl;
    DatabaseReference dbRf;
    FirebaseAuth mAuth;
    Intent intent;
    private boolean doubleBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        edtEmail = findViewById(R.id.edtLogMail);
        edtPass = findViewById(R.id.edtLogPass);
        btr = findViewById(R.id.btnRegister);
        btl = findViewById(R.id.btnLogin);

        btr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

        btl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        });
    }

    @Override
    public void onBackPressed() {
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

    private void logIn() {
        String email = edtEmail.getText().toString().trim();
        String pass = edtPass.getText().toString().trim();


        if (email.isEmpty()){
            edtEmail.setError("*Required");
            edtEmail.requestFocus();
            return;
        }
        if (pass.isEmpty()){
            edtPass.setError("*Required");
            edtPass.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edtEmail.setError("Invalid E-Mail");
            edtEmail.requestFocus();
            return;
        }
        if (pass.length() < 6){
            edtPass.setError("Min. 6 characters");
            edtPass.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    dbRf = FirebaseDatabase.getInstance().getReference().child("User")
                            .child(mAuth.getCurrentUser().getUid().trim()).child("tipe");
                    Log.d(null,"Halo");
                    dbRf.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //User user = dataSnapshot.getValue(User.class);
                            Log.d(null,"Halo");
                            String tipe = dataSnapshot.getValue(String.class);
                            Log.d(null,tipe);
                            if (tipe.equals("Pengguna")){
                                Log.d(null,"Tahu");
                                intent = new Intent(MainActivity.this, UserActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                            else if (tipe.equals("Polisi")){
                                Log.d(null,"Tahu");
                                intent = new Intent(MainActivity.this, HelperActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                Log.d(null,"Halo");
                            }
                            else if (tipe.equals("Pemadam Kebakaran")){
                                Log.d(null,"Tahu");
                                intent = new Intent(MainActivity.this, HelperActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                Log.d(null,"Halo");
                            }
                            else if (tipe.equals("Rumah Sakit")){
                                Log.d(null,"Tahu");
                                intent = new Intent(MainActivity.this, HelperActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                Log.d(null,"Halo");
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"ErrUnknown", Toast.LENGTH_SHORT);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
