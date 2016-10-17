/*
 * Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
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

/*
 * @test
 * @bug 4935798 6521210 6901159
 * @summary Tests that all family names that are reported in all locales
 * correspond to some font returned from getAllFonts().
 * @run main LocaleFamilyNames
 */
import java.awt.*;
import java.util.*;

public final class LocaleFamilyNames {
  private LocaleFamilyNames() {
  }

  public static void main(String[] args) throws Exception {

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        Font[] all_fonts = ge.getAllFonts();

        Locale[] all_locales = Locale.getAvailableLocales();

        HashSet all_families = new HashSet();
    for (Font all_font : all_fonts) {
      all_families.add(all_font.getFamily());
      for (Locale all_locale : all_locales) {
        all_families.add(all_font.getFamily(all_locale));
      }
    }

    for (Locale all_locale : all_locales) {
      String[] families_for_locale = ge.getAvailableFontFamilyNames(all_locale);
      for (String aFamilies_for_locale : families_for_locale) {
        if (!all_families.contains(aFamilies_for_locale)) {
          System.out.println("LOCALE: [" + all_locale + "]");
          System.out.print("NO FONT HAS " + "THE FOLLOWING FAMILY NAME:");
          System.out.println("[" + aFamilies_for_locale + "]");
          throw new Exception("test failed");
        }
      }
    }
    }
}
