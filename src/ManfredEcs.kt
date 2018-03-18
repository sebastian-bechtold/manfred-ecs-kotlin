// Last change: 2018-03-18

package com.sebastianbechtold.vectro

var em = ManfredEcs()

open class ManfredComponent {

    var id: Long = 0
}


class ManfredEcs {

    // NOTE: entities must be public since 'getComponent()' must be 'inline fun <reified T>'.
    var entities = HashMap<Long, HashMap<Any, ManfredComponent>>()

    private var _deleteList = HashSet<Long>()



    private var _nextId: Long = 0
    private var _queryCache = HashMap<String, ArrayList<Long>>()
    private var _useCache = true


    fun getUnusedId(): Long {
        return _nextId++
    }


    inline fun <reified T> getComponent(id: Long, compClass: Class<T>): T? {

        var entity: HashMap<Any, ManfredComponent>? = entities[id]

        if (entity == null) {
            return null
        }

        return entity[compClass] as T?
    }


    fun finish() {

        for (id in _deleteList) {
            entities.remove(id)
        }

        _deleteList.clear()
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

            val cachedResult = _queryCache[cacheKey]

            if (cachedResult != null) {
                return cachedResult
            }
        }
        //############## END Cache lookup #############

        //############# BEGIN Find all entities that have the specified components ###########
        for (entry in entities) {
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
        //############# END Find all entities that have the specified components ###########

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


    fun <T> removeComponent(id: Long, compClass: Class<T>) {

        var entity = entities[id]
        if (entity == null) return


        if (entity.remove(compClass) == null) return


        clearCache(compClass);

        if (entity.isEmpty()) {
            _deleteList.add(id)
        }
    }


    fun removeEntity(id: Long) {

        var entity = entities.get(id)

        if (entity == null) return


        var componentsToRemove = ArrayList<ManfredComponent>()

        componentsToRemove.addAll(entity.values)

        for (comp in componentsToRemove) {
            removeComponent(id, comp::class.java)
        }

        _deleteList.add(id)
    }


    fun setComponent(id: Long, comp: ManfredComponent) {

        var entity = entities[id];

        var prevComp: ManfredComponent? = null

        if (entity == null) {
            entity = HashMap()
            entities.set(id, entity)
        } else {
            prevComp = entity.get(comp::class.java)
        }

        comp.id = id
        entity.set(comp::class.java, comp)

        if (_useCache && prevComp == null) {
        // (_useCache) {
            clearCache(comp::class.java)
        }
    }
}