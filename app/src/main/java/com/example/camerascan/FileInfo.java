package com.example.camerascan;

import java.io.File;

public class FileInfo {
    private File file;
    private String name;


    public FileInfo() {
    }

    public FileInfo(File file,String name ) {
        this.name = name;
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "name='" + name + '\'' +
                ", file=" + file +
                '}';
    }
}
