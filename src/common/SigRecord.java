package common;

import java.util.Vector;

public class SigRecord {
	private Vector<String> phrase;
	private Vector<Cordinate> cordinates;

	public Vector<String> getPhrase() {
		return phrase;
	}
	public void setPhrase(Vector<String> phrase) {
		this.phrase = phrase;
	}
	public Vector<Cordinate> getCordinates() {
		return cordinates;
	}
	public void setCordinates(Vector<Cordinate> cordinates) {
		this.cordinates = cordinates;
	}
	public void print() {
		int pSize = phrase.size();
		int cSize = cordinates.size();
		System.out.println("phrase: ");
		for(int i=0;i<pSize;i++) {
			System.out.println(phrase.get(i));
		}
		System.out.println("cordinates: ");
		for(int i=0;i<cSize;i++) {
			System.out.print("index: " + i + " ");
			cordinates.get(i).print();
		}
	}
}
