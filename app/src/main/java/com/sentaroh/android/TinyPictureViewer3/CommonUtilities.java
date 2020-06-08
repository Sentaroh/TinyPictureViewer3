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
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckedTextView;
import android.widget.EdgeEffect;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import com.sentaroh.android.TinyPictureViewer3.Log.LogUtil;
import com.sentaroh.android.Utilities3.Base64Compat;
import com.sentaroh.android.Utilities3.Dialog.CommonDialog;
import com.sentaroh.android.Utilities3.SafFile3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;

import static com.sentaroh.android.TinyPictureViewer3.Constants.*;

public final class CommonUtilities {
    private static Logger log= LoggerFactory.getLogger(CommonUtilities.class);
	private Context mContext=null;

   	private LogUtil mLog=null;
   	
   	private GlobalParameters mGp=null;
   	
   	@SuppressWarnings("unused")
	private String mLogIdent="";
   	
	public CommonUtilities(Context c, String li, GlobalParameters gp) {
		mContext=c;// ContextはApplicationContext
		mLog=new LogUtil(c, li);
		mLogIdent=li;
        mGp=gp;
	}

	final public SharedPreferences getPrefMgr() {
    	return getPrefMgr(mContext);
    }

    static public void setButtonLabelListener(final Activity a, ImageButton ib, final String label) {
        ib.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                Toast toast= CommonDialog.getToastShort(a, label);
                positionToast(toast, v, a.getWindow(), 0, 0);
                toast.show();
                return true;
            }
        });
    };

    static public void positionToast(Toast toast, View view, Window window, int offsetX, int offsetY) {
        // toasts are positioned relatively to decor view, views relatively to their parents, we have to gather additional data to have a common coordinate system
        Rect rect = new Rect();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);
//        log.info("m_h="+window.getDecorView().getMeasuredHeight());
        // covert anchor view absolute position to a position which is relative to decor view
        int[] viewLocation = new int[2];
        view.getLocationInWindow(viewLocation);
        int viewLeft = viewLocation[0] - rect.left;
        int viewTop = viewLocation[1] - rect.top;
//        log.info("getX="+view.getX()+", getY="+view.getY()+", right="+view.getRight()+", top="+view.getTop()+", bttom="+view.getBottom());
//        log.info("view_loc0="+viewLocation[0]+", view_loc1="+viewLocation[1]);
//        log.info("rect_top="+rect.top+", rect_bottom="+rect.bottom+", rect_left="+rect.left+", rect_right="+rect.right+", width="+rect.width()+", height="+rect.height());

        // measure toast to center it relatively to the anchor view
        DisplayMetrics metrics = new DisplayMetrics();
        window.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(metrics.widthPixels, View.MeasureSpec.UNSPECIFIED);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(metrics.heightPixels, View.MeasureSpec.UNSPECIFIED);
        toast.getView().measure(widthMeasureSpec, heightMeasureSpec);
        int toastWidth = toast.getView().getMeasuredWidth();

        // compute toast offsets
//        int toastX = rect.left<0?(viewLocation[0]-toastWidth/3):(viewLeft + (view.getWidth() - toastWidth) / 2 + offsetX);
        int toastX = viewLocation[0]-toastWidth/4;
        int toastY = view.getHeight()*2;//viewTop + view.getHeight() + offsetY;
//        log.info("x="+toastX+", y="+toastY+", left="+viewLeft+", top="+viewTop+", width="+view.getWidth()+", height="+view.getHeight()+", toastW="+toastWidth);
        toast.setGravity(Gravity.LEFT | Gravity.BOTTOM, toastX, toastY);
    }

//	static public void cleanupWorkFile(GlobalParameters mGp) {
//		final String work_dir=mGp.internalRootDirectory+"/"+APPLICATION_TAG+"/"+WORK_DIRECTORY;
//		File w_lf=new File(work_dir);
//		FileIo.deleteLocalItem(w_lf);
//	};

	public static void setEdgeGlowColor(ViewPager viewPager) {
		int color= Color.argb(255, 255, 0, 127);
	    try {
	        Class<?> clazz = ViewPager.class;
	        for (String name : new String[] {"mLeftEdge", "mRightEdge"}) {
	            Field field = clazz.getDeclaredField(name);
	            field.setAccessible(true);
	            Object edge = field.get(viewPager); // android.support.v4.widget.EdgeEffectCompat
	            Field fEdgeEffect = edge.getClass().getDeclaredField("mEdgeEffect");
	            fEdgeEffect.setAccessible(true);
	            ((EdgeEffect)fEdgeEffect.get(edge)).setColor(color);
	        }
	    } catch (Exception ignored) {
	    }
	};

    public static String getStackTraceElement(Exception e) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.flush();
        pw.close();
        return sw.toString();
    }

    public static String sharePictures(Context c, String[] send_pic_fp) {
        if (send_pic_fp.length>1) {
            Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            intent.setType("image/*"); /* This example is sharing jpeg images. */

            ArrayList<Uri> files = new ArrayList<Uri>();

            for(String path : send_pic_fp) {
                File file = new File(path);
                SafFile3 sf=new SafFile3(c, path);
                Uri uri =sf.getUri();
                files.add(uri);
            }
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
            try {
//                String npe=null;
//                npe.length();
                c.startActivity(intent);
            } catch(Exception e) {
                return "startActivity() failed at shareItem() for multiple item. message="+e.getMessage()+"\n"+getStackTraceElement(e);
            }
        } else {
            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            File lf=new File(send_pic_fp[0]);
            SafFile3 sf=new SafFile3(c, send_pic_fp[0]);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            Uri uri=sf.getUri();
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.setType("image/*");
            try {
                c.startActivity(intent);
            } catch(Exception e) {
                return "startActivity() failed at shareItem() for multiple item. message="+e.getMessage()+"\n"+getStackTraceElement(e);
            }
        }
        return null;
    };

	public static void setSpinnerBackground(Context c, Spinner spinner, boolean theme_is_light) {
		if (theme_is_light) spinner.setBackground(c.getDrawable(R.drawable.spinner_color_background_light));
		else spinner.setBackground(c.getDrawable(R.drawable.action_bar_spinner_color_background));
	};

	@SuppressLint("InlinedApi")
	final static public SharedPreferences getPrefMgr(Context c) {
    	return PreferenceManager.getDefaultSharedPreferences(c);
    }

	final public void setLogId(String li) {
		mLog.setLogId(li);
	};
	
	public static void printStackTraceElement(CommonUtilities ut, StackTraceElement[] ste) {
		for (int i=0;i<ste.length;i++) {
			ut.addLogMsg("E","",ste[i].toString());	
		}
	};

//	@SuppressLint("DefaultLocale")
//	final static public String getFileExtention(String fp) {
//		String fid="";
//		if (fp.lastIndexOf(".") > 0) {
//			fid = fp.substring(fp.lastIndexOf(".") + 1).toLowerCase();
//		}
//		return fid;
//	};
	
	final static public String getExecutedMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		return name;
	}

	final public void resetLogReceiver() {
		mLog.resetLogReceiver();
	};

	final public void flushLog() {
		mLog.flushLog();
	};

	final public void rotateLogFile() {
		mLog.rotateLogFile();
	};

    final public void deleteLogFile() {
    	mLog.deleteLogFile();
	};

	public String buildPrintMsg(String cat, String... msg) {
		return mLog.buildPrintLogMsg(cat, msg);
	};
	
	final public void addLogMsg(String cat, String... msg) {
		mLog.addLogMsg(cat, msg); 
	};
	final public void addDebugMsg(int lvl, String cat, String... msg) {
		mLog.addDebugMsg(lvl, cat, msg);
	};

	final public boolean isLogFileExists() {
		boolean result = false;
		result=mLog.isLogFileExists();
		addDebugMsg(3,"I","Log file exists="+result);
		return result;
	};

	final public String getLogFilePath() {
		return mLog.getLogFilePath();
	};
	
	static public long getSettingsParmSaveDate(Context c, String dir, String fn) {
		File lf=new File(dir+"/"+fn);
		long result=0;
		if (lf.exists()) {
			result=lf.lastModified();
		} else {
			result=-1;
		}
		return result;
	};
	
	public boolean isDebuggable() {
        PackageManager manager = mContext.getPackageManager();
        ApplicationInfo appInfo = null;
        try {
            appInfo = manager.getApplicationInfo(mContext.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            return false;
        }
        if ((appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) == ApplicationInfo.FLAG_DEBUGGABLE)
            return true;
        return false;
    };
	
	public void initAppSpecificExternalDirectory(Context c) {
//		if (Build.VERSION.SDK_INT>=19) {
//			c.getExternalFilesDirs(null);
//		} else {
//		}
//		ContextCompat.getExternalFilesDirs(c, null);
        c.getExternalFilesDirs(null);
	};
	
	static public void setCheckedTextView(final CheckedTextView ctv) {
		ctv.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				ctv.toggle();
			}
		});
	};

	public static final String SETTING_PARMS_SAVE_STRING="S";
	public static final String SETTING_PARMS_SAVE_BOOLEAN="B";
	public static final String SETTING_PARMS_SAVE_INT="I";
	public static final String SETTING_PARMS_SAVE_LONG="I";

	private static void saveSettingsParmsToFileString(Context c, PrintWriter pw, String dflt,
                                                      String key) {
		SharedPreferences prefs = getPrefMgr(c);
		String k_type, k_val;

		k_val=prefs.getString(key, dflt);
		if (k_val!=null) {
			String enc = Base64Compat.encodeToString(
					k_val.getBytes(), 
					Base64Compat.NO_WRAP);
			k_type=SETTING_PARMS_SAVE_STRING;
			String k_str=key+"\t"+k_type+"\t"+enc;

			pw.println(k_str);
		}
	};
	
	@SuppressWarnings("unused")
	static private void saveSettingsParmsToFileInt(Context c, PrintWriter pw, int dflt,
                                                   String key) {
		SharedPreferences prefs = getPrefMgr(c);
		String k_type;
		int k_val;

		k_val=prefs.getInt(key, dflt);
		k_type=SETTING_PARMS_SAVE_INT;
		String k_str=key+"\t"+k_type+"\t"+k_val;
		pw.println(k_str);
	};

	@SuppressWarnings("unused")
	static private void saveSettingsParmsToFileLong(Context c, PrintWriter pw, long dflt,
                                                    String key) {
		SharedPreferences prefs = getPrefMgr(c);
		String k_type;
		long k_val;

		k_val=prefs.getLong(key, dflt);
		k_type=SETTING_PARMS_SAVE_LONG;
		String k_str=key+"\t"+k_type+"\t"+k_val;
		pw.println(k_str);
	};
	
	static private void saveSettingsParmsToFileBoolean(Context c, PrintWriter pw, boolean dflt,
                                                       String key) {
		SharedPreferences prefs = getPrefMgr(c);
		String k_type;
		boolean k_val;

		k_val=prefs.getBoolean(key, dflt);
		k_type=SETTING_PARMS_SAVE_BOOLEAN;
		String k_str=key+"\t"+k_type+"\t"+k_val;
		pw.println(k_str);
	};

	public static void saveSettingsParmsToFile(Context c, String dir, String fn) {
		File df=new File(dir);
		if (!df.exists()) df.mkdirs();
		
		File lf=new File(dir+"/"+fn);
		try {
			PrintWriter pw=new PrintWriter(lf);
			
			pw.println("Config "+System.currentTimeMillis());
			
			saveSettingsParmsToFileString(c, pw, null, GlobalParameters.SCAN_FOLDER_LIST_KEY);
			saveSettingsParmsToFileBoolean(c, pw, true, c.getString(R.string.settings_max_screen_brightness_when_image_showed));
			saveSettingsParmsToFileBoolean(c, pw, true, c.getString(R.string.settings_picture_display_option_restore_when_startup));
			saveSettingsParmsToFileString(c, pw, "2", c.getString(R.string.settings_picture_display_default_ui_mode));
			
			saveSettingsParmsToFileBoolean(c, pw, false, c.getString(R.string.settings_process_hidden_files));
			saveSettingsParmsToFileString(c, pw, "4", 	c.getString(R.string.settings_folder_filter_character_count));
			saveSettingsParmsToFileString(c, pw, AUTO_FILE_CHANGE_DETECTION_ALWAYS,
														c.getString(R.string.settings_file_changed_auto_detect));
			saveSettingsParmsToFileBoolean(c, pw, true, c.getString(R.string.settings_camera_folder_always_top));

			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	};
	static public void restoreSettingsParmFromFile(Context c, String dir, String fn) {
		File lf=new File(dir+"/"+fn);
		if (lf.exists()) {
			BufferedReader br;
			try {
				Editor prefs = getPrefMgr(c).edit();
				br = new BufferedReader(new FileReader(lf),8192);
				String pl;
				pl=br.readLine();
				if (pl.startsWith("Config ")) {
					while ((pl = br.readLine()) != null) {
						String[] tmp_pl=pl.split("\t");// {"type","name","active",options...};
						if (tmp_pl[1].equals(SETTING_PARMS_SAVE_STRING)) {
							byte[] enc_array=Base64Compat.decode(tmp_pl[2], Base64Compat.NO_WRAP);
							String value=new String(enc_array);
							prefs.putString(tmp_pl[0], value).commit();
						} else if (tmp_pl[1].equals(SETTING_PARMS_SAVE_LONG)) {
							prefs.putLong(tmp_pl[0], Long.parseLong(tmp_pl[2])).commit();
						} else if (tmp_pl[1].equals(SETTING_PARMS_SAVE_INT)) {
							prefs.putInt(tmp_pl[0], Integer.parseInt(tmp_pl[2])).commit();
						} else if (tmp_pl[1].equals(SETTING_PARMS_SAVE_BOOLEAN)) {
							if (tmp_pl[2].equals("true")) prefs.putBoolean(tmp_pl[0], true).commit();
							else prefs.putBoolean(tmp_pl[0], false).commit();
						}
					}
				}
				br.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
		}
	};

}
