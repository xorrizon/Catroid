package at.tugraz.ist.catroid.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.utils.filesystem.FileSystem;
/**
 * 
 * @author AlexanderKalchauer
 *	This class contains Images in different sizes for the Images in the root ordner to use in scratch
 */

public class ImageContainer {

	private static final int MAX_WIDTH = (460*3);
	private static final int MAX_HEIGHT = (800*3);
	private static final int THUMBNAIL_WIDTH = 100;
	private static final int THUMBNAIL_HEIGHT = 100;
	static String ROOTPATH;
	private HashMap<String, Bitmap> mImageMap;
	private FileSystem mFilesystem;
	private static ImageContainer mImageContainer =null;
	
	private ImageContainer() {
		mImageMap = new HashMap<String, Bitmap>();
		mFilesystem = new FileSystem();

	}
	
	public static ImageContainer getInstance() {
		if (mImageContainer == null) {
			mImageContainer = new ImageContainer();
		}
		return mImageContainer;
	}
	
	public void setRootPath(String rootpath){
		ROOTPATH = rootpath;
	}
	

	public String saveImageFromPath(String path){
		File imagePath = new File(path);
		String folderPath = imagePath.getParent();
		String image = Calendar.getInstance().getTimeInMillis() + imagePath.getAbsolutePath().replace(folderPath, "").replace("/", "");

		Bitmap bm = null;		
		bm = BitmapFactory.decodeFile((path));
		
		return saveImageFromBitmap(bm, image, true);
	}
	
	public String saveImageFromBitmap(Bitmap bm, String name, boolean recycle){
		name = Utils.changeFileEndingToPng(name);
		Bitmap newbm = null;
		if(MAX_HEIGHT < bm.getHeight() && MAX_WIDTH < bm.getWidth())
		   newbm = ImageEditing.scaleBitmap(bm, MAX_HEIGHT, MAX_WIDTH);
		if(MAX_HEIGHT >= bm.getHeight() && MAX_WIDTH < bm.getWidth())
		   newbm = ImageEditing.scaleBitmap(bm, bm.getHeight(), MAX_WIDTH);
		if(MAX_HEIGHT < bm.getHeight() && MAX_WIDTH >= bm.getWidth())
		   newbm = ImageEditing.scaleBitmap(bm, MAX_HEIGHT, bm.getWidth());
		if(MAX_HEIGHT >= bm.getHeight() && MAX_WIDTH >= bm.getWidth())
		   newbm = bm;
		
		final String path = Utils.concatPaths(ConstructionSiteActivity.ROOT_IMAGES, name);
		final Bitmap newbmToSave = newbm;
		final Boolean recycleBm = recycle;
		new Thread(new Runnable() {
			
			public void run() {
				Utils.saveBitmapOnSDCardAsPNG(path, newbmToSave);
				if(newbmToSave != null && recycleBm)
					newbmToSave.recycle();
			}
		}).start();
		
		
		
		if(bm != null && bm != newbm && recycle)
			bm.recycle();
		return name;
	}
	
	public String saveThumbnailFromPath(String path){
		File imagePath = new File(path);
		String folderPath = imagePath.getParent();
		String image = Calendar.getInstance().getTimeInMillis() + imagePath.getAbsolutePath().replace(folderPath, "").replace("/", "thumb");

		Bitmap bm = null;
		bm = BitmapFactory.decodeFile((path));
		return saveThumbnailFromBitmap(bm, image, true);
	}
	
	public String saveThumbnailFromBitmap(Bitmap bm, String name, boolean recycle){
		name = Utils.changeFileEndingToPng(name);
		Bitmap newbm = null;
		newbm = ImageEditing.scaleBitmap(bm, THUMBNAIL_HEIGHT, THUMBNAIL_WIDTH);
		mImageMap.put(name, newbm);
		
		final String path = Utils.concatPaths(ConstructionSiteActivity.ROOT_IMAGES, name);
		final Bitmap newbmToSave = newbm;
		new Thread(new Runnable() {
			
			public void run() {
				Utils.saveBitmapOnSDCardAsPNG(path, newbmToSave);
			}
		}).start();
		
		
		
		if(bm != null && bm != newbm && recycle)
			bm.recycle();
			
		return name;
	}
	

	public Bitmap getImage(String name){
		if(!mImageMap.containsKey(name)){
			Bitmap bm = BitmapFactory.decodeFile(getFullImagePath(name));
			String a = getFullImagePath(Utils.concatPaths(ConstructionSiteActivity.ROOT_IMAGES, name));
			if(bm != null)
				mImageMap.put(name, bm);
		}
		
		return mImageMap.get(name);
	}
	
	private String getFullImagePath(String path){
		return (Utils.concatPaths(ROOTPATH, path));
	}

	public void deleteImage(String name){
		mFilesystem.deleteFile(getFullImagePath(name), null);
		mImageMap.remove(name);
	}
	
	public void deleteAll(){
		mImageMap.clear();
	}
	


}