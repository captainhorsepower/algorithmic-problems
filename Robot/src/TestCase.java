public class TestCase {

    private int N;

    private int[][] vp;

    private long ans;

    public TestCase(int n, int[][] vp, long ans) {
        N = n;
        this.vp = vp;
        this.ans = ans;
    }

    public int getN() {
        return N;
    }

    public int[][] getVp() {
        return vp;
    }

    public long getAns() {
        return ans;
    }

}
