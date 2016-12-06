package skinjob.internal;

import android.graphics.BitmapFactory;

import java.awt.Image;
import java.io.IOException;

import sun.awt.datatransfer.DataTransferer;
import sun.awt.datatransfer.ToolkitThreadBlockedHandler;

/**
 * Created by chris on 12/5/2016.
 */
public class SkinJobDataTransferer extends DataTransferer {
    @Override
    public String getDefaultUnicodeEncoding() {
        return "UTF-8";
    }

    @Override
    public boolean isLocaleDependentTextFormat(long format) {
        // TODO
        return false;
    }

    @Override
    public boolean isFileFormat(long format) {
        // TODO
        return false;
    }

    @Override
    public boolean isImageFormat(long format) {
        // TODO
        return false;
    }

    @Override
    protected Long getFormatForNativeAsLong(String str) {
        // TODO
        return null;
    }

    @Override
    protected String getNativeForFormat(long format) {
        // TODO
        return null;
    }

    @Override
    protected String[] dragQueryFile(byte[] bytes) {
        // TODO
        return new String[0];
    }

    @Override
    protected Image platformImageBytesToImage(byte[] bytes, long format) throws IOException {
        return new SkinJobBufferedImage(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
    }

    @Override
    public ToolkitThreadBlockedHandler getToolkitThreadBlockedHandler() {
        return new SkinJobToolkitThreadBlockedHandler();
    }
}
