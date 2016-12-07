package sun.java2d.cmm;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;

/**
 * Partial reimplementation of the OpenJDK class for use by SkinJob.
 */
public enum PCMM {
  INSTANCE;

  public static ColorSpace LINEAR_RGBspace;
  public static ColorSpace GRAYspace;

  public int getTagSize(Profile p, int tagSignature) {
    // TODO
    return 0;
  }

  public void getTagData(Profile p, int tagSignature, byte[] tagData) {
    // TODO
  }

  public int getProfileSize(Profile cmmProfile) {
    // TODO
    return 0;
  }

  public void getProfileData(Profile cmmProfile, byte[] profileData) {
    // TODO
  }

  public void setTagData(Profile cmmProfile, int tagSignature, byte[] tagData) {
    // TODO
  }

  public Profile loadProfile(byte[] profileData) {
    return new Profile(profileData);
  }

  public ColorTransform createTransform(ICC_Profile profile, int any, int in) {
    // TODO
    return null;
  }

  public ColorTransform createTransform(ColorTransform[] transformList) {
    // TODO
    return null;
  }
}
