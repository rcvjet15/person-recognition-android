package org.opencv.samples.facedetect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.opencv.samples.facedetect.data.PersonContract.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robi on 13/09/2017.
 */

public class PeopleActivity extends BaseAppActivity implements PeopleAdapter.PeopleAdapterOnClickHandler {

    private PeopleAdapter mPeopleAdapter;
    private RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;
    private ProgressBar mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewPeopleList);

        mProgressDialog = (ProgressBar) findViewById(R.id.pbLoadingIndicator);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        mPeopleAdapter = new PeopleAdapter(this, this);

        mRecyclerView.setAdapter(mPeopleAdapter);

        fetchPeople();
    }

    @Override
    public void onClick(long id) {
        Intent personShowIntent = new Intent(this, PeopleActivity.class);

    }

    private void showLoading(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressDialog.setVisibility(View.VISIBLE);
    }

    private void showPeople(){
        mProgressDialog.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void fetchPeople(){

        new AsyncTask<Void, Void, Cursor>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoading();
            }

            @Override
            protected Cursor doInBackground(Void... params) {
                db = dbHelper.getReadableDatabase();

                String[] projectionColumns = new String[]{
                        PersonEntry._ID,
                        PersonEntry.COLUMN_FIRST_NAME,
                        PersonEntry.COLUMN_LAST_NAME,
                        PersonEntry.COLUMN_PROFILE_PIC
                };

                Cursor cursor  = null;

                try{
                    cursor = db.query(
                            PersonEntry.TABLE_NAME,
                            projectionColumns,
                            null,
                            null,
                            null,
                            null,
                            PersonEntry._ID
                    );
                }
                catch (IllegalArgumentException e){
                    e.printStackTrace();
                }
                finally {
                    // If cursor is closed, data won't be show in recyclerview
//            cursor.close();
                }
                return cursor;
            }

            @Override
            protected void onPostExecute(Cursor cursor) {
                super.onPostExecute(cursor);
                showPeople();
                mPeopleAdapter.swapCursor(cursor);
                if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
                mRecyclerView.smoothScrollToPosition(mPosition);

            }
        }.execute();
    }
}
