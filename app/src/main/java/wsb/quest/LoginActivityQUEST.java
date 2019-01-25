package wsb.quest;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivityQUEST extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public String login = "";
    public String password = "";

    private boolean isLoggedIn = false;

    EditText loginText;
    EditText passwordText;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_quest);

        loginText = (EditText) findViewById(R.id.emailText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        loginButton = (Button) findViewById(R.id.loginButton);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("### Login", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("### Login", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login = loginText.getText().toString();
                password = passwordText.getText().toString();
                Log.i("### Login","Login: " + login);
                Log.i("### Login", "Password: " + password);
                if(loginUser(login,password)){
                    Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                    myIntent.putExtra("login",login);
                    myIntent.putExtra("password",password);
                    startActivity(myIntent);
                }
            }
        });
    }

    private boolean loginUser(String login, String password) {
        Log.d("### Login","inside loginUser method");
        Log.d("### Login","inside loginUser method: login: " + login);
        Log.d("### Login","inside loginUser method: password: " + password);
        mAuth.signInWithEmailAndPassword(login, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("### Login", "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("### Login", "signInWithEmail", task.getException());
                            Toast.makeText(LoginActivityQUEST.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else{
                            mAuth.addAuthStateListener(mAuthListener);
                            isLoggedIn = true;
                        }

                        // ...
                    }
        });
        return isLoggedIn;
    }

}
