package ru.kircoop.gk23.comparator;

import java.util.Comparator;

public class SeriesComparator implements Comparator<String> {

    private boolean isThereAnyNumber(String a, String b) {
        return isNumber(a) && isNumber(b);
    }

    private boolean isNumber(String s) {
        return s.matches("[-+]?\\d*\\.?\\d+");
    }

    @Override
    public int compare(String a, String b) {
        if (isThereAnyNumber(a, b)) {
            Integer first = Integer.parseInt(a);
            Integer second = Integer.parseInt(b);
            return first.compareTo(second);

        }
        if (isNumber(a)) {
            return 1;
        }
        return a.compareTo(b);
    }
}
