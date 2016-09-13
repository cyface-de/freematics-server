/*
 * Created on 06.09.16 at 15:40.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de.cyface.obd2.persistence;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
            existingDeviceData = new DeviceData();
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
     * This attribute stores the captured data.
     * </p>
     */
    private final List<InputData> data;

    /**
     * <p>
     * Creates a new completely initialized {@code DeviceData} object for one specific vehicle.
     * </p>
     */
    DeviceData() {
        data = new ArrayList<>();
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
        data.addAll(channel.getInputData());
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        for (final InputData entry : data) {
            ret.append("\t").append(entry).append("\n");
        }
        return ret.toString();
    }
}
