package com.app.mschooling.utils;

import android.content.ContentResolver;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class MSchoolingUriRequestBody extends RequestBody {
    private final MediaType contentType;
    private final ContentResolver contentResolver;
    private final Uri uri;

    public MSchoolingUriRequestBody(MediaType contentType, ContentResolver contentResolver, Uri uri) {
        if (uri == null) throw new NullPointerException("uri == null");
        this.contentType = contentType;
        this.contentResolver = contentResolver;
        this.uri = uri;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return contentType;
    }

    @Override
    public long contentLength() throws IOException {
        return -1;
    }

    @Override
    public void writeTo(@NonNull BufferedSink sink) throws IOException, FileNotFoundException {
        Source source = null;
        try {
            source = Okio.source(contentResolver.openInputStream(uri));
            sink.writeAll(source);
        } finally {
            Util.closeQuietly(source);
        }
    }
}
