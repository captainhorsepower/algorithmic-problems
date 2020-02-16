import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NavigableSet;
import java.util.Random;
import java.util.TreeSet;

public class TestCaseGenerator {

    static class GroundTruth {
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
            stops[vp.length - 1] = -1;
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
    }

    static TestCase generateTestCase(int N) {
        assert N < 5 * 10e5;

        int counter = 1;
        long ans = -1;
        Random random = new Random();

        int[][] vp = new int[N][2];

        while (ans == -1) {
            System.out.printf("generate for N %d, attempt %d\n", N, counter++);
            for (int i = 0; i < N; i++) {
                vp[i][0] = Math.abs(random.nextInt()) % 20;
                vp[i][1] = Math.abs(random.nextInt()) % (N * 2 / 3);
            }

            ans = GroundTruth.robot(vp);
        }

        System.out.println();
        return new TestCase(N, vp, ans);
    }

    public static void main(String[] args) throws IOException {

        for (int i = 0; i <= 32768; i += 1) {
            TestCase testCase = generateTestCase(1024);

            BufferedWriter writer = new BufferedWriter(new FileWriter("tests/test" + i + ".txt"));
            writer.write(testCase.getN() + " " + testCase.getAns() + "\n");

            for (int j = 0; j < testCase.getN(); j++) {
                writer.write(testCase.getVp()[j][0] + " " + testCase.getVp()[j][1] + "\n");
            }

            writer.close();
        }
    }

}
