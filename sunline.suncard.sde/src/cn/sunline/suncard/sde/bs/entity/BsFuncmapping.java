package cn.sunline.suncard.sde.bs.entity;

import java.sql.Timestamp;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * BsFuncmapping entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BS_FUNCMAPPING")
public class BsFuncmapping implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields

	private BsFuncmappingId id;
	private String mappingType;
	private Timestamp modiDate;
	private String modiUser;
	private Integer version;

	// Constructors

	/** default constructor */
	public BsFuncmapping() {
	}

	/** minimal constructor */
	public BsFuncmapping(BsFuncmappingId id) {
		this.id = id;
	}

	/** full constructor */
	public BsFuncmapping(BsFuncmappingId id, String mappingType,
			Timestamp modiDate, String modiUser, Integer version) {
		this.id = id;
		this.mappingType = mappingType;
		this.modiDate = modiDate;
		this.modiUser = modiUser;
		this.version = version;
	}

	// Property accessors
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "bankorgId", column = @Column(name = "BANKORG_ID", nullable = false, precision = 10, scale = 0)),
			@AttributeOverride(name = "pcId", column = @Column(name = "PC_ID", nullable = false, length = 20)),
			@AttributeOverride(name = "functionId", column = @Column(name = "FUNCTION_ID", nullable = false, length = 10)),
			@AttributeOverride(name = "widgetId", column = @Column(name = "WIDGET_ID", nullable = false, length = 10)) })
	public BsFuncmappingId getId() {
		return this.id;
	}

	public void setId(BsFuncmappingId id) {
		this.id = id;
	}

	@Column(name = "MAPPING_TYPE", length = 1)
	public String getMappingType() {
		return this.mappingType;
	}

	public void setMappingType(String mappingType) {
		this.mappingType = mappingType;
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