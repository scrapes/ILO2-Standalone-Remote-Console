# ILO2RemoteConsole
ILO2 Java Remote console as Standalone version

Tested with Java 8

# Usage

Because even on the latest firmware (2.33 as of 2021-04-16),
due to hardware limitations, iLO2 does not support modern TLS (and ciphers).
Therefore, adjusting the JRE's security settings is necessary. Seemingly, this can not be done at runtime,
so a custom security file has to be passed to Java.

You can either pass host, username and password as arguments:

```java -jar ILO2RemCon.jar -Djava.security.properties=java.security <Hostname> <Username> <Password>```

Or you can use a config file (for an example, see `config.json_template`):

``java -jar ILO2RemCon.jar -Djava.security.properties=java.security -c <Path to config.json>``

Running the Application without arguments will try using a `config.json` in the current working directory.

Because even on the latest firmware (2.33 as of 2021-04-16),
due to hardware limitations, iLO2 does not support modern TLS (and ciphers).
Therefore, adjusting the JRE's security settings is necessary. Seemingly, this can not be done at runtime,
so a custom security file has to be passed to Java.
That is what the `-Djava.security.properties=java.security` part does.