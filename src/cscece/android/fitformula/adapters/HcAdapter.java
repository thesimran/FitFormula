package cscece.android.fitformula.adapters;

import java.util.ArrayList;

import cscece.android.fitformula.R;
import cscece.android.fitformula.objects.HcListItem;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HcAdapter extends BaseAdapter {

	private ArrayList<HcListItem> hcList;
	private static LayoutInflater inflater=null;
	private Activity activity;
	
	public HcAdapter(ArrayList<HcListItem> list, Activity activity){
		hcList = list;
		this.activity = activity;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public Object getItem(int position) {
		
		return null;
	}

	public long getItemId(int position) {
		
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		final ViewHolder holder;
        if(convertView == null){
        	vi = inflater.inflate(R.layout.item_row_hc, null);
            holder = new ViewHolder();
            holder.title = (TextView) vi.findViewById(R.id.hc_item_title);
            holder.actualAge = (TextView) vi.findViewById(R.id.hc_actual_age);
            holder.heartAge = (TextView) vi.findViewById(R.id.hc_heart_age);
          
            vi.setTag(holder);
        } else {
        	holder = (ViewHolder)vi.getTag();
        }
        
        HcListItem item = hcList.get(position);
        
        holder.title.setText(item.getTitle());
        int type = item.getType();
        if(type == HcListItem.TYPE_BMI || type == HcListItem.TYPE_PAL){
        	holder.actualAge.setVisibility(View.GONE);
        	holder.heartAge.setVisibility(View.GONE);
        }else{
        	holder.actualAge.setText(item.getActualAge());
        	holder.heartAge.setText(item.getHeartAge());
        	holder.actualAge.setVisibility(View.VISIBLE);
        	holder.heartAge.setVisibility(View.VISIBLE);
        }
        
		return vi;
	}
	
	public int getCount() {

	    if (hcList == null) {
	    	return 0;
	    }

	    return hcList.size();
	}

	public static class ViewHolder{
        public TextView title;
        public TextView actualAge;
        public TextView heartAge;
    }
}
