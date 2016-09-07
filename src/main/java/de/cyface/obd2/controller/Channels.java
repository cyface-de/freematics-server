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
 * </p>
 *
 * @author Klemens Muthmann
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
public class Channels {
    private final Map<Integer,Channel> activeChannels;

    public Channels() {
        this.activeChannels = Collections.synchronizedMap(new HashMap<>());
    }

    public synchronized Channel getNextFreeChannel(final String vehicleIdentificationNumber) {
        int i = 0;
        while(activeChannels.get(i)!=null) {
            i++;
        }
        Channel ret = new Channel(i,vehicleIdentificationNumber);
        activeChannels.put(i,ret);
        return ret;
    }

    public synchronized  void closeChannel(final int channelIdentifier) {
        activeChannels.remove(channelIdentifier);
    }

    public Channel getChannel(int channelIdentifier) {
        return activeChannels.get(channelIdentifier);
    }
}
