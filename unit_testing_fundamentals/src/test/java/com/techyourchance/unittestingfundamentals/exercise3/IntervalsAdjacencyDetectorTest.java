package com.techyourchance.unittestingfundamentals.exercise3;

import com.techyourchance.unittestingfundamentals.example3.Interval;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class IntervalsAdjacencyDetectorTest {


    private IntervalsAdjacencyDetector SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new IntervalsAdjacencyDetector();    
    }

    // interval1 is before interval2 -> false
    // interval1 is after interval2 -> false

    @Test
    public void isAdjacent_interval1BeforeInterval2_falseReturned() {
        Interval interval1 = new Interval(-5, -3);
        Interval interval2 = new Interval(3, 5);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    @Test
    public void isAdjacent_interval1AfterInterval2_falseReturned() {
        Interval interval1 = new Interval(3, 5);
        Interval interval2 = new Interval(-5, -3);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    // i1 end is adjacent to i2 start -> true

    @Test
    public void isAdjacent_interval1EndAdjacentToInterval2Start_trueReturned() {
        Interval interval1 = new Interval(-5, 3);
        Interval interval2 = new Interval(3, 5);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(true));
    }

    // i1 start is adjacent to i2 end -> true

    @Test
    public void isAdjacent_interval1StartAdjacentToInterval2End_trueReturned() {
        Interval interval1 = new Interval(-5, 3);
        Interval interval2 = new Interval(-8, -5);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(true));
    }

    // interval1 is overlapping start of interval2 -> false
    // interval1 is overlapping end of interval2 -> false

    @Test
    public void isAdjacent_interval1OverlapseInterval2Start_falseReturned() {
        Interval interval1 = new Interval(-5, 3);
        Interval interval2 = new Interval(2, 5);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    @Test
    public void isAdjacent_interval1OverlapseInterval2End_falseReturned() {
        Interval interval1 = new Interval(-5, 5);
        Interval interval2 = new Interval(3, 8);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    // i1 is inside i2 -> false
    // i1 is outside i2 -> false

    @Test
    public void isAdjacent_interval1InsideInterval2_falseReturned() {
        Interval interval1 = new Interval(3, 4);
        Interval interval2 = new Interval(2, 5);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    @Test
    public void isAdjacent_interval1OutsideInterval2_falseReturned() {
        Interval interval1 = new Interval(-5, 5);
        Interval interval2 = new Interval(2, 3);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    // i1 is i2 -> false

    @Test
    public void isAdjacent_interval1IsInterval2_falseReturned() {
        Interval interval1 = new Interval(3, 5);
        Interval interval2 = new Interval(3, 5);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }
}