package com.yz.oneapi.utils.cache;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.StampedLock;

/**
 * 超时和限制大小的缓存的默认实现<br>
 * 继承此抽象缓存需要：<br>
 * <ul>
 * <li>创建一个新的Map</li>
 * <li>实现 {@code prune} 策略</li>
 * </ul>
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @author Looly, jodd
 */
public abstract class AbstractCache<K, V> implements Cache<K, V> {
	private static final long serialVersionUID = 1L;

	protected Map<K, CacheObj<K, V>> cacheMap;

	// 乐观锁，此处使用乐观锁解决读多写少的场景
	// get时乐观读，再检查是否修改，修改则转入悲观读重新读一遍，可以有效解决在写时阻塞大量读操作的情况。
	// see: https://www.cnblogs.com/jiagoushijuzi/p/13721319.html
	private final StampedLock lock = new StampedLock();

	/**
	 * 写的时候每个key一把锁，降低锁的粒度
	 */
	protected final Map<K, Lock> keyLockMap = new ConcurrentHashMap<>();

	/**
	 * 返回缓存容量，{@code 0}表示无大小限制
	 */
	protected int capacity;
	/**
	 * 缓存失效时长， {@code 0} 表示无限制，单位毫秒
	 */
	protected long timeout;

	/**
	 * 每个对象是否有单独的失效时长，用于决定清理过期对象是否有必要。
	 */
	protected boolean existCustomTimeout;

	/**
	 * 命中数，即命中缓存计数
	 */
	protected AtomicLong hitCount = new AtomicLong();
	/**
	 * 丢失数，即未命中缓存计数
	 */
	protected AtomicLong missCount = new AtomicLong();



	// ---------------------------------------------------------------- put start
	@Override
	public void put(K key, V object) {
		put(key, object, timeout);
	}

	@Override
	public void put(K key, V object, long timeout) {
		final long stamp = lock.writeLock();
		try {
			putWithoutLock(key, object, timeout);
		} finally {
			lock.unlockWrite(stamp);
		}
	}

	/**
	 * 加入元素，无锁
	 *
	 * @param key     键
	 * @param object  值
	 * @param timeout 超时时长
	 * @since 4.5.16
	 */
	private void putWithoutLock(K key, V object, long timeout) {
		CacheObj<K, V> co = new CacheObj<>(key, object, timeout);
		if (timeout != 0) {
			existCustomTimeout = true;
		}
		if (isFull()) {
			pruneCache();
		}
		cacheMap.put(key, co);
	}
	// ---------------------------------------------------------------- put end

	// ---------------------------------------------------------------- get start
	@Override
	public boolean containsKey(K key) {
		final long stamp = lock.readLock();
		try {
			// 不存在或已移除
			final CacheObj<K, V> co = cacheMap.get(key);
			if (co == null) {
				return false;
			}

			if (false == co.isExpired()) {
				// 命中
				return true;
			}
		} finally {
			lock.unlockRead(stamp);
		}

		// 过期
		remove(key, true);
		return false;
	}

	/**
	 * @return 命中数
	 */
	public long getHitCount() {
		return hitCount.get();
	}

	/**
	 * @return 丢失数
	 */
	public long getMissCount() {
		return missCount.get();
	}


	@Override
	public V get(K key, boolean isUpdateLastAccess) {
		// 尝试读取缓存，使用乐观读锁
		long stamp = lock.tryOptimisticRead();
		CacheObj<K, V> co = cacheMap.get(key);
		if(false == lock.validate(stamp)){
			// 有写线程修改了此对象，悲观读
			stamp = lock.readLock();
			try {
				co = cacheMap.get(key);
			} finally {
				lock.unlockRead(stamp);
			}
		}

		// 未命中
		if (null == co) {
			missCount.getAndIncrement();
			return null;
		} else if (false == co.isExpired()) {
			hitCount.getAndIncrement();
			return co.get(isUpdateLastAccess);
		}

		// 过期，既不算命中也不算非命中
		remove(key, true);
		return null;
	}

	// ---------------------------------------------------------------- get end


	// ---------------------------------------------------------------- prune start

	/**
	 * 清理实现<br>
	 * 子类实现此方法时无需加锁
	 *
	 * @return 清理数
	 */
	protected abstract int pruneCache();

	@Override
	public final int prune() {
		final long stamp = lock.writeLock();
		try {
			return pruneCache();
		} finally {
			lock.unlockWrite(stamp);
		}
	}
	// ---------------------------------------------------------------- prune end

	// ---------------------------------------------------------------- common start
	@Override
	public int capacity() {
		return capacity;
	}

	/**
	 * @return 默认缓存失效时长。<br>
	 * 每个对象可以单独设置失效时长
	 */
	@Override
	public long timeout() {
		return timeout;
	}

	/**
	 * 只有设置公共缓存失效时长或每个对象单独的失效时长时清理可用
	 *
	 * @return 过期对象清理是否可用，内部使用
	 */
	protected boolean isPruneExpiredActive() {
		return (timeout != 0) || existCustomTimeout;
	}

	@Override
	public boolean isFull() {
		return (capacity > 0) && (cacheMap.size() >= capacity);
	}

	@Override
	public void remove(K key) {
		remove(key, false);
	}

	@Override
	public void clear() {
		final long stamp = lock.writeLock();
		try {
			cacheMap.clear();
		} finally {
			lock.unlockWrite(stamp);
		}
	}

	@Override
	public int size() {
		return cacheMap.size();
	}

	@Override
	public boolean isEmpty() {
		return cacheMap.isEmpty();
	}

	@Override
	public String toString() {
		return this.cacheMap.toString();
	}
	// ---------------------------------------------------------------- common end




	/**
	 * 移除key对应的对象
	 *
	 * @param key           键
	 * @param withMissCount 是否计数丢失数
	 */
	private void remove(K key, boolean withMissCount) {
		final long stamp = lock.writeLock();
		CacheObj<K, V> co;
		try {
			co = removeWithoutLock(key, withMissCount);
		} finally {
			lock.unlockWrite(stamp);
		}
	}

	/**
	 * 移除key对应的对象，不加锁
	 *
	 * @param key           键
	 * @param withMissCount 是否计数丢失数
	 * @return 移除的对象，无返回null
	 */
	private CacheObj<K, V> removeWithoutLock(K key, boolean withMissCount) {
		final CacheObj<K, V> co = cacheMap.remove(key);
		if (withMissCount) {
			// 在丢失计数有效的情况下，移除一般为get时的超时操作，此处应该丢失数+1
			this.missCount.getAndIncrement();
		}
		return co;
	}
}
