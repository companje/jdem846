package us.wthr.planets;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GLProfile;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.Lists;



public class ObjectLoader {
	
	
	public static List<Renderable> loadObjects(String objectConfigJson, GLProfile glProfile) throws IOException
	{
		List<Renderable> objectList = Lists.newArrayList();
		InputStream in = ObjectLoader.class.getResourceAsStream(objectConfigJson);
		String jsonTxt = IOUtils.toString( in );
		in.close();
		
		JSONObject json = null;
		try {
			json = (JSONObject) JSONSerializer.toJSON( jsonTxt );
		} catch (JSONException ex) {
			ex.printStackTrace();
			throw ex;
		}
		
		JSONArray objectsArray = json.getJSONArray("objects");
		for (int i = 0; i < objectsArray.size(); i++) {
			JSONObject objectObj = objectsArray.getJSONObject(i);
			
			Renderable object = buildBaseObject(objectObj);
			if (object != null) {
				objectList.add(object);
			}
		}
		
		return objectList;
	}
	
	protected static Renderable buildBaseObject(JSONObject objectObj)
	{
		String name = objectObj.getString("name");
		String id = objectObj.getString("id");
		
		String surfaceTexture = (objectObj.has("textures.surface")) ? objectObj.getString("textures.surface") : null;
		String cloudTexture = (objectObj.has("textures.clouds")) ? objectObj.getString("textures.clouds") : null;
		
		return null;
	}
}
