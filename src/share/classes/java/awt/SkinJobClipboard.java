package java.awt;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.URI;

/**
 * <p>An AWT clipboard that wraps the Android clipboard, and also stores its contents privately so
 * that they can be pasted unchanged into the process they were copied from.</p>
 * <p>
 * <p>AWT clipboard semantics are very different from Android clipboard semantics, which allow for
 * storing only four types: {@link Intent}, {@link Uri}, plain text, and HTML. Anything else is
 * likely to become plain text or HTML when copied to or pasted from another app, except that this
 * class will convert a {@link URI} to a {@link Uri} when copying (but not vice-versa when pasting,
 * since it doesn't know which class is actually desired).</p>
 */
public class SkinJobClipboard extends Clipboard
    implements ClipboardManager.OnPrimaryClipChangedListener {
  private final ClipboardManager clipboardManager;
  private final ContentResolver contentResolver;

  private ClipData lastExported;

  public SkinJobClipboard(Context androidContext) {
    super("Android system clipboard");
    clipboardManager = androidContext.getSystemService(ClipboardManager.class);
    contentResolver = androidContext.getContentResolver();
  }

  protected static Object pasteItem(ClipData.Item item) {
    if (item == null) {
      return null;
    }
    Intent intent = item.getIntent();
    if (intent != null) {
      return intent;
    }
    Uri uri = item.getUri();
    if (uri != null) {
      return uri;
    }
    String html = item.getHtmlText();
    if (html != null) {
      return html;
    }
    return item.getText();
  }

  /**
   * <p>Creates a {@link ClipData} that holds <i>some</i> representation of the given {@link
   * Transferable}. A non-empty representation will be found for <i>any</i> non-null Transferable,
   * even one that has no {@link DataFlavor}s with non-null data.</p>
   * <p>
   * <p>{@code export} will attempt to use the following factory methods, in descending order of
   * preference:</p>
   * <ol>
   * <li>{@link ClipData#newIntent(CharSequence, Intent)}</li>
   * <li>{@link ClipData#newUri(ContentResolver, CharSequence, Uri)} (including where the
   * {@link Uri} must be converted from a {@link URI})</li>
   * <li>{@link ClipData#newHtmlText(CharSequence, CharSequence, String)}</li>
   * <li>{@link ClipData#newPlainText(CharSequence, CharSequence)}</li>
   * </ol>
   * <p>{@code newHtmlText} will only be called if the {@link Transferable} declares a
   * representation with {@code text/html} as its exact mimetype. When calling {@code newHtmlText}
   * or {@code newPlainText}, one of the following plain-text representations is chosen, in
   * descending order of preference:</p>
   * <ol>
   * <li>A {@link CharSequence} whose mimetype is declared as {@code text/plain}.</li>
   * <li>A {@link CharSequence} with any other declared mimetype.</li>
   * <li>A non-{@link CharSequence} returned by {@link
   * Transferable#getTransferData(DataFlavor)}, converted by {@link Object#toString()}.</li>
   * <li>A {@link CharSequence} whose mimetype is declared as {@code text/html}.</li>
   * <li>The {@link Transferable} itself, converted by {@link Object#toString()}.</li>
   * </ol>
   *
   * @param transferable The {@link Transferable} to export.
   * @return A {@link ClipData} extracted from the {@link Transferable}, or null if {@code
   * transferable} is null.
   */
  protected ClipData export(Transferable transferable) {
    if (transferable == null) {
      return null;
    }
    String htmlFallback = null;
    CharSequence plainTextFallback1 = null;
    CharSequence plainTextFallback2 = null;
    String plainTextFallback3 = null;
    for (DataFlavor flavor : transferable.getTransferDataFlavors()) {
      Object contentsWithFlavor;
      try {
        contentsWithFlavor = transferable.getTransferData(flavor);
      } catch (UnsupportedFlavorException | IOException e) {
        throw new RuntimeException(e);
      }
      if (contentsWithFlavor == null) {
        continue;
      }
      if (contentsWithFlavor instanceof Intent) {
        return ClipData.newIntent(contentsWithFlavor.toString(), (Intent) contentsWithFlavor);
      } else if (contentsWithFlavor instanceof Uri) {
        return ClipData.newUri(contentResolver,
            contentsWithFlavor.toString(),
            (Uri) contentsWithFlavor);
      } else if (contentsWithFlavor instanceof URI) {
        String uriString = contentsWithFlavor.toString();
        return ClipData.newUri(contentResolver, uriString, Uri.parse(uriString));
        // TODO: Find some way to handle AWT classes such as RenderedImage?
      } else if (contentsWithFlavor instanceof CharSequence) {
        CharSequence contentsChars = (CharSequence) contentsWithFlavor;
        if (htmlFallback == null && flavor
            .getMimeType()
            .equals(ClipDescription.MIMETYPE_TEXT_HTML)) {
          htmlFallback = contentsChars.toString();
        } else if (plainTextFallback1 == null && flavor
            .getMimeType()
            .equals(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
          plainTextFallback1 = contentsChars;
        } else {
          plainTextFallback2 = (CharSequence) contentsWithFlavor;
        }
      } else if (plainTextFallback1 == null && plainTextFallback2 == null
          && plainTextFallback3 == null) {
        plainTextFallback3 = contentsWithFlavor.toString();
      }
    }
    CharSequence chosenPlainTextFallback;
    if (plainTextFallback1 != null) {
      chosenPlainTextFallback = plainTextFallback1;
    } else if (plainTextFallback2 != null) {
      chosenPlainTextFallback = plainTextFallback2;
    } else if (plainTextFallback3 != null) {
      chosenPlainTextFallback = plainTextFallback3;
    } else if (htmlFallback != null) {
      chosenPlainTextFallback = htmlFallback;
    } else {
      chosenPlainTextFallback = transferable.toString();
    }
    if (htmlFallback != null) {
      return ClipData.newHtmlText(chosenPlainTextFallback, chosenPlainTextFallback, htmlFallback);
    } else {
      return ClipData.newPlainText(chosenPlainTextFallback, chosenPlainTextFallback);
    }
  }

  @Override
  public synchronized void setContents(Transferable contents, ClipboardOwner owner) {
    super.setContents(contents, owner);
    lastExported = export(contents);
    clipboardManager.setPrimaryClip(lastExported);
  }

  @Override
  public synchronized Transferable getContents(Object requestor) {
    Transferable privateContents = super.getContents(requestor);
    if (privateContents != null) {
      return privateContents;
    }
    if (clipboardManager.hasPrimaryClip()) {
      ClipData externalClipboard = clipboardManager.getPrimaryClip();
      DataFlavor flavor;
      try {
        flavor = new DataFlavor(externalClipboard.getDescription().getMimeType(0));
      } catch (ClassNotFoundException e) {
        return null;
      }
      return new Transferable() {
        @Override
        public DataFlavor[] getTransferDataFlavors() {
          return new DataFlavor[]{flavor};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor desiredFlavor) {
          return desiredFlavor.equals(flavor);
        }

        @Override
        public Object getTransferData(DataFlavor desiredFlavor)
            throws UnsupportedFlavorException, IOException {
          if (!(desiredFlavor.equals(flavor))) {
            throw new UnsupportedFlavorException(desiredFlavor);
          }
          int itemCount = externalClipboard.getItemCount();
          String outputMimeType = flavor.getMimeType();
          boolean outputHtml = false;
          switch (outputMimeType) {
            case ClipDescription.MIMETYPE_TEXT_HTML:
              outputHtml = true;
              // Fall through
            case ClipDescription.MIMETYPE_TEXT_PLAIN:
              StringBuilder unpackedText = new StringBuilder();
              for (int i = 0; i < itemCount; i++) {
                ClipData.Item item = externalClipboard.getItemAt(i);
                CharSequence unpackedSubstring = null;
                if (outputHtml) {
                  unpackedSubstring = item.getHtmlText();
                }
                if (unpackedSubstring == null) {
                  // HTML is either absent from this item or not desired.
                  unpackedSubstring = item.getText();
                }
                unpackedText.append(unpackedSubstring);
              }
              return unpackedText.toString();
            default:
              if (itemCount <= 1) {
                return pasteItem(externalClipboard.getItemAt(0));
              }
              Object[] unpackedItems = new Object[itemCount];
              for (int i = 0; i < itemCount; i++) {
                unpackedItems[i] = pasteItem(externalClipboard.getItemAt(i));
              }
              return unpackedItems;
          }
        }
      };
    } else {
      return null;
    }
  }

  @Override
  public DataFlavor[] getAvailableDataFlavors() {
    return super.getAvailableDataFlavors();
  }

  @Override
  public boolean isDataFlavorAvailable(DataFlavor flavor) {
    return super.isDataFlavorAvailable(flavor);
  }

  @Override
  public Object getData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
    return super.getData(flavor);
  }

  @Override
  public synchronized void onPrimaryClipChanged() {
    if (!(lastExported.equals(clipboardManager.getPrimaryClip()))) {
      super.setContents(null, null);
    }
  }
}
