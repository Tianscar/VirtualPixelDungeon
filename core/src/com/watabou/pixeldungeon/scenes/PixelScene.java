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
package com.watabou.pixeldungeon.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.watabou.input.NoosaInputProcessor;
import com.watabou.noosa.*;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.audio.Sample;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Badges;
import com.watabou.pixeldungeon.PixelDungeon;
import com.watabou.pixeldungeon.effects.BadgeBanner;
import com.watabou.utils.BitmapCache;
import com.watabou.noosa.ui.Component;

public class PixelScene extends Scene {
	
	// Minimum virtual display size for portrait orientation
	public static final float MIN_WIDTH_P		= 128;
	public static final float MIN_HEIGHT_P		= 224;
	
	// Minimum virtual display size for landscape orientation
	public static final float MIN_WIDTH_L		= 224;
	public static final float MIN_HEIGHT_L		= 160;
	
	public static float defaultZoom = 0;
	public static float minZoom;
	public static float maxZoom;
	
	public static Camera uiCamera;
	
	@Override
	public void create() {
		
		super.create();
		
		GameScene.scene = null;
		
		float minWidth, minHeight;
		if (PixelDungeon.landscape()) {
			minWidth = MIN_WIDTH_L;
			minHeight = MIN_HEIGHT_L;
		} else {
			minWidth = MIN_WIDTH_P;
			minHeight = MIN_HEIGHT_P;
		}
		
		defaultZoom = (int)Math.ceil( Game.density * 2.5 );
		while ((
			Game.width / defaultZoom < minWidth || 
			Game.height / defaultZoom < minHeight
			) && defaultZoom > 1) {
			
			defaultZoom--;
		}
			
		if (PixelDungeon.scaleUp()) {
			while (
				Game.width / (defaultZoom + 1) >= minWidth && 
				Game.height / (defaultZoom + 1) >= minHeight) {
				
				defaultZoom++;
			}	
		}
		minZoom = 1;
		maxZoom = defaultZoom * 2;	
		
		Camera.reset( new PixelCamera( defaultZoom ) );
		
		float uiZoom = defaultZoom;
		uiCamera = Camera.createFullscreen( uiZoom );
		Camera.add( uiCamera );
		
        int renderedTextPageSize;
        if (defaultZoom <= 3){
            renderedTextPageSize = 256;
        } else if (defaultZoom <= 8){
            renderedTextPageSize = 512;
        } else {
            renderedTextPageSize = 1024;
		}
        
        renderedTextPageSize *= 2;
        
        Game.platform.setupFontGenerators(renderedTextPageSize, true);
	}

	@Override
	public void destroy() {
		super.destroy();
		Game.instance.getInputProcessor().removeAllTouchEvent();
	}
	
	public static BitmapText createText( float size ) {
		return createText( "", size );
	}
	
	public static BitmapText createText( String text, float size ) {
		BitmapText result = new BitmapText( text, (int)(size*defaultZoom));
        result.zoom(1/(float)defaultZoom);
		return result;
	}
	
	public static BitmapTextMultiline createMultiline( float size ) {
		return createMultiline( "", size );
	}
	
	public static BitmapTextMultiline createMultiline( String text, float size ) {
        BitmapTextMultiline result = new BitmapTextMultiline( text, (int)(size*defaultZoom));
        result.zoom(1/(float)defaultZoom);
		return result;
	}
	
    public static float align( float pos ) {
        return Math.round(pos * defaultZoom) / (float)defaultZoom;
    }

    public static float align( Camera camera, float pos ) {
        return Math.round(pos * camera.zoom) / camera.zoom;
    }

    public static void align( Visual v ) {
        v.x = align( v.x );
        v.y = align( v.y );
	}
    
    public static void align( Component c ){
        c.setPos(align(c.left()), align(c.top()));
	}
	
	public static boolean noFade = false;
	protected void fadeIn() {
		if (noFade) {
			noFade = false;
		} else {
			fadeIn( 0xFF000000, false );
		}
	}
	
	protected void fadeIn( int color, boolean light ) {
		add( new Fader( color, light ) );
	}
	
	public static void showBadge( Badges.Badge badge ) {
		BadgeBanner banner = BadgeBanner.show( badge.image );
		banner.camera = uiCamera;
		banner.x = align( banner.camera, (banner.camera.width - banner.width) / 2 );
		banner.y = align( banner.camera, (banner.camera.height - banner.height) / 3 );
		Game.scene().add( banner );
	}
	
	protected static class Fader extends ColorBlock {
		
		private static float FADE_TIME = 1f;
		
		private boolean light;
		
		private float time;
		
		public Fader( int color, boolean light ) {
			super( uiCamera.width, uiCamera.height, color );
			
			this.light = light;
			
			camera = uiCamera;
			
			alpha( 1f );
			time = FADE_TIME;
		}
		
		@Override
		public void update() {
			
			super.update();
			
			if ((time -= Game.elapsed) <= 0) {
				alpha( 0f );
				parent.remove( this );
			} else {
				alpha( time / FADE_TIME );
			}
		}

		@Override
		public void draw() {
			if (light) {
				Gdx.gl.glBlendFunc( GL20.GL_SRC_ALPHA, GL20.GL_ONE );
				super.draw();
				Gdx.gl.glBlendFunc( GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA );
			} else {
				super.draw();
			}
		}
	}
	
	private static class PixelCamera extends Camera {
		
		public PixelCamera( float zoom ) {
			super( 
				(int)(Game.width - Math.ceil( Game.width / zoom ) * zoom) / 2, 
				(int)(Game.height - Math.ceil( Game.height / zoom ) * zoom) / 2, 
				(int)Math.ceil( Game.width / zoom ), 
				(int)Math.ceil( Game.height / zoom ), zoom );
		}
		
		@Override
		protected void updateMatrix() {
			float sx = align( this, scroll.x + shakeX );
			float sy = align( this, scroll.y + shakeY );
			
			matrix[0] = +zoom * invW2;
			matrix[5] = -zoom * invH2;
			
			matrix[12] = -1 + x * invW2 - sx * matrix[0];
			matrix[13] = +1 - y * invH2 - sy * matrix[5];
			
		}
	}
}
