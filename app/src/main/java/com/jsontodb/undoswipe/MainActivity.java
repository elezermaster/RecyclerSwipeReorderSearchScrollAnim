package com.jsontodb.undoswipe;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


import com.jsontodb.undoswipe.adapter.ItemAdapter;
import com.jsontodb.undoswipe.helper.Contact;
import com.jsontodb.undoswipe.helper.DatabaseHandler;
import com.jsontodb.undoswipe.helper.SimpleItemTouchHelperCallback;
import com.jsontodb.undoswipe.model.Item;
import com.jsontodb.undoswipe.service.LoadFromPhoneBookService;
import com.l4digital.fastscroll.FastScrollRecyclerView;

import org.polaric.colorful.ColorPickerDialog;
import org.polaric.colorful.Colorful;


public class MainActivity extends AppCompatActivity implements ItemAdapter.OnStartDragListener,  SearchView.OnQueryTextListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    // URL to get contacts JSON
    private static String url = "http://api.androidhive.info/contacts/";
    private ProgressDialog pDialog;
    private ItemTouchHelper mItemTouchHelper;
    private int nu=0;
    TextView tvNumber;
    ArrayList<HashMap<String, String>> contactList;
    DatabaseHandler db;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    List<Item> items;// = new ArrayList<>();
    //FloatingActionButton fab;
    ColorPickerDialog dialog;
    Toolbar toolbar;
    //RecyclerView recyclerView;
    FastScrollRecyclerView recyclerView;
    ItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setAllView();

        dialog = new ColorPickerDialog(this);
        dialog.setOnColorSelectedListener(new ColorPickerDialog.OnColorSelectedListener() {
            @Override
            public void onColorSelected(Colorful.ThemeColor color) {
                //TODO: Do something with the color
                Log.d("color", ""+ color);
                Colorful.config(getApplication().getApplicationContext())
                        .primaryColor(color) //Colorful.ThemeColor.RED
                        .accentColor(color)  //Colorful.ThemeColor.BLUE
                        .translucent(false)
                        .dark(true)
                        .apply();

                setColorTheme(color);
                setAllView();

            }
        });

        loadItems();
    }

    public void setAllView(){
        setContentView(R.layout.activity_main);


        TextView tvDate=(TextView)findViewById(R.id.tvDate);
        TextView tvDay=(TextView)findViewById(R.id.tvDay);
        tvNumber=(TextView)findViewById(R.id.tvNumber);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("MM.dd.yyyy", Locale.getDefault());
        assert tvDate!=null;
        assert  tvDay!=null;
        tvDate.setTypeface(Typefaces.getRobotoBlack(this));
        tvDay.setTypeface(Typefaces.getRobotoBlack(this));
        tvDate.setText( dateformat.format(c.getTime()).toUpperCase());
        recyclerView = (FastScrollRecyclerView) findViewById(R.id.cardList);
        //recyclerView = (RecyclerView) findViewById(R.id.cardList);
        assert recyclerView != null;
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        itemAdapter = new ItemAdapter(getApplicationContext(),this,tvNumber);
        recyclerView.setAdapter(itemAdapter);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(itemAdapter,this);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab!=null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.show();
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //setQuerySearch();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setTabs();
        readContactsFromDbSorted();
        for(Item i: items){
            ItemAdapter.itemList.add(i);
        }

    }

    private void setColorTheme(Colorful.ThemeColor color) {
        if(color == Colorful.ThemeColor.AMBER){
            setTheme(R.style.OverlayThemeDeepAmber);
        }
        if(color == Colorful.ThemeColor.BLUE){
            setTheme(R.style.OverlayThemeBlue);
        }
        if(color == Colorful.ThemeColor.CYAN){
            setTheme(R.style.OverlayThemeCyan);
        }
        if(color == Colorful.ThemeColor.DEEP_ORANGE){
            setTheme(R.style.OverlayThemeDeepOrange);
        }
        if(color == Colorful.ThemeColor.DEEP_PURPLE){
            setTheme(R.style.OverlayThemeDeepPurple);
        }
        if(color == Colorful.ThemeColor.GREEN){
            setTheme(R.style.OverlayThemeGreen);
        }
        if(color == Colorful.ThemeColor.INDIGO){
            setTheme(R.style.OverlayThemeIndigo);
        }
        if(color == Colorful.ThemeColor.LIGHT_BLUE){
            setTheme(R.style.OverlayThemeLightBlue);
        }
        if(color == Colorful.ThemeColor.LIGHT_GREEN){
            setTheme(R.style.OverlayThemeGreen);
        }
        if(color == Colorful.ThemeColor.LIME){
            setTheme(R.style.OverlayThemeLime);
        }
        if(color == Colorful.ThemeColor.ORANGE){
            setTheme(R.style.OverlayThemeOrange);
        }
        if(color == Colorful.ThemeColor.PINK){
            setTheme(R.style.OverlayThemePink);
        }
        if(color == Colorful.ThemeColor.PURPLE){
            setTheme(R.style.OverlayThemePurple);
        }
        if(color == Colorful.ThemeColor.RED){
            setTheme(R.style.OverlayThemeRed);
        }
        if(color == Colorful.ThemeColor.TEAL){
            setTheme(R.style.OverlayThemeTeal);
        }
        if(color == Colorful.ThemeColor.YELLOW){
            setTheme(R.style.OverlayThemeYellow);
        }

        //setTheme(R.style.OverlayThemeRed);
    }

    @Override
    public void onDestroy()
    {
      super.onDestroy();
        ItemAdapter.itemList.clear();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setOnQueryTextListener(this);
        searchView.setSearchableInfo( searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("...");//getResources().getString(R.string.hint));

        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener(){
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        Log.w(TAG, "onQueryTextSubmit ");
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        Log.w(TAG, "onQueryTextChange ");
                        return false;
                    }
                });

        //return true;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent intAbout = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intAbout);
            return true;
        }
        if(id == R.id.action_search){

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);

    }

    private void setTabs() {

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        assert tabLayout !=null;
        tabLayout.addTab(tabLayout.newTab().setText("GET JSON"));
        tabLayout.addTab(tabLayout.newTab().setText("LOAD DB"));
        tabLayout.addTab(tabLayout.newTab().setText("SORT BY ABC"));
       //TabLayout font & size
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(Typefaces.getRobotoBlack(this));
                    ((TextView) tabViewChild).setTextSize(3);
                }
            }
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //do stuff here
                int position = tab.getPosition();
                if(position == 2){
                    showContacts();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    private void loadItems()
    {
        //Initial items
       /* for(int i=10;i>0;i--)
        {
            Item item = new Item();
            item.setItemName("item"+i);
            item.setItemMobile("+9000123456");
            ItemAdapter.itemList.add(item);
        }*/

        readContactsFromDbSorted();
        //new GetContacts().execute();
        //readContacts();
        for(Item i: items){
            ItemAdapter.itemList.add(i);
        }
        tvNumber.setText(String.valueOf(ItemAdapter.itemList.size()));
        //load from phone book
        showContacts();
        for(Item i: items){
            ItemAdapter.itemList.add(i);
        }
        tvNumber.setText(String.valueOf(ItemAdapter.itemList.size()));
    }
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * CRUD Operations
     * */
    // Inserting Contacts
    public void writeContactToDb(Contact contact) {
        db = new DatabaseHandler(this);
        db.addContact(contact);
        Log.d("Insert: ", "Inserting .."+ contact.getName());
        //db.addContact(new Contact("Ravi", "9100000000"));
        //db.addContact(new Contact("Srinivas", "9199999999"));
        //db.addContact(new Contact("Tommy", "9522222222"));
        //db.addContact(new Contact("Karthik", "9533333333"));
    }

    //Reading from local database and sorting
    public void readContactsFromDbSorted(){
        // Reading all contacts
        items = new ArrayList<>();
        Log.d("Reading: ", "Reading all contacts..");
        db = new DatabaseHandler(getApplicationContext());
        List<Contact> contacts = db.getAllContacts();
        Log.d(TAG, "contacts cout: "+ contacts.size());
        //Sort contacts
        if (contacts.size() > 0) {
            Collections.sort(contacts, new Comparator<Contact>() {
                @Override
                public int compare(final Contact contact1, final Contact contact2) {
                    return contact1.getName().compareTo(contact2.getName());
                }

            } );
        }

        //List<Item> itemList = new ArrayList<>();
        contactList = new ArrayList<>();
        for (Contact cn : contacts) {
            String log = "Id: "+cn.getID()+" ,Name: " + cn.getName() + " ,Phone: " + cn.getPhoneNumber();
            // Writing Contacts to log
            Log.d("Name: ", log);
            HashMap<String, String> contact = new HashMap<>();
            contact.put("id", String.valueOf(cn.getID()));
            contact.put("name", cn.getName());
            contact.put("mobile", cn.getPhoneNumber());
            contactList.add(contact);
            Item item = new Item();
            item.setItemName(cn.getName());
            item.setItemMobile(cn.getPhoneNumber());
            items.add(item);
            //ItemAdapter.itemList.add(item);
        }
        tvNumber.setText(String.valueOf(ItemAdapter.itemList.size()));

    }

    /**
     * Async task class to get json by making HTTP call
     */
   /* private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Connecting...");

            pDialog.show();
            pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    Log.i("inside on cancel","Cancel Called");
                    finish(); //If you want to finish the activity.
                }
            });


            Toast.makeText(getApplicationContext(),
                    "connecting...",
                    Toast.LENGTH_LONG)
                    .show();


            *//*
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //playSplashScreen();
                    Toast.makeText(getApplicationContext(),
                            "run on ui thread",
                            Toast.LENGTH_LONG)
                            .show();
                }
            });*//*


        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall("api.androidhive.info/contacts/");

            Log.e(TAG, "Response from url: " + jsonStr);

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
                        //contactList.add(contact);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
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
            *//**
             * Updating parsed JSON data into ListView
             * *//*
            //contactList =
            readContactsFromDbSorted();

            //loadItems();
            //setupList(contactList);
        }

    }
*/

    public void readContacts(){
        items = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        /*List<Item> userList = new ArrayList<Item>();
        if (cur.getCount() > 0) {
            Item user = new Item();
            while (cur.moveToNext()) {
                user.setItemName(cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                user.setItemMobile(cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                userList.add(user);
                Log.d(TAG, "Tel-> Name : " + user.getItemName().toString());
                Log.d(TAG, "Tel-> Phone : " + user.getItemMobile().toString());
            }
        }*/
        //cur.close();

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    //System.out.println("name : " + name + ", ID : " + id);
                    Log.d(TAG, "name : " + name + ", ID : " + id);
                    Item item = new Item();
                    nu= ItemAdapter.itemList.size();
                    nu++;
                    item.setItemName(name);


                    // get the phone number
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phone = pCur.getString(
                                pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //System.out.println("phone" + phone);
                        Log.d(TAG, "phone" + phone);
                        item.setItemMobile(phone);

                    }
                    pCur.close();
                    items.add(item);


                    // get email and type

                    Cursor emailCur = cr.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    if (emailCur.getCount() > 0) {
                        while (emailCur.moveToNext()) {
                            // This would allow you get several email addresses
                            // if the email addresses were stored in an array
                            String email = emailCur.getString(
                                    emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                            String emailType = emailCur.getString(
                                    emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

                            //System.out.println("Email " + email + " Email Type : " + emailType);
                            Log.d(TAG, "Email " + email + " Email Type : " + emailType);
                        }
                    }
                    emailCur.close();

                    // Get note.......
                    String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] noteWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
                    Cursor noteCur = cr.query(ContactsContract.Data.CONTENT_URI, null, noteWhere, noteWhereParams, null);
                    if (noteCur.getCount() > 0) {
                        if (noteCur.moveToFirst()) {
                            String note = noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                            //System.out.println("Note " + note);
                            Log.d(TAG, "Note " + note);
                        }
                    }
                    noteCur.close();

                    //Get Postal Address....

                    String addrWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] addrWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
                    Cursor addrCur = cr.query(ContactsContract.Data.CONTENT_URI,
                            null, null, null, null);
                    if (addrCur.getCount() > 0) {
                        while (addrCur.moveToNext()) {
                            String poBox = addrCur.getString(
                                    addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
                            String street = addrCur.getString(
                                    addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                            String city = addrCur.getString(
                                    addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                            String state = addrCur.getString(
                                    addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                            String postalCode = addrCur.getString(
                                    addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
                            String country = addrCur.getString(
                                    addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                            String type = addrCur.getString(
                                    addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));

                            // Do something with these....

                        }
                    }
                    addrCur.close();

                    // Get Instant Messenger.........
                    String imWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] imWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE};
                    Cursor imCur = cr.query(ContactsContract.Data.CONTENT_URI,
                            null, imWhere, imWhereParams, null);
                    if (imCur.getCount() > 0) {
                        if (imCur.moveToFirst()) {
                            String imName = imCur.getString(
                                    imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
                            String imType;
                            imType = imCur.getString(
                                    imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.TYPE));
                        }
                    }
                    imCur.close();

                    // Get Organizations.........

                    String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] orgWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
                    Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI,
                            null, orgWhere, orgWhereParams, null);
                    if (orgCur.getCount() > 0) {
                        if (orgCur.moveToFirst()) {
                            String orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
                            String title = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
                        }
                    }
                    orgCur.close();
                }
            }
        }
        cur.close();

    }

    public void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            //List<String> contacts = getContactNames();
            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contacts);
            //lstNames.setAdapter(adapter);

            //////////////////////
            //just wrap into asynctask
            //readContacts();
            //new ExportTask().execute();
            Intent serviceLoadPhoneBook = new Intent(this, LoadFromPhoneBookService.class);
            startService(serviceLoadPhoneBook);
        }
    }


    public void letsGetContacts(View view) {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        List<Item> userList = new ArrayList<Item>();
        while (phones.moveToNext()) {
            Item user = new Item();
            user.setItemName(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
            user.setItemMobile(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            userList.add(user);
            Log.d(TAG, "Name : " + user.getItemName().toString());
            Log.d(TAG, "Phone : " + user.getItemMobile().toString());
        }
        phones.close();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }


    class ExportTask extends AsyncTask<Void, Object, Void> {
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Exporting from phone book ...");
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgress(0);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            items = new ArrayList<>();
            ContentResolver cr = getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);
            ///*
            //start exporting
            try {

        /*List<Item> userList = new ArrayList<Item>();
        if (cur.getCount() > 0) {
            Item user = new Item();
            while (cur.moveToNext()) {
                user.setItemName(cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                user.setItemMobile(cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                userList.add(user);
                Log.d(TAG, "Tel-> Name : " + user.getItemName().toString());
                Log.d(TAG, "Tel-> Phone : " + user.getItemMobile().toString());
            }
        }*/
                //cur.close();
                int i = 0;
                if (cur.getCount() > 0) {
                    while (cur.moveToNext()) {
                        //publishProgress(++i * 100 / cur.getCount());

                        String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                        if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                            //System.out.println("name : " + name + ", ID : " + id);
                            Log.d(TAG, "name : " + name + ", ID : " + id);
                            Item item = new Item();
                            nu = ItemAdapter.itemList.size();
                            nu++;
                            item.setItemName(name);


                            // get the phone number
                            Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    new String[]{id}, null);
                            while (pCur.moveToNext()) {
                                String phone = pCur.getString(
                                        pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                //System.out.println("phone" + phone);
                                Log.d(TAG, "phone" + phone);
                                item.setItemMobile(phone);

                            }
                            pCur.close();
                            items.add(item);
                            publishProgress(++i * 100 / cur.getCount(), item.getItemName() +"\n"+item.getItemMobile());

                            // get email and type

                            Cursor emailCur = cr.query(
                                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                                    new String[]{id}, null);
                            if (emailCur.getCount() > 0) {
                                while (emailCur.moveToNext()) {
                                    // This would allow you get several email addresses
                                    // if the email addresses were stored in an array
                                    String email = emailCur.getString(
                                            emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                                    String emailType = emailCur.getString(
                                            emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

                                    //System.out.println("Email " + email + " Email Type : " + emailType);
                                    Log.d(TAG, "Email " + email + " Email Type : " + emailType);
                                }
                            }
                            emailCur.close();

                            // Get note.......
                            String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                            String[] noteWhereParams = new String[]{id,
                                    ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
                            Cursor noteCur = cr.query(ContactsContract.Data.CONTENT_URI, null, noteWhere, noteWhereParams, null);
                            if (noteCur.getCount() > 0) {
                                if (noteCur.moveToFirst()) {
                                    String note = noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                                    //System.out.println("Note " + note);
                                    Log.d(TAG, "Note " + note);
                                }
                            }
                            noteCur.close();

                            //Get Postal Address....

                            String addrWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                            String[] addrWhereParams = new String[]{id,
                                    ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
                            Cursor addrCur = cr.query(ContactsContract.Data.CONTENT_URI,
                                    null, null, null, null);
                            if (addrCur.getCount() > 0) {
                                while (addrCur.moveToNext()) {
                                    String poBox = addrCur.getString(
                                            addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
                                    String street = addrCur.getString(
                                            addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                                    String city = addrCur.getString(
                                            addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                                    String state = addrCur.getString(
                                            addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                                    String postalCode = addrCur.getString(
                                            addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
                                    String country = addrCur.getString(
                                            addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                                    String type = addrCur.getString(
                                            addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));

                                    // Do something with these....

                                }
                            }
                            addrCur.close();

                            // Get Instant Messenger.........
                            String imWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                            String[] imWhereParams = new String[]{id,
                                    ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE};
                            Cursor imCur = cr.query(ContactsContract.Data.CONTENT_URI,
                                    null, imWhere, imWhereParams, null);
                            if (imCur.getCount() > 0) {
                                if (imCur.moveToFirst()) {
                                    String imName = imCur.getString(
                                            imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
                                    String imType;
                                    imType = imCur.getString(
                                            imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.TYPE));
                                }
                            }
                            imCur.close();

                            // Get Organizations.........

                            String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                            String[] orgWhereParams = new String[]{id,
                                    ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
                            Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI,
                                    null, orgWhere, orgWhereParams, null);
                            if (orgCur.getCount() > 0) {
                                if (orgCur.moveToFirst()) {
                                    String orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
                                    String title = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
                                }
                            }
                            orgCur.close();
                        }
                    }
                }
                cur.close();
            }catch (Exception e){
                Log.d(TAG, "Exception on exporting contacts: \n" + e);
            }


            return null;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values[0]);
            pDialog.setProgress((int)values[0]);
            pDialog.setMessage(String.valueOf(values[1]));
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pDialog.dismiss();

            if (result == null) {
                Toast.makeText(MainActivity.this, "Export task failed!",
                        Toast.LENGTH_LONG).show();
                return;
            }

            //Intent shareIntent = new Intent();
            //shareIntent.setAction(Intent.ACTION_SEND);
            //shareIntent.setType("text/*");
            //shareIntent.putExtra(Intent.EXTRA_STREAM, result);
            //startActivity(Intent.createChooser(shareIntent, "Send file to"));
        }

    }

    class ProgressUpdate {
        public final String detail;
        public final int value;

        public ProgressUpdate(int value, String detail) {
            this.detail = detail;
            this.value = value;
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive from BroadcastReceiver");
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String string = bundle.getString(LoadFromPhoneBookService.FILEPATH);
                int resultCode = bundle.getInt(LoadFromPhoneBookService.RESULT);
                if (resultCode == RESULT_OK) {
                    Toast.makeText(MainActivity.this,
                            "Download complete. Download URI: " + string,
                            Toast.LENGTH_LONG).show();
                    //textView.setText("Download done");
                    ///put contacts on listview
                    readContactsFromDbSorted();
                    for(Item i: items){
                        ItemAdapter.itemList.add(i);
                        //itemAdapter.addItem(0, item);
                    }
                    //tvNumber.setText(String.valueOf(ItemAdapter.itemList.size()));
                } else {
                    Toast.makeText(MainActivity.this, "Download failed",
                            Toast.LENGTH_LONG).show();
                    //textView.setText("Download failed");
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(
                LoadFromPhoneBookService.NOTIFICATION));
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }








}
