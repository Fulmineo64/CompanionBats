# 1.19.6
- Updated to Minecraft 1.20
- Changed speed icon in UI (➶ -> 🦇)

# 1.19.5
- Updated to Minecraft 1.19.4
- The Magic Protection ability, granted by the Ankh accessory, now guards against the Thorns enchantment
- Updated commands to work with the Netherite upgraded Bat Item
- Minor text fixes

# 1.19.4
- Updated to Minecraft 1.19.3
- Fixed unusable item slots on the Netherite Bat Item

# 1.19.3
- Updated to Minecraft 1.19.2

# 1.19.2
- Fixed and optimized bundle logic

# 1.19.1
- Updated to Minecraft 1.19
- Removed Cave House feature, because porting it wouldn't want to work in any way, might return in the future

# 1.19.0
## Blazing companionship
- Updated Companion Bat's death system, when defeated while the owner isn't around the bat will drop itself as an item instead of dying.
- Added new smithing recipe to completely fireproof your Companion Bat in item form.
- Any enchantments applied to the Companion Bat in item form will now be maintained.
- Added new accessory, the Baleful Lantern, an ominous artifact that seems to curse the bearer to attract all sorts of flames.
- Added new ability ATTRACT_FLAMES.
- Added new setting to configure the radius of the ATTRACT_FLAMES ability.
- !POSSIBLE BREAKING CHANGE FOR DATAPACKS! Renamed ability ZEALOUS_FIRE to FLAME_EATER for clarity.
- Added descriptions to the Command Flute modes.

# 1.18.7
- Updated to Minecraft 1.18.1

# 1.18.6
- Fixed incorrect return value for the generateMixin in SwampHutGeneratorMixin

# 1.18.5
- Updated to Minecraft 1.18

# 1.18.4
- Fixed the Pie O'Echanting crashing Multiplayer worlds

# 1.18.3
- Mini Inferno rework
- Added the new ability ZEALOUS_FIRE that immediately extinguishes active fires on the bat and grants a corresponding Strength effect based on the ability level
- Unified the data structure of Companion Bat Abilities for accessories and class levels

# 1.18.2
- Changed the attack damage of Tridents from the Trident ability to scale with the Bat's attack damage
- Added support for Sneak Attacks to the Tridents

# 1.18.1
- Fixed incompatibility with The Guild mod

# 1.18.0
## Tweaks and Tridents
- Reworked the mod to be data-driven, meaning that you'll be able to create your own accessories, armors and classes with just some JSONs and a texture!
- Added JSON class representation
- Added brand new system to declare bat abilities, you can now specify On Hit Effects, Active Effects, Aura Effects and Companion Bat Abilities in the class level and accessory JSON
- Added configuration for the Companion Bat's variables
- Added the ability to swim to the Companion Bat
- Added new class the Merling
- New ability "Swim" that makes the bat faster in water (and also lava)
- New ability "Trident" that makes the bat launch tridents that apply on-hit effects!
- Buffed the "Plated Boots" accessory
- Increased Ninja's teleport ticks from 7 to 70
- Dropped compatibility with versions prior to 1.8

# 1.17.9
- Added wither immunity to the Ankh
- Increased the Duelist's combo reset timer from 10s to 15s
- Reduced Effect Potion cooldown from 90 to 80

# 1.17.8
- Added new accessory, the Ankh, when equipped protects the bat from all magic damage
- Added the ability to cure the bat from Status Effects with a Milk Bucket

# 1.17.7
- Bats get now automatically recalled when the player disconnects
- Changed Speed icon in the Bat GUI
- Updated to fabric API 0.40.1+1.17

# 1.17.6
- Glow berries can no longer be used to revivify an exausted bat (too OP!).
- Companion Bats don't really like being underwater, so now they'll avoid diving in it when possible.
- Made Cave Houses more common

# 1.17.5
- Fixed super flat worlds crashing because of the Cave House incorrectly generating.

	Many thanks to Alluysl for opening the [issue](https://github.com/Fulmineo64/CompanionBats/issues/12) and helpimnotdrowning for the [pull request](https://github.com/Fulmineo64/CompanionBats/pull/13)!

# 1.17.4
- Companion Bats can now eat Glow Berries to restore a small amount of health.
Glow berries can be used directy on the bat by right-clicking it and can be used when the bat is exausted to revivify it. Glow Berries cannot be used to tame new bats.
- Fixed Power Leveling achievement being obtainable with any food
- Added 5 new Combat Levels with big stat rewards
- Increased chance back to 100% to find Ninja Garbs in End City treasures

# 1.17.3
- Lowered the Wandering Trader's prices

# 1.17.2
- Updated to Minecraft version 1.17.1
- Added Spirit Crystal trade to the Wandering Trader
- Slightly increased the Spirit Shard's price
- Now the Wandering Trader will have a random amount of Spirit Shards ranging from 2 to 4
- Spirit Crystals have a 20% chance to be traded for, while Spirit Shards a 50% chance

# 1.17.1
- Added Spirit Shard trade to the Wandering Trader

# 1.17.0
## Caves and Houses
- Added new structure, the Cave House
- Added 2 new treasure chests
- Added a new achievement for finding the Cave House

# 1.16.5
- Added Advancements to guide the player in the initial phases of the mod

# 1.16.4
- Added Russian localization (thanks to Lanchel0t)

# 1.16.3
- Updated to Minecraft version 1.17

# 1.16.2
- Updated to Minecraft version 1.17-rc1

# 1.16.1
- Now the Destroyer won't nuke the owner anymore when in its trajectory (probably)
- Added Strenght potion to the Alchemist's buffs

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