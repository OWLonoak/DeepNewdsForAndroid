package android.support.v4.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class ArraySet<E> implements Collection<E>, Set<E> {
    private static final int BASE_SIZE = 4;
    private static final int CACHE_SIZE = 10;
    private static final boolean DEBUG = false;
    private static final int[] INT = new int[0];
    private static final Object[] OBJECT = new Object[0];
    private static final String TAG = "ArraySet";
    private static Object[] sBaseCache;
    private static int sBaseCacheSize;
    private static Object[] sTwiceBaseCache;
    private static int sTwiceBaseCacheSize;
    Object[] mArray;
    private MapCollections<E, E> mCollections;
    private int[] mHashes;
    int mSize;

    /* renamed from: android.support.v4.util.ArraySet$1 */
    class C07731 extends MapCollections<E, E> {
        C07731() {
        }

        protected void colClear() {
            ArraySet.this.clear();
        }

        protected Object colGetEntry(int i, int i2) {
            return ArraySet.this.mArray[i];
        }

        protected Map<E, E> colGetMap() {
            throw new UnsupportedOperationException("not a map");
        }

        protected int colGetSize() {
            return ArraySet.this.mSize;
        }

        protected int colIndexOfKey(Object obj) {
            return ArraySet.this.indexOf(obj);
        }

        protected int colIndexOfValue(Object obj) {
            return ArraySet.this.indexOf(obj);
        }

        protected void colPut(E e, E e2) {
            ArraySet.this.add(e);
        }

        protected void colRemoveAt(int i) {
            ArraySet.this.removeAt(i);
        }

        protected E colSetValue(int i, E e) {
            throw new UnsupportedOperationException("not a map");
        }
    }

    public ArraySet() {
        this(0);
    }

    public ArraySet(int i) {
        if (i == 0) {
            this.mHashes = INT;
            this.mArray = OBJECT;
        } else {
            allocArrays(i);
        }
        this.mSize = 0;
    }

    public ArraySet(ArraySet<E> arraySet) {
        this();
        if (arraySet != null) {
            addAll((ArraySet) arraySet);
        }
    }

    public ArraySet(Collection<E> collection) {
        this();
        if (collection != null) {
            addAll((Collection) collection);
        }
    }

    private void allocArrays(int i) {
        Object[] objArr;
        if (i == 8) {
            synchronized (ArraySet.class) {
                if (sTwiceBaseCache != null) {
                    objArr = sTwiceBaseCache;
                    this.mArray = objArr;
                    sTwiceBaseCache = (Object[]) objArr[0];
                    this.mHashes = (int[]) objArr[1];
                    objArr[1] = null;
                    objArr[0] = null;
                    sTwiceBaseCacheSize--;
                    return;
                }
            }
        } else if (i == 4) {
            synchronized (ArraySet.class) {
                if (sBaseCache != null) {
                    objArr = sBaseCache;
                    this.mArray = objArr;
                    sBaseCache = (Object[]) objArr[0];
                    this.mHashes = (int[]) objArr[1];
                    objArr[1] = null;
                    objArr[0] = null;
                    sBaseCacheSize--;
                    return;
                }
            }
        }
        this.mHashes = new int[i];
        this.mArray = new Object[i];
    }

    private static void freeArrays(int[] iArr, Object[] objArr, int i) {
        if (iArr.length == 8) {
            synchronized (ArraySet.class) {
                if (sTwiceBaseCacheSize < 10) {
                    objArr[0] = sTwiceBaseCache;
                    objArr[1] = iArr;
                    for (i--; i >= 2; i--) {
                        objArr[i] = null;
                    }
                    sTwiceBaseCache = objArr;
                    sTwiceBaseCacheSize++;
                }
            }
        } else if (iArr.length == 4) {
            synchronized (ArraySet.class) {
                if (sBaseCacheSize < 10) {
                    objArr[0] = sBaseCache;
                    objArr[1] = iArr;
                    for (i--; i >= 2; i--) {
                        objArr[i] = null;
                    }
                    sBaseCache = objArr;
                    sBaseCacheSize++;
                }
            }
        }
    }

    private MapCollections<E, E> getCollection() {
        if (this.mCollections == null) {
            this.mCollections = new C07731();
        }
        return this.mCollections;
    }

    private int indexOf(Object obj, int i) {
        int i2 = this.mSize;
        if (i2 == 0) {
            return -1;
        }
        int binarySearch = ContainerHelpers.binarySearch(this.mHashes, i2, i);
        if (binarySearch < 0 || obj.equals(this.mArray[binarySearch])) {
            return binarySearch;
        }
        int i3 = binarySearch + 1;
        while (i3 < i2 && this.mHashes[i3] == i) {
            if (obj.equals(this.mArray[i3])) {
                return i3;
            }
            i3++;
        }
        binarySearch--;
        while (binarySearch >= 0 && this.mHashes[binarySearch] == i) {
            if (obj.equals(this.mArray[binarySearch])) {
                return binarySearch;
            }
            binarySearch--;
        }
        return i3 ^ -1;
    }

    private int indexOfNull() {
        int i = this.mSize;
        if (i == 0) {
            return -1;
        }
        int binarySearch = ContainerHelpers.binarySearch(this.mHashes, i, 0);
        if (binarySearch < 0 || this.mArray[binarySearch] == null) {
            return binarySearch;
        }
        int i2 = binarySearch + 1;
        while (i2 < i && this.mHashes[i2] == 0) {
            if (this.mArray[i2] == null) {
                return i2;
            }
            i2++;
        }
        binarySearch--;
        while (binarySearch >= 0 && this.mHashes[binarySearch] == 0) {
            if (this.mArray[binarySearch] == null) {
                return binarySearch;
            }
            binarySearch--;
        }
        return i2 ^ -1;
    }

    public boolean add(E e) {
        int indexOfNull;
        int i;
        if (e == null) {
            indexOfNull = indexOfNull();
            i = 0;
        } else {
            indexOfNull = e.hashCode();
            i = indexOfNull;
            indexOfNull = indexOf(e, indexOfNull);
        }
        if (indexOfNull >= 0) {
            return false;
        }
        int i2;
        Object obj;
        indexOfNull ^= -1;
        int i3 = this.mSize;
        if (i3 >= this.mHashes.length) {
            i2 = 4;
            if (i3 >= 8) {
                i2 = (i3 >> 1) + i3;
            } else if (i3 >= 4) {
                i2 = 8;
            }
            obj = this.mHashes;
            Object obj2 = this.mArray;
            allocArrays(i2);
            Object obj3 = this.mHashes;
            if (obj3.length > 0) {
                System.arraycopy(obj, 0, obj3, 0, obj.length);
                System.arraycopy(obj2, 0, this.mArray, 0, obj2.length);
            }
            freeArrays(obj, obj2, this.mSize);
        }
        int i4 = this.mSize;
        if (indexOfNull < i4) {
            obj = this.mHashes;
            i2 = indexOfNull + 1;
            System.arraycopy(obj, indexOfNull, obj, i2, i4 - indexOfNull);
            Object obj4 = this.mArray;
            System.arraycopy(obj4, indexOfNull, obj4, i2, this.mSize - indexOfNull);
        }
        this.mHashes[indexOfNull] = i;
        this.mArray[indexOfNull] = e;
        this.mSize++;
        return true;
    }

    public void addAll(ArraySet<? extends E> arraySet) {
        int i = arraySet.mSize;
        ensureCapacity(this.mSize + i);
        int i2 = 0;
        if (this.mSize != 0) {
            while (i2 < i) {
                add(arraySet.valueAt(i2));
                i2++;
            }
        } else if (i > 0) {
            System.arraycopy(arraySet.mHashes, 0, this.mHashes, 0, i);
            System.arraycopy(arraySet.mArray, 0, this.mArray, 0, i);
            this.mSize = i;
        }
    }

    public boolean addAll(Collection<? extends E> collection) {
        ensureCapacity(this.mSize + collection.size());
        boolean z = false;
        for (Object add : collection) {
            z |= add(add);
        }
        return z;
    }

    public void append(E e) {
        int i = this.mSize;
        int hashCode = e == null ? 0 : e.hashCode();
        int[] iArr = this.mHashes;
        if (i >= iArr.length) {
            throw new IllegalStateException("Array is full");
        } else if (i <= 0 || iArr[i - 1] <= hashCode) {
            this.mSize = i + 1;
            this.mHashes[i] = hashCode;
            this.mArray[i] = e;
        } else {
            add(e);
        }
    }

    public void clear() {
        int i = this.mSize;
        if (i != 0) {
            freeArrays(this.mHashes, this.mArray, i);
            this.mHashes = INT;
            this.mArray = OBJECT;
            this.mSize = 0;
        }
    }

    public boolean contains(Object obj) {
        return indexOf(obj) >= 0;
    }

    public boolean containsAll(Collection<?> collection) {
        for (Object contains : collection) {
            if (!contains(contains)) {
                return false;
            }
        }
        return true;
    }

    public void ensureCapacity(int i) {
        Object obj = this.mHashes;
        if (obj.length < i) {
            Object obj2 = this.mArray;
            allocArrays(i);
            i = this.mSize;
            if (i > 0) {
                System.arraycopy(obj, 0, this.mHashes, 0, i);
                System.arraycopy(obj2, 0, this.mArray, 0, this.mSize);
            }
            freeArrays(obj, obj2, this.mSize);
        }
    }

    public boolean equals(java.lang.Object r5) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/318857719.run(Unknown Source)
*/
        /*
        r4 = this;
        r0 = 1;
        if (r4 != r5) goto L_0x0004;
    L_0x0003:
        return r0;
    L_0x0004:
        r1 = r5 instanceof java.util.Set;
        r2 = 0;
        if (r1 == 0) goto L_0x002a;
    L_0x0009:
        r5 = (java.util.Set) r5;
        r1 = r4.size();
        r3 = r5.size();
        if (r1 == r3) goto L_0x0016;
    L_0x0015:
        return r2;
    L_0x0016:
        r1 = 0;
    L_0x0017:
        r3 = r4.mSize;	 Catch:{ NullPointerException -> 0x002a, NullPointerException -> 0x002a }
        if (r1 >= r3) goto L_0x0029;	 Catch:{ NullPointerException -> 0x002a, NullPointerException -> 0x002a }
    L_0x001b:
        r3 = r4.valueAt(r1);	 Catch:{ NullPointerException -> 0x002a, NullPointerException -> 0x002a }
        r3 = r5.contains(r3);	 Catch:{ NullPointerException -> 0x002a, NullPointerException -> 0x002a }
        if (r3 != 0) goto L_0x0026;
    L_0x0025:
        return r2;
    L_0x0026:
        r1 = r1 + 1;
        goto L_0x0017;
    L_0x0029:
        return r0;
    L_0x002a:
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.util.ArraySet.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        int[] iArr = this.mHashes;
        int i = 0;
        for (int i2 = 0; i2 < this.mSize; i2++) {
            i += iArr[i2];
        }
        return i;
    }

    public int indexOf(Object obj) {
        return obj == null ? indexOfNull() : indexOf(obj, obj.hashCode());
    }

    public boolean isEmpty() {
        return this.mSize <= 0;
    }

    public Iterator<E> iterator() {
        return getCollection().getKeySet().iterator();
    }

    public boolean remove(Object obj) {
        int indexOf = indexOf(obj);
        if (indexOf < 0) {
            return false;
        }
        removeAt(indexOf);
        return true;
    }

    public boolean removeAll(ArraySet<? extends E> arraySet) {
        int i = arraySet.mSize;
        int i2 = this.mSize;
        for (int i3 = 0; i3 < i; i3++) {
            remove(arraySet.valueAt(i3));
        }
        return i2 != this.mSize;
    }

    public boolean removeAll(Collection<?> collection) {
        boolean z = false;
        for (Object remove : collection) {
            z |= remove(remove);
        }
        return z;
    }

    public E removeAt(int i) {
        Object[] objArr = this.mArray;
        E e = objArr[i];
        int i2 = this.mSize;
        if (i2 <= 1) {
            freeArrays(this.mHashes, objArr, i2);
            this.mHashes = INT;
            this.mArray = OBJECT;
            this.mSize = 0;
        } else {
            int[] iArr = this.mHashes;
            int i3 = 8;
            Object obj;
            int i4;
            Object obj2;
            if (iArr.length <= 8 || i2 >= iArr.length / 3) {
                this.mSize--;
                int i5 = this.mSize;
                if (i < i5) {
                    obj = this.mHashes;
                    i4 = i + 1;
                    System.arraycopy(obj, i4, obj, i, i5 - i);
                    obj2 = this.mArray;
                    System.arraycopy(obj2, i4, obj2, i, this.mSize - i);
                }
                this.mArray[this.mSize] = null;
            } else {
                if (i2 > 8) {
                    i3 = i2 + (i2 >> 1);
                }
                obj2 = this.mHashes;
                obj = this.mArray;
                allocArrays(i3);
                this.mSize--;
                if (i > 0) {
                    System.arraycopy(obj2, 0, this.mHashes, 0, i);
                    System.arraycopy(obj, 0, this.mArray, 0, i);
                }
                i4 = this.mSize;
                if (i < i4) {
                    int i6 = i + 1;
                    System.arraycopy(obj2, i6, this.mHashes, i, i4 - i);
                    System.arraycopy(obj, i6, this.mArray, i, this.mSize - i);
                }
            }
        }
        return e;
    }

    public boolean retainAll(Collection<?> collection) {
        boolean z = false;
        for (int i = this.mSize - 1; i >= 0; i--) {
            if (!collection.contains(this.mArray[i])) {
                removeAt(i);
                z = true;
            }
        }
        return z;
    }

    public int size() {
        return this.mSize;
    }

    public Object[] toArray() {
        int i = this.mSize;
        Object obj = new Object[i];
        System.arraycopy(this.mArray, 0, obj, 0, i);
        return obj;
    }

    public <T> T[] toArray(T[] tArr) {
        Object obj;
        if (tArr.length < this.mSize) {
            obj = (Object[]) Array.newInstance(tArr.getClass().getComponentType(), this.mSize);
        }
        System.arraycopy(this.mArray, 0, obj, 0, this.mSize);
        int length = obj.length;
        int i = this.mSize;
        if (length > i) {
            obj[i] = null;
        }
        return obj;
    }

    public String toString() {
        if (isEmpty()) {
            return "{}";
        }
        StringBuilder stringBuilder = new StringBuilder(this.mSize * 14);
        stringBuilder.append('{');
        for (int i = 0; i < this.mSize; i++) {
            if (i > 0) {
                stringBuilder.append(", ");
            }
            ArraySet valueAt = valueAt(i);
            if (valueAt != this) {
                stringBuilder.append(valueAt);
            } else {
                stringBuilder.append("(this Set)");
            }
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    public E valueAt(int i) {
        return this.mArray[i];
    }
}
