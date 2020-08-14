package com.sentaroh.android.TinyPictureViewer3;

import android.content.Context;
import android.content.SharedPreferences;

import com.sentaroh.android.Utilities3.Base64Compat;
import com.sentaroh.android.Utilities3.SafFile3;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import static com.sentaroh.android.TinyPictureViewer3.CommonUtilities.getPrefMgr;
import static com.sentaroh.android.TinyPictureViewer3.Constants.AUTO_FILE_CHANGE_DETECTION_ALWAYS;
import static com.sentaroh.android.TinyPictureViewer3.GlobalParameters.DISPLAY_OPTION_LAST_UIMODE;
import static com.sentaroh.android.TinyPictureViewer3.GlobalParameters.FOLDER_LIST_SORT_KEY;
import static com.sentaroh.android.TinyPictureViewer3.GlobalParameters.FOLDER_LIST_SORT_ORDER;
import static com.sentaroh.android.TinyPictureViewer3.GlobalParameters.SCAN_FOLDER_LIST_KEY;
import static com.sentaroh.android.TinyPictureViewer3.GlobalParameters.SHOW_SIMPLE_FOLDER_VIEW_KEY;

class ConfigUtility {

    private static final String SETTING_PARMS_SAVE_STRING="S";
    private static final String SETTING_PARMS_SAVE_BOOLEAN="B";
    private static final String SETTING_PARMS_SAVE_INT="I";
    private static final String SETTING_PARMS_SAVE_LONG="L";

    private static void saveSettingsParmsToFileString(Context c, PrintWriter pw, String dflt,
                                                      String key) {
        SharedPreferences prefs = getPrefMgr(c);
        String k_type, k_val;

        k_val=prefs.getString(key, dflt);
        if (k_val!=null) {
            String enc = Base64Compat.encodeToString( k_val.getBytes(), Base64Compat.NO_WRAP);
            k_type=SETTING_PARMS_SAVE_STRING;
            String k_str=key+"\t"+k_type+"\t"+enc;

            pw.println(k_str);
        }
    };

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

    private final static String CONFIG_FILE_DIRECTORY="/storage/emulated/0/TinyPictureViewer", CONFIG_FILE_NAME="config.txt",
            CONFIG_FILE_PATH=CONFIG_FILE_DIRECTORY+"/"+CONFIG_FILE_NAME;

    private static final String CONFIG_FILE_IDENTIFIER="Config : Do not change this file ";

    public static boolean isBackupConfigFileExists(Context c) {
        SafFile3 lf=new SafFile3(c, CONFIG_FILE_PATH);
        return lf.exists();
    }

    public static void saveSettingsParmsToFile(Context c) {
        SafFile3 df=new SafFile3(c, CONFIG_FILE_DIRECTORY);
        if (!df.exists()) df.mkdirs();

        SafFile3 lf=new SafFile3(c, CONFIG_FILE_PATH);
        try {
            BufferedOutputStream bos=new BufferedOutputStream(lf.getOutputStream(), 1024*1024);
            PrintWriter pw=new PrintWriter(bos);

            pw.println(CONFIG_FILE_IDENTIFIER+System.currentTimeMillis());


//            saveSettingsParmsToFileInt(c, pw, 0, DISPLAY_OPTION_LAST_UIMODE);
            saveSettingsParmsToFileBoolean(c, pw, false, SHOW_SIMPLE_FOLDER_VIEW_KEY);
            saveSettingsParmsToFileInt(c, pw, 0, FOLDER_LIST_SORT_KEY);
            saveSettingsParmsToFileInt(c, pw, 0, FOLDER_LIST_SORT_ORDER);
            saveSettingsParmsToFileString(c, pw, "", SCAN_FOLDER_LIST_KEY);


//            android:key="@string/settings_suppress_add_external_storage_notification"


            saveSettingsParmsToFileBoolean(c, pw, true, c.getString(R.string.settings_max_screen_brightness_when_image_showed));
            saveSettingsParmsToFileBoolean(c, pw, true, c.getString(R.string.settings_picture_display_option_restore_when_startup));
            saveSettingsParmsToFileString(c, pw, "2", c.getString(R.string.settings_picture_display_default_ui_mode));

            saveSettingsParmsToFileBoolean(c, pw, false, c.getString(R.string.settings_process_hidden_files));
            saveSettingsParmsToFileString(c, pw, "4", 	c.getString(R.string.settings_folder_filter_character_count));
            saveSettingsParmsToFileString(c, pw, AUTO_FILE_CHANGE_DETECTION_ALWAYS, c.getString(R.string.settings_file_changed_auto_detect));
            saveSettingsParmsToFileBoolean(c, pw, true, c.getString(R.string.settings_camera_folder_always_top));

            pw.flush();
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    };

    static public void restoreSettingsParmFromFile(Context c) {
        File lf=new File(CONFIG_FILE_PATH);
        if (lf.exists()) {
            BufferedReader br;
            try {
                SharedPreferences.Editor prefs = getPrefMgr(c).edit();
                br = new BufferedReader(new FileReader(lf), 1024*1024);
                String pl;
                pl=br.readLine();
                if (pl.startsWith(CONFIG_FILE_IDENTIFIER)) {
                    while ((pl = br.readLine()) != null) {
                        String[] tmp_pl=pl.split("\t");// {"type","name","active",options...};
                        if (tmp_pl[1].equals(SETTING_PARMS_SAVE_STRING)) {
                            byte[] enc_array= Base64Compat.decode(tmp_pl[2], Base64Compat.NO_WRAP);
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
