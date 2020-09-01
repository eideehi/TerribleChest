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

package net.eidee.minecraft.terrible_chest.init;

import java.util.concurrent.atomic.AtomicInteger;

import net.eidee.minecraft.terrible_chest.network.Networks;
import net.eidee.minecraft.terrible_chest.network.message.gui.ChangePage;
import net.eidee.minecraft.terrible_chest.network.message.gui.UnlockMaxPage;

public class NetworkInitializer
{
    public static void registerMessage()
    {
        AtomicInteger id = new AtomicInteger( 0 );

        Networks.getChannel()
                .registerMessage( id.getAndIncrement(),
                                  ChangePage.class,
                                  ChangePage::encode,
                                  ChangePage::decode,
                                  ChangePage::handle );

        Networks.getChannel()
                .registerMessage( id.getAndIncrement(),
                                  UnlockMaxPage.class,
                                  UnlockMaxPage::encode,
                                  UnlockMaxPage::decode,
                                  UnlockMaxPage::handle );
    }
}
