Cyface Demo Server for Freematics OBD II Dongle V4
==================================================
This software is a case study server developed as part of a [blog post](https://www.cyface.info/index.php/2016/09/12/how-to-log-telematics-data-to-the-cloud-using-freematics-obd2-dongle-v4-und-java/) we published on our homepage.
It is capable of handling data transmitted from a Freematics OBD (On-Board-Diagnostics) Dongle.
This dongle is Open Hardware and may be purchased at http://freematics.com/store/index.php?route=product/product&path=20&product_id=82.

Cyface is in no way associated with Freematics.


Prerequisites
-------------
To use this server you need a FreematicsONE version 4 OBD 2 dongle with the ESP8266 Wifi module and some server hardware.
For simple tests a common PC or Mac should suffice.
The server was developed and tested on a Dell XPS 13 (9350) running Ubuntu Linux 16.04.

The dongle with the GSM module might work, but has not been tested.
I guess if you want to use that one you need to make the server available on the internet.
Beware that the server has not been developed with safety and security in mind!

Running the Server
------------------
The server is implemented using SpringBoot and thus based on the SpringFramework.
Spring is a [Java](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) software stack, so you are going to need a Java installation on your system.
Since GitHub contains only the sources of the server you need the Java Development Kit (JDK) and not the smaller Java Runtime Environment (JRE).
Actually the JDK contains a JRE so you do not need to download a JRE separately.
On Ubuntu you may simply install the OpenJDK via: `sudo apt install default-jdk` or follow the nice [tutorial](http://www.webupd8.org/2012/09/install-oracle-java-8-in-ubuntu-via-ppa.html) from WebUpd8 Team to get the Oracle JDK if you don't trust OpenJDK.

Clone the server repository from GitHub using [Git](https://git-scm.com/).
On Ubuntu Linux this works from the command line with:
`> git clone https://github.com/cyface-de/freematics-server.git`
You will be asked for your GitHub credentials, so register a free account.

A successful clone places all the source files in your current directory.
From there build and run the server using [gradle](https://gradle.org/), which thanks to the gradle wrapper you do not need to install :).
`> ./gradlew clean build check bootRun`
If all works well the build process should stop with a line telling you something like `Started MessboxServerApplication in 2.929 seconds`.
To stop the server you can just close the window.
This also means you should not close the window after you entered the command! ;)
The server runs as long as the window is open on TCP port 8080.
To test this you can enter `http://localhost:8080` in your Web browsers address bar and should see a blank white screen.
If you see an error message, something went wrong.
Study the output you get from your command line/terminal to see what happened.

To prepare the server to receive data you need to setup a connection for the FreematicsONE.
During development I used to setup my laptop as access point for the dongle.
On Ubuntu Linux 16.04 this works via the network manager, for example like [so](http://ubuntuhandbook.org/index.php/2016/04/create-wifi-hotspot-ubuntu-16-04-android-supported/).
On older versions of Ubuntu it requires some hacking in some configuration files, for example like [so](http://ubuntuhandbook.org/index.php/2014/09/3-ways-create-wifi-hotspot-ubuntu/).
It should be possible on Windows too but my attempts have been unsuccessful so far.
This might just be my inability to do anything more complex than starting a game on Windows.
So if you are a Windows user I am sure you'll manage.

Running the OBD II Software
---------------------------
To submit data to the server you need the Arduino telelogger sketch for the FreematicsONE.
It is available from the Freematics GitHub repository.
However do not use that version at the moment.
We created a fork and made some fixes to the code, that are mandatory for the telelogger to work.
There is a pull request and as soon as that one has been merged you might also use the Freematics code directly.
Until then checkout the FreematicsONE sketches from [here](https://github.com/cyface-de/Freematics) using a command like:
`> git clone https://github.com/cyface-de/Freematics.git`
Install an [Arduino IDE](https://www.arduino.cc/en/Main/Software)!
Open the file `firmware_v4/telelogger_esp8266/telelogger_esp8266.ino` in your IDE.
This should automatically open the files `config.h` and `datalogger.h` in separate tabs.
In `config.h` you can setup the data for you Wifi hotspot: SSID, password and server address.
The default server address if you are running the hotspot is `10.41.0.1`.

If you do not run your test inside a car and have no [OBD 2 emulator](http://freematics.com/store/index.php?route=product/product&product_id=71) you also need to change the line `#define USE_OBDII 1` to `#define USE_OBDII 0` in `config.h`.
Since I own no emulator and my bike has no OBD 2 port, this is a patch I added.
It is not available in the official sources yet.

To run the telelogger on your FreematicsONE connect the dongle via USB to your PC!
Click the upload button in the upper left corner of your Arduino IDE!
As soon as the log window in the lower are of the IDE tells you `avrdude done. Thank you!` hit `Ctrl+Shift+M` to see the output from your dingle.
It should successfully connect to your Wifi and start transmitting data.

Since the server stores everything in memory the time will come when your memory is full and the server will crash.
So do not let it run forever.
There will be errors!
The connection will be reset from time to time and it might take some time at the beginning until the first data is transmitted.
The server might also throw some error messages if it encounters not implemented entries.

License
-------
The Cyface freematics server is available under MIT License.
This means:

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

Outlook
-------
Over time and as we require new functionality this server might become more stable and support additional features.
Since we are a small team we are also happy about pull requests providing fixes or new features.
