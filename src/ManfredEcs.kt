// Last change: 2018-02-18

package com.sebastianbechtold.vectro

var em = ManfredEcs()

open class ManfredComponent {

    var id : Long = 0
}


class ManfredEcs {

    private var _deleteList = ArrayList<Long>()

    private var _entities = HashMap<Long, HashMap<Any, ManfredComponent>>()

    private var nextId : Long = 0

    private var _queryCache = HashMap<String, ArrayList<Long>>()

    private var _useCache = true

    fun getUnusedId(): Long {
        return nextId++
    }


    fun <T> getComponent(id: Long, compClass: Class<T>): T? {

        var entity : HashMap<Any, ManfredComponent>? = _entities.get(id)

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

    //    println(_entities.size)
    }


    fun getEntitiesWith(vararg compClasses: Class<*>): ArrayList<Long> {

        var result = ArrayList<Long>()

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
        
        if (entity.isEmpty()) {
            removeEntity(id)
        }
    }

    fun removeEntity(id: Long) {
        _deleteList.add(id)

    }

    fun setComponent(id: Long, comp: ManfredComponent) {

        var entity = _entities.get(id);

        var prevComp : ManfredComponent? = null

        if (entity == null) {
            entity = HashMap<Any, ManfredComponent>()
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