package com.sentaroh.android.TinyPictureViewer3;
/*
The MIT License (MIT)
Copyright (c) 2011-2019 Sentaroh

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
and to permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be included in all copies or
substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.

*/

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomActionBarSpinnerAdapter extends ArrayAdapter<String> {
    private static Logger log= LoggerFactory.getLogger(CustomActionBarSpinnerAdapter.class);
	private Context mContext;

	public CustomActionBarSpinnerAdapter(Context c) {
		super(c, 0);
		mContext=c;
	}
	
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
        	view= LayoutInflater.from(mContext).inflate(R.layout.action_bar_spinner_item, null);
        } else {
            view=convertView;
        }
//    	final float dp_scale = getContext().getResources().getDisplayMetrics().density;
//    	final float sp_scale = getContext().getResources().getDisplayMetrics().scaledDensity;
        TextView tv=(TextView)view.findViewById(R.id.text);
//        tv.setPadding(0, 0, 50, 0);
        tv.setText(getItem(position));
    	tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        if (getCount()==1) tv.setAlpha(0.3f);
        else tv.setAlpha(1.0f);
        return view;
	}
	
//	@SuppressWarnings("deprecation")
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
        final TextView text=(TextView)super.getDropDownView(position, convertView, parent);
    	// Convert the dips to pixels  
    	final float dp_scale = getContext().getResources().getDisplayMetrics().density;
//    	final float sp_scale = getContext().getResources().getDisplayMetrics().scaledDensity;z
    	text.setMinWidth((int) (100*dp_scale));
//    	text.setTextSize(10*sp_scale);
    	
//		text.setCompoundDrawablesWithIntrinsicBounds(null,null,
//          		mContext.getResources().getDrawable(android.R.drawable.btn_radio), 
//          		null );
		text.post(new Runnable(){
			@Override
			public void run() {
				text.setSingleLine(false);
			}
		});
        return text;
	}
	
}
