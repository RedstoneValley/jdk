package sun.text;

import java.text.CharacterIterator;
import java.util.Iterator;

/**
 * Created by cryoc on 2016-10-15.
 */
public class CodePointIterator implements Iterator<Integer> {
  public static final int DONE = CharacterIterator.DONE;
  private final CharacterIterator aci;
  private Integer next;

  private CodePointIterator(CharacterIterator aci) {
    this.aci = aci;
  }

  public static Iterator<Integer> create(CharacterIterator aci) {
    return new CodePointIterator(aci);
  }

  private Integer internalNext() {
    char next = aci.next();
    if (next == CharacterIterator.DONE) {
      return DONE;
    }
    if (Character.isHighSurrogate(next)) {
      return Character.toCodePoint(next, aci.next());
    }
    return (int) next;
  }
  @Override
  public boolean hasNext() {
    if (next == null) {
      next = internalNext();
    }
    return next != DONE;
  }

  @Override
  public Integer next() {
    if (next == null) {
      next = internalNext();
    }
    Integer returned = next;
    next = null;
    return returned;
  }
}
