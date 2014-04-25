trusted-qsl
===========

Java library package to manage Trusted Qsl and GAbbI file format, with hooks to upload to Logbook of the World.

## Using the library

The operator is required to extend the WriteGabbi class. There are 4 abstract functions, plus a constructor required to use this function.

* getStations() Return all possible stations that you might want to upload. Each station should follow the Station class, which is a class with all public variables, see the source code for more help.
* getQsoData- For the provided QsoData, return all of the Qsos you want to include.
* publishProgress- This will let you know how far along the process is, you can ignore it if you don't care about the progress.
* Constructor- Takes a KeyStore, password, and an optional alias. The KeyStore can be read from a .p12 file, like ARRL uses, by reading it with the static getKeyStore function. By default, the first alias is used, unless otherwise specified.

If you want to write a class, provide an OutputStream to the write() function to write a new file. If you want to directly upload to Logbook of the World, simply call the writeToLotw, providing a file name (Fake, must end in .tq8) to include with the upload.

## Stability

Right now the system functions, and as a result I plan on not changing the use of this as much as possible. I do reserve teh right to make changes, but you can use it as you please.

## Assistance


If you would like to assist, please contact me, and we can work together to improve the tool! The best thing that can be done now is to test it, use it, and improve the pre-upload verification.

## License

The majority of this code is licensed via the MIT license. A few came from other open sourced projects.

The MIT License (MIT)

Copyright (c) 2014 Ben Pearson, http://www.kd7uiy.com

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
