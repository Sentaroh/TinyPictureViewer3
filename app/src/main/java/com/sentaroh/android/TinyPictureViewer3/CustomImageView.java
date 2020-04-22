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
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.sentaroh.android.Utilities3.Widget.ExtendImageViewTouch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.sentaroh.android.TinyPictureViewer3.Constants.*;

@SuppressWarnings("unused")
public class CustomImageView extends ExtendImageViewTouch{
    private static final Logger log= LoggerFactory.getLogger(CustomImageView.class);
	private Bitmap mBitmap=null;
	
	public interface OnZoomChangedListener {
		/**
		 * Callback invoked when the Zoom changed 
		 * @param scale
		 */
		void onZoomChanged(float scale);
	};

	public CustomImageView(Context context, AttributeSet attrs,
                           int defStyle) {
	    super(context, attrs, defStyle); 
//	    LOG_ENABLED=true;
	}

	public CustomImageView(Context context, AttributeSet attrs) {
	    super(context, attrs);
//	    LOG_ENABLED=true;
	}

	public CustomImageView(Context context) {
	    super(context);
//	    LOG_ENABLED=true;
	}

	@Override
	public void setImageBitmap( final Bitmap bitmap) {
		super.setImageBitmap(bitmap);
		if (mBitmap!=null && !mBitmap.equals(bitmap)) {
			mBitmap.recycle(); 
		}
		mBitmap=bitmap;
	}

	@Override
	public void setImageBitmap(final Bitmap bitmap, Matrix matrix, float min_zoom, float max_zoom ) {
		super.setImageBitmap(bitmap, matrix, min_zoom, max_zoom);
		if (mBitmap!=null && !mBitmap.equals(bitmap)) mBitmap.recycle();
		mBitmap=bitmap;
	}
	
	public Bitmap getBitMap() {
		return mBitmap;
	}
	
	private float mRotation=0f;
	
	@Override
	public float getRotation() {
		return mRotation;
	}
	
//	@Override
//	public void setRotation(float rotate) {
//		mRotation=rotate;
//	}

	public void postRotation(float rotation) {
		PointF center = getCenter();
//		mSuppMatrix.postScale( 2.0f, 2.0f, center.x, center.y );
		mSuppMatrix.postRotate( 90.0f, center.x, center.y );
		setImageMatrix( getImageViewMatrix() );
	}
	
	@Override
	protected float computeMaxZoom() {
		return PICTURE_VIEW_MAX_SCALE;
	}

	@Override
	protected float computeMinZoom() {
		return PICTURE_VIEW_MIN_SCALE;
	}

	
	private OnZoomChangedListener mOnZoomChangedListener=null;
	public void setOnZoomChangedListener(OnZoomChangedListener listener) {
		mOnZoomChangedListener=listener;
	}
	
	@Override
	protected void _setImageDrawable(final Drawable drawable, final Matrix initial_matrix, float min_zoom, float max_zoom ) {
		super._setImageDrawable( drawable, initial_matrix, min_zoom, max_zoom );
//		mScaleFactor = getMaxScale() / 3;
		mScaleFactor=4.0f;
	}
	
	@Override
	protected float onDoubleTapPost( float scale, float maxZoom ) {
		if ( (scale*2) <= PICTURE_VIEW_MAX_SCALE ) return scale*2;
		else if (scale>=PICTURE_VIEW_MAX_SCALE ) return 1f;
		else return PICTURE_VIEW_MAX_SCALE;
	};

	private boolean mZoomEnabled=false;
	public void setZoomEnabled(boolean enabled) {
		mZoomEnabled=enabled;
	};
	public boolean isZoomEnabled() {return mZoomEnabled;}
	
	@Override
	protected void zoomTo(float scale ) {
		if (isZoomEnabled()) {
			super.zoomTo(scale);
		} else {
		}
	};
	
	@Override
	protected void onZoom( float scale ) {
		if (mOnZoomChangedListener!=null) mOnZoomChangedListener.onZoomChanged(scale);
	}
//	
//	@Override
//	protected void onZoomAnimationCompleted( float scale ) {
//		Log.v("","zoomAnimation="+scale);
//	}
	
}
