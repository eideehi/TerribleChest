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

package net.eidee.minecraft.terrible_chest.item;

import java.util.NoSuchElementException;
import javax.annotation.ParametersAreNonnullByDefault;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.capability.Capabilities;
import net.eidee.minecraft.terrible_chest.capability.TerribleChestCapability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TerribleBangleItem
    extends Item
{
    public TerribleBangleItem( Properties properties )
    {
        super( properties );
    }

    private void moveInventoryItems( IItemHandler src, IItemHandler dest )
    {
        for ( int i = 0; i < src.getSlots(); i++ )
        {
            ItemStack stackInSlot;
            while ( !( stackInSlot = src.getStackInSlot( i ) ).isEmpty() )
            {
                ItemStack stack = src.extractItem( i, stackInSlot.getMaxStackSize(), false );

                IntList emptySlots = new IntArrayList();
                for ( int j = 0; j < dest.getSlots(); j++ )
                {
                    if ( dest.getStackInSlot( j ).isEmpty() )
                    {
                        emptySlots.add( j );
                    }
                    else
                    {
                        stack = dest.insertItem( j, stack, false );
                        if ( stack.isEmpty() )
                        {
                            break;
                        }
                    }
                }

                if ( !stack.isEmpty() )
                {
                    IntListIterator it = emptySlots.iterator();
                    while ( it.hasNext() )
                    {
                        int emptySlot = it.nextInt();
                        stack = dest.insertItem( emptySlot, stack, false );
                        if ( stack.isEmpty() )
                        {
                            break;
                        }
                    }
                }

                if ( !stack.isEmpty() )
                {
                    src.insertItem( i, stack, false );
                    break;
                }
            }
        }
    }

    private void collectItems( PlayerEntity playerEntity, TileEntity tileEntity )
    {
        IItemHandler src = tileEntity.getCapability( CapabilityItemHandler.ITEM_HANDLER_CAPABILITY )
                                     .orElse( EmptyHandler.INSTANCE );
        IItemHandler dest = playerEntity.getCapability( Capabilities.TERRIBLE_CHEST )
                                        .map( TerribleChestCapability::getContainers )
                                        .map( TerribleChestItemHandler::create )
                                        .orElseThrow( NoSuchElementException::new );
        moveInventoryItems( src, dest );
    }

    @Override
    public ActionResult< ItemStack > onItemRightClick( World world, PlayerEntity playerEntity, Hand hand )
    {
        ItemStack heldItem = playerEntity.getHeldItem( hand );

        BlockRayTraceResult rayTrace = rayTrace( world, playerEntity, RayTraceContext.FluidMode.NONE );
        if ( rayTrace.getType() != RayTraceResult.Type.BLOCK )
        {
            return ActionResult.resultPass( heldItem );
        }

        BlockPos pos = rayTrace.getPos();
        TileEntity tileEntity = world.getTileEntity( pos );
        if ( tileEntity != null )
        {
            playerEntity.setActiveHand( hand );
            playerEntity.swingArm( hand );
            playerEntity.playSound( SoundEvents.UI_BUTTON_CLICK, 0.25F, 1.0F );

            if ( !world.isRemote() )
            {
                collectItems( playerEntity, tileEntity );
            }

            return ActionResult.resultSuccess( heldItem );
        }
        else
        {
            return ActionResult.resultFail( heldItem );
        }
    }
}
