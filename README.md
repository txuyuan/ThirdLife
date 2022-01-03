## ThirdLife Plugin
Merely a rudimentary lives system inspired by the [3rd Life](https://www.youtube.com/results?search_query=3rd+Life) and [Last Life](https://www.youtube.com/results?search_query=Last+Life) series on Youtube.

Each player is initially provided 7 lives (and the ability to become a ghoul), indicated by their name colour (4-7 lives = blue, 3 = green, 2 = yellow, 1 = red, ghoul = darkRed, dead = gray)

<br>

### Lives
Each player is initially given 7 lives (excluding the ghoul).

| Lives | Name colour                                 |
|-------|---------------------------------------------|
| 4-7   | <span style="color:blue">Blue</span>        |
| 3     | <span style="color:green">Green</span>      |
| 2     | <span style="color:yellow">Yellow</span>    |
| 1     | <span style="color:red">Red</span>          |
| Ghoul | <span style="color:darkred">Dark Red</span> |
| Dead  | <span style="color:grey">Grey</span>        |

### Blue lives
A blue life's death messages are hidden and their life-count hence kept secret

At the beginning of each session, blue lives will lose one life

### Ghoul
After losing their last life for the first time, a player becomes a ghoul, losing half of their healthbar.

In this state, they have one session's time to regain a life as all ghouls die at the beginning of each session. This can occur in 2 ways, either with another player giving them a life, or by the ghoul's unique ability to take lives by murder. However, they cannot harm or take lives from a fellow ghoul.

A player can only become a ghoul one time and hence the ghoul serves as a one-time chance to escape death





<br>


## Utilisation

### Commands

`thirdlife`  *(Alt alias: /tl)* << ***Admin version***
* `add <target>` - Adds one life to the specified target player. `<target>` defaults to self
* `remove <target>` - Removes one life from the specified target player. `<target>` defaults to self
* `get <target>` - Gets `<target>`'s life-count, `<target>` defaults to self
* `reset` - Resets life system (lives, ghouls)
* `newsession` - Starts new session (BlueLifes, Ghouls)
* `give <target>` - Gives life to other player

`thirdlife`  *(Alt alias: /tl)* << ***Player version***
* `get` - Gets own life-count
* `give <target>` - Gives life to other player

### Permissions

`thirdlife.use` - Player is included in the ThirdLife system and is affected by the livesAZ (Default:true)

`thirdlife.admin` - Player is allowed to manage the thirdlife system and use the /thirdlife command (Default:op)


<br></br>
---

Source code is licensed under [GNU GPL v3.0](./LICENSE)