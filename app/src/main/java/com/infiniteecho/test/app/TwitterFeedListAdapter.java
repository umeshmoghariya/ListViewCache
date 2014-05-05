package com.infiniteecho.test.app;

import com.infiniteecho.test.app.R;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
public class TwitterFeedListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private List<String> tweetList = new ArrayList<String>();
    private List<String> fileList = new ArrayList<String>();
    private String imagePath = "sdcard/imgs/";
    private LruCache<String,Bitmap> mMemoryCache;

    public TwitterFeedListAdapter(Context context, List<String> tweetList, List<String> fileList) {
        super(context, R.layout.list_mobile, tweetList);
        this.context = context;
        this.tweetList = tweetList;
        this.fileList = fileList;


        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        /*Google advices [Shows in demos] using 1/8th of the available memory for this memory cache.
        *To obtain better performance
        */

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        final int cacheDivisor;
        switch(metrics.densityDpi) {

            //High Density. 480 DPI
            case 480:
                cacheDivisor = 4;
                break;

            //320 DPI
            case 320:
                cacheDivisor = 3;
                break;
            default:
                cacheDivisor = 2;
                break;
        }

        final int cacheSize = maxMemory / cacheDivisor;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
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

            if(getBitmapFromMemCache(mPosition+"")==null)
            {
                Log.i("Cache","Miss");
                File imageFile = new File(imagePath+fileList.get(mPosition));
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                mMemoryCache.put(mPosition+"", bitmap);
                return bitmap;
            }
            else
            {
                Log.i("Cache", "Hit");
                Bitmap bitmap = getBitmapFromMemCache(mPosition+"");
                return bitmap;
            }


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
