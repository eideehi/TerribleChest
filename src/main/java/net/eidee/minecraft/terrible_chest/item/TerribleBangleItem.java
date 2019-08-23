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

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.capability.Capabilities;
import net.eidee.minecraft.terrible_chest.capability.TerribleChestItemsCapability;
import net.eidee.minecraft.terrible_chest.capability.logic.TerribleChestItemsLogic;
import net.eidee.minecraft.terrible_chest.inventory.ItemHandler;
import net.eidee.minecraft.terrible_chest.tileentity.TerribleChestTileEntity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TerribleBangleItem
    extends Item
{
    public TerribleBangleItem( Properties properties )
    {
        super( properties );
    }

    private void collectItems( TileEntity tileEntity, PlayerEntity playerEntity )
    {
        LazyOptional< IItemHandler > inventoryOptional;
        inventoryOptional = playerEntity.getCapability( Capabilities.TERRIBLE_CHEST )
                                        .map( TerribleChestItemsCapability::getLogic )
                                        .map( TerribleChestItemsLogic::createInventory )
                                        .map( ItemHandler::new );
        LazyOptional< IItemHandler > itemHandlerOptional;
        itemHandlerOptional = tileEntity.getCapability( CapabilityItemHandler.ITEM_HANDLER_CAPABILITY );

        inventoryOptional.ifPresent( inventory -> {
            itemHandlerOptional.ifPresent( itemHandler -> {
                for ( int i = 0; i < itemHandler.getSlots(); i++ )
                {
                    ItemStack stack = itemHandler.extractItem( i, Integer.MAX_VALUE, false );
                    for ( int j = 0; j < inventory.getSlots(); j++ )
                    {
                        stack = inventory.insertItem( j, stack, false );
                        if ( stack.isEmpty() )
                        {
                            break;
                        }
                    }
                    if ( !stack.isEmpty() )
                    {
                        itemHandler.insertItem( i, stack, false );
                    }
                }
            } );
        } );
    }

    @Override
    public ActionResult< ItemStack > onItemRightClick( World world, PlayerEntity playerEntity, Hand hand )
    {
        ItemStack heldItem = playerEntity.getHeldItem( hand );
        RayTraceResult rayTrace = rayTrace( world, playerEntity, RayTraceContext.FluidMode.NONE );
        if ( rayTrace.getType() == RayTraceResult.Type.BLOCK && rayTrace instanceof BlockRayTraceResult )
        {
            BlockRayTraceResult blockRayTrace = ( BlockRayTraceResult )rayTrace;
            BlockPos pos = blockRayTrace.getPos();
            TileEntity tileEntity = world.getTileEntity( pos );
            if ( tileEntity != null && !( tileEntity instanceof TerribleChestTileEntity ) )
            {
                playerEntity.setActiveHand( hand );
                playerEntity.swingArm( hand );
                playerEntity.playSound( SoundEvents.UI_BUTTON_CLICK, 0.25F, 1.0F );
                if ( !world.isRemote() )
                {
                    collectItems( tileEntity, playerEntity );
                }
            }
        }
        return new ActionResult<>( ActionResultType.SUCCESS, heldItem );
    }
}
