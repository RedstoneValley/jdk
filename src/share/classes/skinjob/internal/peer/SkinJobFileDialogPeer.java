package skinjob.internal.peer;

import java.awt.FileDialog;
import java.awt.peer.FileDialogPeer;
import java.io.FilenameFilter;

/**
 * Created by cryoc on 2016-10-11.
 */
public class SkinJobFileDialogPeer extends SkinJobWindowPeer<android.view.Window>
    implements FileDialogPeer {
  public SkinJobFileDialogPeer(FileDialog target) {
    super(target);
  }

  @Override
  public void setFile(String file) {
    // TODO
  }

  @Override
  public void setDirectory(String dir) {
    // TODO
  }

  @Override
  public void setFilenameFilter(FilenameFilter filter) {
    // TODO
  }
}
