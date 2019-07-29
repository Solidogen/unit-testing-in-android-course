package com.techyourchance.unittestingfundamentals.exercise1;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class NegativeNumberValidatorTest {

    private NegativeNumberValidator SUT;

    @Before
    public void setup() {
        SUT = new NegativeNumberValidator();
    }

    @Test
    public void testNegativeNumber() {

        boolean result = SUT.isNegative(-1);
        assertThat(result, is(true));
    }

    @Test
    public void testZero() {
        boolean result = SUT.isNegative(0);
        assertThat(result, is(false));
    }

    @Test
    public void testPositiveNumber() {
        boolean result = SUT.isNegative(1);
        assertThat(result, is(false));
    }
}