package com.jsontodb.undoswipe.service;

import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;

import com.jsontodb.undoswipe.adapter.ItemAdapter;
import com.jsontodb.undoswipe.helper.Contact;
import com.jsontodb.undoswipe.helper.DatabaseHandler;
import com.jsontodb.undoswipe.model.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by elezermaster on 18/07/17.
 */
public class LoadFromPhoneBookService extends IntentService {

    private static final String TAG = LoadFromPhoneBookService.class.getSimpleName();
    private int result = Activity.RESULT_CANCELED;
    public static final String URL = "urlpath";
    public static final String FILENAME = "filename";
    public static final String FILEPATH = "filepath";
    public static final String RESULT = "result";
    public static final String NOTIFICATION = "com.jsontodb.undoswipe";
    List<Item> items;// = new ArrayList<>();
    private int nu=0;
    DatabaseHandler db;

    public LoadFromPhoneBookService() {
        super("LoadFromPhoneBookService");
    }

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate from Service");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "onStartCommand from Service");
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent from Service");
        longRunningTaskReadingContacts();
        if(items.size()> 0){
            result = Activity.RESULT_OK;
        }
        publishResults(String.valueOf(items.size()), result);
    }

    private void publishResults(String outputPath, int result) {
        Log.d(TAG, "onPublishResult from Service");
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(FILEPATH, outputPath);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }

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

    private void longRunningTaskReadingContacts(){
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
                        Contact mycontact = new Contact();
                        mycontact.setID(id);
                        mycontact.setName(name);

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
                                mycontact.setPhoneNumber(phone);
                            }
                            pCur.close();
                            items.add(item);
                            //here save to db local
                            writeContactToDb(mycontact);
                            //contactList.add(mycontact);

                            //publishProgress(++i * 100 / cur.getCount(), item.getItemName() +"\n"+item.getItemMobile());
                            String publishProgress = ++i * 100 / cur.getCount()+"\n"+ item.getItemName() +"\n"+item.getItemMobile();
                            Log.d(TAG, "phone " + publishProgress);
                            // get email and type

                            ///Email
                            /*
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
                            emailCur.close();*/

                            // Get note.......
                            ///Note
                            /*
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
                            noteCur.close();*/

                            //Get Postal Address....
                            ///PostalAddress
                            /*
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
                            addrCur.close();*/

                            // Get Instant Messenger.........
                            /*
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
                            imCur.close();*/

                            // Get Organizations.........
                            /*
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
                            orgCur.close();*/
                        }
                    }
                }
                cur.close();
            }catch (Exception e){
                Log.d(TAG, "Exception on exporting contacts: \n" + e);
            }


            //return null;

    }
}
