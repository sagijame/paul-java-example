package mml.paul.window;

import java.awt.TextArea;

@SuppressWarnings("serial")
public class Output extends TextArea {

	public Output() {
		super();
		setEditable(false);
	}
	
	public void log(String name, String text) {
		append(name + " : " + text + "\n");
	}
	
}
