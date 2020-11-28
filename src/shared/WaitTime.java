package shared;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class WaitTime {
	
	public long wait(int time, InputStream inputStream) throws IOException {
		// time unit: second
		
		long startTime = System.currentTimeMillis();
		while ((System.currentTimeMillis() - startTime) < time * 1000 && !(inputStream.available() > 0)) {
			// wait...
		}
		return System.currentTimeMillis() - startTime;
	}
	// ----------------------------------------------------------------------
	public long wait(int time, Scanner scanner) {
		
		long startTime = System.currentTimeMillis();
		while ((System.currentTimeMillis() - startTime) < time * 1000 && !(scanner.hasNextLine())) {
			// wait...
		}
		return System.currentTimeMillis() - startTime;		
	}
}
