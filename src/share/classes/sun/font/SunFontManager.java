package sun.font;

import java.io.FilenameFilter;

/**
 * Created by cryoc on 2016-10-11.
 */
@Deprecated
public class SunFontManager {
  private String defaultFontFile;
  private String defaultFontFaceName;

  public String getDefaultFontFile() {
    return defaultFontFile;
  }

  public String getDefaultFontFaceName() {
    return defaultFontFaceName;
  }

  public void registerFontsInDir(String fallbackDirName) {
    // TODO
  }

  public FilenameFilter getTrueTypeFilter() {
    // TODO
    return null;
  }

  public FilenameFilter getType1Filter() {
    // TODO
    return null;
  }

  public boolean usingAlternateFontforJALocales() {
    // TODO
    return false;
  }
}
