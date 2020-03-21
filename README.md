<img src="https://app.box.com/shared/static/cbpgau8w2td9jwmkjvxpzllwhj19mxqw.png" width="200" alt="Logo - Terrible Chest" />

# Terrible Chest
ぶっ壊れ性能なチェストを追加するMod

Add chest the terrible spec.

## 概要 / Outline
<img src="https://app.box.com/shared/static/8je81ugovw06ktmrmqn1fgknq2fblz24.gif" alt="Demo - Outline" />

１ページ２７スロット、１スロットにつき約４３億個収納することができるチェストを追加します。
さらに、このＭｏｄにより追加されるアイテム「ダイヤモンドスフィア」を消費することで、１ページ分の容量を追加することが可能です。
また、チェストの中身はエンダーチェストのように、すべてのチェスト間で共有されます。

1 page 27 slots and about 4.3 billion amounts per slot.
Use 'diamond sphere' for unlock new page.
It shares content like an Ender Chest.

<img src="https://app.box.com/shared/static/qso2piyvor3gdt808pjhb82b1uua0i5d.png" alt="Image - Outline" />

コンフィグにて設定することで、シングルページモードを利用できます。
このモードではページを増やすことはできませんが、マルチページモードよりも大きなGUIでアイテムを管理することができます。

You can use single-page mode by setting in the Config.
This mode does not allow you to add more pages, but you can manage items with a larger GUI than in multi-page mode.

## コンフィグ / Config
ディレクトリ: .minecraft/config/terrible_chest-common.toml<br>
Directory: .minecraft/config/terrible_chest-common.toml

|Property|Description|
|--------|-----------|
|maxPageLimit|マルチページモードでの、拡張可能なページの上限を指定します。<br>Specifies the maximum number of pages that can be extended in multi-page mode.|
|slotStackLimit|１スロットに格納できるアイテム数の上限を指定します。<br>Specifies the maximum number of items that can be stored in a slot.|
|useSinglePageMode|シングルページモードを使用するかどうかを指定します。シングルページモードからマルチページモードに移行した場合、最大ページ数が０になってしまいます。これを修正するにはresetMaxPageのコンフィグを使用してください。<br>Specifies whether to use single-page mode. If you switch from single-page mode to multi-page mode, the maximum number of pages will be zero. Use the resetMaxPage configuration to correct this.|
|resetMaxPage|シングルページモードからマルチページモードに切り替えた際に０となってしまう最大ページ数を修正します。<br>Correct the maximum number of pages that will be zero when switching from single-page mode to multi-page mode.|

## レシピ / Recipe
D = ダイヤモンドブロック / minecraft:diamond_block<br>
S = ダイヤモンドスフィア / terrible_chest:diamond_sphere

- テリブルチェスト / Terrible Chest (terrible_chest:terrible_chest)<br>
  "DDD"<br>
  "D D"<br>
  "DDD"<br>
  <img src="https://app.box.com/shared/static/l6rxw83mc36ik9iuet8seywoa3v53dnj.png" alt="Recipe - Terrible Chest" />

- ダイヤモンドスフィア / Diamond Sphere (terrible_chest:diamond_sphere)<br>
  " D "<br>
  "DDD"<br>
  " D "<br>
  <img src="https://app.box.com/shared/static/1mt4nuthpf42gtbd8wmjykw9trxuk6mq.png" alt="Recipe - Diamond Sphere" />

- テリブルバングル / Terrible Bangle (terrible_chest:terrible_bangle)<br>
  " S "<br>
  "S S"<br>
  " S "<br>
  <img src="https://app.box.com/shared/static/ygp19qb5iqjlbx6c8wpti360z9u95fe6.png" alt="Recipe - Terrible Bangle" />

## GUI操作説明 / GUI Keybinds
Terrible Chestは「Inventory Tweaks」に対応していませんが、それを補うための機能を実装しています。
以下にその機能と操作方法を記載しています。

This Mod is not support 'Inventory Tweaks', but to compensate following functions are implemented.

- アイテムを１個ずつ移動する / Move one by one<br>
  スロットのアイテムを１つずつ、チェストからインベントリ／インベントリからチェストへ直接移動します。<br>
  [CTRL] + [LEFT-CLICK]<br>
  <img src="https://app.box.com/shared/static/gh2ra72psg7id4rnw9srzmgj3uwzgfvd.gif" alt="Demo - Move one by one" />

- アイテムを一括で移動する / Move all stack for one item<br>
  スロットのアイテムと同じアイテムのスタックをすべて、チェストからインベントリ／インベントリからチェストへ直接移動します。<br>
  [CTRL] + [SHIFT] + [LEFT-CLICK]<br>
  <img src="https://app.box.com/shared/static/tqododmh9iusncge56pfhs08zu0d64kt.gif" alt="Demo - Move all stack for one item" />

- スロットを入れ替える / Swap slots<br>
  選択したスロットの中身を入れ替えます<br>
  [ALT] + [LEFT-CLICK]<br>
  <img src="https://app.box.com/shared/static/al71kqj9o3ij9bvrplg0n3le118472s5.gif" alt="Demo - Swap slots" />

- アイテムをソートする / Sort<br>
  インベントリ内のアイテムを並べ替えます<br>
    - [1(NUMBER)] : ID順 / With ID<br>
      <img src="https://app.box.com/shared/static/qqoi4qsbyvcev8xlhkuo7mqmc74oehmt.gif" alt="Demo - Sort with id" />
    - [2(NUMBER)] : 名前順 / With Name<br>
    - [3(NUMBER)] : アイテム個数順 / With Item Count<br>
  これらのキーはMinecraftのコントロール設定から変更することが可能です。
  <img src="https://app.box.com/shared/static/suw40dxj5zueh619kdm5jc6djhtdgcut.png" alt="Demo - Controls" />

## テリブルバングル / Terrible Bangle
テリブルバングルを使用することで、テリブルチェストのインベントリに対して特殊な操作を行うことができます。

Using Terrible Bangle, you can perform special operations for Terrible Chest inventory.

- 一括収納 / Item collection<br>
  対象のインベントリを持つブロックから、テリブルチェストのインベントリへ、一括でアイテムの移動を行います。<br>
  [SHIFT(SNEAK)] + [RIGHT-CLICK]<br>
  [![Demo - Item collection](https://app.box.com/shared/static/0ysimm68b6oypkhstk7p1r49sg7tsvol.png)](https://youtu.be/vcEgjA0yTZI)

## クレジット / Credits

#### 翻訳協力者 / Translators:
|Language|Translators|
|--------|-----------|
|zh_cn|Aikini|
|ru_ru|The_BadUser(vanja-san)|
