package com.jsontodb.undoswipe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.jsontodb.undoswipe.helper.Contact;
import com.jsontodb.undoswipe.helper.DatabaseHandler;
import com.jsontodb.undoswipe.helper.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import gr.net.maroulis.library.EasySplashScreen;

/**
 * Created by elezermaster on 24/06/17.
 */
public class SplashActivity extends Activity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    private ProgressDialog pDialog;

    DatabaseHandler db;
    ArrayList<Contact> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = getApplicationContext();

        EasySplashScreen config = new EasySplashScreen(SplashActivity.this)
                .withFullScreen()
                .withTargetActivity(MainActivity.class)
                .withSplashTimeOut(2000)
                .withBackgroundResource(android.R.color.white)
                .withHeaderText("Code Solutions")
                .withFooterText("Copyright 2017")
                .withBeforeLogoText("Our Mission Is Your Success")
                .withLogo(R.drawable.emaster_stu_885x885)
                .withAfterLogoText("connecting...");
         //In a world where innovation is the name of the game,
        // we’re always at the forefront of technology, learning,
        // enriching, and tweaking the technologies, the products, and our practices.

        //set your own animations
        myCustomTextViewAnimation(config.getFooterTextView());

        //customize all TextViews
        //Typeface pacificoFont = Typeface.createFromAsset(getAssets(), "Roboto-Black.ttf");
        config.getAfterLogoTextView().setTypeface(Typefaces.getRobotoMedium(context));

        config.getHeaderTextView().setTextColor(Color.BLACK);
        config.getFooterTextView().setTextColor(Color.BLACK);

        //create the view
        View easySplashScreenView = config.create();

        setContentView(easySplashScreenView);

        new GetContacts().execute();
    }

    private void myCustomTextViewAnimation(TextView tv){
        Animation animation=new TranslateAnimation(0,0,480,0);
        animation.setDuration(2000);
        tv.startAnimation(animation);
    }


    /**
     * Async task class to get json by making HTTP call
     */
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Showing progress dialog
         /*   pDialog = new ProgressDialog(SplashActivity.this);
            pDialog.setMessage("Connecting...");

            pDialog.show();
            pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    Log.i("inside on cancel","Cancel Called");
                    finish(); //If you want to finish the activity.
                }
            });*/

            /*Toast.makeText(getApplicationContext(),
                    "connecting...",
                    Toast.LENGTH_LONG)
                    .show();*/

            /*
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //playSplashScreen();
                    Toast.makeText(getApplicationContext(),
                            "run on ui thread",
                            Toast.LENGTH_LONG)
                            .show();
                }
            });*/

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall("http://api.androidhive.info/contacts/");

            Log.e(TAG, "Response from url: " + jsonStr);

            contactList = new ArrayList<>();
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("contacts");

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        String id = c.getString("id");
                        String name = c.getString("name");
                        String email = c.getString("email");
                        String address = c.getString("address");
                        String gender = c.getString("gender");

                        // Phone node is JSON Object
                        JSONObject phone = c.getJSONObject("phone");
                        String mobile = phone.getString("mobile");
                        String home = phone.getString("home");
                        String office = phone.getString("office");

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();
                        Contact mycontact = new Contact();

                        // adding each child node to HashMap key => value
                        contact.put("id", id);
                        contact.put("name", name);
                        //contact.put("email", email);
                        contact.put("mobile", mobile);
                        mycontact.setID(id);
                        mycontact.setName(name);
                        mycontact.setPhoneNumber(mobile);
                        // adding contact to contact list
                        writeContactToDb(mycontact);
                        contactList.add(mycontact);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                          /*  Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();*/
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server. Check LogCat for possible errors!");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       /* Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();*/
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            //if (pDialog.isShowing())
            //    pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            int i =0;
            for(Contact c : contactList){
               // writeContactToDb(c);
                Log.d(TAG, i++ + " "+ c.getName());
            }
        }

    }

    /**
     * CRUD Operations
     * */
    // Inserting Contacts
    public void writeContactToDb(Contact contact) {

        db = new DatabaseHandler(getApplicationContext());
        db.addContact(contact);
        Log.d("Insert: ", "Inserting .."+ contact.getName());
        //db.addContact(new Contact("Ravi", "9100000000"));
        //db.addContact(new Contact("Srinivas", "9199999999"));
        //db.addContact(new Contact("Tommy", "9522222222"));
        //db.addContact(new Contact("Karthik", "9533333333"));
    }


}
