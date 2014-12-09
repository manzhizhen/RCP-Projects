/* 文件名：     SqlEditorInput.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-1-14
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.sql.editor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import cn.sunline.suncard.powerdesigner.Activator;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;

/**
 * SQL执行Editor的Input
 * @author  Manzhizhen
 * @version 1.0, 2013-1-14
 * @see 
 * @since 1.0
 */
public class SqlEditorInput implements IStorageEditorInput{
	private String editorName = "SQL命令窗口";
	private IStorage storage;
	
	public SqlEditorInput() {
		storage = new IStorage() {
			public InputStream getContents() throws CoreException {
				return new ByteArrayInputStream("".getBytes());
			}

			public IPath getFullPath() {
				return null;
			}

			public String getName() {
				return editorName;
			}

			public boolean isReadOnly() {
				return false;
			}

			public Object getAdapter(Class adapter) {
				return null;
			}
		};
	}
	
	public SqlEditorInput(IStorage storage) {
		this.storage = storage;
	}
	
	
	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return AbstractUIPlugin
				.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						IDmImageKey.SQL_COMMAND_WINDOW_16);
	}

	@Override
	public String getName() {
		return editorName;
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return editorName;
	}

	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public IStorage getStorage() throws CoreException {
		return storage;
	}

}
