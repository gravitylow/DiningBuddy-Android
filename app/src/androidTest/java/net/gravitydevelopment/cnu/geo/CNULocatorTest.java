package net.gravitydevelopment.cnu.geo;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class CNULocatorTest extends TestCase {

    /**
     * @param name
     */
    public CNULocatorTest(String name) {
        super(name);
    }

    /**
     * Does adding a location to the local list increase its size
     */
    public final void testDoesAddLocationIncreaseSize() {
        CNULocator locator = new CNULocator();
        final CNUFence fence = new CNUFence();
        fence.addBound(0.0, 0.0);
        fence.addBound(0.0, 0.0);
        fence.addBound(0.0, 0.0);
        fence.addBound(0.0, 0.0);

        List<CNUFence> list = new ArrayList<CNUFence>();
        list.add(fence);
        CNULocation location = new CNULocation("test", list);

        locator.addLocation(location);
        assertEquals(1, locator.getLocations().size());
    }

    /**
     * Does the locator use a location's more accurate sublocation when possible
     */
    public final void testDoesSubLocationTakePriority() {
        CNULocator locator = new CNULocator();
        final CNUFence fence = new CNUFence();
        fence.addBound(1.0, 1.0);
        fence.addBound(1.0, 2.0);
        fence.addBound(2.0, 1.0);
        fence.addBound(2.0, 2.0);

        List<CNUFence> list1 = new ArrayList<CNUFence>();
        list1.add(fence);

        final CNUFence subFence = new CNUFence();
        subFence.addBound(1.3, 1.3);
        subFence.addBound(1.3, 1.5);
        subFence.addBound(1.5, 1.3);
        subFence.addBound(1.5, 1.5);

        List<CNUFence> list2 = new ArrayList<CNUFence>();
        list2.add(subFence);
        CNULocation subLocation = new CNULocation("test-sublocation", list2);

        List<CNULocation> subList = new ArrayList<CNULocation>();
        subList.add(subLocation);

        CNULocation location = new CNULocation("test", list1, subList);
        locator.addLocation(location);

        assertEquals(subLocation, locator.getLocation(1.4, 1.4));
        assertNotSame(location, locator.getLocation(1.4, 1.4));
    }
}
