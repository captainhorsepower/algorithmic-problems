import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;

import java.io.File;
import java.io.IOException;
import java.util.*;
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
                System.out.printf("ERROR %10s: ans=%d, expected=%d\n", test.getName(), result, ans);
            }
        }

        System.out.println("finished");
    }
}
