<img src="https://app.box.com/shared/static/cbpgau8w2td9jwmkjvxpzllwhj19mxqw.png" width="200" alt="Logo - Terrible Chest" />

# Terrible Chest - 1.12.2 #
Add a chest with extreme(terrible) spec.

## Outline ##
<img src="https://app.box.com/shared/static/0oz5ykrqa9tvofjy6h5yah4zsa2c4i3u.gif" alt="Demo - Outline" />

Adds a multi-page chest with 27 slots, capable of holding approximately 4.3 billion items per slot.
You can spend 'Diamond Sphere', an item added by this mod, to add a single page of space.
Like 'Ender Chest', only individuals can access the contents of the 'Terrible Chest', but the contents of the 'Emerald Terrible Chest' can be accessed by anyone, just like the 'Chest'

## Config ##
### Common ###
/MINECRAFT_DIR/config/terrible_chest-common.toml

|Property|Description|
|--------|-----------|
|maxPageLimit|Specifies the maximum number of pages that can be extended.|
|slotStackLimit|Specifies the maximum number of items that can be stored in a slot.|
|stopItemCollectionAndDeliver|If set to true, the collection and delivery of items will be disabled.|
|useItemHandlerCapability|Set to true to manipulate items using hoppers and the like.|
|transferCooldown|Set the interval at which the item transfer function is performed.|
|transferStackCount|Sets the stack size to be transferred at once by the item transfer function.|

## Item ##
### Terrible Chest ###
<img src="https://app.box.com/shared/static/l6rxw83mc36ik9iuet8seywoa3v53dnj.png" alt="Recipe - Terrible Chest" />

This is a chest that gives you access to the shared inventory stored per player. It cannot be used by anyone other than the player who placed it.
Also, if the config's `stopItemCollectionAndDeliver` is set to false, you can use the item collection and delivery feature to target blocks with surrounding inventory.

<img src="https://app.box.com/shared/static/qxe56u7ujl26on7fgb15vkvk8e8jq09w.gif" alt="Demo - Item Collection" />
<img src="https://app.box.com/shared/static/oyz9fk1xolivjtobmtp6dpj9ay9d2lls.gif" alt="Demo - Item Delivery" />


### Diamond Sphere ###
<img src="https://app.box.com/shared/static/1mt4nuthpf42gtbd8wmjykw9trxuk6mq.png" alt="Recipe - Diamond Sphere" />

Can be used to increase the page limit for 'Terrible Chest'.

### Emerald Terrible Chest ###
<img src="https://app.box.com/shared/static/j53hkrlk2z18s61wtgxac332cabaetlb.png" alt="Recipe - Emerald Terrible Chest" />

This is a portable chest like the 'Shulker Box'. The inventory is accessible to all players.

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

## Languages ##
|Language|Translators|Status|
|--------|-----------|------|
|en_us|Translation Tools|Complete|
|ja_jp|EideeHi|Complete|
|zh_cn|Aikini,Translation Tools|Complete|

## Credits ##
- Aikini
