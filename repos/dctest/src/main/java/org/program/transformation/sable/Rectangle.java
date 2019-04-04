package org.program.transformation.sable;

public class Rectangle implements Drawable {
	public short height, width;
	public Rectangle(short h, short w) {
		height = h; width = w; }
	public boolean isFat() {return (width > height);}
	public void draw() {
		// Code to draw ...
	}
}
