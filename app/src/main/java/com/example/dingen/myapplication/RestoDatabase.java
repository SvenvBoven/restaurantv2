package com.example.dingen.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sven on 4/12/2017.
 */

public class RestoDatabase extends SQLiteOpenHelper {
    private static RestoDatabase instance;
    private static  String DATABASE_NAME = "RestoDatabase";
    private static  Integer DATABASE_VERSION = 1;

    private static  String TABLE_NAME = "resto";

    private static  String KEY_SQL_ID = "_id";
    private static  String KEY_SQL_NAME = "name";
    private static  String KEY_SQL_PRICE = "price";
    private static  String KEY_SQL_COUNT = "count";
    public Context context;
    public SQLiteDatabase db;
    private Cursor cursor;

    private RestoDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE resto(_id INTEGER PRIMARY KEY, name TEXT, price, INTEGER, count INTEGER DEFAULT 0)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public void databasefill(){
        db = instance.getWritableDatabase();
        RequestQueue queue = Volley.newRequestQueue(context);

        final String url = "https://resto.mprog.nl/menu?category";

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            JSONArray categories  = response.getJSONArray("items");
                            Log.d("jsonarray 50", response.getJSONArray("items").toString());
                            for (int i = 0; i < categories.length(); i++) {
                                JSONObject subitem = categories.getJSONObject(i);
                                Integer price = subitem.getInt("price");
                                Integer id = subitem.getInt("id");
                                String name = subitem.getString("name");
                                ContentValues data = new ContentValues();
                                data.put("_id", id);
                                data.put("name", name);
                                data.put("price", price);
                                db.insert("resto", null, data);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        );
        // add it to the RequestQueue
        queue.add(getRequest);
    }
    public void addItem(String name){
        db = instance.getWritableDatabase();

        String valueToIncrementBy = "1";

        String[] bindingArgs = new String[] { valueToIncrementBy, name};
        db.execSQL("UPDATE " + TABLE_NAME +
                " SET " + KEY_SQL_COUNT + "= " + KEY_SQL_COUNT + " + ?" +
                " WHERE " + KEY_SQL_NAME + "= ?",bindingArgs);
    }
    public Cursor selectAll(){

        String selectall = "SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_SQL_COUNT + " > 0";
        SQLiteDatabase db = instance.getWritableDatabase();
        cursor = db.rawQuery(selectall, null);
        return cursor;
    }
    public Cursor totalPrice() {
        String totalPricesql = "SELECT SUM( " + KEY_SQL_COUNT + "*" + KEY_SQL_PRICE + ") AS TOTAL FROM " + TABLE_NAME;
        cursor = db.rawQuery(totalPricesql, null);
        return cursor;
    }
    public static RestoDatabase getInstance(Context context) {
        if (instance != null) {
            return instance;
        } else {
            return instance = new RestoDatabase(context, "RestoDatabase", null, 1);
        }
    }
    public void clear(){
        db.execSQL("UPDATE resto SET count = 0 WHERE 1=1");
    }
}
