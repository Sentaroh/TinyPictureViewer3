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

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.sentaroh.android.Utilities3.Preference.CustomDialogPreference;
import com.sentaroh.android.Utilities3.Preference.CustomDialogPreference.CustomDialogPreferenceButtonListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class ActivitySettings extends PreferenceActivity {
    private static Logger log= LoggerFactory.getLogger(ActivitySettings.class);

	private static GlobalParameters mGp=null;
	
	private CommonUtilities mUtil=null;
	
//	private GlobalParameters mGp=null;
	
    @Override
    protected boolean isValidFragment(String fragmentName) {
        return true;
    }
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		mGp=GlobalWorkArea.getGlobalParameters(getApplicationContext());
//		setTheme(mGp.applicationTheme);
		super.onCreate(savedInstanceState);
		if (mUtil==null) mUtil=new CommonUtilities(this, "SettingsActivity", mGp);
		mUtil.addDebugMsg(1, "I", CommonUtilities.getExecutedMethodName()+" entered");
	}

    @Override
    public void onStart(){
        super.onStart();
        mUtil.addDebugMsg(1, "I", CommonUtilities.getExecutedMethodName()+" entered");
    };
 
    @Override
    public void onResume(){
        super.onResume();
        mUtil.addDebugMsg(1, "I", CommonUtilities.getExecutedMethodName()+" entered");
//		setTitle(R.string.settings_main_title);
    };
 
    @Override
    public void onBuildHeaders(List<Header> target) {
    	mUtil.addDebugMsg(1, "I", CommonUtilities.getExecutedMethodName()+" entered");
        loadHeadersFromResource(R.xml.settings_frag, target);
    };

    @Override
    public boolean onIsMultiPane () {
    	mGp=GlobalWorkArea.getGlobalParameters(getApplicationContext());
//    	mPrefActivity=this;
    	mUtil=new CommonUtilities(this, "SettingsActivity", mGp);
    	mUtil.addDebugMsg(1, "I", CommonUtilities.getExecutedMethodName()+" entered");
        return true;
    };

	@Override  
	protected void onPause() {  
	    super.onPause();  
	    mUtil.addDebugMsg(1, "I", CommonUtilities.getExecutedMethodName()+" entered");
	};

	@Override
	final public void onStop() {
		super.onStop();
		mUtil.addDebugMsg(1, "I", CommonUtilities.getExecutedMethodName()+" entered");
	};

	@Override
	final public void onDestroy() {
		super.onDestroy();
		mUtil.addDebugMsg(1, "I", CommonUtilities.getExecutedMethodName()+" entered");
	};

    public static class SettingsMisc extends PreferenceFragment {
    	private SharedPreferences.OnSharedPreferenceChangeListener listenerAfterHc =
    		    new SharedPreferences.OnSharedPreferenceChangeListener() {
    	    public void onSharedPreferenceChanged(SharedPreferences shared_pref, String key_string) {
                checkSettingValue(mUtil, shared_pref, getString(R.string.settings_exit_clean), getContext());
    	    }
    	};
    	private CommonUtilities mUtil=null;
        private Context mContext=null;
        @Override
        public void onCreate(Bundle savedInstanceState) {
        	super.onCreate(savedInstanceState);
        	mContext=getContext();
        	mUtil=new CommonUtilities(mContext, "SettingsMisc", mGp);
        	mUtil.addDebugMsg(1, "I", CommonUtilities.getExecutedMethodName()+" entered");
            
    		addPreferencesFromResource(R.xml.settings_frag_misc);

    		SharedPreferences shared_pref = PreferenceManager.getDefaultSharedPreferences(mContext);
    		
        	shared_pref.edit().putBoolean(getString(R.string.settings_exit_clean),true).commit();
            checkSettingValue(mUtil, shared_pref, getString(R.string.settings_exit_clean), getContext());
        };
        
        @Override
        public void onStart() {
        	super.onStart();
        	mUtil.addDebugMsg(1, "I", CommonUtilities.getExecutedMethodName()+" entered");
    	    getPreferenceScreen().getSharedPreferences()
    			.registerOnSharedPreferenceChangeListener(listenerAfterHc);
    		getActivity().setTitle(R.string.settings_misc_title);
        };
        @Override
        public void onStop() {
        	super.onStop();
        	mUtil.addDebugMsg(1, "I", CommonUtilities.getExecutedMethodName()+" entered");
    	    getPreferenceScreen().getSharedPreferences()
    			.unregisterOnSharedPreferenceChangeListener(listenerAfterHc);  
        };

        private boolean checkSettingValue(CommonUtilities ut, SharedPreferences shared_pref, String key_string, Context c) {
            boolean isChecked = false;
            Preference pref_key=findPreference(key_string);
            if (key_string.equals(c.getString(R.string.settings_exit_clean))) {
                isChecked=true;
                if (shared_pref.getBoolean(key_string, true)) {
                    pref_key.setSummary(c.getString(R.string.settings_exit_clean_summary_ena));
                } else {
                    pref_key.setSummary(c.getString(R.string.settings_exit_clean_summary_dis));
                }
            }
            return isChecked;
        };

    };

    public static class SettingsUi extends PreferenceFragment {
    	private SharedPreferences.OnSharedPreferenceChangeListener listenerAfterHc =
    		    new SharedPreferences.OnSharedPreferenceChangeListener() {
    	    public void onSharedPreferenceChanged(SharedPreferences shared_pref, String key_string) {
                checkSettingValue(mUtil, shared_pref, key_string, getContext());
    	    }
    	};
    	private CommonUtilities mUtil=null;
        private Context mContext=null;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mContext=getContext();
        	mUtil=new CommonUtilities(mContext, "SettingsUi", mGp);
        	mUtil.addDebugMsg(1, "I", CommonUtilities.getExecutedMethodName()+" entered");
           
    		addPreferencesFromResource(R.xml.settings_frag_ui);

    		SharedPreferences shared_pref = PreferenceManager.getDefaultSharedPreferences(mContext);

            checkSettingValue(mUtil, shared_pref,getString(R.string.settings_picture_display_default_ui_mode), getContext());
            checkSettingValue(mUtil, shared_pref,getString(R.string.settings_max_screen_brightness_when_image_showed), getContext());
            checkSettingValue(mUtil, shared_pref,getString(R.string.settings_picture_display_option_restore_when_startup), getContext());
    		
        };

        private boolean checkSettingValue(CommonUtilities ut, SharedPreferences shared_pref, String key_string, Context c) {
            boolean isChecked = false;
            if (key_string.equals(c.getString(R.string.settings_picture_display_default_ui_mode))) {
                isChecked=true;
            } else if (key_string.equals(c.getString(R.string.settings_max_screen_brightness_when_image_showed))) {
                isChecked=true;
            } else if (key_string.equals(c.getString(R.string.settings_picture_display_option_restore_when_startup))) {
                isChecked=true;
            }

            return isChecked;
        };

        @Override
        public void onStart() {
        	super.onStart();
        	mUtil.addDebugMsg(1, "I", CommonUtilities.getExecutedMethodName()+" entered");
    	    getPreferenceScreen().getSharedPreferences()
    			.registerOnSharedPreferenceChangeListener(listenerAfterHc);
    		getActivity().setTitle(R.string.settings_ui_title);
        };
        @Override
        public void onStop() {
        	super.onStop();
        	mUtil.addDebugMsg(1, "I", CommonUtilities.getExecutedMethodName()+" entered");
    	    getPreferenceScreen().getSharedPreferences()
    			.unregisterOnSharedPreferenceChangeListener(listenerAfterHc);  
        };
    };
    
    public static class SettingsFile extends PreferenceFragment {
    	private SharedPreferences.OnSharedPreferenceChangeListener listenerAfterHc =
    		    new SharedPreferences.OnSharedPreferenceChangeListener() {
    	    public void onSharedPreferenceChanged(SharedPreferences shared_pref, String key_string) {
                checkSettingValue(mUtil, shared_pref, key_string, getContext());
    	    }
    	};
    	private CommonUtilities mUtil=null;
        private Context mContext=null;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mContext=getContext();
        	mUtil=new CommonUtilities(mContext, "SettingsFile", mGp);
        	mUtil.addDebugMsg(1, "I", CommonUtilities.getExecutedMethodName()+" entered");
           
    		addPreferencesFromResource(R.xml.settings_frag_file);

    		SharedPreferences shared_pref = PreferenceManager.getDefaultSharedPreferences(mContext);
    		
    		if (mGp.masterFolderList.size()==0) {
    			findPreference(getString(R.string.settings_clear_cache)).setEnabled(false);
    		}
    		
    		checkSettingValue(mUtil, shared_pref,getString(R.string.settings_process_hidden_files), getContext());
    		checkSettingValue(mUtil, shared_pref,getString(R.string.settings_folder_filter_character_count), getContext());
    		checkSettingValue(mUtil, shared_pref,getString(R.string.settings_file_changed_auto_detect), getContext());
    		checkSettingValue(mUtil, shared_pref,getString(R.string.settings_camera_folder_always_top), getContext());
    		checkSettingValue(mUtil, shared_pref,getString(R.string.settings_clear_cache), getContext());
        };

        private boolean checkSettingValue(CommonUtilities ut, SharedPreferences shared_pref, String key_string, Context c) {
            boolean isChecked = false;
            final Preference pref_key=findPreference(key_string);
            if (key_string.equals(c.getString(R.string.settings_process_hidden_files))) {
                isChecked=true;
            } else if (key_string.equals(c.getString(R.string.settings_folder_filter_character_count))) {
                isChecked=true;
                pref_key.setSummary(String.format(c.getString(R.string.settings_folder_filter_character_count_summary),
                        shared_pref.getString(key_string, "4")));
            } else if (key_string.equals(c.getString(R.string.settings_file_changed_auto_detect))) {
                isChecked=true;
            } else if (key_string.equals(c.getString(R.string.settings_camera_folder_always_top))) {
                isChecked=true;
            } else if (key_string.equals(c.getString(R.string.settings_clear_cache))) {
                isChecked=true;
                CustomDialogPreference cdp=(CustomDialogPreference)pref_key;
                cdp.setButtonListener(new CustomDialogPreferenceButtonListener(){
                    @Override
                    public void onButtonClick(DialogInterface dialog, int which) {
                        if (which== DialogInterface.BUTTON_POSITIVE) {
                            File lf=new File(mGp.folderListFilePath);
                            lf.delete();

                            PictureUtil.clearCacheFileDirectory(mGp.pictureFileCacheDirectory);
                            PictureUtil.clearCacheFileDirectory(mGp.pictureBitmapCacheDirectory);

                            mGp.masterFolderList.clear();
                            mGp.showedFolderList.clear();
                            mGp.pictureFileCacheList.clear();

                            pref_key.setEnabled(false);
                        }
                    }
                });
            }

            return isChecked;
        };

        @Override
        public void onStart() {
        	super.onStart();
        	mUtil.addDebugMsg(1, "I", CommonUtilities.getExecutedMethodName()+" entered");
    	    getPreferenceScreen().getSharedPreferences()
    			.registerOnSharedPreferenceChangeListener(listenerAfterHc);
    		getActivity().setTitle(R.string.settings_file_title);
        };
        @Override
        public void onStop() {
        	super.onStop();
        	mUtil.addDebugMsg(1, "I", CommonUtilities.getExecutedMethodName()+" entered");
    	    getPreferenceScreen().getSharedPreferences()
    			.unregisterOnSharedPreferenceChangeListener(listenerAfterHc);  
        };
    };

}