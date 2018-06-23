package taller2.ataller2.services;

public enum EmotionType {
    LIKE(0),
    DONT_LIKE(1),
    FUN(2),
    BORE(3);

    private final int value;

    EmotionType(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getEmotionServer () {
        switch(value) {
            case 0:
                return "me gusta";
            case 1:
                return "no me gusta";
            case 2:
                return "me divierte";
            case 3:
                return "me aburre";
        }
        return "me gusta";
    }

    public static taller2.ataller2.services.EmotionType fromInteger(int x) {
        switch(x) {
            case 0:
                return EmotionType.LIKE;
            case 1:
                return EmotionType.DONT_LIKE;
            case 2:
                return EmotionType.FUN;
            case 3:
                return EmotionType.BORE;
        }
        return EmotionType.LIKE;
    }
}
