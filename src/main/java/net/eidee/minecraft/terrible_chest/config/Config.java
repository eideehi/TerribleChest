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

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config
{
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    static
    {
        Pair< Common, ForgeConfigSpec > common = new ForgeConfigSpec.Builder().configure( Common::new );
        COMMON_SPEC = common.getRight();
        COMMON = common.getLeft();
    }

    public static final class Common
    {
        public final ForgeConfigSpec.BooleanValue useSinglePageMode;
        public final ForgeConfigSpec.LongValue slotStackLimit;
        public final ForgeConfigSpec.IntValue maxPageLimit;
        public final ForgeConfigSpec.IntValue resetMaxPage;

        Common( ForgeConfigSpec.Builder builder )
        {
            builder.comment( "" )
                   .push( "common" );

            useSinglePageMode = builder.comment( "Use single page mode" )
                                       .translation( "config.terrible_chest.use_single_page_mode" )
                                       .worldRestart()
                                       .define( "useSinglePageMode", false );

            slotStackLimit = builder.comment( "Stack size limit of slot" )
                                    .translation( "config.terrible_chest.slot_stack_limit" )
                                    .worldRestart()
                                    .defineInRange( "slotStackLimit", 4294967295L, 64, 4294967295L );

            maxPageLimit = builder.comment( "Maximum page limit" )
                                  .translation( "config.terrible_chest.max_page_limit" )
                                  .worldRestart()
                                  .defineInRange( "maxPageLimit", 79536431, 2, 79536431 );

            resetMaxPage = builder.comment( "*Recovery options* Reset the max page that is 0." )
                                  .translation( "" )
                                  .worldRestart()
                                  .defineInRange( "resetMaxPage", 1, 1, 79536431 );

            builder.pop();
        }
    }
}
