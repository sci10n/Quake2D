# Quake 2-D

A fast-paced top-down shooter written for the course TNM095 "AI for Interactive Media" at LiTH. It features a full *behaviour tree* implementation for the bot's decision making process, a homebrew *pathfinding system* using *a-star* and a method for evolving the behaviour tree and its nodes/actions/condition by using *genetric programming*. We have written a paper on this as well called *Behaviour Tree Evolution by Genetic Programming* which you should find on the internet and can be compiled here as well (see below)...

## Report and Documents

You can find the bundled technical report in the `documents` folder. Assuming you have `pdflatex` and company, just go into that directory and issue `make`. If you are on Windows or non-Unix system, too bad... Download MinGW or get a real operating system. After that you should have a `report.pdf`. You know what to do after this I hope.

## Building and Running

If you've ever built anything using Gradle. It's obvious:

### Eclipse and IDEA IDE

1. Download the Gradle plugin for Eclipse or IDEA.
2. Clone this repository to the desired directory.
3. I dunno, press buttons until it works my guess.

### In the Command Line

1. Fetch the latest implementation of JDK and JRE.
2. Clone this repository to the desired directory.
3. Run the `gradlew` wrapper (packaged) for setup.
4. Hopefully everything worked nicely.
5. Finally: `gradlew desktop:run`

### Playing the Game

- Walk around with W, A, S, D.
- Aim with the mouse if you are playing.
- Shoot with SPACE or with left mouse button.
- The keys 1, 2, 3 load the built-in levels.
- Switch a bot's BT visualizer with TAB.
- Pause the game for inspection with P.
- Step forward game by holding in P.
- Resume the game by pressing R.
- Use E to toggle evolution.
- In evolution, use F to fast-forward.
- Use the left and right to FF slower/faster.
- Press Q to restart the level.

## Library Dependencies

Most of this will be fetched automatically by `gradlew`, but in case you want to do things the old fashioned-way, here is a list of the libraries, Java versions and tooling used. It might still work with older libraries of versions of Java, but you will be treading in unknown territory. You will need to fix any issues that may arise yourself. You've been warned.

- Java 8-ish
- Gradle 4.2
- Box2D 2.3.1
- libGDX 1.9.6

## Acknowledgements

All our assets are taken from elsewhere, since we are programmers, we suck at making art and audio. Luckily some people have released some good quality assets for free and under a permissive license (creative commons). This enables us to still keep our MIT license for the game.

- Kenney's Game Assets (CC 3.0)
- Sounds by the SFXR Tool (MIT License)
- A Drop A Day - As Far as it Gets (CC 3.0)
