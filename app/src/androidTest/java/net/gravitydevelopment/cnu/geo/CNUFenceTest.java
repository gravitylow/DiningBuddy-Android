package net.gravitydevelopment.cnu.geo;

import junit.framework.TestCase;

public class CNUFenceTest extends TestCase {

    /**
     * @param name
     */
    public CNUFenceTest(String name) {
        super(name);
    }

    /**
     * Is fence size zero after it is first instantiated
     */
    public final void testIsFenceSizeZeroAfterInstantiation() {
        CNUFence fence = new CNUFence();
        assertEquals(0, fence.getSize());
    }

    /**
     * Does adding a 5 bounds not increase the size?
     */
    public final void testDoesAddingFiveFencesFail() {
        CNUFence fence = new CNUFence();
        assertTrue(fence.addBound(0.0, 0.0));
        assertTrue(fence.addBound(0.0, 0.0));
        assertTrue(fence.addBound(0.0, 0.0));
        assertTrue(fence.addBound(0.0, 0.0));

        assertFalse(fence.addBound(0.0, 0.0));
        assertEquals(4, fence.getSize());
    }

    /**
     * Does checking bounds fail if there are no added bounds
     */
    public final void testDoesIsInsideFenceFailIfNoBounds() {
        CNUFence fence = new CNUFence();
        assertFalse(fence.isInsideFence(0.0, 0.0));
    }

    /**
     * Does adding a bound increase the size?
     */
    public final void testIsFenceSizeOneAfterOneCoordinatePairAdded() {
        CNUFence fence = new CNUFence();
        assertTrue(fence.addBound(1.0, 1.0));
        assertEquals(1, fence.getSize());
    }


    /**
     * Does adding the first bound set all effective min/max to those values
     */
    public final void testDoesAddingFirstBoundSetAllMinMax() {
        CNUFence fence = new CNUFence();
        assertTrue(fence.addBound(1.0, 1.0));
        assertEquals(1.0, fence.getEffectiveMinLat());
        assertEquals(1.0, fence.getEffectiveMaxLat());
        assertEquals(1.0, fence.getEffectiveMinLong());
        assertEquals(1.0, fence.getEffectiveMaxLong());
    }

    /**
     * Does adding a larger bound set the applicable max
     */
    public final void testDoesAddingLargerBoundSetApplicableMax() {
        CNUFence fence = new CNUFence();
        assertTrue(fence.addBound(1.0, 1.0));
        assertEquals(1.0, fence.getEffectiveMinLat());
        assertEquals(1.0, fence.getEffectiveMaxLat());
        assertEquals(1.0, fence.getEffectiveMinLong());
        assertEquals(1.0, fence.getEffectiveMaxLong());

        assertTrue(fence.addBound(2.0, 2.0));
        assertEquals(1.0, fence.getEffectiveMinLat());
        assertEquals(2.0, fence.getEffectiveMaxLat());
        assertEquals(1.0, fence.getEffectiveMinLong());
        assertEquals(2.0, fence.getEffectiveMaxLong());
    }

    /**
     * Does adding a smaller bound set the applicable min
     */
    public final void testDoesAddingSmallerBoundSetApplicableMin() {
        CNUFence fence = new CNUFence();
        assertTrue(fence.addBound(2.0, 2.0));
        assertEquals(2.0, fence.getEffectiveMinLat());
        assertEquals(2.0, fence.getEffectiveMaxLat());
        assertEquals(2.0, fence.getEffectiveMinLong());
        assertEquals(2.0, fence.getEffectiveMaxLong());

        assertTrue(fence.addBound(1.0, 1.0));
        assertEquals(1.0, fence.getEffectiveMinLat());
        assertEquals(2.0, fence.getEffectiveMaxLat());
        assertEquals(1.0, fence.getEffectiveMinLong());
        assertEquals(2.0, fence.getEffectiveMaxLong());
    }

    /**
     * Does checking a boundary between a given min and max succeed
     */
    public final void testDoesCheckingIntermediateBoundSucceed() {
        CNUFence fence = new CNUFence();
        assertTrue(fence.addBound(1.0, 1.0));
        assertTrue(fence.addBound(1.0, 2.0));
        assertTrue(fence.addBound(2.0, 1.0));
        assertTrue(fence.addBound(2.0, 2.0));
        assertTrue(fence.isInsideFence(1.5, 1.5));
    }

    /**
     * Does checking a boundary above a given max fail
     */
    public final void testDoesCheckingHighBoundFail() {
        CNUFence fence = new CNUFence();
        assertTrue(fence.addBound(1.0, 1.0));
        assertTrue(fence.addBound(1.0, 2.0));
        assertTrue(fence.addBound(2.0, 1.0));
        assertTrue(fence.addBound(2.0, 2.0));
        assertFalse(fence.isInsideFence(3.0, 3.0));
    }

    /**
     * Does checking a boundary below a given min fail
     */
    public final void testDoesCheckingLowBoundFail() {
        CNUFence fence = new CNUFence();
        assertTrue(fence.addBound(1.0, 1.0));
        assertTrue(fence.addBound(1.0, 2.0));
        assertTrue(fence.addBound(2.0, 1.0));
        assertTrue(fence.addBound(2.0, 2.0));
        assertFalse(fence.isInsideFence(0.0, 0.0));
    }

    /**
     * Does checking a boundary below and above bounds fail
     */
    public final void testDoesCheckingMixedBoundFail() {
        CNUFence fence = new CNUFence();
        assertTrue(fence.addBound(1.0, 1.0));
        assertTrue(fence.addBound(1.0, 2.0));
        assertTrue(fence.addBound(2.0, 1.0));
        assertTrue(fence.addBound(2.0, 2.0));
        assertFalse(fence.isInsideFence(1.5, 3.0));
        assertFalse(fence.isInsideFence(3.0, 1.5));
    }
}
