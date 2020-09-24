package com.sentaroh.android.TinyPictureViewer3;


/*
The MIT License (MIT)
Copyright (c) 2011-2020 Sentaroh

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

public class Constants {

	public static final String APPLICATION_TAG="TinyPictureViewer3";
    public static final String PACKAGE_NAME="com.sentaroh.android."+APPLICATION_TAG;

	public static long SERIALIZABLE_VERSION_CODE=41L;

	public static final String DEFAULT_PREFS_FILENAME="default_preferences";
	
	final static public int IO_AREA_SIZE=1024*1024;

	public static final int ACTIVITY_REQUEST_CODE_SDCARD_STORAGE_ACCESS=40;

	public static final int SORT_ORDER_ASCENDANT=0;
	public static final int SORT_ORDER_DESCENDANT=1;
	
	public static final int SORT_KEY_FOLDER_NAME=0;
	public static final int SORT_KEY_FOLDER_PATH=2;
	public static final int SORT_KEY_FOLDER_DIR_LAST_MODIFIED=1;
	
	public static final int SORT_KEY_THUMBNAIL_FILE_NAME=0;
	public static final int SORT_KEY_THUMBNAIL_PICTURE_TIME=1;
	public static final int SORT_KEY_THUMBNAIL_FILE_LAST_MODIFIED=2;

	public static final float PICTURE_VIEW_MAX_SCALE=8.0f;
	public static final float PICTURE_VIEW_MIN_SCALE=1.0f;
	
	public static final int UI_MODE_FULL_SCREEN=1;
	public static final int UI_MODE_FULL_SCREEN_WITH_NAVI=2;
//	public static final int UI_MODE_FULL_SCREEN_WITH_SYSTEM_VIEW=2;
	public static final int UI_MODE_ACTION_BAR=0;
	final public static int CURRENT_VIEW_FOLDER=0;
	final public static int CURRENT_VIEW_THUMBNAIL=1;
	final public static int CURRENT_VIEW_PICTURE=2;
	
	final public static String AUTO_FILE_CHANGE_DETECTION_NONE="0"; 
	final public static String AUTO_FILE_CHANGE_DETECTION_ALWAYS="1";
	final public static String AUTO_FILE_CHANGE_DETECTION_MEDIA_STORE_CHANGED="2";


}
