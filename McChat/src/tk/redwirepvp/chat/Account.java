package tk.redwirepvp.chat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class Account {
	private String username;
	public String sessionName;
	private String sessionId;
	private String password;

	public Account(String username, String password) {
		this.username = username;
		this.password = password;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the sessionId
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * @param sessionId
	 *            the sessionId to set
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	public void login() {
		String result;
		String parameters;
		try {
			parameters = (new StringBuilder("user="))
					.append(URLEncoder.encode(username, "UTF-8"))
					.append("&password=")
					.append(URLEncoder.encode(password, "UTF-8"))
					.append("&version=").append(13).toString();

			result = executePost("https://login.minecraft.net/", parameters);
			if (result == null) {
				System.err.println("Can't connect to minecraft.net");
			} else if (!result.contains(":")) {
				if (result.trim().equals("Bad login"))
					System.err.println("Login failed");
				else if (result.trim().equals("Old version")) {
					System.err.println("Outdated version");
				} else {
					System.out.println(result);
				}
			} else {
				this.sessionName = result.split(":")[2];
				setSessionId(result.split(":")[3]);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public static String executePost(String targetURL, String urlParameters) {
		HttpsURLConnection connection = null;
		try {
			URL url = new URL(targetURL);
			connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.setRequestProperty(
					"Content-Length",
					(new StringBuilder()).append(
							Integer.toString(urlParameters.getBytes().length))
							.toString());
			connection.setRequestProperty("Content-Language", "en-US");
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.connect();
			try (DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream())) {
				wr.writeBytes(urlParameters);
				wr.flush();
			}
			try (java.io.InputStream is = connection.getInputStream();
					BufferedReader rd = new BufferedReader(
							new InputStreamReader(is))) {
				StringBuffer response = new StringBuffer();
				String line;
				while ((line = rd.readLine()) != null) {
					response.append(line);
					response.append('\r');
				}
				return response.toString();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return null;
	}
}