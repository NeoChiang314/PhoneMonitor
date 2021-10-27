package com.example.phonemonitor;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class MyDatabaseContext extends ContextWrapper {

    private static final String DEBUG_CONTEXT = "DatabaseContext";

    public MyDatabaseContext(Context base) {
        super(base);
    }

    @Override
    public File getDatabasePath (String name){
        File sdcard = getExternalFilesDir(null);
        String dbfile = sdcard.getAbsolutePath() + File.separator+ "PMDatabases" + File.separator + name;

        if (!dbfile.endsWith(".db")) {
            dbfile += ".db" ;
        }

        File result = new File(dbfile);

        if (!result.getParentFile().exists()) {
            result.getParentFile().mkdirs();
        }

        if (Log.isLoggable(DEBUG_CONTEXT, Log.WARN)) {
            Log.w(DEBUG_CONTEXT, "getDatabasePath(" + name + ") = " + result.getAbsolutePath());
        }

        return result;

    }
}
