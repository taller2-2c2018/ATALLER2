package taller2.ataller2.networking;


public enum HttpMethodType {
    GET(0),
    POST(1),
    PUT(2),
    DELETE(3);

    private final int value;
    private HttpMethodType(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static HttpMethodType fromInteger(int x) {
        switch(x) {
            case 0:
                return HttpMethodType.GET;
            case 1:
                return HttpMethodType.POST;
            case 2:
                return HttpMethodType.PUT;
            case 3:
                return HttpMethodType.DELETE;
        }
        return HttpMethodType.GET;
    }
}
