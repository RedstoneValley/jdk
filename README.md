# What is SkinJob?

SkinJob (an early work-in-progress) is a self-contained implementation of AWT for Android.

# Design goals

## Wrap Android APIs, don't reimplement them

Android provides plenty of high-level API methods for creating GUI widgets and View hierarchies, drawing shapes, rendering rich text, and loading and saving bitmaps. Delegating these API functions will probably always give better performance than reimplementing them.

Occasional exceptions to this rule may be needed to implement a few rarely-used formatting features that the Android APIs don't provide. For example, android.graphics.Canvas.drawOval doesn't support dashed lines, so these will probably have to be implemented with a drawArc call for each dash. However, these features are a lower priority than those that are widely-used enough to be available in the Android APIs directly.

## No native code

As a corollary to the above, SkinJob probably won't ever need to contain native code, unlike OpenJDK AWT. Some optional native code may be included in the future to improve performance (although a *lot* of evidence will be needed that it actually improves performance) or to implement rarely-used features.