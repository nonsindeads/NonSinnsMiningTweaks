# Balancing notes — dev9

The target is a long private-co-op progression: useful early tools, meaningful material sinks, and no mandatory endless reroll loop.

| Tier | Strain capacity | Hammer durability | Shovel durability | Full base repair |
|---|---:|---:|---:|---:|
| Copper | 4 | 500 | 300 | 4 bars |
| Iron | 6 | 650 | 450 | 5 bars |
| Thorium | 8 | 850 | 600 | 6 bars |
| Cobalt | 10 | 1050 | 750 | 7 bars |

Normal calibration has 60% standard, 30% precision, and 10% masterwork odds. Exact-slot recycling at the assembly bench returns 1, 2, or 3 splinters respectively. A focus core needs three splinters plus one of each Endgame catalyst family and guarantees a useful roll: 75% precision or 25% masterwork. Thus bad rolls still advance the next attempt without making masterwork deterministic or cheap.

Part power and strain:

| Grade | Power | Strain | Recycle yield |
|---|---:|---:|---:|
| Standard | 1 | 2 | 1 splinter |
| Precision | 2 | 3 | 2 splinters |
| Masterwork | 3 | 4 | 3 splinters |

Effects are intentionally additive by power: tempo +8%, maximum durability +15%, stability -10% extra-area wear. Stability has a 55% wear floor. Capacity prevents stacking every best effect on low tiers and leaves endgame build choices on cobalt.

The separate assembly bench costs 8 iron bars, 4 thorium bars, 3 resonance splinters and 2 purple crystals. It is deliberately gated behind the Resonance Forge: players must first engage with calibration and recycling, but the station is affordable before the late cobalt tier. Installation itself has no second material fee because the rolled part and strain capacity already provide the long-term sink and build constraint.

Repair cost is `ceil((tier base + strain * 0.35 + parts * 0.5) * missing durability fraction)`, minimum one bar when damaged. Tier upgrades charge substantial new-tier and legacy materials, allow only the direct next tier, and preserve the player's resonance investment and relative damage.
