package cn.sunline.suncard.sde.bs.entity;

import java.sql.Timestamp;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * BsPluginlog entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BS_PLUGINLOG")
public class BsPluginlog implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields

	private BsPluginlogId id;
	private String pluginId;
	private String processType;
	private Timestamp processDate;
	private String srcPluginVer;
	private String pluginVer;
	private String patchId;
	private String replaceUrl;
	private Timestamp modiDate;
	private String modiUser;
	private Integer version;

	// Constructors

	/** default constructor */
	public BsPluginlog() {
	}

	/** minimal constructor */
	public BsPluginlog(BsPluginlogId id) {
		this.id = id;
	}

	/** full constructor */
	public BsPluginlog(BsPluginlogId id, String pluginId, String processType,
			Timestamp processDate, String srcPluginVer, String pluginVer,
			String patchId, String replaceUrl, Timestamp modiDate,
			String modiUser, Integer version) {
		this.id = id;
		this.pluginId = pluginId;
		this.processType = processType;
		this.processDate = processDate;
		this.srcPluginVer = srcPluginVer;
		this.pluginVer = pluginVer;
		this.patchId = patchId;
		this.replaceUrl = replaceUrl;
		this.modiDate = modiDate;
		this.modiUser = modiUser;
		this.version = version;
	}

	// Property accessors
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "bankorgId", column = @Column(name = "BANKORG_ID", nullable = false, precision = 10, scale = 0)),
			@AttributeOverride(name = "pcId", column = @Column(name = "PC_ID", nullable = false, length = 20)),
			@AttributeOverride(name = "logSeq", column = @Column(name = "LOG_SEQ", nullable = false, precision = 10, scale = 0)) })
	public BsPluginlogId getId() {
		return this.id;
	}

	public void setId(BsPluginlogId id) {
		this.id = id;
	}

	@Column(name = "PLUGIN_ID", length = 10)
	public String getPluginId() {
		return this.pluginId;
	}

	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}

	@Column(name = "PROCESS_TYPE", length = 1)
	public String getProcessType() {
		return this.processType;
	}

	public void setProcessType(String processType) {
		this.processType = processType;
	}

	@Column(name = "PROCESS_DATE", length = 0)
	public Timestamp getProcessDate() {
		return this.processDate;
	}

	public void setProcessDate(Timestamp processDate) {
		this.processDate = processDate;
	}

	@Column(name = "SRC_PLUGIN_VER", length = 10)
	public String getSrcPluginVer() {
		return this.srcPluginVer;
	}

	public void setSrcPluginVer(String srcPluginVer) {
		this.srcPluginVer = srcPluginVer;
	}

	@Column(name = "PLUGIN_VER", length = 10)
	public String getPluginVer() {
		return this.pluginVer;
	}

	public void setPluginVer(String pluginVer) {
		this.pluginVer = pluginVer;
	}

	@Column(name = "PATCH_ID", length = 10)
	public String getPatchId() {
		return this.patchId;
	}

	public void setPatchId(String patchId) {
		this.patchId = patchId;
	}

	@Column(name = "REPLACE_URL", length = 40)
	public String getReplaceUrl() {
		return this.replaceUrl;
	}

	public void setReplaceUrl(String replaceUrl) {
		this.replaceUrl = replaceUrl;
	}

	@Column(name = "MODI_DATE", length = 0)
	public Timestamp getModiDate() {
		return this.modiDate;
	}

	public void setModiDate(Timestamp modiDate) {
		this.modiDate = modiDate;
	}

	@Column(name = "MODI_USER", length = 64)
	public String getModiUser() {
		return this.modiUser;
	}

	public void setModiUser(String modiUser) {
		this.modiUser = modiUser;
	}

	@Column(name = "VERSION", precision = 8, scale = 0)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

}