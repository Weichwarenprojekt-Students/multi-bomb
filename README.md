# Multi-Bomb

## Description
This project is a multiplayer version of the game "Bomberman".
In Multi-Bomb 8 players can compete on a map consisting of quadratic fields and try to eliminate each other. 

Different weapons are available such as Bombs, Arrows and Swords to make the gameplay interesting. Furthermore different collectible buff items are also available, which for example affect speed, health or the explosion range of the bombs.

On a map there are obstacles, trees which are indestructible and mushrooms which can be destroyed by items. A few maps are included in the game, but new ones can easily be created with the built in map editor.

## Installation
For running the client you can use one of the official installers (.msi, .exe and .deb) from our [github.io page](Timbogen.github.io/Multi-Bomb).

If you don't want to or are not able to use the installers, you can also download the .jar file from the same page.
For the .jar file you need to have java 11 or higher installed!

Alternatively you can just clone the repository and build the project yourself.

## Running the server
For running just the server without the UI you need the .jar file and can't use an installer. The jar must be started with the `-s` command line argument.

The full syntax is 

```
java -jar Multi-Bomb.jar -s <serverName> <tickrate> <maxLobbies>
```

Where `<serverName>` is the display name of the server, `<tickrate>` is the rate in which the game loop runs and `<maxLobbies>` is the maximum number of lobbies that can be opened on the server.

## Troubleshooting
### High DPI Display Incompatibility On Windows
1. Right click on the application
2. Select Properties
3. Select Compatibility
4. Change settings for all users
5. Change high DPI settings
6. Check "Override high DPI scaling behaviour" and select "System (Enhanced)"
7. Confirm every dialog