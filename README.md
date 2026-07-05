# BetterRTPx

BetterRTPx is the BetterRTP Reborn fork focused on current Paper, Purpur, and Folia servers.

This fork keeps the original BetterRTP command and permission surface where practical, while modernizing the runtime internals for safer scheduling, better performance, and newer Minecraft compatibility.

## Recent Update

- Added Folia-safe scheduler wrappers for async, entity, and location work.
- Moved RTP safe-location checks and teleport completion onto Folia-compatible schedulers.
- Fixed first-join RTP timing with a configurable join delay.
- Added optional world whitelist support.
- Fixed SQL cooldown table handling for world names with spaces.
- Fixed queue circle-distance math and a world Z-coordinate validation bug.
- Improved random coordinate generation and world height handling.
- Reduced stuck RTP state after cancelled or failed searches.
- Cleared player RTP/cooldown state on player unload and plugin disable.
- Hardened cooldown loading and shared player data against async access races.
- Skipped invalid configured sound names instead of throwing runtime errors.
- Updated Maven dependencies and repository configuration for modern Java builds.

## Compatibility

- Java 21 runtime supported.
- Paper and Purpur supported.
- Folia support is included through FoliaLib.
- The plugin is published as `BetterRTPx` and declares `provides: [BetterRTP]` for compatibility with integrations that still look for the original plugin name.

## Commands And Permissions

The main command remains:

```text
/betterrtp
```

Existing aliases such as `/rtp`, `/wild`, and `/randomtp` remain available. Existing `betterrtp.*` permission nodes are preserved for compatibility.

## Build

```bash
mvn clean package
```

The compiled plugin jar is written to `target/`.

## Wiki

The wiki is available from this repository's GitHub wiki.
