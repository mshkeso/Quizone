package com.example.keso.quizone;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mUsername;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private User user;
    private AlertDialog dialog;
    private AutoCompleteTextView rUsername;
    private AutoCompleteTextView rEmail;
    private EditText rPassword1;
    private EditText rPassword2;
    private TextView tvReg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mUsername = (AutoCompleteTextView) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        Button mRegisterButton = (Button) findViewById(R.id.register);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegister();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvReg = (TextView) findViewById(R.id.tvReg);
        skipLogin();
    }

    private void skipLogin(){
        mUsername.setText("Smirc");
        mPasswordView.setText("test");
        attemptLogin();
    }

    private void showRegister() {
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.register, null);
        rUsername = (AutoCompleteTextView) v.findViewById(R.id.rUsername);
        rEmail = (AutoCompleteTextView) v.findViewById(R.id.rEmail);
        rPassword1 = (EditText) v.findViewById(R.id.rPassword1);
        rPassword2 = (EditText) v.findViewById(R.id.rPassword2);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Registrera nytt konto");
        builder.setPositiveButton("Registrera", null);
        builder.setNegativeButton("Avbryt", null);
        builder.setView(v);

        dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface asd) {
                Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if(TextUtils.isEmpty(rUsername.getText().toString())){
                            rUsername.setError(getString(R.string.error_field_required));
                            rUsername.requestFocus();
                        }else if(!TextUtils.isEmpty(rUsername.getText().toString()) && !isUsernameValid(rUsername.getText().toString())){
                            rUsername.setError(getString(R.string.error_invalid_username));
                            rUsername.requestFocus();
                        }else if(TextUtils.isEmpty(rEmail.getText().toString())){
                            rEmail.setError(getString(R.string.error_field_required));
                            rEmail.requestFocus();
                        }else if(!TextUtils.isEmpty(rEmail.getText().toString()) && !isEmailValid(rEmail.getText().toString())){
                            rEmail.setError(getString(R.string.error_invalid_email));
                            rEmail.requestFocus();
                        }else if(TextUtils.isEmpty(rPassword1.getText().toString())){
                            rPassword1.setError(getString(R.string.error_field_required));
                            rPassword1.requestFocus();
                        }else if(TextUtils.isEmpty(rPassword2.getText().toString())){
                            rPassword2.setError(getString(R.string.error_field_required));
                            rPassword2.requestFocus();
                        }else if(!(rPassword1.getText().toString().equals(rPassword2.getText().toString()))){
                            rPassword2.setError("Lösenorden stämmer ej överens");
                            rPassword2.requestFocus();
                        }else if(!TextUtils.isEmpty(rPassword1.getText().toString()) && !isPasswordValid(rPassword1.getText().toString())){
                            rPassword1.setError(getString(R.string.error_invalid_password));
                            rPassword1.requestFocus();
                        }else{
                            showProgress(true);
                            UserRegisterTask task = new UserRegisterTask(rUsername.getText().toString(), rPassword1.getText().toString(), rEmail.getText().toString() );
                            task.execute();
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsername.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsername.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid username address.
        if (TextUtils.isEmpty(username)) {
            mUsername.setError(getString(R.string.error_field_required));
            focusView = mUsername;
            cancel = true;
        }else if(!TextUtils.isEmpty(username) && !isUsernameValid(username)){
            mUsername.setError(getString(R.string.error_invalid_username));
            focusView = mUsername;
            cancel = true;
        }else if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }else if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(username, password);
            mAuthTask.execute();
        }
    }

    private boolean isPasswordValid(String password) {
        Pattern ps = Pattern.compile("^[a-zA-Z]+$");
        Matcher ms = ps.matcher(password);
        boolean response = ms.matches();
        if(password.length() < 4 || password.length() > 15){
            response = false;
        }
        return response;
    }

    private boolean isEmailValid(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isUsernameValid(String username) {
        Pattern ps = Pattern.compile("^[a-zA-Z]+$");
        Matcher ms = ps.matcher(username);
        boolean response = ms.matches();
        if(username.length() < 4 || username.length() > 15){
            response = false;
        }

        return response;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mUsername.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    protected void loginIntent(JSONObject userJSON){
        try {
            String date = String.valueOf(userJSON.getString("logindate"));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dDate = dateFormat.parse(date);
            user = new User(userJSON.getInt("id"),String.valueOf(userJSON.getString("name")), String.valueOf(userJSON.getString("email")), userJSON.getInt("quizcoin"), dDate);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("User", user);
        startActivity(intent);
    }



    public class UserRegisterTask extends AsyncTask<String, Void, String> {

        private final String mUsername;
        private final String mPassword;
        private final String mEmail;

        UserRegisterTask(String username, String password, String email) {
            mUsername = username;
            mPassword = password;
            mEmail = email;
        }

        @Override
        protected String doInBackground(String... params) {
            String inputString = null;
            try {
                URL url = new URL(String.format("http://185.53.129.12/register.php?username="+mUsername+"&password="+mPassword+"&email="+mEmail));
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));

                inputString = bufferedReader.readLine();


                urlConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return inputString;
        }

        @Override
        protected void onPostExecute(String response) {

            showProgress(false);
            if(response.equals("Username exists")){
                rUsername.setError("Användarnamn upptaget");
                rUsername.requestFocus();
            }else if(response.equals("Email exists")){
                rEmail.setError("Email upptaget");
                rEmail.requestFocus();
            }else if(response.equals("1")){
                dialog.cancel();
                tvReg.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Registrerad!", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<String, Void, JSONObject> {

        private final String mUsername;
        private final String mPassword;

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject user = null;
            try {
                    URL url = new URL(String.format("http://185.53.129.12/login.php?username="+mUsername+"&password="+mPassword));
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();

                String inputString;
                while ((inputString = bufferedReader.readLine()) != null) {
                    builder.append(inputString);
                }

                JSONArray topLevel = new JSONArray(builder.toString());
                user = topLevel.getJSONObject(0);

                urlConnection.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return user;
        }

        @Override
        protected void onPostExecute(JSONObject userJSON) {
            mAuthTask = null;
            showProgress(false);

            if (userJSON!=null) {
                loginIntent(userJSON);
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}