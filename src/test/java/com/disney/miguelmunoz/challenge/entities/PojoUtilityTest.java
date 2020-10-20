package com.disney.miguelmunoz.challenge.entities;

import com.disney.miguelmunoz.framework.PojoUtility;
import org.junit.Test;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 8/29/20
 * <p>Time: 3:04 PM
 *
 * @author Miguel Mu\u00f1oz
 */
public class PojoUtilityTest {
  @Test(expected = AssertionError.class)
  public void neverNullAssertionTest() {
    // NOTE: This test assumes assertions are turned on during testing. If assertions are off, this test will fail.
    PojoUtility.confirmNeverNull(new MenuItem());
    testIfAssertionsAreOn(); // makes test pass if assertions are off.
  }
  
  @Test(expected = AssertionError.class)
  // NOTE: This test assumes assertions are turned on during testing. If assertions are off, this test will fail.
  public void confirmFoundAssertionTest() {
    PojoUtility.confirmEntityFound("x", 0);
    testIfAssertionsAreOn(); // makes test pass if assertions are off.
  }

  @SuppressWarnings("ErrorNotRethrown")
  private void testIfAssertionsAreOn() {
    try {
      assert false;
    } catch (AssertionError ignore) {
      return;
    }
    System.out.println("Warning: Assertions are off. This test needs assertions to be on.");
    throw new AssertionError("Assertions are off");
  }
}