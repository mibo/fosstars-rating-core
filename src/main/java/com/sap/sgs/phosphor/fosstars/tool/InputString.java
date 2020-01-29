package com.sap.sgs.phosphor.fosstars.tool;

import com.sap.sgs.phosphor.fosstars.data.UserCallback;
import java.util.Objects;

/**
 * Reads a string provided by a user.
 */
public class InputString {

  /**
   * A callback to interact with a user.
   */
  private final UserCallback callback;

  public InputString(UserCallback callback) {
    Objects.requireNonNull(callback, "Hey! User callback can't be null!");
    this.callback = callback;
  }

  public String get() {
    String string;
    while (true) {
      string = callback.ask();
      if (string != null) {
        string = string.trim();
      }
      if (string == null || string.isEmpty()) {
        callback.say("[!] Hmm ... Looks like an empty string. Please try again ...");
      } else {
        break;
      }
    }
    return string;
  }
}
