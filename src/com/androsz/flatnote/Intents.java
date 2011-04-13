package com.androsz.flatnote;

/*
 * Adapted from http://stackoverflow.com/questions/604424/java-enum-converting-string-to-enum/2965252#2965252
 */
public enum Intents {
	Notebook(Intents.class+"bleh");
	

	private String text;

	Intents(String text) {
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	public static Intents fromString(String text) {
		if (text != null) {
			for (Intents b : Intents.values()) {
				if (text.equalsIgnoreCase(b.text)) {
					return b;
				}
			}
		}
		return null;
	}
}