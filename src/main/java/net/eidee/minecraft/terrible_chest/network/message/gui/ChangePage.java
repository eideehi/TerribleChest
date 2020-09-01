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

package net.eidee.minecraft.terrible_chest.network.message.gui;

import java.util.function.Supplier;

import net.eidee.minecraft.terrible_chest.inventory.container.TerribleChestContainer;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public final class ChangePage
{
    private final int page;

    public ChangePage( int page )
    {
        this.page = page;
    }

    public int getPage()
    {
        return page;
    }

    public static void encode( ChangePage message, PacketBuffer buffer )
    {
        buffer.writeInt( message.page );
    }

    public static ChangePage decode( PacketBuffer buffer )
    {
        return new ChangePage( buffer.readInt() );
    }

    public static void handle( ChangePage message, Supplier< NetworkEvent.Context > ctx )
    {
        NetworkEvent.Context _ctx = ctx.get();
        _ctx.enqueueWork( () -> {
            ServerPlayerEntity player = _ctx.getSender();
            if ( player != null && player.openContainer instanceof TerribleChestContainer )
            {
                ( ( TerribleChestContainer )player.openContainer ).changePage( message.getPage() );
            }
        } );
        _ctx.setPacketHandled( true );
    }
}
