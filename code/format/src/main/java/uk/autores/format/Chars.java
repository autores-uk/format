package uk.autores.format;

final class Chars {
    private Chars() {}

    static String concat(Object[] objs) {
        if (objs.length == 1) {
            return objs[0].toString();
        }
        var buffer = new StringBuilder(8 * objs.length)
        for (Object o : objs) {
            buffer.append(o);
        }
        return buffer.toString();
    }
}
