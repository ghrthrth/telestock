package com.example.massage_parlor;

import okhttp3.MediaType;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import okhttp3.RequestBody;
import java.io.IOException;

public class ProgressRequestBody extends RequestBody {
    private static final int DEFAULT_BUFFER_SIZE = 2048;

    private final RequestBody requestBody;
    private final ProgressListener progressListener;

    public interface ProgressListener {
        void onProgress(long bytesWritten, long contentLength);
    }

    public ProgressRequestBody(RequestBody requestBody, ProgressListener progressListener) {
        this.requestBody = requestBody;
        this.progressListener = progressListener;
    }

    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        long contentLength = requestBody.contentLength();

        // Создаем BufferedSink один раз
        BufferedSink bufferedSink = Okio.buffer(sink);

        long bytesWritten = 0;
        // Записываем данные в sink
        requestBody.writeTo(bufferedSink);

        // После записи всех данных, вызываем flush
        bufferedSink.flush();

        // Обновляем прогресс
        bytesWritten += contentLength;  // Прогресс будет 100% после завершения записи
        progressListener.onProgress(bytesWritten, contentLength);
    }

    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    public static ProgressRequestBody create(RequestBody requestBody, ProgressListener listener) {
        return new ProgressRequestBody(requestBody, listener);
    }
}