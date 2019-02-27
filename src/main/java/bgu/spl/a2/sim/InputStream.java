package bgu.spl.a2.sim;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

public class InputStream {
	
	public static void main(String [] args) throws FileNotFoundException, IOException{
	ObjectInputStream in = new ObjectInputStream(new FileInputStream("result.ser"));
	int count=0;
	try {
	    while (true) {
	        count++;
	        try {
	            Object obj = in.readObject();
	            System.out.println(obj.toString());
	            
	        } catch (ClassNotFoundException e) {
	            System.out.println("can't read obj #" + count + ": " + e);
	        }
	    }
	} catch (EOFException e) {
	    // unfortunately ObjectInputStream doesn't have a good way to detect the end of the stream
	    // so just ignore this exception - it's expected when there are no more objects
	} finally {
	    in.close();
	}
	}
}
