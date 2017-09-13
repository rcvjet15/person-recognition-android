package org.opencv.samples.facedetect.utilities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.ActivityChooserView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.opencv.samples.facedetect.BaseAppActivity;
import org.opencv.samples.facedetect.PeopleAdapter;
import org.opencv.samples.facedetect.Person;
import org.opencv.samples.facedetect.R;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

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

    private void fetchPeople(){
        db = dbHelper.getReadableDatabase();

        String[] projectionColumns = new String[]{
                PersonEntry._ID,
                PersonEntry.COLUMN_FIRST_NAME,
                PersonEntry.COLUMN_LAST_NAME,
                PersonEntry.COLUMN_PROFILE_PIC
        };

        Cursor cursor = db.query(
                PersonEntry.TABLE_NAME,
                projectionColumns,
                null,
                null,
                null,
                null,
                PersonEntry._ID
        );

        List<Person> people = new ArrayList<>();

        try{
            mPeopleAdapter.swapCursor(cursor);
            if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
            mRecyclerView.smoothScrollToPosition(mPosition);

            if (cursor.getCount() != 0) Toast.makeText(this, "People count is " + cursor.getCount(), Toast.LENGTH_SHORT).show();
        }
        catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        finally {
            cursor.close();
        }

    }
}
