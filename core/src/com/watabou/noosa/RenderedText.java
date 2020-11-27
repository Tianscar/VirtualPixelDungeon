/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
 *
 * Virtual Pixel Dungeon
 * Copyright (C) 2020-2021 AnsdoShip Studio
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

package com.watabou.noosa;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Matrix4;
import com.watabou.glwrap.Matrix;
import com.watabou.glwrap.Quad;

import java.nio.FloatBuffer;
import java.util.HashMap;
import com.badlogic.gdx.Gdx;

public class RenderedText extends Image {
	
	private BitmapFont font = null;
	private int size;
	private String text;

	public static final int NORMAL = -1;
	public static final int BOLD = 0;
	public static final int ITALIC = 1;
	public static final int DELETELINE = 2;
	public static final int UNDERLINE = 3;
	public static final int RANDOM = 4;

	private int style = NORMAL;
	
	public RenderedText( ) {
		text = null;
	}
	
	public RenderedText( int size ){
		text = null;
		this.size = size;
	}
	
	public RenderedText(String text, int size){
		this.text = text;
		this.size = size;
		
		measure();
	}
	
	public void text( String text ){
		this.text = text;
		
		measure();
	}
	
	public String text(){
		return text;
	}
	
	public void size( int size ){
		this.size = size;
		measure();
	}

	public void setStyle (int style) {
		this.style = style;
	}

	public int getStyle() {
		return style;
	}

	public void addStyle (int style) {
		this.style = this.style | style;
		measure();
	}

	private synchronized void measure(){
		
		if ( text == null || text.equals("") ) {
			text = "";
			width=height=0;
			visible = false;
			return;
		} else {
			visible = true;
		}
		
		font = Game.platform.getFont(size, text);
		
		if (font != null){
			GlyphLayout glyphs = new GlyphLayout( font, text);
			
			for (char c : text.toCharArray()) {
				BitmapFont.Glyph g = font.getData().getGlyph(c);
				if (g == null || (g.id != c)){
					Gdx.app.log("cs","font file " + font.toString() + " could not render " + c);
				}
			}
			
			//We use the xadvance of the last glyph in some cases to fix issues
			// with fullwidth punctuation marks in some asian scripts
			BitmapFont.Glyph lastGlyph = font.getData().getGlyph(text.charAt(text.length()-1));
			if (lastGlyph != null && lastGlyph.xadvance > lastGlyph.width*1.5f){
				width = glyphs.width - lastGlyph.width + lastGlyph.xadvance;
			} else {
				width = glyphs.width;
			}
			
			//this is identical to l.height in most cases, but we force this for consistency.
			height = Math.round(size*0.75f);
			renderedHeight = glyphs.height;
		}
	}
	
	private float renderedHeight = 0;
	
	@Override
	protected void updateMatrix() {
		super.updateMatrix();
		//sometimes the font is rendered oddly, so we offset here to put it in the correct spot
		if (renderedHeight != height) {
			Matrix.translate(matrix, 0, Math.round(height - renderedHeight));
		}
	}
	
	private static TextRenderBatch textRenderer = new TextRenderBatch();
	
	@Override
	public synchronized void draw() {
		if (font != null) {
			updateMatrix();
			TextRenderBatch.textBeingRendered = this;
			font.draw(textRenderer, text, 0, 0);
			if (style == BOLD) {

			}
			if (style == ITALIC) {

			}
			if (style == DELETELINE) {

			}
			if (style == UNDERLINE) {
				font.draw(textRenderer, text.replaceAll(".", "_"), 0, 0);
			}
			if (style == RANDOM) {

			}
		}
	}

	// implements regular PD rendering within a libGDX batch so that our rendering logic
	// can interface with the freetype font generator
	private static class TextRenderBatch implements Batch {
		
		// this isn't as good as only updating once, like with bitmaptext
		// but it skips almost all allocations, which is almost as good
		private static RenderedText textBeingRendered = null;
		private static float[] vertices = new float[16];
		private static HashMap<Integer, FloatBuffer> buffers = new HashMap<>();
		
		@Override
		public void draw(Texture texture, float[] spriteVertices, int offset, int count) {
			Visual v = textBeingRendered;
			
			FloatBuffer toOpenGL;
			if (buffers.containsKey(count/20)){
				toOpenGL = buffers.get(count/20);
				toOpenGL.position(0);
			} else {
				toOpenGL = Quad.createSet(count / 20);
				buffers.put(count/20, toOpenGL);
			}
			
			for (int i = 0; i < count; i += 20){
				
				vertices[0] 	= spriteVertices[i+0];
				vertices[1] 	= spriteVertices[i+1];
				
				vertices[2]		= spriteVertices[i+3];
				vertices[3]		= spriteVertices[i+4];
				
				vertices[4] 	= spriteVertices[i+5];
				vertices[5] 	= spriteVertices[i+6];
				
				vertices[6]		= spriteVertices[i+8];
				vertices[7]		= spriteVertices[i+9];
				
				vertices[8] 	= spriteVertices[i+10];
				vertices[9] 	= spriteVertices[i+11];
				
				vertices[10]	= spriteVertices[i+13];
				vertices[11]	= spriteVertices[i+14];
				
				vertices[12]	= spriteVertices[i+15];
				vertices[13]	= spriteVertices[i+16];
				
				vertices[14]	= spriteVertices[i+18];
				vertices[15]	= spriteVertices[i+19];
				
				toOpenGL.put(vertices);
				
			}
			
			toOpenGL.position(0);
			
			NoosaScript script = NoosaScript.get();
			
			texture.bind();
			
			
			script.camera( v.camera() );
			
			script.uModel.valueM4( v.matrix );
			script.lighting(
					v.rm, v.gm, v.bm, v.am,
					v.ra, v.ga, v.ba, v.aa );
			
			script.drawQuadSet( toOpenGL, count/20 );
		}
		
		//none of these functions are needed, so they are stubbed
		@Override
		public void begin() { }
		public void end() { }
		public void setColor(Color tint) { }
		public void setColor(float r, float g, float b, float a) { }
		public Color getColor() { return null; }
		public void setPackedColor(float packedColor) { }
		public float getPackedColor() { return 0; }
		public void draw(Texture texture, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY) { }
		public void draw(Texture texture, float x, float y, float width, float height, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY) { }
		public void draw(Texture texture, float x, float y, int srcX, int srcY, int srcWidth, int srcHeight) { }
		public void draw(Texture texture, float x, float y, float width, float height, float u, float v, float u2, float v2) { }
		public void draw(Texture texture, float x, float y) { }
		public void draw(Texture texture, float x, float y, float width, float height) { }
		public void draw(TextureRegion region, float x, float y) { }
		public void draw(TextureRegion region, float x, float y, float width, float height) { }
		public void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) { }
		public void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, boolean clockwise) { }
		public void draw(TextureRegion region, float width, float height, Affine2 transform) { }
		public void flush() { }
		public void disableBlending() { }
		public void enableBlending() { }
		public void setBlendFunction(int srcFunc, int dstFunc) { }
		public void setBlendFunctionSeparate(int srcFuncColor, int dstFuncColor, int srcFuncAlpha, int dstFuncAlpha) { }
		public int getBlendSrcFunc() { return 0; }
		public int getBlendDstFunc() { return 0; }
		public int getBlendSrcFuncAlpha() { return 0; }
		public int getBlendDstFuncAlpha() { return 0; }
		public Matrix4 getProjectionMatrix() { return null; }
		public Matrix4 getTransformMatrix() { return null; }
		public void setProjectionMatrix(Matrix4 projection) { }
		public void setTransformMatrix(Matrix4 transform) { }
		public void setShader(ShaderProgram shader) { }
		public ShaderProgram getShader() { return null; }
		public boolean isBlendingEnabled() { return false; }
		public boolean isDrawing() { return false; }
		public void dispose() { }
	}
}
