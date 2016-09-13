/*
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de.cyface;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <p>
 * This is the entry point for the SpringBoot enabled Freematics server application.
 * </p>
 */
@SpringBootApplication
public class MessboxServerApplication {

    /**
     * Private constructor to prevent everyone from creating an object of this class.
     */
    private MessboxServerApplication() {
        // Nothing to do here.
    }

    /**
     * <p>
     * Main method used to start the server.
     * </p>
     *
     * @param args Takes command line arguments but is ignored in this case.
     */
    public static void main(String[] args) {
        SpringApplication.run(MessboxServerApplication.class, args);
    }
}
