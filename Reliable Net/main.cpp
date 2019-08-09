#include <bits/stdc++.h>
using namespace std;

struct edge {
    int flow;
    const int capacity, to, from, cost;
    edge *reversed;
    edge(int from, int to, int cost, int flow = 0, int capacity = 1) 
        : to(to), from(from), cost(cost), flow(flow), capacity(capacity), reversed(nullptr) {}  

    bool used() {
        return !(flow < capacity);
    }

    void inc_flow() {
        flow++;
        reversed->flow--;
    }
};

void add_inner_edge(vector< vector<edge*> > &g, int v) {
    edge *forward = new edge(v * 2, v * 2 + 1, 0);
    edge *backward = new edge(v * 2 + 1, v * 2, 0, 1);
    forward->reversed = backward;
    backward->reversed = forward;
    g[v * 2].push_back(forward);
    g[v * 2 + 1].push_back(backward);
}

void add_edge(vector< vector<edge*> > &g, const int from, const int to, const int cost) {
    edge *fromto = new edge(from * 2 + 1, to * 2, cost);
    edge *fromto_reversed = new edge(to * 2, from * 2 + 1, -cost, 1);
    fromto->reversed = fromto_reversed;
    fromto_reversed->reversed = fromto;
    g[from * 2 + 1].push_back(fromto);
    g[to * 2].push_back(fromto_reversed);

    if (from != to) {
        edge *tofrom = new edge(to * 2 + 1, from * 2, cost);
        edge *tofrom_reversed = new edge(from * 2, to * 2 + 1, -cost, 1);
        tofrom->reversed = tofrom_reversed;
        tofrom_reversed->reversed = tofrom;
        g[to * 2 + 1].push_back(tofrom);
        g[from * 2].push_back(tofrom_reversed);
    }
}

long long find_shortest_paht(vector< vector<edge*> > &g, int from, int to) {
    vector< short > id(g.size());   // id = 0 - not calculated
                                    //    = 1 - in queue
                                    //    = 2 - calculated
    vector< long long > distance(g.size(), __LONG_LONG_MAX__);
    vector< edge* > parent(g.size());
    id[from] = 1;
    distance[from] = 0;
    parent[from] = nullptr;

    deque<int> q;
    q.push_back(from);

    while (!q.empty()) {
        int v = q.front(); q.pop_front();
        id[v] = 2;
        for (edge* e : g[v]) {
            if (e->used()) continue;

            if (e->from != v) {
                cout << "I suck";
            }
            int to = e->to;
            int cost = e->cost;

            if (distance[to] > distance[v] + cost) {
                distance[to] = distance[v] + cost;
                parent[to] = e;
                if (id[to] == 0) q.push_back(to);
                else if (id[to] == 2) q.push_front(to);
            }
        }
    }

    // increase flow throug the shortest path 
    edge *curr = parent[to];
    while (curr != nullptr) {
        curr->inc_flow();
        curr = parent[curr->from];
    }

    return distance[to];
}

int main() {

    ifstream in("input.txt");

    int n, m;
    in >> n >> m;

    vector< vector<edge*> > graph(n * 2 + 1);
    for (int i = 0; i < n; i++) {
        add_inner_edge(graph, i);
    }
    for (int i = 0; i < m; i++) {
        int from, to, cost;
        in >> from >> to >> cost;
        add_edge(graph, from - 1, to - 1, cost);
    }

    int a, b, k;
    in >> a >> b >> k;
    a--; b--;

    in.close();

    int flow = 0;
    long long shortest_path = 0;
    while (flow <= k) {
        long long tmp = find_shortest_paht(graph, a * 2 + 1, b * 2);
        if (tmp == __LONG_LONG_MAX__) {
            break;
        }
        shortest_path += tmp;
        flow++;
    }

    ofstream out("output.txt");

    if (flow <= k) {
        out << "No\n" << flow;
    } else {
        out << "Yes\n" << shortest_path;
    }

    out.close();

    for (vector<edge*> edges : graph) {
        for (edge* e : edges) {
            delete e;
        }
    }

    return 0;
}