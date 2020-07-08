package com.noknown.framework.cache.file;

/**
 * @author guodong
 */
public class PackedFile {

	private String md5;

	private int seek;

	private int size;

	private long packedKey;

	public String getMd5() {
		return md5;
	}

	public PackedFile setMd5(String md5) {
		this.md5 = md5;
		return this;
	}

	public int getSeek() {
		return seek;
	}

	public PackedFile setSeek(int seek) {
		this.seek = seek;
		return this;
	}

	public int getSize() {
		return size;
	}

	public PackedFile setSize(int size) {
		this.size = size;
		return this;
	}

	public long getPackedKey() {
		return packedKey;
	}

	public PackedFile setPackedKey(long packedKey) {
		this.packedKey = packedKey;
		return this;
	}
}
