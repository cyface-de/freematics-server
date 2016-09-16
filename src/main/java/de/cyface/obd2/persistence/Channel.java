/*
 * Created on 07.09.16 at 07:34.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de.cyface.obd2.persistence;

import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * One transmission channel reserved for a specific Freematics session.
 * </p>
 *
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public final class Channel {
    /**
     * <p>
     * The channels identifier, which is unique among the currently active channels.
     * </p>
     */
    private final int channelIdentifier;
    /**
     * <p>
     * The world wide unique vehicle identification (VIN) number for each car. This is used to merge data collected by
     * the same car.
     * </p>
     */
    private final String vehicleIdentificationNumber;
    /**
     * <p>
     * The data captured and transmitted via this channel.
     * </p>
     */
    private final List<InputData> data;

    /**
     * <p>
     * Creates a new completely initialized object identified by a channel identifier in combination with a vehicle
     * identification number.
     * </p>
     *
     * @param channelIdentifier           The channels identifier, which is unique among the currently active channels.
     * @param vehicleIdentificationNumber The world wide unique vehicle identification (VIN) number for each car. This
     *                                    is used to merge data collected by the same car.
     */
    public Channel(final int channelIdentifier, final String vehicleIdentificationNumber) {
        Validate.notEmpty(vehicleIdentificationNumber);
        this.channelIdentifier = channelIdentifier;
        this.vehicleIdentificationNumber = vehicleIdentificationNumber;
        this.data = new ArrayList<>();
    }

    /**
     * @return The channels identifier, which is unique among the currently active channels.
     */
    public int getChannelIdentifier() {
        return channelIdentifier;
    }

    /**
     * @return The world wide unique vehicle identification (VIN) number for each car. This is used to merge data
     * collected by the same car.
     */
    String getVehicleIdentificationNumber() {
        return vehicleIdentificationNumber;
    }

    /**
     * <p>
     * Adds the provided {@link InputData} to this {@code Channel} possibly merging it with existing data.
     * </p>
     *
     * @param inputData The {@link InputData} to merge with the existing data.
     */
    public void addInputData(final InputData inputData) {
        Validate.notNull(inputData);

        data.add(inputData);
    }

    /**
     * @return All the data received via this channel.
     */
    public Collection<InputData> getInputData() {
        return Collections.unmodifiableCollection(data);
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        for (InputData entry : data) {
            ret.append("\t\t").append(entry).append(":\n");
        }
        return ret.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Channel channel = (Channel)o;

        if (channelIdentifier != channel.channelIdentifier) {
            return false;
        }
        if (vehicleIdentificationNumber != null ?
                !vehicleIdentificationNumber.equals(channel.vehicleIdentificationNumber) :
                channel.vehicleIdentificationNumber != null) {
            return false;
        }
        return data != null ? data.equals(channel.data) : channel.data == null;

    }

    @Override
    public int hashCode() {
        int result = channelIdentifier;
        result = 31 * result + (vehicleIdentificationNumber != null ? vehicleIdentificationNumber.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }
}
