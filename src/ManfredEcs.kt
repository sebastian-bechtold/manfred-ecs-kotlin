

// Last change: 2019-09-07

package com.sebastianbechtold.manfred


// IManfredComponent is the interface for components that is used in ManfredEntity
// and ManfredEntityList. The class 'ManfredComponent' is a minimal implementation
// that can be used as a base class for derived component classes that don't need
// to have their own implementation of the onRemove() method.

interface IManfredComponent {}


class ManfredEntity : Iterable<IManfredComponent> {

    private var _components = HashMap<Any, IManfredComponent>()


    fun <T> getComponent(compClass: Class<T>): T {
        return _components[compClass] as T
    }


    override fun iterator(): Iterator<IManfredComponent> {
        return _components.values.iterator()
    }


    fun removeAllComponents() {

        _components.clear()
    }


    fun <T> removeComponent(compClass: Class<T>) {

        var comp = _components.get(compClass)

        if (comp == null) return

        _components.remove(compClass)
    }


    fun setComponent(comp: IManfredComponent) {
        _components.set(comp::class.java, comp)
    }
}


class ManfredEntityList : Iterable<ManfredEntity> {

    private var _entities = ArrayList<ManfredEntity>()

    val size: Int
        get() {
            return _entities.size
        }


    fun add(entity: ManfredEntity) {

        if (_entities.contains(entity)) {
            return
        }

        _entities.add(entity)
    }

    fun clear() {
        _entities.clear()
    }



    fun getEntitiesWith(vararg compClasses: Class<*>): ManfredEntityList {

        var result = ManfredEntityList()

        //############# BEGIN Find all entities that have the specified components ###########
        for (entity in _entities) {

            var allIn = true

            for (compClass in compClasses) {
                if (entity.getComponent(compClass) == null) {
                    allIn = false;
                    break;
                }
            }

            if (allIn) {
                result._entities.add(entity)
            }
        }
        //############# END Find all entities that have the specified components ###########

        return result
    }


    override fun iterator(): Iterator<ManfredEntity> {
        return _entities.iterator()
    }


    fun remove(entity: ManfredEntity) {

        // ATTENTION: Just removing an target from a ManfredEntityList does not destroy it!
        _entities.remove(entity)
    }
}

