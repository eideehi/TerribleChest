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
import net.eidee.minecraft.terrible_chest.item.Items;
import net.eidee.minecraft.terrible_chest.tileentity.TerribleChestTileEntity;
import net.eidee.minecraft.terrible_chest.util.IntUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TerribleChestContainer
    extends Container
{
    private IInventory inventory;
    private IInventory unlockInventory;
    private int[] data;

    public TerribleChestContainer( InventoryPlayer InventoryPlayer )
    {
        this( InventoryPlayer, new DummyInventory() );
    }

    public TerribleChestContainer( InventoryPlayer InventoryPlayer, IInventory inventory )
    {
        this.inventory = inventory;
        this.unlockInventory = new InventoryBasic( "", false, 1 );
        this.data = new int[ this.inventory.getFieldCount() ];

        this.inventory.openInventory( InventoryPlayer.player );

        for ( int i = 0; i < 3; ++i )
        {
            for ( int j = 0; j < 9; ++j )
            {
                addSlotToContainer( new Slot( this.inventory, j + i * 9, 8 + j * 18, 34 + i * 18 ) );
            }
        }

        for ( int i = 0; i < 3; ++i )
        {
            for ( int j = 0; j < 9; ++j )
            {
                addSlotToContainer( new Slot( InventoryPlayer, j + i * 9 + 9, 8 + j * 18, 100 + i * 18 ) );
            }
        }

        for ( int i = 0; i < 9; ++i )
        {
            addSlotToContainer( new Slot( InventoryPlayer, i, 8 + i * 18, 158 ) );
        }

        addSlotToContainer( new Slot( unlockInventory, 0, 183, 35 ) );
    }

    public void setPage( int page )
    {
        int maxPage = inventory.getField( TerribleChestTileEntity.DATA_MAX_PAGE );
        int _page = MathHelper.clamp( page, 0, maxPage - 1 );
        inventory.setField( TerribleChestTileEntity.DATA_PAGE, _page );
    }

    public void unlockMaxPage()
    {
        ItemStack stack = unlockInventory.getStackInSlot( 0 );
        if ( !stack.isEmpty() && stack.getItem() == Items.DIAMOND_SPHERE )
        {
            stack.shrink( 1 );
            int maxPage = inventory.getField( TerribleChestTileEntity.DATA_MAX_PAGE );
            inventory.setField( TerribleChestTileEntity.DATA_MAX_PAGE, maxPage + 1 );
        }
    }

    @SideOnly( Side.CLIENT )
    public IInventory getChestInventory()
    {
        return this.inventory;
    }

    @Override
    public void addListener( IContainerListener listener )
    {
        super.addListener( listener );
        listener.sendAllWindowProperties( this, inventory );
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for ( int i = 0; i < data.length; i++ )
        {
            int value = inventory.getField( i );
            if ( data[ i ] != value )
            {
                for ( IContainerListener listener : listeners )
                {
                    listener.sendWindowProperty( this, i, value );
                    data[ i ] = value;
                }
            }
        }
    }

    @SideOnly( Side.CLIENT )
    @Override
    public void updateProgressBar( int id, int data )
    {
        if ( id >= 0 && id <= 30 )
        {
            inventory.setField( id, data );
        }
    }

    @Override
    public boolean canInteractWith( EntityPlayer playerIn )
    {
        return inventory.isUsableByPlayer( playerIn );
    }

    @Override
    public void onContainerClosed( EntityPlayer playerIn )
    {
        super.onContainerClosed( playerIn );
        inventory.closeInventory( playerIn );

        ItemStack stack = unlockInventory.removeStackFromSlot( 0 );
        if ( !stack.isEmpty() && !playerIn.addItemStackToInventory( stack ) )
        {
            playerIn.dropItem( stack, true );
        }
    }

    @Override
    public boolean canMergeSlot( ItemStack stack, Slot slotIn )
    {
        return slotIn.slotNumber >= 27;
    }

    @Override
    public boolean canDragIntoSlot( Slot slotIn )
    {
        return slotIn.slotNumber >= 27;
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
            if ( index < 0 || index >= 63 )
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
                if ( index < 27 )
                {
                    int count = inventory.getField( index );
                    int limit = -1 - count;
                    if ( limit != 0 )
                    {
                        int size = IntUtil.minUnsigned( stack.getCount(), limit );
                        inventory.setField( index, count + size );
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
                if ( emptySlot < 27 )
                {
                    slot.putStack( ItemHandlerHelper.copyStackWithSize( stack, 1 ) );
                    inventory.setField( emptySlot, stack.getCount() );
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
    public ItemStack transferStackInSlot( EntityPlayer playerIn, int index )
    {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get( index );
        if ( slot != null && slot.getHasStack() )
        {
            ItemStack stackInSlot = slot.getStack();
            stack = stackInSlot.copy();
            if ( index < 27 )
            {
                ItemStack copy = stackInSlot.copy();
                int count = inventory.getField( index );
                int size = IntUtil.minUnsigned( count, stackInSlot.getMaxStackSize() );
                copy.setCount( size );
                if ( !mergeItemStack( copy, 27, 63, true ) )
                {
                    return ItemStack.EMPTY;
                }
                int newCount = count - ( size - copy.getCount() );
                inventory.setField( index, newCount );
                if ( newCount == 0 )
                {
                    stackInSlot.setCount( 0 );
                }
            }
            else if ( !mergeItemStack( stackInSlot, 0, 27, false ) )
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
    public ItemStack slotClick( int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player )
    {
        // logger().debug( "slotId={}, click={}, drag={}", slotId, clickTypeIn, dragType );
        if ( slotId >= 0 && slotId < 27 )
        { // チェストのスロットがクリックされた
            InventoryPlayer InventoryPlayer = player.inventory;
            if ( clickTypeIn == ClickType.PICKUP )
            { // 通常のクリック処理＋独自イベント処理

                int _dragType = dragType;
                if ( inventory.getField( TerribleChestTileEntity.DATA_SWAP_TARGET ) != -1 )
                { // 入れ替え対象スロットを選択済みの場合、スロット入れ替え処理を優先する
                    _dragType = 2;
                }

                ItemStack stack = InventoryPlayer.getItemStack().copy();
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
                            InventoryPlayer.setItemStack( result );
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
                            inventory.setField( slotId, count );
                            InventoryPlayer.setItemStack( ItemStack.EMPTY );
                        }
                        else
                        { // 対象スロットが空ではない
                            if ( ItemHandlerHelper.canItemStacksStack( stack, stackInSlot ) )
                            { // 対象スロットのアイテムと手持ちのアイテムがマージ可能
                                if ( mergeItemStack( stack, slotId, slotId + 1, false ) )
                                {
                                    InventoryPlayer.setItemStack( stack );
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
                            int count = inventory.getField( slotId );
                            int amount = IntUtil.minUnsigned( count, stackInSlot.getMaxStackSize() ) / 2;
                            ItemStack result = slot.decrStackSize( amount );
                            InventoryPlayer.setItemStack( result );
                            return result;
                        }
                    }
                    else
                    { // 手持ちのスタックが存在する
                        if ( stackInSlot.isEmpty() )
                        { // 対象スロットが空
                            // 手持ちからスロットへ、一つアイテムを移動
                            ItemStack _stack = stack.splitStack( 1 );
                            slot.putStack( _stack );
                            InventoryPlayer.setItemStack( stack );
                            return stack;
                        }
                        else if ( ItemHandlerHelper.canItemStacksStack( stack, stackInSlot ) )
                        {
                            int count = inventory.getField( slotId );
                            if ( count != -1 )
                            {
                                stack.shrink( 1 );
                                inventory.setField( slotId, count + 1 );
                                InventoryPlayer.setItemStack( stack );
                                return stack;
                            }
                        }
                    }
                }
                else if ( _dragType == 2 )
                { // 独自イベント処理 [スロット入れ替え]
                    int actualSlotId = slotId + ( inventory.getField( TerribleChestTileEntity.DATA_PAGE ) * 27 );
                    int swapTarget = inventory.getField( TerribleChestTileEntity.DATA_SWAP_TARGET );
                    if ( swapTarget >= 0 )
                    {
                        int value = TerribleChestTileEntity.SWAP_EXEC_FLAG + ( swapTarget << 15 ) + actualSlotId;
                        inventory.setField( TerribleChestTileEntity.DATA_SWAP_TARGET, value );
                        detectAndSendChanges();
                    }
                    else
                    {
                        inventory.setField( TerribleChestTileEntity.DATA_SWAP_TARGET, actualSlotId );
                    }
                }
                else if ( _dragType == 3 )
                { // 独自イベント処理 [１つだけ移動]
                    if ( !stackInSlot.isEmpty() )
                    {
                        ItemStack copy = ItemHandlerHelper.copyStackWithSize( stackInSlot, 1 );
                        if ( mergeItemStack( copy, 27, 63, true ) )
                        {
                            int newCount = inventory.getField( slotId ) - 1;
                            inventory.setField( slotId, newCount );
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
                inventory.setField( TerribleChestTileEntity.DATA_SWAP_TARGET, -1 );

                ItemStack stack = InventoryPlayer.getItemStack();
                if ( stack.isEmpty() )
                { // 手持ちのスタックが存在しない
                    Slot slot = getSlot( slotId );
                    ItemStack stackInSlot = slot.getStack();
                    if ( !stackInSlot.isEmpty() )
                    { // 対象スロットが空ではない
                        int count = inventory.getField( slotId );
                        // コントロールが押下されている場合は最大スタック（あるいは在庫）数をドロップ
                        int stackSize = dragType == 1 ? stackInSlot.getMaxStackSize() : 1;
                        int dropSize = IntUtil.minUnsigned( stackSize, count );
                        ItemStack dropStack = ItemHandlerHelper.copyStackWithSize( stackInSlot, dropSize );
                        player.dropItem( dropStack, false );
                        int newCount = count - dropSize;
                        inventory.setField( slotId, newCount );
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
                inventory.setField( TerribleChestTileEntity.DATA_SWAP_TARGET, -1 );

                if ( dragType == 2 )
                { // 独自イベント処理 [同種アイテム一括移動]
                    ItemStack stackInSlot = getSlot( slotId ).getStack().copy();
                    if ( !stackInSlot.isEmpty() )
                    { // 対象スロットが空ではない
                        for ( int i = 0; i < 27; i++ )
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
            else if ( clickTypeIn == ClickType.CLONE ||
                      clickTypeIn == ClickType.PICKUP_ALL ||
                      clickTypeIn == ClickType.QUICK_CRAFT ||
                      clickTypeIn == ClickType.SWAP )
            {
                inventory.setField( TerribleChestTileEntity.DATA_SWAP_TARGET, -1 );
                return ItemStack.EMPTY;
            }
        }
        else if ( slotId >= 27 && slotId < 63 )
        { // プレイヤーインベントリのスロットがクリックされた
            inventory.setField( TerribleChestTileEntity.DATA_SWAP_TARGET, -1 );

            if ( clickTypeIn == ClickType.PICKUP )
            { // 通常のクリック処理
                if ( dragType == 3 )
                { // 独自イベント処理 [一つだけ移動]
                    ItemStack stackInSlot = getSlot( slotId ).getStack();
                    if ( !stackInSlot.isEmpty() )
                    {
                        ItemStack copy = ItemHandlerHelper.copyStackWithSize( stackInSlot, 1 );
                        if ( mergeItemStack( copy, 0, 27, false ) )
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
                        for ( int i = 27; i < 63; i++ )
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
            inventory.setField( TerribleChestTileEntity.DATA_SWAP_TARGET, -1 );
        }
        return super.slotClick( slotId, dragType, clickTypeIn, player );
    }

    private static class DummyInventory
        extends InventoryBasic
    {
        private int[] data = new int[ 30 ];

        {
            data[ TerribleChestTileEntity.DATA_PAGE ] = 0;
            data[ TerribleChestTileEntity.DATA_MAX_PAGE ] = 1;
            data[ TerribleChestTileEntity.DATA_SWAP_TARGET ] = -1;
        }

        DummyInventory()
        {
            super( "dummy", false, 27 );
        }

        @Override
        public int getField( int id )
        {
            return data[ id ];
        }

        @Override
        public void setField( int id, int value )
        {
            data[ id ] = value;
        }

        @Override
        public int getFieldCount()
        {
            return 30;
        }
    }
}
