import java.io.File;
import java.io.IOException;
import java.util.NavigableSet;
import java.util.Scanner;
import java.util.TreeSet;

public class Main {

    static class FastMax {

        private int[] range;

        FastMax(int[][] vals) {
            int n;
            for (n = 1; (n >> 1) < vals.length; n <<= 1) ;
            this.range = new int[n];

            for (int i = 0, j = n / 2; i < vals.length; i++, j++) {
                range[j] = vals[i][1];
            }

            for (int i = n / 2 - 1; i > 0; i--) {
                range[i] = Math.max(range[2 * i], range[2 * i + 1]);
            }
        }

        int max(int l, int r) {
            int n = range.length;
            l += n >> 1;
            r += n >> 1;

            // [l, r] from now on
            int lMax = range[l];
            int rMax = range[r];

            while (r - l > 1) {
                lMax = (l & 1) == 0 ? range[l / 2] : lMax;
                rMax = (r & 1) == 0 ? rMax : range[r / 2];

                l >>= 1;
                r >>= 1;
            }

            return Math.max(lMax, rMax);
        }
    }



    static FastMax fastMax;
    static long[] accumScore;
    static long[] stopsRecords;
    static NavigableSet<Integer> set = new TreeSet<>();

    static void initAccumSum(int[][] vp) {
        accumScore = new long[vp.length];
        long sum = 0L;
        for (int i = 0; i < vp.length; i++) {
            sum += vp[i][0];
            accumScore[i] = sum;
        }
    }

    static void initStops(int[][] vp) {
        stopsRecords = new long[vp.length];
    }

    static void findStops(int[][] vp, int startInd, int l, int r) {
        int maxPower = fastMax.max(l, r);
        int endInd = startInd + vp[startInd][1];
        if (r + maxPower < endInd) return;

        if (r - l < 16) {
            for (; l <= r; l++) {
                int power = vp[l][1];
                if (l + power < endInd) continue;
                set.add(l);
            }

            return;
        }

        int m = (l + r) / 2;
        findStops(vp, startInd, l, m);
        findStops(vp, startInd, m + 1, r);
    }

    /*
     * Complete the robot function below.
     */
    static long robot(int[][] vp) {
        initAccumSum(vp);
        initStops(vp);
        fastMax = new FastMax(vp);

        final int N = vp.length - 1;

        set.add(0);
        while (!set.isEmpty()) {
            int currInd = set.pollFirst(); // lowest

            int currPower = vp[currInd][1];
            int currMaxDistance = currInd + currPower;

            if (currMaxDistance >= N) {
                stopsRecords[N] = Math.max(
                        stopsRecords[N],
                        stopsRecords[currInd] + accumScore[N] - accumScore[currInd]
                );
                continue;
            }

            findStops(vp, currInd, currInd + 1, currMaxDistance);

            for (Integer i : set.subSet(currInd + 1, currMaxDistance)) {
                stopsRecords[i] = Math.max(stopsRecords[i], stopsRecords[currInd] + accumScore[i - 1] - accumScore[currInd]);
            }
        }

        return stopsRecords[N];

    }

    public static void main(String[] args) throws IOException {

        File folder = new File("tests/");

        for (File test : folder.listFiles()) {
            Scanner scanner = new Scanner(test);
            int n = scanner.nextInt();
            long ans = scanner.nextLong();
            scanner.nextLine();
            int[][] vp = new int[n][2];
            for (int vpRowItr = 0; vpRowItr < n; vpRowItr++) {
                String[] vpRowItems = scanner.nextLine().split(" ");
                for (int vpColumnItr = 0; vpColumnItr < 2; vpColumnItr++) {
                    int vpItem = Integer.parseInt(vpRowItems[vpColumnItr]);
                    vp[vpRowItr][vpColumnItr] = vpItem;
                }
            }
            scanner.close();

            long result = robot(vp);

            if (result != ans) {
                System.out.printf("ERROR %-15s: ans %-14d expected %-14d ans-expected %-14d\n", test.getName(), result, ans, result - ans);
            }
        }

        System.out.println("finished");
    }
}
