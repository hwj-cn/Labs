package org.hwj.FileManager.file.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class FileSizeFinder extends RecursiveTask<Long> {
    final File file;

    public FileSizeFinder(final File theFile) {
        file = theFile;
    }

    @Override
    public Long compute() {
        long size = 0;
        if (file.isFile()) {
            size = file.length();
        } else {
            final File[] children = file.listFiles();
            if (children != null) {
                List<ForkJoinTask<Long>> tasks = new ArrayList<ForkJoinTask<Long>>();
                for (final File child : children) {
                    if (child.isFile()) {
                        size += child.length();
                    } else {
                        tasks.add(new FileSizeFinder(child));
                    }
                }
                for (final ForkJoinTask<Long> task : invokeAll(tasks)) {
                    size += task.join();
                }
            }
        }
        return size;
    }
}