package com.jj.investigation.openfire.utils;

import okhttp3.MediaType;
import okhttp3.RequestBody;


public class RequestBodyUtils {
    public static RequestBody toRequestBody(String value) {
        if (!Utils.isNull(value)) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), value);
            return requestBody;
        }
        return null;
    }
}
