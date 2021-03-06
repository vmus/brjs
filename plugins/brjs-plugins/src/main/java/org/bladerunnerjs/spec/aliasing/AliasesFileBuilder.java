package org.bladerunnerjs.spec.aliasing;

import java.io.IOException;
import java.util.Arrays;

import org.bladerunnerjs.api.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.api.spec.engine.BuilderChainer;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.api.spec.engine.SpecTestBuilder;
import org.bladerunnerjs.plugin.bundlers.aliasing.AliasOverride;
import org.bladerunnerjs.plugin.bundlers.aliasing.AliasesFile;

public class AliasesFileBuilder implements SpecTestBuilder {
	private AliasesFile aliasesFile;
	private BuilderChainer builderChainer;
	private SpecTest specTest;
	
	public AliasesFileBuilder(SpecTest specTest, AliasesFile aliasesFile) {
		this.aliasesFile = aliasesFile;
		builderChainer = new BuilderChainer(specTest);
		this.specTest = specTest;
	}
	
	public BuilderChainer exists() throws Exception {
		writeAliasesFile();
		
		return builderChainer;
	}
	
	public BuilderChainer usesScenario(String scenario) throws Exception {
		aliasesFile.setScenarioName(scenario);
		writeAliasesFile();
		
		return builderChainer;
	}
	
	public BuilderChainer usesGroups(String... groups) throws Exception {
		aliasesFile.setGroupNames(Arrays.asList(groups));
		writeAliasesFile();
		
		return builderChainer;
	}
	
	public BuilderChainer hasAlias(String aliasName, String classRef) throws Exception {
		aliasesFile.addAlias(new AliasOverride(aliasName, classRef));
		writeAliasesFile();
		
		return builderChainer;
	}
	
	public void writeAliasesFile() throws ContentFileProcessingException, IOException {
		aliasesFile.write();
		specTest.brjs.getFileModificationRegistry().incrementFileVersion(aliasesFile.getUnderlyingFile());
	}

	public String getUnderlyingFilePath()
	{
		return aliasesFile.getUnderlyingFile().getPath();
	}
}
