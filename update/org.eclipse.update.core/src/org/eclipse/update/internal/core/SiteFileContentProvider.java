package org.eclipse.update.internal.core;
/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.model.*;
import org.eclipse.update.core.*;
import org.eclipse.update.core.model.InvalidSiteTypeException;
import org.eclipse.update.internal.core.obsolete.FeaturePackaged;

/**
 * Site on the File System
 */
public class SiteFileContentProvider extends SiteContentProvider {
	
	private String path;
	
	public static final String INSTALL_FEATURE_PATH = "install/features/";	
	public static final String SITE_TYPE = "org.eclipse.update.core.file";	

	/**
	 * Constructor for FileSite
	 */
	public SiteFileContentProvider(URL url) throws CoreException, InvalidSiteTypeException {
		super(url);
	}

	
	
	/**
 	 * move into contentSelector, comment to provider and consumer (SiteFile)
 	 */
	private String getFeaturePath(VersionedIdentifier featureIdentifier) {
		String path = UpdateManagerUtils.getPath(getURL());
		String featurePath = path + INSTALL_FEATURE_PATH + featureIdentifier.toString();
		return featurePath;
	}

	/**
	 * We do not need to optimize the download
	 * As the archives are already available on the file system
	 */
	public boolean optimize() {
		return false;
	}
	
	

	/**
	 * Method parseSite.
	 */
	protected void parseSite() throws CoreException {

		String path = UpdateManagerUtils.getPath(getURL());
		String pluginPath = path + Site.DEFAULT_PLUGIN_PATH;
		String fragmentPath = path + Site.DEFAULT_FRAGMENT_PATH;
		PluginRegistryModel model = new PluginRegistryModel();		

		//PACKAGED
		parsePackagedFeature(); // in case it contains JAR files

		parsePackagedPlugins(pluginPath);
		
		parsePackagedPlugins(fragmentPath);		

		// EXECUTABLE	
		parseExecutableFeature();
		
		model = parsePlugins(pluginPath);
		addParsedPlugins(model.getPlugins());

		// FIXME: fragments
		model = parsePlugins(fragmentPath);
		addParsedPlugins(model.getFragments());

	}
	
	/**
	 * Method parseFeature.
	 * @throws CoreException
	 */
	private void parseExecutableFeature() throws CoreException {
		
		String path = UpdateManagerUtils.getPath(getURL());
		String featurePath = path + INSTALL_FEATURE_PATH;
		
		
		File featureDir = new File(featurePath);
		if (featureDir.exists()) {
			String[] dir;
			FeatureReference featureRef;
			URL featureURL;
			String newFilePath = null;
		
			try {
				// handle teh installed featuresConfigured under featuresConfigured subdirectory
				dir = featureDir.list();
				for (int index = 0; index < dir.length; index++) {
					// teh URL must ends with '/' for teh bundle to be resolved
					newFilePath = featurePath + dir[index] + "/";
					featureURL = new URL("file", null, newFilePath);
					featureRef = new FeatureReference();
					featureRef.setSite(site);
					featureRef.setURL(featureURL);
				//	addFeatureReference(featureRef);
				}
			} catch (MalformedURLException e) {
				String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
				IStatus status = new Status(IStatus.ERROR, id, IStatus.OK, "Error creating file URL for:" + newFilePath, e);
				throw new CoreException(status);
			}
		}
	}
	
		/**
	 * Method parseFeature.
	 * @throws CoreException
	 */
	private void parsePackagedFeature() throws CoreException {
		
		String path = UpdateManagerUtils.getPath(getURL());
		String featurePath = path + Site.DEFAULT_FEATURE_PATH;
		
		// FEATURES
		File featureDir = new File(featurePath);
		if (featureDir.exists()) {
			String[] dir;
			FeatureReference featureRef;
			URL featureURL;
			String newFilePath = null;
		
			try {
				// handle teh installed featuresConfigured under featuresConfigured subdirectory
				dir = featureDir.list(FeaturePackaged.filter);
				for (int index = 0; index < dir.length; index++) {
					newFilePath = featurePath + dir[index];
					featureURL = new URL("file", null, newFilePath);
					featureRef = new FeatureReference();
					featureRef.setSite(site);
					featureRef.setURL(featureURL);
				//	addFeatureReference(featureRef);
				}
			} catch (MalformedURLException e) {
				String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
				IStatus status = new Status(IStatus.ERROR, id, IStatus.OK, "Error creating file URL for:" + newFilePath, e);
				throw new CoreException(status);
			}
		}
	}
	
	/**
	 * Method parsePlugins.
	 * 
	 * look into each plugin/fragment directory, crack the plugin.xml open (or fragment.xml ???)
	 * get id and version, calculate URL...	
	 * 
	 * @return PluginRegistryModel
	 * @throws CoreException
	 */
	private PluginRegistryModel parsePlugins(String path) throws CoreException {
		PluginRegistryModel model;
		String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();		
		MultiStatus parsingStatus = new MultiStatus(id, IStatus.WARNING, "Error parsing plugin.xml in " + path, new Exception());
		Factory factory = new Factory(parsingStatus);
		
		try {
			URL pluginURL = new URL("file", null, path);
			model = Platform.parsePlugins(new URL[] { pluginURL }, factory);
		} catch (MalformedURLException e) {
			IStatus status = new Status(IStatus.ERROR, id, IStatus.OK, "Error creating file URL for :" + path, e);
			throw new CoreException(status);
		}
				
		if (factory.getStatus().getChildren().length != 0) {
			throw new CoreException(parsingStatus);
		}
		
		return model;
	}

	/**
	 * Method addParsedPlugins.
	 * @param model
	 * @throws CoreException
	 */
	private void addParsedPlugins(PluginModel[] plugins) throws CoreException {
		
		String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
		
		// tranform each Plugin and Fragment in an Archive fro the Site
		String location = null;
		try {
			if (plugins.length > 0) {
				URLEntry info;
				for (int index = 0; index < plugins.length; index++) {
					String pluginID = new VersionedIdentifier(plugins[index].getId(), plugins[index].getVersion()).toString() + FeaturePackaged.JAR_EXTENSION;
					location = plugins[index].getLocation();
					URL url = new URL(location);
					info = new URLEntry();
					info.setAnnotation(pluginID);
					info.resolve(url,null);
			//		this.addArchive(info);
				}
			}
		} catch (MalformedURLException e) {
			IStatus status = new Status(IStatus.ERROR, id, IStatus.OK, "Error creating file URL for plugin:" + location, e);
			throw new CoreException(status);
		}
	}

	/**
	 * 
	 */
	private void parsePackagedPlugins(String pluginPath) throws CoreException { 
			
		File pluginDir = new File(pluginPath);
		File file = null;
		ZipFile zipFile = null;
		ZipEntry entry = null;
		String[] dir;	
		URL pluginURL=null;
		
		String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
		PluginRegistryModel registryModel;
		MultiStatus parsingStatus = new MultiStatus(id, IStatus.WARNING, "Error parsing plugin.xml", new Exception());
		Factory factory = new Factory(parsingStatus);
		
		String tempDir = System.getProperty("java.io.tmpdir");
		if (!tempDir.endsWith(File.separator)) tempDir += File.separator;
					
		try {
		if (pluginDir.exists()) {
			dir = pluginDir.list(FeaturePackaged.filter);
			for (int i = 0; i < dir.length; i++) {
				file = new File(pluginPath,dir[i]);
				zipFile = new ZipFile(file);
				entry = zipFile.getEntry("plugin.xml");
				if (entry==null) entry = zipFile.getEntry("fragment.xml"); //FIXME: fragments
				if (entry!=null){
					pluginURL=UpdateManagerUtils.copyToLocal(zipFile.getInputStream(entry),tempDir+entry.getName(),null);
					registryModel = Platform.parsePlugins(new URL[] { pluginURL }, factory);					
					if (registryModel!=null) {
						PluginModel[] models = null;
						if (entry.getName().equals("plugin.xml")){
							models = registryModel.getPlugins();
						} else {
							models = registryModel.getFragments();
						}
						for (int index = 0; index < models.length; index++) {
							// the id is plugins\<pluginid>_<ver> as per the specs
							String pluginID = Site.DEFAULT_PLUGIN_PATH+new VersionedIdentifier(models[index].getId(), models[index].getVersion()).toString() + FeaturePackaged.JAR_EXTENSION;
							URL url = new URL("file",null,file.getAbsolutePath());
							URLEntry info = new URLEntry();
							info.setAnnotation(pluginID);
							info.resolve(url,null);
		//					this.addArchive(info);
						}
					}
				}
				zipFile.close();		
			}	
		}
		}
		//catch (MalformedURLException m){throw new CoreException(new Status(IStatus.ERROR, id, IStatus.OK, "Error accessing plugin.xml in file :" + file, m));}		
		//catch (ZipException z){throw new CoreException(new Status(IStatus.ERROR, id, IStatus.OK, "Error accessing plugin.xml in file :" + file, z));}		
		catch (IOException e){ throw new CoreException(new Status(IStatus.ERROR, id, IStatus.OK, "Error accessing plugin.xml in file :" + file, e));}
		 finally {try {zipFile.close();} catch (Exception e) {}}
		 
	}

	
	/*
	 * @see ISite#getDefaultExecutableFeatureType()
	 */
	public String getDefaultExecutableFeatureType() {
		String pluginID = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier()+".";
		return pluginID+IFeatureFactory.EXECUTABLE_FEATURE_TYPE;
	}

	/*
	 * @see ISite#getDefaultInstallableFeatureType()
	 */
	public String getDefaultInstallableFeatureType() {
		String pluginID = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier()+".";
		return pluginID+IFeatureFactory.INSTALLABLE_FEATURE_TYPE;
	}

	/*
	 * @see ISiteContentProvider#getSiteManifestReference()
	 */
	public ContentReference getSiteManifestReference() throws MalformedURLException {
		return null;
	}

	/*
	 * @see ISiteContentProvider#getArchivesReferences(String)
	 */
	public ContentReference getArchivesReferences(String archiveID) {
		return null;
	}

	/*
	 * @see ISiteContentProvider#getFeatureArchivesReferences(IFeature)
	 */
	public ContentReference getFeatureArchivesReferences(IFeature feature) {
		return null;
	}

}