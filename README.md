# NonSinn's Mining Tweaks

An open, forkable Hytale plugin adding configurable 3x3 mining tools, tool
progression, modifiers, recycling, and custom workbenches. The project is
developed on **Der Waldbrand**, but is intended to remain usable outside that
server.

> **Status: 2.8.0 beta.** The mechanics are playable, while icons and some
> models are still being refined. Feedback and forks are explicitly welcome.

## Features in 2.8.0

- Copper, iron, thorium, and cobalt mining hammers mine eligible rock and ore in a view-aligned 3x3 plane.
- Matching wide-bladed area shovels mine only eligible soil and soft blocks; mixed stone remains intact.
- The mining workbench has three real tiers: copper/iron, thorium plus Resonance Forge, then cobalt. Direct tier upgrades preserve installed parts, strain, and relative durability.
- The two-tier Resonance Forge performs normal/focused calibration. Installation and exact-slot recycling share the physical Montagebank with a visible tool slot, part slot, capacity preview, combine button, and recycle button.
- Native inventory tooltips show installed speed, durability and reduced area wear plus total strain. Internal grade counters remain available for balancing, but the redundant visible part list and free-capacity arithmetic have been removed.
- Creative mode no longer consumes materials for calibration, recycling, assembly, repair or tier upgrades. Creative 3x3 drops are inserted through safe inventory transactions instead of spawning pickup entities during ECS processing.
- Hammer icons carry the same cyan 3x3 badge as area shovels. Modifier icons use restrained Hytale-style materials and clear speed, durability, and area-stability silhouettes instead of sci-fi machinery.
- Unwanted unused parts can be recycled into 1/2/3 resonance splinters. A crafted focus core changes the roll to precision (75%) or masterwork (25%).
- Tempo adds 8% speed per power, durability adds 15% maximum durability per power, and stability reduces extra-area wear by 10% per power down to a 55% floor.
- Capacity is copper 4, iron 6, thorium 8, cobalt 10. Part grades cost 2/3/4 strain and grant 1/2/3 power.
- Repair costs scale with damage, tier, installed part count, and strain. Tiny repairs cost one matching bar; a fully strained cobalt tool costs 13 bars only when completely broken.
- Every candidate block is checked against the held tool's real harvest specification. Native block breaking remains responsible for protection, drops, Natural20, and MMO hooks.

## Use

- `/bergbauhilfe` — short in-game guide.
- `/nonsinn` — admin/test toggle for ordinary pickaxes and shovels; custom area tools work without it.
- Resonance Forge tabs: Resonance Technology and Calibration.
- Craft the Montagebank in Resonance Technology. Select a calibrated part on the right. Choose a hammer or area shovel on the left and press Combine, or press Recycle with no tool required. Both actions consume the exact selected inventory slot; the recycle yield is shown on the button.
- `/werkzeugpflege` — repairs the held custom tool using matching tier bars.
- `/werkzeugupgrade <weiter|eisen|thorium|kobalt>` — buys only the direct next tier at a nearby mining workbench and preserves the tool build.

## Safety and compatibility

- Containers, benches, connected/state blocks, block entities, protruding special blocks, and unsuitable materials are skipped.
- Durability is charged only for blocks confirmed as broken. Creative mode receives explicit drops because Hytale suppresses non-natural extra-block drops there.
- Preview particles mark only eligible blocks and follow horizontal/vertical view alignment.
- All recipes reference validated Hytale or installed Endgame assets.

## Compatibility and dependencies

The plugin targets the Hytale server API and uses assets from Endgame & QoL.
The exact supported game and dependency versions are declared on each release.
Do not copy a production server's generated configuration into a public issue.

The current build is a beta and should be tested on a disposable world before
being added to an established server.

## In-game test checklist

1. Hammer and wide-shovel inventory, hand, dropped-item and workbench visuals.
2. Horizontal/vertical 3x3 preview plus mixed stone/dirt and ore/stone grids.
3. Nine real drops in Adventure and Creative, MMO XP, Natural20 hooks, and per-block wear.
4. Protected blocks, containers, benches, state blocks, block entities, and reconnect behavior.
5. Normal calibration odds and all three catalyst paths.
6. Montagebank recycling for all nine real parts in Survival and Creative, including full-inventory refunds, plus focused calibration material consumption/refunds.
7. Montagebank installation for both hammers and shovels, strain limits, stacking effects, modifier persistence, and proportional repairs.
8. Copper to iron to thorium to cobalt upgrades with preserved parts and relative condition.
