import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.function.ToIntFunction;

public class Main {

    static class ScoreTree {
        private int[][] vp;

        private int n;
        long[] accumScore;

        private long[] scores;

        int[] recordSettersTree;

        public ScoreTree(int[][] vp) {
            this.vp = vp;
            for (n = 1; (n >> 1) < vp.length; n <<= 1) ;

            this.recordSettersTree = new int[n];
            Arrays.fill(recordSettersTree, -1);
            recordSettersTree[toTreeInd(0)] = 0;

            this.scores = new long[vp.length];

            accumScore = new long[vp.length];
            accumScore[0] = vp[0][0];
            for (int i = 1; i < vp.length; i++) {
                accumScore[i] = accumScore[i - 1] + vp[i][0];
            }
        }

        public long getScore(int ind) {
            long score = -1L;
            int treeInd = toTreeInd(ind);
            int recordInd = recordSettersTree[treeInd];
            for (; treeInd > 0; treeInd >>= 1, recordInd = recordSettersTree[treeInd]) {
                if ((recordInd == -1) || (recordInd + vp[recordInd][1] < ind)) continue;
                long parentScore = scores[recordInd] + accumScore[Math.max(ind - 1, 0)] - accumScore[recordInd];
                score = Math.max(score, parentScore);
            }

            return score;
        }

        public void put(int l) {
            int r = Math.min(l + vp[l][1], vp.length - 1);

            long lScore = getScore(l);
            scores[l] = lScore;

            int rTree = toTreeInd(r);
            // handle first step
            int recordInd = recordSettersTree[rTree];
            if (recordInd != -1) {
                long recordScore = scores[recordInd] + accumScore[l] - accumScore[recordInd];
                if (lScore <= recordScore) {
                    return;
                }
            }

            recordSettersTree[rTree] = l;
            int localRootInd = getLocalRootInd(l, r);

            boolean cameFromLeft;
            for (cameFromLeft = (rTree & 1) == 0, rTree >>= 1; rTree >= localRootInd; cameFromLeft = (rTree & 1) == 0, rTree >>= 1) {
                recordInd = recordSettersTree[rTree];
                if (recordInd == -1) {
                    recordSettersTree[rTree] = l;
                    continue;
                }

                long recordScore = scores[recordInd] + accumScore[l] - accumScore[recordInd];
                if (cameFromLeft) {
                    if (lScore <= recordScore) break;

                    if (recordInd + vp[recordInd][1] > r)
                        goDown(rTree << 1, recordInd, recordScore);
                } else {
                    if (lScore < recordScore) {
                        goDown(rTree << 1, l, lScore);
                        break;
                    }
                }

                recordSettersTree[rTree] = l;
            }
        }

        private void goDown(int topInd, int l, long lScore) {

            int lMaxReach = toTreeInd(l) + vp[l][1];

            for (int asdf = topInd; asdf < recordSettersTree.length; asdf <<= 1) {
                for (int treeInd = asdf; treeInd < recordSettersTree.length; treeInd = treeInd * 2 + 1) {
                    int nodeMinInd = treeInd;
                    for (; nodeMinInd < recordSettersTree.length; nodeMinInd <<= 1) {
                        if (nodeMinInd > lMaxReach) {
                            return;
                        }
                    }
                    int recordInd = recordSettersTree[treeInd];
                    if (recordInd == -1) {
                        recordSettersTree[treeInd] = l;
                        continue;
                    }

                    long recordScore = scores[recordInd] + (l < recordInd ? 0 : accumScore[l] - accumScore[recordInd]);
                    if (recordScore < lScore) {
                        recordSettersTree[treeInd] = l;
                    }
                }
            }
        }

        private int toTreeInd(int ind) {
            return ind + n / 2;
        }

        private int getLocalRootInd(int l, int r) {
            int lTree = toTreeInd(l), rTree = toTreeInd(r);
            for (; lTree != rTree; rTree >>= 1, lTree >>= 1) ;
            return lTree;
        }
    }

    static long robot(int[][] input) {
        ScoreTree scoreTree = new ScoreTree(input);

        for (int l = 0, r = input.length; l < r; l++) {
            scoreTree.put(l);
        }

        return input[input.length - 1][0] + scoreTree.getScore(input.length - 1);

    }

    public static void main(String[] args) throws IOException {

        File folder = new File("tests/");

        ToIntFunction<File> toN = (f) -> Integer.parseInt(f.getName().replace("test", "").replace(".txt", ""));
        Arrays.stream(folder.listFiles())
//                .filter(f -> toN.applyAsInt(f) == 15)
                .filter(File::isFile)
                .sorted(Comparator.comparingInt(toN::applyAsInt))
                .forEach(test -> {
                    Scanner scanner = null;
                    try {
                        scanner = new Scanner(test);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
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
                    } else {
                        test.delete();
                    }
                });

        System.out.println("finished");
    }
}
