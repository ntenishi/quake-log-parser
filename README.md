# Quake Log Parser

## Introduction

It's a parser used for gathering data from Quake 3 Arena log files.

## Requirements

- Java
- Gradle

## Usage

After cloning the project, run the command:

`$ ./gradlew run`

Or:

`$ ./gradlew jar`

`$ java -jar build/libs/QuakeLogParser-1.0.jar src/main/resources/qgames.log`


## Output

Outputs grouped information for each match to stdout. Example:

```json
"game_6" : {
  "total_kills" : 130,
  "players" : [ "Dono da Bola", "Chessus", "Assasinu Credi", "Mal", "Isgalamido", "Zeh", "Oootsimo" ],
  "kills" : {
    "Oootsimo" : 20,
    "Assasinu Credi" : 16,
    "Isgalamido" : 12,
    "Dono da Bola" : 8,
    "Zeh" : 7,
    "Chessus" : 0,
    "Mal" : -3
  },
  "kills_by_means" : {
    "MOD_MACHINEGUN" : 9,
    "MOD_TRIGGER_HURT" : 20,
    "MOD_RAILGUN" : 9,
    "MOD_FALLING" : 7,
    "MOD_ROCKET_SPLASH" : 49,
    "MOD_ROCKET" : 29,
    "MOD_SHOTGUN" : 7
  }
}
```

### Fields:

- **total_kills** is the sum of all deaths in each game.
- **players** is an array of all players name that played in each game.
- **kills** is a map with player's name and the respective kill score, ordered by kill score descending.
- **kills_by_means** is a map with deaths count grouped by death cause for each game.

### Notes:

- The game data is only being collected for games that two or more players were active at the same time. A player is 
considered active when there is an entry in the log like: ` 01:22 ClientBegin: 5`
- When `<world>` kills a player or when the player kills himself, that player loses -1 kill score. 
