package com.dada.marsframework.network.download;

public interface DownloadProgressListener {
    void onProgressChange(long bytesRead, long contentLength, boolean done);
}