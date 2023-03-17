package net.hk.hk97.Services.Util.CustomUtil;

import net.hk.hk97.Models.Nation;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NationScoreSorter {
    public static void sortObjectsByScore(List<Nation> objects) {
        Collections.sort(objects, new Comparator<Nation>() {
            @Override
            public int compare(Nation o1, Nation o2) {
                return Double.compare(o1.getScore(), o2.getScore());
            }
        });
    }
}
