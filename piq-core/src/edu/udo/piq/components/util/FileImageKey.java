package edu.udo.piq.components.util;

import java.io.File;
import java.net.URI;

public class FileImageKey {
	
	private final File file;
	
	public FileImageKey(File file) {
		this.file = file;
	}
	
	public FileImageKey(URI uri) {
		file = new File(uri);
	}
	
	public FileImageKey(String pathName) {
		file = new File(pathName);
	}
	
	@Override
	public int hashCode() {
		return file.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof FileImageKey) {
			FileImageKey other = (FileImageKey) obj;
			return file.equals(other.file);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return file.getAbsolutePath();
	}
	
}