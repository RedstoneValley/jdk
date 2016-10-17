/*
 * Copyright (c) 2009, 2012, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * This source code is provided to illustrate the usage of a given feature
 * or technique and has been deliberately simplified. Additional steps
 * required for a production-quality application, such as security checks,
 * input validation and proper error handling, might not be present in
 * this sample code.
 */

package com.sun.nio.zipfs;

/**
 * @author Xueming Shen
 */

final class ZipConstants {
  /*
   * Compression methods
   */
  static final int METHOD_STORED = 0;
  static final int METHOD_DEFLATED = 8;
  static final int METHOD_DEFLATED64 = 9;
  static final int METHOD_BZIP2 = 12;
  static final int METHOD_LZMA = 14;
  static final int METHOD_LZ77 = 19;
  static final int METHOD_AES = 99;

  /*
   * General purpose big flag
   */
  static final int FLAG_ENCRYPTED = 0x01;
  static final int FLAG_DATADESCR = 0x08;    // crc, size and csize in dd
  static final int FLAG_EFS = 0x800;   // If this bit is set the filename and
  /*
   * Header sizes in bytes (including signatures)
   */
  static final int LOCHDR = 30;       // LOC header size
  static final int EXTHDR = 16;       // EXT header size
  static final int CENHDR = 46;       // CEN header size
  static final int ENDHDR = 22;       // END header size
  /*
   * Local file (LOC) header field offsets
   */
  static final int LOCVER = 4;        // version needed to extract
  static final int LOCFLG = 6;        // general purpose bit flag
  static final int LOCHOW = 8;        // compression method
  static final int LOCTIM = 10;       // modification time
  static final int LOCCRC = 14;       // uncompressed file crc-32 value
  static final int LOCSIZ = 18;       // compressed size
  static final int LOCLEN = 22;       // uncompressed size
  static final int LOCNAM = 26;       // filename length
  static final int LOCEXT = 28;       // extra field length
  /*
   * Extra local (EXT) header field offsets
   */
  static final int EXTCRC = 4;        // uncompressed file crc-32 value
  static final int EXTSIZ = 8;        // compressed size
  static final int EXTLEN = 12;       // uncompressed size
  /*
   * Central directory (CEN) header field offsets
   */
  static final int CENVEM = 4;        // version made by
  static final int CENVER = 6;        // version needed to extract
  static final int CENFLG = 8;        // encrypt, decrypt flags
  static final int CENHOW = 10;       // compression method
  static final int CENTIM = 12;       // modification time
  static final int CENCRC = 16;       // uncompressed file crc-32 value
  static final int CENSIZ = 20;       // compressed size
  static final int CENLEN = 24;       // uncompressed size
  static final int CENNAM = 28;       // filename length
  static final int CENEXT = 30;       // extra field length
  static final int CENCOM = 32;       // comment length
  static final int CENDSK = 34;       // disk number start
  static final int CENATT = 36;       // internal file attributes
  static final int CENATX = 38;       // external file attributes
  static final int CENOFF = 42;       // LOC header offset
  /*
   * End of central directory (END) header field offsets
   */
  static final int ENDSUB = 8;        // number of entries on this disk
  static final int ENDTOT = 10;       // total number of entries
  static final int ENDSIZ = 12;       // central directory size in bytes
  static final int ENDOFF = 16;       // offset of first CEN header
  static final int ENDCOM = 20;       // zip file comment length
  /*
   * ZIP64 constants
   */
  static final long ZIP64_ENDSIG = 0x06064b50L;  // "PK\006\006"
  static final long ZIP64_LOCSIG = 0x07064b50L;  // "PK\006\007"
  static final int ZIP64_ENDHDR = 56;           // ZIP64 end header size
  static final int ZIP64_LOCHDR = 20;           // ZIP64 end loc header size
  static final int ZIP64_EXTHDR = 24;           // EXT header size
  static final int ZIP64_EXTID = 0x0001;       // Extra field Zip64 header ID
  static final int ZIP64_MINVAL32 = 0xFFFF;
  static final long ZIP64_MINVAL = 0xFFFFFFFFL;
  /*
   * Zip64 End of central directory (END) header field offsets
   */
  static final int ZIP64_ENDLEN = 4;       // size of zip64 end of central dir
  static final int ZIP64_ENDVEM = 12;      // version made by
  static final int ZIP64_ENDVER = 14;      // version needed to extract
  static final int ZIP64_ENDNMD = 16;      // number of this disk
  static final int ZIP64_ENDDSK = 20;      // disk number of start
  static final int ZIP64_ENDTOD = 24;      // total number of entries on this disk
  static final int ZIP64_ENDTOT = 32;      // total number of entries
  static final int ZIP64_ENDSIZ = 40;      // central directory size in bytes
  static final int ZIP64_ENDOFF = 48;      // offset of first CEN header
  static final int ZIP64_ENDEXT = 56;      // zip64 extensible data sector
  /*
   * Zip64 End of central directory locator field offsets
   */
  static final int ZIP64_LOCDSK = 4;       // disk number start
  static final int ZIP64_LOCOFF = 8;       // offset of zip64 end
  static final int ZIP64_LOCTOT = 16;      // total number of disks
  /*
   * Zip64 Extra local (EXT) header field offsets
   */
  static final int ZIP64_EXTCRC = 4;       // uncompressed file crc-32 value
  static final int ZIP64_EXTSIZ = 8;       // compressed size, 8-byte
  static final int ZIP64_EXTLEN = 16;      // uncompressed size, 8-byte
  /*
   * Extra field header ID
   */
  static final int EXTID_ZIP64 = 0x0001;      // ZIP64
  static final int EXTID_NTFS = 0x000a;      // NTFS
  static final int EXTID_UNIX = 0x000d;      // UNIX
  static final int EXTID_EFS = 0x0017;      // Strong Encryption
  static final int EXTID_EXTT = 0x5455;      // Info-ZIP Extended Timestamp
  /* The END header is followed by a variable length comment of size < 64k. */
  static final long END_MAXLEN = 0xFFFF + ENDHDR;
  static final int READBLOCKSZ = 128;
  // comment fields for this file must be
  // encoded using UTF-8.
    /*
     * Header signatures
     */
  static final long LOCSIG = 0x04034b50L;   // "PK\003\004"
  static final long EXTSIG = 0x08074b50L;   // "PK\007\008"
  static final long CENSIG = 0x02014b50L;   // "PK\001\002"
  static final long ENDSIG = 0x06054b50L;   // "PK\005\006"

  private ZipConstants() {
  }

  /*
   * fields access methods
   */
  ///////////////////////////////////////////////////////
  static int CH(byte[] b, int n) {
    return Byte.toUnsignedInt(b[n]);
  }

  static int SH(byte[] b, int n) {
    return Byte.toUnsignedInt(b[n]) | Byte.toUnsignedInt(b[n + 1]) << 8;
  }

  static long LG(byte[] b, int n) {
    return (SH(b, n) | SH(b, n + 2) << 16) & 0xffffffffL;
  }

  static long LL(byte[] b, int n) {
    return LG(b, n) | LG(b, n + 4) << 32;
  }

  static long GETSIG(byte[] b) {
    return LG(b, 0);
  }

  // local file (LOC) header fields
  static long LOCSIG(byte[] b) {
    return LG(b, 0);
  } // signature

  static int LOCVER(byte[] b) {
    return SH(b, 4);
  } // version needed to extract

  static int LOCFLG(byte[] b) {
    return SH(b, 6);
  } // general purpose bit flags

  static int LOCHOW(byte[] b) {
    return SH(b, 8);
  } // compression method

  static long LOCTIM(byte[] b) {
    return LG(b, 10);
  } // modification time

  static long LOCCRC(byte[] b) {
    return LG(b, 14);
  } // crc of uncompressed data

  static long LOCSIZ(byte[] b) {
    return LG(b, 18);
  } // compressed data size

  static long LOCLEN(byte[] b) {
    return LG(b, 22);
  } // uncompressed data size

  static int LOCNAM(byte[] b) {
    return SH(b, 26);
  } // filename length

  static int LOCEXT(byte[] b) {
    return SH(b, 28);
  } // extra field length

  // extra local (EXT) header fields
  static long EXTCRC(byte[] b) {
    return LG(b, 4);
  }  // crc of uncompressed data

  static long EXTSIZ(byte[] b) {
    return LG(b, 8);
  }  // compressed size

  static long EXTLEN(byte[] b) {
    return LG(b, 12);
  } // uncompressed size

  // zip64 end of central directory recoder fields
  static long ZIP64_ENDTOD(byte[] b) {
    return LL(b, 24);
  }  // total number of entries on disk

  // central directory header (CEN) fields
  static long CENSIG(byte[] b, int pos) {
    return LG(b, pos);
  }

  static int CENVEM(byte[] b, int pos) {
    return SH(b, pos + 4);
  }

  static int CENVER(byte[] b, int pos) {
    return SH(b, pos + 6);
  }

  static int CENFLG(byte[] b, int pos) {
    return SH(b, pos + 8);
  }

  static int CENHOW(byte[] b, int pos) {
    return SH(b, pos + 10);
  }

  static long CENTIM(byte[] b, int pos) {
    return LG(b, pos + 12);
  }

  static long CENCRC(byte[] b, int pos) {
    return LG(b, pos + 16);
  }

  static long CENSIZ(byte[] b, int pos) {
    return LG(b, pos + 20);
  }

  static long CENLEN(byte[] b, int pos) {
    return LG(b, pos + 24);
  }

  static int CENNAM(byte[] b, int pos) {
    return SH(b, pos + 28);
  }

  static int CENEXT(byte[] b, int pos) {
    return SH(b, pos + 30);
  }

  static int CENCOM(byte[] b, int pos) {
    return SH(b, pos + 32);
  }

  static int CENDSK(byte[] b, int pos) {
    return SH(b, pos + 34);
  }

  static int CENATT(byte[] b, int pos) {
    return SH(b, pos + 36);
  }

  static long CENATX(byte[] b, int pos) {
    return LG(b, pos + 38);
  }

  static long CENOFF(byte[] b, int pos) {
    return LG(b, pos + 42);
  }
}
