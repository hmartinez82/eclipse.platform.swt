/*******************************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.browser;

import org.eclipse.swt.*;
import org.eclipse.swt.internal.mozilla.*;

class EmbedFileLocProvider {

	public static final String NS_GRE_DIR = "GreD"; //$NON-NLS-1$
	public static final String NS_GRE_COMPONENT_DIR = "GreComsD"; //$NON-NLS-1$
	public static final String NS_APP_DEFAULTS_50_DIR = "DefRt"; //$NON-NLS-1$
	public static final String NS_APP_PREF_DEFAULTS_50_DIR = "PrfDef"; //$NON-NLS-1$
	public static final String NS_APP_PROFILE_DEFAULTS_50_DIR = "profDef"; //$NON-NLS-1$
	public static final String NS_APP_PROFILE_DEFAULTS_NLOC_50_DIR = "ProfDefNoLoc"; //$NON-NLS-1$
	public static final String NS_APP_USER_PROFILES_ROOT_DIR = "DefProfRt"; //$NON-NLS-1$
	public static final String NS_APP_RES_DIR = "ARes"; //$NON-NLS-1$
	public static final String NS_APP_CHROME_DIR = "AChrom"; //$NON-NLS-1$
	public static final String NS_APP_PLUGINS_DIR = "APlugns"; //$NON-NLS-1$
	public static final String NS_APP_SEARCH_DIR = "SrchPlugns"; //$NON-NLS-1$
	public static final String NS_APP_PLUGINS_DIR_LIST = "APluginsDL"; //$NON-NLS-1$
	public static final String DEFAULTS_DIR_NAME = "defaults"; //$NON-NLS-1$
	public static final String DEFAULTS_PREF_DIR_NAME = "pref"; //$NON-NLS-1$
	public static final String DEFAULTS_PROFILE_DIR_NAME = "profile"; //$NON-NLS-1$
	public static final String RES_DIR_NAME = "res"; //$NON-NLS-1$
	public static final String CHROME_DIR_NAME ="chrome"; //$NON-NLS-1$
	public static final String PLUGINS_DIR_NAME = "plugins"; //$NON-NLS-1$
	public static final String SEARCH_DIR_NAME = "searchplugins"; //$NON-NLS-1$
	public static final String COMPONENTS_DIR_NAME = "components"; //$NON-NLS-1$
	public static final String NS_XPCOM_INIT_CURRENT_PROCESS_DIR = "MozBinD"; //$NON-NLS-1$
	public static final String NS_XPCOM_CURRENT_PROCESS_DIR = "XCurProcD"; //$NON-NLS-1$
	public static final String NS_XPCOM_COMPONENT_DIR = "ComsD"; //$NON-NLS-1$
	public static final String NS_OS_CURRENT_PROCESS_DIR = "CurProcD"; //$NON-NLS-1$

	XPCOMObject supports;
	XPCOMObject directoryServiceProvider;
	XPCOMObject directoryServiceProvider2;	
	int refCount = 0;
	String mozillaPath;
	String grePath;

public EmbedFileLocProvider() {
	mozillaPath = GRE.mozillaPath;
	grePath = GRE.grePath;
	if (mozillaPath == null) throw new SWTError(XPCOM.errorMsg(XPCOM.NS_ERROR_FAILURE));	
	createCOMInterfaces();
}

int AddRef() {
	refCount++;
	return refCount;
}

void createCOMInterfaces() {
	/* Create each of the interfaces that this object implements */
	supports = new XPCOMObject(new int[]{2, 0, 0}){
		public int method0(int[] args) {return queryInterface(args[0], args[1]);}
		public int method1(int[] args) {return AddRef();}
		public int method2(int[] args) {return Release();}
	};
	
	directoryServiceProvider = new XPCOMObject(new int[]{2, 0, 0, 3}){
		public int method0(int[] args) {return queryInterface(args[0], args[1]);}
		public int method1(int[] args) {return AddRef();}
		public int method2(int[] args) {return Release();}
		public int method3(int[] args) {return getFile(args[0], args[1], args[2]);}
	};
		
	directoryServiceProvider2 = new XPCOMObject(new int[]{2, 0, 0, 3, 2}){
		public int method0(int[] args) {return queryInterface(args[0], args[1]);}
		public int method1(int[] args) {return AddRef();}
		public int method2(int[] args) {return Release();}
		public int method3(int[] args) {return getFile(args[0], args[1], args[2]);}
		public int method4(int[] args) {return getFiles(args[0], args[1]);}
	};
}

void disposeCOMInterfaces() {
	if (supports != null) {
		supports.dispose();
		supports = null;
	}	
	if (directoryServiceProvider != null) {
		directoryServiceProvider.dispose();
		directoryServiceProvider = null;	
	}
	if (directoryServiceProvider2 != null) {
		directoryServiceProvider2.dispose();
		directoryServiceProvider2 = null;	
	}	
}

int getAddress() {
	return directoryServiceProvider.getAddress();
}

int queryInterface(int riid, int ppvObject) {
	if (riid == 0 || ppvObject == 0) return XPCOM.NS_ERROR_NO_INTERFACE;
	nsID guid = new nsID();
	XPCOM.memmove(guid, riid, nsID.sizeof);
	
	if (guid.Equals(nsISupports.NS_ISUPPORTS_IID)) {
		XPCOM.memmove(ppvObject, new int[] {supports.getAddress()}, 4);
		AddRef();
		return XPCOM.NS_OK;
	}
	if (guid.Equals(nsIDirectoryServiceProvider.NS_IDIRECTORYSERVICEPROVIDER_IID)) {
		XPCOM.memmove(ppvObject, new int[] {directoryServiceProvider.getAddress()}, 4);
		AddRef();
		return XPCOM.NS_OK;
	}
	if (guid.Equals(nsIDirectoryServiceProvider2.NS_IDIRECTORYSERVICEPROVIDER2_IID)) {
		XPCOM.memmove(ppvObject, new int[] {directoryServiceProvider2.getAddress()}, 4);
		AddRef();
		return XPCOM.NS_OK;
	}
	
	XPCOM.memmove(ppvObject, new int[] {0}, 4);
	return XPCOM.NS_ERROR_NO_INTERFACE;
}
        	
int Release() {
	refCount--;
	if (refCount == 0) disposeCOMInterfaces();
	return refCount;
}
	
/* nsIDirectoryServiceProvider2 implementation */

int getFiles(int str, int nsISimpleEnumerator) {
	int rc = XPCOM.NS_ERROR_FAILURE;
	int length = XPCOM.strlen(str);
	byte[] dest = new byte[length];
	XPCOM.memmove(dest, str, length);
	String prop = new String(dest);	
	XPCOM.memmove(nsISimpleEnumerator, new int[] {0}, 4);	
	nsILocalFile localFile = null;	
	if (NS_APP_PLUGINS_DIR_LIST.equals(prop)) {
		if (mozillaPath == null) return XPCOM.NS_ERROR_FAILURE;
		if (mozillaPath.length() <= 0) return XPCOM.NS_ERROR_FAILURE;
		String path = new String(mozillaPath);
		int[] retVal = new int[1];
		rc = XPCOM.NS_NewLocalFile(path, true, retVal);
		if (rc == XPCOM.NS_OK && retVal[0] == 0) rc = XPCOM.NS_ERROR_NULL_POINTER;
		if (rc == XPCOM.NS_OK) localFile = new nsILocalFile(retVal[0]);
        nsString node = new nsString(PLUGINS_DIR_NAME);  
		rc = localFile.Append(node.getAddress());
        node.dispose();
	}
	if (rc == XPCOM.NS_OK) {
		int[] retVal = new int[1];
		rc = XPCOM.NS_NewSingletonEnumerator(localFile.getAddress(), retVal);
		if (rc == XPCOM.NS_OK && retVal[0] == 0) rc = XPCOM.NS_ERROR_NULL_POINTER;
		if (rc == XPCOM.NS_OK) XPCOM.memmove(nsISimpleEnumerator, retVal, 4);	 
	}
	if (localFile != null) localFile.Release();
	return rc;
}	
	
/* nsIDirectoryServiceProvider implementation */
	
int getFile(int str, int persistent, int nsFile) {    
	int rc = XPCOM.NS_ERROR_FAILURE;
	int length = XPCOM.strlen(str);
	byte[] dest = new byte[length];
	XPCOM.memmove(dest, str, length);
	String prop = new String(dest);	
	XPCOM.memmove(persistent, new int[] {1}, 4);
	XPCOM.memmove(nsFile, new int[] {0}, 4);	
	nsILocalFile localFile = null;		
	if (NS_GRE_DIR.equals(prop) || NS_GRE_COMPONENT_DIR.equals(prop)) {
		if (grePath == null || grePath.length() == 0) return XPCOM.NS_ERROR_FAILURE;
		int[] retVal = new int[1];
		rc = XPCOM.NS_NewLocalFile(grePath,true,retVal);
		if (rc == XPCOM.NS_OK && retVal[0] == 0) rc = XPCOM.NS_ERROR_NULL_POINTER;                          
		if (rc == XPCOM.NS_OK) {
			localFile = new nsILocalFile(retVal[0]);
			if (NS_GRE_COMPONENT_DIR.equals(prop)) {
                nsString node = new nsString(COMPONENTS_DIR_NAME);
                rc = localFile.Append(node.getAddress());
                node.dispose(); 
            } 
		}
	}
	else if (NS_XPCOM_INIT_CURRENT_PROCESS_DIR.equals(prop) || 
		NS_OS_CURRENT_PROCESS_DIR.equals(prop) ||
		NS_XPCOM_COMPONENT_DIR.equals(prop)) {
		if (mozillaPath == null || mozillaPath.length() == 0) return XPCOM.NS_ERROR_FAILURE;
		String path = new String(mozillaPath);	
		int[] retVal = new int[1];
		rc = XPCOM.NS_NewLocalFile(mozillaPath,true,retVal);
		if (rc == XPCOM.NS_OK && retVal[0] == 0) rc = XPCOM.NS_ERROR_NULL_POINTER;
		if (rc == XPCOM.NS_OK) {
			localFile = new nsILocalFile(retVal[0]);
			if (NS_XPCOM_COMPONENT_DIR.equals(prop)) {
                nsString node = new nsString(COMPONENTS_DIR_NAME);
                rc = localFile.Append(node.getAddress());
                node.dispose(); 
            } 
		}
	}
	if (localFile != null && XPCOM.NS_OK == rc) {
		int[] result = new int[1];
	    int rc1 = localFile.QueryInterface(nsIFile.NS_IFILE_IID, result); 
		if (rc1 != XPCOM.NS_OK || result[0] == 0) rc1 = XPCOM.NS_NOINTERFACE;
		else XPCOM.memmove(nsFile, result, 4);	 	
	}		
	if (localFile != null) localFile.Release();

	return rc; 
}		
}