fancyYooloo User-Manual
=======================
Build
--------------------
Just checkout the project and make a `mvn clean install` in the Code-directory

Startup
--------------------

### Server

`java -jar yooloo-server.jar`

### Client

`java -jar yooloo-client.jar`

Server-Configuration (server.properties)
--------------------

Just place a `server.properties`-File next to the `yooloo-server.jar`.

### Server

#### Defines the used server-port:

`server.port=44137`

### Game

#### Defines the total nuber of Players in a Game:

`game.size=3`

#### Defines the minimal count of real Players in a Game:

`game.min.players=3`

#### Defines the time the server schould wait for more Players, before some bots spawn (Seconds):

`game.bot.wait=100`

#### Prevent duplicated players (deactivate for legacy-clients):

`game.nameCheck=false`

-   `true`: on
-   `false`: off

### Bot

#### Sets gamemode of spawned bots:

`bot.mode=RANDOM`

-   `RANDOM`: random-deck

------------------------------------------------------------------------

Client-Configuration (client.properties)
--------------------

Just place a `client.properties`-File next to the `yooloo-client.jar`.

#### Prevent duplicated players (deactivate for legacy-servers):

`game.nameCheck=false`

-   `true`: on
-   `false`: off

#### Change the gamemode:

`game.play.mode=X`

-   `0`: random
-   `1`: 1 to 10
-   `2`: 10 to 1