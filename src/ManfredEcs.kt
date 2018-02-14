// Last change: 2018-02-14

package com.sebastianbechtold.vectro

var em = ManfredEcs()

open class Component {

    var id : Long = 0
}


class ManfredEcs {

    private var _deleteList = ArrayList<Long>()

    private var _entities = HashMap<Long, HashMap<Any, Component>>()

    private var nextId : Long = 0

    private var _queryCache = HashMap<String, Set<Long>>()

    private var _useCache = true


    fun getUnusedId(): Long {
        return nextId++
    }


    fun <T> getComponent(id: Long, compClass: Class<T>): T? {

        var entity : HashMap<Any,Component>? = _entities.get(id)

        if (entity == null) {
            return null
        }

        return entity.get(compClass) as T?
    }


    fun finish() {
        for(id in _deleteList) {
            _entities.remove(id)
        }

        _deleteList.clear()
    }

    
    fun getEntitiesWith(vararg compClasses: Class<*>): Set<Long> {

        var result = HashSet<Long>()

        //############## BEGIN Cache lookup #############
        // Build cache key:
        var cacheKey = ""

        // Return cached result if it exists:
        if (_useCache) {
            for (compClass in compClasses) {
                cacheKey += compClass.name + ";"
            }

            var cachedResult = _queryCache.get(cacheKey)

            if (cachedResult != null) {
                return cachedResult
            }
        }
        //############## END Cache lookup #############


        for (entry in _entities) {
            var allIn = true


            for (compClass in compClasses) {
                if (!entry.value.containsKey(compClass)) {
                    allIn = false;
                    break;
                }
            }

            if (allIn) {
                result.add(entry.key)
            }
        }

        // Write query result to cache:
        if (_useCache) {
            _queryCache.put(cacheKey, result);
        }

        return result
    }


    private fun clearCache(type: Class<*>) {
        val dirty = ArrayList<String>()

        for (key in _queryCache.keys) {

            if (key.contains(type.name + ";")) {
                dirty.add(key)
            }
        }

        for (key in dirty) {
            _queryCache.remove(key)
        }
    }

    fun hasEntity(id: Long): Boolean {
        return _entities.containsKey(id)
    }

    fun <T> removeComponent(id: Long, compClass: Class<T>) {
        var entity = _entities.get(id)

        if (entity == null) {
            return
        }

        entity.remove(compClass)

        clearCache(compClass);

        // TODO: 3 Fire component replaced event?

        if (entity.isEmpty()) {
            removeEntity(id)
        }
    }

    fun removeEntity(id: Long) {
        _deleteList.add(id)

    }

    fun setComponent(id: Long, comp: Component) {

        var entity = _entities.get(id);

        var prevComp : Component? = null

        if (entity == null) {
            entity = HashMap<Any, Component>()
            _entities.set(id, entity)
        }
        else {
            prevComp = entity.get(comp::class.java)
        }

        comp.id = id
        entity.set(comp::class.java, comp)

        if (prevComp == null) {
            clearCache(comp.javaClass)
        }
    }
}