package cscece.android.fitformula;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;


public class ImageAdapter extends BaseAdapter {
    private Context mContext;    
    
    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
    	//return imageView;
        return null;
    }

    public long getItemId(int position) {
    	//return imageView.getId();
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(110, 110));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
            imageView.setBackgroundColor(Color.LTGRAY);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    // references to our images
    public Integer[] mThumbIds = {
            R.drawable.achievement_locked, R.drawable.achievement_locked, R.drawable.achievement_locked,
            R.drawable.achievement_locked, R.drawable.achievement_locked, R.drawable.achievement_locked,                        
            R.drawable.achievement_locked, R.drawable.achievement_locked, R.drawable.achievement_locked,
            R.drawable.achievement_locked, R.drawable.achievement_locked, R.drawable.achievement_locked,
            R.drawable.achievement_locked, R.drawable.achievement_locked, R.drawable.achievement_locked,
            R.drawable.achievement_locked, R.drawable.achievement_locked, R.drawable.achievement_locked,
            R.drawable.achievement_locked, R.drawable.achievement_locked, R.drawable.achievement_locked,
            R.drawable.achievement_locked, R.drawable.achievement_locked, R.drawable.achievement_locked,
            R.drawable.achievement_locked, R.drawable.achievement_locked, R.drawable.achievement_locked,
            R.drawable.achievement_locked, R.drawable.achievement_locked, R.drawable.achievement_locked            
    };
}
