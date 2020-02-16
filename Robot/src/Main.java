import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    static class ScoreTree {
        private int[][] vp;

        private int n;
        long[] accumScore;

        /**
         * рекорды по индексу рекордсмена
         */
        private long[] scores;

        /**
         * представляет из себя дерево
         * - нижний уровень пусть будет j: 0..N - номера шагов.
         * нижний_уровень[j] = номер шага (был раньше j), на котором взяли энергию,
         * и в результате получили максимально возможный счёт на текущем шаге.
         * НО, это не массив, а дерево, поэтому на нижнем уровне на самом деле
         * не хранится инфа непосредственно в [j], она может находится в дереве выше.
         * <p>
         * Поэтому, чтобы получить индекс рекодсмена, надо
         * - пройти до корня, собрать всех рекордсменов
         * - откинуть тех, кто не доходит до этой клетки (??? может это не надо)
         * - выбрать наилучшего
         */
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

        /**
         * @param ind номер хода
         * @return лучший счёт, с которым можно начинать этот ход
         */
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


        /**
         * На каждом ходу, беру ход (его результат (как он может улучшить ситуацию впереди))
         * и вешаю в дерево.
         * Начну  с того, что вешать буду только на правую ветку, т.к. в левую мне не надо будет идти ??
         *
         * @param l
         */
        public void put(int l) {
            int r = Math.min(l + vp[l][1], vp.length - 1);

            long lScore = getScore(l);
            long rScore = l == 0 ? 0 : l + accumScore[r - 1] - accumScore[l];
            scores[l] = lScore;

            int rTree = toTreeInd(r);
            // handle first step
            int recordInd = recordSettersTree[rTree];
            if (recordInd != -1) {
                long recordScore = scores[recordInd] + accumScore[r - 1] - accumScore[recordInd];
                if (rScore <= recordScore) {
                    return;
                }
            }

            recordSettersTree[rTree] = l;
            int localRootInd = getLocalRootInd(l, r);

            boolean cameFromLeft, goDown = false;
            // go up tree
            for (cameFromLeft = (rTree & 1) == 0, rTree >>= 1; rTree >= localRootInd; cameFromLeft = (rTree & 1) == 0, rTree >>= 1) {
                recordInd = recordSettersTree[rTree];

                if (recordInd == -1) {
                    recordSettersTree[rTree] = l;
                    continue;
                }

                long recordScore = scores[recordInd] + accumScore[r - 1] - accumScore[recordInd];
                if (cameFromLeft) {
                    if (rScore <= recordScore) break;
                } else {
                    if (rScore < recordScore) {
                        goDown = true;
                        break;
                    }
                }

                recordSettersTree[rTree] = l;
            }

            // go down
            if (goDown) {
                rTree <<= 1; // one time descend left, all other times descend right
                for (; rTree < recordSettersTree.length; rTree = rTree * 2 + 1) {
                    recordInd = recordSettersTree[rTree];
                    if (recordInd == -1) {
                        recordSettersTree[rTree] = l;
                        continue;
                    }

                    long recordScore = scores[recordInd] + accumScore[r - 1] - accumScore[recordInd];
                    if (rScore >= recordScore) {
                        recordSettersTree[rTree] = l;
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
