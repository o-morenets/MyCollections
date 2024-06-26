package my_hashmap;

import java.util.*;

/**
 * HashMap implementation
 * null as key is allowed
 * Created by a-morenets on 28.11.2016.
 */
public class MyHashMap<K, V> implements Map<K, V> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private Node<K, V>[] data;
    private int capacity;
    private float loadFactor;
    private int modCount;
    private int size;

    /**
     * Inner class Node
     */
    private class Node<K, V> {
        private K key;
        private V value;
        private Node<K, V> next;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return "(" + key + "," + value + ")" + (next != null ? "->" + next.toString() : "");
        }
    }

    public MyHashMap() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public MyHashMap(int capacity, float loadFactor) {
        this.capacity = capacity;
        this.loadFactor = loadFactor;
        data = new Node[capacity];
    }

    /* Hash function */
    private int hash(K key) {
        return key == null ? 0 : key.hashCode();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        int index = hash((K) key) % capacity;
        if (data[index] == null) // cell is empty - the key is not stored in the map
            return false;
        // cell contains a chain of node(s)
        Node<K, V> currentNode = data[index];
        while (currentNode != null) {
            // element found with the given key
            if (currentNode.key == null) {
                if (key == null)
                    return true;
            } else if (currentNode.key.equals(key))
                return true;
            currentNode = currentNode.next;
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        for (int i = 0; i < data.length; i++) {
            if (data[i] != null) {
                // cell contains a chain of node(s)
                Node<K, V> currentNode = data[i];
                while (currentNode != null) {
                    // element found with the given value
                    if (currentNode.value == null) {
                        if (value == null)
                            return true;
                    } else if (currentNode.value.equals(value))
                        return true;
                    currentNode = currentNode.next;
                }
            }
        }
        return false;
    }

    @Override
    public V get(Object key) {
        int index = hash((K) key) % capacity;
        if (data[index] == null) // cell is empty - the key is not stored in the map
            return null;
        // cell contains a chain of node(s)
        Node<K, V> currentNode = data[index];
        while (currentNode != null) {
            // element found with the given key
            if (currentNode.key == null) {
                if (key == null)
                    return currentNode.value;
            } else if (currentNode.key.equals(key))
                return currentNode.value;
            currentNode = currentNode.next;
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        int index = hash(key) % capacity;
        if (data[index] == null) // cell is empty
            // create new node and place it at index'th position in the data table
            data[index] = new Node<>(key, value);
        else { // cell contains a chain of node(s)
            Node<K, V> currentNode = data[index];
            while (currentNode != null) {
                // element found with the given key
                if (currentNode.key == null) {
                    if (key == null) {
                        V originalValue = currentNode.value;
                        currentNode.value = value;
                        return originalValue;
                    }
                } else if (currentNode.key.equals(key)) {
                    V originalValue = currentNode.value;
                    currentNode.value = value;
                    return originalValue;
                }
                if (currentNode.next == null)
                    break;
                currentNode = currentNode.next;
            }
            // add new node
            currentNode.next = new Node<>(key, value);
        }
        size++;
        modCount++;

        ensureCapacity();
        return null;
    }

    /**
     * Grow table and rehash data if (size > capacity * loadFactor)
     */
    private void ensureCapacity() {
        if (size >  capacity * loadFactor) {
            Node<K, V>[] oldData = data;
            capacity *= 2;
            size = 0;
            data = new Node[capacity];

            // rehash table
            for (Node<K, V> node : oldData){
                if (node != null) {
                    Node<K, V> current = node;
                    while (current != null) {
                        put(current.key, current.value);
                        current = current.next;
                    }
                }
            }
        }
    }

    @Override
    public V remove(Object key) {
        int index = hash((K) key) % capacity;
        if (data[index] == null) // cell is empty - the key is not stored in the map
            return null;
        // cell contains a chain of node(s)
        Node<K, V> currentNode = data[index];
        Node<K, V> prevNode = null;
        while (currentNode != null) {
            // element found with the given key
            if (currentNode.key == null) {
                if (key == null) {
                    V originalValue = currentNode.value;
                    if (prevNode == null)
                        data[index] = currentNode.next;
                    else
                        prevNode.next = currentNode.next;
                    size--;
                    modCount++;
                    return originalValue;
                }
            } else if (currentNode.key.equals(key)) {
                V originalValue = currentNode.value;
                if (prevNode == null)
                    data[index] = currentNode.next;
                else
                    prevNode.next = currentNode.next;
                size--;
                modCount++;
                return originalValue;
            }
            prevNode = currentNode;
            currentNode = currentNode.next;
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        modCount++;
        for (Node<K, V> node : data)
            node = null;
        size = 0;
    }

    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<K>();
        for (Node<K, V> hashMapNode : data) {
            if (hashMapNode != null) {
                Node<K, V> currentNode = hashMapNode;
                while (currentNode != null) {
                    set.add(currentNode.key);
                    currentNode = currentNode.next;
                }
            }
        }
        return set;
    }

    @Override
    public Collection<V> values() {
        // TODO
        Collection<V> col = new AbstractCollection<V>() {
            Iterator<V> vIterator = new Iterator<V>() {
                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public V next() {
                    return null;
                }
            };

            @Override
            public Iterator<V> iterator() {
                return null;
            }

            @Override
            public int size() {
                return MyHashMap.this.size;
            }
        };

        for (Node<K, V> hashMapNode : data) {
            if (hashMapNode != null) {
                Node<K, V> currentNode = hashMapNode;
                while (currentNode != null) {
                    col.add(currentNode.value);
                    currentNode = currentNode.next;
                }
            }
        }

        return col;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> set = new HashSet<>();

        for (Node<K, V> hashMapNode : data) {
            if (hashMapNode != null) {
                Node<K, V> currentNode = hashMapNode;
                while (currentNode != null) {
                    set.add(new Entry<K, V>() {
                        @Override
                        public K getKey() {
                            return hashMapNode.key;
                        }

                        @Override
                        public V getValue() {
                            return hashMapNode.value;
                        }

                        @Override
                        public V setValue(V value) {
                            V oldValue = hashMapNode.value;
                            hashMapNode.value = value;
                            return oldValue;
                        }
                    });
                    currentNode = currentNode.next;
                }
            }
        }
        return set;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MyHashMap<?, ?> myHashMap = (MyHashMap<?, ?>) o;

        if (size != myHashMap.size) return false;

        for (Node<K, V> node : data) {
//            System.out.println(node.key + "=" + node.value + "\t" + myHashMap.get(node.key));
            if (!myHashMap.get(node.key).equals(node.value))
                return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(data);
        result = 31 * result + size;
        return result;
    }
}
