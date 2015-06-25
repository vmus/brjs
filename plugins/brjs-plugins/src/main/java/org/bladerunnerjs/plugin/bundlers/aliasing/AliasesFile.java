package org.bladerunnerjs.plugin.bundlers.aliasing;

import java.util.List;

import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.model.engine.Node;

public class AliasesFile {
	private final MemoizedFile file;
	private final PersistentAliasesData persistentAliasesData;
	
	public AliasesFile(Node node, BRJS brjs) {
		this.file = node.file("resources/aliases.xml");
		persistentAliasesData = new PersistentAliasesData(brjs, file);
	}
	
	public MemoizedFile getUnderlyingFile() {
		return file;
	}
	
	public String scenarioName() throws ContentFileProcessingException {
		return persistentAliasesData.getData().scenario;
	}
	
	public void setScenarioName(String scenarioName) throws ContentFileProcessingException {
		persistentAliasesData.getData().scenario = scenarioName;
	}
	
	public List<String> groupNames() throws ContentFileProcessingException {
		return persistentAliasesData.getData().groupNames;
	}
	
	public void setGroupNames(List<String> groupNames) throws ContentFileProcessingException {
		persistentAliasesData.getData().groupNames = groupNames;
	}
	
	public List<AliasOverride> aliasOverrides() throws ContentFileProcessingException {
		return persistentAliasesData.getData().aliasOverrides;
	}
	
	public void addAlias(AliasOverride aliasOverride) throws ContentFileProcessingException {
		List<AliasOverride> aliasOverrides = aliasOverrides();
		aliasOverrides.add(aliasOverride);
		persistentAliasesData.getData().aliasOverrides = aliasOverrides;
	}
	
	public void write() throws ContentFileProcessingException {
		persistentAliasesData.writeData();
	}
	
}
