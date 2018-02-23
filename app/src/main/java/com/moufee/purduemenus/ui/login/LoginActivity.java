package com.moufee.purduemenus.ui.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.moufee.purduemenus.R;
import com.moufee.purduemenus.api.Webservice;
import com.moufee.purduemenus.menus.Favorites;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    @Inject
    Webservice mWebservice;
    @Inject
    OkHttpClient mHTTPClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = findViewById(R.id.username);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.integer.login_id || id == EditorInfo.IME_NULL) {
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

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
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
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check that password is entered
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
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
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
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

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mUsername = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            /*try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }*/

            /*for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mUsername)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }*/

            try {

                String favoritesURL = "https://api.hfs.purdue.edu/menus/v2/favorites";
                String ticketURL = "https://www.purdue.edu/apps/account/cas/v1/tickets";


                FormBody formBody = new FormBody.Builder()
                        .add("username", mUsername)
                        .add("password", mPassword)
                        .build();
                Request firstRequest = new Request.Builder()
                        .url(ticketURL)
                        .post(formBody)
                        .build();

                Response response = mHTTPClient.newCall(firstRequest).execute();
                Log.d(TAG, "doInBackground: code" + response.code());
                Log.d(TAG, "doInBackground: was successful: " + response.isSuccessful());
                if (!response.isSuccessful())
                    return false;
                Headers responseHeaders = response.headers();
                String location = response.headers().get("Location");
                Log.d(TAG, "doInBackground: location: " + location);
                if (location == null)
                    return false;

                for (int i = 0; i < responseHeaders.size(); i++) {
                    Log.d(TAG, responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }


                FormBody ticketRequestBody = new FormBody.Builder()
                        .add("service", favoritesURL)
                        .build();

                Request ticketRequest = new Request.Builder()
                        .url(location)
                        .post(ticketRequestBody)
                        .build();

                Response ticketResponse = mHTTPClient.newCall(ticketRequest).execute();
                if (!ticketResponse.isSuccessful()) {
                    Log.d(TAG, "doInBackground: Ticket response not successful");
                    return false;
                }
                String ticket = ticketResponse.body().string().trim();
                Log.d(TAG, "doInBackground: ticket: " + ticket);


                String requestURL = favoritesURL + "?ticket=" + ticket;
                Log.d(TAG, "doInBackground: request URL: " + requestURL);
                HttpUrl favoriteHttpUrl = HttpUrl.parse(requestURL);
                Log.d(TAG, "doInBackground: " + favoriteHttpUrl);
                Log.d(TAG, "doInBackground: params: " + favoriteHttpUrl.queryParameter("ticket"));
                Request favoritesRequest = new Request.Builder()
                        .get()
                        .url(favoriteHttpUrl)
                        .addHeader("Accept", "text/json")
                        .build();

                Call favoritesCall = mWebservice.getFavorites(ticket);
                Log.d(TAG, "doInBackground: favoritesCall: " + favoritesCall.request().toString());
                retrofit2.Response<Favorites> favoritesResponse = mWebservice.getFavorites(ticket).execute();
                if (favoritesResponse.isSuccessful()) {
                    Favorites favorites = favoritesResponse.body();
                    if (favorites == null)
                        Log.d(TAG, "doInBackground: favorites were null!");

                    Log.d(TAG, "doInBackground: favorites: " + favorites.getFavorites());
                } else {
                    Log.d(TAG, "doInBackground: favorites call not successful!");
                    Log.d(TAG, "doInBackground: " + favoritesResponse.code());
                    Log.d(TAG, "doInBackground: " + favoritesResponse.errorBody().string());
                    Log.d(TAG, "doInBackground: " + favoritesResponse.raw().toString());
                }


            } catch (Exception e) {
                Log.e(TAG, "doInBackground: http error", e);
                return false;
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
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

