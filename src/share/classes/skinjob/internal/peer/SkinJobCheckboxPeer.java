package skinjob.internal.peer;

import android.widget.CompoundButton;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.peer.CheckboxPeer;

/**
 * Created by cryoc on 2016-10-09.
 */
public class SkinJobCheckboxPeer extends SkinJobComponentPeerForView<CompoundButton>
    implements CheckboxPeer {
  protected final Checkbox thisBox;
  protected CheckboxGroup group;

  public SkinJobCheckboxPeer(Checkbox target) {
    super((CompoundButton) target.sjAndroidWidget);
    thisBox = target;
    setCheckboxGroup(target.getCheckboxGroup());
  }

  @Override
  public void setState(boolean state) {
    androidWidget.setChecked(state);
  }

  @Override
  public void setCheckboxGroup(CheckboxGroup g) {
    if (group == g) {
      return;
    }
    group = g;
    Checkbox currentChoice = group.getSelectedCheckbox();
    if (currentChoice != null && currentChoice != thisBox) {
      setState(false);
    }
  }

  @Override
  public void setLabel(String label) {
    androidWidget.setText(label);
  }
}
