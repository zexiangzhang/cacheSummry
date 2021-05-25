package BaseOnVisitTime;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 在JavaLinkedHashMap已经实现了LRU缓存淘汰算法，需要在构造函数第三个参数传入true，表示按照时间顺序访问
 * 可以直接继承LinkedHashMap来实现
 * 但是LinkedHashMap会自动扩容，
 * 如果想实现限制容量删除队列顶端元素，需要重写removeEldestEntry()方法，当map里面的元素个数大于了缓存最大容量，删除链表的顶端元素
 */
public class LruOnLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

    private int capacity;

    LruOnLinkedHashMap(int capacity) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }

}
