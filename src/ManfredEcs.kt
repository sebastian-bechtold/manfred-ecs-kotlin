// Last change: 2019-01-05

package com.sebastianbechtold.manfred

interface IManfredComponent {
    fun onRemove();
}


class ManfredEntity : Iterable<IManfredComponent> {

    private var _components = HashMap<Any, IManfredComponent>()

    
    fun <T> getComponent(compClass: Class<T>): T {
        return _components[compClass] as T
    }


    override fun iterator(): Iterator<IManfredComponent> {
        return _components.values.iterator()
    }


    fun removeAllComponents() {
        for (comp in _components.values) {
            comp.onRemove()
        }

        _components.clear()
    }


    fun <T> removeComponent(compClass: Class<T>) {

        var comp = _components.get(compClass)

        if (comp == null) return

        comp.onRemove()

        _components.remove(compClass)
    }


    fun setComponent(comp: IManfredComponent) {
        _components.set(comp::class.java, comp)
    }
}


class ManfredEntityList : Iterable<ManfredEntity> {

    private var _entities = HashSet<ManfredEntity>()

    val size: Int
        get() {
            return _entities.size
        }


    fun add(entity: ManfredEntity) {
        _entities.add(entity)
    }


    fun getEntitiesWith(vararg compClasses: Class<*>): ManfredEntityList {

        var result = ManfredEntityList()

        //############# BEGIN Find all _entities that have the specified components ###########
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
        //############# END Find all _entities that have the specified components ###########

        return result
    }


    override fun iterator(): Iterator<ManfredEntity> {
        return _entities.iterator()
    }


    fun remove(entity: ManfredEntity) {

        // ATTENTION: Just removing an entity from a ManfredEntityList does not destroy it!
        _entities.remove(entity)
    }
}

