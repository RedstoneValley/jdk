/*
 * Copyright (c) 1995, 2006, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package sun.awt.image;

import java.net.URL;
import java.net.URLConnection;

public class URLImageSource extends InputStreamImageSource {
  URL url;
  URLConnection conn;
  String actualHost;
  int actualPort;

  public URLImageSource(URL u) {
    url = u;
  }

  @Override
  final boolean checkSecurity(Object context, boolean quiet) {
    // If actualHost is not null, then the host/port parameters that
    // the image was actually fetched from were different than the
    // host/port parameters the original URL specified for at least
    // one of the download attempts.  The original URL security was
    // checked when the applet got a handle to the image, so we only
    // need to check for the real host/port.
    if (actualHost != null) {
      try {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
          security.checkConnect(actualHost, actualPort, context);
        }
      } catch (SecurityException e) {
        if (!quiet) {
          throw e;
        }
        return false;
      }
    }
    return true;
  }

}
