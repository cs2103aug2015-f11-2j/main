package app.constants;

import app.Main;

public class ViewConstants {

	public static String STATUS_STYLE_SUCCESS = "success";
	public static String STATUS_STYLE_ERROR = "error";
	public static String STATUS_STYLE_INFO = "info";

	public static String THEME_LIGHT_CSS = Main.class.getResource("view/css/theme_light.css").toExternalForm();
	public static String THEME_DARK_CSS = Main.class.getResource("view/css/theme_dark.css").toExternalForm();

	public enum StatusType {
		SUCCESS, ERROR, INFO;
	}
}
