Play! with Reactive Couchbase and SecureSocial
=====================================

About
--------
This application is an example of SecureSocial integration in Play! Scala
backed up by Couchbase using Reactive Couchbase driver.

Usage
--------
This sample uses system variables for configuration, so you may export some
and use play dist to run the application :
```sh
export COUCHABSE_HOST=
export COUCHABSE_PORT=
export COUCHABSE_BASE=
export COUCHABSE_BUCKET=
export COUCHABSE_USER=
export COUCHABSE_PASS=

export GMAIL_USER=
export GMAIL_PASSWORD=
export EMAIL_ADDRESS=

export TWITTER_CONSUMER_KEY=
export TWITTER_CONSUMER_SECRET=
export FACEBOOK_CLIENT_ID=
export FACEBOOK_CLIENT_SECRET=
export GOOGLE_CLIENT_ID=
export GOOGLE_CLIENT_SECRET=
```

Or you can just update the conf files to your convenience.

Versions used
--------
[Play! framework](http://www.playframework.com/) 2.2.1
[SecureSocial](http://securesocial.ws/) 2.1.2
[Reactive Couchbase](http://reactivecouchbase.org/) 0.1-SNAPSHOT

License
--------
This software comes under the BEER-WARE Licence.

Copyright (c) 2014 Matthieu Clochard

"THE BEER-WARE LICENSE" (Revision 42):
Matthieu Clochard (clochard.matthieu@gmail.com) wrote this software. As long as
you retain this notice you can do whatever you want with this stuff. If we meet
some day, and you think this stuff is worth it, you can buy us a beer in return.
