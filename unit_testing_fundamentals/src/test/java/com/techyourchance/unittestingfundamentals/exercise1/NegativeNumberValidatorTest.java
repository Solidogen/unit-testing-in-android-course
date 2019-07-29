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
        assertThat(SUT.isNegative(-1), is(true));
    }

    @Test
    public void testZero() {
        assertThat(SUT.isNegative(0), is(false));
    }

    @Test
    public void testPositiveNumber() {
        assertThat(SUT.isNegative(1), is(false));
    }
}