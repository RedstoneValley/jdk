/**
 * <P>Provides interfaces that enable the development of input methods
 * that can be used with any Java runtime environment. Input methods are
 * software components that let the user enter text in ways other than
 * simple typing on a keyboard. They are commonly used to enter
 * Japanese, Chinese, or Korean - languages using thousands of different
 * characters - on keyboards with far fewer keys. However, this package
 * also allows the development of input methods for other languages and
 * the use of entirely different input mechanisms, such as handwriting
 * recognition.</P>
 * <H2><A NAME="package_specification"></A>Package Specification</H2>
 * <UL>
 * <LI><B><A HREF="../../../../../technotes/guides/imf/spec.html">Input Method
 * Framework Specification</A></B>
 * <LI><B><A HREF="../../../../../technotes/guides/imf/spi-reference.html">Input
 * Method Engine SPI Reference</A></B>
 * </UL>
 * <H4><A NAME="Packaging"></A>Packaging Input Methods</H4>
 * <P>Input methods are packaged as installed extensions, as specified
 * by the <A HREF="../../../../../technotes/guides/extensions/index.html">Extension
 * Mechanism</A>. The main JAR file of an input method must contain the
 * file:</P>
 * <PRE>    META-INF/services/java.awt.im.spi.InputMethodDescriptor</PRE>
 * <P>The file should contain a list of fully-qualified class names, one
 * per line, of classes implementing the
 * {@code java.awt.im.spi.InputMethodDescriptor} interface. Space
 * and tab characters surrounding each name, as well as blank lines, are
 * ignored. The comment character is {@code '#'}
 * ({@code \u0023}); on each line all characters following the
 * first comment character are ignored. The file must be encoded in
 * UTF-8.</P>
 * <P>For example, if the fully-qualified name of the class that
 * implements {@code java.awt.im.spi.InputMethodDesciptor} for the
 * <EM>Foo</EM> input method is
 * {@code com.sun.ime.FooInputMethodDescriptor}, the file
 * {@code META-INF/services/java.awt.im.spi.InputMethodDescriptor}
 * contains a line:</P>
 * <PRE>    com.sun.ime.FooInputMethodDescriptor</PRE>
 * <P>The input method must also provide at least two classes: one class
 * implementing the {@code java.awt.im.spi.InputMethodDescriptor}
 * interface, one class implementing the
 * {@code java.awt.im.spi.InputMethod} interface. The input method
 * should separate the implementations for these interfaces, so that
 * loading of the class implementing {@code InputMethod} can be
 * deferred until actually needed.</P>
 * <H4><A NAME="Loading"></A>Loading Input Methods</H4>
 * <P>The input method framework will usually defer loading of input
 * method classes until they are absolutely needed. It loads only the
 * {@code InputMethodDescriptor} implementations during AWT
 * initialization. It loads an {@code InputMethod} implementation
 * when the input method has been selected.</P>
 * <H4><A NAME="PeeredComponents"></A>Java Input Methods and Peered Text
 * Components</H4>
 * <P>The Java input method framework intends to support all
 * combinations of input methods (host input methods and Java input
 * methods) and components (peered and lightweight). However, because of
 * limitations in the underlying platform, it may not always be possible
 * to enable the communication between Java input methods and peered AWT
 * components. Support for this specific combination is therefore
 * platform dependent. In Sun's Java SE Runtime Environments, this
 * combination is supported on Windows, but not on Solaris.</P>
 * <H2>Related Documentation</H2>
 * <P>For overviews, tutorials, examples, guides, and tool
 * documentation, please see:</P>
 * <UL>
 * <LI><B><A HREF="../../../../../technotes/guides/imf/overview.html">Input
 * Method Framework Overview</A></B>
 * <LI><B><A HREF="../../../../../technotes/guides/imf/spi-tutorial.html">Input
 * Method Engine SPI Tutorial</A></B>
 * </UL>
 *
 * @since JDK1.3
 */
package java.awt.im.spi;