Hardware: Raspberry Pi 3 Model B <BR />
OS: RaspAnd 6.0.1 <BR />
  http://store.payloadz.com/details/2506110-software-home-and-desktop-raspand-6-0-1-for-raspberry-pi-3-and-pi-2-build-160915-faster-and-more-responsive-than-ever.html
<BR />
**Installation Instructions:**<BR />
  1) Obtain image file provided in link (9$) or request from group<BR />
  2) Using a 16Gb or greater microSD card:<BR />
  -2a) Windows:<BR />
    - Use Win32DiskImager to install<BR />
  -2b) Linux:<BR />
    - Run df without the SD card inserted<BR />
    - Insert SD card<BR />
    - Run df<BR />
    - New drive listed is SD card<BR />
    - Run 'dd if=system.img of=/dev/[SD DRIVE] bs=1M<BR />
      -- if: path to image file (from)<BR />
      -- of: path to drive (to)<BR />
 <BR />
    Insert the SD Card and power on the Raspberry Pi. This may take 10-15 minutes for the initially boot. Afterwards, go to settings -> about -> restart and restart the device to allow the OS to optimize applications.<BR />
<BR />
**Connecting to the Device (requires linux kernal):**<BR />
    - Wireless network options are available from the home screen. Open this and connect to your wireless network. Record the IP address.<BR />
    - Go to settings -> apps -> system apps and scroll over until you find 'Bluetooth Settings.' Open this app and enable Bluetooth. Ensure the current mode is set to 'Discoverable and Pairable.'<BR />
    - On your PC/Linux machine, install Android Debugger (sudo apt install android-tools-adb). Run the follow commands:<BR />
      - adb start-server<BR />
      - adb connect (IP previously recorded)<BR />
      - adb devices<BR />
    Your Raspberry Pi will be shown in the list displayed if a connection was made.<BR />
  <BR />
**Additional Applications:**<BR />
  Aptoide (sideloaded)<BR /> 
    - https://m.aptoide.com/installer
    - Use the link above to download the Aptoide APK and save it to a memorable location.<BR />
    - Open a terminal in that location and run:<BR />
      - adb devices (to ensure connection is still established)<BR />
      - adb install (filename of aptoide APK).apk<BR />
      <BR />
  SwiftKey (installed from Aptoide)<BR />
    - Go to settings -> apps -> downloaded apps and open Aptoide. Search for and install the latest SwiftKey version.<BR />
    
**Footkeys Installation (requires linux kernal):** <BR />
   Clone this GitHub repository and load it into Android Studio, or pull it directly from Android Studio using the built in version control software. After loading, build the APK and save it to a memorable location.
   Open a terminal in that location and run:<BR />
    - adb devices (to ensure connection is still established)<BR />
    - adb install (filename of built APK).apk<BR />
<BR />
Your Raspberry Pi 3 Model B is now configured for use with our application. Continue the steps below to load the necessary software on your PC. Application may work without this software, but behavorior and compatibility is indeterminate.<BR />

**Footkeys Windows Application Installation (requires Visual Studio):**
  https://github.com/mausilio/SwiftFootKeysWindowsApp
  - Clone the GitHub repository at the location above.<BR />
  - Open the .sln file<BR />
  - Press F5 to run the application.<BR />
    -- You may need to these dependencies to the linker (msvcrt.lib;msvcmrt.lib;ws2_32.lib;Bthprops.lib)<BR />
    -- Project -> Project Properties -> Linker -> Input -> Additional Dependencies <BR />
