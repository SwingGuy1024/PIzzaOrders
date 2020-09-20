package com.disney.miguelmunoz.challenge.util;

import java.util.regex.Pattern;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/20/20
 * <p>Time: 2:51 PM
 *
 * @author Miguel Mu\u00f1oz
 */
public final class ReplaceChain {
  private String currentText;
  
  private ReplaceChain(String source) {
    currentText = source;
  }

  public static ReplaceChain build(String text) {
    return new ReplaceChain(text);
  }

  public ReplaceChain replaceAll(Pattern match, String replacement) {
    currentText = match.matcher(currentText).replaceAll(replacement);
    return this;
  }

  @Override
  public String toString() {
    return currentText;
  }
}
