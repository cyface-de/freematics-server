/*
 * Created on 14.09.16 at 08:02.
 */
package de.cyface.obd2.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import de.cyface.obd2.persistence.Channel;
import de.cyface.obd2.persistence.DataRepository;
import de.cyface.obd2.persistence.GpsData;
import de.cyface.obd2.persistence.InputData;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * Tests if the root controller produces the correct output.
 * </p>
 *
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Obd2RootControllerTest {

    /**
     * <p>
     * Client used to communicate with the test server.
     * </p>
     */
    @Autowired
    TestRestTemplate restTemplate;

    /**
     * <p>
     * Repository containing test data for this test.
     * </p>
     */
    @Autowired
    DataRepository repo;

    /**
     * <p>
     * Tests whether the root controller produces the correct output for a filled data repository.
     * </p>
     *
     * @throws Exception
     */
    @Test
    public void testRootControllerOutput() throws Exception {
        InputData data1 = new InputData();
        data1.addGpsData(new GpsData(10000L, 51.0, 13.0, 179, 1.0, 12));
        data1.addAccelerationTuple(10000L, -1, 2, 107);
        data1.addAccelerationTuple(10001L, 0, 2, 108);

        InputData data2 = new InputData();
        data2.addAccelerationTuple(10002L, 1, 1, 107);
        data2.addAccelerationTuple(10003L, 1, 1, 107);

        InputData data3 = new InputData();
        data3.addGpsData(new GpsData(10000L, 12.0, 10.0, 20, 1.2, 5));
        data3.addAccelerationTuple(10000L, 20, 7, 50);

        Channel exampleChannel1 = new Channel(1, "vin");
        exampleChannel1.addInputData(data1);
        exampleChannel1.addInputData(data2);
        Channel exampleChannel2 = new Channel(0, "vin2");
        exampleChannel2.addInputData(data3);

        repo.store(exampleChannel1);
        repo.store(exampleChannel2);

        String expectedResult = "vin2: \n"
                + "\t\t\ttimestamp: 10000 Lat: 12.0 Lon: 10.0 Speed: 1.2 Altitude: 20 Satellites: 5\n"
                + "\t\t\ttimestamp: 10000 ax: 20 ay: 7 az: 50" + "\n\n" + "vin: \n"
                + "\t\t\ttimestamp: 10000 Lat: 51.0 Lon: 13.0 Speed: 1.0 Altitude: 179 Satellites: 12\n"
                + "\t\t\ttimestamp: 10000 ax: -1 ay: 2 az: 107\n" + "\t\t\ttimestamp: 10001 ax: 0 ay: 2 az: 108\n"
                + "\n" + "\t\t\ttimestamp: 10002 ax: 1 ay: 1 az: 107\n" + "\t\t\ttimestamp: 10003 ax: 1 ay: 1 az: 107\n"
                + "\n";

        ResponseEntity<String> response = restTemplate.getForEntity("/", String.class);
        assertThat(response).hasFieldOrPropertyWithValue("statusCode", HttpStatus.OK);
        assertThat(response).hasFieldOrPropertyWithValue("body", expectedResult);
    }
}
