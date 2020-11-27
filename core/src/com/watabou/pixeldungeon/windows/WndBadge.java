/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.watabou.pixeldungeon.windows;

import com.watabou.noosa.BitmapText;
import com.watabou.noosa.BitmapTextMultiline;
import com.watabou.noosa.Image;
import com.watabou.pixeldungeon.Badges;
import com.watabou.pixeldungeon.effects.BadgeBanner;
import com.watabou.pixeldungeon.scenes.PixelScene;
import com.watabou.pixeldungeon.ui.Window;
import com.watabou.noosa.ui.Component;

public class WndBadge extends Window {
	
	private static final int WIDTH = 120;
    private static final int MARGIN = 4;

    public WndBadge( Badges.Badge badge ) {

        super();

        Image icon = BadgeBanner.image( badge.image );
        icon.scale.set( 2 );
        add( icon );

        BitmapTextMultiline info = PixelScene.createMultiline( badge.description, 8 );
        info.maxWidth(WIDTH - MARGIN * 2);
        info.align(BitmapTextMultiline.CENTER_ALIGN);
        PixelScene.align(info);
        add(info);

        float w = Math.max( icon.width(), info.width() ) + MARGIN * 2;

        icon.x = (w - icon.width()) / 2f;
        icon.y = MARGIN;
        PixelScene.align(icon);

        info.setPos((w - info.width()) / 2, icon.y + icon.height() + MARGIN);
        resize( (int)w, (int)(info.bottom() + MARGIN) );

        BadgeBanner.highlight( icon, badge.image );
	}
}
