package org.eclipse.update.core;

/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
 
import java.net.URL;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
 
 /**
  * A content consumer manages the storage or archives, plugins and
  * feature inside an <code>IFeature</code> or an <code> ISite</code>
  * 
  * A FeatureContentConsumer has a <i>parent</i> which is the entry (IFeature, IPluginEntry
  * or INonPluginEntry) that is going to be used for storage.
  * 
  * Only a FeatureContentConsumer with an IFeature parent can open sub-FeatureContentConsumer.
  */
 
public interface ISiteContentConsumer {

	/**
	 * opens a Non plugin Entry for storage
	 * @return the new FeatureContentConsumer for this <code>INonPluginEntry</code>
	 * @throws CoreException if the opens is done on a FeatureContentConsumer parent other than an IFeature.
	 * @since 2.0 
	 */

	IFeatureContentConsumer opens(INonPluginEntry nonPluginEntry) throws CoreException;

	/**
	 * opens a Non plugin Entry for storage
	 * @return the new FeatureContentConsumer for this <code>IPluginEntry</code>
	 * @throws CoreException if the opens is done on a FeatureContentConsumer parent other than an IFeature.
	 * @since 2.0 
	 */

	IFeatureContentConsumer opens(IPluginEntry pluginEntry) throws CoreException;
	
	/**
	 * Stores a content reference into the FeatureContentConsumer
	 * @param ContentReference the content reference to store
	 * @param IProgressMonitor the progress monitor
	 * @throws CoreException if an error occurs storing the content reference
	 * @since 2.0 
	 */

	void store(ContentReference contentReference, IProgressMonitor monitor) throws CoreException;
	
	/**
	 * removes a content reference into the FeatureContentConsumer
	 * @param ContentReference the content reference to remove
	 * @param IProgressMonitor the progress monitor
	 * @throws CoreException if an error occurs removing the content reference
	 * @since 2.0 
	 */

	void remove(ContentReference contentReference, IProgressMonitor monitor) throws CoreException;	
	
	/**
	 * closes the opened FeatureContentConsumer
	 * @since 2.0 
	 */

	void close();	
	
	/**
	 * sets the Site for this content consumer
	 * @param the ISite
	 * @since 2.0
	 */
	void setSite(ISite Site);
			
}


