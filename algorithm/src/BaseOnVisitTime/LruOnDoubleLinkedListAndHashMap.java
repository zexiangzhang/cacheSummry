package BaseOnVisitTime;

import java.util.HashMap;

/**
 * 相比于linkedList+HashMap实现的Lru算法，其实删除操作的时间复杂度依然是O(n)
 * 因此可以自定义双向链表的结构，这里定义了内部类Node，存放KV以及前后指针
 * 这样通过hashmap找到对应Node，然后根据其前驱节点进行指针的操作，就可以实现复杂度O(1)的删除操作
 * 同样因为访问HashMap需要key，所以定义Node节点存放了K和V，而不是只存放V。保存队列的头节点和尾节点
 */
public class LruOnDoubleLinkedListAndHashMap<K, V> {

    private int size;
    private HashMap<K, Node> map;
    private Node head;
    private Node tail;

    LruOnDoubleLinkedListAndHashMap(int size) {
        this.size = size;
        map = new HashMap<>();
    }

    /**
     * 添加元素
     * 1.元素存在，将元素移动到队尾
     * 2.不存在，判断链表是否满。
     * 如果满，则删除队首元素，放入队尾元素，删除更新哈希表
     * 如果不满，放入队尾元素，更新哈希表
     */
    public void put(K key, V value) {
        Node node = map.get(key);
        if (node != null) {
            node.v = value;
            moveNodeToTail(node);
        } else {
            Node newNode = new Node(key, value);
            if (map.size() == size) {
                Node delHead = removeHead();
                map.remove(delHead.k);
            }
            addLast(newNode);
            map.put(key, newNode);
        }
    }

    public V get(K key) {
        Node node = map.get(key);
        if (node != null) {
            moveNodeToTail(node);
            return node.v;
        }
        return null;
    }

    /**
     * 添加元素到队尾
     */
    public void addLast(Node newNode) {
        if (newNode == null) {
            return;
        }
        if (head == null) {
            head = newNode;
        } else {
            tail.next = newNode;
            newNode.pre = tail;
        }
        tail = newNode;
    }

    /**
     * 将队列中元素移动到队尾
     */
    public void moveNodeToTail(Node node) {
        if (tail == node) {
            return;
        }
        if (head == node) {
            head = node.next;
            head.pre = null;
        } else {
            node.pre.next = node.next;
            node.next.pre = node.pre;
        }
        node.pre = tail;
        node.next = null;
        tail.next = node;
        tail = node;
    }

    /**
     * 删除队列头节点并返回
     */
    public Node removeHead() {
        if (head == null) {
            return null;
        }
        Node res = head;
        if (head == tail) {
            head = null;
            tail = null;
        } else {
            head = res.next;
            head.pre = null;
            res.next = null;
        }
        return res;
    }

    class Node {
        K k;
        V v;
        Node pre;
        Node next;

        Node(K k, V v) {
            this.k = k;
            this.v = v;
        }
    }

}
