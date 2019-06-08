package untad.aldochristopherleo.emergence;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.regex.Pattern;

import static com.basgeekball.awesomevalidation.ValidationStyle.COLORATION;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    EditText edtEmail, edtPass, edtNama, edtNIK, edtTelp;
    FirebaseAuth mAuth;
    Spinner spinner;
    String token;
    AwesomeValidation mAwesomeValidation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        spinner = findViewById(R.id.spTipe);
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(this, R.array.spTipe, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();

        edtEmail = findViewById(R.id.edtRegMail);
        edtPass = findViewById(R.id.edtRegPass);
        edtNama = findViewById(R.id.edtRegNama);
        edtTelp = findViewById(R.id.edtRegTel);
        edtNIK = findViewById(R.id.edtRegNIK);
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isSuccessful()){
                    token = task.getResult().getToken();
                }else{

                }
            }
        });

        mAwesomeValidation = new AwesomeValidation(COLORATION);
        mAwesomeValidation.addValidation(this, R.id.edtRegNama, "[a-zA-Z\\s]+", R.string.err_name);
        mAwesomeValidation.addValidation(this, R.id.edtRegTel, RegexTemplate.TELEPHONE, R.string.err_tel);
        findViewById(R.id.btnRegistration).setOnClickListener(this);
        findViewById(R.id.btnBtl2).setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() != null){
            //has login
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String txt = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnRegistration:
                registerUser();
                break;
            case R.id.btnBtl2:
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void registerUser() {

        mAwesomeValidation.validate();
        final String email = edtEmail.getText().toString().trim();
        String pass = edtPass.getText().toString().trim();
        final String nama = edtNama.getText().toString().trim();
        final String nik = edtNIK.getText().toString().trim();
        final String telp = edtTelp.getText().toString().trim();
        final String tipe = spinner.getSelectedItem().toString().trim();

        if(nama.isEmpty()){
            edtNama.setError("*Required");
            edtNama.requestFocus();
            return;
        }
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
        if (nik.isEmpty()){
            edtNIK.setError("*Required");
            edtNIK.requestFocus();
            return;
        }
        if (nik.length() != 16){
            edtNIK.setError("The length must be 16");
            edtNIK.requestFocus();
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
        if (telp.length() < 11 || telp.length() > 13){
            edtTelp.setError("Invalid phone number length");
            edtTelp.requestFocus();
            return;
        }


        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            User user = new User(
                                    email,
                                    nama,
                                    nik,
                                    tipe,
                                    telp,
                                    token
                            );

                            FirebaseDatabase.getInstance().getReference("User")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(RegisterActivity.this, "Registration Succeed", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                startActivity(intent);
                                            }else{
                                                if (task.getException() instanceof FirebaseAuthUserCollisionException){
                                                    Toast.makeText(RegisterActivity.this, "Already exist", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    });
                        }else{
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
