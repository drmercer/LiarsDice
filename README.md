LiarsDice
=========

An open-source command prompt liar's dice game. Download the JAR and launcher batch files [here](export/)!

Running the game
----------------
To play the game, you will need to have a Java Runtime Environment (JRE) 
installed. If you don't have Java in your PATH, you may have to type out
the path to java.exe.

To run the game as a **server**, just run the jar from the command line:
```
java -jar program.jar
```

You can optionally specify the port with the `--port` argument (the
default is **4444**):
```
java -jar program.jar --port 1234
```

To **join a server** on your local network, add the `--join` argument:
```
java -jar program.jar --join
```

You can also specify the IP address and port of the server immediately
after the `--join` argument:
```
java -jar program.jar --join 123.45.67.89 1234
```

Other options
-------------

You can also add a few other arguments. Note that most of these only apply
when creating a game (not joining), as logic dictates.

`--no-remotes`
    Will not ask if you wish to add remote players (i.e. it will be a 1-player
    game against computer players).

`--num-of-computers <number>`
    Specifies the number of computers. Ignored if the number is invalid.

`--dice-per-player <number>`
    Specifies the number of dice per player. Ignored if the number is invalid.
    
`--name <name>`
    Specifies your player name.

There is also at least one debugging/cheat argument, which I won't document
here to make it more interesting. :)
