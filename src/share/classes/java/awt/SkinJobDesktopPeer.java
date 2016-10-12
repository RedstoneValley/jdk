package java.awt;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.awt.peer.DesktopPeer;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;

/**
 * Created by cryoc on 2016-10-09.
 */
class SkinJobDesktopPeer implements DesktopPeer {

  private final Context androidContext;

  public SkinJobDesktopPeer(Context androidContext) {
    this.androidContext = androidContext;
  }

  protected void launchIntent(File file, String action) throws IOException {
    Intent intentToOpen = new Intent(action);
    launchIntentForFile(intentToOpen, file);
  }

  private void launchIntentForFile(Intent intent, File file) throws IOException {
    Uri fileUri = Uri.fromFile(file);
    FileInputStream fileInputStream = new FileInputStream(file);
    try {
      String mime = URLConnection.guessContentTypeFromStream();
      if (mime == null) {
        mime = URLConnection.guessContentTypeFromName(file.getName());
      }
      intent.setDataAndType(fileUri, mime);
      androidContext.startActivity(intent);
    } finally {
      fileInputStream.close();
    }
  }

  @Override
  public boolean isSupported(Desktop.Action action) {
    if (action == Desktop.Action.PRINT) {
      return ContextCompat.checkSelfPermission(androidContext, Manifest.permission.INTERNET)
          == PackageManager.PERMISSION_GRANTED
          && ContextCompat.checkSelfPermission(androidContext, Manifest.permission.READ_CONTACTS)
          == PackageManager.PERMISSION_GRANTED;
    }
    return true;
  }

  @Override
  public void open(File file) throws IOException {
    launchIntent(file, Intent.ACTION_VIEW);
  }

  @Override
  public void edit(File file) throws IOException {
    launchIntent(file, Intent.ACTION_EDIT);
  }

  @Override
  public void print(File file) throws IOException {
    if (!isSupported(Desktop.Action.PRINT)) {
      throw new UnsupportedOperationException("Don't have permission to connect to a printer");
    }
    Intent printIntent = new Intent(androidContext, PrintDialogActivity.class);
    launchIntentForFile(printIntent, file);
  }

  @Override
  public void mail(URI mailtoURL) throws IOException {
    Intent mailIntent = new Intent(Intent.ACTION_SENDTO);
    mailIntent.setData(Uri.parse(mailtoURL.toString()));
    androidContext.startActivity(mailIntent);
  }

  @Override
  public void browse(URI uri) throws IOException {
    Intent browseIntent = new Intent(Intent.ACTION_VIEW);
    browseIntent.setData(Uri.parse(uri.toString()));
    androidContext.startActivity(browseIntent);
  }

  public static class PrintDialogActivity extends Activity {
    private static final String PRINT_DIALOG_URL = "https://www.google.com/cloudprint/dialog.html";
    private static final String JS_INTERFACE = "AndroidPrintDialog";
    private static final String CONTENT_TRANSFER_ENCODING = "base64";

    /**
     * Post message that is sent by Print Dialog web page when the printing dialog
     * needs to be closed.
     */
    private static final String CLOSE_POST_MESSAGE_NAME = "cp-dialog-on-close";
    /**
     * Intent that started the action.
     */
    Intent cloudPrintIntent;
    /**
     * Web view element to show the printing dialog in.
     */
    private WebView dialogWebView;

    @Override
    public void onCreate(Bundle icicle) {
      super.onCreate(icicle);

      dialogWebView = new WebView(this);
      cloudPrintIntent = this.getIntent();

      WebSettings settings = dialogWebView.getSettings();
      settings.setJavaScriptEnabled(true);

      dialogWebView.setWebViewClient(new PrintDialogWebClient());
      dialogWebView.addJavascriptInterface(new PrintDialogJavaScriptInterface(), JS_INTERFACE);

      dialogWebView.loadUrl(PRINT_DIALOG_URL);
    }

    final class PrintDialogJavaScriptInterface {
      public String getType() {
        return cloudPrintIntent.getType();
      }

      public String getTitle() {
        return cloudPrintIntent.getExtras().getString("title");
      }

      public String getContent() {
        try {
          ContentResolver contentResolver = getContentResolver();
          InputStream is = contentResolver.openInputStream(cloudPrintIntent.getData());
          ByteArrayOutputStream baos = new ByteArrayOutputStream();

          byte[] buffer = new byte[4096];
          int n = is.read(buffer);
          while (n >= 0) {
            baos.write(buffer, 0, n);
            n = is.read(buffer);
          }
          is.close();
          baos.flush();

          return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
        return "";
      }

      public String getEncoding() {
        return CONTENT_TRANSFER_ENCODING;
      }

      public void onPostMessage(String message) {
        if (message.startsWith(CLOSE_POST_MESSAGE_NAME)) {
          finish();
        }
      }
    }

    private final class PrintDialogWebClient extends WebViewClient {

      @Override
      public void onPageFinished(WebView view, String url) {
        if (PRINT_DIALOG_URL.equals(url)) {
          // Submit print document.
          view.loadUrl(
              "javascript:printDialog.setPrintDocument(printDialog.createPrintDocument(" + "window."
                  + JS_INTERFACE + ".getType(),window." + JS_INTERFACE + ".getTitle()," + "window."
                  + JS_INTERFACE + ".getContent(),window." + JS_INTERFACE + ".getEncoding()))");

          // Add post messages listener.
          view.loadUrl("javascript:window.addEventListener('message'," + "function(evt){window."
              + JS_INTERFACE + ".onPostMessage(evt.data)}, false)");
        }
      }
    }
  }
}
