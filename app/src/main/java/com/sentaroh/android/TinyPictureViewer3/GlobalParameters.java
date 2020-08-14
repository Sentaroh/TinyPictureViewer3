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

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.sentaroh.android.TinyPictureViewer3.Log.LogUtil;
import com.sentaroh.android.Utilities3.SafManager3;
import com.sentaroh.android.Utilities3.ThemeColorList;
import com.sentaroh.android.Utilities3.Widget.NonWordwrapTextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.LoggerWriter;

import java.io.File;
import java.util.ArrayList;

import static com.sentaroh.android.TinyPictureViewer3.Constants.*;

public class GlobalParameters  {
    private static Logger log= LoggerFactory.getLogger(GlobalParameters.class);

	public boolean debugEnabled=true;
	public boolean activityIsDestroyed=false;
	public boolean activityIsBackground=false;
//	public int applicationTheme=R.style.Theme_AppCompat_NoActionBar;
	public ThemeColorList themeColorList=null;
	public boolean themeIsLight=false;

//	public ISvcCallback callbackStub=null;

	public Context appContext=null;

	public CommonUtilities cUtil=null;

	public boolean debuggable=false;

	public boolean externalStorageIsMounted=false;
	public boolean externalStorageAccessIsPermitted=false;
	
	final static public String STORAGE_STATUS_UNMOUNT="/unknown";
	public String internalRootDirectory=STORAGE_STATUS_UNMOUNT;
	public String externalRootDirectory=STORAGE_STATUS_UNMOUNT;
	public String appSpecificDirectory="/Android/data/com.sentaroh.android."+APPLICATION_TAG+"/files";
	public String applicationRootDirectory="/";
	public String applicationCacheDirectory="/";
	
	public String folderListFilePath="", pictureFileCacheDirectory="", pictureBitmapCacheDirectory="";

	public SafManager3 safMgr=null;

	public ArrayList<FolderListItem> showedFolderList=new ArrayList<FolderListItem>();
	public ArrayList<FolderListItem> masterFolderList=null;
	public ArrayList<PictureUtil.PictureFileCacheItem>pictureFileCacheList=new ArrayList<PictureUtil.PictureFileCacheItem>();

	public FolderListItem currentFolderListItem=null;
	public boolean showSinglePicture=false;

	public int uiMode=UI_MODE_ACTION_BAR;

	public int currentView=CURRENT_VIEW_FOLDER;

	public ArrayList<PictureListItem> currentPictureList=null;

	public ArrayList<PictureListItem>showedPictureList=new ArrayList<PictureListItem>();
	
	public AdapterPictureList adapterPictureView=null;
	
	public ArrayList<String> copyCutList=new ArrayList<String>();
	public boolean isCutMode=false;

	public boolean pictureZoomLocked=false;
	public boolean pictureScreenRotationLocked=false;
	
	public boolean mapApplicationAvailable=false;

	public boolean pictureShowTestDirctionNext=true;
	public boolean pictureShowTestMode=false;

    public Toolbar mainToolBar=null;

	public LinearLayout folderView=null;
	public LinearLayout thumbnailView=null;
	public AdapterFolderList adapterFolderView=null;
	public GridView folderGridView=null;
	public AdapterThumbnailList adapterThumbnailView=null;
	public GridView thumbnailGridView=null;
	public TextView thumbnailEmptyView=null;
	
	public LinearLayout mainProgressView=null;
	public ProgressBar mainProgressBar=null;
	
	public RelativeLayout pictureView=null;
	public LinearLayout pictureViewTopControl=null;
	public LinearLayout pictureViewBottomControl=null;
	public LinearLayout customViewPagerView=null;
	public CustomViewPager customViewPager=null;
	public NonWordwrapTextView pictureViewFileInfo=null;
	public TextView pictureViewFileName=null;
	public TextView pictureViewZoomRatio=null;
	public ImageButton picturePrevBtn=null;
	public ImageButton pictureNextBtn=null;
	public ImageButton pictureZoomOutBtn=null;
	public ImageButton pictureZoomInBtn=null;
	public ImageButton pictureLockScreenRotationBtn=null;
	public ImageButton pictureShowMapBtn=null;
	public ImageButton pictureRotatePictureRightBtn=null;
	public ImageButton pictureRotatePictureLeftBtn=null;
	public ImageButton pictureLockZoomBtn=null;
	public ImageButton pictureResetBtn=null;
	public ImageButton pictureShareBtn=null;
	public ImageButton pictureWallpaperBtn=null;
	public ImageButton pictureDeleteBtn=null;

    public Button pictureLeftBtn=null;
    public Button pictureRightBtn=null;

	public ImageButton contextButtonFolderExclude=null,
			contextButtonFolderRename=null, contextButtonFolderAdd=null,
			contextButtonFolderDelete=null, contextButtonFolderSelectAll=null, 
			contextButtonFolderUnselectAll=null;
	public LinearLayout contextButtonFolderExcludeView=null,
			contextButtonFolderRenameView=null, contextButtonFolderAddView=null,
			contextButtonFolderDeleteView=null, contextButtonFolderSelectAllView=null, 
			contextButtonFolderUnselectAllView=null;

	public ImageButton contextButtonThumbnailShare=null,
			contextButtonThumbnailRename=null, 
			contextButtonThumbnailPaste=null, contextButtonThumbnailCopy=null, contextButtonThumbnailCut=null,
			contextButtonThumbnailDelete=null, contextButtonThumbnailSelectAll=null, 
			contextButtonThumbnailUnselectAll=null;
	public LinearLayout contextButtonThumbnailShareView=null,
			contextButtonThumbnailRenameView=null,
			contextButtonThumbnailPasteView=null, contextButtonThumbnailCopyView=null, contextButtonThumbnailCutView=null,
			contextButtonThumbnailDeleteView=null, contextButtonThumbnailSelectAllView=null, 
			contextButtonThumbnailUnselectAllView=null;

    public LinearLayout contextClipBoardView=null;
	public ImageView contextClipBoardIcon=null;
    public TextView contextClipBoardText=null;
    public Button contextClipBoardClear=null;

	public Spinner spinnerPictureSelector=null;
	public CustomActionBarSpinnerAdapter adapterPictureSelectorSpinner=null;
	public int selectPictureDateSpinnerPosition=0;
	
	public Spinner spinnerFolderSelector=null;
	public CustomActionBarSpinnerAdapter adapterFolderSelectorSpinner=null;
	public int selectFolderSpinnerPosition=0;
	
	public OnItemSelectedListener folderSelectorListener=null;
	public OnItemSelectedListener pictureSelectorListener=null;


//	Settings parameter	    	
	public boolean settingExitClean=false;

	public boolean settingMaxBrightWhenImageShowed=true;
//	public boolean settingPictureScreenWithoutNavigateButton=false;

    public static final String SHOW_SIMPLE_FOLDER_VIEW_KEY="show_simple_folder_view_key";
    public boolean settingShowSimpleFolderView=true;
	
	public String settingAutoFileChangeDetection=AUTO_FILE_CHANGE_DETECTION_ALWAYS;
	
	public boolean settingCameraFolderAlwayTop=true;

	public boolean settingSupressAddExternalStorageNotification =false;
    public boolean isSupressNotificationAddExternalStorage() {
        return settingSupressAddExternalStorageNotification;
    }
    public void setSupressNotificationAddExternalStorage(boolean suppress) {
        settingSupressAddExternalStorageNotification =suppress;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(appContext);
        prefs.edit().putBoolean(appContext.getString(R.string.settings_suppress_add_external_storage_notification), suppress).commit();
    }

	public int settingFolderSelectionCharacterCount=4;

	public int 		settingPictureDisplayDefualtUiMode=UI_MODE_FULL_SCREEN_WITH_NAVI;
	public boolean 	settingPictureDisplayOptionRestoreWhenStartup=false;
	public int 		settingPictureDisplayLastUiMode=UI_MODE_FULL_SCREEN_WITH_NAVI;
//	public boolean settingPictureDisplayOptionShowNavigationButton=true;
//	public boolean settingPictureDisplayOptionShowPictureInfo=true;
	
	public boolean settingScanHiddenFile=false;
	public ArrayList<ScanFolderItem> settingScanDirectoryList=null;
	public String[] settingScanFileType=new String[]{"jpg","jpeg","png"};
	
	public int folderListSortKey=0;
	public int folderListSortOrder=0;

	public int thumbnailListSortKey=SORT_KEY_THUMBNAIL_PICTURE_TIME;
	public int thumbnailListSortOrder=SORT_ORDER_DESCENDANT;

	public int settingImageQuality=30;
	public int settingImagesizeMaxWidth=512;

	public Handler uiHandler=null;


	public GlobalParameters() {
//		Log.v("","constructed");
	};
	
//	@SuppressLint("Wakelock")
//	@Override
//	public void onCreate() {
////		Log.v("","onCreate dir="+getFilesDir().toString());
//		appContext=this.getApplicationContext();
//		uiHandler=new Handler();
//		debuggable=isDebuggable();
//
//		internalRootDirectory=Environment.getExternalStorageDirectory().toString();
//
//		applicationRootDirectory=getFilesDir().toString();
//		applicationCacheDirectory=getCacheDir().toString();
//
//	    folderListFilePath=internalRootDirectory+appSpecificDirectory+"/folder_list_cache";
//	    pictureFileCacheDirectory=internalRootDirectory+appSpecificDirectory+"/pic_cache/";
//	    pictureBitmapCacheDirectory=internalRootDirectory+appSpecificDirectory+"/bitmap_cache/";
//	    File lf=new File(pictureFileCacheDirectory);
//	    if (!lf.exists()) lf.mkdirs();
//	    lf=new File(pictureBitmapCacheDirectory);
//	    if (!lf.exists()) lf.mkdirs();
//
//		initStorageStatus(this);
//
//		initSettingsParms(this);
//		loadSettingsParms(this);
//		setLogParms(this);
//		loadFolderSortParm(this);
//	};
//
    public void initGlobalParameter(Context c) {
//		Log.v("","onCreate dir="+getFilesDir().toString());
        appContext=c;
        uiHandler=new Handler();
        debuggable=isDebuggable();

        internalRootDirectory= Environment.getExternalStorageDirectory().toString();

        applicationRootDirectory=c.getFilesDir().toString();
        applicationCacheDirectory=c.getCacheDir().toString();

        folderListFilePath=internalRootDirectory+appSpecificDirectory+"/folder_list_cache";
        pictureFileCacheDirectory=internalRootDirectory+appSpecificDirectory+"/pic_cache/";
        pictureBitmapCacheDirectory=internalRootDirectory+appSpecificDirectory+"/bitmap_cache/";
        createCacheDiretory();

        initStorageStatus(c);

        LogUtil lu=new LogUtil(c, "Slf4j");
        Slf4jLogWriter lw=new Slf4jLogWriter(lu);
        log.setWriter(lw);

        initSettingsParms(c);
        loadSettingsParms(c);
        loadFolderSortParm(c);
    };

    public void createCacheDiretory()  {
        File lf=new File(pictureFileCacheDirectory);
        if (!lf.exists()) lf.mkdirs();
        lf=new File(pictureBitmapCacheDirectory);
        if (!lf.exists()) lf.mkdirs();
    }

    public void clearParms() {
		showedFolderList=new ArrayList<FolderListItem>();
		masterFolderList=null;
		pictureFileCacheList=new ArrayList<PictureUtil.PictureFileCacheItem>();

		currentFolderListItem=null;
		showSinglePicture=false;

		uiMode=UI_MODE_ACTION_BAR;
		currentView=CURRENT_VIEW_FOLDER;
		currentPictureList=null;
		showedPictureList=new ArrayList<PictureListItem>();
		adapterPictureView=null;
		copyCutList=new ArrayList<String>();
		isCutMode=false;

	};
	
	@SuppressLint("NewApi")
	public void initStorageStatus(Context c) {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
    		externalStorageIsMounted=false;
    	} else  {  
    		externalStorageIsMounted=true;
    	}
		
		if (Build.VERSION.SDK_INT>=23) {
			externalStorageAccessIsPermitted=
					(c.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED);
		} else {
			externalStorageAccessIsPermitted=true;
		}
		
		refreshMediaDir(c);
	};
	
	public void refreshMediaDir(Context c) {
//		File[] fl=ContextCompat.getExternalFilesDirs(c, null);
        File[] fl=c.getExternalFilesDirs(null);
		if (fl!=null) {
			for(File item:fl) {
				if (item!=null && !item.getAbsolutePath().startsWith(internalRootDirectory)) {
					externalRootDirectory=item.getAbsolutePath().substring(0,item.getAbsolutePath().indexOf("/Android"));
					break;
				}
			}
		}
		if (safMgr==null) {
			safMgr=new SafManager3(c);
		}
	};
	

//	private int mTextColorForeground=0;
//	private int mTextColorBackground=0;
//	public void initTextColor(Context c) {
//    	TypedValue outValue = new TypedValue();
//    	c.getTheme().resolveAttribute(android.R.attr.textColorPrimary, outValue, true);
//    	mTextColorForeground=c.getResources().getColor(outValue.resourceId);
//    	c.getTheme().resolveAttribute(android.R.attr.colorBackground, outValue, true);
//    	mTextColorBackground=c.getResources().getColor(outValue.resourceId);
//    	Log.v("","f="+String.format("0x%08x", mTextColorForeground));
//    	Log.v("","b="+String.format("0x%08x", mTextColorBackground));
//	};

	public void initSettingsParms(Context c) {
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
		
		if (!prefs.contains(c.getString(R.string.settings_max_screen_brightness_when_image_showed))) {
            prefs.edit().putBoolean(c.getString(R.string.settings_max_screen_brightness_when_image_showed), true).commit();
		}
        if (!prefs.contains(c.getString(R.string.settings_picture_display_default_ui_mode))) {
            prefs.edit().putString(c.getString(R.string.settings_picture_display_default_ui_mode), String.valueOf(UI_MODE_FULL_SCREEN_WITH_NAVI)).commit();
        }

        if (!prefs.contains(c.getString(R.string.settings_folder_filter_character_count))) {
			prefs.edit().putString(c.getString(R.string.settings_folder_filter_character_count), "4").commit();
		}

		if (!prefs.contains(c.getString(R.string.settings_file_changed_auto_detect))) {
			prefs.edit().putString(c.getString(R.string.settings_file_changed_auto_detect), 
					AUTO_FILE_CHANGE_DETECTION_MEDIA_STORE_CHANGED).commit();
		}

		if (!prefs.contains(c.getString(R.string.settings_camera_folder_always_top))) {
			prefs.edit().putBoolean(c.getString(R.string.settings_camera_folder_always_top), true).commit();
		}
        if (!prefs.contains(c.getString(R.string.settings_suppress_add_external_storage_notification))) {
            prefs.edit().putBoolean(c.getString(R.string.settings_suppress_add_external_storage_notification), false).commit();
        }

	};

	public static final String DISPLAY_OPTION_LAST_UIMODE="settings_picture_display_last_ui_mode";
	public void saveSettingPictureDisplayUiMode(Context c) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
		prefs.edit().putInt(DISPLAY_OPTION_LAST_UIMODE, uiMode).commit();
		settingPictureDisplayLastUiMode=uiMode;
	};

	public void saveSettingOptionHiddenFile(Context c, boolean enable_hidden_file) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
		prefs.edit().putBoolean(c.getString(R.string.settings_process_hidden_files), enable_hidden_file).commit(); 
	};

    public void saveSettingOptionShowSimpleFolderView(Context c, boolean show_simple_folder_view) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        prefs.edit().putBoolean(SHOW_SIMPLE_FOLDER_VIEW_KEY, show_simple_folder_view).commit();
        settingShowSimpleFolderView=show_simple_folder_view;
    };

    public void loadSettingsParms(Context c) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);

		settingMaxBrightWhenImageShowed=prefs.getBoolean(c.getString(R.string.settings_max_screen_brightness_when_image_showed),true);
		settingPictureDisplayDefualtUiMode=
				Integer.parseInt(prefs.getString(c.getString(R.string.settings_picture_display_default_ui_mode), String.valueOf(UI_MODE_FULL_SCREEN_WITH_NAVI)));
		settingPictureDisplayLastUiMode=prefs.getInt(DISPLAY_OPTION_LAST_UIMODE, UI_MODE_FULL_SCREEN_WITH_NAVI);
		
		settingScanHiddenFile=prefs.getBoolean(c.getString(R.string.settings_process_hidden_files),false);
		
		settingFolderSelectionCharacterCount=
				Integer.parseInt(prefs.getString(c.getString(R.string.settings_folder_filter_character_count), "4"));
		
		settingAutoFileChangeDetection=prefs.getString(c.getString(R.string.settings_file_changed_auto_detect), 
																		AUTO_FILE_CHANGE_DETECTION_MEDIA_STORE_CHANGED);
		
		settingCameraFolderAlwayTop=prefs.getBoolean(c.getString(R.string.settings_camera_folder_always_top), false);

		settingPictureDisplayOptionRestoreWhenStartup=
				prefs.getBoolean(c.getString(R.string.settings_picture_display_option_restore_when_startup), false);
//		Log.v("","gp init pi="+settingPictureDisplayOptionShowPictureInfo);

        settingShowSimpleFolderView=prefs.getBoolean(SHOW_SIMPLE_FOLDER_VIEW_KEY, true);

        settingSupressAddExternalStorageNotification =
                prefs.getBoolean(c.getString(R.string.settings_suppress_add_external_storage_notification), false);

        loadScanFolderList(c);
	};

	public final static String FOLDER_LIST_SORT_KEY="settings_folder_list_sort_key";
	public final static String FOLDER_LIST_SORT_ORDER="settings_folder_list_sort_order";
	public void saveFolderSortParm(Context c) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
		prefs.edit().putInt(FOLDER_LIST_SORT_KEY, folderListSortKey).commit(); 
		prefs.edit().putInt(FOLDER_LIST_SORT_ORDER, folderListSortOrder).commit();
	};
	public void loadFolderSortParm(Context c) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
		folderListSortKey=prefs.getInt(FOLDER_LIST_SORT_KEY, 0); 
		folderListSortOrder=prefs.getInt(FOLDER_LIST_SORT_ORDER, 0);
	};

	public void saveScanFolderList(Context c) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
		String scan_list_value="", sep="";
		for(ScanFolderItem sfi:settingScanDirectoryList) {
			String sub=sfi.process_sub_directories?"1":"0";
			String inc=sfi.include?"1":"0";
			scan_list_value+=sep+(sfi.folder_path+"\t"+sub+"\t"+inc);
			sep="\n";
		}
		prefs.edit().putString(SCAN_FOLDER_LIST_KEY, scan_list_value).commit();
//		Log.v("","saved sv="+scan_list_value);
	};

	final static private String OLD_INCLUDE_DIR_KEY="settings_scan_directory_list";
	final static private String OLD_EXCLUDE_DIR_KEY="settings_scan_exclude_directory_list";
	final static public String SCAN_FOLDER_LIST_KEY="settings_scan_list";
	
	public void loadScanFolderList(Context c) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
		String[] settingSelectPictureDirectory=null;
		String[] settingExcludePictureDirectory=null;
		ArrayList<ScanFolderItem>scan_list=new ArrayList<ScanFolderItem>();

        String scan_list_value=prefs.getString(SCAN_FOLDER_LIST_KEY, "default");
        if (scan_list_value.equals("default")) {
            ScanFolderItem sfi=new ScanFolderItem();
            sfi.folder_path="/storage/emulated/0/DCIM";
            sfi.process_sub_directories=true;
            sfi.include=true;
            scan_list.add(sfi);

            sfi=new ScanFolderItem();
            sfi.folder_path="/storage/emulated/0/Pictures";
            sfi.process_sub_directories=true;
            sfi.include=true;
            scan_list.add(sfi);
        } else {
            if (!scan_list_value.equals("")) {
                String[] items=scan_list_value.split("\n");
                for(int i=0;i<items.length;i++) {
                    String[] list=items[i].split("\t");
                    ScanFolderItem sfi=new ScanFolderItem();
                    sfi.folder_path=list[0];
                    sfi.process_sub_directories=list[1].equals("1")?true:false;
                    sfi.include=list[2].equals("1")?true:false;
                    scan_list.add(sfi);
                }
            }
        }
        settingScanDirectoryList=scan_list;
	}
	
	private boolean isDebuggable() {
		boolean result=false;
        PackageManager manager = appContext.getPackageManager();
        ApplicationInfo appInfo = null;
        try {
            appInfo = manager.getApplicationInfo(appContext.getPackageName(), 0);
        } catch (NameNotFoundException e) {
        	result=false;
        }
        if ((appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) == ApplicationInfo.FLAG_DEBUGGABLE)
        	result=true;
//        Log.v("","debuggable="+result);
        return result;
    }

    private class Slf4jLogWriter extends LoggerWriter {
        private LogUtil mLu =null;
        public Slf4jLogWriter(LogUtil lu) {
            mLu =lu;
        }
        @Override
        public void write(String msg) {
            mLu.addDebugMsg(1,"I", msg);
        }
    }

}
