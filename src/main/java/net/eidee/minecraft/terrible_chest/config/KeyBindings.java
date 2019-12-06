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

package net.eidee.minecraft.terrible_chest.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyConflictContext;

@OnlyIn( Dist.CLIENT )
public class KeyBindings
{
    private static final List< KeyBinding > keyBindings;
    public static final KeyBinding SORT_0;
    public static final KeyBinding SORT_1;
    public static final KeyBinding SORT_2;
    public static final KeyBinding SORT_3;
    public static final KeyBinding SORT_4;
    public static final KeyBinding SORT_5;
    public static final KeyBinding SORT_6;
    public static final KeyBinding SORT_7;
    public static final KeyBinding SORT_8;
    public static final KeyBinding SORT_9;

    static
    {
        ArrayList< KeyBinding > list = Lists.newArrayList();

        SORT_0 = new KeyBinding( "key.terrible_chest.sort0",
                                 KeyConflictContext.GUI,
                                 InputMappings.getInputByName( "key.keyboard.0" ),
                                 "key.terrible_chest" );

        SORT_1 = new KeyBinding( "key.terrible_chest.sort1",
                                 KeyConflictContext.GUI,
                                 InputMappings.getInputByName( "key.keyboard.1" ),
                                 "key.terrible_chest" );

        SORT_2 = new KeyBinding( "key.terrible_chest.sort2",
                                 KeyConflictContext.GUI,
                                 InputMappings.getInputByName( "key.keyboard.2" ),
                                 "key.terrible_chest" );

        SORT_3 = new KeyBinding( "key.terrible_chest.sort3",
                                 KeyConflictContext.GUI,
                                 InputMappings.getInputByName( "key.keyboard.3" ),
                                 "key.terrible_chest" );

        SORT_4 = new KeyBinding( "key.terrible_chest.sort4",
                                 KeyConflictContext.GUI,
                                 InputMappings.getInputByName( "key.keyboard.4" ),
                                 "key.terrible_chest" );

        SORT_5 = new KeyBinding( "key.terrible_chest.sort5",
                                 KeyConflictContext.GUI,
                                 InputMappings.getInputByName( "key.keyboard.5" ),
                                 "key.terrible_chest" );

        SORT_6 = new KeyBinding( "key.terrible_chest.sort6",
                                 KeyConflictContext.GUI,
                                 InputMappings.getInputByName( "key.keyboard.6" ),
                                 "key.terrible_chest" );

        SORT_7 = new KeyBinding( "key.terrible_chest.sort7",
                                 KeyConflictContext.GUI,
                                 InputMappings.getInputByName( "key.keyboard.7" ),
                                 "key.terrible_chest" );

        SORT_8 = new KeyBinding( "key.terrible_chest.sort8",
                                 KeyConflictContext.GUI,
                                 InputMappings.getInputByName( "key.keyboard.8" ),
                                 "key.terrible_chest" );

        SORT_9 = new KeyBinding( "key.terrible_chest.sort9",
                                 KeyConflictContext.GUI,
                                 InputMappings.getInputByName( "key.keyboard.9" ),
                                 "key.terrible_chest" );

        Collections.addAll( list, SORT_0, SORT_1, SORT_2, SORT_3, SORT_4, SORT_5, SORT_6, SORT_7, SORT_8, SORT_9 );
        keyBindings = Collections.unmodifiableList( list );
    }

    public static List< KeyBinding > getAll()
    {
        return keyBindings;
    }
}
