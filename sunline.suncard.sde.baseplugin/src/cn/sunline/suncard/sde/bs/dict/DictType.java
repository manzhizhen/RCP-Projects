package cn.sunline.suncard.sde.bs.dict;

import java.util.ArrayList;
import java.util.List;

public class DictType {

	private String dictTypeId;
	
	private String dictTypeName;
	
	private List<DictEntry> dictEntries = new ArrayList<DictEntry>();

	public String getDictTypeId() {
		return dictTypeId;
	}

	public void setDictTypeId(String dictTypeId) {
		this.dictTypeId = dictTypeId;
	}

	public String getDictTypeName() {
		return dictTypeName;
	}

	public void setDictTypeName(String dictTypeName) {
		this.dictTypeName = dictTypeName;
	}

	public List<DictEntry> getDictEntries() {
		return dictEntries;
	}

	public void setDictEntries(List<DictEntry> dictEntries) {
		this.dictEntries = dictEntries;
	}
}
