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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.sentaroh.android.Utilities3.NotifyEvent;
import com.sentaroh.android.Utilities3.Widget.NonWordwrapTextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class AdapterScanFolderList  extends BaseAdapter {
      private static final Logger log= LoggerFactory.getLogger(AdapterScanFolderList.class);
	  private ActivityMain mActivity;
	  private LayoutInflater mLayoutInflater;
	  private ArrayList<ScanFolderItem> mFolderList=null;
	  
	  private static class ViewHolder {
		  public ImageButton del_btn;
		  public NonWordwrapTextView directory;
		  public CheckBox process_sub_directories;
		  public RadioButton include, exclude;
	  };

	  public AdapterScanFolderList(ActivityMain a, ArrayList<ScanFolderItem> fl) {
		  mActivity = a;
		  mLayoutInflater = LayoutInflater.from(a);
		  mFolderList=fl;
	  };

	  public void sort() {
		  Collections.sort(mFolderList, new Comparator<ScanFolderItem>(){
			@Override
			public int compare(ScanFolderItem lhs, ScanFolderItem rhs) {
				return lhs.folder_path.compareToIgnoreCase(rhs.folder_path);
			}
		  });
	  };
	  
	  public boolean contains(String directory) {
		  for(ScanFolderItem sfi:mFolderList) {
			  if (sfi.folder_path.equals(directory)) return true;
		  }
		  return false;
	  };

	  public int getCount() {
		  return mFolderList.size();
	  };

	  @Override
	  public boolean isEnabled(int pos) {
		  return !getItem(pos).deleted;
	  };
	  
	  public ScanFolderItem getItem(int position) {
		  return mFolderList.get(position);
	  };

	  public void addItem(ScanFolderItem item) {
		  mFolderList.add(item);
	  };

	  public long getItemId(int position) {
		  return position;
	  };

	  private NotifyEvent mNotifyDelete=null;
	  public void setNotifyDeleteListener(NotifyEvent ntfy) {mNotifyDelete=ntfy;}

	  private NotifyEvent mNotifyChange=null;
	  public void setNotifyChangeListener(NotifyEvent ntfy) {mNotifyChange=ntfy;}

	  @SuppressLint("InflateParams")
	  public View getView(final int position, View convertView, ViewGroup parent) {

		  ViewHolder holder;
		  if (convertView == null) {
		      convertView = mLayoutInflater.inflate(R.layout.scan_folder_list_item, null);
		      holder = new ViewHolder();
		      holder.del_btn = (ImageButton)convertView.findViewById(R.id.scan_folder_list_item_delete_btn);
		      holder.directory = (NonWordwrapTextView)convertView.findViewById(R.id.scan_folder_list_item_scan_folder);
		      holder.include = (RadioButton)convertView.findViewById(R.id.scan_folder_list_item_rb_select_type_include);
		      holder.exclude = (RadioButton)convertView.findViewById(R.id.scan_folder_list_item_rb_select_type_exclude);
		      holder.process_sub_directories=(CheckBox)convertView.findViewById(R.id.scan_folder_list_item_process_sub_diretories);
		      convertView.setTag(holder);
		  } else {
			  holder = (ViewHolder)convertView.getTag();
		  }

		  holder.directory.setText(getItem(position).folder_path);
		  if (getItem(position).deleted) {
//			  holder.del_btn.setText(mActivity.getString(R.string.msgs_main_edit_scan_folder_deleted_directory));
			  holder.del_btn.setEnabled(false);
              holder.del_btn.setAlpha(0.3F);
              holder.directory.setEnabled(false);
		      holder.include.setEnabled(false);
		      holder.exclude.setEnabled(false);
		      holder.process_sub_directories.setEnabled(false);
		  } else {
//			  holder.del_btn.setText(mActivity.getString(R.string.msgs_main_edit_scan_folder_to_delete_directory));
			  holder.del_btn.setEnabled(true);
              holder.del_btn.setAlpha(1.0F);
              holder.directory.setEnabled(true);
		      holder.include.setEnabled(true);
		      holder.exclude.setEnabled(true);
		      holder.process_sub_directories.setEnabled(true);
		  }
		  
		  holder.del_btn.setOnClickListener(new OnClickListener(){
			  @Override
			  public void onClick(View v) {
				  getItem(position).deleted=!getItem(position).deleted;
				  getItem(position);
				  notifyDataSetChanged();
				  if (mNotifyDelete!=null) mNotifyDelete.notifyToListener(true, null);
			  }
		  });
		  
		  holder.include.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			  @Override
			  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				  getItem(position).include=isChecked;
				  notifyDataSetChanged();
				  if (mNotifyChange!=null) mNotifyChange.notifyToListener(true, new Object[]{position});
			  }
		  });
		  
		  holder.process_sub_directories.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			  @Override
			  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				  getItem(position).process_sub_directories=isChecked;
				  notifyDataSetChanged();
				  if (mNotifyChange!=null) mNotifyChange.notifyToListener(true, new Object[]{position});
			  }
		  });

		  if (getItem(position).include) holder.include.setChecked(true);
		  else holder.exclude.setChecked(true);

		  holder.process_sub_directories.setChecked(getItem(position).process_sub_directories);
		  
		  return convertView;	
	  };
}

