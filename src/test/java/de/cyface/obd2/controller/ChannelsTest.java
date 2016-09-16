/*
 * Created on 14.09.16 at 08:03.
 */
package de.cyface.obd2.controller;

import de.cyface.obd2.persistence.Channel;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * Tests whether the Channels class works correctly.
 * </p>
 *
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public class ChannelsTest {

    /**
     * <p>
     * The object of the class under test.
     * </p>
     */
    private Channels oocut;

    @Before
    public void setUp() {
        oocut = new Channels();
    }

    /**
     * <p>
     * Tests whether channels identifiers are correctly assigned.
     * </p>
     */
    @Test
    public void testGetNewIdentifier() {
        final Channel channel0 = oocut.getNextFreeChannel("vin");
        assertThat(channel0).hasFieldOrPropertyWithValue("channelIdentifier", 0);
        assertThat(channel0).hasFieldOrPropertyWithValue("vehicleIdentificationNumber", "vin");
        assertThat(channel0.getInputData()).isEmpty();

        final Channel channel1 = oocut.getNextFreeChannel("vin2");
        assertThat(channel1).hasFieldOrPropertyWithValue("channelIdentifier", 1);
        assertThat(channel1).hasFieldOrPropertyWithValue("vehicleIdentificationNumber", "vin2");
        assertThat(channel1.getInputData()).isEmpty();

        final Channel channel2 = oocut.getNextFreeChannel("vin");
        assertThat(channel2).hasFieldOrPropertyWithValue("channelIdentifier", 2);
        assertThat(channel2).hasFieldOrPropertyWithValue("vehicleIdentificationNumber", "vin");
        assertThat(channel2.getInputData()).isEmpty();
    }

    /**
     * <p>
     * Tests if the {@code getChannel} method reacts correctly to a request for a channel that does not exist.
     * </p>
     */
    @Test
    public void testGetNonExistantChannel() {
        assertThat(oocut.getChannel(0)).isNull();
    }

    /**
     * <p>
     * Tests if the {@code getChannel()} method actually returns a recently created channel.
     * </p>
     */
    @Test
    public void testGetExistingChannel() {
        oocut.getNextFreeChannel("vin");

        assertThat(oocut.getChannel(0)).hasFieldOrPropertyWithValue("channelIdentifier", 0);
        assertThat(oocut.getChannel(0)).hasFieldOrPropertyWithValue("vehicleIdentificationNumber", "vin");
        assertThat(oocut.getChannel(0).getInputData()).isEmpty();
    }
}
