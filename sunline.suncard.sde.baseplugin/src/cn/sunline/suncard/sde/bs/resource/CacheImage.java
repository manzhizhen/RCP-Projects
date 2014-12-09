package cn.sunline.suncard.sde.bs.resource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class CacheImage {

	private final Map<String, Image> imageMap = new HashMap<String, Image>();
	
	private static CacheImage cacheImage = new CacheImage();
	
	private CacheImage() {
		
	}
	
	public static CacheImage getCacheImage() {
		return cacheImage;
	}
	
	public Image getImage(String applicationId, String imageName) {
		if(imageName == null) {
			return null;
		}
		Image image = imageMap.get(imageName);
		if(image == null) {
			image = AbstractUIPlugin.imageDescriptorFromPlugin(applicationId, imageName).createImage();
			imageMap.put(imageName, image);
		}
		return image;
	}
	
	public void dispose() {
		Iterator<Image> iterator = imageMap.values().iterator();
		while(iterator.hasNext()) {
			iterator.next().dispose();
		}
		imageMap.clear();
	}
}
