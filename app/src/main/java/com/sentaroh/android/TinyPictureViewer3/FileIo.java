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
import android.media.MediaScannerConnection;

import com.sentaroh.android.Utilities3.Dialog.ProgressSpinDialogFragment;
import com.sentaroh.android.Utilities3.SafFile3;
import com.sentaroh.android.Utilities3.ThreadCtrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class FileIo {
    private static final Logger log= LoggerFactory.getLogger(FileIo.class);
	private final static int BUFFER_SIZE=1024*1024*4;

    static public void scanMediaFile(GlobalParameters gp, CommonUtilities util, String fp) {
//        MediaScannerConnection.scanFile(gp.appContext, new String[]{fp}, null, null);
//        util.addDebugMsg(2, "I","Media scanner invoked, name=",fp);
    };

    static public boolean deleteLocalItem(GlobalParameters gp, CommonUtilities util, SafFile3 del_item) {
		boolean result=false;
		if (del_item.exists()) {
			if (del_item.isDirectory()) {
				SafFile3[] del_list=del_item.listFiles();
				if (del_list!=null && del_list.length>0) {
					for(SafFile3 child_item:del_list) {
						result=deleteLocalItem(gp, util, child_item);
						if (!result) break;
					}
					if (result) {
					    result=del_item.delete();
					    if (result) {
                            scanMediaFile(gp, util, del_item.getPath());
                        }
                    }
				} else {
					result=del_item.delete();
                    if (result) {
                        scanMediaFile(gp, util,  del_item.getPath());
                    }
				}
			} else {
				result=del_item.delete();
                if (result) {
                    scanMediaFile(gp, util,  del_item.getPath());
                }
			}
		}
		return result;
	}

    static public boolean moveLocalToLocal(GlobalParameters gp, CommonUtilities util,
                                           ProgressSpinDialogFragment psdf, ThreadCtrl tc,
                                           ArrayList<String>ccl, String to_dir) {
        boolean result=false;
        for(String item:ccl) {
            if (!tc.isEnabled()) break;
            result=copyMoveFile(true, gp, util, psdf, tc, item, to_dir);
        }
        return result;
    };

	static public boolean copyLocalToLocal(GlobalParameters gp, CommonUtilities util,
                                           ProgressSpinDialogFragment psdf, ThreadCtrl tc,
                                           ArrayList<String>ccl, String to_dir) {
		boolean result=false;
		for(String f_path:ccl) {
			if (!tc.isEnabled()) break;
			result=copyMoveFile(false, gp, util, psdf, tc, f_path, to_dir);
		}
		return result;
	}

    static private boolean copyMoveFile(boolean move, GlobalParameters gp, CommonUtilities util,
                                 ProgressSpinDialogFragment psdf, ThreadCtrl tc,
                                 String from_path, String to_dir) {
        boolean result=false;
        SafFile3 in_file=null;
        SafFile3 out_file_tmp=null;
        OutputStream output_stream_tmp=null;
        try {
            boolean move_required=false;
            in_file=new SafFile3(gp.appContext, from_path);
            SafFile3 df=new SafFile3(gp.appContext, to_dir);
            if (df.getAppDirectoryCache()!=null) {
                move_required=true;
//                    output_stream_tmp=new FileOutputStream(new File(in_file.getAppDirectoryCache()+"/"+in_file.getName()));
                out_file_tmp=new SafFile3(gp.appContext, df.getAppDirectoryCache()+"/"+in_file.getName());
                out_file_tmp.createNewFile();
            } else {
                out_file_tmp=new SafFile3(gp.appContext, to_dir+"/"+in_file.getName()+".temp_file.tmp");
                out_file_tmp.createNewFile();
            }
            InputStream fis=in_file.getInputStream();
            OutputStream fos=out_file_tmp.getOutputStream();
            byte[] buff=new byte[BUFFER_SIZE];
            int rc=0;
            while((rc=fis.read(buff))>0) {
                if (!tc.isEnabled()) {
                    break;
                } else {
                    fos.write(buff, 0, rc);
                }
            }
            fos.flush();
            fos.close();
            fis.close();
            if (!tc.isEnabled()) {
                out_file_tmp.delete();
                String msg="";
                if (move) String.format(gp.appContext.getString(R.string.msgs_main_file_move_cancel), in_file.getName());
                else String.format(gp.appContext.getString(R.string.msgs_main_file_copy_cancel), in_file.getName());
                psdf.updateMsgText(msg);
                util.addLogMsg("I", CommonUtilities.getExecutedMethodName()+" "+msg);
                return false;
            }

            if (move_required) {
                File lf=new File(df.getAppDirectoryCache()+"/"+in_file.getName());
                lf.setLastModified(in_file.lastModified());
            }
            if (!df.exists()) df.mkdirs();
            SafFile3 out_file=new SafFile3(gp.appContext, to_dir+"/"+in_file.getName());
            out_file.deleteIfExists();
            if (move_required) result=out_file_tmp.moveTo(out_file);
            else result=out_file_tmp.renameTo(out_file);
            scanMediaFile(gp, util, out_file.getPath());
            String msg="";
            if (move) {
                result=in_file.delete();
                if (result) {
                    String.format(gp.appContext.getString(R.string.msgs_main_file_move_success), in_file.getName());
                    scanMediaFile(gp, util, in_file.getPath());
                } else String.format(gp.appContext.getString(R.string.msgs_main_file_move_fail), in_file.getName());
            } else {
                if (result) String.format(gp.appContext.getString(R.string.msgs_main_file_copy_success), in_file.getName());
                else String.format(gp.appContext.getString(R.string.msgs_main_file_copy_fail), in_file.getName());
            }
            psdf.updateMsgText(msg);
            util.addLogMsg("I", CommonUtilities.getExecutedMethodName()+" "+msg);
        } catch(Exception e) {
            util.addLogMsg("I", CommonUtilities.getExecutedMethodName()+" failed. error="+e.getMessage()+"\n"+CommonUtilities.getStackTraceElement(e));
            return false;
        }
        return true;
    };

}
