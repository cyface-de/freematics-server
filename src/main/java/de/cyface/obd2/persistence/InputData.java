/*
 * Created on 06.09.16 at 16:52.
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
 * A value object for storing the parsed input data in a structured manner.
 * </p>
 */
public final class InputData {
    /**
     * <p>
     * The identifier of the parsed input line used to avoid duplication and merge acceleration
     * information to the corresponding location (GPS) information.
     * </p>
     */
    private final String identifier;

    /**
     * <p>
     * All accelerations collected within this {@code InputData}.
     * </p>
     */
    private final List<Acceleration> accelerations;

    /**
     * <p>
     * An object containing all the information from the GPS sensor captured for this {@code InputData}. This is an
     * optional value and only exists if GPS data was available during capturing.
     * </p>
     */
    private GpsData gpsData;

    /**
     * <p>
     * Creates a new completly initialized {@code InputData} object with the mandatory identifier.
     * </p>
     *
     * @param identifier The identifier of the parsed input line used to avoid duplication and merge acceleration
     *                   information to the corresponding location (GPS) information.
     */
    public InputData(final String identifier) {
        Validate.notEmpty(identifier);
        this.identifier = identifier;
        this.accelerations = new ArrayList<>();
    }

    /**
     * <p>
     * Adds another tuple of accelerations in x, y and z direction to this {@code InputData} object.
     * </p>
     *
     * @param ax Acceleration in device local x direction.
     * @param ay Acceleration in device local y direction.
     * @param az Acceleration in device local z direction.
     */
    public void addAccelerationTuple(final int ax, final int ay, final int az) {
        accelerations.add(new Acceleration(ax, ay, az));
    }

    /**
     * <p>
     * Adds all the acceleration tuples from the provided {@code Collection} to this {@code InputData} object.
     * </p>
     *
     * @param accelerations Accelerations to add to this object.
     */
    public void addAccelerationTuples(final Collection<Acceleration> accelerations) {
        this.accelerations.addAll(accelerations);
    }

    /**
     * <p>
     * Adds captured GPS data to this input data, but only if no previous value has been set. GPS receivers usually have
     * a lower frequency than for example accelerometers and thus not all input data has GPS data.
     * </p>
     *
     * @param gpsData An object containing all the information from the GPS sensor captured for this {@code InputData}.
     */
    public void addGpsData(final GpsData gpsData) {
        if (this.gpsData != null) {
            return;
        }
        this.gpsData = gpsData;
    }

    /**
     * @return The identifier of the parsed input line used to avoid duplication and merge acceleration information to
     * the corresponding location (GPS) information.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * @return All accelerations collected within this {@code InputData}.
     */
    public Collection<Acceleration> getAccelerationTuples() {
        return Collections.unmodifiableList(accelerations);
    }

    /**
     * @return An object containing all the information from the GPS sensor captured for this {@code InputData}. This is
     * an optional value and only exists if GPS data was available during capturing. If no data was available the
     * method returns {@code null}.
     */
    public GpsData getGpsData() {
        return gpsData;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        if (gpsData != null) {
            ret.append("\t\t\t").append("Timestamp: ").append(gpsData.getGpsTime()).append(" Lat: ")
               .append(gpsData.getLatitude()).append(" Lon: ").append(gpsData.getLongitude()).append("Speed: ")
               .append(gpsData.getGpsSpeed()).append(" Altitude: ").append(gpsData.getAltitude())
               .append(" Satellites: ").append(gpsData.getSattelites()).append("\n");
        }
        for (Acceleration acceleration : accelerations) {
            ret.append("\t\t\t").append("ax: ").append(acceleration.getAx()).append(" ay: ")
               .append(acceleration.getAy()).append(" az: ").append(acceleration.getAz()).append("\n");
        }
        return ret.toString();
    }
}
