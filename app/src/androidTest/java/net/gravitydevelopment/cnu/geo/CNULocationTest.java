package net.gravitydevelopment.cnu.geo;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class CNULocationTest extends TestCase {

    /**
     * @param name
     */
    public CNULocationTest(String name) {
        super(name);
    }

    /**
     * Does checking a boundary between a given min and max succeed
     */
    public final void testDoesCheckingIntermediateBoundSucceed() {
        final CNUFence fence = new CNUFence();
        fence.addBound(1.0, 1.0);
        fence.addBound(1.0, 2.0);
        fence.addBound(2.0, 1.0);
        fence.addBound(2.0, 2.0);

        List<CNUFence> list = new ArrayList<CNUFence>();
        list.add(fence);
        CNULocation location = new CNULocation("test", list);
        assertTrue(location.isInsideLocation(1.5, 1.5));
    }

    /**
     * Does checking a boundary between a given min and max succeed
     */
    public final void testDoesCheckingIntermediateBoundSucceedWithTwoFences() {
        final CNUFence fence1 = new CNUFence();
        fence1.addBound(1.0, 1.0);
        fence1.addBound(1.0, 2.0);
        fence1.addBound(2.0, 1.0);
        fence1.addBound(2.0, 2.0);

        final CNUFence fence2 = new CNUFence();
        fence2.addBound(3.0, 3.0);
        fence2.addBound(3.0, 4.0);
        fence2.addBound(4.0, 3.0);
        fence2.addBound(4.0, 4.0);

        List<CNUFence> list = new ArrayList<CNUFence>();
        list.add(fence1);
        list.add(fence2);
        CNULocation location = new CNULocation("test", list);
        assertTrue(location.isInsideLocation(3.5, 3.5));
    }

    /**
     * Does checking a boundary between a given min and max in two different fences fail
     */
    public final void testDoesCheckingIntermediateBoundInTwoFencesFail() {
        final CNUFence fence1 = new CNUFence();
        fence1.addBound(1.0, 1.0);
        fence1.addBound(1.0, 2.0);
        fence1.addBound(2.0, 1.0);
        fence1.addBound(2.0, 2.0);

        final CNUFence fence2 = new CNUFence();
        fence2.addBound(3.0, 3.0);
        fence2.addBound(3.0, 4.0);
        fence2.addBound(4.0, 3.0);
        fence2.addBound(4.0, 4.0);

        List<CNUFence> list = new ArrayList<CNUFence>();
        list.add(fence1);
        list.add(fence2);
        CNULocation location = new CNULocation("test", list);
        assertFalse(location.isInsideLocation(1.5, 3.5));
    }
}
