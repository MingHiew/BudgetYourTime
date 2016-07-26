/**
 * Copyright 2016 Alex Yanchenko
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.droidparts.test.testcase.rest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.droidparts.net.http.HTTPException;
import org.droidparts.net.http.HTTPResponse;
import org.droidparts.net.http.RESTClient2;
import org.droidparts.net.http.UserAgent;
import org.droidparts.net.http.worker.HTTPWorker;
import org.droidparts.net.http.worker.HttpClientWorker;
import org.droidparts.net.http.worker.HttpURLConnectionWorker;

import android.test.AndroidTestCase;

public class RESTClientPostMultipart extends AndroidTestCase {

	// Simple server for testing POST requests. Supports multipart/form-data
	// file uploads.
	private static final String URL = "http://posttestserver.com/post.php?dump";
	private static final String FILE_NAME = "test";
	private static final String FILE_BODY = "Test POST multipart file";
	private static final String CONTENT_TYPE = "text/plain";

	public void testPostMultipartURLConnection() throws Exception {
		HTTPWorker worker = new HttpURLConnectionWorker(getContext(), UserAgent.getDefault());
		testPostMultipartFile(worker);
		testPostMultipartBytes(worker);
	}

	public void testPostMultipartHttpClient() throws Exception {
		HTTPWorker worker = new HttpClientWorker(UserAgent.getDefault());
		testPostMultipartFile(worker);
		testPostMultipartBytes(worker);
	}

	//

	private void testPostMultipartFile(HTTPWorker worker) throws Exception {
		RESTClient2 client = new RESTClient2(getContext(), worker);
		File file = writeTestFile(FILE_BODY);
		// Without content type
		HTTPResponse resp = client.postMultipart(URL, FILE_NAME, file);
		assertPostMultipartResponse(resp);
		// With content type
		resp = client.postMultipart(URL, FILE_NAME, CONTENT_TYPE, file);
		assertPostMultipartResponse(resp);
	}

	private void testPostMultipartBytes(HTTPWorker worker) throws Exception {
		RESTClient2 client = new RESTClient2(getContext(), worker);
		byte[] fileBytes = FILE_BODY.getBytes();
		// Without content type
		HTTPResponse resp = client.postMultipart(URL, FILE_NAME, FILE_NAME, fileBytes);
		assertPostMultipartResponse(resp);
		// With content type
		resp = client.postMultipart(URL, FILE_NAME, CONTENT_TYPE, FILE_NAME, fileBytes);
		assertPostMultipartResponse(resp);
	}

	private File writeTestFile(String data) throws IOException {
		File file = new File(getContext().getCacheDir(), "test.txt");
		FileWriter fw = new FileWriter(file);
		fw.write(data);
		fw.close();
		return file;
	}

	private void assertPostMultipartResponse(HTTPResponse response) throws HTTPException {
		assertNotNull(response);
		String body = response.body;

		// Get uploaded file URL
		Pattern pattern = Pattern.compile("Uploaded File: (.+)");
		Matcher matcher = pattern.matcher(body);
		assertTrue(matcher.find());

		String fileUrl = matcher.group(1);
		RESTClient2 client = new RESTClient2(getContext());
		assertEquals(client.get(fileUrl).body, FILE_BODY);
	}
}
