package com.noknown.framework.cache.file;

import java.util.ArrayList;
import java.util.List;

/**
 * @author guodong
 */
public class ReleaseList {

	private List<String> keys;

	private List<Long> packedKeys;

	public void addKey(String key) {
		if (keys == null) {
			keys = new ArrayList<>();
		}
		keys.add(key);
	}

	public void addPackedKey(long key) {
		if (packedKeys == null) {
			packedKeys = new ArrayList<>();
		}
		packedKeys.add(key);
	}

	public List<String> getKeys() {
		return keys;
	}

	public ReleaseList setKeys(List<String> keys) {
		this.keys = keys;
		return this;
	}

	public List<Long> getPackedKeys() {
		return packedKeys;
	}

	public ReleaseList setPackedKeys(List<Long> packedKeys) {
		this.packedKeys = packedKeys;
		return this;
	}
}
