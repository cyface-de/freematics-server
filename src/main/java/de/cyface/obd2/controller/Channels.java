/*
 * Created on 06.09.16 at 16:31.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de.cyface.obd2.controller;

import de.cyface.obd2.persistence.Channel;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Class for an object managing the currently active channels on the server.
 * </p>
 *
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
public class Channels {
    /**
     * <p>
     * Mapping from the channel identifier to the channel object for all the active channels.
     * </p>
     */
    private final Map<Integer, Channel> activeChannels;

    /**
     * <p>
     * Creates a new completely initialized object of this class.
     * </p>
     */
    public Channels() {
        this.activeChannels = Collections.synchronizedMap(new HashMap<>());
    }

    /**
     * <p>
     * Provides the next free channel. {@link Channel} identifiers are assigned as increasing number. If one in the
     * middle is removed this method will find the missing value and assign it.
     * </p>
     * <p>
     * This method is thread safe to avoid duplication for channel identifiers.
     * </p>
     *
     * @param vehicleIdentificationNumber The vehicle identification number for the new {@link Channel}.
     * @return The newly created {@link Channel}.
     */
    public synchronized Channel getNextFreeChannel(final String vehicleIdentificationNumber) {
        int i = 0;
        while (activeChannels.get(i) != null) {
            i++;
        }
        Channel ret = new Channel(i, vehicleIdentificationNumber);
        activeChannels.put(i, ret);
        return ret;
    }

    /**
     * <p>
     *     Closes the channel with the provided {@code channelIdentifier}.
     * </p>
     * @param channelIdentifier The channel identifier of the {@link Channel} to close.
     */
    public synchronized void closeChannel(final int channelIdentifier) {
        activeChannels.remove(channelIdentifier);
    }

    /**
     * @param channelIdentifier The identifier of the channel to get.
     * @return The {@link Channel} for the provided {@code channelIdentifier} or {@code null} if none exists.
     */
    public Channel getChannel(int channelIdentifier) {
        return activeChannels.get(channelIdentifier);
    }
}
