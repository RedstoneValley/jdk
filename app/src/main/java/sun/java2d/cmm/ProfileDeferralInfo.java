package sun.java2d.cmm;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by chris on 12/6/2016.
 */
public class ProfileDeferralInfo extends BufferedInputStream {
  public String filename;
  public int colorSpaceType;
  public int numComponents;
  public int profileClass;

  public ProfileDeferralInfo(String filename, int typeXyz, int numComponents, int profileClass) {
    super(createFileInputStream(filename));
    colorSpaceType = typeXyz;
    this.filename = filename;
    this.numComponents = numComponents;
    this.profileClass = profileClass;
  }

  private static InputStream createFileInputStream(String filename) {
    try {
      return new FileInputStream(filename);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
