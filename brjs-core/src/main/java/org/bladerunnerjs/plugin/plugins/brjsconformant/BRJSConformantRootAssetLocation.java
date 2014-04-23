package org.bladerunnerjs.plugin.plugins.brjsconformant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.aliasing.NamespaceException;
import org.bladerunnerjs.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetFilter;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.InstantiatedBRJSNode;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.plugin.AssetPlugin;
import org.bladerunnerjs.utility.JsStyleUtility;

public class BRJSConformantRootAssetLocation extends InstantiatedBRJSNode implements AssetLocation {
	private final List<LinkedAsset> emptyLinkedAssetList = new ArrayList<>();
	private final List<Asset> emptyAssetList = new ArrayList<>();
	private final List<AssetLocation> emptyAssetLocationList = new ArrayList<>();
	private AliasDefinitionsFile aliasDefinitionsFile;
	
	private final MemoizedValue<String> jsStyle = new MemoizedValue<>("AssetLocation.jsStyle", root(), dir());
	
	public BRJSConformantRootAssetLocation(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
	}
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException {
		// do nothing
	}
	
	@Override
	public String jsStyle() {
		return jsStyle.value(() -> {
			return JsStyleUtility.getJsStyle(dir());
		});
	}
	
	@Override
	public String requirePrefix() throws RequirePathException {
		// TODO: integrate the code in BRLib
		return ((JsLib) assetContainer()).getName();
	}
	
	@Override
	public String namespace() throws RequirePathException {
		// TODO: integrate the code in BRLib
		return requirePrefix().replace("/", ".");
	}
	
	@Override
	public void assertIdentifierCorrectlyNamespaced(String identifier) throws NamespaceException, RequirePathException {
		throw new RuntimeException("BRJSConformantRootAssetLocation.assertIdentifierCorrectlyNamespaced() should never be invoked");
	}
	
	@Override
	public SourceModule sourceModule(String requirePath) throws RequirePathException {
		throw new RuntimeException("BRJSConformantRootAssetLocation.sourceModule() should never be invoked");
	}
	
	@Override
	public AliasDefinitionsFile aliasDefinitionsFile() {
		if(aliasDefinitionsFile == null) {
			aliasDefinitionsFile = new AliasDefinitionsFile(assetContainer(), dir(), "aliasDefinitions.xml");
		}
		
		return aliasDefinitionsFile;
	}
	
	@Override
	public List<LinkedAsset> seedResources() {
		return emptyLinkedAssetList;
	}
	
	@Override
	public List<LinkedAsset> seedResources(String fileExtension) {
		return emptyLinkedAssetList;
	}
	
	@Override
	public List<Asset> bundleResources(String fileExtension) {
		return emptyAssetList;
	}
	
	@Override
	public List<Asset> bundleResources(AssetPlugin assetProducer) {
		return emptyAssetList;
	}
	
	@Override
	public AssetContainer assetContainer() {
		return (AssetContainer) parentNode();
	}
	
	@Override
	public List<AssetLocation> dependentAssetLocations() {
		return emptyAssetLocationList ;
	}
	
	@Override
	public <A extends Asset> A obtainAsset(Class<? extends A> assetClass, File dir, String assetName) throws AssetFileInstantationException {
		throw new RuntimeException("BRJSConformantRootAssetLocation.obtainAsset() should never be invoked");
	}
	
	@Override
	public <A extends Asset> List<A> obtainMatchingAssets(AssetFilter assetFilter, Class<A> assetClass, Class<? extends A> instantiateAssetClass) throws AssetFileInstantationException {
		return new ArrayList<A>();
	}
}
