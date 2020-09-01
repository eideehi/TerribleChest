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
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TerribleChestBlock
    extends Block
    implements IWaterLoggable
{
    private static final ITextComponent MESSAGE_AUTH_ERROR;

    static
    {
        MESSAGE_AUTH_ERROR = new TranslationTextComponent( "message.terrible_chest.auth_error" );
    }

    static final VoxelShape TERRIBLE_CHEST_SHAPE;

    static
    {
        TERRIBLE_CHEST_SHAPE = VoxelShapes.or( Block.makeCuboidShape( 0.0D, 0.0D, 0.0D, 4.0D, 16.0D, 4.0D ),
                                               Block.makeCuboidShape( 0.0D, 0.0D, 12.0D, 4.0D, 16.0D, 16.0D ),
                                               Block.makeCuboidShape( 0.0D, 0.0D, 4.0D, 4.0D, 4.0D, 12.0D ),
                                               Block.makeCuboidShape( 12.0D, 0.0D, 4.0D, 16.0D, 4.0D, 12.0D ),
                                               Block.makeCuboidShape( 0.0D, 12.0D, 4.0D, 4.0D, 16.0D, 12.0D ),
                                               Block.makeCuboidShape( 12.0D, 12.0D, 4.0D, 16.0D, 16.0D, 12.0D ),
                                               Block.makeCuboidShape( 12.0D, 0.0D, 0.0D, 16.0D, 16.0D, 4.0D ),
                                               Block.makeCuboidShape( 12.0D, 0.0D, 12.0D, 16.0D, 16.0D, 16.0D ),
                                               Block.makeCuboidShape( 4.0D, 0.0D, 0.0D, 12.0D, 4.0D, 4.0D ),
                                               Block.makeCuboidShape( 4.0D, 12.0D, 0.0D, 12.0D, 16.0D, 4.0D ),
                                               Block.makeCuboidShape( 4.0D, 0.0D, 12.0D, 12.0D, 4.0D, 16.0D ),
                                               Block.makeCuboidShape( 4.0D, 12.0D, 12.0D, 12.0D, 16.0D, 16.0D ) );
    }

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public TerribleChestBlock( Properties properties )
    {
        super( properties );
        setDefaultState( stateContainer.getBaseState().with( WATERLOGGED, false ) );
    }

    @Override
    public VoxelShape getShape( BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context )
    {
        return TERRIBLE_CHEST_SHAPE;
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
        TileEntity tileentity = worldIn.getTileEntity( pos );
        if ( tileentity instanceof TerribleChestTileEntity )
        {
            TerribleChestTileEntity chestTileEntity = ( TerribleChestTileEntity )tileentity;
            if ( placer instanceof PlayerEntity )
            {
                chestTileEntity.setOwnerId( placer.getUniqueID() );
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
            TileEntity tileEntity = worldIn.getTileEntity( pos );
            if ( tileEntity instanceof TerribleChestTileEntity )
            {
                if ( ( ( TerribleChestTileEntity )tileEntity ).isOwner( player ) )
                {
                    player.openContainer( state.getContainer( worldIn, pos ) );
                }
                else
                {
                    player.sendMessage( MESSAGE_AUTH_ERROR, Util.DUMMY_UUID );
                }
            }
            return ActionResultType.CONSUME;
        }
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
