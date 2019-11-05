#include <bits/stdc++.h>
using namespace std;

int n, m;
int counter = 0;

struct triplet {
    int x, y, color;

    triplet(int x, int y, int color) : x(x), y(y), color(color) {}
};

void print_g(vector< vector<int> > &g);

void paint(vector< vector<int> > &g, int x, int y) {
    if (g[x][y] != 0) return;
    queue<triplet> q;

    q.emplace(x, y, 1);

    int clr1 = 0, clr2 = 0;

    // cout << "before: " << x << ' ' << y << endl;
    // print_g(g);

    while( !q.empty() ) {
        triplet t = q.front(); q.pop();
        int x = t.x;
        int y = t.y;
        int color = t.color;

        if (g[x][y] != 0) continue;
        
        if (color == 1) clr1++;
        else clr2++;
        
        g[x][y] = color;
        
        color = (color == 1) ? 2 : 1;
        if (x - 1 > 0 and y - 2 > 0 and g[x - 1][y - 2] == 0) q.emplace(x - 1, y - 2, color);
        if (x - 2 > 0 and y - 1 > 0 and g[x - 2][y - 1] == 0) q.emplace(x - 2, y - 1, color);
        if (x - 1 > 0 and y + 2 <= m and g[x - 1][y + 2] == 0) q.emplace(x - 1, y + 2, color);
        if (x - 2 > 0 and y + 1 <= m and g[x - 2][y + 1] == 0) q.emplace(x - 2, y + 1, color);
        if (x + 1 <= n and y - 2 > 0 and g[x + 1][y - 2] == 0) q.emplace(x + 1, y - 2, color);
        if (x + 2 <= n and y - 1 > 0 and g[x + 2][y - 1] == 0) q.emplace(x + 2, y - 1, color);
        if (x + 1 <= n and y + 2 <= m and g[x + 1][y + 2] == 0) q.emplace(x + 1, y + 2, color);
        if (x + 2 <= n and y + 1 <= m and g[x + 2][y + 1] == 0) q.emplace(x + 2, y + 1, color);
    }

    counter += max(clr1, clr2);

    // cout << "after: " << x << ' ' << y << endl;
    // print_g(g);
}

void print_g(vector< vector<int> > &g) {
    for (int i = 1; i < g.size(); i++ ) {
        for (int j = 1; j < g[0].size(); j++) {
            cout << g[i][j] << ' ';
        }
        cout << endl;
    }
}

int main() {

    ifstream in("input.txt");
    in >> n >> m;

    vector< vector<int> > board(n + 1, vector<int>(m + 1));

    int x, y;
    while (in >> x >> y) {
        board[x][y] = -1;
    }

    for (int x = 1; x <= n; x++) {
        for (int y = 1; y <= m; y++ ) {
            paint(board, x , y);
        }
    }

    ofstream out("output.txt");
    out << counter;
    out.close();
    return 0;
}