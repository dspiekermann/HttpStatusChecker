package de.dsp.android.hsc.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.dsp.android.hsc.CheckObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class HscApplicationPreferences {

	private static final String PREF_KEY_INIT = "init";

	private static final String PREF_KEY_PREFIX_NAME = "name_";
	private static final String PREF_KEY_PREFIX_URL = "url_";
	
	private Context context;
	private SharedPreferences p;
	
	private List<CheckObject> checkObject = new ArrayList<CheckObject>();
	
	private static final Map<String, String> DEFAULT_URLS = new HashMap<String, String>();
	
	private HscApplicationPreferences(Context context){
		this.context = context;
	}
	
	private void init(){
        p = context.getSharedPreferences(CommonConstants.PREFERENECES_APPLICATION, Context.MODE_PRIVATE);
//		if (!p.getBoolean(PREF_KEY_INIT, false)){
			setDefaultPreferences();
//		}
		fillCheckObjects();
	}
	
	private static void initDefaultUrls(){
		DEFAULT_URLS.put("Fremdportlet-Service Abna", "https://centralparkabna.sparkasse-koelnbonn.de/axis2/services/SkbFPWebservice?wsdl");
		DEFAULT_URLS.put("Fremdportlet-Service Prod", "https://centralpark.sparkasse-koelnbonn.de/axis2/services/SkbFPWebservice?wsdl");
		DEFAULT_URLS.put("Kontowecker-Service Prod", "https://www.s-mobilebanking.de/koeln/ktw/services/InfoService?wsdl");
		DEFAULT_URLS.put("Kontowecker-Service Abna", "https://www.rheinlandmobil.de/koeln/ktw/services/InfoService?wsdl");
		
	}
	
	private void setDefaultPreferences(){
		Editor edit = p.edit();
		edit.clear();
		
		initDefaultUrls();
		
		int i = 0;
		for (Entry<String, String> entry : DEFAULT_URLS.entrySet()) {
			edit.putString(PREF_KEY_PREFIX_NAME + i, entry.getKey());
			edit.putString(PREF_KEY_PREFIX_URL + i, entry.getValue());
			i++;
		}
		edit.putBoolean(PREF_KEY_INIT, true);
		edit.commit();
	}
	
	private void fillCheckObjects(){
		int i = 0;
		while (p.contains(PREF_KEY_PREFIX_NAME + i)){
			checkObject.add(new CheckObject(i, p.getString(PREF_KEY_PREFIX_NAME + i, "undefined"), p.getString(PREF_KEY_PREFIX_URL + i, "undefined")));
			i++;
		}
	}
	
	public static synchronized HscApplicationPreferences getInstance(Context context){
		HscApplicationPreferences instance = new HscApplicationPreferences(context);
		instance.init();
		return instance;
	}

	public List<CheckObject> getCheckObjects(){
		return checkObject;
	}
}
