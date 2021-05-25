package BaseOnVisitTime;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 也可以使用LinkedList和HashMap实现，但时间复杂度较高
 * 使用HashMap可以通过O(1)时间拿到元素，但是无法在O(1)时间定位它在链表中的位置
 * 在LinkedList里访问元素仍然是顺序遍历，所以删除元素的时间复杂度仍然是O(n)
 * 因为从HashMap中删除元素需要Key，所以这里在链表中存放Key而不是Value
 */
public class LruOnLinkedListAndHashMap<K, V> {

    int capacity;
    Map<K, V> map;
    LinkedList<K> list;

    public LruOnLinkedListAndHashMap(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.list = new LinkedList<>();
    }

    /**
     * 添加元素
     * 1.元素存在，放到队尾
     * 2.不存在，判断链表是否满。
     * 如果满，则删除队首元素，放入队尾元素，删除更新哈希表
     * 如果不满，放入队尾元素，更新哈希表
     */
    public void put(K key, V value) {
        V v = map.get(key);
        if (v != null) {
            list.remove(key);
            list.addLast(key);
            map.put(key, value);
            return;
        }
        //队列未满，添加到尾部
        if (list.size() >= capacity) {
            //队列已满，移除队首
            K firstKey = list.removeFirst();
            map.remove(firstKey);
        }
        list.addLast(key);
        map.put(key, value);
    }

    /**
     * 访问元素
     * 元素存在，放到队尾
     */
    public V get(K key) {
        V v = map.get(key);
        if (v != null) {
            list.remove(key);
            list.addLast(key);
            return v;
        }
        return null;
    }
}
