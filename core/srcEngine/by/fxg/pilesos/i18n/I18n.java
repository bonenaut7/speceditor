package by.fxg.pilesos.i18n;

import java.util.HashMap;
import java.util.Map;

public class I18n {
	public static String language = "en";
	public static Map<String, I18nPool> map = new HashMap<>();
	static { createPool(language); }
	
	public static void setLanguage(String key) { language = map.containsKey(key) ? key : "en"; }
	public static String get(String code) { return map.containsKey(language) ? map.get(language).getString(code) : map.get("en").getString(code); }
	public static I18nPool getPool(String language) { return map.get(language); }
	public static void createPool(String language) { map.put(language, new I18nPool(language)); }
	public static void addPool(I18nPool pool) { if (pool != null) map.put(pool.language, pool); }
	
	public static class I18nPool {
		public String language;
		public Map<String, String> map = new HashMap<>();
		
		public I18nPool(String language) {
			this.language = language;
		}
		
		public String getString(String code) {
			return this.map.containsKey(code) ? this.map.get(code) : code;
		}
		
		public I18nPool add(String code, String value) {
			this.map.put(code, value);
			return this;
		}
	}
}
