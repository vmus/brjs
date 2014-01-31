package com.caplin.jstestdriver.plugin;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BladerunnerUri;

import com.caplin.cutlass.BRJSAccessor;

public class BRJSWritingResourceBundlerHandler extends WritingResourceBundlerHandler
{
	protected final String bundlerFileExtension;
	private final String brjsRequestPath;
	

	public BRJSWritingResourceBundlerHandler(String bundlerFileExtension, String brjsRequestPath, boolean serveOnly)
	{
		super(null, bundlerFileExtension, serveOnly);
		this.bundlerFileExtension = bundlerFileExtension;
		this.brjsRequestPath = brjsRequestPath;
	}
	
	@Override
	public List<File> getBundledFiles(File rootDir, File testDir, File bundlerFile)
	{
		createParentDirectory(bundlerFile);
		OutputStream outputStream = createBundleOutputStream(bundlerFile);
		
		BRJS brjs = null;
		try
		{
    		brjs = BRJSAccessor.root;
    	
    		App app = brjs.locateAncestorNodeOfClass(testDir, App.class);
    		if (app == null)
    		{
    			throw new RuntimeException("Unable to calculate App node for the test dir: " + testDir.getAbsolutePath());
    		}
    		
    		BladerunnerUri requestUri = new BladerunnerUri(brjs, testDir, brjsRequestPath);
    		app.handleLogicalRequest(requestUri, outputStream);
		}
		catch (Exception ex)
		{
			throw new RuntimeException("There was an error while bundling.", ex);
		}
		finally 
		{
			try
			{
				outputStream.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		/* TODO: this is a hack because when we create file infos files cannot be loaded properly in tests
		  			(see TODO in BundlerInjector */
		//return Arrays.asList(bundlerFile);
		return Arrays.asList(new File[0]);
	}
	
}
