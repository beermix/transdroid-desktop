package org.transdroid.desktop.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.transdroid.daemon.DaemonSettings;
import org.transdroid.daemon.util.HttpHelper;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

/**
 * General object to store application settings and to load/save them
 * from/to file.
 * 
 * @author erickok
 */
public class AppSettings {

	private List<DaemonSettings> savedServers;
	
	public AppSettings() {
		this.savedServers = new ArrayList<DaemonSettings>();
	}
	
	/**
	 * Add the server settings to the list of saved servers. It can later
	 * be saved/loaded to/from a file.
	 * @param server The server settings to save
	 */
	public void saveServer(DaemonSettings server) {
		this.savedServers.add(server);
	}

	/**
	 * Remove a previously saved server from the settings
	 * @param idString The server settings identifier to remove
	 */
	public void removeServer(String idString) {
		DaemonSettings toRemove = null;
		for (DaemonSettings server : this.savedServers) {
			if (server.getIdString().equals(idString)) {
				toRemove = server;
				break;
			}
		}
		if (toRemove != null) {
			savedServers.remove(toRemove);
		}
	}
	
	/**
	 * Returns a list of saved servers. Do not use this list to save a new
	 * server settings object - use saveServer() instead.
	 * @return The server settings for the saved servers
	 */
	public List<DaemonSettings> getSavedServers() {
		return new ArrayList<DaemonSettings>(savedServers);
	}

	/**
	 * Saves all the settings to a local file
	 * @param file The file to write the JSON settings to
	 * @throws FileNotFoundException Thrown when the file can not be written to
	 * @throws JSONException Thrown when the settings can not be serialised
	 */
	public void saveToFile(File file) throws IOException, JSONException {
		if (file.exists()) {
			file.delete();
		}
		file.getParentFile().mkdirs();
		file.createNewFile();
		FileWriter writer = new FileWriter(file);
		writer.write(toJson().toString(2));
		writer.flush();
		writer.close();
	}

	/**
	 * Loads all the settings from a local file
	 * @param file The file with JSON settings
	 * @throws IOException Thrown when the file can not be read
	 * @throws JSONException Thrown when the file does not contain settings
	 */
	public void loadFromFile(File file) throws FileNotFoundException, JSONException {
		String raw = HttpHelper.ConvertStreamToString(new FileInputStream(file));
		fromJson(new JSONObject(raw));
	}

	private JSONObject toJson() throws JSONException {
		JSONObject all = new JSONObject();
		Gson gson = new Gson();
		all.put("servers", new JSONArray(gson.toJson(savedServers)));
		return all;
	}
	
	private void fromJson(JSONObject json) throws JsonParseException, JSONException {
		Gson gson = new Gson();
		savedServers = gson.fromJson(json.getJSONArray("servers").toString(), 
				new TypeToken<List<DaemonSettings>>(){}.getType());
	}

}
