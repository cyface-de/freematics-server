/*
 * Created on 14.09.16 at 08:05.
 */
package de.cyface.persistence;

import de.cyface.obd2.persistence.Channel;
import de.cyface.obd2.persistence.GpsData;
import de.cyface.obd2.persistence.InputData;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * </p>
 *
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public class ChannelTest {

    @Test
    public void testChannelHandlesInputData() {
        Channel oocut = new Channel(0,"vin");
        assertThat(oocut).hasFieldOrPropertyWithValue("channelIdentifier",0);

        InputData exampleData = new InputData();
        exampleData.addGpsData(new GpsData(10000L,51.0,13.0,179,1.0,12));
        exampleData.addAccelerationTuple(10000L,1,2,109);
        exampleData.addAccelerationTuple(10001L,1,1,110);

        oocut.addInputData(exampleData);
        assertThat(oocut.getInputData()).containsExactly(exampleData);
    }
}
