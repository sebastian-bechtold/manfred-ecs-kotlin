// Last change: 2018-12-29

package com.sebastianbechtold.manfred

abstract class ManfredComponent {

    open fun onRemove() {

    }
}


class ManfredEntity {

    private var _components = HashMap<Any, ManfredComponent>()


    fun <T> getComponent(compClass: Class<T>): T {
        return _components[compClass] as T
    }


    fun <T> removeComponent(compClass: Class<T>) {

        var comp  = _components.get(compClass)

        if (comp == null) return

        comp.onRemove()

        _components.remove(compClass)
    }


    fun removeAllComponents() {
        for(comp in _components.values) {
            comp.onRemove()
        }

        _components.clear()
    }


    fun setComponent(comp: ManfredComponent) {
        _components.set(comp::class.java, comp)
    }
}


class ManfredEntityList : Iterable<ManfredEntity> {

    private var _entities = HashSet<ManfredEntity>()

    val size : Int
    get() {
        return _entities.size
    }

    fun addEntity(entity : ManfredEntity) {
        _entities.add(entity)
    }


    fun destroyEntity(entity: ManfredEntity) {

        entity.removeAllComponents()

        _entities.remove(entity)
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


    fun newEntity() : ManfredEntity {

        var entity = ManfredEntity()

        _entities.add(entity)

        return entity
    }


    fun removeEntity(entity: ManfredEntity) {

        // ATTENTION: Just removing an entity from a ManfredEntityList does not destroy it!
        _entities.remove(entity)
    }


}

