package vonn.droidtheremin;


import java.util.TreeMap;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

// A simple text management/rendering class to render shit
public class TextManager {
	public TreeMap<String, TextObject> textMap;

	public TextManager() {
		textMap = new TreeMap<String, TextObject>();
	}

	public void render(Canvas canvas) {
		for (TreeMap.Entry<String, TextObject> entry : textMap.entrySet()) {
			// Set up font
			Paint p = new Paint();
			p.setTextSize(40);
			Typeface bold = Typeface.DEFAULT_BOLD;
			p.setTypeface(bold);
			// and render text where it needs to be
			TextObject t = entry.getValue();
			p.setColor(Color.BLACK);
			canvas.drawText(t.s, t.x, t.y, p);
		}
	}
}

// A string with an x and y position
class TextObject {
	public int x;
	public int y;
	public String s;

	public TextObject(String string, int x, int y) {
		this.s = string;
		this.x = x;
		this.y = y;
	}
}