/*
 * MIT License
 *
 * Copyright (c) 2020 EideeHi
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

package net.eidee.minecraft.terrible_chest.tileentity;

import javax.annotation.ParametersAreNonnullByDefault;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.inventory.TerribleChestInventory;
import net.eidee.minecraft.terrible_chest.inventory.container.TerribleChestContainer;
import net.eidee.minecraft.terrible_chest.item.ItemStackContainer;
import net.eidee.minecraft.terrible_chest.item.TerribleChestItemHandler;
import net.eidee.minecraft.terrible_chest.settings.Config;
import net.eidee.minecraft.terrible_chest.util.ItemStackContainerUtil;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TerribleChestTileEntity2
    extends LockableTileEntity
    implements INamedContainerProvider,
               TerribleChestInventory
{
    private final IIntArray chestData = new IIntArray()
    {
        @Override
        public int get( int index )
        {
            if ( index == TerribleChestContainer.DATA_MAX_PAGE )
            {
                return maxPage;
            }
            if ( index == TerribleChestContainer.DATA_CURRENT_PAGE )
            {
                return page;
            }
            return 0;
        }

        @Override
        public void set( int index, int value )
        {
            if ( index == TerribleChestContainer.DATA_MAX_PAGE )
            {
                maxPage = value;
            }
            if ( index == TerribleChestContainer.DATA_CURRENT_PAGE )
            {
                page = value;
            }
        }

        @Override
        public int size()
        {
            return 2;
        }
    };

    private Int2ObjectMap< ItemStackContainer > containers;
    private int maxPage;
    private int page;

    public TerribleChestTileEntity2()
    {
        super( TileEntityTypes.TERRIBLE_CHEST_2 );
        this.containers = ItemStackContainerUtil.newContainers();
        this.maxPage = 1;
        this.page = 0;
    }

    public void loadFromNbt( CompoundNBT compound )
    {
        containers.clear();
        ItemStackContainerUtil.loadAllItems( compound, containers );
        maxPage = compound.getInt( "MaxPage" );
        page = compound.getInt( "Page" );
    }

    public CompoundNBT saveToNbt( CompoundNBT compound )
    {
        ItemStackContainerUtil.saveAllItems( compound, containers );
        compound.putInt( "MaxPage", maxPage );
        compound.putInt( "Page", page );
        return compound;
    }

    @Override
    protected IItemHandler createUnSidedHandler()
    {
        return getItemHandler();
    }

    @Override
    public TerribleChestItemHandler getItemHandler()
    {
        if ( Config.COMMON.useSinglePageMode.get() )
        {
            return TerribleChestItemHandler.create( containers, 0, 133 );
        }
        else
        {
            int slots = Config.COMMON.inventoryRows.get() * 9;
            return TerribleChestItemHandler.create( containers, () -> page * slots, () -> slots );
        }
    }

    @Override
    protected ITextComponent getDefaultName()
    {
        return TerribleChestTileEntity.CONTAINER_NAME;
    }

    @Override
    protected Container createMenu( int id, PlayerInventory playerInventory )
    {
        return new TerribleChestContainer( id, playerInventory, this, chestData );
    }

    @Override
    public boolean isUsableByPlayer( PlayerEntity player )
    {
        World world = getWorld();
        BlockPos pos = getPos();
        if ( world == null || world.getTileEntity( pos ) != this )
        {
            return false;
        }
        else
        {
            return !( player.getDistanceSq( pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D ) > 64.0D );
        }
    }

    @Override
    public void read( BlockState state, CompoundNBT compound )
    {
        super.read( state, compound );
        loadFromNbt( compound );
    }

    @Override
    public CompoundNBT write( CompoundNBT compound )
    {
        super.write( compound );
        return saveToNbt( compound );
    }
}
