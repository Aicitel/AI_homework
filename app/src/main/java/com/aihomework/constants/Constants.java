package com.aihomework.constants;

public class Constants {
    //public static int
    public final static int MOBILE_LARGE_TEXT_SIZE =28;
    public final static int MOBILE_SMALL_TEXT_SIZE =18;
    public final static int PAD_LARGE_TEXT_SIZE =38;
    public final static int PAD_SMALL_TEXT_SIZE =28;
    public static boolean tulingUsed = false;
    public final static int INF=100;
    public final static String SESSION_ID = "1234";
    public final static String CONSTANT_SUBJECT = "subject";
    public final static String CONSTANT_DATA = "orders";
    public final static int[] CONSTANT_SUBJECTS = {0,1,2};
    public final static int[] questionCounts = {12,7,7};
    public final static int CONSTANT_CHINESE = 0;
    public final static int CONSTANT_MATH = 1;
    public final static int CONSTANT_ENGLISH = 2;

    public final static int ROUND_COUNT = 2;
    public final static int SOCKET_TIME_OUT = 5000;
    public final static String MAGIC_ANSEWER = "magic correct!";

    public final static int SQUARE_UPPER = 100;
    public final static int TIME_LIMIT_MS = 10000;

    public final static int QUESTION_TYPE_CHOOSE = 0;
    public final static int QUESTION_TYPE_FILLIN = 1;
    public final static int QUESTION_TYPE_WRITE = 2;

    public final static int HW_SINGLE_WRITE = 0;
    public final static int HW_FORMULA = 1;
    public final static int HW_ENGLISH_WORD= 2;

    public final static int QUESTION_TYPE_READ = 0;
    public final static int QUESTION_TYPE_COPY = 1;

    public final static int QUESTION_TYPE_STEM = -1;
    public final static int QUESTION_TYPE_CN_READ = 0;
    public final static int QUESTION_TYPE_CN_COPY = 1;
    public final static int QUESTION_TYPE_CN_DICT = 2;
    public final static int QUESTION_TYPE_CN_PARTREAD = 3;
    public final static int QUESTION_TYPE_CN_CHOICE = 4;
    public final static int QUESTION_TYPE_MA_CAL = 1005;
    public final static int QUESTION_TYPE_MA_CHOICE = 1006;
    public final static int QUESTION_TYPE_MA_FILLIN = 1007;
    public final static int QUESTION_TYPE_EN_READ = 2000;
    public final static int QUESTION_TYPE_EN_COPY = 2001;
    public final static int QUESTION_TYPE_EN_DICT = 2002;
    public final static int QUESTION_TYPE_EN_FOLLOWREAD = 2003;
    public final static int QUESTION_TYPE_EN_DIAG = 2004;

    public final static int INT_RIGHT = 0;
    public final static int INT_WRONG = 1;


    public final static String SHARED_FILE_NAME = "aihomework";
    public final static String SHARED_CN_WRONG_RADIX = "chinesewrongradix";
    public final static String SHARED_MA_WRONG_RADIX = "mathwrongradix";
    public final static String SHARED_EN_WRONG_RADIX = "englishwrongradix";

    public final static String READ_TEXT = "READ_TEXT";
    public final static String COPY = "COPY";
    public final static String DICTATION = "DICTATION";
    public final static String[] CHOICE_TITLE = {"A","B","C","D"};

    public final static int MAX_LINE_GAP = 3000;
    public final static String HW_KEY_STRING = "homework";

    //public final static String baseURL = "121.42.211.144:3000/homework";
    //public final static String baseURL = "http://121.42.211.144:3000/homework";
    public final static String baseURL = "http://59.78.22.97:3000/homework";
}
