trusted-qsl
===========

Java library package to manage Trusted Qsl and GAbbI file format, with hooks to upload to Logbook of the World.

== Using the library

The operator is required to extend the WriteGabbi class. There are 4 abstract functions, plus a constructor required to use this function.

* getApplicationName() Return your application name
* getStations() Return all possible stations that you might want to upload. Each station should follow the Station class, which is a class with all public variables, see the source code for more help.
* getQsoData- For the provided QsoData, return all of the Qsos you want to include.
* publishProgress- This will let you know how far along the process is, you can ignore it if you don't care about the progress.
* Constructor- Takes a KeyStore, password, and an optional alias. The KeyStore can be read from a .p12 file, like ARRL uses, by reading it with the static getKeyStore function

From your extended class, you simply need to provide an OutputStream via the write(OutputStream) function, and you'll write every contact provided to a GAbbI formatted file. 

Coming soon is an example of how to use this to write and upload an ARRL signed Logbook of the World file, and upload it to the LotW server.

== Stability

As of right now, the library is in considerable flux, and many change with any build. However, there are some areas which should be constant enough to build off of for future use.

* Station - Names will remain constant, but some fields might be added, and some types might change, to facilitate Error checking.
* QsoData - Same as Station
* Examples- Will change as better code is available. Working to improve the example, and directly output a .tq8 file from the example.
* HamBand- Should remain unchanged, except perhaps to tweak or add functionality. The Band should remain constant.
* WriteGabbi- The API should remain constant, but there might be some tweaks, especially in the constructor.


If you would like to assist, please contact me, and we can work together to improve the tool!
