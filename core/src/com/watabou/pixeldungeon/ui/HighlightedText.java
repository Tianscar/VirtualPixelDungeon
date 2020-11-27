package com.watabou.pixeldungeon.ui;

import com.watabou.noosa.BitmapTextMultiline;
import com.watabou.noosa.ui.Component;
import com.watabou.pixeldungeon.scenes.PixelScene;
import com.watabou.utils.Highlighter;

public class HighlightedText extends Component {

	protected BitmapTextMultiline normal;
	protected BitmapTextMultiline highlighted;
	
	protected int nColor = 0xFFFFFF;
	protected int hColor = 0xFFFF44;
	
	public HighlightedText( float size ) {
		normal = PixelScene.createMultiline( size );
		add( normal );
		
		highlighted = PixelScene.createMultiline( size );
		add( highlighted );
		
		setColor( 0xFFFFFF, 0xFFFF44 );
	}
	
	@Override
	protected void layout() {
		normal.setPos(x, y);
		highlighted.setPos(x, y);
	}
	
	public void text( String value, int maxWidth ) {
		Highlighter hl = new Highlighter( value );
		
		normal.text( hl.text );
		normal.maxWidth(maxWidth);
		
		
		if (hl.isHighlighted()) {
//			normal.mask = hl.inverted();
			
			highlighted.text( hl.text );
			highlighted.maxWidth(maxWidth);
			
//			highlighted.mask = hl.mask;
			highlighted.visible = true;
		} else {
			highlighted.visible = false;
		}
		
		width = normal.width();
		height = normal.height();
	}
	
	public void setColor( int n, int h ) {
		normal.hardlight( n );
		highlighted.hardlight( h );
	}
}
