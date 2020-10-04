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

package net.eidee.minecraft.terrible_chest.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.TerribleChest;
import net.eidee.minecraft.terrible_chest.gui.GuiHandler;
import net.eidee.minecraft.terrible_chest.item.ItemStackContainer;
import net.eidee.minecraft.terrible_chest.tileentity.TerribleChestTileEntity2;
import net.eidee.minecraft.terrible_chest.util.ItemStackContainerUtil;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TerribleChestBlock2
    extends BlockContainer
{
    public TerribleChestBlock2()
    {
        super( Material.IRON, MapColor.EMERALD );
        setHardness( 3.0F );
        setResistance( 10.0F );
        setSoundType( SoundType.METAL );
    }

    @SideOnly( Side.CLIENT )
    @Override
    public void addInformation( ItemStack stack, @Nullable World player, List< String > tooltip, ITooltipFlag advanced )
    {
        NBTTagCompound compound = stack.getTagCompound();

        if ( compound != null && compound.hasKey( "BlockEntityTag", Constants.NBT.TAG_COMPOUND ) )
        {
            NBTTagCompound blockEntityTag = compound.getCompoundTag( "BlockEntityTag" );

            if ( blockEntityTag.hasKey( "Items", Constants.NBT.TAG_LIST ) )
            {
                Int2ObjectMap< ItemStackContainer > containers = ItemStackContainerUtil.newContainers();
                ItemStackContainerUtil.loadAllItems( blockEntityTag, containers );

                int i = 0;
                int j = 0;

                for ( ItemStackContainer next : containers.values() )
                {
                    if ( !next.isEmpty() )
                    {
                        ++j;

                        if ( i <= 4 )
                        {
                            ++i;
                            tooltip.add( String.format( "%s x%d", next.getStack().getDisplayName(), next.getCount() ) );
                        }
                    }
                }

                if ( j - i > 0 )
                {
                    tooltip.add( String.format( TextFormatting.ITALIC + I18n.translateToLocal( "container.shulkerBox.more" ), j - i ) );
                }
            }
        }
    }

    @Override
    public ItemStack getPickBlock( IBlockState state,
                                   RayTraceResult target,
                                   World world,
                                   BlockPos pos,
                                   EntityPlayer player )
    {
        ItemStack stack = super.getPickBlock( state, target, world, pos, player );

        TileEntity tileEntity = world.getTileEntity( pos );
        if ( tileEntity instanceof TerribleChestTileEntity2 )
        {
            NBTTagCompound compound = ( ( TerribleChestTileEntity2 )tileEntity ).writeTerribleChestData(
                new NBTTagCompound() );
            if ( !compound.hasNoTags() )
            {
                stack.setTagInfo( "BlockEntityTag", compound );
            }
        }

        return stack;
    }

    @Override
    public void breakBlock( World worldIn, BlockPos pos, IBlockState state )
    {
        TileEntity tileentity = worldIn.getTileEntity( pos );

        if ( tileentity instanceof TerribleChestTileEntity2 )
        {
            TerribleChestTileEntity2 terribleChestTileEntity2 = ( TerribleChestTileEntity2 )tileentity;

            ItemStack stack = new ItemStack( Item.getItemFromBlock( this ) );
            NBTTagCompound compound = new NBTTagCompound();
            compound.setTag( "BlockEntityTag",
                             terribleChestTileEntity2.writeTerribleChestData( new NBTTagCompound() ) );
            stack.setTagCompound( compound );

            spawnAsEntity( worldIn, pos, stack );
        }

        super.breakBlock( worldIn, pos, state );
    }

    @Override
    public boolean isOpaqueCube( IBlockState state )
    {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType( IBlockState state )
    {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity( World worldIn, int meta )
    {
        return new TerribleChestTileEntity2();
    }

    @Override
    public boolean onBlockActivated( World worldIn,
                                     BlockPos pos,
                                     IBlockState state,
                                     EntityPlayer playerIn,
                                     EnumHand hand,
                                     EnumFacing facing,
                                     float hitX,
                                     float hitY,
                                     float hitZ )
    {
        if ( !worldIn.isRemote )
        {
            TileEntity tileEntity = worldIn.getTileEntity( pos );
            if ( tileEntity instanceof TerribleChestTileEntity2 )
            {
                playerIn.openGui( TerribleChest.INSTANCE,
                                  GuiHandler.GUI_TERRIBLE_CHEST_2,
                                  worldIn,
                                  pos.getX(),
                                  pos.getY(),
                                  pos.getZ() );
            }
        }
        return true;
    }
}
