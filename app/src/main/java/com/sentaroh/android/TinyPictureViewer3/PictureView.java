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
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.sentaroh.android.TinyPictureViewer3.CustomImageView.OnZoomChangedListener;
import com.sentaroh.android.Utilities3.Dialog.CommonDialog;
import com.sentaroh.android.Utilities3.NotifyEvent;
import com.sentaroh.android.Utilities3.NotifyEvent.NotifyEventListener;
import com.sentaroh.android.Utilities3.SafFile3;
import com.sentaroh.android.Utilities3.Widget.NonWordwrapTextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;

import static com.sentaroh.android.TinyPictureViewer3.Constants.*;

public class PictureView {
    private static Logger log= LoggerFactory.getLogger(PictureView.class);
	private GlobalParameters mGp=null;
	private CommonDialog mCommonDlg=null;
	@SuppressWarnings("unused")
	private FragmentManager mFragmentManager=null;
	private Context mContext=null;
	private ActivityMain mActivity=null;
	private CommonUtilities mUtil=null;
	private Handler mUiHandler=null;
	
	public PictureView(ActivityMain a, GlobalParameters gp, CommonUtilities cu, CommonDialog cd) {
		mActivity=a;
		mGp=gp;
		mCommonDlg=cd;
		mUtil=cu;
		mContext=gp.appContext;
		mUiHandler=new Handler();
		mFragmentManager=a.getSupportFragmentManager();
		
       if (!mGp.settingPictureDisplayOptionRestoreWhenStartup) {
    	   mGp.settingPictureDisplayLastUiMode=mGp.settingPictureDisplayDefualtUiMode;
       }

	}
	
	public void closeView() {
		mGp.customViewPager.setAdapter(null);
		mGp.adapterPictureView.cleanup();
		mGp.adapterPictureView=null;
	}
	
	public void configChanged() {
		
	}
	
	@SuppressLint("InflateParams")
	public void createView() {
		LayoutInflater vi = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout ll_picture=(RelativeLayout)vi.inflate(R.layout.main_view_picture,null);
        LinearLayout container_picture=(LinearLayout)mActivity.findViewById(R.id.main_view_picture_container);
        container_picture.addView(ll_picture, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		mGp.pictureView=(RelativeLayout)ll_picture.findViewById(R.id.main_view_picture_view);
//		mGp.mPictureView.setBackgroundColor(Color.BLACK);//mGp.themeColorList.window_background_color_content);
		mGp.pictureView.setVisibility(LinearLayout.GONE);
		mGp.pictureViewTopControl=(LinearLayout)ll_picture.findViewById(R.id.main_view_picture_image_top_control);
//		mGp.pictureViewTopControl.setBackgroundColor(Color.argb(255, 32, 32, 32));
		mGp.pictureViewBottomControl=(LinearLayout)ll_picture.findViewById(R.id.main_view_picture_image_bottom_control);
		mGp.pictureViewFileName=(TextView)ll_picture.findViewById(R.id.main_view_picture_image_file_name);
		mGp.pictureViewZoomRatio=(TextView)ll_picture.findViewById(R.id.main_view_picture_image_zoom_ratio);
		mGp.pictureViewFileName.setBackgroundColor(Color.argb(128, 32, 32, 32));
		mGp.pictureViewZoomRatio.setBackgroundColor(Color.argb(128, 32, 32, 32));
		mGp.pictureViewFileInfo=(NonWordwrapTextView) ll_picture.findViewById(R.id.main_view_picture_image_info);
		mGp.pictureViewFileInfo.setBackgroundColor(Color.argb(128, 32, 32, 32));
		mGp.picturePrevBtn=(ImageButton)ll_picture.findViewById(R.id.main_view_picture_image_prev);
//		mImagePrev.setBackgroundColor(Color.argb(00, 32, 32, 32));
		mGp.pictureNextBtn=(ImageButton)ll_picture.findViewById(R.id.main_view_picture_image_next);
//		mImageNext.setBackgroundColor(Color.argb(00, 32, 32, 32));
		mGp.pictureZoomInBtn=(ImageButton)ll_picture.findViewById(R.id.main_view_picture_image_zoom_in);
		mGp.pictureZoomOutBtn=(ImageButton)ll_picture.findViewById(R.id.main_view_picture_image_zoom_out);
		mGp.pictureLockScreenRotationBtn=(ImageButton)ll_picture.findViewById(R.id.main_view_picture_lock_screen_rotation);
		mGp.pictureShowMapBtn=(ImageButton)ll_picture.findViewById(R.id.main_view_picture_image_show_map);
		mGp.pictureRotatePictureRightBtn=(ImageButton)ll_picture.findViewById(R.id.main_view_picture_rotate_picture_right);
		mGp.pictureRotatePictureLeftBtn=(ImageButton)ll_picture.findViewById(R.id.main_view_picture_rotate_picture_left);
		mGp.pictureLockZoomBtn=(ImageButton)ll_picture.findViewById(R.id.main_view_picture_image_lock_zoom);
		mGp.pictureShareBtn=(ImageButton)ll_picture.findViewById(R.id.main_view_picture_share);
		mGp.pictureWallpaperBtn=(ImageButton)ll_picture.findViewById(R.id.main_view_picture_image_wallpaper);
		mGp.pictureDeleteBtn=(ImageButton)ll_picture.findViewById(R.id.main_view_picture_delete);

        mGp.pictureLeftBtn=(Button)ll_picture.findViewById(R.id.main_view_picture_left_button);
        mGp.pictureRightBtn=(Button)ll_picture.findViewById(R.id.main_view_picture_right_button);
		
		mGp.customViewPagerView=(LinearLayout)ll_picture.findViewById(R.id.main_view_picture_image_pager_view);
		
		mGp.pictureResetBtn=(ImageButton)ll_picture.findViewById(R.id.main_view_picture_reset);
		try {
			mGp.customViewPager=new CustomViewPager(mContext);
			mGp.customViewPager.setPageMargin(20);
			mGp.customViewPager.enableDefaultPageTransformer(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mGp.customViewPagerView.addView(mGp.customViewPager);
		CommonUtilities.setEdgeGlowColor(mGp.customViewPager);
		
		setPictureViewPagerListener();
		setPictureViewPrevNextButtonEnabled(mGp.customViewPager);
		setPictureViewListener();

	}
	
	public ViewSaveObjects saveViewContents() {
		ViewSaveObjects sv=new ViewSaveObjects();
		
		sv.picture_view_pos_x=mGp.customViewPager.getCurrentItem();
		sv.picture_view_file_name=mGp.pictureViewFileName.getText().toString();
		sv.picture_view_info=mGp.pictureViewFileInfo.getText().toString();
		sv.picture_view_show_info=mGp.pictureViewFileInfo.getVisibility();
		sv.picture_view_zoom=mGp.pictureViewZoomRatio.getText().toString();
		
		sv.picture_map_button_visibility=mGp.pictureShowMapBtn.getVisibility();
	    mGp.pictureShowTestMode=false;
	    
	    sv.picture_view_reset_enabled=mGp.pictureResetBtn.isEnabled();
	    
		return sv;
	};

	public void restoreViewContents(ViewSaveObjects sv) {
		mGp.pictureViewFileName.setText(sv.picture_view_file_name);
		mGp.pictureViewFileInfo.setText(sv.picture_view_info);
		mGp.pictureViewZoomRatio.setText(sv.picture_view_zoom);
		mGp.customViewPager.setAdapter(mGp.adapterPictureView);
		mGp.customViewPager.setCurrentItem(sv.picture_view_pos_x);
		if (mGp.pictureScreenRotationLocked) mGp.pictureLockScreenRotationBtn.setImageResource(R.drawable.ic_128_screen_rotation_locked);
		else mGp.pictureLockScreenRotationBtn.setImageResource(R.drawable.ic_128_screen_rotation_unlocked);

		if (mGp.pictureZoomLocked) mGp.pictureLockZoomBtn.setImageResource(R.drawable.ic_128_zoom_lock);
		else mGp.pictureLockZoomBtn.setImageResource(R.drawable.ic_128_zoom_unlock);
		mGp.pictureShowMapBtn.setVisibility(sv.picture_map_button_visibility);
		
		if (sv.picture_view_reset_enabled) mGp.pictureResetBtn.setAlpha(1.0f);
		else mGp.pictureResetBtn.setAlpha(0.3f);
		mGp.pictureResetBtn.setEnabled(sv.picture_view_reset_enabled);
		
	};
	
	public void showPictureView(final int image_position) {
		mGp.currentView=CURRENT_VIEW_PICTURE;

		mActivity.setBackLightLevelToMax();
		mActivity.refreshOptionMenu();
//		Log.v("","button="+mGp.mPictureViewShowNavigateButton+", info="+mPictureViewShowFileInfo);
		
		if (mGp.settingPictureDisplayLastUiMode==UI_MODE_FULL_SCREEN) {
			mActivity.setUiFullScreen();
		} else if (mGp.settingPictureDisplayLastUiMode==UI_MODE_FULL_SCREEN_WITH_NAVI) {
			mActivity.setUiFullScreenWithNaviButton();
//		} else if (mGp.settingPictureDisplayLastUiMode==UI_MODE_FULL_SCREEN_WITH_SYSTEM_VIEW) {
//			mActivity.setUiFullScreenWithSystemView();
		}

		mGp.folderView.setVisibility(LinearLayout.GONE);
		mGp.thumbnailView.setVisibility(LinearLayout.GONE);
		mGp.pictureView.setVisibility(LinearLayout.VISIBLE);
//		FolderListItem fli=mGp.showedFolderList.get(mFolderListPosition);
		if (mGp.adapterPictureView!=null && mGp.adapterPictureView.getPictureWorkList().size()>0) {
			if (!mGp.adapterPictureView.getPictureWorkList().get(0).image_file_parent_directory.equals(mGp.currentFolderListItem.getParentDirectory())) {
				mGp.customViewPager.setAdapter(null);
				mGp.adapterPictureView.cleanup();
				mGp.adapterPictureView=new AdapterPictureList(mActivity, mGp.showedPictureList, mGp, image_position);
			}
		} else {
			mGp.adapterPictureView=new AdapterPictureList(mActivity, mGp.showedPictureList, mGp, image_position);
		}
		try {
			mGp.customViewPager=new CustomViewPager(mContext);
			mGp.customViewPager.setPageMargin(20);
			mGp.customViewPager.enableDefaultPageTransformer(true);
//			mGp.mCustomViewPager.setOffscreenPageLimit(3);
			setPictureViewPagerListener();
		} catch (Exception e) {
			e.printStackTrace();
		}
		mGp.customViewPagerView.removeAllViews();
		mGp.customViewPagerView.addView(mGp.customViewPager);
		mGp.customViewPager.setAdapter(mGp.adapterPictureView);
		mGp.customViewPager.setCurrentItem(image_position, false);
		mUiHandler.post(new Runnable(){
			@Override
			public void run() {
				setPictureViewResetButtonVisibility();
			}
		});
		mGp.customViewPager.setOverScrollMode(ViewPager.OVER_SCROLL_ALWAYS);
		CommonUtilities.setEdgeGlowColor(mGp.customViewPager);
		
		mGp.pictureLockScreenRotationBtn.setImageResource(R.drawable.ic_128_screen_rotation_unlocked);
		
		mGp.pictureLockZoomBtn.setImageResource(R.drawable.ic_128_zoom_unlock);
		mGp.pictureZoomLocked=false;
		
		mGp.mainProgressBar.setVisibility(ProgressBar.GONE);

        mGp.pictureDeleteBtn.setVisibility(ImageButton.VISIBLE);

		setPictureViewInformation(image_position);
		setPictureViewPrevNextButtonEnabled(mGp.customViewPager);
		setPictureViewResetButtonVisibility();
		
		mActivity.resetDeviceOrientation();
	};
	
	private void setPictureViewInformation(final int position) {
		if (mGp.adapterPictureView==null) return;
		if (mGp.adapterPictureView.getPictureWorkList().size()>0) {
			mActivity.setTitle(mGp.adapterPictureView.getPictureWorkList().get(position).image_file_name);
			mGp.pictureViewFileInfo.setText(mGp.adapterPictureView.getPictureWorkList().get(position).image_file_info);
			mGp.pictureViewFileName.setText(mGp.adapterPictureView.getPictureWorkList().get(position).image_file_path);
			mGp.pictureViewFileName.requestFocus();
			mGp.pictureViewZoomRatio.setText(String.format("%s of %s, %s%%", 
					position+1, mGp.adapterPictureView.getPictureWorkList().size(),
					(Math.round(mGp.adapterPictureView.getPictureWorkList().get(position).image_scale*100))));
		} else {
			mGp.pictureViewFileInfo.setText("");
		}
		if (mGp.mapApplicationAvailable) {
			if (mGp.adapterPictureView.getPictureWorkList().get(position).image_gps_longitude!=0D) {
				mGp.pictureShowMapBtn.setVisibility(ImageButton.VISIBLE);
			} else {
				mGp.pictureShowMapBtn.setVisibility(ImageButton.INVISIBLE);
			}
		} else {
			mGp.pictureShowMapBtn.setVisibility(ImageButton.INVISIBLE);
		}
		CustomImageView new_civ=mGp.adapterPictureView.getPictureWorkList().get(position).image_view;
		new_civ.setOnZoomChangedListener(new OnZoomChangedListener(){
			@Override
			public void onZoomChanged(float scale) {
				PictureWorkItem pi=mGp.adapterPictureView.getPictureWorkList().get(position);
				pi.image_scale=scale;
				setPictureViewResetButtonVisibility();
				mGp.pictureViewZoomRatio.setText(String.format("%s of %s, %s%%", 
						position+1, mGp.adapterPictureView.getPictureWorkList().size(), (Math.round(pi.image_scale*100))));
			}
		});

	};
	
	private void setPictureViewPagerListener() {
		mGp.customViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
			@Override
			public void onPageScrollStateChanged(int arg0) {
//				Log.v("","onPageScrollStateChanged 0="+arg0);
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
//				Log.v("","onPageScrolled 0="+arg0+", 1="+arg1+", 2="+arg2);
			}
			@Override
			public void onPageSelected(final int pos) {
				mUtil.addDebugMsg(2, "I", "onPageSelected pos="+pos);
				mUiHandler.post(new Runnable(){
					@Override
					public void run() {
						mGp.adapterPictureView.applyZoomLock(pos);
						setPictureViewInformation(pos);
						setPictureViewPrevNextButtonEnabled(mGp.customViewPager);
						setPictureViewResetButtonVisibility();
						if (mGp.pictureShowTestMode) {
							mUiHandler.postDelayed(new Runnable(){
								@Override
								public void run() {
									if (mGp.pictureShowTestDirctionNext) {
										if (mGp.pictureNextBtn.isEnabled()) mGp.pictureNextBtn.performClick();
										else {
											mGp.pictureShowTestDirctionNext=false;
											mGp.picturePrevBtn.performClick();
										}
									} else {
										if (mGp.picturePrevBtn.isEnabled()) mGp.picturePrevBtn.performClick();
										else {
											mGp.pictureShowTestDirctionNext=true;
											mGp.pictureNextBtn.performClick();
										}
									}
								}
							},500);
						}
					}
				});
			}
		});
	};
	
//	public void pictureSetCompleted(int position) {
//		if (mGp.mCustomViewPager.getCurrentItem()==position) setPictureResetButtonVisibility();
//	};
	
	private void setPictureViewResetButtonVisibility() {
		if (mGp.adapterPictureView==null) return;
		ArrayList<PictureWorkItem> pa=mGp.adapterPictureView.getPictureWorkList();
		boolean btn_visible=false;
		if (pa!=null && pa.size()>0) {
			for(int i=0;i<pa.size();i++) {
				PictureWorkItem pi=pa.get(i);
				if (pi!=null && pi.image_view!=null) {
					if (pi.image_rotation!=0 || pi.image_scale!=1.0f) {
						btn_visible=true;
						break;
					}
				}
			}
		}
		
		PictureWorkItem pi=pa.get(mGp.customViewPager.getCurrentItem());
//		Log.v("","scale="+pi.image_view.getScale()+", max="+pi.image_view.getMaxScale());
		if (pi.image_view.getMaxScale()<=pi.image_view.getScale()) {
//			mPictureZoomInBtn.setVisibility(ImageButton.INVISIBLE);
			mGp.pictureZoomInBtn.setAlpha(0.2f);
		} else {
//			mPictureZoomInBtn.setVisibility(ImageButton.VISIBLE);
			mGp.pictureZoomInBtn.setAlpha(1f);
		}
		if (pi.image_view.getScale()>PICTURE_VIEW_MIN_SCALE) {
//			mPictureZoomOutBtn.setVisibility(ImageButton.VISIBLE);
			mGp.pictureZoomOutBtn.setAlpha(1.0f);
		} else {
//			mPictureZoomOutBtn.setVisibility(ImageButton.INVISIBLE);
			mGp.pictureZoomOutBtn.setAlpha(0.2f);
		}

		if (btn_visible) {
			mGp.pictureResetBtn.setEnabled(true);
			mGp.pictureResetBtn.setAlpha(1.0f);
		} else {
			mGp.pictureResetBtn.setEnabled(false);
			mGp.pictureResetBtn.setAlpha(0.3f);
		}
	};
	
	private void setPictureViewListener() {
        mGp.picturePrevBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
				Toast toast = Toast.makeText(mContext, mContext.getString(R.string.msgs_main_oper_label_show_previous), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
                return true;
            }
        });
		mGp.picturePrevBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
                showPrevPicture();
			}
		});

        mGp.pictureNextBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast toast = Toast.makeText(mContext, mContext.getString(R.string.msgs_main_oper_label_show_next), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return true;
            }
        });
		mGp.pictureNextBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
                showNextPicture();
			}
		});

        mGp.pictureZoomInBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast toast = Toast.makeText(mContext, mContext.getString(R.string.msgs_main_oper_label_zoom_in), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return true;
            }
        });
		mGp.pictureZoomInBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (!mActivity.isUiEnabled()) return;
				final int pos=mGp.customViewPager.getCurrentItem();
				final CustomImageView iv=mGp.adapterPictureView.getPictureWorkList().get(pos).image_view;
//				float max_scale=iv.getMaxScale();
				float new_scale=iv.getScale()*2.0f;
				if (new_scale>PICTURE_VIEW_MAX_SCALE) new_scale=PICTURE_VIEW_MAX_SCALE;
				iv.zoomTo(new_scale, 1);
				if (mGp.pictureZoomLocked) {
					mUiHandler.post(new Runnable(){
						@Override
						public void run() {
							mGp.adapterPictureView.setZoomLock(iv.getDisplayMatrix(), iv.getScale());
						}
					});
				}
				setPictureViewResetButtonVisibility();
//				Toast toast = Toast.makeText(mContext, "Zoom="+new_scale+"%", Toast.LENGTH_SHORT);
//				toast.show();
			}
		});

        mGp.pictureZoomOutBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast toast = Toast.makeText(mContext, mContext.getString(R.string.msgs_main_oper_label_zoom_out), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return true;
            }
        });
		mGp.pictureZoomOutBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (!mActivity.isUiEnabled()) return;
				int pos=mGp.customViewPager.getCurrentItem();
				final CustomImageView iv=mGp.adapterPictureView.getPictureWorkList().get(pos).image_view;
				float new_scale=iv.getScale()/2.0f;
				if (new_scale<PICTURE_VIEW_MIN_SCALE) new_scale=PICTURE_VIEW_MIN_SCALE;
				iv.zoomTo(new_scale, 1);
				if (mGp.pictureZoomLocked) {
					mUiHandler.post(new Runnable(){
						@Override
						public void run() {
							mGp.adapterPictureView.setZoomLock(iv.getDisplayMatrix(), iv.getScale());
						}
					});
				}
				setPictureViewResetButtonVisibility();
//				Toast toast = Toast.makeText(mContext, "Zoom="+new_scale+"%", Toast.LENGTH_SHORT);
//				toast.show();
			}
		});

        mGp.pictureRotatePictureLeftBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast toast = Toast.makeText(mContext, mContext.getString(R.string.msgs_main_oper_label_rotate_left), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return true;
            }
        });
		mGp.pictureRotatePictureRightBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (!mActivity.isUiEnabled()) return;
				rotatePicture(true);
			}
		});

        mGp.pictureRotatePictureRightBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast toast = Toast.makeText(mContext, mContext.getString(R.string.msgs_main_oper_label_rotate_right), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return true;
            }
        });
		mGp.pictureRotatePictureLeftBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (!mActivity.isUiEnabled()) return;
				rotatePicture(false);
			}
		});

        mGp.pictureLockZoomBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast toast = Toast.makeText(mContext, mContext.getString(R.string.msgs_main_oper_label_lock_zoom_and_pos), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return true;
            }
        });
		mGp.pictureLockZoomBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (!mActivity.isUiEnabled()) return;
				if (mGp.pictureZoomLocked) {
					mGp.pictureZoomLocked=false;
					mGp.pictureLockZoomBtn.setImageResource(R.drawable.ic_128_zoom_unlock);
					mGp.adapterPictureView.unlockZoom();
//					Toast toast = Toast.makeText(mContext, 
//							mContext.getString(R.string.msgs_main_picture_zoom_unlocked), Toast.LENGTH_SHORT);
//					toast.show();
				} else {
					mGp.pictureZoomLocked=true;
					mGp.pictureLockZoomBtn.setImageResource(R.drawable.ic_128_zoom_lock);
					int pos=mGp.customViewPager.getCurrentItem();
					final CustomImageView iv=mGp.adapterPictureView.getPictureWorkList().get(pos).image_view;
					mGp.adapterPictureView.setZoomLock(iv.getDisplayMatrix(), iv.getScale());
//					Toast toast = Toast.makeText(mContext, 
//							mContext.getString(R.string.msgs_main_picture_zoom_locked), Toast.LENGTH_SHORT);
//					toast.show();
				}
			}
		});

        mGp.pictureLockScreenRotationBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast toast = Toast.makeText(mContext, mContext.getString(R.string.msgs_main_oper_label_lock_phone_orientation), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return true;
            }
        });
		mGp.pictureLockScreenRotationBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (!mActivity.isUiEnabled()) return;
				if (mGp.pictureScreenRotationLocked) {
					mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
					mGp.pictureScreenRotationLocked=false;
					mGp.pictureLockScreenRotationBtn.setImageResource(R.drawable.ic_128_screen_rotation_unlocked);
				} else {
					int rotation=mActivity.getWindowManager().getDefaultDisplay().getRotation();
					if (rotation== Surface.ROTATION_0) mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					else if (rotation== Surface.ROTATION_90) mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
					else if (rotation== Surface.ROTATION_180) mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
					else if (rotation== Surface.ROTATION_270) mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
				    mGp.pictureScreenRotationLocked=true;
				    mGp.pictureLockScreenRotationBtn.setImageResource(R.drawable.ic_128_screen_rotation_locked);
				}
			}
		});

        mGp.pictureResetBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast toast = Toast.makeText(mContext, mContext.getString(R.string.msgs_main_oper_label_revert), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return true;
            }
        });
		mGp.pictureResetBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (!mActivity.isUiEnabled()) return;
				final Dialog dialog=mActivity.showProgressSpinIndicator();
				Thread th=new Thread(){
					@Override
					public void run() {
						ArrayList<PictureWorkItem> pal=mGp.adapterPictureView.getPictureWorkList();
						for(final PictureWorkItem pi:pal) {
							if (pi!=null) {
								final CustomImageView civ=pi.image_view;
								if (civ!=null) {
									if (pi.image_rotation!=0.0f || civ.getScale()!=1.0f) {
										Bitmap w_bm=null;
										if (pi.image_rotation!=0.0f && civ.getBitMap()!=null) {
											w_bm=PictureUtil.rotateBitmap(civ.getBitMap(), pi.image_rotation*-1.0f);						
										}
										final Bitmap r_bm=w_bm;
										mUiHandler.post(new Runnable(){
											@Override
											public void run() {
												if (r_bm!=null) {
													civ.setImageBitmap(r_bm, civ.getDisplayMatrix(), civ.getMinScale(), civ.getMaxScale());						
												}
												pi.image_rotation=0.0f;
												civ.zoomTo(1.0f, 1);
											}
										});
									}
								}
							}
						}
						mUiHandler.postDelayed(new Runnable(){
							@Override
							public void run() {
								dialog.dismiss();
								Toast toast = Toast.makeText(mContext,
										mContext.getString(R.string.msgs_main_picture_reset_zoom_and_rotation), Toast.LENGTH_SHORT);
								toast.show();
								mGp.adapterPictureView.notifyDataSetChanged();
								setPictureViewResetButtonVisibility();
							}
						}, 300);
					}
				};
				th.start();
			}
		});

        mGp.pictureShareBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast toast = Toast.makeText(mContext, mContext.getString(R.string.msgs_main_oper_label_share_picture), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return true;
            }
        });
		mGp.pictureShareBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (!mActivity.isUiEnabled()) return;
				int pos=mGp.customViewPager.getCurrentItem();
				String[] send_pic_fp=new String[]{mGp.adapterPictureView.getPictureWorkList().get(pos).image_file_path};
                String emsg=CommonUtilities.sharePictures(mContext, send_pic_fp);
                if (emsg!=null) mCommonDlg.showCommonDialog(false, "E", "Action_Send error", emsg, null);
			}
		});

        mGp.pictureWallpaperBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast toast = Toast.makeText(mContext, mContext.getString(R.string.msgs_main_oper_label_wall_paper), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return true;
            }
        });
		mGp.pictureWallpaperBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (!mActivity.isUiEnabled()) return;
				int pos=mGp.customViewPager.getCurrentItem();
				PictureUtil.invokeWallPaperEditor(mContext, mGp.adapterPictureView.getPictureWorkList().get(pos).image_file_path);
			}
		});

        mGp.pictureShowMapBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast toast = Toast.makeText(mContext, mContext.getString(R.string.msgs_main_oper_label_show_maps), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return true;
            }
        });
		mGp.pictureShowMapBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (!mActivity.isUiEnabled()) return;
				final int pos=mGp.customViewPager.getCurrentItem();
				PictureWorkItem pa=mGp.adapterPictureView.getPictureWorkList().get(pos);
				String geo=String.valueOf(pa.image_gps_latitude)+","+String.valueOf(pa.image_gps_longitude);
				String geo_uri="geo:"+geo+"?q=loc:"+geo;
//				Uri.parse("geo:0,0?q=loc:"+geo+"?z=19");
				Uri gmmIntentUri= Uri.parse(geo_uri);
				Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
				mapIntent.setPackage("com.google.android.apps.maps");
				if (mapIntent.resolveActivity(mActivity.getPackageManager()) != null) {
					mActivity.startActivity(mapIntent);
				    mUtil.addDebugMsg(1, "I", "Invoke Map activity parm="+geo_uri);
				} else {
			        final Toast toast= Toast.makeText(mContext, mContext.getString(R.string.msgs_main_map_activity_not_found),
							Toast.LENGTH_SHORT);
			        toast.show();;
			        mGp.pictureShowMapBtn.setVisibility(ImageButton.INVISIBLE);
				}
			}
		});

        mGp.pictureLeftBtn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                if (!mActivity.isUiEnabled()) return;
                mUtil.addDebugMsg(1, "I", "Left button clicked");
                showPrevPicture();
            }
        });

        mGp.pictureRightBtn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                if (!mActivity.isUiEnabled()) return;
                mUtil.addDebugMsg(1, "I", "Right button clicked");
                showNextPicture();
            }
        });

        mGp.pictureDeleteBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast toast = Toast.makeText(mContext, mContext.getString(R.string.msgs_main_oper_label_delete_picture), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return true;
            }
        });
		mGp.pictureDeleteBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (!mActivity.isUiEnabled()) return;
				final int pos=mGp.customViewPager.getCurrentItem();
				NotifyEvent ntfy=new NotifyEvent(mContext);
				ntfy.setListener(new NotifyEventListener(){
					@Override
					public void positiveResponse(Context c, Object[] o) {
						final Dialog dialog=mActivity.showProgressSpinIndicator();
						Thread th=new Thread(){
							@Override
							public void run() {
								final int c_num=mGp.adapterPictureView.getPictureWorkList().size();
								final PictureListItem del_pic_item=mGp.adapterPictureView.getPictureWorkList().get(pos).pictureItem;
                                SafFile3 sf=new SafFile3(mContext, del_pic_item.getParentDirectory()+"/"+del_pic_item.getFileName());
								boolean rc_delete=sf.deleteIfExists();
								PictureUtil.removeBitmapCacheFile(mGp, del_pic_item.getParentDirectory()+"/"+del_pic_item.getFileName());
								if (!rc_delete) {
									mCommonDlg.showCommonDialog(false, "I", 
											String.format(mContext.getString(R.string.msgs_main_file_delete_file_was_failed),del_pic_item.getFileName()),
											"", null);
									mUiHandler.post(new Runnable(){
										@Override
										public void run(){
											dialog.dismiss();
										}
									});
									return;
								}
								
//								SystemClock.sleep(200);
								
								mUiHandler.post(new Runnable(){
									@Override
									public void run(){
										mGp.adapterPictureView.remove(mGp.customViewPager, pos);
										mGp.currentPictureList.remove(del_pic_item);
										if (c_num>1) {
											int n_pos=pos;
											if ((pos+1)==c_num) n_pos=pos-1;
											setPictureViewInformation(n_pos);
											setPictureViewPrevNextButtonEnabled(mGp.customViewPager);
											setPictureViewResetButtonVisibility();
										}
										if (!mGp.showSinglePicture) {
											mGp.adapterThumbnailView.getPictureList().remove(del_pic_item);
//											mGp.mSelectedPictureList.remove(del_pic_item);
											if (mGp.adapterThumbnailView.getCount()>0) 
												mGp.currentFolderListItem.setThumbnailArray(mGp.adapterThumbnailView.getItem(0).getThumbnailImageByte());
											else {
												mGp.currentFolderListItem.setThumbnailArray(null);
											}
											if (mGp.adapterThumbnailView.getCount()>0) {
												mGp.adapterThumbnailView.setAllItemsSelected(false);
												mGp.adapterThumbnailView.setSelectMode(false);
												mGp.adapterThumbnailView.notifyDataSetChanged();
												mGp.adapterFolderView.notifyDataSetChanged();
												
												mActivity.putPictureList(mGp.showedPictureList, mGp.currentFolderListItem);
												
												PictureListItem pli=mGp.adapterThumbnailView.getPictureList().get(0);
												if (!mGp.currentFolderListItem.getThumbnailFilePath().equals(pli.getParentDirectory()+"/"+pli.getFileName())) {
													mGp.currentFolderListItem.setThumbnailArray(pli.getThumbnailImageByte());
													mGp.currentFolderListItem.setThumbnailFilePath(pli.getParentDirectory()+"/"+pli.getFileName());
													mGp.adapterFolderView.notifyDataSetChanged();
												}
												mGp.currentFolderListItem.setNoOfPictures(mGp.currentPictureList.size());
												
												mGp.adapterFolderView.notifyDataSetChanged();
												mActivity.saveFolderList(mGp.masterFolderList);
												
//										   		String sel_month=(String)mGp.pictureSelectSpinner.getSelectedItem();
//										   		createSelectedTumbnailPictureList(sel_month);

											} else {
												mActivity.removePictureList(mGp.currentFolderListItem);
												mGp.masterFolderList.remove(mGp.currentFolderListItem);
												mActivity.saveFolderList(mGp.masterFolderList);
												mActivity.showFolderView();
											}
										} else {
											if (mGp.adapterPictureView.getCount()==0) {
												mActivity.handleCloseButtonPressed();
											}
										}
										dialog.dismiss();
									}
								});
							}
						};
						th.setPriority(Thread.MIN_PRIORITY);
						th.start();
					}
					@Override
					public void negativeResponse(Context c, Object[] o) {
					}
				});
				
				mCommonDlg.showCommonDialog(true, "W", 
						mContext.getString(R.string.msgs_main_file_delete_confirm_delete), 
						mGp.adapterPictureView.getPictureWorkList().get(pos).image_file_path, ntfy);
			}
		});
	};

    private void showPrevPicture() {
        if (!mActivity.isUiEnabled()) return;
        if (mGp.customViewPager.getAdapter()==null) return;
        if (mGp.customViewPager.getCurrentItem()>0) {
            mGp.picturePrevBtn.setEnabled(false);
            mGp.customViewPager.setUseFastScroll(true);
            mGp.customViewPager.setCurrentItem(mGp.customViewPager.getCurrentItem()-1, true);
            mUiHandler.postDelayed(new Runnable(){
                @Override
                public void run() {
                    mGp.customViewPager.setUseFastScroll(false);
                }
            },100);
        }
//		setPrevNextButtonEnabled(mGp.mCustomViewPager);

    }

    private void showNextPicture() {
        if (!mActivity.isUiEnabled()) return;
        if (mGp.customViewPager.getAdapter()==null) return;
        if (mGp.customViewPager.getCurrentItem()<mGp.customViewPager.getAdapter().getCount()) {
            mGp.pictureNextBtn.setEnabled(false);
            mGp.customViewPager.setUseFastScroll(true);
            mGp.customViewPager.setCurrentItem(mGp.customViewPager.getCurrentItem()+1, true);
            mUiHandler.postDelayed(new Runnable(){
                @Override
                public void run() {
                    mGp.customViewPager.setUseFastScroll(false);
                }
            },100);
        }
//		setPrevNextButtonEnabled(mGp.mCustomViewPager);
    }

    private void rotatePicture(final boolean rotate_clockwise) {
		final int pos=mGp.customViewPager.getCurrentItem();
		final PictureWorkItem pw=mGp.adapterPictureView.getPictureWorkList().get(pos);
		final CustomImageView civ=pw.image_view;
		
//		mGp.mPictureView.setAlpha(0.2f);
//		setUiDisabled();
		final Dialog dialog=mActivity.showProgressSpinIndicator();
		Thread th=new Thread(){
			@Override
			public void run() {
				SystemClock.sleep(100);
				float crotate=mGp.adapterPictureView.getPictureWorkList().get(pos).image_rotation;
				float n_rotate=0.0f;
				if (rotate_clockwise) {//rotate clockwise
					if (crotate==270.0f) n_rotate=0.0f;
					else n_rotate=crotate+90.0f;
				} else {//rotate counter-clockwise
					if (crotate==-270.0f) n_rotate=0.0f;
					else n_rotate=crotate-90.0f;
				}
				final float w_rotate=n_rotate;
				Bitmap w_bm=null;
				if (rotate_clockwise) {//rotate clockwise
					w_bm=PictureUtil.rotateBitmap(civ.getBitMap(), 90.0f);
				} else {//rotate counter-clockwise
					w_bm=PictureUtil.rotateBitmap(civ.getBitMap(), -90.0f);
				}
				final Bitmap bm=w_bm;
				mUiHandler.post(new Runnable(){
					@Override
					public void run() {
						civ.setImageBitmap(null);
						civ.setImageBitmap(bm, civ.getDisplayMatrix(), civ.getMinScale(), civ.getMaxScale());
						mGp.adapterPictureView.getPictureWorkList().get(pos).image_rotation=w_rotate;
						setPictureViewResetButtonVisibility();
//						mGp.mPictureView.setAlpha(1.0f);

						mUiHandler.postDelayed(new Runnable(){
							@Override
							public void run() {
								dialog.dismiss();
							}
						},300);

					}
				});
			}
		};
		th.setPriority(Thread.MIN_PRIORITY);
		th.start();
	};
	
	private void setPictureViewPrevNextButtonEnabled(final CustomViewPager cvp) {
		if (cvp.getAdapter()==null) return;
		Handler hndl=new Handler();
		hndl.post(new Runnable(){
			@Override
			public void run() {
				if (cvp.getAdapter()==null) return;
				int image_count=cvp.getAdapter().getCount();
				if (cvp.getCurrentItem()>=(image_count-1)) {
					mGp.pictureNextBtn.setEnabled(false);
					mGp.pictureNextBtn.setImageResource(R.drawable.next_file_disabled);
				} else {
					mGp.pictureNextBtn.setEnabled(true);
					mGp.pictureNextBtn.setImageResource(R.drawable.next_file_enabled);
				}
				if (cvp.getCurrentItem()<=0) {
					mGp.picturePrevBtn.setEnabled(false);
					mGp.picturePrevBtn.setImageResource(R.drawable.prev_file_disabled);
				} else {
					mGp.picturePrevBtn.setEnabled(true);
					mGp.picturePrevBtn.setImageResource(R.drawable.prev_file_enabled);
				}
			}
		});
	};

	
}
