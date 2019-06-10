package com.nakhmadov.diplomapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    EditText usernameInputText;
    EditText passwordInputText;
    EditText emailInputText;
    Button registerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        //Objects.requireNonNull(getSupportActionBar()).setTitle("Регистрация");

        usernameInputText = (EditText) findViewById(R.id.usernameInputText);
        passwordInputText = (EditText) findViewById(R.id.passwordInputText);
        emailInputText = (EditText) findViewById(R.id.emailInputText);
        registerButton = (Button) findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailString = emailInputText.getText().toString();
                String passwordString = passwordInputText.getText().toString();
                String usernameString = usernameInputText.getText().toString();

                if (TextUtils.isEmpty(emailString) || TextUtils.isEmpty(passwordString) || TextUtils.isEmpty(usernameString)) {
                    Toast.makeText(RegisterActivity.this, "Пожалуйста заполните все поля", Toast.LENGTH_LONG).show();
                } else {
                    if (passwordString.length() < 6)
                        Toast.makeText(RegisterActivity.this, "Длина пароля должно быть минимум 6 символов", Toast.LENGTH_LONG).show();
                    else if (!isEmailValid(emailString))
                        Toast.makeText(RegisterActivity.this, "Неверный электронный адрес", Toast.LENGTH_LONG).show();
                    else
                        register(usernameString, passwordString, emailString);
                }

            }
        });

    }

    public void register(final String username, String password, String email) {
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    assert firebaseUser != null;
                    String userId = firebaseUser.getUid();

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id", userId);
                    hashMap.put("username", username);
                    hashMap.put("imageURL", "default");
                    hashMap.put("status", "offline");

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();

                            }
                        }
                    });
                } else {
                    Toast.makeText(RegisterActivity.this, "You can't register with this email or password", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
