package java.awt;

import java.awt.peer.ComponentPeer;

/**
 * Static members used to implement AWT on Android. Public mutable fields in this class control
 * certain behaviors of wrapped Android objects that aren't fully specified by AWT's implementation
 * contract.
 */
public class SkinJob {

    /** Do not instantiate. */
    private SkinJob() {}

    /** Whether {@link SkinJobListPeer#select(int)} should be animated. */
    public static volatile boolean animateListAutoSelection = true;

    /**
     * How far {@link SkinJobComponentPeerForView#setZOrder(ComponentPeer)} should place a
     * component in front of the other component, as a multiple of
     * {@link android.util.DisplayMetrics#density}.
     */
    public static volatile float layerZSpacing = 100.0f;
}
