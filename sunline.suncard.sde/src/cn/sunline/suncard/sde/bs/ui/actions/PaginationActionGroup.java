/*
 * 文件名：PaginationActonGroup.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：分页工具类
 * 修改人：heyong
 * 修改时间：2011-10-22
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.actions.ActionGroup;

import cn.sunline.suncard.sde.bs.biz.AbstractEntityBiz;
import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 分页工具类
 * 此类实现了分页的首页、上一页、下一页及尾页等功能，还可根据当前页改变各个按钮的可用状态
 * 此类可通用各个模块
 * @author heyong
 * @version 1.0, 2011-10-22
 * @see 
 * @since 1.0
 */
public class PaginationActionGroup<T> extends ActionGroup{
	//要分页的TableViewer
	private TableViewer tableViewer;
	//总页数
	private int pageCount;
	//总记录数
	private int recordCount;
	//当前页,默认为1
	private int currPage = 1;
	//每页显示记录数
	private int pageSize = Constants.RECORD_SIZE_PER_PAGE;
	
	private Action firstPageAction; //首页
	private Action previousPageAction; //前一页
	private Action nextPageAction;	//后一页
	private Action lastPageAction;	//尾页
	//提示信息，由于ToolBar中不能添加标签，故采用此方法
	private Action msgAction;
	//所有需进行分业的业务逻辑类
	private AbstractEntityBiz<T> biz;
	
	public PaginationActionGroup(TableViewer tableViewer, AbstractEntityBiz<T> biz){
		this.tableViewer = tableViewer;
		this.biz = biz;
	}

	private void makeActions(){
		firstPageAction = new FirstPageAction();
		previousPageAction = new PreviousPageAction();
		nextPageAction = new NextPageAction();
		lastPageAction = new LastPageAction();
		msgAction = new MessageAction();
		changeActionEnabled();
	}
	
	public void fillActionToolBars(ToolBarManager toolBarManager){
		this.makeActions();
		toolBarManager.add(createContributionItem(firstPageAction));
		toolBarManager.add(createContributionItem(previousPageAction));
		toolBarManager.add(createContributionItem(nextPageAction));
		toolBarManager.add(createContributionItem(lastPageAction));
		toolBarManager.add(msgAction);
		//更新工具栏
		toolBarManager.update(true);
	}

	//对Action进行封装，使其显示图片和文字
	private IContributionItem createContributionItem(Action action){
		ActionContributionItem aci = new ActionContributionItem(action);
		aci.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		return aci;
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}
	
	
	class FirstPageAction extends Action{

		public FirstPageAction(){
			setId("firstPageAction");
			setText(I18nUtil.getMessage("firstPage"));
		//	setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(IAppconstans.APPLICATION_ID, 
		//			IImageKey.PAGINATION_FIRST_PAGE_ICON));
			setToolTipText(I18nUtil.getMessage("firstPage"));
		}
		
		@Override
		public void run() {
			setCurrPage(1);
			changeActionEnabled();
		}
	}
	
	class PreviousPageAction extends Action{
		
		public PreviousPageAction(){
			setId("previousPageAction");
			setText(I18nUtil.getMessage("previousPage"));
		//	setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(IAppconstans.APPLICATION_ID, 
		//			IImageKey.PAGINATION_PRE_PAGE_ICON));
			setToolTipText(I18nUtil.getMessage("previousPage"));
		}
		
		@Override
		public void run() {
			setCurrPage(currPage - 1);
			changeActionEnabled();
		}
	}
	
	class NextPageAction extends Action{
		
		public NextPageAction(){
			setId("nextPageAction");
			setText(I18nUtil.getMessage("nextPage"));
		//	setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(IAppconstans.APPLICATION_ID, 
		//			IImageKey.PAGINATION_NEXT_PAGE_ICON));
			setToolTipText(I18nUtil.getMessage("nextPage"));
		}
		
		@Override
		public void run() {
			setCurrPage(currPage + 1);
			changeActionEnabled();
		}
	}
	
	class LastPageAction extends Action{
		public LastPageAction(){
			setId("lastPageAction");
			setText(I18nUtil.getMessage("lastPage"));
		//	setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(IAppconstans.APPLICATION_ID, 
		//			IImageKey.PAGINATION_LAST_PAGE_ICON));
			setToolTipText(I18nUtil.getMessage("lastPage"));
		}
		
		@Override
		public void run() {
			setCurrPage(pageCount);
			changeActionEnabled();
		}
	}
	
	class MessageAction extends Action{
		public MessageAction(){
			setId("messageAction");
			setEnabled(true);
		}
		
		@Override
		public void run() {
		}
	}
	private void setCurrPage(int currPage){
		if (currPage <1 ) this.currPage = 1;
		else if (currPage > pageCount) this.currPage = pageCount;
		else this.currPage = currPage;
	}
	
	/**
	 * 根据当前页改变按钮的enabled属性
	 */
	public void changeActionEnabled(){
		//设置表的数据源
		this.tableViewer.setInput(biz.getByPage(currPage, pageSize));
		//得到总记录数
		recordCount = biz.getRecordCount();
		//计算总页数
		pageCount = recordCount % pageSize == 0? recordCount / pageSize : recordCount / pageSize + 1;
		if (pageCount == 1 || pageCount == 0){//如果只有一页,或者记录数为0
				firstPageAction.setEnabled(false);
				previousPageAction.setEnabled(false);
				nextPageAction.setEnabled(false);
				lastPageAction.setEnabled(false);
		 }else if (currPage == 1){  //如果当前页为首页（即第一页），则首页按钮及上一页按钮不可用
			firstPageAction.setEnabled(false);
			previousPageAction.setEnabled(false);
			nextPageAction.setEnabled(true);
			lastPageAction.setEnabled(true);
		}else if (currPage == pageCount){ //如果当前页为尾页（即最后一页），则下一页及最后一页按钮不可用
			firstPageAction.setEnabled(true);
			previousPageAction.setEnabled(true);
			nextPageAction.setEnabled(false);
			lastPageAction.setEnabled(false);
		}else{
			firstPageAction.setEnabled(true);
			previousPageAction.setEnabled(true);
			nextPageAction.setEnabled(true);
			lastPageAction.setEnabled(true);
		}
		//如果记录数为0,则将当前页设为0
		if (recordCount == 0){
			pageCount = 1;
		}
		//主要用于删除时用,当当前页为尾页时，且尾页记录全部删除后，执行以下代码
		if (currPage > pageCount){
			setCurrPage(currPage);
			lastPageAction.run();
		}
		msgAction.setText(I18nUtil.getMessage("paginationinfo", new Object[]{recordCount, pageSize, currPage, pageCount}));
	}
}
