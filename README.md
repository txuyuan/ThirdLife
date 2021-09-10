## ThirdLife Plugin
Merely a rudimentary lives system inspired by the [3rd-Life](https://www.youtube.com/results?search_query=3rd+Life) series on Youtube. 

Each person has 3 lives to expend in the world, indicated by the colour of their name (green, yellow, red). Upon using up the last life, the player will be automatically be put into spectator mode and only be allowed to view the others. This was originally made to have a hardcore-esque feel but without the one-time risk that makes it so nerve-wracking and someone dull in multiplayer environments. Keep in mind I made this in a very short time, so it is very rough around the edges. 


**Commands**

`thirdlife` - Main command for the plugin *(Alt alias: /tl)*
 * `reset` -  Resets the lives of all players back to 3 and restores everyone to survival mode
 * `add <target>` - Adds one life to the specified target player. If <target> field is empty, command sender will be target
 * `remove <target>` - Removes one life from the specified target player. If <target> field is empty, command sender will be target
 
**Permissions**
 
`thirdlife.bypass` - Player is ignored by the thirdlife system, lives are not lost, player does not participate (Default:false)
 
`thirdlife.admin` - Player is allowed to manage the thirdlife system and use the /thirdlife command (Default:op)
 
 <br></br>
 ---
 
 Source code is licensed under [GNU GPL v3.0](./LICENSE)
