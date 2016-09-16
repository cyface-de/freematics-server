/*
 * Created on 14.09.16 at 08:04.
 */
package de.cyface.obd2.controller;

import de.cyface.obd2.persistence.Channel;
import de.cyface.obd2.persistence.DataRepository;
import de.cyface.obd2.persistence.GpsData;
import de.cyface.obd2.persistence.InputData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <p>
 * A test for posting data to the server.
 * </p>
 *
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
// TODO make a parameterized test from this as soon as I know how this works with SpringRunner
@RunWith(SpringRunner.class)
@WebMvcTest(Obd2Controller.class)
public class Obd2PostControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private DataRepository repo;

    @MockBean
    private Channels channels;

    /**
     * <p>
     * Tests if data send with an invalid entry is gracefully ignored.
     * </p>
     */
    @Test
    public void postDataWithUnknownEntry() throws Exception {
        Channel exampleChannel = new Channel(1,"testVIN");
        InputData expectedData = new InputData();
        expectedData.addAccelerationTuple(6103000L,2,-1,109);
        expectedData.addAccelerationTuple(6103000L,2,-1,109);
        expectedData.addAccelerationTuple(6103000L,2,0,109);
        expectedData.addAccelerationTuple(6103000L,2,-1,109);
        expectedData.addAccelerationTuple(6103000L,1,0,110);
        expectedData.addAccelerationTuple(6103000L,2,-1,109);
        expectedData.addAccelerationTuple(6103000L,2,-1,109);
        expectedData.addAccelerationTuple(6103000L,2,-1,109);
        expectedData.addAccelerationTuple(6103000L,2,-1,109);
        expectedData.addAccelerationTuple(6103000L,2,-1,109);
        expectedData.addAccelerationTuple(6103000L,2,-1,109);
        expectedData.addAccelerationTuple(6103000L,2,-1,109);
        expectedData.addGpsData(new GpsData(7284200L,51.025750,13.722881,179,0.0,0));
        given(this.channels.getChannel(1)).willReturn(exampleChannel);

        BufferedReader reader = Files.newBufferedReader(Paths.get(this.getClass().getResource("/example.data").toURI()));
        String dataEntryWithDTE = reader.readLine()+" 0,DTE,090916";

        mvc.perform(post("/post").content(dataEntryWithDTE).param("id","1")).andExpect(status().isOk());
        verify(repo, times(1)).store(exampleChannel);
        assertThat(exampleChannel.getInputData()).hasSize(1);
        assertThat(exampleChannel.getInputData()).containsExactly(expectedData);
    }

    /**
     * <p>
     * Tests if normal data is processed correctly.
     * </p>
     */
    @Test
    public void postNormalDataEntry() throws Exception {
        Channel exampleChannel = new Channel(1, "testVIN");
        InputData expectedData = new InputData();
        expectedData.addAccelerationTuple(75831000L, 2, 0, 109);
        expectedData.addAccelerationTuple(75831000L, 2, -1, 108);
        expectedData.addAccelerationTuple(75831000L, 2, -1, 110);
        expectedData.addAccelerationTuple(75831000L, 2, -1, 109);
        expectedData.addAccelerationTuple(75831000L, 2, -1, 109);
        expectedData.addAccelerationTuple(75831000L, 2, -2, 109);
        expectedData.addAccelerationTuple(75831000L, 2, -1, 110);
        expectedData.addAccelerationTuple(75831000L, 2, -1, 109);
        expectedData.addAccelerationTuple(75831000L, 1, -1, 109);
        expectedData.addAccelerationTuple(75831000L, 2, -1, 109);
        expectedData.addAccelerationTuple(75831000L, 1, 0, 109);
        expectedData.addAccelerationTuple(75831000L, 2, -1, 110);
        // TODO Check this data entry. It has no speed and satellite information.
        expectedData.addGpsData(new GpsData(7295170, 51.025763, 13.723145, 179, 0.0, 0));
        given(this.channels.getChannel(1)).willReturn(exampleChannel);

        BufferedReader reader = Files
                .newBufferedReader(Paths.get(this.getClass().getResource("/example.data").toURI()));
        reader.readLine();
        String secondLine = reader.readLine(); // the second line contains a normal data entry.

        mvc.perform(post("/post").param("id", "1").content(secondLine)).andExpect(status().isOk());
        verify(repo, times(1)).store(exampleChannel);
        assertThat(exampleChannel.getInputData()).hasSize(1);
        assertThat(exampleChannel.getInputData()).containsExactly(expectedData);
    }
}
