package com.jsontodb.undoswipe;

import android.app.Application;

import org.polaric.colorful.Colorful;

/**
 * Created by elezermaster on 23/07/17.
 */
public class ContactsApp  extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        /*
        Colorful.defaults()
                .primaryColor(Colorful.ThemeColor.RED)
                .accentColor(Colorful.ThemeColor.BLUE)
                .translucent(false)
                .dark(true);
        Colorful.init(this);
        */
        Colorful.init(this);
    }
}
