package taller2.ataller2.services.notifications;


import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import taller2.ataller2.networking.DownloadCallback;
import taller2.ataller2.networking.HttpMethodType;
import taller2.ataller2.networking.NetworkObject;
import taller2.ataller2.networking.NetworkResult;
import taller2.ataller2.services.ServiceLocator;
import taller2.ataller2.services.facebook.FacebookService;

public class FirebaseNotificationService extends FirebaseInstanceIdService implements NotificationService {

    private static final String URL_PUT_INSTANCE_ID = "http://34.237.197.99:9000/api/v1/mobile/users";
    private static final String TOKEN_HEADER_NAME = "appToken";
    private static final int READ_TIMEOUT_MS = 3000;
    private static final int CONNECT_TIMEOUT_MS = 3000;
    private static final int STREAM_MAX_SIZE = 4096;

    private String token = "";

    @Override
    public void scheduleSendInstanceId() {

    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public void refreshToken() {
        onTokenRefresh();
    }

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        token = FirebaseInstanceId.getInstance().getToken();


        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken);
    }

    @Override
    public void sendInstanceIdToken(String token, final SendInstanceIdCallback callback) {
        SendFirebaseInstanceIdTask sendInstanceIdTask = new SendFirebaseInstanceIdTask(new DownloadCallback<NetworkResult>() {
            @Override
            public void onResponseReceived(@NonNull NetworkResult result) {
                if (result.mException == null) {
                    callback.onSuccess();
                } else {
                    callback.onError("Falló en enviar el instance Id token al backoffice");
                }
            }

            @Override
            public NetworkInfo getActiveNetworkInfo(Context context) {
                ConnectivityManager connectivityManager =
                        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                return networkInfo;
            }

            @Override
            public void onProgressUpdate(int progressCode, int percentComplete) {

            }

            @Override
            public void onFinishDownloading() {

            }
        });
        NetworkObject networkObject = createUpdateRequestNetworkObject(token);
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
            sendInstanceIdTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, networkObject);
        } else {
            sendInstanceIdTask.execute(networkObject);
        }
    }

    private NetworkObject createUpdateRequestNetworkObject(String token) {
        String postBody = createSendTokenRequestBody(token);
        Map<String, String> requestProperties = createRequestProperties(token);
        String uri = String.format(URL_PUT_INSTANCE_ID);
        NetworkObject networkObject = new NetworkObject(uri, HttpMethodType.PUT, requestProperties, postBody);
        networkObject.setAuthToken(ServiceLocator.get(FacebookService.class).getAuthToken());
        return networkObject;
    }

    private String createSendTokenRequestBody(String token) {
        String body = "";
        try {
            JSONObject bodyJson = new JSONObject();
            bodyJson.put(TOKEN_HEADER_NAME, token);
            body = bodyJson.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return body;
    }

    private Map<String, String> createRequestProperties(String token) {
        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put(TOKEN_HEADER_NAME, token);
        return requestProperties;
    }

    public class SendFirebaseInstanceIdTask extends AsyncTask<NetworkObject, Integer, NetworkResult> {

        private DownloadCallback<NetworkResult> mCallback;

        public SendFirebaseInstanceIdTask(DownloadCallback<NetworkResult> callback) {
            setCallback(callback);
        }

        void setCallback(DownloadCallback<NetworkResult> callback) {
            mCallback = callback;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected NetworkResult doInBackground(NetworkObject... networkObjects) {
            NetworkResult result = null;
            if (!isCancelled() && networkObjects != null && networkObjects.length > 0) {
                NetworkObject networkObject = networkObjects[0];
                result = downloadUrl(networkObject);
            }
            return result;
        }

        @Override
        protected void onPostExecute(NetworkResult result) {
            if (mCallback != null) {
                if (result != null) {
                    mCallback.onResponseReceived(result);
                }
                mCallback.onFinishDownloading();
            }
        }

        @Override
        protected void onCancelled(NetworkResult result) {
        }


        private NetworkResult downloadUrl(NetworkObject networkObject) {
            InputStream stream = null;
            HttpURLConnection connection = null;
            NetworkResult result = null;
            try {
                String authToken = ServiceLocator.get(FacebookService.class).getAuthToken();
                while (authToken == null || authToken.equals("")) {
                    Thread.sleep(1000);
                    authToken = ServiceLocator.get(FacebookService.class).getAuthToken();
                }

                URL url = new URL(networkObject.getURL());

                connection = (HttpURLConnection) url.openConnection();

                AddRequestProperties(connection, networkObject);

                connection.addRequestProperty("authorization", authToken);

                // Timeout for reading InputStream
                connection.setReadTimeout(READ_TIMEOUT_MS);
                // Timeout for connection.connect()
                connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
                // For this use case, set HTTP method to GET.
                connection.setRequestMethod(networkObject.getHttpMethod());
                // Already true by default but setting just in case; needs to be true since this request
                // is carrying an input (response) body.
                connection.setDoInput(true);

                if ((networkObject.getHttpMethod().equals("POST")) || (networkObject.getHttpMethod().equals("PUT"))) {
                    // set the connection content-type as JSON, meaning we are sending JSON data.
                    connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                    // Send POST data.
                    publishProgress(DownloadCallback.Progress.DOWNLOADING);
                    DataOutputStream printout = new DataOutputStream(connection.getOutputStream());
                    printout.write(networkObject.getPostData().getBytes("UTF-8"));
                    printout.flush();
                    printout.close();
                }

                publishProgress(DownloadCallback.Progress.CONNECT_SUCCESS);

                // Open communications link (network traffic occurs here).
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    result = new NetworkResult(new Exception("HTTP error code: " + responseCode));
                }
                // Retrieve the response body as an InputStream.
                stream = connection.getInputStream();
                publishProgress(DownloadCallback.Progress.DOWNLOADING, 0);
                if (stream != null) {
                    // Converts Stream to String with max length of contentLength.
                    // The content length may be null if the server uses Chunked Transfer Encoding to send data
                    String contentLength = connection.getHeaderField("Content-Length");
                    long contentLengthLong = contentLength == null ? STREAM_MAX_SIZE : Long.parseLong(contentLength);
                    result = new NetworkResult(readStream(stream, (int) contentLengthLong), networkObject.getURL());
                }
            } catch(Exception e) {
                result = new NetworkResult(e);
            }
            finally {
                // Get response headers
                List<String> responseHeaders = networkObject.getResponseHeaders();
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
            }
            publishProgress(DownloadCallback.Progress.DONE);
            return result;
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



/*
    public static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    OkHttpClient mClient = new OkHttpClient();
    @SuppressLint("StaticFieldLeak")
    public void sendMessage(final JSONArray recipients, final String title, final String body, final String icon, final String message) {
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    JSONObject root = new JSONObject();
                    JSONObject notification = new JSONObject();
                    notification.put("body", body);
                    notification.put("title", title);
                    notification.put("icon", icon);

                    JSONObject data = new JSONObject();
                    data.put("message", message);
                    root.put("notification", notification);
                    root.put("data", data);
                    root.put("registration_ids", recipients);

                    String result = postToFCM(root.toString());
                    //Log.d(TAG, "Result: " + result);
                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                try {
                    JSONObject resultJson = new JSONObject(result);
                    int success, failure;
                    success = resultJson.getInt("success");
                    failure = resultJson.getInt("failure");
                    //Toast.makeText(getCurrentActivity, "Message Success: " + success + "Message Failed: " + failure, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(getCurrentActivity(), "Message Failed, Unknown error occurred.", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    String postToFCM(String bodyString) throws IOException {
        RequestBody body = RequestBody.create(JSON, bodyString);
        Request request = new Request.Builder()
                .url(FCM_MESSAGE_URL)
                .post(body)
                .addHeader("Authorization", "key=" + SERVER_KEY)
                .build();
        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }
    */

}
