package com.tertiary.verify.data_model.model.constants;

import java.io.Serializable;
import java.net.URL;
import java.text.SimpleDateFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConstants implements Serializable {

  /** The Constant sdf. */
  public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

  /** The Constant sdf2. */
  public static final SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");

  /** The app path. */
  // public static final String path = "src/main/webapp/images/" ;
  /** The Constant sdf3. */
  public static final SimpleDateFormat sdf3 = new SimpleDateFormat("dd MMMM yyyy (hh:mm a)");

  /** The Constant sdf4. */
  public static final SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  /** The Constant sdfYYYY. */
  public static final SimpleDateFormat sdfYYYY = new SimpleDateFormat("yyyy");

  /** The Constant sdfYYYY. */
  public static final SimpleDateFormat sddfMMMMyyyy = new SimpleDateFormat("MMMM yyyy");

  /** The Constant telephone format. */
  public static final String TELPHONE_FORMAT = "099 999 9999";

  public static final Long YES_ID = 1L;
  public static final Long NO_ID = 2L;
  public static final String POSSIBLE_TAGS = "#NAME# #SURNAME# #COMPANY_NAME#";
  public static final String FROM_EMAIL = "info@tertiaryverify.co.za";
  public static final String APP_NAME = "Tertiary Verify";

  /** The Constant logger. */
  protected static final Log logger = LogFactory.getLog(AppConstants.class);

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  public final String realpath = getPath();
  public final String webappPath = getWebappPath();
  public final String lookupsPath = getLookupsPath();
  public final String reportsPath = getReportPath();

  public String getPath() {
    URL resource = getClass().getResource("/");
    String path = resource.getPath();

    // path=path.replace("target/classes/", "src/main/webapp");
    return path;
  }

  public String getWebappPath() {
    String path = getPath().replace("target/classes/", "src/main/webapp");
    return path;
  }

  public String getLookupsPath() {
    /* /C:/Users/Christoph%20Sibiya/Documents/mopanefsProject/MopaneFS/src/main/webapp */
    String path = getPath().replace("target/classes/", "src/main/webapp");
    if (path.contains("%20")) {
      path = path.replaceAll("%20", " ");
    }
    if (String.valueOf(path.charAt(0)).equalsIgnoreCase("/")
        || String.valueOf(path.charAt(0)).equalsIgnoreCase("\\")) {
      path = path.replaceFirst(String.valueOf(path.charAt(0)), "");
    }

    path = path + "/admin/lookups";

    return path;
  }

  public String getReportPath() {
    /* /C:/Users/Christoph%20Sibiya/Documents/mopanefsProject/MopaneFS/src/main/webapp */
    String path = getPath().replace("target/classes/", "src/main/webapp");
    if (path.contains("%20")) {
      path = path.replaceAll("%20", " ");
    }
    if (String.valueOf(path.charAt(0)).equalsIgnoreCase("/")
        || String.valueOf(path.charAt(0)).equalsIgnoreCase("\\")) {
      path = path.replaceFirst(String.valueOf(path.charAt(0)), "");
    }

    path = path + "/reports";

    return path;
  }

  public String getReportDocPath() {
    /* /C:/Users/Christoph%20Sibiya/Documents/mopanefsProject/MopaneFS/src/main/webapp */
    String path = getPath().replace("target/classes/", "src/main/webapp");
    if (path.contains("%20")) {
      path = path.replaceAll("%20", " ");
    }
    if (String.valueOf(path.charAt(0)).equalsIgnoreCase("/")
        || String.valueOf(path.charAt(0)).equalsIgnoreCase("\\")) {
      path = path.replaceFirst(String.valueOf(path.charAt(0)), "");
    }

    path = path + "/docs";

    return path;
  }
}
