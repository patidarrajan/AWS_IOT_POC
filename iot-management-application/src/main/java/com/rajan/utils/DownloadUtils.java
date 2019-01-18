/**
 * 
 */
package com.rajan.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author rajan
 *
 */
public class DownloadUtils {

	public static void downloadFromUrl(String downloadUrl,String outputFileName) throws IOException {
		URL url = new URL(downloadUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		InputStream in = connection.getInputStream();
		FileOutputStream out = new FileOutputStream(outputFileName);
		copy(in, out, 1024);
		out.close();

	}

	  public static void copy(InputStream input, OutputStream output, int bufferSize) throws IOException {
	    byte[] buf = new byte[bufferSize];
	    int n = input.read(buf);
	    while (n >= 0) {
	      output.write(buf, 0, n);
	      n = input.read(buf);
	    }
	    output.flush();
	  }
}
