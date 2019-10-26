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
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sentaroh.android.Utilities3.Dialog.CommonDialog;
import com.sentaroh.android.Utilities3.NotifyEvent;
import com.sentaroh.android.Utilities3.StringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.sentaroh.android.TinyPictureViewer3.Constants.*;

public class AdapterFolderList extends BaseAdapter {
      private static final Logger log= LoggerFactory.getLogger(AdapterFolderList.class);
	  private ActivityMain mActivity;
	  private LayoutInflater mLayoutInflater;
	  private ArrayList<FolderListItem> mFolderList=null;
	  private int mViewHeight=0, mViewWidth=0;
	  
	  private static class ViewHolder {
		  public LinearLayout folder_desc_view;
		  public ImageView image_view;
		  public TextView folder_name, directory, no_of_pictures, latest_picture_time;
		  public CheckBox checkbox;
	  }

	  private Drawable mDummyThumbnail=null;
	  private boolean mShowSimpleFolderView=false;
	  public AdapterFolderList(ActivityMain a, ArrayList<FolderListItem> fl, boolean show_simple_folder_view) {
		  mActivity = a;
		  mLayoutInflater = LayoutInflater.from(a);
		  mFolderList=fl;
          mShowSimpleFolderView=show_simple_folder_view;
		  mDummyThumbnail=a.getResources().getDrawable(R.drawable.ic_128_tiny_picture_viewer, null);
		  Resources res = a.getResources();
		  mViewHeight=(int) res.getDimension(R.dimen.folder_list_image_height);
		  mViewWidth=(int) res.getDimension(R.dimen.folder_list_image_width);
	  }
	  
//	  @Override
//	  public boolean isEnabled(int pos) {
//		  return false;
//	  }
	  
	  public void setFolderList(ArrayList<FolderListItem> p) {
		  mFolderList=p;
		  sort();
	  }
		  
	  public ArrayList<FolderListItem> getFolderList() {return mFolderList;}

	  private int mSortOrder=SORT_ORDER_ASCENDANT;
	  public void setSortOrder(int order) {mSortOrder=order;}
	  public int getSortOrder() {return mSortOrder;}

	  private int mSortKey=SORT_KEY_FOLDER_NAME;
	  public void setSortKey(int key) {mSortKey=key;}
	  public int getSortKey() {return mSortKey;}
	  
	  public void sort() {
		  sort(mFolderList, mSortKey, mSortOrder);
		  notifyDataSetChanged();
	  };
	  
	  static public void sort(ArrayList<FolderListItem>fl, final int key, final int order) {
		  Collections.sort(fl, new Comparator<FolderListItem>(){
			@Override
			public int compare(FolderListItem lhs, FolderListItem rhs) {
				if (key==SORT_KEY_FOLDER_NAME) {
					if (order==SORT_ORDER_ASCENDANT) {
						return lhs.getFolderName().compareToIgnoreCase(rhs.getFolderName());
					} else {
						return rhs.getFolderName().compareToIgnoreCase(lhs.getFolderName());
					}
				} else if (key==SORT_KEY_FOLDER_DIR_LAST_MODIFIED) {
					if (order==SORT_ORDER_ASCENDANT) {
						return Long.valueOf(lhs.getFileLastModified()).compareTo(Long.valueOf(rhs.getFileLastModified()));
					} else {
						return Long.valueOf(rhs.getFileLastModified()).compareTo(Long.valueOf(lhs.getFileLastModified()));
					}
				} else if (key==SORT_KEY_FOLDER_PATH) {
					if (order==SORT_ORDER_ASCENDANT) {
						return lhs.getParentDirectory().compareToIgnoreCase(rhs.getParentDirectory());
					} else {
						return rhs.getParentDirectory().compareToIgnoreCase(lhs.getParentDirectory());
					}
				}

				return 0;
			}
		  });
		  ArrayList<FolderListItem>top_folder=new ArrayList<FolderListItem>();
		  for(FolderListItem fli:fl) {
			  if (fli.isAlwaysTop()) {
				  top_folder.add(fli);
			  }
		  }
		  if (top_folder.size()>0) {
			  fl.removeAll(top_folder);
			  fl.addAll(0,top_folder);
		  }
	  };

	  public int getCount() {
		  return mFolderList.size();
	  }

	  public FolderListItem getItem(int position) {
		  return mFolderList.get(position);
	  }

	  public long getItemId(int position) {
		  return position;
	  }

	  public void setAllItemsSelected(boolean p) {
		  for(FolderListItem pli:mFolderList) pli.setSelected(p);
	  };
	  
	  public boolean isAllItemSelected() {
		  for(FolderListItem pli:mFolderList) 
			  if (!pli.isSelected()) {
				  return false;
			  }
		  return true;
	  };

	  public int getSelectedItemCount() {
		int result=0;
		for(FolderListItem pli:mFolderList) if (pli.isSelected()) result++;
		return result;
	  };

	  private boolean select_mode=false;
	  public void setSelectMode(boolean p) {
			select_mode=p;
			if (!p) {
				setAllItemsSelected(false);
			}
	  };
	  public boolean isSelectMode() {return select_mode;}
	  
	  public boolean isAnyItemSelected() {
		  for(FolderListItem pli:mFolderList) if (pli.isSelected()) return true;
		  return false;
	  };

	  private NotifyEvent mCheckedChangeNotify=null;
	  public void setCheckedChangeListener(NotifyEvent ntfy) {
		  mCheckedChangeNotify=ntfy;
	  };
	  
	  @Override
	  public boolean isEnabled(int pos) {
		  if (isAdapterEnabled() && getItem(pos).isEnabled()) return true;
		  else return false;
	  };

	  public void setEnabled(int pos, boolean enabled) {
		  getItem(pos).setEnabled(enabled);
	  };
	  public void setAllItemsEnabled(boolean enabled) {
		  synchronized(mFolderList) {
			  for(FolderListItem fli:mFolderList) fli.setEnabled(enabled);
		  }
	  };
	  private boolean mAdapterEnabled=true;
	  public void setAdapterEnabled(boolean enabled) {
		  mAdapterEnabled=enabled;
	  };
	  public boolean isAdapterEnabled() {
		  return mAdapterEnabled;
	  }

	  private boolean mCacheCreateWarningIssued=false;
	  @SuppressLint("InflateParams")
	  public View getView(int position, View convertView, ViewGroup parent) {

		  ViewHolder holder;
		  if (convertView == null) {
		      if (mShowSimpleFolderView) convertView = mLayoutInflater.inflate(R.layout.cell_folder_simple_view, null);
		      else convertView = mLayoutInflater.inflate(R.layout.cell_folder_detail_view, null);
		      holder = new ViewHolder();
		      holder.folder_desc_view=(LinearLayout)convertView.findViewById(R.id.cell_folder_view);
		      holder.image_view = (ImageView)convertView.findViewById(R.id.cell_folder_view_image);
		      holder.folder_name = (TextView)convertView.findViewById(R.id.cell_folder_view_folder_name);
		      holder.directory = (TextView)convertView.findViewById(R.id.cell_folder_view_directory);
		      holder.no_of_pictures = (TextView)convertView.findViewById(R.id.cell_folder_view_no_of_pictures);
		      holder.latest_picture_time=(TextView)convertView.findViewById(R.id.cell_folder_view_latest_picture_time);
		      holder.checkbox=(CheckBox)convertView.findViewById(R.id.cell_folder_view_checkbox);
		      holder.checkbox.setBackgroundColor(Color.argb(200,0,0,0));
		      convertView.setTag(holder);
		  } else {
			  holder = (ViewHolder)convertView.getTag();
		  }
		  
		  if (isSelectMode()) {
			  holder.checkbox.setVisibility(CheckBox.VISIBLE);
		  } else {
			  holder.checkbox.setVisibility(CheckBox.GONE);
		  }
		  if (getCount()>0 && position<getCount()) {
			  FolderListItem fli=getItem(position);
			  if (fli.getThumbnailArray()!=null) {
				  Bitmap bm= BitmapFactory.decodeByteArray(fli.getThumbnailArray(), 0, fli.getThumbnailArray().length);
                  holder.image_view.setScaleType(ScaleType.CENTER_CROP);
				  if (bm!=null) {
//                      int b_w=bm.getWidth();
//                      int b_h=bm.getHeight();
//
//                      Matrix matrix=new Matrix();
//
//                      float scale_w=(float)mViewWidth/(float)b_w;
//                      float scale_h=(float)mViewHeight/(float)b_h;
//                      float scale=Math.min(scale_w, scale_h);
//
//                      matrix.postScale(scale, scale);
//
//                      int s_w=Math.round((float)b_w*scale);
//                      if (s_w<mViewWidth) {
//                          int translate_val=Math.abs(mViewWidth-s_w);
//                          if (translate_val>1) {
//                              matrix.postTranslate((float)(translate_val/2), 0.0f);
//                          }
//                      }
//                      holder.image_view.setScaleType(ScaleType.MATRIX);
//                      holder.image_view.setImageMatrix(matrix);

                      holder.image_view.setImageBitmap(bm);
                      holder.image_view.setBackgroundColor(Color.BLACK);
                  } else {
//                      holder.image_view.setScaleType(ScaleType.FIT_CENTER);
                      holder.image_view.setImageDrawable(mDummyThumbnail);
                      if (!mCacheCreateWarningIssued) {
                          mCacheCreateWarningIssued=true;
                          CommonDialog cd=new CommonDialog(mActivity.getApplicationContext(), mActivity.getSupportFragmentManager());
                          cd.showCommonDialog(false, "W", mActivity.getApplicationContext().getString(R.string.msgs_main_picture_cache_file_may_be_corrupted), "", null);
                      }
                  }
			  } else {
				  holder.image_view.setScaleType(ScaleType.FIT_CENTER);
				  holder.image_view.setImageDrawable(mDummyThumbnail);
			  }
			  holder.folder_name.setText(fli.getFolderName());
			  holder.directory.setText(fli.getParentDirectory());
			  holder.no_of_pictures.setText(
					  String.format(mActivity.getApplicationContext().getString(R.string.msgs_main_folder_view_no_of_pictures), fli.getNoOfPictures()));
			  holder.latest_picture_time.setText(StringUtil.convDateTimeTo_YearMonthDayHourMinSec(fli.getFileLastModified()));
			  
			  if (fli.isEnabled()) holder.folder_desc_view.setAlpha(1.0f);
			  else holder.folder_desc_view.setAlpha(0.4f);
		  }
		  final int p=position;
		  holder.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (p<getCount()) {
					getItem(p).setSelected(isChecked);
					if (mCheckedChangeNotify!=null) mCheckedChangeNotify.notifyToListener(true, new Object[]{isChecked});
				}
			}
		  });
		  holder.checkbox.setChecked(getItem(position).isSelected());
		  
		  return convertView;	
	  }
}