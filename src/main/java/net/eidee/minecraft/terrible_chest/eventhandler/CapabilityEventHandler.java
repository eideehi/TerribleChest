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

package net.eidee.minecraft.terrible_chest.eventhandler;

import net.eidee.minecraft.terrible_chest.TerribleChestMod;
import net.eidee.minecraft.terrible_chest.capability.Capabilities;
import net.eidee.minecraft.terrible_chest.capability.TerribleChestCapability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber( modid = TerribleChestMod.MOD_ID )
public class CapabilityEventHandler
{
    @SubscribeEvent
    public static void clone( PlayerEvent.Clone event )
    {
        PlayerEntity original = event.getOriginal();
        original.revive();
        original.getCapability( Capabilities.TERRIBLE_CHEST ).ifPresent( chest -> {
            PlayerEntity clone = event.getPlayer();
            clone.getCapability( Capabilities.TERRIBLE_CHEST ).ifPresent( cloneChest -> {
                cloneChest.deserializeNBT( chest.serializeNBT() );
            } );
        } );
        original.remove( true );
    }

    @SubscribeEvent
    public static void attachCapabilities( AttachCapabilitiesEvent< Entity > event )
    {
        Entity entity = event.getObject();
        if ( entity instanceof PlayerEntity )
        {
            event.addCapability( TerribleChestCapability.REGISTRY_KEY, TerribleChestCapability.provider() );
        }
    }
}
