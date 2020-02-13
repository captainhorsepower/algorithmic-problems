package edu.varabei;

import java.util.Arrays;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.stream.Stream;

public class Main {

    static long[] accumScore;
    static long[] stops;

    static void initAccumSum(int[][] vp) {
        accumScore = new long[vp.length];
        long sum = 0L;
        for (int i = 0; i < vp.length; i++) {
            sum += vp[i][0];
            accumScore[i] = sum;
        }
    }

    static void initStops(int[][] vp) {
        stops = new long[vp.length];
    }

    /*
     * Complete the robot function below.
     */
    static long robot(int[][] vp) {

        initAccumSum(vp);
        initStops(vp);

        NavigableSet<Integer> set = new TreeSet<>();

        final int N = vp.length - 1;

        set.add(0);
        while (!set.isEmpty()) {
            int currInd = set.pollFirst(); // lowest

            int currPower = vp[currInd][1];

            if (currInd + currPower >= N) {
                stops[N] = Math.max(stops[N], stops[currInd] + accumScore[N] - accumScore[currInd]);
                continue;
            }

            for (int i = currInd + 1; i <= currInd + currPower; i++) {
                int power = vp[i][1];
                if (i + power < currInd + currPower) continue;

                set.add(i);
                stops[i] = Math.max(stops[i], stops[currInd] + accumScore[i - 1] - accumScore[currInd]);
            }
        }

        return stops[N];

    }


    public static void main(String[] args) {

        int[][] vp = {
                {4, 2},
                {0, 2},
                {4, 0},
                {3, 4},
        };

        System.out.println(robot(vp));
    }
}
