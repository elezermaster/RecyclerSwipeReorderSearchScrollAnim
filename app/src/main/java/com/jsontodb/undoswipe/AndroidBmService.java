package com.jsontodb.undoswipe;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by elezermaster on 23/07/17.
 */
public class AndroidBmService extends Service /*implements BmService*/ {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
