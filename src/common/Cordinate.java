package common;

import java.util.Vector;

public class Cordinate {
	private int x;
	private int y;

	public boolean isUnique(Vector<Cordinate> targetVec, Cordinate targetCor) {
		int range = targetVec.size();
		boolean result=true;
		for(int i=0;i<range;i++) {
			if(equal(targetVec.get(i),targetCor))
				return false;
		}
		return result;
	}
	
	public boolean equal(Cordinate Cor1, Cordinate Cor2) {
		if(Cor1.getX()==Cor2.getX() && Cor1.getY()==Cor2.getY()) return true;
		else return false;
	}
	
	public void setCordinate(int x, int y) {
		this.setX(x);
		this.setY(y);
	}

	public Vector<Integer> getCordinate() {
		Vector<Integer> content = new Vector<>();
		content.add(x);
		content.add(y);
		return content;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	public void print() {
		System.out.println("X: "+x + " Y: "+y);
	}
}
