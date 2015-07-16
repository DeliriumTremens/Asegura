package com.lorant.mobile.android.util;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import com.lorant.mobile.android.constant.Config;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Base64;
import android.widget.ImageView;

public class ImageUtils {
	
  public static Bitmap getImageFromUri(Uri uri, Context ctx) 
		                      throws FileNotFoundException {
    BitmapFactory.Options o = new BitmapFactory.Options();
    BitmapFactory.Options o2 = null;
    Bitmap result = null;
    int width_tmp = 0, height_tmp = 0;
    int factor = 1;
    o.inJustDecodeBounds = true;
    BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri)
    		                                                     , null, o);
    width_tmp = o.outWidth;
    height_tmp = o.outHeight;
    while (true) {
      if(width_tmp / 2 < Config.MAX_IMAGE_SIZE 
           || height_tmp / 2 < Config.MAX_IMAGE_SIZE) {
        break;
      }
      width_tmp /= 2;
      height_tmp /= 2;
      factor *= 2;
    }
    o2 = new BitmapFactory.Options();
    o2.inSampleSize = factor;
    result = BitmapFactory.decodeStream(ctx.getContentResolver()
    		                   .openInputStream(uri), null, o2);
    return rotate(ctx, result, uri);
  }
 
  
  public static String getBASE64Encoding(ImageView imageView){
	Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
	return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
  }
  
  public static String getRealPathFromURI(Uri contentUri, Context ctx) {
	String[] proj = { MediaStore.Images.Media.DATA };
	CursorLoader cursorLoader = new CursorLoader(ctx, contentUri, proj, null
			                                                  , null, null);        
	Cursor cursor = cursorLoader.loadInBackground();  
	if(contentUri.toString().startsWith("file://")){
		return contentUri.toString();
	}
	int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	cursor.moveToFirst();
	return cursor.getString(column_index); 
  }
  
  
  public static Bitmap getResizedBitmap(Bitmap bm, int size) {
    int width = bm.getWidth();
    int height = bm.getHeight();
    float scaleWidth = ((float) size) / width;
    float scaleHeight = ((float) size) / height;
    Matrix matrix = new Matrix();
    matrix.postScale(scaleWidth, scaleHeight); 
    Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    return resizedBitmap;
  }
  
  public static Bitmap rotate(Context ctx, Bitmap bm, String filePath) {
	try {
	     ExifInterface exif = new ExifInterface(filePath);
	     Matrix mat = new Matrix();
	     int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION
	        		                          , ExifInterface.ORIENTATION_NORMAL);
	     int angle = 0;
	     switch (orientation) {
	       case ExifInterface.ORIENTATION_ROTATE_90:
	            angle = 90;
	            break;
	       case ExifInterface.ORIENTATION_ROTATE_180:
	            angle = 180;
	            break;
	       case ExifInterface.ORIENTATION_ROTATE_270:
	            angle = 270;
	            break;
	       default:
	            angle = 0;
	            break;
	      }
	      mat.postRotate(angle);
	      return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight()
	        		                                           , mat, true);
	} catch (Exception e) {
	    //e.printStackTrace();
	}
	return null;	  
  }
  
  public static Bitmap rotate(Context ctx, Bitmap bm, Uri imageUri) {
    return rotate(ctx,bm, getRealPathFromURI(imageUri, ctx) );
  }

  
  public static Uri getImageUri(Context inContext, Bitmap inImage) {
	ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
	String path = Images.Media.insertImage(inContext.getContentResolver()
			                                   , inImage, "Title", null);
	return Uri.parse(path);
  }
  
  public static boolean isWebUri(Uri uri){
	boolean isWeb = false;
	if(uri != null){
	  isWeb = uri.toString().startsWith("http:/");
	}
	return isWeb;
  }
}
