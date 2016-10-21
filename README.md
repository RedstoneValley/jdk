# What is SkinJob?

SkinJob (an early work-in-progress) is a self-contained implementation of AWT for Android.

# Design goals

## JDK 7 first

SkinJob will probably mostly be used for porting code that pre-dates Android (or at least pre-dates the level of maturity that Android reached at around Gingerbread or Honeycomb), so AWT features that were added in JDK 8 or later are a much lower priority than those that existed in JDK 7.

## Target API level 16

Unless it turns out to make a major feature impossible to implement, SkinJob will aim to be buildable against API level 16 or lower. (Keep in mind that Google's usage-share statistics may underestimate the frequency of lower API versions, because they don't include AOSP devices that never make themselves known to the Play Store.)

## Wrap Android APIs, don't reimplement them

Android provides plenty of high-level API methods for creating GUI widgets and View hierarchies, drawing shapes, rendering rich text, and loading and saving bitmaps. Delegating these API functions will probably always give better performance than reimplementing them. This may occasionally mean some compromises of rarely-used functionality; for instance, if someone extends `java.awt.BasicStroke` to draw wavy or tapered lines around shapes, then those extra features won't work in all of the `draw*` methods in `SkinJobGraphics`, because some of the methods don't use a `java.awt.Stroke` directly.

## No native code

As a corollary to the above, SkinJob probably won't ever need to contain native code, unlike OpenJDK AWT. Some optional native code may be included in the future to improve performance (although a *lot* of evidence will be needed that it actually improves performance) or to implement rarely-used features.

## API compatibility

All the public classes and interfaces in `java.awt.*` should have all the public methods of their OpenJDK counterparts; those that are not final should have the protected methods as well. They should be able to accept parameters of the same types, and their return types should have the same list of supertypes and interfaces under `java.*` and `javax.*`.

`sun.*` is *not* an API; compatibility with OpenJDK `sun.*` isn't a design goal unless some class in that package hierarchy turns out to be *very* widely used (as happened with `sun.misc.Unsafe`); even then, it's a low priority.