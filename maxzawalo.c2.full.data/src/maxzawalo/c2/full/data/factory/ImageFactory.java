package maxzawalo.c2.full.data.factory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import maxzawalo.c2.free.bo.Settings;
import maxzawalo.c2.free.cache.Cache;

public class ImageFactory {

	public Map<Integer, List<File>> getImages() {
		Map<Integer, List<File>> images = new HashMap<>();
		String key = "Product.Images";
		images = (Map<Integer, List<File>>) Cache.I().get(key);
		if (images == null) {
			images = new HashMap<>();
			File[] imgs = new File(Settings.imagesPath()).listFiles();
			for (File i : imgs) {
				int id = Integer.parseInt(i.getName().split("_")[0]);
				if (!images.containsKey(id))
					images.put(id, new ArrayList<>());

				List<File> list = images.get(id);
				list.add(i);
				images.put(id, list);
			}

			Cache.I().put(key, images, Cache.heatingCacheTimeSec);
		}
		return images;
	}

	public boolean HasImage(int id) {
		Map<Integer, List<File>> images = getImages();
		return images.containsKey(id);
	}

	public List<File> GetProductImages(int id) {
		Map<Integer, List<File>> images = getImages();
		List<File> imgList = images.get(id);
		if (imgList == null)
			imgList = new ArrayList<>();
		return imgList;
	}
}