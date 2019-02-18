/**
 * 
 */
package com.rajan.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.amazonaws.services.iot.client.AWSIotException;
import com.rajan.model.JobDocument;

/**
 * @author rajan
 *
 */
public class JobOperations {
	public static void changeCertificate(JobDocument jobDocument) throws IOException {
		String certificatePem = jobDocument.getCertificatePem();
		Files.copy(
				Paths.get(IotJobUtils.getArguments().getNotNull("certificateFile",
						PropertyUtil.getConfig("certificateFile"))),
				Paths.get(IotJobUtils.getArguments()
						.getNotNull("certificateFile", PropertyUtil.getConfig("certificateFile"))
						.replaceAll(".crt", "_old.crt")),StandardCopyOption.REPLACE_EXISTING);
		Files.write(Paths.get(IotJobUtils.getArguments().getNotNull("certificateFile",
				PropertyUtil.getConfig("certificateFile"))), certificatePem.getBytes());
		System.out.println("Reconnecting IOT CLient with new certificate");
		try {
			IotJobUtils.reloadClient();
		} catch (AWSIotException e) {
			// TODO Auto-generated catch block
			System.err.println("Error: Reconnecting IOT CLient with new certificate");
		}
	}

	public static void downloadFromUrl(String downloadUrl, String outputFileName) throws IOException {
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
