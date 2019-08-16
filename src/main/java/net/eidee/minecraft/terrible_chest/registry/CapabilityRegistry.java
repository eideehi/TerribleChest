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

package net.eidee.minecraft.terrible_chest.registry;

import net.eidee.minecraft.terrible_chest.TerribleChest;
import net.eidee.minecraft.terrible_chest.capability.Capabilities;
import net.eidee.minecraft.terrible_chest.capability.TerribleChestInventoryCapability;
import net.eidee.minecraft.terrible_chest.inventory.TerribleChestInventory;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber( modid = TerribleChest.MOD_ID )
public class CapabilityRegistry
{
    public static void register()
    {
        CapabilityManager.INSTANCE.register( TerribleChestInventory.class,
                                             new TerribleChestInventoryCapability.Storage(),
                                             TerribleChestInventory::new );
    }

    @SubscribeEvent
    public static void attachForEntity( AttachCapabilitiesEvent< Entity > event )
    {
        Entity entity = event.getObject();
        if ( entity instanceof EntityPlayer )
        {
            event.addCapability( TerribleChestInventoryCapability.REGISTRY_KEY,
                                 new TerribleChestInventoryCapability.Provider( Capabilities.TERRIBLE_CHEST ) );
        }
    }
}
