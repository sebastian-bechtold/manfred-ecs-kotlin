// Last change: 2020-09-19

// TODO: 3 Change ManfredEntityList to use a HasSet<uuid, entity>?

package com.sebastianbechtold.manfred

import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

// IManfredComponent is the interface for components that is used in ManfredEntity
// and ManfredEntityList. The class 'ManfredComponent' is a minimal implementation
// that can be used as a base class for derived component classes that don't need
// to have their own implementation of the onRemove() method.

interface IManfredComponent {
    fun onRemove();
}


open class ManfredComponent : IManfredComponent {
    override fun onRemove() {

    }
}


class ManfredEntity(val uuid : String = UUID.randomUUID().toString()) : Iterable<IManfredComponent> {

    private val _components = HashMap<Any, IManfredComponent>()


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


    fun setComponent(comp: IManfredComponent) : IManfredComponent {
        _components.set(comp::class.java, comp)
        return comp
    }
}


class ManfredEntityList : Iterable<ManfredEntity> {

    private val _entities = HashSet<ManfredEntity>()

    val size: Int
        get() {
            return _entities.size
        }


    fun add(entity: ManfredEntity) {
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


    fun getEntityByUuid(uuid : String) : ManfredEntity? {
        for(entity in _entities) {
            if (entity.uuid.toString() == uuid) {
                return entity
            }
        }

        return null
    }


    override fun iterator(): Iterator<ManfredEntity> {
        return _entities.iterator()
    }


    fun remove(entity: ManfredEntity) {

        // ATTENTION: Just removing an target from a ManfredEntityList does not destroy it!
        _entities.remove(entity)
    }
}

