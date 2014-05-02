package com.infiniteecho.test.app;

import com.infiniteecho.test.app.R;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by umesh on 4/30/14.
 */
public class MobileArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private List<String> tweetList = new ArrayList<String>();
    private List<String> fileList = new ArrayList<String>();
    private String imagePath = "sdcard/imgs/";
    public MobileArrayAdapter(Context context, List<String> tweetList, List<String> fileList) {
        super(context, R.layout.list_mobile, tweetList);
        this.context = context;
        this.tweetList = tweetList;
        this.fileList = fileList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewHolder holder;
        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.list_mobile, parent, false);
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.label);
            holder.thumbnail = (ImageView) convertView.findViewById(R.id.logo);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }


        holder.text.setText(tweetList.get(position));

        holder.position = position;
        new ThumbnailTask(position, holder)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);

        return convertView;

    }
    private class ThumbnailTask extends AsyncTask<Void, Bitmap, Bitmap> {
        private int mPosition;
        private ViewHolder mHolder;

        public ThumbnailTask(int position, ViewHolder holder) {
            mPosition = position;
            mHolder = holder;
        }

        @Override
        protected Bitmap doInBackground(Void... arg0) {
            // Download bitmap here

            File imageFile = new File(imagePath+fileList.get(mPosition));
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (mHolder.position == mPosition) {
                mHolder.thumbnail.setImageBitmap(bitmap);
            }
        }
    }

    private static class ViewHolder {
        public TextView text;
        public ImageView thumbnail;
        public int position;
    }
    /**
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimum value
     * @param max Maximum value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int randInt(int min, int max) {

        // Usually this can be a field rather than a method variable
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}
