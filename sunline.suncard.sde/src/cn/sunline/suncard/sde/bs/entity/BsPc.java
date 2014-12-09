package cn.sunline.suncard.sde.bs.entity;

import java.sql.Timestamp;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * BsPc entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BS_PC")
public class BsPc implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields

	private BsPcId id;
	private String pcName;
	private String ipAddr;
	private Timestamp modiDate;
	private String modiUser;
	private Integer version;

	// Constructors

	/** default constructor */
	public BsPc() {
	}

	/** minimal constructor */
	public BsPc(BsPcId id) {
		this.id = id;
	}

	/** full constructor */
	public BsPc(BsPcId id, String pcName, String ipAddr, Timestamp modiDate,
			String modiUser, Integer version) {
		this.id = id;
		this.pcName = pcName;
		this.ipAddr = ipAddr;
		this.modiDate = modiDate;
		this.modiUser = modiUser;
		this.version = version;
	}

	// Property accessors
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "bankorgId", column = @Column(name = "BANKORG_ID", nullable = false, precision = 10, scale = 0)),
			@AttributeOverride(name = "pcId", column = @Column(name = "PC_ID", nullable = false, length = 20)) })
	public BsPcId getId() {
		return this.id;
	}

	public void setId(BsPcId id) {
		this.id = id;
	}

	@Column(name = "PC_NAME", length = 40)
	public String getPcName() {
		return this.pcName;
	}

	public void setPcName(String pcName) {
		this.pcName = pcName;
	}

	@Column(name = "IP_ADDR", length = 40)
	public String getIpAddr() {
		return this.ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
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