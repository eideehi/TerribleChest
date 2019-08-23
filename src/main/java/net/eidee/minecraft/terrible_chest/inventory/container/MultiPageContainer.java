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

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.config.Config;
import net.eidee.minecraft.terrible_chest.inventory.SingleStackInventory;
import net.eidee.minecraft.terrible_chest.item.Items;
import net.eidee.minecraft.terrible_chest.tileentity.MultiPageTileEntity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MultiPageContainer
    extends TerribleChestContainer
{
    private IInventory unlockInventory;

    public MultiPageContainer( int id, PlayerInventory playerInventory )
    {
        this( id, playerInventory, new SingleStackInventory( 27 ), new IntArray( 29 ) );
    }

    public MultiPageContainer( int id,
                               PlayerInventory playerInventory,
                               IInventory inventory,
                               IIntArray data )
    {
        super( id, playerInventory, inventory, data );
        this.unlockInventory = new Inventory( 1 );

        assertInventorySize( this.chestInventory, 27 );
        assertIntArraySize( this.data, 29 );

        this.chestInventory.openInventory( playerInventory.player );

        for ( int i = 0; i < 3; ++i )
        {
            for ( int j = 0; j < 9; ++j )
            {
                addSlot( new Slot( this.chestInventory, j + i * 9, 8 + j * 18, 34 + i * 18 ) );
            }
        }

        for ( int i = 0; i < 3; ++i )
        {
            for ( int j = 0; j < 9; ++j )
            {
                addSlot( new Slot( playerInventory, j + i * 9 + 9, 8 + j * 18, 100 + i * 18 ) );
            }
        }

        for ( int i = 0; i < 9; ++i )
        {
            addSlot( new Slot( playerInventory, i, 8 + i * 18, 158 ) );
        }

        addSlot( new Slot( unlockInventory, 0, 183, 35 ) );
    }

    @Override
    protected void setSwapIndex( int swapIndex1, int swapIndex2 )
    {
        //TODO: スワップ関係の処理をもう少しエレガントにする
        int _swapIndex1 = swapIndex1;
        if ( _swapIndex1 != this.swapIndex1 && _swapIndex1 >= 0 )
        {
            _swapIndex1 += ( data.get( MultiPageTileEntity.DATA_PAGE ) * 27 );
        }
        int _swapIndex2 = swapIndex2;
        if ( _swapIndex2 != this.swapIndex2 && _swapIndex2 >= 0 )
        {
            _swapIndex2 += ( data.get( MultiPageTileEntity.DATA_PAGE ) * 27 );
        }
        super.setSwapIndex( _swapIndex1, _swapIndex2 );
    }

    public void setPage( int page )
    {
        int maxPage = data.get( MultiPageTileEntity.DATA_MAX_PAGE );
        int _page = MathHelper.clamp( page, 0, maxPage - 1 );
        data.set( MultiPageTileEntity.DATA_PAGE, _page );
    }

    public void unlockMaxPage()
    {
        ItemStack stack = unlockInventory.getStackInSlot( 0 );
        if ( !stack.isEmpty() && stack.getItem() == Items.DIAMOND_SPHERE )
        {
            int maxPage = data.get( MultiPageTileEntity.DATA_MAX_PAGE );
            if ( maxPage < Config.COMMON.maxPageLimit.get() )
            {
                stack.shrink( 1 );
                data.set( MultiPageTileEntity.DATA_MAX_PAGE, 0 );
            }
        }
    }

    @OnlyIn( Dist.CLIENT )
    public int getPage()
    {
        return data.get( MultiPageTileEntity.DATA_PAGE );
    }

    @OnlyIn( Dist.CLIENT )
    public int getMaxPage()
    {
        return data.get( MultiPageTileEntity.DATA_MAX_PAGE );
    }

    @Override
    public void onContainerClosed( PlayerEntity playerIn )
    {
        super.onContainerClosed( playerIn );

        ItemStack stack = unlockInventory.removeStackFromSlot( 0 );
        if ( !stack.isEmpty() && !playerIn.addItemStackToInventory( stack ) )
        {
            playerIn.dropItem( stack, true );
        }
    }
}
