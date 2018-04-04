package com.moufee.purduemenus.ui.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import com.moufee.purduemenus.R;
import com.moufee.purduemenus.api.Webservice;
import com.moufee.purduemenus.db.FavoriteDao;
import com.moufee.purduemenus.menus.Favorites;
import com.moufee.purduemenus.util.AuthHelper;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
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
    @Inject
    SharedPreferences mSharedPreferences;
    @Inject
    FavoriteDao mFavoriteDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = findViewById(R.id.username);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == R.integer.login_id || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });

        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(view -> attemptLogin());

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

            try {

                Request firstRequest = AuthHelper.getTGTRequest(mUsername, mPassword);

                Response response = mHTTPClient.newCall(firstRequest).execute();
                Log.d(TAG, "doInBackground: code" + response.code());
                Log.d(TAG, "doInBackground: was successful: " + response.isSuccessful());
                if (!response.isSuccessful()) {
                    mSharedPreferences.edit()
                            .putBoolean("logged_in", false)
                            .apply();
                    return false;
                }
                // otherwise the credentials are valid
                //todo: store this more securely and use constants for keys
                mSharedPreferences.edit()
                        .putBoolean("logged_in", true)
                        .putString("username", mUsername)
                        .putString("password", mPassword)
                        .apply();
                String location = response.headers().get("Location");
                Log.d(TAG, "doInBackground: location: " + location);

                if (location == null)
                    return false;


                Request ticketRequest = AuthHelper.getTicketRequest(location);

                Response ticketResponse = mHTTPClient.newCall(ticketRequest).execute();
                if (!ticketResponse.isSuccessful()) {
                    Log.d(TAG, "doInBackground: Ticket response not successful");
                    return false;
                }
                String ticket = ticketResponse.body().string().trim();
                Log.d(TAG, "doInBackground: ticket: " + ticket);


                Call favoritesCall = mWebservice.getFavorites(ticket);
                Log.d(TAG, "doInBackground: favoritesCall: " + favoritesCall.request().toString());
                retrofit2.Response<Favorites> favoritesResponse = mWebservice.getFavorites(ticket).execute();
                if (favoritesResponse.isSuccessful()) {
                    Favorites favorites = favoritesResponse.body();
                    if (favorites == null)
                        Log.d(TAG, "doInBackground: favorites were null!");
                    else {
                        mFavoriteDao.insertFavorites(favorites.getFavorites());
                    }

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

