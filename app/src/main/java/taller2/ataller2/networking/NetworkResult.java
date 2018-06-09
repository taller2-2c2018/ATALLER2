package taller2.ataller2.networking;

import android.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Wrapper class that serves as a union of a result value and an exception. When the download
 * task has completed, either the result value or exception can be a non-null value.
 * This allows you to pass exceptions to the UI thread that were thrown during doInBackground().
 */
public class NetworkResult {
    public String mResultValue;
    public Map<String, String> mHeaders;
    public Map<String, String> mResponseHeaders;
    public Exception mException;
    public String mUrl;

    public NetworkResult(String resultValue, String url) {
        mResultValue = resultValue;
        mUrl = url;
        mResponseHeaders = new HashMap<>();
    }

    public NetworkResult(Exception exception) {
        mResponseHeaders = new HashMap<>();
        mException = exception;
    }

    @Override
    public String toString() {
        if (mResultValue != null) {
            return mResultValue;
        } else if (mException != null) {
            return mException.getMessage();
        }
        return "";
    }
}