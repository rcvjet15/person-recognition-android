package org.opencv.samples.facedetect;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.samples.facedetect.data.PersonContract.*;
import org.opencv.samples.facedetect.utilities.ImageUtils;

/**
 * Created by Robi on 13/09/2017.
 */

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.PeopleAdapterViewHolder> {

    private final Context mContext;

    private Cursor mCursor;

    final private PeopleAdapterOnClickHandler mClickHandler;

    public interface PeopleAdapterOnClickHandler{
        void onClick(long id);
    }

    public PeopleAdapter(@NonNull Context context, PeopleAdapterOnClickHandler clickHandler){
        mContext = context;
        mClickHandler = clickHandler;
    }

    @Override
    public PeopleAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater
                .from(mContext)
                .inflate(R.layout.person_list_item, viewGroup, false);
        view.setFocusable(true);
        return new PeopleAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PeopleAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        long id = mCursor.getLong(
                mCursor.getColumnIndexOrThrow(PersonEntry._ID));

        String firstName = mCursor.getString(
                mCursor.getColumnIndexOrThrow(PersonEntry.COLUMN_FIRST_NAME));

        String lastName = mCursor.getString(
                mCursor.getColumnIndexOrThrow(PersonEntry.COLUMN_LAST_NAME));

        byte[] imageByteArr = mCursor.getBlob(
                mCursor.getColumnIndexOrThrow(PersonEntry.COLUMN_PROFILE_PIC));

//        Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArr, 0, imageByteArr.length);
//        RoundedBitmapDrawable roundBitmap = RoundedBitmapDrawableFactory.create(mContext.getResources(), bitmap);
//        roundBitmap.setCircular(true);
//
//        holder.profilePic.setImageDrawable(roundBitmap);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;

        Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArr, 0, imageByteArr.length, options);
        RoundedBitmapDrawable roundBitmap = RoundedBitmapDrawableFactory.create(mContext.getResources(), bitmap);
        roundBitmap.setCircular(true);

        holder.profilePic.setImageDrawable(roundBitmap);

        String personSummary = String.format("%s %s", lastName, firstName);
        holder.personSummary.setText(personSummary);
    }

    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    public void swapCursor(Cursor newCursor){
        mCursor = newCursor;
        notifyDataSetChanged();
    }


    class PeopleAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final ImageView profilePic;
        final TextView personSummary;

        PeopleAdapterViewHolder(View view){
            super(view);

            profilePic = (ImageView) view.findViewById(R.id.personItemProfilePic);
            personSummary = (TextView) view.findViewById(R.id.personItemSummary);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);

            long id = mCursor.getLong(
                    mCursor.getColumnIndexOrThrow(PersonEntry._ID));

            if (id < 1) return;


        }
    }
}
