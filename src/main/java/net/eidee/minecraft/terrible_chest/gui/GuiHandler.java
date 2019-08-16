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

package net.eidee.minecraft.terrible_chest.gui;

import javax.annotation.Nullable;

import net.eidee.minecraft.terrible_chest.inventory.container.TerribleChestContainer;
import net.eidee.minecraft.terrible_chest.tileentity.TerribleChestTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler
    implements IGuiHandler
{
    public static final int GUI_TERRIBLE_CHEST = 0;

    @Nullable
    @Override
    public Object getServerGuiElement( int ID, EntityPlayer player, World world, int x, int y, int z )
    {
        if ( ID == GUI_TERRIBLE_CHEST )
        {
            TileEntity tileEntity = world.getTileEntity( new BlockPos( x, y, z ) );
            if ( tileEntity instanceof TerribleChestTileEntity )
            {
                TerribleChestTileEntity terribleChestTileEntity = ( TerribleChestTileEntity )tileEntity;
                return terribleChestTileEntity.createContainer( player.inventory );
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement( int ID, EntityPlayer player, World world, int x, int y, int z )
    {
        if ( ID == GUI_TERRIBLE_CHEST )
        {
            TileEntity tileEntity = world.getTileEntity( new BlockPos( x, y, z ) );
            if ( tileEntity instanceof TerribleChestTileEntity )
            {
                TerribleChestContainer container = new TerribleChestContainer( player.inventory );
                return new TerribleChestScreen( container, player.inventory );
            }
        }
        return null;
    }
}
