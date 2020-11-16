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

import android.widget.LinearLayout;

import com.sentaroh.android.Utilities3.SafFile3;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

class PictureWorkItem {
	public LinearLayout view=null;
	public CustomImageView image_view=null;
//	public Bitmap image_bitmap=null;
	public byte[] image_thumbnail=null;
	public double image_gps_longitude=0D, image_gps_latitude=0D;
	public String image_file_info="";
	public String image_file_name="";
	public String image_folder_name="";
	public float image_scale=1.0f;
	public String image_orientation="";
	public float image_rotation=0.0f;
    public SafFile3 image_saf_file=null;
	public String image_file_path="";
	public String image_file_parent_directory="";
	public PictureListItem pictureItem=null;

	public ImageViewTouch.OnImageViewTouchSingleTapListener single_tap_listener=null;
}
