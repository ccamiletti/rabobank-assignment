package nl.rabobank.account.util;

import java.util.*;


public class Test {

    public static long bioHazard(int n, List<Integer> allergic, List<Integer> poisonous) {

        HashMap<Integer, HashSet<Integer>> hashMap = new HashMap<>();
        int index = 0;
        HashSet<Integer> set = null;
        for(int poison: poisonous) {
            if(hashMap.containsKey(poison)) {
                hashMap.get(poison).add(allergic.get(index));
            } else {
                set = new HashSet<>();
                set.add(allergic.get(index));
                hashMap.put(poison, set);
            }
            index++;
        }
        int intervals = 0;
        List<Integer> sequence = new ArrayList<>();
        for (int i=1;i<=n;i++) {
            if(hashMap.containsKey(i) && hashMap.get(i).contains(i+1)){
                continue;
            }
            if(hashMap.containsKey(i+1) && hashMap.get(i+1).contains(i)){
                continue;
            }
            sequence.add(i);
        }
        int size = sequence.size();
        long result =(long)Math.pow(2, size) - 1 - size;
        System.out.println(result + n);
        return intervals;
    }

}
