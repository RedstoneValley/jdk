package java.awt;

import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import java.awt.peer.ListPeer;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by cryoc on 2016-10-09.
 */
class SkinJobListPeer extends SkinJobComponentPeerForView<Spinner> implements ListPeer {
    private ArrayList<String> entries = new ArrayList<>();
    private boolean multipleMode = false;

    private TextView createItem(String text) {
        TextView textView;
        if (multipleMode) {
            textView = new CheckBox(androidComponent.getContext());
        } else {
            textView = new TextView(androidComponent.getContext());
        }
        textView.setText(text);
        return textView;
    }

    public SkinJobListPeer(List target) {
        super((Spinner) target.androidWidget);
    }

    @Override
    public synchronized int[] getSelectedIndexes() {
        if (multipleMode) {
            int[] selected = new int[entries.size()];
            int nSelected = 0;
            for (int i = 0; i < androidComponent.getChildCount(); i++) {
                if (((CheckBox) androidComponent.getChildAt(i)).isChecked()) {
                    selected[nSelected++] = i;
                }
            }
            return Arrays.copyOfRange(selected, 0, nSelected);
        } else {
            return new int[] {androidComponent.getSelectedItemPosition()};
        }
    }

    @Override
    public synchronized void add(String item, int index) {
        androidComponent.addView(createItem(item), index);
        entries.add(index, item);
    }

    @Override
    public synchronized void delItems(int start, int end) {
        int numToRemove = 1 + end - start;
        androidComponent.removeViews(start, numToRemove);
        for (int i = 0; i < numToRemove; i++) {
            entries.remove(start);
        }
    }

    @Override
    public synchronized void removeAll() {
        androidComponent.removeAllViews();
        entries.clear();
    }

    @Override
    public synchronized void select(int index) {
        if (multipleMode) {
            ((CheckBox) (androidComponent.getItemAtPosition(index))).setChecked(true);
        } else {
            androidComponent.setSelection(index, SkinJob.animateListAutoSelection);
        }
    }

    @Override
    public void deselect(int index) {
        if (multipleMode) {
            ((CheckBox) (androidComponent.getItemAtPosition(index))).setChecked(false);
        } else {
            androidComponent.setSelection(-1);
        }
    }

    @Override
    public synchronized void makeVisible(int index) {
        if (multipleMode) {
            androidComponent.setSelection(index, SkinJob.animateListAutoSelection);
        }
    }

    @Override
    public synchronized void setMultipleMode(boolean m) {
        if (multipleMode == m) {
            return;
        }
        multipleMode = m;
        androidComponent.removeAllViews();
        for (String entry : entries) {
            androidComponent.addView(createItem(entry));
        }
    }

    @Override
    public Dimension getPreferredSize(int rows) {
        if (rows < 2 || entries.size() < 1) {
            return new Dimension(androidComponent.getWidth(), androidComponent.getHeight());
        }
        View anEntry = androidComponent.getSelectedView();
        return new Dimension(anEntry.getWidth(), anEntry.getHeight() * rows);
    }

    @Override
    public Dimension getMinimumSize(int rows) {
        if (rows < 2 || entries.size() < 1) {
            return new Dimension(androidComponent.getMinimumWidth(),
                    androidComponent.getMinimumHeight());
        }
        View anEntry = androidComponent.getSelectedView();
        return new Dimension(anEntry.getMinimumWidth(), anEntry.getMinimumHeight() * rows);
    }
}
