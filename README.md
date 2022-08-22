# ILO2RemoteConsole
ILO2 Java Remote console as Standalone version

Tested with Java 8 and Java 11. Hangs for an unknown reason on Java 15.

# Usage

Because even on the latest firmware (2.33 as of 2021-04-16),
due to hardware limitations, iLO2 does not support modern TLS (and ciphers).
Therefore, adjusting the JRE's security settings is necessary. Seemingly, this can not be done at runtime,
so a custom security file has to be passed to Java. That is what the `-Djava.security.properties=java.security` part does.

if this still fails with a TLS related error, the certificate in use by your ILO might still rely on pre-2.33 ciphers.
In that case, regenerate or replace it through the ILO web interface.

You can either pass host, username and password as arguments:

```java -Djava.security.properties=java.security -jar ILO2RemCon.jar <Hostname> <Username> <Password>```

Or you can use a config file (for an example, see `config_template.properties`):

``java -Djava.security.properties=java.security -jar ILO2RemCon.jar -c <Path to config.properties>``

Running the Application without arguments will try using a `config.properties` in the current working directory.


