[![Build Status](https://github.com/chrisblutz/jetway/workflows/CI%20-%20Unit%20Tests/badge.svg)](https://github.com/chrisblutz/jetway/actions)
[![Maintainability](https://api.codeclimate.com/v1/badges/48507ad376c7b9cb5d6b/maintainability)](https://codeclimate.com/github/chrisblutz/jetway/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/48507ad376c7b9cb5d6b/test_coverage)](https://codeclimate.com/github/chrisblutz/jetway/test_coverage)

# Jetway

Jetway is a Java library that provides a fast way to access information contained in the FAA's monthly NASR subscription files.
It uses Apache's XMLBeans and a compiled AIXM 5.1 schema to load data from the NASR files, clean it and convert it to Java types,
and then insert it into a database for quicker access in the future.

## Third-Party Libraries

#### Apache XMLBeans ([xmlbeans.apache.org](https://xmlbeans.apache.org/))

Jetway uses XMLBeans to load AIXM-formatted XML data.  XMLBeans is licensed under the
[Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0).

#### Apache Log4j 2 ([logging.apache.org/log4j](https://logging.apache.org/log4j/2.x/))

Jetway uses Log4j to log its internal processes and provide context to errors that might arise.  Log4j 2 is licensed under the
[Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0).

#### AIXM 5.1 XML Schema ([aixm.aero](http://aixm.aero/))

Jetway uses the AIXM 5.1 schema alongside XMLBeans to load the FAA's NASR files.  AIXM is a joint development of the US Federal
Aviation Administration, the US National Geospatial Intelligence Agency, and the European Organization for the Safety of Air Navigation.
The specific schema compiled and used by Jetway is distributed with each FAA NASR subscription file.

#### FAA Extensions to AIXM 5.1

Jetway uses the FAA's extensions to the AIXM 5.1 schema to load certain XML elements specific to the FAA's NASR data.

## Copyright

Jetway is licensed under the Apache License, Version 2.0.  See [LICENSE](LICENSE) for more information.
