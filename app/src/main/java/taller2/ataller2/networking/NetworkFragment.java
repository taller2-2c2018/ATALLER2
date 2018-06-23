package taller2.ataller2.networking;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of headless Fragment that runs an AsyncTask to fetch data from the network.
 * https://developer.android.com/training/basics/network-ops/connecting.html#HeadlessFragment
 */
public class NetworkFragment extends Fragment {
    private static final int READ_TIMEOUT_MS = 3000;
    private static final int CONNECT_TIMEOUT_MS = 3000;
    private static final int STREAM_MAX_SIZE = 4096;

    private static final String AUTH_KEY_FIELD = "Authorization";
    private static final String AUTH_KEY_ID = "facebookUserId";

    private static final String TAG = "NetworkFragment";

    private static final String DATA_OBJECT = "DataObject";

    private static NetworkObject sNetworkObject;

    private DownloadCallback mCallback;
    private DownloadTask mDownloadTask;

    /**
     * Static initializer for NetworkFragment that sets the URL of the host it will be downloading
     * from.
     */
    public static NetworkFragment getInstance(FragmentManager fragmentManager, NetworkObject networkObject) {
        // Recover NetworkFragment in case we are re-creating the Activity due to a config change.
        // This is necessary because NetworkFragment might have a task that began running before
        // the config change occurred and has not finished yet.
        // The NetworkFragment is recoverable because it calls setRetainInstance(true).
        sNetworkObject = networkObject;
        NetworkFragment networkFragment = (NetworkFragment) fragmentManager.findFragmentByTag(NetworkFragment.TAG);

        if (networkFragment == null) {
            networkFragment = new NetworkFragment();
            Bundle args = new Bundle();
            args.putParcelable(DATA_OBJECT, networkObject);
            networkFragment.setArguments(args);
            fragmentManager.beginTransaction().add(networkFragment, TAG).commit();
        } else {
            updateFragmentNetworkObject(networkFragment, networkObject);
        }
        // execute immediately
        fragmentManager.executePendingTransactions();

        return networkFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sNetworkObject = getArguments().getParcelable(DATA_OBJECT);

        // Retain this Fragment across configuration changes in the host Activity.
        setRetainInstance(true);
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Host Activity would handle callbacks from task if we force downloadcallbacks to be activities
        // mCallback = (DownloadCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Clear reference to host Activity to avoid memory leak.
        mCallback = null;
    }

    @Override
    public void onDestroy() {
        // Cancel task when Fragment is destroyed.
        cancelDownload();
        super.onDestroy();
    }

    /**
     * Start non-blocking execution of DownloadTask.
     */
    @SuppressWarnings("unchecked")
    public void startDownload(@NonNull DownloadCallback callback) {
        mCallback = callback;
        cancelDownload();
        mDownloadTask = new DownloadTask(mCallback);
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
            mDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, sNetworkObject);
        } else {
            mDownloadTask.execute(sNetworkObject);
        }
    }

    /**
     * Cancel (and interrupt if necessary) any ongoing DownloadTask execution.
     */
    public void cancelDownload() {
        if (mDownloadTask != null) {
            mDownloadTask.cancel(true);
        }
    }

    private static void updateFragmentNetworkObject(NetworkFragment fragment, NetworkObject networkObject) {
        Bundle bundle = fragment.getArguments();
        bundle.putParcelable(DATA_OBJECT, networkObject);
    }

    /**
     * Implementation of AsyncTask designed to fetch data from the network.
     */
    private class DownloadTask extends AsyncTask<NetworkObject, Integer, NetworkResult> {

        private DownloadCallback<NetworkResult> mCallback;

        public DownloadTask(DownloadCallback<NetworkResult> callback) {
            setCallback(callback);
        }

        void setCallback(DownloadCallback<NetworkResult> callback) {
            mCallback = callback;
        }

        /**
         * Cancel background network operation if we do not have network connectivity.
         */
        @Override
        protected void onPreExecute() {
            Activity activity = getActivity();
            if ((mCallback != null) && (activity != null)) {
                NetworkInfo networkInfo = mCallback.getActiveNetworkInfo(activity);
                if (networkInfo == null || !networkInfo.isConnected() ||
                        (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                                && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
                    // If no connectivity, cancel task and update Callback with null data.
                    mCallback.onResponseReceived(new NetworkResult(new NoConnectionException()));
                    cancel(true);
                }
            }
        }

        /**
         * Defines work to perform on the background thread.
         */
        @Override
        protected NetworkResult doInBackground(NetworkObject... networkObjects) {
            NetworkResult result = null;
            if (!isCancelled() && networkObjects != null && networkObjects.length > 0) {
                NetworkObject networkObject = networkObjects[0];
                result = downloadUrl(networkObject);
            }
            return result;
        }

        /**
         * Updates the DownloadCallback with the result.
         */
        @Override
        protected void onPostExecute(NetworkResult result) {
            if (mCallback != null) {
                if (result != null) {
                    mCallback.onResponseReceived(result);
                }
                mCallback.onFinishDownloading();
            }
        }

        /**
         * Override to add special behavior for cancelled AsyncTask.
         */
        @Override
        protected void onCancelled(NetworkResult result) {
        }

        /**
         * Given a URL, sets up a connection and gets the HTTP response body from the server.
         * If the network request is successful, it returns the response body in String form. Otherwise,
         * it will throw an IOException.
         */
        private NetworkResult downloadUrl(NetworkObject networkObject) {
            InputStream stream = null;
            HttpURLConnection connection = null;
            NetworkResult result = null;
            try {
                URL url = new URL(networkObject.getURL());

                connection = (HttpURLConnection) url.openConnection();

                AddRequestProperties(connection, networkObject);

                connection.addRequestProperty(AUTH_KEY_FIELD, networkObject.getAuthToken());
                connection.addRequestProperty(AUTH_KEY_ID,networkObject.getFacebookID());

                // Timeout for reading InputStream
                connection.setReadTimeout(READ_TIMEOUT_MS);
                // Timeout for connection.connect()
                connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
                // For this use case, set HTTP method to GET.
                connection.setRequestMethod(networkObject.getHttpMethod());
                // Already true by default but setting just in case; needs to be true since this request
                // is carrying an input (response) body.
                connection.setDoInput(true);

                if (networkObject.getHttpMethod().equals("DELETE")) {
                    connection.setInstanceFollowRedirects(false);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestProperty("charset", "utf-8");
                    connection.setUseCaches (false);
                }

                if ((networkObject.getHttpMethod().equals("POST")) || (networkObject.getHttpMethod().equals("PUT"))) {
                    // set the connection content-type as JSON, meaning we are sending JSON data.
                    // Send POST data.
                    publishProgress(DownloadCallback.Progress.DOWNLOADING);
                    if (networkObject.getPostData() != null) {
                        connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                        DataOutputStream printout = new DataOutputStream(connection.getOutputStream());
                        printout.write(networkObject.getPostData().getBytes("UTF-8"));
                        printout.flush();
                        printout.close();
                    }
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

        /**
         * Converts the contents of an InputStream to a String.
         */
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
}
