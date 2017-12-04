package com.example.dingen.myapplication;



import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends ListFragment {

    public List<String> menuItems = new ArrayList<>();
    public ArrayAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = this.getArguments();
        String add = arguments.getString("category");
        Log.d("string add", add);
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        final String url = "https://resto.mprog.nl/menu?category=" + add;
        Log.d("string url", url);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("json response", response.toString());
                        try {
                            JSONArray categories = response.getJSONArray("items");
                            Log.d("json", categories.toString());
                            for (int i = 0; i < categories.length(); i++) {
                                JSONObject subitem = categories.getJSONObject(i);
                                menuItems.add(subitem.getString("name"));
                            }
                            updateAdapter();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        );
        Log.d("jsonurl", getRequest.toString());

        // add it to the RequestQueue
        queue.add(getRequest);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String name = l.getItemAtPosition(position).toString();
        RestoDatabase db = RestoDatabase.getInstance(getContext());
        db.addItem(name);

    }

    public void updateAdapter() {
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, menuItems);
        this.setListAdapter(adapter);
    }
}