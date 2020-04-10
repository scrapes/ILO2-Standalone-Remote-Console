# ILO2RemoteConsole
ILO2 Java Remote console as Standalone version

# Usage

You can either pass host, username and password as arguments:

```java -jar ILO2RemCon.jar <Hostname> <Username> <Password>```

Or you can use a config file (for an example, see `config.json_template`):

``java -jar ILO2RemCon.jar -c <Path to config.json>``

Running the Application without arguments will try using a `config.json` in the current working directory.