package taller2.ataller2.services.notifications;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import taller2.ataller2.networking.NetworkObject;
import taller2.ataller2.networking.NetworkResult;
import taller2.ataller2.services.ServiceLocator;
import taller2.ataller2.services.facebook.FacebookService;

public class SendFirebaseTokenThread implements Runnable {
    private static final int READ_TIMEOUT_MS = 3000;
    private static final int CONNECT_TIMEOUT_MS = 3000;
    private static final int STREAM_MAX_SIZE = 4096;

    private final SendInstanceIdCallback mCallback;
    private final NetworkObject mNetworkObject;

    public SendFirebaseTokenThread(NetworkObject networkObject, SendInstanceIdCallback callback) {
        mNetworkObject = networkObject;
        mCallback = callback;
    }
    @Override
    public void run() {
        InputStream stream = null;
        HttpURLConnection connection = null;
        NetworkResult result = null;
        try {
            String authToken = ServiceLocator.get(FacebookService.class).getAuthToken();
            while (authToken == null || authToken.equals("")) {
                Thread.sleep(1000);
                authToken = ServiceLocator.get(FacebookService.class).getAuthToken();
            }

            URL url = new URL(mNetworkObject.getURL());

            connection = (HttpURLConnection) url.openConnection();

            AddRequestProperties(connection, mNetworkObject);

            connection.addRequestProperty("authorization", authToken);

            // Timeout for reading InputStream
            connection.setReadTimeout(READ_TIMEOUT_MS);
            // Timeout for connection.connect()
            connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
            // For this use case, set HTTP method to GET.
            connection.setRequestMethod(mNetworkObject.getHttpMethod());
            // Already true by default but setting just in case; needs to be true since this request
            // is carrying an input (response) body.
            connection.setDoInput(true);

            if ((mNetworkObject.getHttpMethod().equals("POST")) || (mNetworkObject.getHttpMethod().equals("PUT"))) {
                // set the connection content-type as JSON, meaning we are sending JSON data.
                connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                // Send POST data.
                DataOutputStream printout = new DataOutputStream(connection.getOutputStream());
                printout.write(mNetworkObject.getPostData().getBytes("UTF-8"));
                printout.flush();
                printout.close();
            }

            // Open communications link (network traffic occurs here).
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                result = new NetworkResult(new Exception("HTTP error code: " + responseCode));
            }
            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();
            if (stream != null) {
                // Converts Stream to String with max length of contentLength.
                // The content length may be null if the server uses Chunked Transfer Encoding to send data
                String contentLength = connection.getHeaderField("Content-Length");
                long contentLengthLong = contentLength == null ? STREAM_MAX_SIZE : Long.parseLong(contentLength);
                result = new NetworkResult(readStream(stream, (int) contentLengthLong), mNetworkObject.getURL());
            }
        } catch(Exception e) {
            result = new NetworkResult(e);
        }
        finally {
            // Get response headers
            List<String> responseHeaders = mNetworkObject.getResponseHeaders();
            for (String header: responseHeaders) {
                String headerValue = connection.getHeaderField(header);
                result.mResponseHeaders.put(header, headerValue);
            }
            // Close Stream and disconnect HTTP connection.
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                result = new NetworkResult(e);
            }
            if (connection != null) {
                connection.disconnect();
            }

            if (result.mException == null) {
                mCallback.onSuccess();
            } else {
                mCallback.onError("Falló en enviar el instance Id token al backoffice");
            }
        }
    }

    private void AddRequestProperties(HttpURLConnection conn, NetworkObject networkObject) {
        Map<String, String> requestProperties = networkObject.GetRequestProperties();
        if (requestProperties != null) {
            for (Map.Entry<String, String> entry : requestProperties.entrySet()) {
                conn.addRequestProperty(entry.getKey(), entry.getValue());
            }
        }
    }

    private String readStream(InputStream stream, int maxReadSize)
            throws IOException {
        Reader reader;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] rawBuffer = new char[maxReadSize];
        int readSize;
        StringBuilder buffer = new StringBuilder();
        while (((readSize = reader.read(rawBuffer)) != -1) && maxReadSize > 0) {
            if (readSize > maxReadSize) {
                readSize = maxReadSize;
            }
            buffer.append(rawBuffer, 0, readSize);
            maxReadSize -= readSize;
        }
        return buffer.toString();
    }
}
