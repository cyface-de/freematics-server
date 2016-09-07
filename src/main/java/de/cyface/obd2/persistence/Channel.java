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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * One transmission channel reserved for a specific Freematics session
 * </p>
 *
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public final class Channel {
    private final int channelIdentifier;
    private final String vehicleIdentificationNumber;

    private final Map<String, InputData> data;

    public Channel(final int channelIdentifier, final String vehicleIdentificationNumber) {
        Validate.notEmpty(vehicleIdentificationNumber);
        this.channelIdentifier = channelIdentifier;
        this.vehicleIdentificationNumber = vehicleIdentificationNumber;
        this.data = new HashMap<>();
    }

    public int getChannelIdentifier() {
        return channelIdentifier;
    }

    public String getVehicleIdentificationNumber() {
        return vehicleIdentificationNumber;
    }

    public void addInputData(final InputData inputData) {
        Validate.notNull(inputData);

        InputData existingData = this.data.get(inputData.getIdentifier());
        if (existingData != null) {
            existingData = mergeData(existingData, inputData);
        } else {
            existingData = inputData;
        }
        this.data.put(inputData.getIdentifier(), existingData);
    }

    public void addInputData(final Collection<InputData> inputData) {
        Validate.notNull(inputData);

        for (InputData entry : inputData) {
            addInputData(entry);
        }
    }

    private InputData mergeData(final InputData existingData, final InputData data) {
        Validate.notNull(existingData);
        Validate.notNull(data);

        existingData.addAccelerationTuples(data.getAccelerationTuples());
        GpsData gpsData = data.getGpsData();
        if (gpsData != null) {
            existingData.addGpsData(gpsData);
        }
        return existingData;
    }

    public Collection<InputData> getInputData() {
        return Collections.unmodifiableCollection(data.values());
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        for (final Map.Entry<String, InputData> entry : data.entrySet()) {
            ret.append("\t\t").append(entry.getValue()).append(":\n").append(entry.getValue());
        }
        return ret.toString();
    }
}
