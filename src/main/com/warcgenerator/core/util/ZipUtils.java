package com.warcgenerator.core.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Observable;
import java.util.Observer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils extends Observable {
    private String sourceDirPath;
    private String zipFilePath;

    public ZipUtils(Observer observer, String sourceDirPath, String zipFilePath) {
        this.sourceDirPath = sourceDirPath;
        this.zipFilePath = zipFilePath;
        this.addObserver(observer);
    }

    /**
     * Compress the file
     * @throws IOException
     */
    public void pack() throws IOException {
        Path p = Files.createFile(Paths.get(zipFilePath));
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
            Path filePath = Paths.get(sourceDirPath);
            Files.walk(filePath)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(filePath.relativize(path).toString());
                        try {
                            zs.putNextEntry(zipEntry);
                            Files.copy(path, zs);
                            zs.closeEntry();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
        setChanged();
        notifyObservers();
    }
}
