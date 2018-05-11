// Last change: 2018-05-11

package com.sebastianbechtold.vectro

var em = ManfredEntityList()

abstract class ManfredComponent {

    open fun onRemove() {

    }
}

class ManfredEntity {

    internal var components = HashMap<Any, ManfredComponent>()

    fun <T> getComponent(compClass: Class<T>): T? {
        return components[compClass] as T?
    }


    fun <T> removeComponent(compClass: Class<T>) {

        var comp  = components.get(compClass)

        if (comp == null) return

        comp.onRemove()

        components.remove(compClass)
    }


    fun setComponent(comp: ManfredComponent) {
        components.set(comp::class.java, comp)
    }
}


class ManfredEntityList : Iterable<ManfredEntity> {

    private var _entities = HashSet<ManfredEntity>()


    fun addEntity(entity : ManfredEntity) {
        _entities.add(entity)
    }


    fun deleteEntity(entity: ManfredEntity) {

        for(comp in entity.components.values) {
            comp.onRemove()
        }

        _entities.remove(entity)
    }


    fun getEntitiesWith(vararg compClasses: Class<*>): ManfredEntityList {

        var result = ManfredEntityList()

        //############# BEGIN Find all _entities that have the specified components ###########
        for (entity in _entities) {

            var allIn = true

            for (compClass in compClasses) {

                if (!entity.components.containsKey(compClass)) {
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
}

