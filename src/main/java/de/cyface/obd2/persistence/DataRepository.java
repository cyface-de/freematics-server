/*
 * Created on 06.09.16 at 15:40.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de.cyface.obd2.persistence;

import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Data storage repository storing all the information received by this server instance.
 * </p>
 *
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
public final class DataRepository {
    /**
     * <p>
     * A mapping from a vehicle identification number to all the data captured for that vehicle on this server.
     * </p>
     */
    private final Map<String, DeviceData> dataPerDevice;

    /**
     * <p>
     * Creates a new completely initialized {@code DataRepository}.
     * </p>
     */
    public DataRepository() {
        this.dataPerDevice = new HashMap<>();
    }

    /**
     * <p>
     * Stores the data from the provided {@link Channel} in this repository, possibly merging it with existing data for
     * the same vehicle.
     * </p>
     *
     * @param data The {@code data} to store in the repository.
     */
    public void store(final Channel data) {
        String vehicleIdentificationNumber = data.getVehicleIdentificationNumber();
        DeviceData existingDeviceData = dataPerDevice.get(vehicleIdentificationNumber);
        if (existingDeviceData == null) {
            existingDeviceData = new DeviceData(vehicleIdentificationNumber);
        }
        existingDeviceData.addChannelInformation(data);
        dataPerDevice.put(vehicleIdentificationNumber, existingDeviceData);
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        for (final Map.Entry<String, DeviceData> entry : dataPerDevice.entrySet()) {
            ret.append(entry.getKey()).append(": \n").append(entry.getValue());
        }
        return ret.toString();
    }
}

/**
 * <p>
 * Data captured per device. A device will in most cases be a car identified by its vehicle identification number, which
 * it broadcasts over its OBD II interface.
 * </p>
 */
class DeviceData {
    /**
     * <p>
     * The world wide unique vehicle identification (VIN) number for each car. This is used to merge data collected by
     * the same car.
     * </p>
     */
    private final String vehicleIdentificationNumber;

    /**
     * <p>
     * For each restart of the Freematics dongle a new communication channel is created. This attribute stores the
     * captured data per created channel.
     * </p>
     */
    private final Map<Integer, Channel> channelInformation;

    /**
     * <p>
     * Creates a new completely initialized {@code DeviceData} object for one specific vehicle identified by its vehicle
     * identification number.
     * </p>
     *
     * @param vehicleIdentificationNumber The world wide unique vehicel identification (VIN) number for each car. This
     *                                    is used to merge data collected by the same car.
     */
    DeviceData(final String vehicleIdentificationNumber) {
        Validate.notEmpty(vehicleIdentificationNumber);
        this.vehicleIdentificationNumber = vehicleIdentificationNumber;
        channelInformation = new HashMap<>();
    }

    /**
     * <p>
     * Adds all the information from the provided channel to this {@code DeviceData} object, merging it with existing
     * information from the same channel if there are some.
     * </p>
     *
     * @param channel The channel information to merge into this {@code DeviceData} object.
     */
    public void addChannelInformation(final Channel channel) {
        Channel existingChannel = channelInformation.get(channel.getChannelIdentifier());
        if (existingChannel != null) {
            existingChannel = mergeChannels(existingChannel, channel);
        } else {
            existingChannel = channel;
        }
        channelInformation.put(existingChannel.getChannelIdentifier(), existingChannel);
    }

    /**
     * <p>
     * Merges the information from {@code channel} into {@code existingChannel} and returns the merged {@code
     * existingChannel}.
     * </p>
     *
     * @param existingChannel The {@link Channel} to merge the information into.
     * @param channel         The {@link Channel} containing the information to merge.
     * @return The merged {@link Channel}.
     */
    private Channel mergeChannels(Channel existingChannel, Channel channel) {
        existingChannel.addInputData(channel.getInputData());
        return existingChannel;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        for (final Map.Entry<Integer, Channel> entry : channelInformation.entrySet()) {
            ret.append("\t").append(entry.getKey()).append(":\n").append(entry.getValue());
        }
        return ret.toString();
    }
}
