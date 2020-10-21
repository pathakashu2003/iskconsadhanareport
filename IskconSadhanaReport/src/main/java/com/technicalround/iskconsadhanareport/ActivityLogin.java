package com.technicalround.iskconsadhanareport;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class ActivityLogin extends AppCompatActivity implements InterfaceTaskCompleted{

    public static AppPreferences userPreferences;
    private EditText usernameField, passwordField;
    private CheckBox checkBoxIsAskPass;
    private ProgressBar httpProgressBar;
    private ImageView imageViewLogin;
    private TextView buttonGuestUser;
    private Toast toast;
    private InterfaceTaskCompleted listener;
    private Context context;

    public static boolean userLoginOnResponse(Context context, String response) {
        //Check whether successful sign-in
        //Log.d("Ashu:", "In userLoginOnResponse" + response);
        if (response.startsWith("Success:")) {
            //Toast.makeText(context, "Login Success", Toast.LENGTH_SHORT).show();
            String jsonResult = response.substring("Success:".length());
            //Parse JSON
            try {
                JSONArray jsonDataArray = new JSONArray(jsonResult);
                //Log.d("ISS", "attendance json length"+jsonDataArray.length());
                JSONObject jsonDataSingle;
                if (jsonDataArray.length() > 0) {
                    for (int i = 0; i < jsonDataArray.length(); i++) {
                        jsonDataSingle = jsonDataArray.getJSONObject(i);
                        userPreferences.saveJsonToUserPref(jsonDataSingle, (short) 0);
                        //Log.d("ISS", "attendance json item retrieved");
                        return true;
                    }
                } else {
                    Toast.makeText(context, "Error:" + jsonResult, Toast.LENGTH_SHORT).show();
                    if (BuildConfig.DEBUG) Log.d("ISS", "no Json object found");
                }
            } catch (Exception e) {
                Toast.makeText(context, "Error:" + jsonResult, Toast.LENGTH_SHORT).show();
                Log.d("ISS", "Exception: " + e.toString());
                e.printStackTrace();
            }
        } else if (response.startsWith("Error0:"))
            Toast.makeText(context, "No Internet Connection !",
                    Toast.LENGTH_SHORT).show();
        else if (response.startsWith("Exception:"))
            Toast.makeText(context, "Could not connect to Server, Try after some time",
                    Toast.LENGTH_SHORT).show();
        else if (response.startsWith("Error:"))
            Toast.makeText(context, "Could not connect to Server, Try after some time",
                    Toast.LENGTH_SHORT).show();
        else Toast.makeText(context, response, Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = getApplicationContext();
        userPreferences = new AppPreferences(this);
        usernameField = (EditText) findViewById(R.id.editTextUserId);
        passwordField = (EditText) findViewById(R.id.editTextPassword);
        httpProgressBar = (ProgressBar) findViewById(R.id.progressBarLogin1);
        httpProgressBar.setVisibility(View.INVISIBLE);
        buttonGuestUser = (TextView) findViewById(R.id.buttonGuest);
        checkBoxIsAskPass = (CheckBox) findViewById(R.id.checkBoxIsAskPassword);
        imageViewLogin = (ImageView) findViewById(R.id.imageViewLogin);

        //Check in system preferences if already logged on
        if (userPreferences.isUserloggedOn()) {
            //put already stored email
            usernameField.setText(userPreferences.user.email);

            if (userPreferences.isRememberPassword()) {
                passwordField.setText(userPreferences.user.pass);
                checkBoxIsAskPass.setChecked(true);
            }

            passwordField.requestFocus();
            //Remove guest user login
            //buttonGuestUser.setVisibility(View.INVISIBLE);
        }

        //Set reminder alarm
        SetAlarms.setReminderBeforeSleep(this);
        if (BuildConfig.DEBUG) {
            //TODO:Remove it
            //SetAlarms.setReminderFrequently(this, 1);//Every Minute alarm for testing
        }
        // Create global configuration and initialize ImageLoader with this config
        ClassImageLoader.initImageLoader(context);
        ClassImageLoader.displayImage(context.getString(R.string.WEB_ROOT) +
                "images/loginPage.jpg", imageViewLogin);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
    }

    /**
     * @param view
     */
    public void performSecurityCheck(View view) {
        String enteredUsername = usernameField.getText().toString();
        String enteredPassword = passwordField.getText().toString();
        boolean performOnlineLogin = true;
        enteredUsername = enteredUsername.replace(" ", "");//Remove Space

        //Check in system preferences if already logged on
        if (userPreferences.isUserloggedOn()) {
            //Compare the password entered with already saved if email same
            if (userPreferences.user.email.equalsIgnoreCase(enteredUsername)) {
                if (userPreferences.user.pass.equalsIgnoreCase(enteredPassword)) {
                    //Save whether to ask password in future
                    userPreferences.saveRememberPassword(checkBoxIsAskPass.isChecked());
                    Intent intent = new Intent(this, ActivityMainDevotee.class);
                    startActivity(intent);
                    performOnlineLogin = false;
                    finish();
                } else {
                    performOnlineLogin = true;
                }
            } else {
                performOnlineLogin = true;
            }
        } else {
            performOnlineLogin = true;
        }

        //Log.d("ISS", "" + performOnlineLogin);
        if (performOnlineLogin) {
            /*
            String link = getApplicationContext().getString(R.string.WEB_ROOT) + "devotee.php?act=login" +
                    "&email=" + enteredUsername + "&pass=" + enteredPassword;
            //Log.d("ISS", link);
            new ExecuteHttpGet(this, this, httpProgressBar).execute(link);
            */
            /*
            try {
                String link = getApplicationContext().getString(R.string.WEB_ROOT) + "post.php";
                String data = URLEncoder.encode("email", "UTF-8")
                        + "=" + URLEncoder.encode(enteredUsername, "UTF-8");

                data += "&" + URLEncoder.encode("pass", "UTF-8")
                        + "=" + URLEncoder.encode(enteredPassword, "UTF-8");

                Log.d("ISS", "encoded data:"+data);
                new ExecuteHttpPost(this, this, httpProgressBar).execute(link, data);
            }
            catch(Exception e) {
                Log.d("ISS", "POST URL Encoding exception:"+e.getMessage());
            }
            */
            String link = getApplicationContext().getString(R.string.WEB_ROOT) + "devotee.php?act=login";
            String data = "email=" + enteredUsername + "&pass=" + enteredPassword;
            new ExecuteHttpPost(this, this, httpProgressBar).execute(link, data);
        }
    }

    /**
     * guestUserLogin
     *
     * @param view
     */
    public void guestUserLogin(View view) {
        Log.d("ISS", "Guest Login started");
        userPreferences.guestUserLogin();
        //Start main activity
        Intent intent = new Intent(this, ActivityMainDevotee.class);
        startActivity(intent);
        finish();
    }

    /**
     * addUser
     *
     * @param view
     */
    public void addUser(View view) {
        //Start Registration activity
        Intent intent = new Intent(this, ActivityAddDevotee.class);
        intent.putExtra("isProfilePage", false);
        startActivity(intent);
    }

    /**
     * passHint
     *
     * @param view
     */
    public void passHint(View view) {
        String enteredUsername = usernameField.getText().toString();
        if (enteredUsername.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Fill E-mail field", Toast.LENGTH_SHORT).show();
            return;
        }
        /*
        String link = getApplicationContext().getString(R.string.WEB_ROOT) + "devotee.php?act=hint" +
                "&email=" + enteredUsername;
        Log.d("ISS", link);
        new ExecuteHttpGet(this, this, httpProgressBar).execute(link);
        */
        String link = getApplicationContext().getString(R.string.WEB_ROOT) + "devotee.php?act=hint";
        String data = "email=" + enteredUsername;
        new ExecuteHttpPost(this, this, httpProgressBar).execute(link, data);
    }

    /**
     * passReset
     *
     * @param view
     */
    public void passResetDialog(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Reset Password")
                .setMessage(
                        "Do you want to reset your password?")
                //.setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_alert))
                .setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                //Do Something Here
                                passReset();
                            }
                        })
                .setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                //Do Something Here

                            }
                        }).show();
    }

    /**
     * passReset
     *
     * @param
     */
    public void passReset() {
        String enteredUsername = usernameField.getText().toString();

        if (enteredUsername.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Fill E-mail field", Toast.LENGTH_SHORT).show();
            return;
        }
        /*
        String link = getApplicationContext().getString(R.string.WEB_ROOT) + "devotee.php?act=reset" +
                "&email=" + enteredUsername;
        Log.d("ISS", link);
        new ExecuteHttpGet(this, this, httpProgressBar).execute(link);
        */
        String link = getApplicationContext().getString(R.string.WEB_ROOT) + "devotee.php?act=reset";
        String data = "email=" + enteredUsername;
        new ExecuteHttpPost(this, this, httpProgressBar).execute(link, data);

    }

    //Callback function handler
    @Override
    public void onHttpResponse(String response) {
        try {
            //Check whether successful sign-in
            //Log.d("Ashu:", "In callback listener in Login" + response);
            if (userLoginOnResponse(this, response)) {
                //Start main activity
                Intent intent = new Intent(this, ActivityMainDevotee.class);
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.d("ISS", "LoginHttpResponse: " + e.toString());
            e.printStackTrace();
        }
    }

    //Callback function handler
    @Override
    public void onFragmentInteraction(String response) {
    }

    @Override
    public void onTimePickSelection(View view, int combinedTime, boolean delayIgnoreDialog) {
    }
}
