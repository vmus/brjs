package org.bladerunnerjs.spec.service;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.SdkJsLib;
import org.bladerunnerjs.spec.aliasing.AliasDefinitionsFileBuilder;
import org.bladerunnerjs.spec.aliasing.AliasesFileBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.bladerunnerjs.plugin.bundlers.aliasing.AliasingUtility.*;


public class ServiceDataTest extends SpecTest
{

	private App app;
	private Aspect aspect;
	private SdkJsLib lib;
	private SdkJsLib servicesLib;
	private AliasesFileBuilder aspectAliasesFile;
	private StringBuffer response = new StringBuffer();
	private AliasDefinitionsFileBuilder aspectAliasDefinitionsFile;
	private AliasDefinitionsFileBuilder libAliasDefinitionsFile;

	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs)
			.automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()			
			.and(brjs).hasBeenCreated();
		app = brjs.app("app");
		aspect = app.defaultAspect();
		lib = brjs.sdkLib("lib");
		
		aspectAliasesFile = new AliasesFileBuilder(this, aliasesFile(aspect));
		aspectAliasDefinitionsFile = new AliasDefinitionsFileBuilder(this, aliasDefinitionsFile(aspect, "resources"));
		libAliasDefinitionsFile = new AliasDefinitionsFileBuilder(this, aliasDefinitionsFile(lib, "resources"));
		
		servicesLib = brjs.sdkLib("ServicesLib");
		given(servicesLib).containsFileWithContents("br-lib.conf", "requirePrefix: br")
			.and(servicesLib).hasClasses("br/AliasRegistry", "br/ServiceRegistry")
			.and(servicesLib).hasClass("br/UnknownClass");
	}
	
	@Test
	public void serviceDataIsEmptyIfNoServicesUsed() throws Exception {
		given(aspect).indexPageRequires("appns/App", "service!$data")
			.and(aspect).hasClass("App")
			.and(aspect).hasClass("ServiceClass")
			.and(aspectAliasesFile).hasAlias("some-service", "appns/ServiceClass");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsLines(
			"module.exports = { }"
		);
	}
	
	@Test
	public void serviceDataIsEmptyIfNoServiceAreDefinedViaAliasesAreUsed() throws Exception {
		given(aspect).indexPageRequires("appns/App", "service!$data")
			.and(aspect).classRequires("App", "service!some-service");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsLines(
			"module.exports = { }"
		);
	}
	
	@Test
	public void serviceDataIsEmptyIfNoServiceAreDefinedViaAliasesWithAnImplementation() throws Exception {
		given(aspect).indexPageRequires("appns/App", "service!$data")
			.and(aspect).classRequires("App", "service!some-service")
			.and(aspect).hasClass("ServiceInterface")
			.and(aspectAliasDefinitionsFile).hasAlias("some-service", null, "appns/ServiceInterface");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsLines(
			"module.exports = { }"
		);
	}
	
	@Test
	public void serviceDataListsASingleUsedService_whenServiceDefinedViaAspectAliases() throws Exception {
		given(aspect).indexPageRequires("appns/App", "service!$data")
			.and(aspect).classRequires("App", "service!some-service")
			.and(aspect).hasClass("ServiceClass")
			.and(aspectAliasesFile).hasAlias("some-service", "appns/ServiceClass");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsLines(
			"module.exports = {",
			"	\"service!some-service\": {",
			"		\"requirePath\": \"appns/ServiceClass\""
		);
	}
	
	@Test
	public void serviceDataListsASingleUsedService_whenServiceDefinedViaAspectAliasDefinitions() throws Exception {
		given(aspect).indexPageRequires("appns/App", "service!$data")
			.and(aspect).classRequires("App", "service!some-service")
			.and(aspect).hasClass("ServiceClass")
			.and(aspectAliasDefinitionsFile).hasAlias("some-service", "appns/ServiceClass");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsLines(
			"module.exports = {",
			"	\"service!some-service\": {",
			"		\"requirePath\": \"appns/ServiceClass\""
		);
	}
	
	@Test
	public void serviceDataListsAllServices_whenServiceDefinedViaAspectAliases() throws Exception {
		given(aspect).indexPageRequires("appns/App", "service!$data")
			.and(aspect).classFileHasContent("App", "require('service!some-service'); require('service!another-service');")
			.and(aspect).hasClass("ServiceClass")
			.and(aspect).hasClass("AnotherServiceClass")
			.and(aspectAliasesFile).hasAlias("some-service", "appns/ServiceClass")
    		.and(aspectAliasesFile).hasAlias("another-service", "appns/AnotherServiceClass");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsLines(
			"module.exports = {",
			"	\"service!some-service\": {",
			"		\"requirePath\": \"appns/ServiceClass\",",
			"		\"dependencies\": []",
			"	},",
			"	\"service!another-service\": {",
			"		\"requirePath\": \"appns/AnotherServiceClass\",",
			"		\"dependencies\": []",
			"	}",
			"}"
		);
	}
	
	@Test
	public void serviceDataListsAllServices_whenServiceDefinedViaAspectAliasDefinitions() throws Exception {
		given(aspect).indexPageRequires("appns/App", "service!$data")
			.and(aspect).classFileHasContent("App", "require('service!some-service'); require('service!another-service');")
			.and(aspect).hasClass("ServiceClass")
			.and(aspect).hasClass("AnotherServiceClass")
			.and(aspectAliasDefinitionsFile).hasAlias("some-service", "appns/ServiceClass")
    		.and(aspectAliasDefinitionsFile).hasAlias("another-service", "appns/AnotherServiceClass");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsLines(
			"module.exports = {",
			"	\"service!some-service\": {",
			"		\"requirePath\": \"appns/ServiceClass\",",
			"		\"dependencies\": []",
			"	},",
			"	\"service!another-service\": {",
			"		\"requirePath\": \"appns/AnotherServiceClass\",",
			"		\"dependencies\": []",
			"	}",
			"}"
		);
	}
	
	@Test
	public void serviceDataListsAServicesServiceDependencies_whenServiceDefinedViaAspectAliases() throws Exception {
		given(aspect).indexPageRequires("appns/App", "service!$data")
			.and(aspect).classRequires("App", "service!some-service")
			.and(aspect).classFileHasContent("ServiceClass", "require('service!dependent-service-1'); require('service!dependent-service-2'); require('service!dependent-service-3');")
			.and(aspect).hasClass("DependentServiceClass1")
			.and(aspect).hasClass("DependentServiceClass2")
			.and(aspect).hasClass("DependentServiceClass3")
			.and(aspectAliasesFile).hasAlias("some-service", "appns/ServiceClass")
			.and(aspectAliasesFile).hasAlias("dependent-service-1", "appns/DependentServiceClass1")
			.and(aspectAliasesFile).hasAlias("dependent-service-2", "appns/DependentServiceClass2")
			.and(aspectAliasesFile).hasAlias("dependent-service-3", "appns/DependentServiceClass3");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsLines(
			"module.exports = {",
			"	\"service!some-service\": {",
			"		\"requirePath\": \"appns/ServiceClass\",",
			"		\"dependencies\": [",
			"			\"dependent-service-1\",",
			"			\"dependent-service-2\",",
			"			\"dependent-service-3\"",
			"		]",
			"	},",
			"	\"service!dependent-service-1\": {",
			"		\"requirePath\": \"appns/DependentServiceClass1\",",
			"		\"dependencies\": []",
			"	},",
			"	\"service!dependent-service-2\": {",
			"		\"requirePath\": \"appns/DependentServiceClass2\",",
			"		\"dependencies\": []",
			"	},",
			"	\"service!dependent-service-3\": {",
			"		\"requirePath\": \"appns/DependentServiceClass3\",",
			"		\"dependencies\": []",
			"	}",
			"}"
		);
	}
	
	@Test
	public void serviceDataListsAServicesServiceDependencies_whenServiceDefinedViaAspectAliasDefinitions() throws Exception {
		given(aspect).indexPageRequires("appns/App", "service!$data")
			.and(aspect).classRequires("App", "service!some-service")
			.and(aspect).classFileHasContent("ServiceClass", "require('service!dependent-service-1'); require('service!dependent-service-2'); require('service!dependent-service-3');")
			.and(aspect).hasClass("DependentServiceClass1")
			.and(aspect).hasClass("DependentServiceClass2")
			.and(aspect).hasClass("DependentServiceClass3")
			.and(aspectAliasDefinitionsFile).hasAlias("some-service", "appns/ServiceClass")
			.and(aspectAliasDefinitionsFile).hasAlias("dependent-service-1", "appns/DependentServiceClass1")
			.and(aspectAliasDefinitionsFile).hasAlias("dependent-service-2", "appns/DependentServiceClass2")
			.and(aspectAliasDefinitionsFile).hasAlias("dependent-service-3", "appns/DependentServiceClass3");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsLines(
			"module.exports = {",
			"	\"service!some-service\": {",
			"		\"requirePath\": \"appns/ServiceClass\",",
			"		\"dependencies\": [",
			"			\"dependent-service-1\",",
			"			\"dependent-service-2\",",
			"			\"dependent-service-3\"",
			"		]",
			"	},",
			"	\"service!dependent-service-1\": {",
			"		\"requirePath\": \"appns/DependentServiceClass1\",",
			"		\"dependencies\": []",
			"	},",
			"	\"service!dependent-service-2\": {",
			"		\"requirePath\": \"appns/DependentServiceClass2\",",
			"		\"dependencies\": []",
			"	},",
			"	\"service!dependent-service-3\": {",
			"		\"requirePath\": \"appns/DependentServiceClass3\",",
			"		\"dependencies\": []",
			"	}",
			"}"
		);
	}

	@Test
	public void serviceDataListsASingleUsedService_whenServiceDefinedViaLibraryAliasDefinitions() throws Exception {
		given(aspect).indexPageRequires("appns/App", "service!$data")
			.and(aspect).classRequires("App", "service!lib.service")
			.and(lib).containsFileWithContents("br-lib.conf", "requirePrefix: lib")
			.and(lib).hasClass("ServiceClass")
			.and(libAliasDefinitionsFile).hasAlias("lib.service", "lib/ServiceClass");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsLines(
			"module.exports = {",
			"	\"service!lib.service\": {",
			"		\"requirePath\": \"lib/ServiceClass\""
		);
	}
	
	@Test
	public void serviceDataListsServicesUsedViaNamespacedJSCode() throws Exception {
		given(aspect).indexPageRequires("appns/App", "service!$data")
			.and(aspect).hasNamespacedJsPackageStyle()
    		.and(aspect).classFileHasContent("App", "br.ServiceRegistry.getService('some-service');")
    		.and(aspect).hasClass("ServiceClass")
    		.and(aspectAliasesFile).hasAlias("some-service", "appns/ServiceClass");
    	when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
    	then(response).containsLines(
    		"module.exports = {",
    		"	\"service!some-service\": {",
    		"		\"requirePath\": \"appns/ServiceClass\""
    	);
	}
	
	@Test
	public void serviceDataListsServicesRequiredViaIndexPage() throws Exception {
		given(aspect).indexPageHasContent("require('service!$data'); require('service!some-service');")
    		.and(aspect).hasClass("ServiceClass")
    		.and(aspectAliasesFile).hasAlias("some-service", "appns/ServiceClass");
    	when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
    	then(response).containsLines(
    		"module.exports = {",
    		"	\"service!some-service\": {",
    		"		\"requirePath\": \"appns/ServiceClass\""
    	);
	}
	
	@Test
	public void serviceDataListsServicesUsedViaIndexPage() throws Exception {
		given(aspect).indexPageHasContent("require('service!$data'); br.ServiceRegistry.getService('some-service');")
    		.and(aspect).hasClass("ServiceClass")
    		.and(aspectAliasesFile).hasAlias("some-service", "appns/ServiceClass");
    	when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
    	then(response).containsLines(
    		"module.exports = {",
    		"	\"service!some-service\": {",
    		"		\"requirePath\": \"appns/ServiceClass\""
    	);
	}
	
	
}
