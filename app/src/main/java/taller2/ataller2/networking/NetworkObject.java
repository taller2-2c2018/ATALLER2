package taller2.ataller2.networking;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NetworkObject implements Parcelable {

    public static final Creator<NetworkObject> CREATOR = new Creator<NetworkObject>() {
        @Override
        public NetworkObject createFromParcel(Parcel in) {
            return new NetworkObject(in);
        }

        @Override
        public NetworkObject[] newArray(int size) {
            return new NetworkObject[size];
        }
    };

    private final String mUrl;
    private String mAuthToken;
    private String mFacebookID;
    private String mContentType;

    private HttpMethodType mHttpMethod;
    private String mPostData;
    private Map<String, String> mRequestProperties;
    private List<String> mResponseHeaders;

    public NetworkObject(String URL, HttpMethodType httpMethod) {
        this.mUrl = URL;
        this.mAuthToken = "";
        this.mFacebookID = "";
        this.mHttpMethod = httpMethod;
        mResponseHeaders = new ArrayList<>();
    }

    public NetworkObject(String URL, HttpMethodType httpMethod, Map<String, String> requestProperties) {
        this(URL, httpMethod);
        this.mRequestProperties = requestProperties;
    }

    public NetworkObject(String URL, HttpMethodType httpMethod, String postData) {
        this(URL, httpMethod);
        this.mPostData = postData;
    }

    public NetworkObject(String URL, HttpMethodType httpMethod, Map<String, String> requestProperties, String postData) {
        this(URL, httpMethod, requestProperties);
        this.mPostData = postData;
    }

    public void setAuthToken(String authToken) {
        this.mAuthToken = authToken;
    }

    public void setFacebookID (String facebookID) {this.mFacebookID = facebookID; }

    public void setContentType (String contentType) {this.mContentType = contentType;}

    public String getAuthToken() {
        return this.mAuthToken;
    }

    public String getFacebookID () {return this.mFacebookID; }

    public String getURL() {
        return mUrl;
    }

    public List<String> getResponseHeaders() {
        return mResponseHeaders;
    }

    public void setResponseHeaders(List<String> responseHeaders) {
        this.mResponseHeaders = responseHeaders;
    }

    public String getHttpMethod() {
        switch (mHttpMethod) {
            case GET:
                return "GET";
            case PUT:
                return "PUT";
            case POST:
                return "POST";
            case DELETE:
                return "DELETE";
        }

        return "N/A";
    }

    public String getPostData() {
        return mPostData;
    }

    public Map<String, String> GetRequestProperties() {
        return mRequestProperties;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mUrl);
        //dest.writeString(mContentType);
        dest.writeString(mFacebookID);
        dest.writeString(mAuthToken);
        dest.writeInt(mHttpMethod.getValue());
        dest.writeString(mPostData);
        dest.writeMap(mRequestProperties);
        dest.writeList(mResponseHeaders);
    }

    private NetworkObject(Parcel in) {
        mUrl = in.readString();
        //mContentType = in.readString();
        mFacebookID = in.readString();
        mAuthToken = in.readString();
        mHttpMethod = HttpMethodType.fromInteger(in.readInt());
        mPostData = in.readString();
        in.readMap(mRequestProperties, String.class.getClassLoader());
        in.readList(mResponseHeaders, String.class.getClassLoader());
    }
}
