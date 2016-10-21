package skinjob.internal.peer;

import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.Spinner;
import android.widget.TextView;
import java.awt.Dimension;
import java.awt.List;
import java.awt.peer.ListPeer;
import java.util.Arrays;
import skinjob.SkinJobGlobals;

/**
 * Created by cryoc on 2016-10-09.
 */
public class SkinJobListPeer extends SkinJobSelectorPeer<Spinner> implements ListPeer {
  protected boolean multipleMode;

  public SkinJobListPeer(List target) {
    super((Spinner) target.sjAndroidWidget);
    multipleMode = target.isMultipleMode();
  }

  @Override
  protected TextView createItem(String text) {
    if (multipleMode) {
      TextView textView = new CheckBox(androidWidget.getContext());
      textView.setText(text);
      return textView;
    } else {
      return super.createItem(text);
    }
  }

  @Override
  public synchronized void select(int index) {
    if (multipleMode) {
      ((Checkable) androidWidget.getItemAtPosition(index)).setChecked(true);
    } else {
      androidWidget.setSelection(index, SkinJobGlobals.animateListAutoSelection);
    }
  }

  @Override
  public synchronized void deselect(int index) {
    if (multipleMode) {
      ((Checkable) androidWidget.getItemAtPosition(index)).setChecked(false);
    } else {
      androidWidget.setSelection(-1);
    }
  }

  @Override
  public synchronized void makeVisible(int index) {
    if (multipleMode) {
      androidWidget.setSelection(index, SkinJobGlobals.animateListAutoSelection);
    } else {
      super.makeVisible(index);
    }
  }

  @Override
  public synchronized int[] getSelectedIndexes() {
    if (multipleMode) {
      int[] selected = new int[entries.size()];
      int nSelected = 0;
      for (int i = 0; i < androidWidget.getChildCount(); i++) {
        if (((Checkable) androidWidget.getChildAt(i)).isChecked()) {
          selected[nSelected] = i;
          nSelected++;
        }
      }
      if (nSelected == selected.length) {
        return selected;
      }
      return Arrays.copyOfRange(selected, 0, nSelected);
    } else {
      return new int[]{androidWidget.getSelectedItemPosition()};
    }
  }

  @Override
  public synchronized void setMultipleMode(boolean m) {
    if (multipleMode == m) {
      return;
    }
    multipleMode = m;
    androidWidget.removeAllViews();
    for (String entry : entries) {
      androidWidget.addView(createItem(entry));
    }
  }

  @Override
  public Dimension getPreferredSize(int rows) {
    return new Dimension(androidWidget.getWidth(), androidWidget.getHeight());
  }

  @Override
  public Dimension getMinimumSize(int rows) {
    return new Dimension(androidWidget.getMinimumWidth(), androidWidget.getMinimumHeight());
  }
}
