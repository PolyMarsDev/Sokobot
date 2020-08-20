# Sokobot

Sokobot is a Discord bot written with [JDA](https://github.com/DV8FromTheWorld/JDA) that lets you play [Sokoban](https://en.wikipedia.org/wiki/Sokoban), the classic box-pushing puzzle game.

## Screenshots
![Level 1](https://cdn.discordapp.com/attachments/670425377503707146/727568442034487316/sokobot_v1.1.gif)
![Level 2](https://cdn.discordapp.com/attachments/670425377503707146/727567694597193829/sokobot_v1.1_.gif)

## Features
### Infinite levels
The maps in Sokobot are randomly generated, increasing in difficulty as you progress.
### Varied controls
Sokobot has multiple control options to improve the player's experience, including reactions and wasd commands!
### Simultaneous games
Thanks to the power of Java HashMaps™️, multiple users can use the bot at the same time without interfering with one another.
### Custom prefixes ``New!``
To prevent Sokobot from conflicting with other bots, admins can choose any single-character prefix to preface Sokobot's commands.

## Commands
### User
- ``!play`` can be used to start a game if you are not currently in one.
- ``!stop`` can be used to stop your active game at any time.
- ``!info`` provides some useful details about the bot and rules of the game.
### Admin ``New!``
- ``!prefix [character]`` can be used to change the prefix the bot responds to in the current server. 

## Usage
### Public host ``New!``
Sokobot is available on top.gg and can be added to your server [in one click](https://top.gg/bot/713635251703906336/)! 
### Self-hosting
Grab the [latest .jar](https://github.com/PolyMarsDev/Sokobot/releases) or [build it yourself](#compiling). Then, create a Discord Bot Application [here](https://discord.com/developers/applications/) and paste the bot token into ``token.txt``. Then, ensure the two files are in the same directory and run the .jar file.



## Compiling

Install [Java 8 JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) and [Gradle](https://gradle.org/).
In the root folder of the project, execute ``gradlew shadowJar``.
The compiled .jar file will be located in ``build/libs``.

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change. Feel free to create a fork and use the code for any noncommercial purposes.
