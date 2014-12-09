/*
 * 文件名：PasswordModifyDialog.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：密码对话框
 * 修改人： 周兵
 * 修改时间：2001-02-16
 * 修改内容：新增
*/
package cn.sunline.suncard.sde.bs.ui.dailogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.springframework.security.providers.encoding.Md5PasswordEncoder;

import cn.sunline.suncard.sde.bs.biz.BsUserBiz;
import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.entity.BsUser;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.system.Context;
import cn.sunline.suncard.sde.bs.ui.resource.IAppconstans;
import cn.sunline.suncard.sde.bs.ui.resource.IImageKey;
import cn.sunline.suncard.sde.bs.ui.resource.IWidgetId;
import cn.sunline.suncard.sde.bs.ui.resource.SWTResourceManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import cn.sunline.suncard.sde.bs.util.WidgetPermissionUtil;

/**
 * 密码对话框〉
 * 实现登陆功能
 * @author    周兵
 * @version   1.0 2011-09-23
 * @see       
 * @since     1.0 
 */
public class PasswordModifyDialog extends TitleAreaDialog {
	private Text previousPassword_text;
	private Text newPassword_text;
	private Text confirmPassword_text;

	private GC gc;
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public PasswordModifyDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	//点击menu按钮“密码修改”后的对话框排布
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(null);
		
		setTitleImage(CacheImage.getCacheImage().getImage(IAppconstans.APPLICATION_ID, IImageKey.PASSWORD_MODIFY_ICON));
		setTitle(I18nUtil.getMessage("passwordmodi"));
		setMessage(I18nUtil.getMessage("passwordmodiinfo"));
		
		Group group = new Group(container, SWT.NONE);
		group.setBounds(10, 0, 415, 133);
		
		Label lblNewLabel = new Label(group, SWT.RIGHT);
		lblNewLabel.setBounds(95, 27, 56, 18);
		lblNewLabel.setText(I18nUtil.getMessage("previousPassword"));
		
		previousPassword_text = new Text(group, SWT.BORDER | SWT.PASSWORD);
		previousPassword_text.setBounds(157, 24, 147, 23);
		previousPassword_text.setTextLimit(40);
		previousPassword_text.setEnabled(WidgetPermissionUtil.checkPermission(IWidgetId.SDE_T_PWD_MODIFY_OLDPWD_TEXT));
		
		Label lblNewLabel_1 = new Label(group, SWT.RIGHT);
		lblNewLabel_1.setBounds(95, 56, 56, 18);
		lblNewLabel_1.setText(I18nUtil.getMessage("newPassword"));
		
		newPassword_text = new Text(group, SWT.BORDER | SWT.PASSWORD);
		newPassword_text.setBounds(157, 53, 147, 23);
		newPassword_text.setTextLimit(40);
		newPassword_text.setEnabled(WidgetPermissionUtil.checkPermission(IWidgetId.SDE_T_PWD_MODIFY_NEWPWD_TEXT));
		
		Label lblNewLabel_2 = new Label(group, SWT.RIGHT);
		lblNewLabel_2.setBounds(95, 88, 56, 15);
		lblNewLabel_2.setText(I18nUtil.getMessage("confirmPassword"));
		
		confirmPassword_text = new Text(group, SWT.BORDER | SWT.PASSWORD);
		confirmPassword_text.setBounds(157, 85, 147, 23);
		confirmPassword_text.setTextLimit(40);
		confirmPassword_text.setEnabled(WidgetPermissionUtil.checkPermission(IWidgetId.SDE_T_PWD_MODIFY_CONFIRMPWD_TEXT));

		//创建gc,用于重画边框
		gc = new GC(group);
				
		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, I18nUtil.getMessage("submit"),
				true).setEnabled(WidgetPermissionUtil.checkPermission(IWidgetId.SDE_B_PWD_MODIFY_COMMIT));
		createButton(parent, IDialogConstants.CANCEL_ID,
				I18nUtil.getMessage("cancle"), false).setEnabled(WidgetPermissionUtil.checkPermission(IWidgetId.SDE_B_PWD_MODIFY_CANCEL));
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(441, 285);
	}

	@Override
	protected void okPressed() {
		//如果验证没有通过，直接返回
		if(!validate()){
			return;
		}
		
		//创建biz对象
		BsUserBiz biz = new BsUserBiz();
		
		//如果密码输入不正确，返回
		if (! biz.checkPassword(previousPassword_text.getText().trim())){
			setErrorMessage(I18nUtil.getMessage("passwordnotRigaht"));
			drawColorBorder(previousPassword_text.getBounds(), SWTResourceManager.getColor(255, 0, 0));
			return ;
		}
		
		//如果通过则弹出对话框
		if(MessageDialog.openConfirm(getShell(), I18nUtil.getMessage("CONFIRMINFO"), 
				I18nUtil.getMessage("CONFIRMMODIFY"))){
			//得到当前登陆用户
			BsUser user = (BsUser)Context.getSessionMap().get(Constants.CURRENT_USER);
			
			String newPasswd = newPassword_text.getText().trim();
			//将新密码与UserId进行加密，组成新的加密密码
			String uiNewpasswd = new Md5PasswordEncoder().encodePassword(newPasswd, user.getId().getUserId());
			//用户设置密码为UIpassword
			user.setPassword(uiNewpasswd);
			//通过biz更新用户user
			biz.updateUser(user);
			
			super.okPressed();
		}
	}
    
	private boolean validate(){
		//先用原色画框
		drawColorBorder(previousPassword_text.getBounds(), previousPassword_text.getParent().getBackground());
		drawColorBorder(newPassword_text.getBounds(), newPassword_text.getParent().getBackground());
		drawColorBorder(confirmPassword_text.getBounds(), confirmPassword_text.getParent().getBackground());
		
		//如果原始密码为空，或者原始密码长度为0，弹出对话框密码不能为空
		if(previousPassword_text.getText() == null || previousPassword_text.getText().trim().length() == 0){
			setErrorMessage(I18nUtil.getMessage("PASSWORD")+I18nUtil.getMessage("notnull"));
			drawColorBorder(previousPassword_text.getBounds(), SWTResourceManager.getColor(255, 0, 0));
			//返回false，表示不通过
			return false;
		}else if (newPassword_text.getText() == null || newPassword_text.getText().trim().length() == 0){
			setErrorMessage(I18nUtil.getMessage("newPassword")+I18nUtil.getMessage("notnull"));
			drawColorBorder(newPassword_text.getBounds(), SWTResourceManager.getColor(255, 0, 0));
			//返回false，表示不通过
			return false;
		}else if (confirmPassword_text.getText() == null || confirmPassword_text.getText().trim().length() == 0){
			setErrorMessage(I18nUtil.getMessage("confirmPassword")+I18nUtil.getMessage("notnull"));
			drawColorBorder(confirmPassword_text.getBounds(), SWTResourceManager.getColor(255, 0, 0));
			//返回false，表示不通过
			return false;
		}else if (!newPassword_text.getText().trim().equals(confirmPassword_text.getText().trim())){ 	 //如果新密码和确认密码不一致，弹出对话框，密码不一致
			setErrorMessage(I18nUtil.getMessage("passwordnotsame"));
			drawColorBorder(newPassword_text.getBounds(), SWTResourceManager.getColor(255, 0, 0));
			drawColorBorder(confirmPassword_text.getBounds(), SWTResourceManager.getColor(255, 0, 0));
			//返回false，表示不通过
			return false;
		}
		setErrorMessage(null);
		return true;
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(I18nUtil.getMessage("passwordmodi"));
		newShell.setImage(CacheImage.getCacheImage().getImage(IAppconstans.APPLICATION_ID, IImageKey.PASSWORD_MODIFY_ICON));
	}
	
	/**
	 * 重画边框
	 * @param rect 
	 * @param color
	 */
	private void drawColorBorder(Rectangle rect, Color color){
		gc.setForeground(color);  
		gc.drawRectangle(new Rectangle(rect.x - 1, rect.y - 1, rect.width + 1, rect.height + 1)); 
	}
}
