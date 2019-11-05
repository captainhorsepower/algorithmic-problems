    #include <bits/stdc++.h>
    using namespace std;

    int main() {

        int n;
        cin >> n;

        long long sum;
        long long max;
        
        long long a;
        while (cin >> a) {
            sum += a;
            max = std::max(max, a);
        }

        if (sum % 2 == 1) cout << "NO";
        else if (sum - max < max) cout << "NO";
        else cout << "YES";

        return 0;
    }