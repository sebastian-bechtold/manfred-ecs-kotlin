// Last change: 2018-04-23

package com.sebastianbechtold.vectro

var em = ManfredEcs()

abstract class ManfredComponent {
    var id: Long = 0
}

class ManfredEcs {

    private var _entities = HashMap<Long, HashMap<Any, ManfredComponent>>()
    private var _deleteList = HashSet<Long>()
    private var _nextId: Long = 0


    fun getUnusedId(): Long {
        return _nextId++
    }

    fun entityExists(id: Long): Boolean {
        return _entities.containsKey(id)// && !_deleteList.contains(id)
    }


    fun <T> getComponent(id: Long, compClass: Class<T>): T? {

       // if (_deleteList.contains(id)) return null

        var entity: HashMap<Any, ManfredComponent>? = _entities[id]

        if (entity == null) {
            return null
        }

        return entity[compClass] as T?
    }


    fun finish() {

        for (id in _deleteList) {
            _entities.remove(id)
        }

        _deleteList.clear()
    }


    fun getEntitiesWithCompInstance(comp: ManfredComponent): HashSet<Long> {
        var result = HashSet<Long>()

        //############# BEGIN Find all _entities that have the specified components ###########
        for (entry in _entities) {

            if (_deleteList.contains(entry.key)) {
          //      continue
            }

            if (entry.value.containsValue(comp)) {
                result.add(entry.key)

            }
        }
        //############# END Find all _entities that have the specified components ###########

        return result
    }


    fun getEntitiesWithCompClasses(vararg compClasses: Class<*>): HashSet<Long> {

        var result = HashSet<Long>()

        //############# BEGIN Find all _entities that have the specified components ###########
        for (entry in _entities) {

            if (_deleteList.contains(entry.key)) {
         //       continue
            }

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
        //############# END Find all _entities that have the specified components ###########

        return result
    }


    fun <T> removeComponent(id: Long, compClass: Class<T>) {

        var entity = _entities[id]
        if (entity == null) return

        // TODO: 2 Maybe not remove components immediately.
        // Instead, create a delete list and do the actual removal in "finish()", just like with _entities.
        // This needs further testing. sbecht 2018-03-18

        entity.remove(compClass)

        if (entity.isEmpty()) {
            _deleteList.add(id)
        }
    }


    fun removeEntity(id: Long) {

        var entity = _entities.get(id)

        if (entity == null) return

        // ATTENTION:
        // entity.clear() must NOT BE CALLED here! Reason: "getEntitiesWithCompClasses()"
        // returns a list of entity IDs, and if removeEntity() is called within a loop over
        // such a list (i.e. typical "system" behaviour), the list becomes invalid. sbecht 2018-03-18

        _deleteList.add(id)
    }


    fun setComponent(id: Long, comp: ManfredComponent) {

        var entity = _entities[id];

        if (entity == null) {
            entity = HashMap()
            _entities.set(id, entity)
        }

        comp.id = id

        entity.set(comp::class.java, comp)
    }

    fun undoDelete(id : Long) {
        _deleteList.remove(id)
    }
}