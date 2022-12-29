import com.formdev.flatlaf.FlatDarkLaf;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ForkJoinPool;
import java.util.prefs.Preferences;

public class Main
{
	public static ForkJoinPool pool = new ForkJoinPool(16);
	public static void main(String[] args)
	{
		configure();
		new Frame();
	}

	public static void configure()
	{
		Preferences pref = Preferences.userNodeForPackage(Main.class);
		FlatDarkLaf.install();
		System.setProperty("sun.java2d.uiScale", "1");
		System.setProperty("sun.java2d.opengl", "true");
		System.setProperty("sun.java2d.noddraw", Boolean.TRUE.toString());
		ProcessBuilder processBuilder = new ProcessBuilder();
	}
}
