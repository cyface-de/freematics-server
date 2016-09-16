/*
 * Created on 07.09.16 at 08:00.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de.cyface.obd2.persistence;

/**
 * <p>
 * A class for a value object storing all information provided by the GPS sensor.
 * </p>
 *
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
public final class GpsData {
    /**
     * <p>
     * The GPS time as a UTC timestamp.
     * </p>
     */
    private final long gpsTime;

    /**
     * <p>
     * Geographic latitude as a decimal value.
     * </p>
     */
    private final double latitude;

    /**
     * <p>
     * Geographic longitude as a decimal value.
     * </p>
     */
    private final double longitude;

    /**
     * <p>
     * The altitude for this input data.
     * </p>
     */
    private final int altitude;

    /**
     * <p>
     * The GPS speed for this input data.
     * </p>
     */
    private final double gpsSpeed;

    /**
     * <p>
     * The sattelite information for this input data.
     * </p>
     */
    private final int satellites;

    /**
     * <p>
     * Creates a new completely initialized object of this class. This is a read only object, so all values are provided
     * via its constructor.
     * </p>
     *
     * @param gpsTime    The GPS time as a UTC timestamp.
     * @param latitude   Geographic latitude as a decimal value.
     * @param longitude  Geographic longitude as a decimal value.
     * @param altitude   The altitude for this input data.
     * @param gpsSpeed   The GPS speed for this input data.
     * @param satellites The satellite information for this input data.
     */
    public GpsData(final long gpsTime, final double latitude, final double longitude, final int altitude,
            final double gpsSpeed, final int satellites) {
        this.gpsTime = gpsTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.gpsSpeed = gpsSpeed;
        this.satellites = satellites;
    }

    /**
     * @return The GPS time as a UTC timestamp
     */
    long getGpsTime() {
        return gpsTime;
    }

    /**
     * @return The GPS speed for this input data.
     */
    double getGpsSpeed() {
        return gpsSpeed;
    }

    /**
     * @return The altitude for this input data.
     */
    int getAltitude() {
        return altitude;
    }

    /**
     * @return Geographic latitude as a decimal value.
     */
    double getLatitude() {
        return latitude;
    }

    /**
     * @return Geographic longitude as a decimal value.
     */
    double getLongitude() {
        return longitude;
    }

    /**
     * @return The satellite information for this input data.
     */
    int getSatellites() {
        return satellites;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GpsData gpsData = (GpsData)o;

        if (gpsTime != gpsData.gpsTime) {
            return false;
        }
        if (Double.compare(gpsData.latitude, latitude) != 0) {
            return false;
        }
        if (Double.compare(gpsData.longitude, longitude) != 0) {
            return false;
        }
        if (altitude != gpsData.altitude) {
            return false;
        }
        if (Double.compare(gpsData.gpsSpeed, gpsSpeed) != 0) {
            return false;
        }
        return satellites == gpsData.satellites;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int)(gpsTime ^ (gpsTime >>> 32));
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int)(temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int)(temp ^ (temp >>> 32));
        result = 31 * result + altitude;
        temp = Double.doubleToLongBits(gpsSpeed);
        result = 31 * result + (int)(temp ^ (temp >>> 32));
        result = 31 * result + satellites;
        return result;
    }
}
