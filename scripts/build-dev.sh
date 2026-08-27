#!/usr/bin/env bash
set -euo pipefail

project_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
server_jar="${HYTALE_SERVER_JAR:?Set HYTALE_SERVER_JAR to HytaleServer.jar}"
assets_zip="${HYTALE_ASSETS_ZIP:?Set HYTALE_ASSETS_ZIP to Assets.zip}"
endgame_jar="${ENDGAME_MOD_JAR:?Set ENDGAME_MOD_JAR to EndgameAndQoL jar}"
build_dir="$project_dir/build"

rm -rf "$build_dir"
mkdir -p "$build_dir/classes" "$build_dir/test-classes" "$build_dir/stage"

find "$project_dir" -type f -name '*.json' -not -path '*/build/*' -print0 \
  | while IFS= read -r -d '' json_file; do jq empty "$json_file"; done

javac -cp "$server_jar" -d "$build_dir/classes" \
  $(find "$project_dir/src/main/java" -type f -name '*.java' -print)

javac -cp "$server_jar:$build_dir/classes" -d "$build_dir/test-classes" \
  $(find "$project_dir/src/test/java" -type f -name '*.java' -print)

java -cp "$server_jar:$build_dir/classes:$build_dir/test-classes" dev.nonsinn.miningtweaks.MiningGeometryTest
java -cp "$server_jar:$build_dir/classes:$build_dir/test-classes" dev.nonsinn.miningtweaks.MiningRulesTest
java -cp "$server_jar:$build_dir/classes:$build_dir/test-classes" dev.nonsinn.miningtweaks.ToolModifiersTest
java -cp "$server_jar:$build_dir/classes:$build_dir/test-classes" dev.nonsinn.miningtweaks.ToolProgressionTest

required_base_assets=(
  'Server/Item/Items/Tool/Pickaxe/Tool_Pickaxe_Iron.json'
  'Server/Item/Items/Ingredient/Bar/Ingredient_Bar_Iron.json'
  'Server/Item/Items/Ingredient/Bar/Ingredient_Bar_Copper.json'
  'Server/Item/Items/Ingredient/Leather/Ingredient_Leather_Light.json'
  'Server/Item/Items/Ingredient/Crystal/Ingredient_Crystal_Cyan.json'
  'Server/Item/Items/Ingredient/Crystal/Ingredient_Crystal_White.json'
  'Server/Item/Items/Ingredient/Crystal/Ingredient_Crystal_Purple.json'
  'Server/Item/Items/Ingredient/Crystal/Ingredient_Crystal_Blue.json'
  'Server/Item/Items/Ingredient/Crystal/Ingredient_Crystal_Green.json'
  'Server/Item/Items/Ingredient/Leather/Ingredient_Leather_Medium.json'
  'Server/Item/Items/Ingredient/Leather/Ingredient_Leather_Heavy.json'
  'Server/Item/Items/Ingredient/Bar/Ingredient_Bar_Thorium.json'
  'Server/Item/Items/Ingredient/Bar/Ingredient_Bar_Cobalt.json'
  'Server/Item/Items/Tool/Pickaxe/Tool_Pickaxe_Copper.json'
  'Server/Item/Items/Tool/Pickaxe/Tool_Pickaxe_Thorium.json'
  'Server/Item/Items/Tool/Pickaxe/Tool_Pickaxe_Cobalt.json'
  'Server/Item/Items/Tool/Shovel/Tool_Shovel_Copper.json'
  'Server/Item/Items/Tool/Shovel/Tool_Shovel_Iron.json'
  'Server/Item/Items/Tool/Shovel/Tool_Shovel_Thorium.json'
  'Server/Item/Items/Tool/Shovel/Tool_Shovel_Cobalt.json'
)
unzip -Z1 "$assets_zip" > "$build_dir/base-assets.list"
for required in "${required_base_assets[@]}"; do
  grep -Fxq "$required" "$build_dir/base-assets.list" \
    || { echo "Missing referenced base asset: $required" >&2; exit 1; }
done

required_common_assets=(
  'Common/Icons/ItemsGenerated/Rock_Crystal_White_Small.png'
  'Common/Icons/ItemsGenerated/Rock_Crystal_Purple_Medium.png'
  'Common/Icons/ItemsGenerated/Ingredient_Feathers_Light.png'
  'Common/Icons/ItemsGenerated/Weapon_Shield_Iron.png'
  'Common/Icons/ItemsGenerated/Rock_Stone_Brick.png'
  'Common/Blocks/Benches/Weapon.blockymodel'
  'Common/Blocks/Benches/Weapon_Texture.png'
  'Common/Icons/ItemsGenerated/Bench_Weapon.png'
)
for required in "${required_common_assets[@]}"; do
  grep -Fxq "$required" "$build_dir/base-assets.list" \
    || { echo "Missing referenced common asset: $required" >&2; exit 1; }
done

required_endgame_assets=(
  'Server/Item/Items/Material/Endgame_Swamp_Crocodile_Scale.json'
  'Server/Item/Items/Swamp_Dungeon/Endgame_Swamp_Ingot.json'
  'Server/Item/Items/Swamp_Dungeon/Swamp_Gem.json'
)
unzip -Z1 "$endgame_jar" > "$build_dir/endgame-assets.list"
for required in "${required_endgame_assets[@]}"; do
  grep -Fxq "$required" "$build_dir/endgame-assets.list" \
    || { echo "Missing referenced Endgame asset: $required" >&2; exit 1; }
done

cp -R "$project_dir/Common" "$project_dir/Server" "$build_dir/stage/"
cp "$project_dir/manifest.json" "$build_dir/stage/manifest.json"
cp -R "$build_dir/classes/." "$build_dir/stage/"

(cd "$build_dir/stage" && jar --create --file "$build_dir/NonSinnsMiningTweaks-2.8.0.jar" .)

test ! -e "$project_dir/DEPLOYED"
echo "Built development artifact only: $build_dir/NonSinnsMiningTweaks-2.8.0.jar"
echo "No files were copied to a server mods directory."
