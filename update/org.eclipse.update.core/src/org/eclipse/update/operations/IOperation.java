/*******************************************************************************
 *  Copyright (c) 2000, 2010 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.update.operations;

import java.lang.reflect.*;

import org.eclipse.core.runtime.*;

/**
 * Base update manager operation.
 * <p>
 * <b>Note:</b> This class/interface is part of an interim API that is still under development and expected to
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
 * (repeatedly) as the API evolves.
 * </p>
 * @since 3.0
 * @deprecated The org.eclipse.update component has been replaced by Equinox p2.
 * This API will be deleted in a future release. See bug 311590 for details.
 */
public interface IOperation {
	/**
	 * Returns true when the operation has been processed.
	 * @return true when the operation completed.
	 */
	public abstract boolean isProcessed();
	/**
	 * Marks the operation as processed.
	 */
	public abstract void markProcessed();
	/**
	 * Executes operation.
	 * @param pm Progress monitor for the operation
	 * @param listener Operation listener
	 * @return true if operation was successful and a restart is needed.
	 * @throws CoreException
	 * @throws InvocationTargetException
	 */
	public abstract boolean execute(IProgressMonitor pm, IOperationListener listener) throws CoreException, InvocationTargetException;
}
