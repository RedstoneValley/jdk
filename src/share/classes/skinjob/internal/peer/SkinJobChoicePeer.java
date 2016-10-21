package skinjob.internal.peer;

import android.widget.AbsListView;
import android.widget.ListView;
import java.awt.Choice;
import java.awt.peer.ChoicePeer;

/**
 * Created by cryoc on 2016-10-11.
 */
public class SkinJobChoicePeer extends SkinJobSelectorPeer<ListView> implements ChoicePeer {
  public SkinJobChoicePeer(Choice target) {
    super((ListView) target.sjAndroidWidget);
    androidWidget.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
  }
}
