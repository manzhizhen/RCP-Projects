package cn.sunline.suncard.sde.bs.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * BsUsermappingId entity. @author MyEclipse Persistence Tools
 */
@Embeddable
public class BsUsermappingId implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long bankorgId;
	private String userId;
	private String pcId;
	private String roleId;

	// Constructors

	/** default constructor */
	public BsUsermappingId() {
	}

	/** full constructor */
	public BsUsermappingId(Long bankorgId, String userId, String pcId,
			String roleId) {
		this.bankorgId = bankorgId;
		this.userId = userId;
		this.pcId = pcId;
		this.roleId = roleId;
	}

	// Property accessors

	@Column(name = "BANKORG_ID", nullable = false, precision = 10, scale = 0)
	public Long getBankorgId() {
		return this.bankorgId;
	}

	public void setBankorgId(Long bankorgId) {
		this.bankorgId = bankorgId;
	}

	@Column(name = "USER_ID", nullable = false, length = 10)
	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Column(name = "PC_ID", nullable = false, length = 20)
	public String getPcId() {
		return this.pcId;
	}

	public void setPcId(String pcId) {
		this.pcId = pcId;
	}

	@Column(name = "ROLE_ID", nullable = false, length = 10)
	public String getRoleId() {
		return this.roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof BsUsermappingId))
			return false;
		BsUsermappingId castOther = (BsUsermappingId) other;

		return ((this.getBankorgId() == castOther.getBankorgId()) || (this
				.getBankorgId() != null && castOther.getBankorgId() != null && this
				.getBankorgId().equals(castOther.getBankorgId())))
				&& ((this.getUserId() == castOther.getUserId()) || (this
						.getUserId() != null && castOther.getUserId() != null && this
						.getUserId().equals(castOther.getUserId())))
				&& ((this.getPcId() == castOther.getPcId()) || (this.getPcId() != null
						&& castOther.getPcId() != null && this.getPcId()
						.equals(castOther.getPcId())))
				&& ((this.getRoleId() == castOther.getRoleId()) || (this
						.getRoleId() != null && castOther.getRoleId() != null && this
						.getRoleId().equals(castOther.getRoleId())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getBankorgId() == null ? 0 : this.getBankorgId().hashCode());
		result = 37 * result
				+ (getUserId() == null ? 0 : this.getUserId().hashCode());
		result = 37 * result
				+ (getPcId() == null ? 0 : this.getPcId().hashCode());
		result = 37 * result
				+ (getRoleId() == null ? 0 : this.getRoleId().hashCode());
		return result;
	}

}