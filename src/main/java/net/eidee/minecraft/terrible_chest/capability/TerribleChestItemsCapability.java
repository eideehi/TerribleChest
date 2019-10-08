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
import javax.annotation.ParametersAreNonnullByDefault;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.terrible_chest.capability.logic.TerribleChestItemsLogic;
import net.eidee.minecraft.terrible_chest.constants.Names;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TerribleChestItemsCapability
    implements INBTSerializable< CompoundNBT >
{
    public static final ResourceLocation REGISTRY_KEY = new ResourceLocation( Names.TERRIBLE_CHEST );

    private final Int2ObjectMap< TerribleChestItem > items;
    private final TerribleChestItemsLogic logic;

    public < T extends TerribleChestItemsLogic > TerribleChestItemsCapability( TerribleChestItemsLogic.Factory< T > factory )
    {
        this.items = new Int2ObjectOpenHashMap<>();
        this.logic = factory.create( this.items );
    }

    public TerribleChestItemsLogic getLogic()
    {
        return logic;
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        ListNBT list = new ListNBT();
        for ( Int2ObjectMap.Entry< TerribleChestItem > entry : items.int2ObjectEntrySet() )
        {
            int index = entry.getIntKey();
            TerribleChestItem item = entry.getValue();
            if ( item.isNotEmpty() )
            {
                CompoundNBT compound = item.serializeNBT();
                compound.putInt( "Index", index );
                list.add( compound );
            }
        }
        nbt.put( "Items", list );
        return logic.writeToNBT( nbt );
    }

    @Override
    public void deserializeNBT( CompoundNBT nbt )
    {
        logic.readFromNBT( nbt );

        ListNBT list = nbt.getList( "Items", Constants.NBT.TAG_COMPOUND );
        items.clear();
        for ( int i = 0; i < list.size(); i++ )
        {
            CompoundNBT compound = list.getCompound( i );
            int index = compound.getInt( "Index" );
            TerribleChestItem item = TerribleChestItem.read( compound );
            if ( item.isNotEmpty() )
            {
                items.put( index, item );
            }
        }
    }

    public static class Storage
        implements Capability.IStorage< TerribleChestItemsCapability >
    {
        @Nullable
        @Override
        public INBT writeNBT( Capability< TerribleChestItemsCapability > capability,
                              TerribleChestItemsCapability instance,
                              Direction side )
        {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT( Capability< TerribleChestItemsCapability > capability,
                             TerribleChestItemsCapability instance,
                             Direction side,
                             INBT nbt )
        {
            if ( nbt instanceof CompoundNBT )
            {
                instance.deserializeNBT( ( CompoundNBT )nbt );
            }
        }
    }

    public static class Provider
        implements ICapabilitySerializable< CompoundNBT >
    {
        private final Capability< TerribleChestItemsCapability > _cap;
        private TerribleChestItemsCapability instance;

        public Provider( Capability< TerribleChestItemsCapability > _cap )
        {
            this._cap = _cap;
            this.instance = this._cap.getDefaultInstance();
        }

        @Nonnull
        @Override
        public < T > LazyOptional< T > getCapability( @Nonnull Capability< T > cap, @Nullable Direction side )
        {
            return _cap.orEmpty( cap, LazyOptional.of( () -> instance ) );
        }

        @Override
        public CompoundNBT serializeNBT()
        {
            INBT inbt = _cap.writeNBT( instance, null );
            return inbt instanceof CompoundNBT ? ( CompoundNBT )inbt : new CompoundNBT();
        }

        @Override
        public void deserializeNBT( CompoundNBT nbt )
        {
            _cap.readNBT( instance, null, nbt );
        }
    }
}
