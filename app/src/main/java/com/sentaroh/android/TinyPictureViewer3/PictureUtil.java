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
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;

import com.sentaroh.android.Utilities3.SafFile3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import static com.sentaroh.android.TinyPictureViewer3.Constants.*;

@SuppressLint("DefaultLocale")
public class PictureUtil {
    private static Logger log= LoggerFactory.getLogger(PictureUtil.class);

	final static public String createBitmapCacheFilePath(GlobalParameters gp, String pic_file_path) {
		String cache_name=(pic_file_path).replace("/", "_")+".bmc";
		return gp.internalRootDirectory+gp.appSpecificDirectory+"/bitmap_cache/"+cache_name;
	};
	
	final static public boolean isBitmapCacheFileExists(GlobalParameters gp, String pic_file_path) {
		File lf=new File(createBitmapCacheFilePath(gp, pic_file_path));
		return lf.exists();
	};
	
	final static public void removeBitmapCacheFile(GlobalParameters gp, String pic_file_path) {
		removePictureFileCacheItemFromCache(gp, pic_file_path);
		File cf=new File(PictureUtil.createBitmapCacheFilePath(gp, pic_file_path));
		cf.delete();
	};
	
	public static class PictureFileCacheItem implements Externalizable{
		private static final long serialVersionUID = SERIALIZABLE_VERSION_CODE;
		public PictureFileCacheItem(){};
		public String file_path="";
        public String file_uri_string="";
		public long file_length=0L;
		public long file_last_modified=0L;
		byte[] bitmap_byte_array=null;
		@Override
		public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException {
			if (input.readLong()!=serialVersionUID) 
				throw new IOException("serialVersionUID was not matched by saved UID");
			file_path=input.readUTF();
			file_uri_string=input.readUTF();
			file_length=input.readLong();
			file_last_modified=input.readLong();
			bitmap_byte_array=readArrayByte(input);
		}
		@Override
		public void writeExternal(ObjectOutput output) throws IOException {
			output.writeLong(serialVersionUID);
			output.writeUTF(file_path);
            output.writeUTF(file_uri_string);
			output.writeLong(file_length);
			output.writeLong(file_last_modified);
			writeArrayByte(output,bitmap_byte_array);
		}
	};
	
	final static public byte[] readArrayByte(ObjectInput input) throws IOException{
		int lsz=input.readInt();
		byte[] result=null;
		if (lsz!=-1) {
			result=new byte[lsz];
			if (lsz>0) input.readFully(result,0,lsz);
		}
		return result;
	};
	
	final static public void writeArrayByte(ObjectOutput output, byte[] al) throws IOException {
		int lsz=-1;
		if (al!=null) {
			if (al.length!=0) lsz=al.length;
			else lsz=0;
		}
		output.writeInt(lsz);
		if (lsz>0) {
			output.write(al,0,lsz);
		}
	};

	final static long MAX_BITMAP_FILE_CACHE_SIZE=1024*1024*512; //512MB
	final static long MAX_BITMAP_FILE_RETENTION_PERIOD=(1000*60*60*24)*30;//30Day
	static public void houseKeepBitmapCacheFile(final GlobalParameters gp, final String cd) {
		Thread th=new Thread(){
			@Override
			public void run() {
//				long b_time=System.currentTimeMillis();
				File df=new File(cd);
				File[] cf_list=df.listFiles();
				if (cf_list!=null && cf_list.length>0) {
					long expired_time=System.currentTimeMillis()-MAX_BITMAP_FILE_RETENTION_PERIOD;
					long fs=0;
					for(File ch_file:cf_list) fs+=ch_file.length();
					for(File ch_file:cf_list) {
						if (ch_file.exists()) {
							if (fs>=MAX_BITMAP_FILE_CACHE_SIZE) {
								if (ch_file.lastModified()<=expired_time) {
									ch_file.delete();
									File del_file=new File(ch_file.getPath().substring(0,ch_file.getPath().lastIndexOf(".")));
									del_file.delete();
								}
							}
						}
					}
				}
			};
		};
		th.setName("cacheHousekeep");
		th.start();
	};

	static public void clearCacheFileDirectory(final String cd) {
		Thread th=new Thread() {
			@Override
			public void run() {
				File df=new File(cd);
				File[] cf_list=df.listFiles();
				if (cf_list!=null && cf_list.length>0) {
					for(File ch_file:cf_list) {
						ch_file.delete();
//						SafFile3 del_file=new SafFile3(ch_file.getPath().substring(0,ch_file.getPath().lastIndexOf(".")));
//						del_file.delete();
					}
				}
			}
		};
		th.setName("ClearBitmapCache");
		th.start();
	};

	static public void removePictureFileCacheItemFromCache(GlobalParameters gp, String pic_file_path) {
		PictureFileCacheItem result=null;
		synchronized(gp.pictureFileCacheList) {
			if (gp.pictureFileCacheList.size()>0) {
				for(PictureFileCacheItem pfci:gp.pictureFileCacheList) {
					if (pfci.file_path.equals(pic_file_path)) {
						result=pfci;
//			        	if (gp.settingDebugLevel>1) Log.v(APPLICATION_TAG,"Cache hit, name="+pic_file_path);
						break;
					}
				}
				if (result!=null) {
					gp.pictureFileCacheList.remove(result);
				}
			}
		}
	};

	static public PictureFileCacheItem getPictureFileCacheItemFromCache(GlobalParameters gp, String pic_file_path) {
		PictureFileCacheItem result=null;
		synchronized(gp.pictureFileCacheList) {
			if (gp.pictureFileCacheList.size()>0) {
				for(PictureFileCacheItem pfci:gp.pictureFileCacheList) {
					if (pfci.file_path.equals(pic_file_path)) {
						result=pfci;
//			        	if (gp.settingDebugLevel>1) Log.v(APPLICATION_TAG,"Cache hit, name="+pic_file_path);
						break;
					}
				}
				if (result!=null) {
					gp.pictureFileCacheList.remove(result);
					gp.pictureFileCacheList.add(0, result);
				}
			}
		}
		return result;
	};
	
	final static private int MAX_CACHE_SIZE=100;
	static private void addPictureFileCacheItemToCache(GlobalParameters gp, PictureFileCacheItem pfci) {
		synchronized(gp.pictureFileCacheList) {
			PictureFileCacheItem c_pfci=getPictureFileCacheItemFromCache(gp, pfci.file_path);
			if (c_pfci==null) {
				gp.pictureFileCacheList.add(0, pfci);
				if (gp.pictureFileCacheList.size()>MAX_CACHE_SIZE) {
					gp.pictureFileCacheList.remove(MAX_CACHE_SIZE);
				}
			} else {
				c_pfci.bitmap_byte_array=pfci.bitmap_byte_array;
				c_pfci.file_last_modified=pfci.file_last_modified;
				c_pfci.file_length=pfci.file_length;
			}
		}
	};
	
	static public PictureFileCacheItem loadPictureFileCacheFile(GlobalParameters gp, String pic_file_path) {
		long b_time=System.currentTimeMillis();
		PictureFileCacheItem pfbmci=null;
		try {
			FileInputStream fis=new FileInputStream(new File(createBitmapCacheFilePath(gp, pic_file_path)));
			BufferedInputStream bis=new BufferedInputStream(fis,1024*1024*4);
			ObjectInputStream ois=new ObjectInputStream(bis);
			pfbmci=new PictureFileCacheItem();
			pfbmci.readExternal(ois);
			ois.close();
			bis.close();
			addPictureFileCacheItemToCache(gp, pfbmci) ;
            log.debug("Bitmap cache SafFile3 loaded"+
                ", elapsed time="+(System.currentTimeMillis()-b_time)+", fp="+pic_file_path);
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return pfbmci;
	};

	static private void savePictureFileCacheFile(GlobalParameters gp,  
			PictureFileCacheItem pfbmci) {
		try {
			long b_time=System.currentTimeMillis();
			final File bmcf=new File(createBitmapCacheFilePath(gp, pfbmci.file_path));
			FileOutputStream fos=new FileOutputStream(bmcf);
			BufferedOutputStream bos=new BufferedOutputStream(fos,1024*1024*4);
			ObjectOutputStream oos=new ObjectOutputStream(bos);
			pfbmci.writeExternal(oos);
			oos.flush();
			oos.close();
			addPictureFileCacheItemToCache(gp, pfbmci);
            log.debug("savePictureFileCacheFile cache SafFile3 saved"+
                ", elapsed time="+(System.currentTimeMillis()-b_time)+", fp="+bmcf.getAbsolutePath());
		} catch (Exception e) {
            log.debug("savePictureFileCacheFile cache SafFile3 save error, error="+e.getMessage()+", fp="+pfbmci.file_path);
		}
	};

	final static public PictureFileCacheItem getPictureFileCacheItem(final GlobalParameters gp,
                                                                     final SafFile3 sf, DisplayMetrics disp_metrics, String orientation) {
    	long b_time=System.currentTimeMillis();
    	boolean recreate_required=false;
    	PictureFileCacheItem pfbmci=getPictureFileCacheItemFromCache(gp, sf.getPath());
    	if (pfbmci==null) {
    		pfbmci=loadPictureFileCacheFile(gp, sf.getPath());
    	}
		if (pfbmci!=null) {
            long[] lm_sz=sf.getLastModifiedAndLength();
    		if (lm_sz[0]!=pfbmci.file_last_modified || lm_sz[1]!=pfbmci.file_length) {
    			recreate_required=true;
    		}
		} else {
			recreate_required=true;
		}
//    	recreate_required=true;
    	
    	if (recreate_required) {
    		pfbmci=createPictureCacheFile(gp, sf, disp_metrics, orientation);
    	}
        gp.cUtil.addDebugMsg(1,"I","getPictureFileCacheItem ended, Elapsed time="+(System.currentTimeMillis()-b_time)+
                ", result="+pfbmci+", fp="+sf.getPath());

		return pfbmci;
    };

    final static private PictureFileCacheItem createPictureCacheFile(
            final GlobalParameters gp,
            final SafFile3 sf, DisplayMetrics disp_metrics, String orientation) {
		Bitmap bitmap=createPictureFileBitmap(gp, sf, disp_metrics, orientation);
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		byte[] ba=null;
		if (bitmap!=null) {
			bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
			ba=baos.toByteArray();
			bitmap.recycle();
		}
		
		final PictureFileCacheItem pfbmci=new PictureFileCacheItem();
		pfbmci.file_path=sf.getPath();
		long[] lm_sz=sf.getLastModifiedAndLength();
		pfbmci.file_last_modified=lm_sz[0];
		pfbmci.file_length=lm_sz[1];
		pfbmci.bitmap_byte_array=ba;
		
		addPictureFileCacheItemToCache(gp, pfbmci) ;

    	Thread save=new Thread() {
    		@Override
    		public void run() {
    			savePictureFileCacheFile(gp, pfbmci);
    		}
    	};
    	save.start();
    	return pfbmci;
    };
    
  	final static public String EXIF_IMAGE_ORIENTATION_CLOCKWISE_90_AND_FLIP_HORIZONTAL="5";//時計回りに90度回転して水平反転
    final static public String EXIF_IMAGE_ORIENTATION_CLOCKWISE_90="6";//時計回りに90度回転
    final static public String EXIF_IMAGE_ORIENTATION_CLOCKWISE_270_AND_FLIP_HORIZONTAL="7";//時計回りに270度回転して水平反転
    final static public String EXIF_IMAGE_ORIENTATION_CLOCKWISE_270="8";//時計回りに270度回転

	final static private Bitmap createPictureFileBitmap(final GlobalParameters gp,
                                                        final SafFile3 sf, DisplayMetrics disp_metrics, String orientation) {
    	long b_time=System.currentTimeMillis();
    	byte[] image_file_byte_array=PictureUtil.createImageByteArray(gp, sf);
    	if (image_file_byte_array!=null) {
            BitmapFactory.Options org_opt = new BitmapFactory.Options();
            org_opt.inJustDecodeBounds=true;
            BitmapFactory.decodeByteArray(image_file_byte_array, 0, image_file_byte_array.length, org_opt);

            BitmapFactory.Options decode_opt = new BitmapFactory.Options();
            if (org_opt.outHeight*org_opt.outHeight>1024*1024*20) {
                decode_opt.inSampleSize=2;
            }
            Bitmap input_bitmap= BitmapFactory.decodeByteArray(image_file_byte_array, 0, image_file_byte_array.length, decode_opt);
            if (input_bitmap!=null) {
                long decoded_time=System.currentTimeMillis()-b_time;

                int bm_width=0, bm_height=0;
                if (orientation.equals(PictureListItem.EXIF_IMAGE_ORIENTATION_CLOCKWISE_270) ||
                        orientation.equals(PictureListItem.EXIF_IMAGE_ORIENTATION_CLOCKWISE_90) ||
                        orientation.equals(PictureListItem.EXIF_IMAGE_ORIENTATION_CLOCKWISE_90_AND_FLIP_HORIZONTAL) ||
                        orientation.equals(PictureListItem.EXIF_IMAGE_ORIENTATION_CLOCKWISE_270_AND_FLIP_HORIZONTAL) ) {
                    bm_width=input_bitmap.getHeight();
                    bm_height=input_bitmap.getWidth();
                } else {
                    bm_height=input_bitmap.getHeight();
                    bm_width=input_bitmap.getWidth();
                }
                bm_height=input_bitmap.getHeight();
                bm_width=input_bitmap.getWidth();

                float scale=getFitImageSize(bm_width, bm_height);

                int o_h=0, o_w=0;
                o_w=(int)((float)input_bitmap.getWidth()/scale);
                o_h=(int)((float)input_bitmap.getHeight()/scale);

                Bitmap output_bitmap=null;
                if (scale!=1.0f) {
                    output_bitmap= Bitmap.createScaledBitmap(input_bitmap, o_w, o_h, true);
                    input_bitmap.recycle();
                } else output_bitmap=input_bitmap;
                Bitmap rot_bm=rotateBitmapByPictureOrientation(output_bitmap, orientation);

                log.debug("createPictureFileBitmap Picture bit map created"+
                        ", Display height="+disp_metrics.heightPixels+", width="+disp_metrics.widthPixels+
                        ", Density="+disp_metrics.density+
                        ", Original Bitmap height="+org_opt.outHeight+", width="+org_opt.outWidth+
                        ", Size="+input_bitmap.getByteCount()+
                        ", Scale="+scale+
                        ", Resized Bitmap height="+rot_bm.getHeight()+", width="+rot_bm.getWidth()+", size="+rot_bm.getByteCount()+
                        ", Decode time="+decoded_time+
                        ", Elapsed time="+(System.currentTimeMillis()-b_time)+
                        ", fp="+sf.getPath());
                return rot_bm;
            } else {
                log.debug("createPictureFileBitmap Picture dummy bit map created"+
                        ", Elapsed time="+(System.currentTimeMillis()-b_time)+
                        ", fp="+sf.getPath());
                return null;
            }
        } else {
            log.debug("createPictureFileBitmap null bm_array"+
                    ", Elapsed time="+(System.currentTimeMillis()-b_time)+
                    ", fp="+sf.getPath());
    	    return null;
        }
    };

    private static float getFitImageSize(int bm_width, int bm_height) {
    	float base=2048.0f;
		float o_h=0, o_w=0;
		o_w=((float)bm_width/base);
		o_h=((float)bm_height/base);
		if (Math.max(o_h, o_w)>=1.0f) return Math.max(o_h, o_w);
		else return 1.0f;
    };
    
	final static public String getFileExtention(String name) {
		int per_pos=name.lastIndexOf(".");
		if (per_pos > 0) {
			return name.substring(per_pos + 1);//.toLowerCase();
		}
		return "";
	};

    final static public String getFileName(String fp) {
        String file_name="";
        if (fp.lastIndexOf("/")>=0) {
            file_name=fp.substring(fp.lastIndexOf("/")+1);
        } else {
            file_name=fp;
        }
        return file_name;
    };

    static private boolean hasContainedNomediaFile(SafFile3 lf, ContentProviderClient cpc) {
		boolean result=false;
		SafFile3 nomedia=new SafFile3(lf.getContext(), lf.getPath()+"/.nomedia");
		result=nomedia.exists(cpc);
		return result;
	};

    static private boolean hasContainedNomediaFile(SafFile3 lf) {
        boolean result=false;
        SafFile3 nomedia=new SafFile3(lf.getContext(), lf.getPath()+"/.nomedia");
        result=nomedia.exists();
        return result;
    };

    static private boolean hasContainedNomediaFile(File lf) {
        boolean result=false;
        File nomedia=new File(lf.getPath()+"/.nomedia");
        result=nomedia.exists();
        return result;
    };

    final static public void getAllPictureFileInDirectory(GlobalParameters gp,
                                                                 ArrayList<SafFile3>fl, SafFile3 lf, boolean process_sub_directories) {
        ContentProviderClient cpc=null;
        try {
            if (Build.VERSION.SDK_INT>=29) {
                cpc=lf.getContentProviderClient();
                if (cpc==null) getFileApiAllPictureFileInDirectory(gp, fl, lf, process_sub_directories);
                else getSafApiAllPictureFileInDirectory(gp, fl, lf, process_sub_directories, cpc);
            } else {
                File tf=new File(lf.getPath());
                if (tf.canRead()) {
                    getFileApiAllPictureFileInDirectory(gp, fl, tf, process_sub_directories);
                } else {
                    cpc=lf.getContentProviderClient();
                    if (cpc==null) getFileApiAllPictureFileInDirectory(gp, fl, lf, process_sub_directories);
                    else getSafApiAllPictureFileInDirectory(gp, fl, lf, process_sub_directories, cpc);
                }
            }
        } finally {
            if (cpc!=null) cpc.release();
        }
    }

    final static public void getSafApiAllPictureFileInDirectory(GlobalParameters gp,
                                          ArrayList<SafFile3>fl, SafFile3 lf, boolean process_sub_directories, ContentProviderClient cpc) {
		if (lf.exists()) {
			if (gp.settingScanHiddenFile || (!lf.isHidden() && !hasContainedNomediaFile(lf, cpc))) {
				if (lf.isDirectory(cpc)) {
					if (!isDirectoryToBeExcluded(gp, lf)) {
						SafFile3[] cfl=lf.listFiles(cpc);
						if (cfl!=null && cfl.length>0) {
							for(SafFile3 cf:cfl) {
								if (gp.settingScanHiddenFile || !cf.isHidden()) {
									if (cf.isDirectory(cpc)) {
										if (gp.settingScanHiddenFile || !hasContainedNomediaFile(lf, cpc)) {
											if (!cf.getName().equals(".thumbnails")) {
												if (process_sub_directories) 
													getSafApiAllPictureFileInDirectory(gp, fl, cf, process_sub_directories, cpc);
											}
										} else {
											break;
										}
									} else {
										if (isPictureFile(gp, cf.getName())) fl.add(cf);
									}
								}
							}
						}
					}
				} else {
					if (isPictureFile(gp, lf.getPath())) fl.add(lf);
				}
			}
		} 
	};

    final static public void getFileApiAllPictureFileInDirectory(GlobalParameters gp,
                                                          ArrayList<SafFile3>fl, File lf, boolean process_sub_directories) {
        if (lf.exists()) {
            if (gp.settingScanHiddenFile || (!lf.isHidden() && !hasContainedNomediaFile(lf))) {
                if (lf.isDirectory()) {
                    if (!isDirectoryToBeExcluded(gp, lf.getPath())) {
                        File[] cfl=lf.listFiles();
                        if (cfl!=null && cfl.length>0) {
                            for(File cf:cfl) {
                                if (gp.settingScanHiddenFile || !cf.isHidden()) {
                                    if (cf.isDirectory()) {
                                        if (gp.settingScanHiddenFile || !hasContainedNomediaFile(lf)) {
                                            if (!cf.getName().equals(".thumbnails")) {
                                                if (process_sub_directories)
                                                    getFileApiAllPictureFileInDirectory(gp, fl, cf, process_sub_directories);
                                            }
                                        } else {
                                            break;
                                        }
                                    } else {
                                        if (isPictureFile(gp, cf.getName())) {
                                            fl.add(new SafFile3(gp.appContext, cf.getPath()));
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (isPictureFile(gp, lf.getPath())) {
                        fl.add(new SafFile3(gp.appContext, lf.getPath()));
                    }
                }
            }
        }
    };

    final static public void getFileApiAllPictureFileInDirectory(GlobalParameters gp,
                                                                 ArrayList<SafFile3>fl, SafFile3 lf, boolean process_sub_directories) {
        if (lf.exists()) {
            if (gp.settingScanHiddenFile || (!lf.isHidden() && !hasContainedNomediaFile(lf))) {
                if (lf.isDirectory()) {
                    if (!isDirectoryToBeExcluded(gp, lf)) {
                        SafFile3[] cfl=lf.listFiles();
                        if (cfl!=null && cfl.length>0) {
                            for(SafFile3 cf:cfl) {
                                if (gp.settingScanHiddenFile || !cf.isHidden()) {
                                    if (cf.isDirectory()) {
                                        if (gp.settingScanHiddenFile || !hasContainedNomediaFile(lf)) {
                                            if (!cf.getName().equals(".thumbnails")) {
                                                if (process_sub_directories)
                                                    getFileApiAllPictureFileInDirectory(gp, fl, cf, process_sub_directories);
                                            }
                                        } else {
                                            break;
                                        }
                                    } else {
                                        if (isPictureFile(gp, cf.getName())) fl.add(cf);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (isPictureFile(gp, lf.getPath())) fl.add(lf);
                }
            }
        }
    };

    static public void getAllPictureDirectoryInDirectory(GlobalParameters gp,
        ArrayList<SafFile3>fl, SafFile3 lf, boolean process_sub_directories, ContentProviderClient cpc) {
		
		if (gp.settingScanHiddenFile || (!lf.isHidden() && !hasContainedNomediaFile(lf, cpc))) {
			if (lf.isDirectory(cpc)) {
				if (!isDirectoryToBeExcluded(gp, lf)) {
					SafFile3[] cfl=lf.listFiles(cpc);
					if (cfl!=null && cfl.length>0) {
						boolean pic_file_exist=false;
						for(SafFile3 cf:cfl) {
							if (gp.settingScanHiddenFile || !cf.isHidden()) {
								if (cf.isDirectory(cpc)) {
									if (gp.settingScanHiddenFile || !hasContainedNomediaFile(lf, cpc)) {
										if (process_sub_directories) 
											getAllPictureDirectoryInDirectory(gp, fl, cf, process_sub_directories, cpc);
									} else {
										break;
									}
								} else {
									if (!pic_file_exist && isPictureFile(gp, cf.getPath()))
										pic_file_exist=true;
								}
							}
						}
						if (pic_file_exist) {
							fl.add(lf);
						}
					}
				}
			}
		}
	};

    static public void getAllPictureDirectoryInDirectory(GlobalParameters gp,
                                                         ArrayList<SafFile3>fl, SafFile3 lf, boolean process_sub_directories) {
        if (gp.settingScanHiddenFile || (!lf.isHidden() && !hasContainedNomediaFile(lf))) {
            if (lf.isDirectory()) {
                if (!isDirectoryToBeExcluded(gp, lf)) {
                    SafFile3[] cfl=lf.listFiles();
                    if (cfl!=null && cfl.length>0) {
                        boolean pic_file_exist=false;
                        for(SafFile3 cf:cfl) {
                            if (gp.settingScanHiddenFile || !cf.isHidden()) {
                                if (cf.isDirectory()) {
                                    if (gp.settingScanHiddenFile || !hasContainedNomediaFile(lf)) {
                                        if (process_sub_directories)
                                            getAllPictureDirectoryInDirectory(gp, fl, cf, process_sub_directories);
                                    } else {
                                        break;
                                    }
                                } else {
                                    if (!pic_file_exist && isPictureFile(gp, cf.getPath()))
                                        pic_file_exist=true;
                                }
                            }
                        }
                        if (pic_file_exist) {
                            fl.add(lf);
                        }
                    }
                }
            }
        }
    };

    static public void getFileApiAllPictureDirectoryInDirectory(GlobalParameters gp,
                                                         ArrayList<SafFile3>fl, File lf, boolean process_sub_directories) {
        if (gp.settingScanHiddenFile || (!lf.isHidden() && !hasContainedNomediaFile(lf))) {
            if (lf.isDirectory()) {
                if (!isDirectoryToBeExcluded(gp, lf.getPath())) {
                    File[] cfl=lf.listFiles();
                    if (cfl!=null && cfl.length>0) {
                        boolean pic_file_exist=false;
                        for(File cf:cfl) {
                            if (gp.settingScanHiddenFile || !cf.isHidden()) {
                                if (cf.isDirectory()) {
                                    if (gp.settingScanHiddenFile || !hasContainedNomediaFile(lf)) {
                                        if (process_sub_directories)
                                            getFileApiAllPictureDirectoryInDirectory(gp, fl, cf, process_sub_directories);
                                    } else {
                                        break;
                                    }
                                } else {
                                    if (!pic_file_exist && isPictureFile(gp, cf.getPath()))
                                        pic_file_exist=true;
                                }
                            }
                        }
                        if (pic_file_exist) {

                            fl.add(new SafFile3(gp.appContext, lf.getPath()));
                        }
                    }
                }
            }
        }
    };

    public static boolean isPictureFile(GlobalParameters gp, String file_name) {
	    if (file_name.endsWith("/.nomedia")) return false;
        if (file_name.endsWith("/.android_secure")) return false;
		String ft=getFileExtention(getFileName(file_name));
		if (!ft.equals("")) {
            for(String sel_type:gp.settingScanFileType) {
                if (ft.equalsIgnoreCase(sel_type)) {
                    return true;
                }
            }
        } else {
//            try {
//                ExifInterface ei=new ExifInterface(file_name);
//                if (ei!=null) return true;
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
		return false;
	};

	public static boolean isDirectoryToBeExcluded(GlobalParameters gp, SafFile3 sel_dir) {
		boolean result=true;
		String fp=sel_dir.getPath();
		result=isDirectoryToBeExcluded(gp, fp);
		return result;
	};

	public static boolean isDirectoryToBeExcluded(GlobalParameters gp, String fp) {
		boolean result=false;
		for(ScanFolderItem exc_item:gp.settingScanDirectoryList) {
			if (!exc_item.include) {
				if (exc_item.process_sub_directories) {
					if (fp.startsWith(exc_item.folder_path)) {
						result=true;
						break;
					}
				} else {
					if (fp.equals(exc_item.folder_path)) {
						result=true;
						break;
					}
				}
			}
		}
		return result;
	};

	public static boolean isDirectoryToBeProcessed(GlobalParameters gp, String fp) {
		boolean result=false;
		for(ScanFolderItem inc_item:gp.settingScanDirectoryList) {
			if (inc_item.include) {
				if (inc_item.process_sub_directories) {
					if (fp.startsWith(inc_item.folder_path)) {
						result=true;
						break;
					}
				} else {
					if (fp.equals(inc_item.folder_path)) {
						result=true;
						break;
					}
				}
			}
		}
		if (result) {
			if (isDirectoryToBeExcluded(gp, fp)) result=false;
		}
		return result;
	};

	static public Bitmap rotateBitmapByPictureOrientation(Bitmap bitmap, String orientation) {
		Bitmap bmp=bitmap;
		if (!orientation.equals(PictureListItem.EXIF_IMAGE_ORIENTATION_NO_ROTATION)) {
			Matrix mat = new Matrix();
			if (orientation.equals(PictureListItem.EXIF_IMAGE_ORIENTATION_CLOCKWISE_90)) {
				mat.postRotate(90);
			} else if (orientation.equals(PictureListItem.EXIF_IMAGE_ORIENTATION_CLOCKWISE_270)) {
				mat.postRotate(270);
			} else if (orientation.equals(PictureListItem.EXIF_IMAGE_ORIENTATION_FLIP_HORIZONTAL)) {
				mat.preScale(-1.0f, 1.0f);//水平反転
			} else if (orientation.equals(PictureListItem.EXIF_IMAGE_ORIENTATION_FLIP_VERTICAL)) {
				mat.preScale(1.0f, -1.0f);//垂直反転
			}
			bmp= Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
		}
		return bmp;
	};

	static public Bitmap rotateBitmap(Bitmap bitmap, float rotation) {
		Bitmap o_bm=bitmap;
		if (rotation!=0f) {
			Matrix mat = new Matrix();
			mat.postRotate(rotation);
			o_bm= Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
		}
		return o_bm;
	};

	
	final static public byte[] createImageByteArrayWithResize(int max_width, int image_quality,
			SafFile3 sf, String orientation) {
		byte[] bm_result=null;
		try {
			InputStream fis = sf.getInputStream();
			BufferedInputStream bis=new BufferedInputStream(fis,1024*1024*2);
			byte[] bm_file=new byte[(int) fis.available()];//sf.length()];
			bis.read(bm_file);
			bis.close();
			
			BitmapFactory.Options imageOptions = new BitmapFactory.Options();
			imageOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(bm_file,0,bm_file.length, imageOptions);

			float imageScale = (float)imageOptions.outWidth / max_width;

		    BitmapFactory.Options imageOptions2 = new BitmapFactory.Options();
		    imageOptions2.inSampleSize = (int)imageScale;
		    Bitmap bitmap = BitmapFactory.decodeByteArray(bm_file,0,bm_file.length, imageOptions2);
		    
		    Bitmap bmp=rotateBitmapByPictureOrientation(bitmap, orientation);
		    if (bmp!=null) {
                ByteArrayOutputStream bos=new ByteArrayOutputStream();
                bmp.compress(CompressFormat.JPEG, image_quality, bos);
                bos.flush();
                bos.close();
                bitmap.recycle();
                bmp.recycle();
                bm_result=bos.toByteArray();
            } else {
                log.debug("BitmapFactory.decodeByteArray failed, fp="+sf.getPath());
                bmp = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);
                Canvas cv = new Canvas(bmp);
                Paint p = new Paint();
                p.setTextSize(30);
                p.setColor(0xffffffff);
                p.setAntiAlias(true);
                cv.drawText("Unknown File",60f, 100f, p);
                cv.drawText("format",90f, 130f, p);

                ByteArrayOutputStream bos=new ByteArrayOutputStream();
                bmp.compress(CompressFormat.JPEG, image_quality, bos);
                bos.flush();
                bos.close();
                bmp.recycle();
                bm_result=bos.toByteArray();
            }
            log.debug("Image SafFile3="+sf.getPath()+
						", Original Image Size: " + imageOptions.outWidth +
						" x " + imageOptions.outHeight+
						", Scale factor="+imageOptions2.inSampleSize+", bitmap array size="+bm_result.length);

        } catch (Exception e) {
            e.printStackTrace();
		}
		return bm_result;
	};

	final static private byte[] createImageByteArray(GlobalParameters gp, SafFile3 sf) {
//		long b_time=System.currentTimeMillis();
		byte[] bm_result=null;
		try {
			InputStream fis = sf.getInputStream();
			byte[] bm_file=new byte[(int) fis.available()];
			fis.read(bm_file);
			fis.close();
			bm_result=bm_file;
            log.debug("createImageByteArray result="+bm_result+", fp="+sf.getPath());
		} catch (Exception e) {
            log.debug("createImageByteArray error="+e.getMessage()+", fp="+sf.getPath());
//			e.printStackTrace();
		}
		return bm_result;
	};
	
	final static public String createPictureInfo(Context c, PictureListItem pfli) {
		String exif_image_info="";
		
		if (pfli.getExifImageHeight()!=0) {
			String image_size=String.format(
					c.getString(R.string.msgs_main_exif_image_info_size), pfli.getExifImageHeight(), 
					pfli.getExifImageWidth()).concat(", ");
			
			String aperture=pfli.getExifAperture().equals("")?"":
				String.format(c.getString(R.string.msgs_main_exif_image_info_aperture),pfli.getExifAperture()).concat(", ");
			
			String exposure_time=pfli.getExifExposureTime().equals("")?"":
				String.format(c.getString(R.string.msgs_main_exif_image_info_exposure_time),pfli.getExifExposureTime()).concat(", ");

			String exposure_bias="";//Exposure mode=manual

			String exposure_mode="";
			if (pfli.getExifExposureMode().equals(PictureListItem.EXIF_IMAGE_EXPOSURE_MODE_MANUAL)) exposure_mode=c.getString(R.string.msgs_main_exif_image_info_exposure_mode_manual).concat(", ");
			else if (pfli.getExifExposureMode().equals(PictureListItem.EXIF_IMAGE_EXPOSURE_MODE_BRACKET)) exposure_mode=c.getString(R.string.msgs_main_exif_image_info_exposure_mode_bracket).concat(", ");

			String exposure_program="";
			if (!pfli.getExifExposureMode().equals(PictureListItem.EXIF_IMAGE_EXPOSURE_MODE_MANUAL)) {
				exposure_bias=pfli.getExifExposureBias().equals("")?"":
					String.format(c.getString(R.string.msgs_main_exif_image_info_exposure_bias),pfli.getExifExposureBias()).concat(", ");
				
				if (pfli.getExifExposureProgram().equals(PictureListItem.EXIF_IMAGE_EXPOSURE_PROGRAM_MANUAL)) exposure_program=c.getString(R.string.msgs_main_exif_image_info_exposure_program_manual).concat(", ");
				else if (pfli.getExifExposureProgram().equals(PictureListItem.EXIF_IMAGE_EXPOSURE_PROGRAM_NORMAL_PROGRAM)) exposure_program=c.getString(R.string.msgs_main_exif_image_info_exposure_program_normal).concat(", ");
				else if (pfli.getExifExposureProgram().equals(PictureListItem.EXIF_IMAGE_EXPOSURE_PROGRAM_APERTURE_PRIORITY)) exposure_program=c.getString(R.string.msgs_main_exif_image_info_exposure_program_aperture).concat(", ");
				else if (pfli.getExifExposureProgram().equals(PictureListItem.EXIF_IMAGE_EXPOSURE_PROGRAM_SHUTTER_PRIORITY)) exposure_program=c.getString(R.string.msgs_main_exif_image_info_exposure_program_shutter).concat(", ");
				else if (pfli.getExifExposureProgram().equals(PictureListItem.EXIF_IMAGE_EXPOSURE_PROGRAM_CREATIVE_PROGRAM)) exposure_program=c.getString(R.string.msgs_main_exif_image_info_exposure_program_creative).concat(", ");
				else if (pfli.getExifExposureProgram().equals(PictureListItem.EXIF_IMAGE_EXPOSURE_PROGRAM_ACTION_PROGRAM)) exposure_program=c.getString(R.string.msgs_main_exif_image_info_exposure_program_action).concat(", ");
				else if (pfli.getExifExposureProgram().equals(PictureListItem.EXIF_IMAGE_EXPOSURE_PROGRAM_PORTRAIT_MODE)) exposure_program=c.getString(R.string.msgs_main_exif_image_info_exposure_program_portrait).concat(", ");
				else if (pfli.getExifExposureProgram().equals(PictureListItem.EXIF_IMAGE_EXPOSURE_PROGRAM_LANDSCAPE_MODE)) exposure_program=c.getString(R.string.msgs_main_exif_image_info_exposure_program_landscape).concat(", ");
				else if (pfli.getExifExposureMode().equals(PictureListItem.EXIF_IMAGE_EXPOSURE_MODE_AUTO)) exposure_program=c.getString(R.string.msgs_main_exif_image_info_exposure_mode_auto).concat(", ");				
			}

			String focal_length=pfli.getExifFocalLength().equals("")?"":
				String.format(c.getString(R.string.msgs_main_exif_image_info_focal_length),pfli.getExifFocalLength()).concat(", ");
			
			String iso=pfli.getExifISO().equals("")?"":String.format(
				String.format(c.getString(R.string.msgs_main_exif_image_info_iso), pfli.getExifISO())).concat(", ");
			
			String date_time=pfli.getExifDateTime().equals("0000/00/00 00:00:00")?
				c.getString(R.string.msgs_main_exif_image_info_date_time_unknown):
				String.format(c.getString(R.string.msgs_main_exif_image_info_date_time),pfli.getExifDateTime()).concat(", ");
			
			String dc_desc=pfli.getExifXmpDcDescription().equals("")?"":
				String.format(c.getString(R.string.msgs_main_exif_image_info_dc_description), pfli.getExifXmpDcDescription()).concat(", ");
			
			String dc_subj="";
			if (pfli.getExifXmpDcSubject()!=null) {
				String sep="", subj="";
				for(String item:pfli.getExifXmpDcSubject()) {
					subj+=sep+item;
					sep=", ";
				}
				dc_subj=String.format(c.getString(R.string.msgs_main_exif_image_info_dc_subject), subj).concat(", ");
			}

			String dc_creator=pfli.getExifXmpDcCreator().equals("")?"":
				String.format(c.getString(R.string.msgs_main_exif_image_info_dc_creator), pfli.getExifXmpDcCreator()).concat(", ");
			String xmp_label=pfli.getExifXmpXmpLabel().equals("")?"":
				String.format(c.getString(R.string.msgs_main_exif_image_info_xmp_label), pfli.getExifXmpXmpLabel()).concat(", ");
			String xmp_rating=pfli.getExifXmpXmpRating().equals("")?"":
				String.format(c.getString(R.string.msgs_main_exif_image_info_xmp_rating), pfli.getExifXmpXmpRating()).concat(", ");

			String no_of_shutter_released="";
			no_of_shutter_released=pfli.getExifNumberOfShutterRelased()>0?
					String.format(c.getString(R.string.msgs_main_exif_number_of_shutter_released), pfli.getExifNumberOfShutterRelased()).concat(", "):
						"";
			
			exif_image_info=String.format("%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s", image_size, 
					date_time, aperture, exposure_time, exposure_bias,  
					focal_length, iso, exposure_mode, exposure_program, 
					no_of_shutter_released, pfli.getExifModel().concat(", "),
					dc_desc, dc_subj, dc_creator, xmp_label, xmp_rating
					).trim();
			int cnt=0;
			while(exif_image_info.endsWith(",")) {
				exif_image_info=exif_image_info.substring(0, exif_image_info.length()-1).trim();
				cnt++;
				if (cnt>10) break;
			}
		}
		
		return exif_image_info;
	};
	
	public static String invokeWallPaperEditor(Context c, String pic_file_path) {
		SafFile3 sf = new SafFile3(c, pic_file_path);
		String result="";
		Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(sf.getUri(), "image/*");
	    try {
		    c.startActivity(intent);
	    } catch(Exception e) {
//	    	e.printStackTrace();
	    	result=e.getMessage();
	    }
	    return result;
	};

	public static Bitmap sharpen(Bitmap src, double weight, double factor, double offset) {
	    double[][] SharpConfig = new double[][] {
	        { 0 , -2    , 0  },
	        { -2, weight, -2 },
	        { 0 , -2    , 0  }
	    };
	    ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
	    convMatrix.applyConfig(SharpConfig);
	    convMatrix.Factor = factor;//weight - 8;
	    convMatrix.Offset = offset;
	    return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
	}
}
