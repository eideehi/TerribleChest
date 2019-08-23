/*
 * MIT License
 *
 * Copyright (c) 2019 EideeHi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.eidee.minecraft.terrible_chest.inventory.container;

import java.util.PrimitiveIterator;
import java.util.stream.IntStream;
import javax.annotation.ParametersAreNonnullByDefault;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.config.Config;
import net.eidee.minecraft.terrible_chest.tileentity.TerribleChestTileEntity;
import net.eidee.minecraft.terrible_chest.util.IntUtil;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class TerribleChestContainer
    extends Container
{
    protected final PlayerInventory playerInventory;
    protected final IInventory chestInventory;
    protected final IIntArray data;
    protected final int totalSlotSize;
    protected int swapIndex1;
    protected int swapIndex2;

    public TerribleChestContainer( int id, PlayerInventory playerInventory, IInventory chestInventory, IIntArray data )
    {
        super( ContainerTypes.TERRIBLE_CHEST, id );
        this.playerInventory = playerInventory;
        this.chestInventory = chestInventory;
        this.data = data;
        this.totalSlotSize = chestInventory.getSizeInventory() + playerInventory.mainInventory.size();
        this.chestInventory.openInventory( this.playerInventory.player );
        this.swapIndex1 = -1;
        this.swapIndex2 = -1;

        trackIntArray( this.data );
    }

    private void swapSlot()
    {
        if ( swapIndex1 >= 0 && swapIndex2 >= 0 )
        {
            playerInventory.player.playSound( SoundEvents.UI_BUTTON_CLICK, 0.25F, 1.0F );
            if ( chestInventory instanceof TerribleChestTileEntity.TerribleChestInventoryWrapper )
            {
                ( ( TerribleChestTileEntity.TerribleChestInventoryWrapper )chestInventory ).swap( swapIndex1, swapIndex2 );
                detectAndSendChanges();
            }
        }
        swapIndex1 = -1;
        swapIndex2 = -1;
    }

    private void sortInventory( int sortType )
    {
        playerInventory.player.playSound( SoundEvents.UI_BUTTON_CLICK, 0.25F, 1.0F );
        if ( chestInventory instanceof TerribleChestTileEntity.TerribleChestInventoryWrapper )
        {
            ( ( TerribleChestTileEntity.TerribleChestInventoryWrapper )chestInventory ).sort( sortType );
            detectAndSendChanges();
        }
    }

    protected void setSwapIndex( int swapIndex1, int swapIndex2 )
    {
        this.swapIndex1 = swapIndex1;
        this.swapIndex2 = swapIndex2;
    }

    public IInventory getChestInventory()
    {
        return chestInventory;
    }

    @OnlyIn( Dist.CLIENT )
    public int getData( int index )
    {
        return data.get( index );
    }

    @OnlyIn( Dist.CLIENT )
    public int getTotalSlotSize()
    {
        return totalSlotSize;
    }

    @OnlyIn( Dist.CLIENT )
    public int getSwapIndex1()
    {
        return swapIndex1;
    }

    @Override
    public boolean canInteractWith( PlayerEntity playerIn )
    {
        return chestInventory.isUsableByPlayer( playerIn );
    }

    @Override
    public void onContainerClosed( PlayerEntity playerIn )
    {
        super.onContainerClosed( playerIn );
        chestInventory.closeInventory( playerIn );
    }

    @Override
    public boolean canMergeSlot( ItemStack stack, Slot slotIn )
    {
        return slotIn.inventory != chestInventory;
    }

    @Override
    public boolean canDragIntoSlot( Slot slotIn )
    {
        return slotIn.inventory != chestInventory;
    }

    @Override
    protected boolean mergeItemStack( ItemStack stack, int startIndex, int endIndex, boolean reverseDirection )
    {
        PrimitiveIterator.OfInt it;
        {
            IntStream stream = IntStream.range( startIndex, endIndex );
            if ( reverseDirection )
            {
                stream = stream.map( i -> endIndex - i + startIndex - 1 );
            }
            it = stream.iterator();
        }
        boolean result = false;
        IntList emptySlots = new IntArrayList( endIndex - startIndex );
        while ( it.hasNext() )
        {
            if ( stack.isEmpty() )
            {
                break;
            }
            int index = it.nextInt();
            if ( index < 0 || index >= totalSlotSize )
            {
                continue;
            }
            Slot slot = getSlot( index );
            ItemStack stackInSlot = slot.getStack();
            if ( stackInSlot.isEmpty() )
            {
                emptySlots.add( index );
                continue;
            }
            if ( ItemHandlerHelper.canItemStacksStack( stack, stackInSlot ) )
            {
                if ( index < chestInventory.getSizeInventory() )
                {
                    int count = data.get( index );
                    int limit = ( int )( Config.COMMON.slotStackLimit.get() - count );
                    if ( limit != 0 )
                    {
                        int size = IntUtil.minUnsigned( stack.getCount(), limit );
                        data.set( index, count + size );
                        stack.shrink( size );
                        slot.onSlotChanged();
                        result = true;
                    }
                }
                else
                {
                    int count = stackInSlot.getCount();
                    int max = Math.min( stackInSlot.getMaxStackSize(), slot.getSlotStackLimit() );
                    int limit = max - count;
                    if ( limit != 0 )
                    {
                        int size = Math.min( stack.getCount(), limit );
                        stackInSlot.grow( size );
                        stack.shrink( size );
                        slot.onSlotChanged();
                        result = true;
                    }
                }
            }
        }
        if ( !stack.isEmpty() )
        {
            IntListIterator emptiesIt = emptySlots.iterator();
            while ( emptiesIt.hasNext() )
            {
                int emptySlot = emptiesIt.nextInt();
                Slot slot = getSlot( emptySlot );
                if ( emptySlot < chestInventory.getSizeInventory() )
                {
                    slot.putStack( ItemHandlerHelper.copyStackWithSize( stack, 1 ) );
                    data.set( emptySlot, stack.getCount() );
                    stack.setCount( 0 );
                    result = true;
                }
                else
                {
                    int size = Math.min( stack.getCount(), slot.getSlotStackLimit() );
                    slot.putStack( ItemHandlerHelper.copyStackWithSize( stack, size ) );
                    stack.shrink( size );
                    result = true;
                }
                if ( stack.isEmpty() )
                {
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public ItemStack transferStackInSlot( PlayerEntity playerIn, int index )
    {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get( index );
        if ( slot != null && slot.getHasStack() )
        {
            ItemStack stackInSlot = slot.getStack();
            stack = stackInSlot.copy();
            if ( index < chestInventory.getSizeInventory() )
            {
                ItemStack copy = stackInSlot.copy();
                int count = data.get( index );
                int size = IntUtil.minUnsigned( count, stackInSlot.getMaxStackSize() );
                copy.setCount( size );
                if ( !mergeItemStack( copy, chestInventory.getSizeInventory(), totalSlotSize, true ) )
                {
                    return ItemStack.EMPTY;
                }
                int newCount = count - ( size - copy.getCount() );
                data.set( index, newCount );
                if ( newCount == 0 )
                {
                    stackInSlot.setCount( 0 );
                }
            }
            else if ( !mergeItemStack( stackInSlot, 0, chestInventory.getSizeInventory(), false ) )
            {
                return ItemStack.EMPTY;
            }

            if ( stackInSlot.isEmpty() )
            {
                slot.putStack( ItemStack.EMPTY );
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return stack;
    }

    @SuppressWarnings( { "StatementWithEmptyBody", "UnnecessarySemicolon" } )
    @Override
    public ItemStack slotClick( int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player )
    {
        // logger().debug( "slotId={}, click={}, drag={}", slotId, clickTypeIn, dragType );
        if ( slotId >= 0 && slotId < chestInventory.getSizeInventory() )
        { // チェストのスロットがクリックされた
            PlayerInventory playerInventory = player.inventory;
            if ( clickTypeIn == ClickType.PICKUP )
            { // 通常のクリック処理＋独自イベント処理

                int _dragType = dragType;
                if ( swapIndex1 != -1 )
                { // 入れ替え対象スロットを選択済みの場合、スロット入れ替え処理を優先する
                    _dragType = 2;
                }

                ItemStack stack = playerInventory.getItemStack().copy();
                Slot slot = getSlot( slotId );
                ItemStack stackInSlot = slot.getStack();

                if ( _dragType == 0 )
                { // 左クリック
                    if ( stack.isEmpty() )
                    { // 手持ち（マウスカーソル）のスタックが存在しない
                        if ( !stackInSlot.isEmpty() )
                        { // 対象スロットが空ではない
                            // 最大スタック数か在庫数を手持ちに移動
                            ItemStack result = slot.decrStackSize( stackInSlot.getMaxStackSize() );
                            playerInventory.setItemStack( result );
                            return result;
                        }
                    }
                    else
                    { // 手持ちのスタックが存在する
                        if ( stackInSlot.isEmpty() )
                        { // 対象スロットが空
                            // スタックをすべてスロットへ移動
                            int count = stack.getCount();
                            stack.setCount( 1 );
                            slot.putStack( stack );
                            data.set( slotId, count );
                            playerInventory.setItemStack( ItemStack.EMPTY );
                        }
                        else
                        { // 対象スロットが空ではない
                            if ( ItemHandlerHelper.canItemStacksStack( stack, stackInSlot ) )
                            { // 対象スロットのアイテムと手持ちのアイテムがマージ可能
                                if ( mergeItemStack( stack, slotId, slotId + 1, false ) )
                                {
                                    playerInventory.setItemStack( stack );
                                    return stack;
                                }
                            }
                        }
                    }
                }
                else if ( _dragType == 1 )
                { // 右クリック
                    if ( stack.isEmpty() )
                    { // 手持ちのスタックが存在しない
                        if ( !stackInSlot.isEmpty() )
                        { // 対象スロットが空
                            // 最大スタック数か在庫数の半分を手持ちに移動
                            int count = data.get( slotId );
                            int amount = IntUtil.minUnsigned( count, stackInSlot.getMaxStackSize() ) / 2;
                            ItemStack result = slot.decrStackSize( amount );
                            playerInventory.setItemStack( result );
                            return result;
                        }
                    }
                    else
                    { // 手持ちのスタックが存在する
                        if ( stackInSlot.isEmpty() )
                        { // 対象スロットが空
                            // 手持ちからスロットへ、一つアイテムを移動
                            ItemStack _stack = stack.split( 1 );
                            slot.putStack( _stack );
                            playerInventory.setItemStack( stack );
                            return stack;
                        }
                        else if ( ItemHandlerHelper.canItemStacksStack( stack, stackInSlot ) )
                        {
                            int count = data.get( slotId );
                            if ( Long.compareUnsigned( count, Config.COMMON.slotStackLimit.get() ) < 0 )
                            {
                                stack.shrink( 1 );
                                data.set( slotId, count + 1 );
                                playerInventory.setItemStack( stack );
                                return stack;
                            }
                        }
                    }
                }
                else if ( _dragType == 2 )
                { // 独自イベント処理 [スロット入れ替え]
                    if ( swapIndex1 >= 0 )
                    {
                        setSwapIndex( swapIndex1, slotId );
                        swapSlot();
                    }
                    else
                    {
                        setSwapIndex( slotId, -1 );
                    }
                }
                else if ( _dragType == 3 )
                { // 独自イベント処理 [１つだけ移動]
                    if ( !stackInSlot.isEmpty() )
                    {
                        ItemStack copy = ItemHandlerHelper.copyStackWithSize( stackInSlot, 1 );
                        if ( mergeItemStack( copy, chestInventory.getSizeInventory(), totalSlotSize, true ) )
                        {
                            int newCount = data.get( slotId ) - 1;
                            data.set( slotId, newCount );
                            if ( newCount == 0 )
                            {
                                slot.putStack( ItemStack.EMPTY );
                            }
                        }
                    }
                }
                return ItemStack.EMPTY;
            }
            else if ( clickTypeIn == ClickType.THROW )
            { // アイテムドロップ処理
                setSwapIndex( -1, -1 );

                ItemStack stack = playerInventory.getItemStack();
                if ( stack.isEmpty() )
                { // 手持ちのスタックが存在しない
                    Slot slot = getSlot( slotId );
                    ItemStack stackInSlot = slot.getStack();
                    if ( !stackInSlot.isEmpty() )
                    { // 対象スロットが空ではない
                        int count = data.get( slotId );
                        // コントロールが押下されている場合は最大スタック（あるいは在庫）数をドロップ
                        int stackSize = dragType == 1 ? stackInSlot.getMaxStackSize() : 1;
                        int dropSize = IntUtil.minUnsigned( stackSize, count );
                        ItemStack dropStack = ItemHandlerHelper.copyStackWithSize( stackInSlot, dropSize );
                        player.dropItem( dropStack, false );
                        int newCount = count - dropSize;
                        data.set( slotId, newCount );
                        if ( newCount == 0 )
                        {
                            slot.putStack( ItemStack.EMPTY );
                        }
                    }
                    return ItemStack.EMPTY;
                }
            }
            else if ( clickTypeIn == ClickType.QUICK_MOVE )
            { // シフトクリック処理
                setSwapIndex( -1, -1 );

                if ( dragType == 2 )
                { // 独自イベント処理 [同種アイテム一括移動]
                    ItemStack stackInSlot = getSlot( slotId ).getStack().copy();
                    if ( !stackInSlot.isEmpty() )
                    { // 対象スロットが空ではない
                        for ( int i = 0; i < chestInventory.getSizeInventory(); i++ )
                        {
                            // 対象スロットのアイテムと同じ種類のアイテムを
                            // チェストからプレイヤーインベントリへ移動
                            ItemStack stackInPlayer = getSlot( i ).getStack();
                            if ( ItemHandlerHelper.canItemStacksStack( stackInSlot, stackInPlayer ) )
                            {
                                while ( !transferStackInSlot( player, i ).isEmpty() )
                                {
                                    ; // 在庫がなくなるか、プレイヤーインベントリが満タンになるまで繰り返す
                                }
                            }
                        }
                    }
                    return ItemStack.EMPTY;
                }
                else
                { // 通常のシフトクリック処理
                    transferStackInSlot( player, slotId );
                    return ItemStack.EMPTY;
                }
            }
            else if ( clickTypeIn == ClickType.SWAP )
            { // 独自イベント処理 [ソート]
                sortInventory( dragType );
                return ItemStack.EMPTY;
            }
            else if ( clickTypeIn == ClickType.CLONE ||
                      clickTypeIn == ClickType.PICKUP_ALL ||
                      clickTypeIn == ClickType.QUICK_CRAFT )
            {
                setSwapIndex( -1, -1 );
                return ItemStack.EMPTY;
            }
        }
        else if ( slotId >= chestInventory.getSizeInventory() && slotId < totalSlotSize )
        { // プレイヤーインベントリのスロットがクリックされた
            setSwapIndex( -1, -1 );

            if ( clickTypeIn == ClickType.PICKUP )
            { // 通常のクリック処理
                if ( dragType == 3 )
                { // 独自イベント処理 [一つだけ移動]
                    ItemStack stackInSlot = getSlot( slotId ).getStack();
                    if ( !stackInSlot.isEmpty() )
                    {
                        ItemStack copy = ItemHandlerHelper.copyStackWithSize( stackInSlot, 1 );
                        if ( mergeItemStack( copy, 0, chestInventory.getSizeInventory(), false ) )
                        {
                            stackInSlot.shrink( 1 );
                        }
                    }
                }
            }
            else if ( clickTypeIn == ClickType.QUICK_MOVE )
            { // シフトクリック処理
                if ( dragType == 2 )
                { // 独自イベント処理 [同種アイテム一括移動]
                    ItemStack stackInSlot = getSlot( slotId ).getStack().copy();
                    if ( !stackInSlot.isEmpty() )
                    { // 対象スロットが空ではない
                        for ( int i = chestInventory.getSizeInventory(); i < totalSlotSize; i++ )
                        {
                            // 対象スロットのアイテムと同じ種類のアイテムを
                            // プレイヤーインベントリからチェストへ移動
                            ItemStack stackInPlayer = getSlot( i ).getStack();
                            if ( ItemHandlerHelper.canItemStacksStack( stackInSlot, stackInPlayer ) )
                            {
                                transferStackInSlot( player, i );
                            }
                        }
                    }
                    return ItemStack.EMPTY;
                }
            }
        }
        else
        {
            setSwapIndex( -1, -1 );
        }
        return super.slotClick( slotId, dragType, clickTypeIn, player );
    }
}
