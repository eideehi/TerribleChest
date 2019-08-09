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

package net.eidee.minecraft.terrible_chest.block;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.tileentity.TerribleChestTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeBlock;
import net.minecraftforge.fml.network.NetworkHooks;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TerribleChestBlock
    extends Block
    implements IForgeBlock
{
    public TerribleChestBlock( Properties properties )
    {
        super( properties );
    }


    @Override
    public BlockRenderType getRenderType( BlockState state )
    {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }


    @Override
    public boolean hasTileEntity( BlockState state )
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity( BlockState state, IBlockReader world )
    {
        return new TerribleChestTileEntity();
    }

    @Override
    public void onBlockPlacedBy( World worldIn,
                                 BlockPos pos,
                                 BlockState state,
                                 @Nullable LivingEntity placer,
                                 ItemStack stack )
    {
        TileEntity tileEntity = worldIn.getTileEntity( pos );
        if ( tileEntity instanceof TerribleChestTileEntity &&
             placer instanceof PlayerEntity )
        {
            PlayerEntity player = ( PlayerEntity )placer;
            ( ( TerribleChestTileEntity )tileEntity ).setOwnerId( player.getUniqueID() );
        }
    }

    @Override
    public boolean onBlockActivated( BlockState state,
                                     World worldIn,
                                     BlockPos pos,
                                     PlayerEntity player,
                                     Hand handIn,
                                     BlockRayTraceResult hit )
    {
        if ( !worldIn.isRemote() )
        {
            TileEntity tileEntity = worldIn.getTileEntity( pos );
            if ( tileEntity instanceof TerribleChestTileEntity )
            {
                TerribleChestTileEntity terribleChest = ( TerribleChestTileEntity )tileEntity;
                if ( terribleChest.isOwner( player ) && player instanceof ServerPlayerEntity )
                {
                    NetworkHooks.openGui( ( ServerPlayerEntity )player, terribleChest );
                }
            }
        }
        return true;
    }
}
