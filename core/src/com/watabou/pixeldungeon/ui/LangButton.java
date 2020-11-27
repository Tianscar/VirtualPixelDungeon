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
package com.watabou.pixeldungeon.ui;

//import com.kurtyu.IabInterface;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Button;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.windows.WndSettings;
import com.watabou.pixeldungeon.windows.WndSelectLanguage;
import com.watabou.pixeldungeon.PixelDungeon;
import net.whitegem.pixeldungeon.LanguageUtil;
import com.watabou.noosa.Game;

public class LangButton extends Button {
	
	private Image image;

	public LangButton() {
		super();

		width = image.width;
		height = image.height;
	}
	
	@Override
	protected void createChildren() {
		super.createChildren();
		
		image = Icons.BOOK.get();
		add( image );
	}
	
	@Override
	protected void layout() {
		super.layout();
		
		image.x = x;
		image.y = y;
	}
	
	@Override
	protected void onTouchDown() {
		image.brightness( 1.5f );
		Sample.INSTANCE.play( Assets.SND_CLICK );
	}
	
	@Override
	protected void onTouchUp() {
		image.resetColor();
	}
	
	@Override
	protected void onClick()
	{
		parent.add( new WndSelectLanguage(LanguageUtil.langtext) {

                @Override
                protected void onSelect(int index) {               
                    PixelDungeon.language(LanguageUtil.lang[index]);
                    LanguageUtil.setLanguage();
                    Game.platform.resetGenerators();
                    Game.resetScene();
                }
            } );
	}
}
