package cn.sunline.suncard.sde.bs.entity;

import java.sql.Timestamp;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * BsWidget entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BS_WIDGET")
public class BsWidget implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields

	private BsWidgetId id;
	private String widgetName;
	private String widgetType;
	private String parWidgetId;
	private String pluginId;
	private Timestamp modiDate;
	private String modiUser;
	private Integer version;

	// Constructors
	
	// 此字段并非属于表中字段
	private String mappingType ;

	/** default constructor */
	public BsWidget() {
	}

	/** minimal constructor */
	public BsWidget(BsWidgetId id) {
		this.id = id;
	}

	/** full constructor */
	public BsWidget(BsWidgetId id, String widgetName, String widgetType,
			String parWidgetId, String pluginId, Timestamp modiDate,
			String modiUser, Integer version) {
		this.id = id;
		this.widgetName = widgetName;
		this.widgetType = widgetType;
		this.parWidgetId = parWidgetId;
		this.pluginId = pluginId;
		this.modiDate = modiDate;
		this.modiUser = modiUser;
		this.version = version;
	}

	// Property accessors
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "bankorgId", column = @Column(name = "BANKORG_ID", nullable = false, precision = 10, scale = 0)),
			@AttributeOverride(name = "pcId", column = @Column(name = "PC_ID", nullable = false, length = 20)),
			@AttributeOverride(name = "widgetId", column = @Column(name = "WIDGET_ID", nullable = false, length = 10)) })
	public BsWidgetId getId() {
		return this.id;
	}

	public void setId(BsWidgetId id) {
		this.id = id;
	}

	@Column(name = "WIDGET_NAME", length = 40)
	public String getWidgetName() {
		return this.widgetName;
	}

	public void setWidgetName(String widgetName) {
		this.widgetName = widgetName;
	}

	@Column(name = "WIDGET_TYPE", length = 1)
	public String getWidgetType() {
		return this.widgetType;
	}

	public void setWidgetType(String widgetType) {
		this.widgetType = widgetType;
	}

	@Column(name = "PAR_WIDGET_ID", length = 10)
	public String getParWidgetId() {
		return this.parWidgetId;
	}

	public void setParWidgetId(String parWidgetId) {
		this.parWidgetId = parWidgetId;
	}

	@Column(name = "PLUGIN_ID", length = 10)
	public String getPluginId() {
		return this.pluginId;
	}

	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
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

	@Transient
	public String getMappingType() {
		return mappingType;
	}

	public void setMappingType(String mappingType) {
		this.mappingType = mappingType;
	}
}