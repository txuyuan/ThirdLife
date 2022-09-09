## ThirdLife Plugin
This plugin is inspired by the [3rd Life](https://www.youtube.com/results?search_query=3rd+Life) and [Last Life](https://www.youtube.com/results?search_query=Last+Life) series on Youtube, with various changes to suit the needs of Firebush.

<br>

### Lives
Each player is initially given 3 lives (excluding the ghoul).

| Lives | Name colour                                 |
|-------|---------------------------------------------|
| 3     | <span style="color:green">Green</span>      |
| 2     | <span style="color:yellow">Yellow</span>    |
| 1     | <span style="color:red">Red</span>          |
| Ghoul | <span style="color:darkred">Dark Red</span> |
| Dead  | <span style="color:grey">Grey</span>        |

### Ghoul
After losing their last life for the first time, a player becomes a ghoul, losing half of their healthbar.

In this state, they have one session's time to regain a life as all ghouls die at the beginning of each session. This can occur in 2 ways, either with another player giving them a life, or by the ghoul's unique ability to take lives by murder. However, they cannot harm or take lives from a fellow ghoul.

A player can only become a ghoul one time and hence the ghoul serves as a one-time chance to escape death

### Shadow Touch
A player is chosen at the start of every session to be given the Shadow Touch. 
It can be transferred to other players by punching them (damaging them). 

At the end of the session, the person with the shadow touch will die



<br>


## Utilisation

### Commands

`thirdlife`  *(Alt alias: /tl)* << ***Admin version***
* `add <target>` - Adds one life to the specified target player. `<target>` defaults to self
* `remove <target>` - Removes one life from the specified target player. `<target>` defaults to self
* `get <target>` - Gets `<target>`'s life-count, `<target>` defaults to self
* `reset` - Resets life system (lives, ghouls)
* `newsession` - Starts new session (allocate Shadow)
* `endSession [now|countdown|cancel]` - Ends session immediately, with 10 minute countdown, or cancels countdown
* `nick <ownNick>` | `nick <target> <targetNick` - Assigns nick to player or self, ampersand colour codes work
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
