package skinjob.internal.peer;

import static android.app.Notification.PRIORITY_DEFAULT;
import static android.app.Notification.PRIORITY_HIGH;
import static android.app.Notification.PRIORITY_MAX;
import static skinjob.util.SkinJobUtil.newAndroidWindow;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;
import android.view.Window;
import java.awt.TrayIcon;
import java.awt.peer.TrayIconPeer;
import java.util.concurrent.atomic.AtomicInteger;
import skinjob.SkinJobGlobals;

/**
 * Created by cryoc on 2016-10-21.
 */
public class SkinJobTrayIconPeer implements TrayIconPeer {
  private static final AtomicInteger NEXT_ID = new AtomicInteger(0);
  private static final String TAG = "SkinJobTrayIconPeer";
  private static final int ERROR_ICON;

  static {
    int errorIcon;
    try {
      // Use a ! in octagon, if available
      errorIcon = Class
          .forName("com.android.settings.R$drawable")
          .getField("ic_print_error")
          .getInt(null);
    } catch (ReflectiveOperationException e) {
      // Fall back to a ! in triangle
      errorIcon = android.R.drawable.stat_notify_error;
    }
    ERROR_ICON = errorIcon;
  }

  private final TrayIcon thisTrayIcon;
  private final Notification.Builder notificationBuilder;
  private final NotificationManager notificationManager;
  private final int id;
  private final Context androidContext;
  private String text;

  public SkinJobTrayIconPeer(TrayIcon target) {
    thisTrayIcon = target;
    androidContext = SkinJobGlobals.getAndroidApplicationContext();
    notificationBuilder = new Notification.Builder(androidContext);
    notificationManager = androidContext.getSystemService(NotificationManager.class);
    id = NEXT_ID.incrementAndGet();
  }

  @Override
  public void dispose() {
    notificationManager.cancel(TAG, id);
  }

  @Override
  public void setToolTip(String tooltip) {
    notificationBuilder.setContentTitle(tooltip);
    updateImage();
  }

  @Override
  public void updateImage() {
    notificationManager.notify(TAG, id, notificationBuilder.build());
  }

  @Override
  public void displayMessage(String caption, String text, String messageType) {
    notificationBuilder.setContentTitle(caption);
    notificationBuilder.setContentText(text);
    int icon;
    switch (messageType) {
      case "ERROR":
        notificationBuilder.setSmallIcon(ERROR_ICON);
        notificationBuilder.setPriority(PRIORITY_MAX);
        break;
      case "WARNING":
        // ! in triangle
        notificationBuilder.setSmallIcon(android.R.drawable.stat_notify_error);
        notificationBuilder.setPriority(PRIORITY_HIGH);
        break;
      case "INFO":
        // i in circle
        notificationBuilder.setSmallIcon(android.R.drawable.ic_dialog_info);
        notificationBuilder.setPriority(PRIORITY_DEFAULT);
        break;
      case "NONE":
        notificationBuilder.setSmallIcon(null);
        notificationBuilder.setPriority(PRIORITY_DEFAULT);
        break;
      default:
        Log.e(TAG, "Unknown message type " + messageType);
    }
    updateImage();
  }

  @Override
  public void showPopupMenu(int x, int y) {
    Window menuWindow = newAndroidWindow(androidContext);
    menuWindow.setContentView(thisTrayIcon.getPopupMenu().sjAndroidWidget);
    menuWindow.makeActive();
  }
}
