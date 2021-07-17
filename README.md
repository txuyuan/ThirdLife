## ThirdLife Plugin
### Rudimentary System to implement the 3rd-life idea (Source:YT)

### Summary
Each person has 3 lives to expend in the world, indicated by the colour of their name (green, yellow, red)

Upon using up the last life, the player will be automatically be put into spectator mode and only be allowed to view the others


### Commands:
/thirdlife(tl):
 > /thirdlife reset
 >> Resets the lives of all players back to 3 and restores everyone to survival mode
 > /thirdlife add <target>
 >> Adds one life to the specified target player. If <target> field is empty, command sender will be target
 > /thirdlife remove <target>
 >> Removes one life from the specified target player. If <target> field is empty, command sender will be target
 
### Permissions:
 > thirdlife.bypass
 >> Player is ignored by the thirdlife system, lives are not lost, player does not participate | Default:false
 > thirdlife.admin
 >> Player is allowed to administrate the thirdlife system and use the /thirdlife command | Default:op
