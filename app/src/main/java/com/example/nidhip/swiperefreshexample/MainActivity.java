package com.example.nidhip.swiperefreshexample;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.R.attr.data;
import static android.R.attr.order;

public class MainActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeContainer;
    RecyclerView rv_contributors;
    Button btn_filter;
    MyAdapter mAdapter;
    List<Contributor> contributorList;
    ProgressDialog pDialog;
    Parcelable listState;
    RecyclerView.LayoutManager mLayoutManager;
    boolean is_filtered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        btn_filter = (Button) findViewById(R.id.btn_filter);
        rv_contributors = (RecyclerView) findViewById(R.id.rv_contributors);


        contributorList = new ArrayList<>();
        mAdapter = new MyAdapter(contributorList, MainActivity.this);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv_contributors.setLayoutManager(mLayoutManager);
        rv_contributors.setItemAnimator(new DefaultItemAnimator());
        rv_contributors.setAdapter(mAdapter);


        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Collections.sort(contributorList, Contributions);
                mAdapter.notifyDataSetChanged();
                is_filtered = true;

            }
        });

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                if (isNetworkAvailable()) {
                    getData();
                } else {
                    Toast.makeText(getApplicationContext(), "No internet connection!", Toast.LENGTH_LONG).show();
                    swipeContainer.setRefreshing(false);
                }
            }
        });


        rv_contributors = (RecyclerView) findViewById(R.id.rv_contributors);

        if (isNetworkAvailable()) {
            getData();
        } else {
            Toast.makeText(getApplicationContext(), "No internet connection!", Toast.LENGTH_LONG).show();
        }


    }

    public void getData() {

        String url = "https://api.github.com/repos/square/retrofit/contributors";

        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("ManActivity", response.toString());
                        pDialog.hide();

                        if (response.length() > 0) {

                            contributorList.clear();
                            Contributor contributor;

                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    contributor = new Contributor();

                                    contributor.setAvatar_url(jsonObject.getString("avatar_url"));
                                    contributor.setName(jsonObject.getString("login"));
                                    contributor.setRepos_url(jsonObject.getString("repos_url"));
                                    contributor.setContributions(jsonObject.getString("contributions"));

                                    contributorList.add(contributor);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            mAdapter.notifyDataSetChanged();
                        }

                        //stop refresh progress
                        if (swipeContainer.isRefreshing()) {
                            swipeContainer.setRefreshing(false);
                            is_filtered = false;
                        }


                        restoreState();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("ManActivity", "Error: " + error.getMessage());
                pDialog.hide();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(req);


    }

    public static Comparator<Contributor> Contributions = new Comparator<Contributor>() {
        @Override
        public int compare(Contributor c1, Contributor c2) {

            int contribution1 = Integer.parseInt(c1.getContributions());
            int contribution2 = Integer.parseInt(c2.getContributions());

            //For descending order
            return contribution1 - contribution2;

        }
    };


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (pDialog != null){
            pDialog.dismiss();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        listState = mLayoutManager.onSaveInstanceState();
        outState.putBoolean("is_filtered",is_filtered);
        outState.putParcelable("recycler_list_state", listState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null){
            is_filtered = savedInstanceState.getBoolean("is_filtered");
            listState = savedInstanceState.getParcelable("recycler_list_state");
        }

    }

    public void restoreState(){

        //restore filter in case of configuration change
        if (is_filtered){
            Collections.sort(contributorList, Contributions);
            mAdapter.notifyDataSetChanged();
        }

        //restore position of list in case of configuration change
        if (listState != null) {
            mLayoutManager.onRestoreInstanceState(listState);
            listState = null;
        }

    }

}
