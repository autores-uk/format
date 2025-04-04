package uk.autores.format;

final class Chars {
    private Chars() {}

    static String concat(Object[] objs) {
        if (objs.length == 1) {
            return objs[0].toString();
        }
        char[] arr = new char[len(objs)];
        int offset = 0;
        for (Object o : objs) {
            offset = concat(arr, offset, o);
        }
        return new String(arr);
    }

    private static int concat(char[] arr, int offset, Object o) {
        String s = o.toString();
        int len = s.length();
        for (int i = 0; i < len; i++) {
            arr[i + offset] = s.charAt(i);
        }
        return offset + len;
    }

    private static int len(Object... objs) {
        int n = 0;
        for (Object o : objs) {
            n += o.toString().length();
        }
        return n;
    }
}
