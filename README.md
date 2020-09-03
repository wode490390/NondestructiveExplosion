# Nondestructive Explosion
[![Nukkit](https://img.shields.io/badge/Nukkit-1.0-green)](https://github.com/NukkitX/Nukkit)
[![Build](https://img.shields.io/circleci/build/github/wode490390/NondestructiveExplosion/master)](https://circleci.com/gh/wode490390/NondestructiveExplosion/tree/master)
[![Release](https://img.shields.io/github/v/release/wode490390/NondestructiveExplosion)](https://github.com/wode490390/NondestructiveExplosion/releases)
[![Release date](https://img.shields.io/github/release-date/wode490390/NondestructiveExplosion)](https://github.com/wode490390/NondestructiveExplosion/releases)
<!--[![MCBBS](https://img.shields.io/badge/-mcbbs-inactive)](https://www.mcbbs.net/thread-825524-1-1.html "非破坏性爆炸")
[![Servers](https://img.shields.io/bstats/servers/4842)](https://bstats.org/plugin/bukkit/NondestructiveExplosion/4842)
[![Players](https://img.shields.io/bstats/players/4842)](https://bstats.org/plugin/bukkit/NondestructiveExplosion/4842)-->

NondestructiveExplosion plugin for Nukkit servers.

![](https://i.loli.net/2019/04/18/5cb7730811986.gif)

If you found any bugs or have any suggestions, please open an issue on [GitHub Issues](https://github.com/wode490390/NondestructiveExplosion/issues).

If you like this plugin, please star it on [GitHub](https://github.com/wode490390/NondestructiveExplosion).

## Configuration
<details>
<summary>config.yml</summary>

```yaml
# Whether to activate nearby TNT blocks during an explosion
activate-nearby-tnt: false
```
</details>

## APIs
| Packages | Classes |
| :- | :- |
| cn.wode490390.nukkit.nde | EntityNondestructiveExplodeEvent |
#### EntityNondestructiveExplodeEvent
This event is the same as [EntityExplodeEvent](https://ci.nukkitx.com/job/NukkitX/job/Nukkit/job/master/javadoc/cn/nukkit/event/entity/EntityExplodeEvent.html)

## Download
- [Releases](https://github.com/wode490390/NondestructiveExplosion/releases)
- [Snapshots](https://circleci.com/gh/wode490390/NondestructiveExplosion)

## Compiling
1. Install [Maven](https://maven.apache.org/).
2. Run `mvn clean package`. The compiled JAR can be found in the `target/` directory.

## Metrics Collection

This plugin uses [bStats](https://github.com/wode490390/bStats-Nukkit). You can opt out using the global bStats config; see the [official website](https://bstats.org/getting-started) for more details.

<!--[![Metrics](https://bstats.org/signatures/bukkit/NondestructiveExplosion.svg)](https://bstats.org/plugin/bukkit/NondestructiveExplosion/4842)-->

###### If I have any grammar and/or term errors, please correct them :)
