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
package com.watabou.pixeldungeon.sprites;

import com.watabou.glwrap.Matrix;
import com.watabou.glwrap.Vertexbuffer;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.NoosaScript;
import com.watabou.noosa.TextureFilm;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.DungeonTilemap;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.pixeldungeon.plants.Plant;

public class PlantSprite extends Image {

	private static final float DELAY = 0.2f;
    
    protected boolean renderShadow  = false;
    protected float shadowWidth     = 1.2f;
    protected float shadowHeight    = 0.25f;
	protected float shadowOffset    = 0f;
	
	private static enum State {
		GROWING, NORMAL, WITHERING
	}
	private State state = State.NORMAL;
	private float time;
	
	private static TextureFilm frames;
	
	private int pos = -1;
	
	public PlantSprite() {
		super( Assets.PLANTS );
        renderShadow = true;
		
		if (frames == null) {
			frames = new TextureFilm( texture, 16, 16 );
		}
		
		origin.set( 8, 12 );
	}
	
	public PlantSprite( int image ) {
		this();
		reset( image );
	}
	
	public void reset( Plant plant ) {
		
		revive();
		
		reset( plant.image );
		alpha( 1f );
		
		pos = plant.pos;
		x = (pos % Level.WIDTH) * DungeonTilemap.SIZE;
		y = (pos / Level.WIDTH) * DungeonTilemap.SIZE;
		
		state = State.GROWING;
		time = DELAY;
	}
	
	public void reset( int image ) {
		frame( frames.get( image ) );
	}
    
    private float[] shadowMatrix = new float[16];

    @Override
    protected void updateMatrix() {
        super.updateMatrix();
        Matrix.copy(matrix, shadowMatrix);
        Matrix.translate(shadowMatrix,
                         (width * (1f - shadowWidth)) / 2f,
                         (height * (1f - shadowHeight)) + shadowOffset);
        Matrix.scale(shadowMatrix, shadowWidth, shadowHeight);
    }

    @Override
    public void draw() {
        if (texture == null || (!dirty && buffer == null))
            return;

        if (renderShadow) {
            if (dirty) {
                verticesBuffer.position(0);
                verticesBuffer.put(vertices);
                if (buffer == null)
                    buffer = new Vertexbuffer(verticesBuffer);
                else
                    buffer.updateVertices(verticesBuffer);
                dirty = false;
            }

            NoosaScript script = script();

            texture.bind();

            script.camera(camera());

            updateMatrix();

            script.uModel.valueM4(shadowMatrix);
            script.lighting(
                0, 0, 0, am * .6f,
                0, 0, 0, aa * .6f);

            script.drawQuad(buffer);
        }

        super.draw();

	}
	
	@Override
	public void update() {
		super.update();
		
		visible = pos == -1 || Dungeon.visible[pos];
		
		switch (state) {
		case GROWING:
			if ((time -= Game.elapsed) <= 0) {
				state = State.NORMAL;
				scale.set( 1 );
			} else {
				scale.set( 1 - time / DELAY );
			}
			break;
		case WITHERING:
			if ((time -= Game.elapsed) <= 0) {
				super.kill();
			} else {
				alpha( time / DELAY );
			}
			break;
		default:
		}
	}
	
	@Override
	public void kill() {
		state = State.WITHERING;
		time = DELAY;
	}
}
