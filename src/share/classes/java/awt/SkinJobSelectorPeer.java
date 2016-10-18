package java.awt;

import android.widget.AdapterView;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by cryoc on 2016-10-11.
 */

public abstract class SkinJobSelectorPeer<T extends AdapterView<?>>
    extends SkinJobComponentPeerForView<T> {
  protected final java.util.List<String> entries = new ArrayList<>();

  public SkinJobSelectorPeer(T androidComponent) {
    super(androidComponent);
  }

  protected TextView createItem(String text) {
    TextView textView = new TextView(androidWidget.getContext());
    textView.setText(text);
    return textView;
  }

  public synchronized void add(String item, int index) {
    androidWidget.addView(createItem(item), index);
    entries.add(index, item);
  }

  public synchronized void delItems(int start, int end) {
    int numToRemove = 1 + end - start;
    androidWidget.removeViews(start, numToRemove);
    for (int i = 0; i < numToRemove; i++) {
      entries.remove(start);
    }
  }

  public synchronized void remove(int index) {
    entries.remove(index);
  }

  public synchronized void removeAll() {
    androidWidget.removeAllViews();
    entries.clear();
  }

  public synchronized void select(int index) {
    androidWidget.setSelection(index);
  }

  public synchronized void deselect(int index) {
    androidWidget.setSelection(-1);
  }

  public synchronized void makeVisible(int index) {
    androidWidget.scrollTo(
        (int) androidWidget.getChildAt(index).getX(),
        androidWidget.getScrollY());
  }
}
