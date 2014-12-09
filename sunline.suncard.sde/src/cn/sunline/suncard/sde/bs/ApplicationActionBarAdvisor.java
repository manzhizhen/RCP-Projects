package cn.sunline.suncard.sde.bs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.entity.BsUser;
import cn.sunline.suncard.sde.bs.system.Context;
import cn.sunline.suncard.sde.bs.system.PermissionContext;
import cn.sunline.suncard.sde.bs.ui.actions.OpenAction;
import cn.sunline.suncard.sde.bs.ui.actions.SearchAction;
import cn.sunline.suncard.sde.bs.ui.resource.IAppconstans;
import cn.sunline.suncard.sde.bs.ui.resource.IImageKey;
import cn.sunline.suncard.sde.bs.ui.status.StatusBarContribution;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {
	private IAction newAction;
	private IAction openAction;
	private IAction closeAction;
	private IAction closeAllAction;
	private IAction saveAction;
	private IAction saveAsAction;
	private IAction saveAllAction;
	private IAction exitAction;
	private IAction cutAction;
	private IAction copyAction;
	private IAction pasteAction;
	private IAction deleteAction;
	private IAction resetwinAction;
	private IAction configAction;
	private IAction printAction;
	private IAction searchAction;
	
	//文件菜单下的Action
	private List<IAction> fileActions = new ArrayList<IAction>();
	//编辑菜单下的Action
	private List<IAction> editActions = new ArrayList<IAction>();
	//工具栏的Action
	private List<IAction> toolbarActions = new ArrayList<IAction>();
	//窗口菜单下的Action
	private List<IAction> windowActions = new ArrayList<IAction>();
	
	private StatusBarContribution stauBarContribution;
	

    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }
    protected void makeActions(IWorkbenchWindow window) {
    	newAction = ActionFactory.NEW.create(window);
    	newAction.setId(IAppconstans.NEW_ACTION_ID);
    	newAction.setText(I18nUtil.getMessage("new"));
    	newAction.setAccelerator(SWT.ALT+'N');
    	newAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
             	IAppconstans.APPLICATION_ID, IImageKey.NEW_ACTION));
    	register(newAction);
    	fileActions.add(newAction);
    	toolbarActions.add(newAction);
    	
        openAction = new OpenAction(window);
		register(openAction);
		fileActions.add(openAction);
		toolbarActions.add(openAction);
        
        closeAction = ActionFactory.CLOSE.create(window);
        closeAction.setText(I18nUtil.getMessage("close"));
        closeAction.setAccelerator(SWT.CTRL+'W');
        closeAction.setToolTipText(I18nUtil.getMessage("close"));
        register(closeAction);
        fileActions.add(closeAction);
        
        closeAllAction = ActionFactory.CLOSE_ALL.create(window);
        closeAllAction.setText(I18nUtil.getMessage("closeAll"));
        closeAllAction.setAccelerator(SWT.CTRL+SWT.SHIFT+'C');
        closeAllAction.setToolTipText(I18nUtil.getMessage("closeAll"));
        register(closeAllAction);
        fileActions.add(closeAllAction);
        
        saveAction = ActionFactory.SAVE.create(window);
        saveAction.setText(I18nUtil.getMessage("save"));
        saveAction.setAccelerator(SWT.CTRL+'S');
        saveAction.setToolTipText(I18nUtil.getMessage("save"));
        register(saveAction);
        fileActions.add(saveAction);
        toolbarActions.add(saveAction);
        
        saveAsAction = ActionFactory.SAVE_AS.create(window);
        saveAsAction.setText(I18nUtil.getMessage("saveAs"));
        saveAsAction.setAccelerator(SWT.ALT+'A');
        saveAsAction.setToolTipText(I18nUtil.getMessage("saveAs"));
        register(saveAsAction);
        fileActions.add(saveAsAction);
        
        saveAllAction = ActionFactory.SAVE_ALL.create(window);
        saveAllAction.setText(I18nUtil.getMessage("saveAll"));
        saveAllAction.setAccelerator(SWT.CTRL+SWT.SHIFT+'S');
        saveAllAction.setToolTipText(I18nUtil.getMessage("saveAll"));
        register(saveAllAction);
        fileActions.add(saveAllAction);
        toolbarActions.add(saveAllAction);
        
        exitAction = ActionFactory.QUIT.create(window);
        exitAction.setText(I18nUtil.getMessage("quit"));
        exitAction.setAccelerator(SWT.CTRL+'Q');
        exitAction.setToolTipText(I18nUtil.getMessage("quit"));
        register(exitAction);
        fileActions.add(exitAction);
        
        cutAction = ActionFactory.CUT.create(window);
        cutAction.setText(I18nUtil.getMessage("cut"));
        cutAction.setAccelerator(SWT.CTRL+'X');
        cutAction.setToolTipText(I18nUtil.getMessage("cut"));
        register(cutAction);
        editActions.add(cutAction);
        
        copyAction = ActionFactory.COPY.create(window);
        copyAction.setText(I18nUtil.getMessage("copy"));
        copyAction.setAccelerator(SWT.CTRL+'C');
        copyAction.setToolTipText(I18nUtil.getMessage("copy"));
        register(copyAction);
        editActions.add(copyAction);
        
        pasteAction = ActionFactory.PASTE.create(window);
        pasteAction.setText(I18nUtil.getMessage("paste"));
        pasteAction.setAccelerator(SWT.CTRL+'V');
        pasteAction.setToolTipText(I18nUtil.getMessage("paste"));
        register(pasteAction);
        editActions.add(pasteAction);
        
        deleteAction = ActionFactory.DELETE.create(window);
        deleteAction.setText(I18nUtil.getMessage("delete"));
        deleteAction.setToolTipText(I18nUtil.getMessage("delete"));
        register(deleteAction);
        editActions.add(deleteAction);
        
//        resetwinAction = new ResetWindowAction();
        resetwinAction = ActionFactory.RESET_PERSPECTIVE.create(window);
        resetwinAction.setId(IAppconstans.RESETWINDOW_ACTION_ID);
        resetwinAction.setText(I18nUtil.getMessage("reset"));
        resetwinAction.setAccelerator(SWT.CTRL+'R');
        register(resetwinAction);
        windowActions.add(resetwinAction);
        
        configAction = ActionFactory.PREFERENCES.create(window);
        configAction.setId(IAppconstans.CONFIG_ACTION_ID);
        configAction.setText(I18nUtil.getMessage("config"));
		register(configAction);
		windowActions.add(configAction);
		
		printAction = ActionFactory.PRINT.create(window);
		printAction.setToolTipText(I18nUtil.getMessage("print"));
		printAction.setAccelerator(SWT.CTRL+'P');
		printAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
				IAppconstans.APPLICATION_ID,IImageKey.PRINT_ACTION ));
		register(printAction);
		toolbarActions.add(printAction);
		
		searchAction = new SearchAction();
		register(searchAction);
		toolbarActions.add(searchAction);
		
		//添加状态栏信息
		String msg = "登录用户：" + ((BsUser)Context.getSessionMap().get(Constants.CURRENT_USER)).getUserName();
		stauBarContribution = new StatusBarContribution(msg);
		stauBarContribution.setVisible(true);
    }
        
        

	protected void fillMenuBar(IMenuManager menuBar) {
    	MenuManager fileMenu = new MenuManager(I18nUtil.getMessage("file"),IWorkbenchActionConstants.M_FILE);
    	
    	if (PermissionContext.getInstance().checkPermission(fileMenu.getId())){
    		menuBar.add(fileMenu);
    		for (int i=0; i<fileActions.size(); i++){
    			IAction action = fileActions.get(i);
    			if (PermissionContext.getInstance().checkPermission(action.getId())){
    				fileMenu.add(action);
    			}
    		}
    	}
    	
    	MenuManager editMenu = new MenuManager(I18nUtil.getMessage("edit"),IWorkbenchActionConstants.M_EDIT);
    	if (PermissionContext.getInstance().checkPermission(editMenu.getId())){
    		menuBar.add(editMenu);
    		for (int i=0; i<editActions.size(); i++){
    			IAction action = editActions.get(i);
    			if (PermissionContext.getInstance().checkPermission(action.getId())){
    				editMenu.add(action);
    			}
    		}
    	}
    	
    	MenuManager windowMenu = new MenuManager(I18nUtil.getMessage("window"),IWorkbenchActionConstants.M_WINDOW);
    	if (PermissionContext.getInstance().checkPermission(editMenu.getId())){
    		menuBar.add(windowMenu);
    		for (int i=0; i<windowActions.size(); i++){
    			IAction action = windowActions.get(i);
    			if (PermissionContext.getInstance().checkPermission(action.getId())){
    				windowMenu.add(action);
    			}
    		}
    	}
    }
	
	@Override
	protected void fillCoolBar(ICoolBarManager coolBar) {
		IToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT | SWT.SHADOW_IN);
		coolBar.add(toolBarManager);
		
    	for (int i=0; i<toolbarActions.size(); i++){
    		IAction action = toolbarActions.get(i);
			if (PermissionContext.getInstance().checkPermission(action.getId())){
				toolBarManager.add(action);
			}
    	}
	}
	
	@Override
	protected void fillStatusLine(IStatusLineManager statusLine) {
		statusLine.add(stauBarContribution);
	}
}
