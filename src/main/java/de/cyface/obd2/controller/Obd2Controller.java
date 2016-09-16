/*
 * Created on 16.08.16 at 16:56.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de.cyface.obd2.controller;

import de.cyface.obd2.persistence.Channel;
import de.cyface.obd2.persistence.DataRepository;
import de.cyface.obd2.persistence.GpsData;
import de.cyface.obd2.persistence.InputData;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.Writer;

/**
 * <p>
 * Class responsible for handling REST request arriving at this server and sending appropriate responses.
 * </p>
 *
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
public final class Obd2Controller {

    /**
     * <p>
     * Logger for objects of this class. Configure it using SpringBoot settings in
     * <tt>resources/application.properties</tt>.
     * </p>
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Obd2Controller.class);

    /**
     * <p>
     * The repository used to store all the captured data to.
     * </p>
     */
    @Autowired
    private DataRepository repo;

    /**
     * <p>
     * An object managing the active {@link Channel} objects.
     * </p>
     */
    @Autowired
    private Channels channels;

    /**
     * <p>
     * Root method for printing the already received data to the screen.
     * </p>
     *
     * @param outputWriter A {@code Writer} taking the output to write to the screen.
     * @throws IOException If anything fails while writing the output.
     */
    @RequestMapping(method = RequestMethod.GET, path = "/")
    public void root(final Writer outputWriter) throws IOException {
        outputWriter.write(repo.toString());
    }

    /**
     * <p>
     * Handler method for initializing a new dongle after startup. It register the transimission device for a vehicle
     * with a new free channel.
     * </p>
     *
     * @param vehicleIdentificationNumber The world wide unique vehicle identification number (VIN) of the vehicle
     *            capturing the data.
     * @return The channel number the client should use for further transmissions.
     */
    @RequestMapping(method = RequestMethod.GET, path = "/push")
    public String push(@RequestParam("VIN") final String vehicleIdentificationNumber) {
        Validate.notEmpty(vehicleIdentificationNumber);
        LOGGER.debug("Received request for new Channel with VIN {}.");
        Channel newChannel = channels.getNextFreeChannel(vehicleIdentificationNumber);
        LOGGER.debug("New channel with identifier {} assigned to VIN {}.", newChannel.getChannelIdentifier(),
                vehicleIdentificationNumber);
        return String.format("CH:%s", newChannel.getChannelIdentifier());
    }

    /**
     * <p>
     * Handler method for data transmission requests.
     * </p>
     *
     * @param body The message body containing the transmitted data.
     * @param channelIdentifier The identifier of the channel to transmit to. This must be an active channel or the call
     *            will fail.
     */
    @RequestMapping(method = RequestMethod.POST, path = "/post")
    public void post(@RequestBody final String body, @RequestParam("id") final int channelIdentifier) {
        LOGGER.debug("Received data for channel {}", channelIdentifier);
        LOGGER.debug("Received: {}", body);
        InputData data = parseBody(body);
        Channel channel = channels.getChannel(channelIdentifier);
        if (channel == null) {
            throw new IllegalStateException(
                    String.format("Channel with identifier %s does not exist.", channelIdentifier));
        }
        channel.addInputData(data);
        repo.store(channel);
    }

    /**
     * <p>
     * Parses the raw message body as received from the Freematics OBD II dongle in the Freematics data format
     * </p>
     *
     * @param body One row of data as received from the Freematics OBD II dongle.
     * @return A parsed representation of the parsed body.
     */
    private InputData parseBody(final String body) {
        String[] entries = body.split(" ");

        long lastAbsoluteTimestamp = 0L;
        InputData ret = new InputData();
        long gpsTime = 0L;
        double latitude = 0.0;
        double longitude = 0.0;
        int altitude = 0;
        double gpsSpeed = 0.0;
        int satellites = 0;

        for (String entry : entries) {
            String[] values = entry.split(",");

            long timestamp;
            if (values[0].startsWith("#")) {
                lastAbsoluteTimestamp = Long.valueOf(values[0].substring(1));
                lastAbsoluteTimestamp *= 1000L;
                timestamp = lastAbsoluteTimestamp;
            } else {
                timestamp = lastAbsoluteTimestamp + Long.valueOf(values[0]);
            }

            try {
                EntryIdentifier lastEntryIdentifier = EntryIdentifier.valueOf(values[1]);
                switch (lastEntryIdentifier) {
                    case UTC:
                        gpsTime = parseLongValue(values[2]);
                        break;
                    case LAT:
                        latitude = parseDoubleValue(values[2]);
                        break;
                    case LNG:
                        longitude = parseDoubleValue(values[2]);
                        break;
                    case ALT:
                        altitude = parseIntValue(values[2]);
                        break;
                    case SPD:
                        gpsSpeed = parseDoubleValue(values[2]);
                        break;
                    case SAT:
                        satellites = parseIntValue(values[2]);
                        break;
                    case ACC:
                        int ax = parseIntValue(values[2]);
                        int ay = parseIntValue(values[3]);
                        int az = parseIntValue(values[4]);
                        ret.addAccelerationTuple(timestamp, ax, ay, az);
                        break;
                    default:
                        throw new IllegalStateException("Unknown data entry.");
                }
            } catch (IllegalArgumentException e) {
                LOGGER.error("Unable to parse a data entry.", e);
            }
        }
        if (gpsTime != 0L) {
            GpsData gpsData = new GpsData(gpsTime, latitude, longitude, altitude, gpsSpeed, satellites);
            ret.addGpsData(gpsData);
        }
        return ret;
    }

    /**
     * <p>
     * Tries to parse a {@code String} value to a {@code long}.
     * </p>
     *
     * @param value The {@code String} value to parse.
     * @return The parsed result as {@code long}.
     * @throws NumberFormatException If the provided {@code value} is not parseable a {@code long}.
     */
    private long parseLongValue(final String value) {
        return Long.valueOf(value);
    }

    /**
     * <p>
     * Tries to parse a {@code String} value to a {@code double}.
     * </p>
     *
     * @param value The {@code String} value to parse.
     * @return The parsed result as {@code double}.
     * @throws NumberFormatException If the provided {@code value} is not parseable to a {@code double}.
     */
    private double parseDoubleValue(final String value) {
        return Double.valueOf(value);
    }

    /**
     * <p>
     * Tries to parse a {@code String} value to an integer.
     * </p>
     *
     * @param value The {@code String} value to parse.
     * @return The parsed result as {@code int}.
     * @throws NumberFormatException If the provided {@code value} is not parseable to an integer.
     */
    private int parseIntValue(final String value) {
        return Integer.valueOf(value);
    }
}

/**
 * <p>
 * An enumeration with all the possible data identifiers, expected as part of a data set.
 * </p>
 */
enum EntryIdentifier {
    /**
     * <p>
     * Identifier for accelerometer data.
     * </p>
     */
    ACC,
    /**
     * <p>
     * Identifier for GPS timestamp in UTC.
     * </p>
     */
    UTC,
    /**
     * <p>
     * Identifier for GPS latitude information.
     * </p>
     */
    LAT,
    /**
     * <p>
     * Identifier for GPS longitude information.
     * </p>
     */
    LNG,
    /**
     * <p>
     * Identifier for altitude information captured by GPS.
     * </p>
     */
    ALT,
    /**
     * <p>
     * Identifier for GPS speed information.
     * </p>
     */
    SPD,
    /**
     * <p>
     * Identifier for GPS Satellite information.
     * </p>
     */
    SAT
}
