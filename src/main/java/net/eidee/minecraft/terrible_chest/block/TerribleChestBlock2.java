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
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.item.ItemStackContainer;
import net.eidee.minecraft.terrible_chest.tileentity.TerribleChestTileEntity2;
import net.eidee.minecraft.terrible_chest.util.ItemStackContainerUtil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TerribleChestBlock2
    extends Block
    implements IWaterLoggable
{
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public TerribleChestBlock2( Properties properties )
    {
        super( properties );
        setDefaultState( stateContainer.getBaseState().with( WATERLOGGED, false ) );
    }

    @OnlyIn( Dist.CLIENT )
    @Override
    public void addInformation( ItemStack stack,
                                @Nullable IBlockReader worldIn,
                                List< ITextComponent > tooltip,
                                ITooltipFlag flagIn )
    {
        CompoundNBT compound = stack.getChildTag( "BlockEntityTag" );
        if ( compound != null )
        {
            if ( compound.contains( "Items", Constants.NBT.TAG_LIST ) )
            {
                Int2ObjectMap< ItemStackContainer > containers = ItemStackContainerUtil.newContainers();
                ItemStackContainerUtil.loadAllItems( compound, containers );

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
                            IFormattableTextComponent textComponent = next.getStack().getDisplayName().deepCopy();
                            textComponent.appendString( " x" ).appendString( String.format( "%,d", next.getCount() ) );
                            tooltip.add( textComponent );
                        }
                    }
                }

                if ( j - i > 0 )
                {
                    tooltip.add( new TranslationTextComponent( "container.shulkerBox.more", j - i ).mergeStyle(
                        TextFormatting.ITALIC ) );
                }
            }
        }
    }

    @Override
    public VoxelShape getShape( BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context )
    {
        return TerribleChestBlock.TERRIBLE_CHEST_SHAPE;
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
        return new TerribleChestTileEntity2();
    }

    @Override
    public void onBlockPlacedBy( World worldIn,
                                 BlockPos pos,
                                 BlockState state,
                                 @Nullable LivingEntity placer,
                                 ItemStack stack )
    {
        TileEntity tileentity = worldIn.getTileEntity( pos );
        if ( tileentity instanceof TerribleChestTileEntity2 )
        {
            TerribleChestTileEntity2 chestTileEntity = ( TerribleChestTileEntity2 )tileentity;
            if ( stack.hasDisplayName() )
            {
                chestTileEntity.setCustomName( stack.getDisplayName() );
            }
        }
    }

    @Nullable
    @Override
    public INamedContainerProvider getContainer( BlockState state, World worldIn, BlockPos pos )
    {
        TileEntity tileEntity = worldIn.getTileEntity( pos );
        return tileEntity instanceof INamedContainerProvider ? ( INamedContainerProvider )tileEntity : null;
    }

    @Override
    public ActionResultType onBlockActivated( BlockState state,
                                              World worldIn,
                                              BlockPos pos,
                                              PlayerEntity player,
                                              Hand handIn,
                                              BlockRayTraceResult hit )
    {
        if ( worldIn.isRemote() )
        {
            return ActionResultType.SUCCESS;
        }
        else
        {
            player.openContainer( state.getContainer( worldIn, pos ) );
            return ActionResultType.CONSUME;
        }
    }

    @Override
    public void onBlockHarvested( World worldIn, BlockPos pos, BlockState state, PlayerEntity player )
    {
        if ( !worldIn.isRemote() &&
             player.isCreative() &&
             worldIn.getGameRules().getBoolean( GameRules.DO_TILE_DROPS ) )
        {
            TileEntity tileEntity = worldIn.getTileEntity( pos );
            if ( tileEntity instanceof TerribleChestTileEntity2 )
            {
                TerribleChestTileEntity2 chestTileEntity = ( TerribleChestTileEntity2 )tileEntity;

                ItemStack stack = new ItemStack( this );
                CompoundNBT compound = chestTileEntity.saveToNbt( new CompoundNBT() );
                if ( !compound.isEmpty() )
                {
                    stack.setTagInfo( "BlockEntityTag", compound );
                }

                if ( chestTileEntity.hasCustomName() )
                {
                    stack.setDisplayName( chestTileEntity.getCustomName() );
                }

                spawnAsEntity( worldIn, pos, stack );
            }
        }

        super.onBlockHarvested( worldIn, pos, state, player );
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement( BlockItemUseContext context )
    {
        FluidState fluid = context.getWorld().getFluidState( context.getPos() );
        BlockState state = super.getStateForPlacement( context );
        return state != null ? state.with( WATERLOGGED, fluid.getFluid() == Fluids.WATER ) : null;
    }

    @Override
    public BlockState updatePostPlacement( BlockState stateIn,
                                           Direction facing,
                                           BlockState facingState,
                                           IWorld worldIn,
                                           BlockPos currentPos,
                                           BlockPos facingPos )
    {
        if ( stateIn.get( WATERLOGGED ) )
        {
            worldIn.getPendingFluidTicks()
                   .scheduleTick( currentPos, Fluids.WATER, Fluids.WATER.getTickRate( worldIn ) );
        }

        return super.updatePostPlacement( stateIn, facing, facingState, worldIn, currentPos, facingPos );
    }

    @Override
    protected void fillStateContainer( StateContainer.Builder< Block, BlockState > builder )
    {
        builder.add( WATERLOGGED );
    }

    @Override
    public FluidState getFluidState( BlockState state )
    {
        return state.get( WATERLOGGED ) ? Fluids.WATER.getStillFluidState( false ) : super.getFluidState( state );
    }
}
