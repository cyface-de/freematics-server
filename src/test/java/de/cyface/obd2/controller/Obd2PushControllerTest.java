/*
 * Created on 14.09.16 at 08:03.
 */
package de.cyface.obd2.controller;

import de.cyface.obd2.persistence.Channel;
import de.cyface.obd2.persistence.DataRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <p>
 * </p>
 *
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@WebMvcTest
public class Obd2PushControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private DataRepository repo;

    @MockBean
    private Channels channels;

    @Test
    public void testPushWithCorrectVin() throws Exception {
        final String vin = "vin";
        given(channels.getNextFreeChannel(vin)).willReturn(new Channel(0,vin));

        mvc.perform(get("/push").param("VIN",vin)).andExpect(status().isOk()).andExpect(content().string("CH:0"));
    }

    @Test
    public void testPushWithMissingVin() throws Exception {
        mvc.perform(get("/push")).andExpect(status().is4xxClientError());
    }
}
