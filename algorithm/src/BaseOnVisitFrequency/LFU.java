package BaseOnVisitFrequency;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

/**
 * 定义两个哈希表
 *
 * 第一个freq_table以频率freq为索引，每个索引存放一个双向链表，
 * 这个链表里存放所有使用频率为freq的缓存，缓存里存放三个信息，分别为键key，值value，以及使用频率freq
 *
 * 第二个key_table以键值key为索引，每个索引存放对应缓存在freq_table中链表里的内存地址
 * 这样就能利用两个哈希表来使得两个操作的时间复杂度均为O(1)
 *
 * 同时需要记录一个当前缓存最少使用的频率minFreq，这是为了删除操作服务的
 *
 * 对于get(key)操作，通过索引key在key_table中找到缓存在freq_table中的链表的内存地址
 * 如果不存在直接返回-1，否则能获取到对应缓存的相关信息，这样就能知道缓存的键值还有使用频率，直接返回key对应的值即可
 * 但是get操作后这个缓存的使用频率加一了，所以需要更新缓存在哈希表freq_table中的位置
 * 已知这个缓存的键key，值value，以及使用频率freq，那么该缓存应该存放到freq_table中freq + 1索引下的链表中
 * 所以在当前链表中O(1)删除该缓存对应的节点，根据情况更新minFreq值，然后将其O(1)插入到 freq + 1 索引下的链表头完成更新
 * 这其中的操作复杂度均为O(1)
 * 更新的时候插入到链表头，是为了保证缓存在当前链表中从链表头到链表尾的插入时间是有序的，为下面的删除操作服务
 *
 * 对于put(key, value)操作，先通过索引key在key_table中查看是否有对应的缓存，如果有的话，其实操作等价于get(key)操作
 * 唯一的区别就是我们需要将当前的缓存里的值更新为value
 * 如果没有的话，相当于是新加入的缓存，如果缓存已经到达容量，需要先删除最近最少使用的缓存，再进行插入
 *
 * 由于是新插入的，所以缓存的使用频率一定是1，所以将缓存的信息插入到freq_table中1索引下的列表头
 * 同时更新key_table[key]的信息，以及更新minFreq = 1。
 *
 * 对于删除操作，由于实时维护了minFreq，所以能够知道freq_table里目前最少使用频率的索引
 * 同时因为保证了链表中从链表头到链表尾的插入时间是有序的，所以freq_table[minFreq]的链表中链表尾的节点即为使用频率最小且插入时间最早的节点
 * 删除它同时根据情况更新minFreq，整个时间复杂度均为O(1)
 */
public class LFU {

    int minFrequency, capacity;
    Map<Integer, Node> key_table;
    Map<Integer, LinkedList<Node>> freq_table;

    public LFU(int capacity) {
        this.minFrequency = 0;
        this.capacity = capacity;
        key_table = new HashMap<>();;
        freq_table = new HashMap<>();
    }

    public int get(int key) {
        if (capacity == 0) {
            return -1;
        }
        if (!key_table.containsKey(key)) {
            return -1;
        }
        Node node = key_table.get(key);
        int val = node.val;
        deleteAndUpdate(node.freq, node, key, val);
        return val;
    }

    public void put(int key, int value) {
        if (capacity == 0) {
            return;
        }
        if (!key_table.containsKey(key)) {
            // 缓存已满，需要进行删除操作
            if (key_table.size() == capacity) {
                // 通过 minFrequency 拿到 freq_table[minFrequency] 链表的末尾节点
                Node node = freq_table.get(minFrequency).peekLast();
                if (Objects.nonNull(node)) {
                    key_table.remove(node.key);
                    freq_table.get(minFrequency).pollLast();
                    if (freq_table.get(minFrequency).size() == 0) {
                        freq_table.remove(minFrequency);
                    }
                }
            }
            LinkedList<Node> list = freq_table.getOrDefault(1, new LinkedList<>());
            list.offerFirst(new Node(key, value, 1));
            freq_table.put(1, list);
            key_table.put(key, freq_table.get(1).peekFirst());
            minFrequency = 1;
        } else {
            Node node = key_table.get(key);
            deleteAndUpdate(node.freq, node, key, value);
        }
    }

    /**
     * 如果当前链表为空，则在哈希表中删除，且更新minFrequency
     */
    private void deleteAndUpdate(int freq, Node node, int key, int value) {
        freq_table.get(freq).remove(node);
        if (freq_table.get(freq).size() == 0) {
            freq_table.remove(freq);
            if (minFrequency == freq) {
                minFrequency += 1;
            }
        }
        LinkedList<Node> list = freq_table.getOrDefault(freq + 1, new LinkedList<Node>());
        list.offerFirst(new Node(key, value, freq + 1));
        freq_table.put(freq + 1, list);
        key_table.put(key, freq_table.get(freq + 1).peekFirst());
    }

    static class Node {
        int key, val, freq;

        Node(int key, int val, int freq) {
            this.key = key;
            this.val = val;
            this.freq = freq;
        }

    }
}
