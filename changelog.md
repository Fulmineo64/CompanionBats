# 1.16.0
- Updated to Minecraft version 1.17-pre1
- Added Simplified Chinese Localization (thanks to stan9558 & friends)

# 1.14.2 - 1.15.2
- Now Companion Bat's Classes can be translated. 

	(Many thanks to codehz for the [pull request](https://github.com/Fulmineo64/CompanionBats/pull/9))

# 1.14.1 - 1.15.1
- Bat flutes now show their bat's custom name
- Bat flutes now show a message instead of disappearing when their bat isn't around

# 1.14.0 - 1.15.0
## Portals and Teleports

### Ninjas
- Revisited Ninja's teleport logic
- The Ninja will now try to teleport whenever possible behind its target, with a much lesser cooldown than before
- Targets can be enemies or items to pick up
- When the target is an enemy the Ninja will automatically perform a guaranteed sneak attack
### Misc
- Summoned bats will now be automatically recalled when changing dimension (eg. by traveling through portals)

# 1.13.4
- Removed unused command_flute recipe

# 1.12.3 - 1.13.3
- Tweaked slightly the Duelist's buffs
- Fixed the Mummy's texture

# 1.12.3 only
- Added a new item, the Command Flute
- Through this flute you can now issue direct commands to your bats
- The available commands are: Attack enemies, Rest / Stop attacking, Toggle Guard mode 
- The commands can be changed by Shift-Right clicking the Command Flute
- The guarding behaviour of the Companion Bat has changed, by default it will NOT help you in battle if you don't attack
- This behaviour can be changed with the Toggle Guard feature of the Command Flute

# 1.12.2 - 1.13.2
- Duelists' lightning bolts no longer set the terrain on fire and no longer harm the bat itself or its owner
- Removed Absorption and Fire resistance buffs from the Duelists' combo buffs 
- Refined the damage scaling of Duelists' lighting bolts

# 1.12.1 - 1.13.1
- Fixed bug that caused disconnections from servers

# 1.12.0 - 1.13.0
## Potions and Thunderstorms

### Alchemists
- Lowered slightly the Alchemist's potions cooldowns
- Added a new treasure chest to the Swamp Hut
- The Alchemist Robe will now be found in the Swamp Hut's chest
### Duelists
- Duelists now show particles representing their current combo level
- Greatly reduced the available time between each combo hit (30s -> 10s)
- Halfed the maximum combo hits (100 -> 50)
- Duelists will now gain persistent buffs whenever their combo is active, these buffs will increase in strenght with the combo level
- Included 3 new powerful buffs that can be obtained nearing the end of the combo
- Added new final move for the Duelist when the 50th consecutive hit is reached that scales with the Duelist's Combo Attack ability level
### Misc
- Lava and poison damage will not be counted anymore as blockable attacks

# 1.10.0 - 1.11.0
- Reduced max class level exp requirement by 30% (2250 -> 1600)
- Adjusted intermediate class levels exp requirements

# 1.8.5 - 1.9.5
- Fixed minor bug with bats obtained by the creative menu
- Removed the "BoundingBox" property that should fix the bat displacement in modpacks 

# 1.8.4 - 1.9.4
- Reflavoured and retextured the Destroyed Gear
- Changed the Destoryer Gear crafting recipe

# 1.8.3 - 1.9.3
- Fixed bug that allowed to heal the bat each time it was summoned

# 1.8.2 - 1.9.2
- Replaced the Melon Bandana with the new Pumpkin Bandana
- Fixed the re-taming of the bat not working correctly

# 1.8.1 - 1.9.1
- Fixed missing Soul Shard recipe for Mummy Bandages and Destroyer Gear
- Added new accessory, Plated Boots, that grants the Increased Armor ability
- Added new accessory, Melon Bandana, that grants a new ability Natural Regeneration. This ability allows the bat to passively heal even when it isn't roosting

# 1.8.0 - 1.9.0
- Updated to Minecraft snapshot 21w13a
- Added new class: Destroyer
- The Companion Bat won't pick up the items thrown away by the owner
- Positive and negative effects will now persist even when the bat is recalled
- The bat will instantly be extinguished when returning to the owner's inventory

### Technical changes
- Redefined completely the bat entity storage, now all entity information will be saved with some exceptions
- Position, Fire ticks and Attributes will be cleared when the Companion Bat is stored in item form

# 1.6.0 - 1.7.0
- Redefined completely the Class Leveling System and the Companion Bat Ability System
- Now abilities of the same type obtained by different sources can stack
- Improved combat level up notifications
- Improved class level up notifications
- Added a new notification for Permanent Abilities
- Improved the Pie o' Enchanting tooltip
- Added the type of equipment for Accessories and Armors in their respective tooltips
- Added one extra level of Slowness to the Mummy class
- Changed the order of obtaining abilities for some classes, the final number of abilities remains the same
- The alchemist will now wait at least 3 seconds before throwing a potion when summoned

# 1.4.1 - 1.5.2
- The Companion Bat will now retaliate against its owner's attackers only if the owner has half health or less
- Fixed bug relative to the Bat Pouch that transformed the picked up potions into Uncraftable potions
- The Bat pouch now shows the contained item if present

# 1.5.1
- Increased slightly wither's ability effect duration

# 1.4.0 - 1.5.0
- Added new class: Mummy

# 1.2.6 - 1.3.6
- Increased the Companion Bat's base speed
- The roosting timeout now varies according to the bat's current health (10s when injured, 30s at full health)
- Changed the accessories tooltips to fit with the new ones  

# 1.2.5 - 1.3.5
- Added new armor tooltips that show obtainable class abilities
- Added hints to where to find each armor
- Bats can now be tamed with the Pie o' Enchanting, but without gaining any experience bonus

# 1.2.4 - 1.3.4
- The Companion Bat won't pick up items when the owner is mining, digging, chopping, etc. to avoid getting in the way
- Reduced the chance of finding a Pie o' Enchanting in chests
- Doubled the rolls for the Woodland Mansion chest
- Added the Pie o' Enchanting to stronghold's corridors chests
- Reduced the chance of finding a Ninja Garb in End cities chests

# 1.2.3 - 1.3.3
- Added new tooltip to the Companion Bat Item that shows the Companion Bat Abilities owned
- The new Pie o' Enchanting can now be found in: dungeons, shipwrecks, pillager outposts, woodland mansions and abandoned mineshafts
- Fixed shift clicking the Bundle and the Bat Pouch in the bat's Gui

# 1.2.2 - 1.3.2
- Added the Pie o' Enchanting, a craftable food that grants a moderate amount of exp to the Companion Bat

# 1.2.1 - 1.3.1
- When the owner attacks or is attacked, the Companion bat will try to find the enemy even if it isn't directly visible

# 1.2.0 - 1.3.0
- Added a new accessory system
- Added 4 new accessories that alter the bat's behaviour towards other entities
- Knights can block attacks more often at max level
- Duelists gain regeneration more often
- Duelists now gain even stronger potion effects as their combo level raises

# 1.0.9 - 1.1.5
- Looters and Alchemists can now attack
- Looters will receive an added Looting bonus that scales with class levels, up to Looting IV
- Looters' permanent ability now grants looting instead of speed
- Alchemists can now use effect potions and emergency potions a little more often
- Fixed armor duplication glitch
- Improved shift-clicking items around in the Companion Bat's GUI

# 1.0.8 - 1.1.4
- Fixed speed level up notification
- Updated to Minecraft snapshot 21w08b

# 1.0.7 - 1.1.3
- When taming, the Companion Bat item will now be dropped if the tamer's inventory is full
- Reduced max health (18 -> 16) and added more movement speed (0.40 -> 0.45) at max level
- The Companion Bat can now find a roost spot farther away from the player

# 1.0.6 - 1.1.2
- Companion Bats can now be tamed on servers!
- Updated the bundle's integration to work server side.

# 1.0.5
- Updated to Minecraft snapshot 21w07a

# 1.0.4 - 1.1.1
- Fixed spawning of the Companion Bat on path blocks and other non-full blocks
- The alchemist's potions cooldown will now be kept when the bat is recalled

# 1.0.3
- Buffed slightly the duelist's regeneration effect
- Fixed bug with class levels at max exp

# 1.1.0
- Ported to Minecraft version 1.16.5
- Added bat pouch as an inferior replacement of the bundle
- Updated the crafting recipes

# 1.0.2
- Updated minecraft version to 21w06a

# 1.0.1
- Fixed CurseGradle integration
- Updated minecraft version to 21w05b

# 1.0.0
Initial release