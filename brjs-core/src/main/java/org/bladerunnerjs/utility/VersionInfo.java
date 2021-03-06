package org.bladerunnerjs.utility;

import java.io.File;
import java.io.IOException;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ConfigException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class VersionInfo
{
	private final BRJS brjs;
	private final EncodedFileUtil fileUtil;
	
	public VersionInfo(BRJS brjs)
	{
		try {
			this.brjs = brjs;
			fileUtil = new EncodedFileUtil(brjs, brjs.bladerunnerConf().getDefaultFileCharacterEncoding());
		}
		catch(ConfigException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getVersionNumber()
	{
		return getValueFromVersionFile("Version");
	}
	
	public String getBuildDate()
	{
		return getValueFromVersionFile("BuildDate");
	}
	
	@Override
	public String toString()
	{
		return BRJS.PRODUCT_NAME + " version: " + getVersionNumber() + ", built: " + getBuildDate();
	}
	
	
	private MemoizedFile getFile()
	{
		return brjs.file("sdk/version.txt");
	}
	
	private String getValueFromVersionFile(String key)
	{
		File versionFile = getFile();
		
		if (versionFile.exists())
		{
			String contents;
			try
			{
				contents = fileUtil.readFileToString(versionFile);
			}
			catch (IOException e)
			{
				return "";
			}
			JsonObject json = new JsonParser().parse(contents).getAsJsonObject();
			String value = json.get(key).toString(); 
			if (value.startsWith("\""))
			{
				value = value.replaceFirst("\"", "");
			}
			if (value.endsWith("\""))
			{
				value = value.substring(0, value.length() -1);
			}
			return value;
		}
		return "";
	}
	
}
