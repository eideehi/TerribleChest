<img src="https://app.box.com/shared/static/cbpgau8w2td9jwmkjvxpzllwhj19mxqw.png" width="200" alt="Logo - Terrible Chest" />

# Terrible Chest - 1.16.3 #
Add a chest with extreme(terrible) spec.

## Outline ##
<img src="https://app.box.com/shared/static/0oz5ykrqa9tvofjy6h5yah4zsa2c4i3u.gif" alt="Demo - Outline" />

Adds a multi-page chest with 27 slots, capable of holding approximately 4.3 billion items per slot.
You can spend 'Diamond Sphere', an item added by this mod, to add a single page of space.
Like 'Ender Chest', only individuals can access the contents of the 'Terrible Chest', but the contents of the 'Emerald Terrible Chest' can be accessed by anyone, just like the 'Chest'

#### Single-page mode ####
<img src="https://app.box.com/shared/static/qso2piyvor3gdt808pjhb82b1uua0i5d.png" alt="Image - Single-page" />

Single-page mode is available by configuring it in the configuration.
You can't add more pages in this mode, but you can manage your items in a larger GUI than in multi-page mode.

## Config ##
### Common ###
/MINECRAFT_DIR/config/terrible_chest-common.toml

|Property|Description|
|--------|-----------|
|maxPageLimit|Specifies the maximum number of pages that can be extended in multi-page mode.|
|slotStackLimit|Specifies the maximum number of items that can be stored in a slot.|
|useSinglePageMode|Specifies whether to use single-page mode.|
|resetMaxPage|This item fixes the maximum number of pages that would be zero when switching from single page mode to multi-page mode in previous versions.|
|inventoryRows|Set the Row number of chests in multipage mode. The capacity of the chest is set to inventoryRows x 9.|

## Item ##
### Terrible Chest ###
<img src="https://app.box.com/shared/static/l6rxw83mc36ik9iuet8seywoa3v53dnj.png" alt="Recipe - Terrible Chest" />

This is a chest that gives you access to the shared inventory stored per player. It cannot be used by anyone other than the player who placed it. Also, like the 'Ender Chest', it does not allow for the use of hoppers.

### Diamond Sphere ###
<img src="https://app.box.com/shared/static/1mt4nuthpf42gtbd8wmjykw9trxuk6mq.png" alt="Recipe - Diamond Sphere" />

Can be used to increase the page limit for 'Terrible Chest' in multi-page mode.

### Terrible Bangle ###
<img src="https://app.box.com/shared/static/ygp19qb5iqjlbx6c8wpti360z9u95fe6.png" alt="Recipe - Terrible Bangle" />

Using Terrible Bangle, you can perform special operations for Terrible Chest inventory.

- Item collection:  
  Move items as far as possible from blocks with inventory such as "Chest" to "Terrible Chest"  
  [SHIFT(SNEAK)] + [RIGHT_CLICK]  
  [![Demo - Item collection](https://app.box.com/shared/static/0ysimm68b6oypkhstk7p1r49sg7tsvol.png)](https://youtu.be/vcEgjA0yTZI)


### Emerald Terrible Chest ###
<img src="https://app.box.com/shared/static/j53hkrlk2z18s61wtgxac332cabaetlb.png" alt="Recipe - Emerald Terrible Chest" />

This is a portable chest like the 'Shulker Box'. The inventory is accessible to all players. It is possible to use a hopper, as 'Chest' does.

## GUI ##
This Mod is not support 'Inventory Tweaks', but to compensate following functions are implemented.

- Move the items one by one:  
  [CTRL] + [LEFT_CLICK]  
  <img src="https://app.box.com/shared/static/gh2ra72psg7id4rnw9srzmgj3uwzgfvd.gif" alt="Demo - Move the items one by one" />

- Move the same type of item at once:  
  [CTRL] + [SHIFT] + [LEFT_CLICK]  
  <img src="https://app.box.com/shared/static/tqododmh9iusncge56pfhs08zu0d64kt.gif" alt="Demo - Move the same type of item at once" />

- Swap the contents of the selected slot:  
  [ALT] + [LEFT_CLICK]  
  <img src="https://app.box.com/shared/static/al71kqj9o3ij9bvrplg0n3le118472s5.gif" alt="Demo - Swap the contents of the selected slot" />

- Sort the items in the chest:  
  <img src="https://app.box.com/shared/static/qqoi4qsbyvcev8xlhkuo7mqmc74oehmt.gif" alt="Demo - Sort the items in the chest" />

  - Sort with Namespaced ID: [1(NUMBER)]
  - Sort with Display name: [2(NUMBER)]
  - Sort with Item count: [3(NUMBER)]

  These keys can be changed from the control settings in Minecraft  
  <img src="https://app.box.com/shared/static/suw40dxj5zueh619kdm5jc6djhtdgcut.png" alt="Image - Controls" />

## Languages ##
|Language|Translators|Status|
|--------|-----------|------|
|en_us|Translation Tools|Complete|
|ja_jp|EideeHi|Complete|
|zh_cn|Aikini|Incomplete|
|ru_ru|The_BadUser(vanja-san)|Incomplete|

## Credits ##
- Aikini
- The_BadUser(vanja-san)
