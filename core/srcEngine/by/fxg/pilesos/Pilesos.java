package by.fxg.pilesos;

public class Pilesos {
	private static Apparat<?> app;
	
	public static void setApp(Apparat<?> inst) { app = inst; }
	public static Apparat<?> getApp() { return app; }
}
