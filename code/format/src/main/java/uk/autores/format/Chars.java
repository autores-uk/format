package uk.autores.format;

final class Chars {
    private Chars() {}

    static String concat(Object[] objs) {
        if (objs.length == 1) {
            return objs[0].toString();
        }
        var buffer = new char[len(objs)];
        int offset = 0;
        for (Object o : objs) {
            offset = concat(buffer, offset, o);
        }
        return new String(buffer);
    }

    private static int concat(char[] buffer, int offset, Object o) {
        var s = o.toString();
        int len = s.length();
        for (int i = 0; i < len; i++) {
            buffer[i + offset] = s.charAt(i);
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
