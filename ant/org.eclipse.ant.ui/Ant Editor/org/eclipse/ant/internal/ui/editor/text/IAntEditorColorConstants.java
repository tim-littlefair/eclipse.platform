/*******************************************************************************
 * Copyright (c) 2002, 2004 GEBIT Gesellschaft fuer EDV-Beratung
 * und Informatik-Technologien mbH, 
 * Berlin, Duesseldorf, Frankfurt (Germany) and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     GEBIT Gesellschaft fuer EDV-Beratung und Informatik-Technologien mbH - initial API and implementation
 * 	   IBM Corporation - bug fixes
 *******************************************************************************/

package org.eclipse.ant.internal.ui.editor.text;

/**
 * The color constants used by the Ant Editor.
 */
public interface IAntEditorColorConstants {
	
	/**
	 * The symbolic names for colors used for the various text tokens in the Ant editor
	 * @see org.eclipse.jface.resource.ColorRegistry
	 */
	public static final String XML_COMMENT_COLOR = "org.eclipse.ant.ui.commentsColor"; //$NON-NLS-1$
	public static final String PROCESSING_INSTRUCTIONS_COLOR = "org.eclipse.ant.ui.processingInstructionsColor"; //$NON-NLS-1$
	public static final String STRING_COLOR = "org.eclipse.ant.ui.constantStringsColor"; //$NON-NLS-1$
	public static final String TEXT_COLOR = "org.eclipse.ant.ui.textColor"; //$NON-NLS-1$
	public static final String TAG_COLOR = "org.eclipse.ant.ui.tagsColor"; //$NON-NLS-1$
}
