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

Sometimes a public field or method needs to be added to a `java.awt.*` class so that SkinJob code in another package can access it. (This includes ones that OpenJDK AWT accesses only from JNI code and/or via reflection. Reflection is to be avoided in SkinJob for performance reasons.) When this is the case, its name should be prefixed with `sj` so that it's clear to the SkinJob maintainers that it's not part of the API.

## Serialization compatibility only if it's easy

There will probably be minimal demand to transfer objects that AWT declares as Serializable between Android and other platforms that AWT runs on. Thus, it's only a minor nuisance if SkinJob implementations aren't interchangeable with other implementations, as long as they have a different serialVersionUid to indicate that.

## Speak Android's visual language

When developers want pixel-by-pixel control over how a GUI widget looked and behaved, they use Swing, not AWT. Using AWT implies that an app is free to adapt its look and feel to the conventions of the platform; and when moving from a desktop OS to Android, that can mean bigger adaptations than usual. For example, people don't expect an Android app to have a horizontal menu bar along the top of the window, so SkinJob's menu bar should default to either being a side panel with the menus stacked vertically, or a drop-down menu accessible by tapping a ≡ icon.

## Exclude `java.awt.font.TextAttribute` and `java.awt.font.NumericShaper`

The Android SDK already contains copies of those classes.

## Make magic-number defaults mutable

AWT contains a lot of hard-coded defaults that may not make sense in a mobile environment, even assuming they still make sense on the desktop. For example, the default 12-point font size is likely to be too large. SkinJob pulls these magic numbers into `public static volatile` fields of `SkinJobGlobals` so the app developer can reconfigure them.

# Package structure

* `java.awt.*` contains all the same classes as in OpenJDK.
* `sun.*` contains those packages from OpenJDK's `sun.*` that SkinJob uses.
* `skinjob.internal.*` contains classes created for SkinJob that the app developer should never have to know about.
* `skinjob.internal.peer` contains implementations of the interfaces in `java.awt.peer`.
* `skinjob.util.*` contains utility methods that are used by SkinJob and may be of use to app developers as well. They can be considered an additional API.
* `skinjob.SkinJobGlobals` is the only class in package `skinjob` that's not in a subpackage. It mainly consists of default values, but it also serves to make objects such as the Android context available in a static scope that are required and wouldn't otherwise be available.