package by.fxg.speceditor.api.addon;

public class AddonInfo {
	public String name;
	public String addonID;
	public String version;
	@Deprecated /** FIXME Dependencies are not implemented, so implement them **/
	public String dependencies;
	public String[] authors;
	
	public static AddonInfo create(String name, String addonID, String version, String dependencies, String... authors) {
		AddonInfo info = new AddonInfo();
		info.name = name;
		info.addonID = addonID;
		info.version = version;
		info.dependencies = dependencies;
		info.authors = authors;
		return info;
	}
}
