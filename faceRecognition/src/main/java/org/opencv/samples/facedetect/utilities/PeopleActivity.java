package org.opencv.samples.facedetect.utilities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import org.opencv.samples.facedetect.BaseAppActivity;
import org.opencv.samples.facedetect.PeopleAdapter;
import org.opencv.samples.facedetect.R;

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

    }

    @Override
    public void onClick(long id) {
        Intent personShowIntent = new Intent(this, PeopleActivity.class);

    }
}
