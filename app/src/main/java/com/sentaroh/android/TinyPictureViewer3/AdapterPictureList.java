package com.sentaroh.android.TinyPictureViewer3;
/*
The MIT License (MIT)
Copyright (c) 2019 Sentaroh

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;

import com.sentaroh.android.TinyPictureViewer3.PictureUtil.PictureFileCacheItem;
import com.sentaroh.android.Utilities3.SafFile3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;

import it.sephiroth.android.library.imagezoom.ImageViewTouch.OnImageViewTouchSingleTapListener;

import static com.sentaroh.android.TinyPictureViewer3.Constants.*;

@SuppressLint("InflateParams")
public class AdapterPictureList extends PagerAdapter {
	private GlobalParameters mGp=null;
    private static final Logger log= LoggerFactory.getLogger(AdapterPictureList.class);	
    private Context mContext;
    private ActivityMain mActivity;

	private DisplayMetrics mDisplayMetrics=null;
    
    private ArrayList<PictureWorkItem> mPictureWorkList=new ArrayList<PictureWorkItem>();

	private Handler mUiHandler=new Handler();
    
    private LayoutInflater mLayoutInflator=null;
    
    private OnImageViewTouchSingleTapListener mSingleTapListener=null;
    
    private Bitmap mDummyThumbnail=null;
    
//	public AdapterPictureList(ActivityMain a, final PictureArray[] pal, 
//    		GlobalParameters gp) {
//        mContext = a.getApplicationContext();
//        mActivity=a;
//        mDisplayMetrics=a.getResources().getDisplayMetrics();
//        mGp=gp;
//        mPictureArray=pal;
//        
//        mLayoutInflator=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        
//        mUiHandler=new Handler();
//        
//    	Drawable dw=a.getResources().getDrawable(R.drawable.ic_128_tiny_picture_viewer, null);
//    	mDummyThumbnail=((BitmapDrawable) dw).getBitmap();
//    	
//    };

	public AdapterPictureList(ActivityMain a, final ArrayList<PictureListItem> al, 
    		GlobalParameters gp, int init_pos) {
        mContext=a.getApplicationContext();
        mActivity=a;
        mDisplayMetrics=a.getResources().getDisplayMetrics();
        mGp=gp;
        
        mLayoutInflator=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
//        mUiHandler=new Handler();
        
        mSingleTapListener=new OnImageViewTouchSingleTapListener(){
			@Override
			public void onSingleTapConfirmed() {
				mActivity.toggleFullScreenMode();
			}
    	};
    	Drawable dw=a.getResources().getDrawable(R.drawable.ic_128_tiny_picture_viewer, null);
    	mDummyThumbnail=((BitmapDrawable) dw).getBitmap();

    	for(int i=0;i<al.size();i++) {
    		PictureWorkItem pal=new PictureWorkItem();
    		pal.pictureItem=al.get(i);
    		mPictureWorkList.add(pal);
    	};
    	
    	
    	initPictureWorkItem(init_pos);
    	if ((init_pos-1)>=0) initPictureWorkItem(init_pos-1);
    	if ((init_pos+1)<al.size()) initPictureWorkItem(init_pos+1);

    };
    
    final public ArrayList<PictureWorkItem> getPictureWorkList() {
    	return mPictureWorkList;
    };

    private PictureFileCacheItem getBitmapCache(SafFile3 sf, String orientation) {
    	PictureFileCacheItem bm=PictureUtil.getPictureFileCacheItem(mGp, sf, mDisplayMetrics, orientation);
    	return bm;
    };

    private void initPictureWorkItem(int position) {
    	PictureWorkItem pw=mPictureWorkList.get(position);
		PictureListItem pfli=pw.pictureItem;
		
    	pw.image_file_name=pfli.getFileName();
    	pw.image_file_parent_directory=pfli.getParentDirectory();
    	if (pfli.getInputFile()==null) pw.image_saf_file=new SafFile3(mContext, Uri.parse(pfli.getPictureFileUriString()));
    	else pw.image_saf_file=pfli.getInputFile();
    	pw.image_file_path=pw.image_file_parent_directory+"/"+pw.image_file_name;
    	
		if (pfli.getExifImageHeight()==-1) {
			pfli.createExifInfo(pw.image_saf_file);
		}

    	pw.image_gps_longitude=pfli.getExifGpsLongitude();
    	pw.image_gps_latitude=pfli.getExifGpsLatitude();
    	pw.image_orientation=pfli.getExifImageOrientation();
    	
    	pw.image_thumbnail=pfli.getThumbnailImageByte();
        LinearLayout v=(LinearLayout)mLayoutInflator.inflate(R.layout.main_view_picture_pager, null);
    	pw.view=v;
    	pw.image_view=(CustomImageView)v.findViewById(R.id.main_view_picture_pager_image_view);
    	pw.image_view.setDisplayType(CustomImageView.DisplayType.FIT_TO_SCREEN);
    	pw.image_view.setSingleTapListener(mSingleTapListener);
    	pw.single_tap_listener=mSingleTapListener;
    	pw.image_file_info=PictureUtil.createPictureInfo(mContext, pfli);
    }
    
	@Override
	final public View instantiateItem(ViewGroup container, final int position) {
		long b_time=System.currentTimeMillis();
		log.debug("instantiateItem entered, pos="+position);
    	boolean load_required=false;
    	if (mPictureWorkList.get(position).view==null) {
    		initPictureWorkItem(position);
    	} else {
    		mPictureWorkList.get(position).image_view.setImageBitmap(null);
    	}
		final PictureWorkItem pa=mPictureWorkList.get(position);
		if (!PictureUtil.isBitmapCacheFileExists(mGp, pa.image_file_path)) {
			if (pa.image_thumbnail!=null) {
				Bitmap bm_in= BitmapFactory.decodeByteArray(pa.image_thumbnail,0,pa.image_thumbnail.length);
				setBitmapWithRestoreRotation(pa, bm_in);
			} else {
				pa.image_view.setImageBitmap(mDummyThumbnail, 
						pa.image_view.getDisplayMatrix(), PICTURE_VIEW_MIN_SCALE, PICTURE_VIEW_MAX_SCALE);
			}
			load_required=true;
		} else {
			PictureFileCacheItem pfbmci=getBitmapCache(pa.image_saf_file, pa.image_orientation);
//					PictureUtil.loadPictureFileCacheFile(mGp, pa.image_file_path);
            if (pfbmci!=null) {
                if (pfbmci.bitmap_byte_array!=null) {
                    Bitmap bm_in= BitmapFactory.decodeByteArray(pfbmci.bitmap_byte_array,0,pfbmci.bitmap_byte_array.length);
                    setBitmapWithRestoreRotation(pa, bm_in);
                    if (pa.image_saf_file.lastModified()!=pfbmci.file_last_modified || pa.image_saf_file.length()!=pfbmci.file_length) load_required=true;
                } else {
                    pa.image_view.setImageBitmap(mDummyThumbnail,
                            pa.image_view.getDisplayMatrix(), PICTURE_VIEW_MIN_SCALE, PICTURE_VIEW_MAX_SCALE);
                    load_required=true;
                }
            } else {
                if (pa.image_thumbnail!=null) {
                    Bitmap bm_in= BitmapFactory.decodeByteArray(pa.image_thumbnail,0,pa.image_thumbnail.length);
                    setBitmapWithRestoreRotation(pa, bm_in);
                } else {
                    pa.image_view.setImageBitmap(mDummyThumbnail,
                            pa.image_view.getDisplayMatrix(), PICTURE_VIEW_MIN_SCALE, PICTURE_VIEW_MAX_SCALE);
                }
                load_required=true;
            }
		}

		log.debug("instantiateItem completed, pos="+position+
    				", elapsed time="+(System.currentTimeMillis()-b_time)+", load_required="+load_required);
		
		if (load_required) {
			Thread load=new Thread(){
				@Override
				public void run(){
					if (pa!=null && pa.image_view.getBitMap()!=null) {
						PictureFileCacheItem bm=getBitmapCache(pa.image_saf_file, pa.image_orientation);
						if (bm.bitmap_byte_array!=null) {
							final Bitmap bm_in= BitmapFactory.decodeByteArray(bm.bitmap_byte_array,0,bm.bitmap_byte_array.length);
							mUiHandler.post(new Runnable(){
								@Override
								public void run() {
									if (pa.image_view!=null && pa.image_view.getBitMap()!=null) {
										pa.image_view.setImageBitmap(null);
										setBitmapWithRestoreRotation(pa, bm_in);
							    		log.debug("instantiateItem load bitmap completed, pos="+position);
									} else {
										bm_in.recycle();
									}
								}
							});
						}
					}
				}
			};
			load.setPriority(Thread.MAX_PRIORITY);
			load.start();
		}
		
//		bitmapPreFetch(position);
		
		pa.view.setTag(R.string.app_name, position);
		pa.view.setTag(R.string.app_name+1, pa.image_file_path);
   		container.addView(pa.view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        return pa.view;
	};

	private void setBitmapWithRestoreRotation(PictureWorkItem pa, Bitmap bm_in) {
		Matrix mx=null;
		mx=mZoomLockedMatrix==null?pa.image_view.getDisplayMatrix():mZoomLockedMatrix;
		float zr=mZoomLockedMatrix==null?pa.image_view.getScale():mZoomLockedZoomRatio;
		if (pa.image_rotation!=0.0f) {
			pa.image_view.setImageBitmap(PictureUtil.rotateBitmap(bm_in, (pa.image_rotation)), 
					mx, PICTURE_VIEW_MIN_SCALE, PICTURE_VIEW_MAX_SCALE);
			bm_in.recycle();
		} else {
			pa.image_view.setImageBitmap(bm_in, 
					mx, PICTURE_VIEW_MIN_SCALE, PICTURE_VIEW_MAX_SCALE);
		}
		pa.image_view.zoomTo(zr);
		pa.image_scale=zr;
	};
	
	private Matrix mZoomLockedMatrix=null;
	private float mZoomLockedZoomRatio=1.0f;
	public void setZoomLock(Matrix mx, float zoom_ratio) {
		mZoomLockedMatrix=mx;
		mZoomLockedZoomRatio=zoom_ratio;
	};
	public void applyZoomLock(int pos) {
		if (mZoomLockedMatrix!=null) {
			final PictureWorkItem pa=mPictureWorkList.get(pos);
			pa.image_view.setImageBitmap(pa.image_view.getBitMap(), 
					mZoomLockedMatrix, PICTURE_VIEW_MIN_SCALE, PICTURE_VIEW_MAX_SCALE);
			pa.image_view.zoomTo(mZoomLockedZoomRatio);
			pa.image_scale=mZoomLockedZoomRatio;
		}
	};
	public void unlockZoom() {
		mZoomLockedMatrix=null;
		mZoomLockedZoomRatio=1.0f;
	};
	
    public void remove(CustomViewPager cvp, int position) {
    	mPictureWorkList.remove(position);
    	notifyDataSetChanged();
    };
    
    @Override
    public int getItemPosition(Object object) {
    	int pos=(Integer)((View)object).getTag(R.string.app_name);
    	String fp=(String)((View)object).getTag(R.string.app_name+1);
    	int result=PagerAdapter.POSITION_NONE;
    	for(int i=0;i<mPictureWorkList.size();i++) {
    		PictureWorkItem pw=mPictureWorkList.get(i);
    		if (pw!=null && pw.image_file_path.equals(fp)) {
    			if (i==pos) result=PagerAdapter.POSITION_UNCHANGED;
    			else if (i!=pos) result=i;
    		}
    	}
    	return result;
    };
    
    @Override
    final public void destroyItem(ViewGroup container, int position, Object object) {
   		log.debug("destroyItem entered, pos="+position);
    	int before=container.getChildCount();
        container.removeView((View) object);
        int after=container.getChildCount();
        if (before==after) {
        	log.debug("destroyItem can not remove view pos="+position);
        }
    	String fp=(String)((View)object).getTag(R.string.app_name+1);
    	if (mPictureWorkList.size()>position && mPictureWorkList.get(position)!=null &&
    			mPictureWorkList.get(position).image_file_path.equals(fp)) {
    		mPictureWorkList.get(position).image_view.setImageBitmap(null);
    		System.gc();
    		Runtime.getRuntime().gc();
    	} else {
    		LinearLayout ll=((LinearLayout)object);
    		CustomImageView civ=(CustomImageView) ll.getChildAt(0);
    		civ.setImageBitmap(null);
    		civ=null;
    	}
    	object=null;
    };

    @Override
    final public int getCount() {
        return mPictureWorkList.size();
    };

    @Override
    final public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    };
    
//    final public void resetZoom() {
//    	for (int i=0;i<getCount();i++) {
//    		if (mPictureWorkList.get(i).image_view!=null) {
//    			mPictureWorkList.get(i).image_view.zoomTo(1.0f, 1);
//    		}
//    	}
//    };
    
    final public void cleanup() {
    	for(int i=0;i<mPictureWorkList.size();i++) {
    		if (mPictureWorkList.get(i)!=null && mPictureWorkList.get(i).image_view!=null) {
    			if (mPictureWorkList.get(i).image_view.getBitMap()!=null && mPictureWorkList.get(i).image_view.getBitMap().isRecycled()) {
    				mPictureWorkList.get(i).image_view.setImageBitmap(null);
    			}
    			mPictureWorkList.get(i).image_view=null;
    			mPictureWorkList.get(i).view=null;
    		}
    	}
    	mPictureWorkList.clear();
    	mPictureWorkList=null;

//    	mGp.pictureFileCacheList.clear();
    };
    
}

