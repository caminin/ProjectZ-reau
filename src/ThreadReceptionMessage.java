import java.io.IOException;
import java.io.ObjectInputStream;

import javafx.scene.control.TextArea;

class ThreadReceptionMessage extends Thread{
	private ObjectInputStream in;
	private TextArea textArea;
	
	public ThreadReceptionMessage(ObjectInputStream in,TextArea textArea){
		this.in=in;
		this.textArea=textArea;
	}
	
	public void run(){
		try {
			while(in!=null){
				textArea.appendText(in.readObject()+"\n");
			}
		} catch (IOException e) {
			textArea.appendText("server offline");
		} catch (ClassNotFoundException e) {
		}
	}
}