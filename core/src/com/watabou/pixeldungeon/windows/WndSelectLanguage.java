/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014  Oleg Dolya
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

import com.watabou.pixeldungeon.PixelDungeon;
import com.watabou.pixeldungeon.ui.RedButton;
import com.watabou.pixeldungeon.ui.Window;

public class WndSelectLanguage extends Window {

	public WndSelectLanguage(String[] options) {
		super();

		int WIDTH = 120;
        
        int GAP = 2;
        
        int BUTTON_HEIGHT = 20;

		int BUTTON_SIZE = PixelDungeon.landscape() ? 4 : 6;

		float pos = GAP;

		final int columns = PixelDungeon.landscape() ? 4 : 3;

		int BUTTON_WIDTH = WIDTH / columns - GAP;

		int lastButtonBottom = 0;

		for (int i = 0; i < options.length / columns + 1; i++) {

			for (int j = 0; j < columns; j++) {
				final int index = i * columns + j;
				if (!(index < options.length)) {
					break;
				}
				RedButton btn = new RedButton(options[index]) {
					@Override
					protected void onClick() {
						hide();
						onSelect(index);
					}
				};

				btn.setRect(GAP + j * (BUTTON_WIDTH + GAP), pos, BUTTON_WIDTH, BUTTON_HEIGHT);
                btn.btnsize(BUTTON_SIZE);
				add(btn);

				lastButtonBottom = (int) btn.bottom();
			}
			pos += BUTTON_HEIGHT + GAP;
		}

		resize(WIDTH, lastButtonBottom + GAP);
	}

	protected void onSelect(int index) {
	}
}
