package org.sirius.core.pull;

import org.sirius.domain.Config;

public class PullKey {
	
	private String key;
	
	private PullKeyType type;
	
	public PullKey(String key, PullKeyType type) {
		this.key = key;
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public PullKeyType getType() {
		return type;
	}

	public void setType(PullKeyType type) {
		this.type = type;
	}
	
	public boolean matchs(Config config){
		if(type == PullKeyType.MD5){
			return this.key.equals(config.getMd5());
		}else{
			return this.key.equals(config.getNamespace()+"_"+config.getName());
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PullKey other = (PullKey) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
}
