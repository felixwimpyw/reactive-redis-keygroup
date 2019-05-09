# Reactive Redis Keygroup


What?
=====

Reactive Redis Keygroup Project will help people to group redis cache so it will be having group prefix and list of keys instead of just plain redis keys. This is support spring webflux v2 which includes reactive redis.

Why?
=====
When creating a cache, we cannot differentiate by just a parameter. Sometimes one or two function can have same type of parameter. So we need to have one key group. You can define this keygroup, so it can different per function (or maybe the same if you consider is from business point of iew). After you group it, all keys will be saved to one REDIS RANGE. My main aim is when we need to remove all keys from the same prefix, we can do this more efficient, both in the process and performance, and also cleaner, if you compare it to flushAll which will remove all other keys on other prefix. This can also helps if you need to listing all keys for certain group key.

Why not use the Spring one?
=====
This one is a little bit complex. It actually have me hard time once. We always use spring-data-redis before with a @Cacheable support. It easier and more clean by the code. We use @CacheEvict allEntries=true and it works just fine. 

One day there is problem which the lock is somehow stuck in redis. And it make the @Cacheable method wait by 30 seconds. It makes some of our services down by random. This makes us frustate and once we know the problem is on the @CacheEvict allEntries=true. After that we remove all allEntries=true codes in our codeBase, and use flushAll instead.

That is like 2 years ago. Now there is spring-data-redis v2.0, it somehow has better logic on allEntries=true so we decides to use it. But after some period, there is performance issue which is spiking everytime we hit evict. We found that they are using REDIS KEYS which is actually having O(n) complexity, where N is the **database count**. so it will cause this issue when our keys is huge ~ 100,000 data. You can see here for the info https://redis.io/commands/KEYS.
After this, I decide to create one simpler library for redis and add reactive support also.

How To Use?
=====
