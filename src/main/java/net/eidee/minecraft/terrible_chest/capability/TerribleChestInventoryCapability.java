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

package net.eidee.minecraft.terrible_chest.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.eidee.minecraft.terrible_chest.constants.Names;
import net.eidee.minecraft.terrible_chest.inventory.TerribleChestInventory;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class TerribleChestInventoryCapability
{
    public static final ResourceLocation REGISTRY_KEY = new ResourceLocation( Names.TERRIBLE_CHEST );

    public static class Storage
        implements Capability.IStorage< TerribleChestInventory >
    {
        @Nullable
        @Override
        public NBTBase writeNBT( Capability< TerribleChestInventory > capability,
                                 TerribleChestInventory instance,
                                 EnumFacing side )
        {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT( Capability< TerribleChestInventory > capability,
                             TerribleChestInventory instance,
                             EnumFacing side,
                             NBTBase nbt )
        {
            if ( nbt instanceof NBTTagCompound )
            {
                instance.deserializeNBT( ( NBTTagCompound )nbt );
            }
        }

    }

    public static class Provider
        implements ICapabilitySerializable< NBTTagCompound >
    {
        private final Capability< TerribleChestInventory > _cap;
        private TerribleChestInventory instance;

        public Provider( Capability< TerribleChestInventory > _cap )
        {
            this._cap = _cap;
            this.instance = this._cap.getDefaultInstance();
        }

        @Override
        public boolean hasCapability( @Nonnull Capability< ? > capability, @Nullable EnumFacing facing )
        {
            return capability == _cap;
        }

        @SuppressWarnings( "unchecked" )
        @Nullable
        @Override
        public < T > T getCapability( @Nonnull Capability< T > capability, @Nullable EnumFacing facing )
        {
            return capability == _cap ? ( T )instance : null;
        }

        @Override
        public NBTTagCompound serializeNBT()
        {
            return ( NBTTagCompound )_cap.writeNBT( instance, null );
        }

        @Override
        public void deserializeNBT( NBTTagCompound nbt )
        {
            _cap.readNBT( instance, null, nbt );
        }
    }
}
