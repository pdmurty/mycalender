package com.pdmurty.mycalender;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public class Sqlitedb  extends SQLiteOpenHelper {

   private Context myContext;

   public static final String KEY_ID = "_id";

    private boolean createDatabase = false;
    private boolean upgradeDatabase = false;
    private static String DB_NAME = "geonames.db";
    private static String DB_PATH = ""; //DB_DIR + DB_NAME;

    public Sqlitedb(@Nullable Context context, String dbName,int version) {
       super(context, dbName, null, version);
       DB_NAME = dbName;
       myContext = context;
       DB_PATH = myContext.getDatabasePath(DB_NAME).getAbsolutePath();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        createDatabase = true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        upgradeDatabase = true;

    }
   /**
     * Upgrade the database in internal storage if it exists but is not current.
     * Create a new empty database in internal storage if it does not exist.
     */
    public void initializeDataBase(String assetDB) {
        /*
         * Creates or updates the database in internal storage if it is needed
         * before opening the database. In all cases opening the database copies
         * the database in internal storage to the cache.
         */
        getWritableDatabase();

        if (createDatabase) {
            /*
             * If the database is created by the copy method, then the creation
             * code needs to go here. This method consists of copying the new
             * database from assets into internal storage and then caching it.
             */

            try {
                /*
                 * Write over the empty data that was created in internal
                 * storage with the one in assets and then cache it.
                 */
                copyDataBase(assetDB);
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        } else if (upgradeDatabase) {
            /*
             * If the database is upgraded by the copy and reload method, then
             * the upgrade code needs to go here. This method consists of
             * renaming the old database in internal storage, create an empty
             * new database in internal storage, copying the database from
             * assets to the new database in internal storage, caching the new
             * database from internal storage, loading the data from the old
             * database into the new database in the cache and then deleting the
             * old database from internal storage.
             */
            try {

               // FileHelper.copyFile(DB_PATH, OLD_DB_PATH);
                copyDataBase(assetDB);
                //SQLiteDatabase old_db = SQLiteDatabase.openDatabase(OLD_DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
                //SQLiteDatabase new_db = SQLiteDatabase.openDatabase(DB_PATH,null, SQLiteDatabase.OPEN_READWRITE);
                /*
                 * Add code to load data into the new database from the old
                 * database and then delete the old database from internal
                 * storage after all data has been transferred.
                 */
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }

    }
    private void copyDataBase(String assetDB) throws IOException {
        /*
         * Close SQLiteOpenHelper so it will commit the created empty database
         * to internal storage.
         */
        close();
        InputStream myInput = null;
        OutputStream myOutput = null;
        /*
         * Open the database in the assets folder as the input stream.
         */
        try {
            myInput = myContext.getAssets().open(assetDB);

        }catch (Exception e)
        {
             e.printStackTrace();
            Log.d("DB", e.getMessage()+ " inputstream excep");
        }

        /*
         * Open the empty db in interal storage as the output stream.
         */
        try {

            myOutput = new FileOutputStream(DB_PATH);

        }catch (Exception e)
        {
            Log.d("DB", e.getMessage()+ "-outstream excep");
        }


        /*
         * Copy over the empty db in internal storage with the database in the
         * assets folder.
         */
        FileHelper.copyFile(myInput, myOutput);
        /*
         * Access the copied database so SQLiteHelper will cache it and mark it
         * as created.
         */
        getWritableDatabase().close();
    }
}
