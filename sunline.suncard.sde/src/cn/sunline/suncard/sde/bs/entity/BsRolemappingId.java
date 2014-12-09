package cn.sunline.suncard.sde.bs.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * BsRolemappingId entity. @author MyEclipse Persistence Tools
 */
@Embeddable
public class BsRolemappingId implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long bankorgId;
	private String pcId;
	private String roleId;
	private String functionId;

	// Constructors

	/** default constructor */
	public BsRolemappingId() {
	}

	/** full constructor */
	public BsRolemappingId(Long bankorgId, String pcId, String roleId,
			String functionId) {
		this.bankorgId = bankorgId;
		this.pcId = pcId;
		this.roleId = roleId;
		this.functionId = functionId;
	}

	// Property accessors

	@Column(name = "BANKORG_ID", nullable = false, precision = 10, scale = 0)
	public Long getBankorgId() {
		return this.bankorgId;
	}

	public void setBankorgId(Long bankorgId) {
		this.bankorgId = bankorgId;
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

	@Column(name = "FUNCTION_ID", nullable = false, length = 10)
	public String getFunctionId() {
		return this.functionId;
	}

	public void setFunctionId(String functionId) {
		this.functionId = functionId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof BsRolemappingId))
			return false;
		BsRolemappingId castOther = (BsRolemappingId) other;

		return ((this.getBankorgId() == castOther.getBankorgId()) || (this
				.getBankorgId() != null && castOther.getBankorgId() != null && this
				.getBankorgId().equals(castOther.getBankorgId())))
				&& ((this.getPcId() == castOther.getPcId()) || (this.getPcId() != null
						&& castOther.getPcId() != null && this.getPcId()
						.equals(castOther.getPcId())))
				&& ((this.getRoleId() == castOther.getRoleId()) || (this
						.getRoleId() != null && castOther.getRoleId() != null && this
						.getRoleId().equals(castOther.getRoleId())))
				&& ((this.getFunctionId() == castOther.getFunctionId()) || (this
						.getFunctionId() != null
						&& castOther.getFunctionId() != null && this
						.getFunctionId().equals(castOther.getFunctionId())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getBankorgId() == null ? 0 : this.getBankorgId().hashCode());
		result = 37 * result
				+ (getPcId() == null ? 0 : this.getPcId().hashCode());
		result = 37 * result
				+ (getRoleId() == null ? 0 : this.getRoleId().hashCode());
		result = 37
				* result
				+ (getFunctionId() == null ? 0 : this.getFunctionId()
						.hashCode());
		return result;
	}

}