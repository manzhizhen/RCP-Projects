/* 文件名：     SqlEditorActionContributor.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-2-16
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.sql.editor;

import org.eclipse.ui.editors.text.TextEditorActionContributor;
import org.eclipse.ui.texteditor.RetargetTextEditorAction;

/**
 * SQL脚本编辑Editor的工具栏
 * @author  Manzhizhen
 * @version 1.0, 2013-2-16
 * @see 
 * @since 1.0
 */
public class SqlEditorActionContributor extends TextEditorActionContributor{
    private RetargetTextEditorAction fContentAssistProposal;
    private RetargetTextEditorAction fContentAssistTip;
    
    
}
