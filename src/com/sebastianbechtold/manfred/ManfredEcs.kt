// Last change: 2020-10-18

package com.sebastianbechtold.manfred

import java.util.*
import kotlin.collections.HashMap

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

    private val _entities = HashMap<String, ManfredEntity>()

    val size: Int
        get() {
            return _entities.size
        }


    fun add(entity: ManfredEntity) {
        // TODO: 1 What to do if entity ID already exists?
        _entities.put(entity.uuid, entity)
    }


    fun addAll(others : ManfredEntityList) {
        for(entity in others) {
            add(entity)
        }
    }


    fun clear() {
        _entities.clear()
    }


    fun getEntitiesWith(vararg compClasses: Class<*>): ManfredEntityList {

        var result = ManfredEntityList()

        //############# BEGIN Find all entities that have the specified components ###########
        for (entity in _entities.values) {

            var allIn = true

            for (compClass in compClasses) {
                if (entity.getComponent(compClass) == null) {
                    allIn = false;
                    break;
                }
            }

            if (allIn) {
                result.add(entity)
            }
        }
        //############# END Find all entities that have the specified components ###########

        return result
    }


    fun getEntityByUuid(uuid : String) : ManfredEntity? {
        return _entities.get(uuid)
    }


    override fun iterator(): Iterator<ManfredEntity> {
        return _entities.values.iterator()
    }


    fun remove(entity: ManfredEntity) {

        // ATTENTION: Just removing an target from a ManfredEntityList does not destroy it!
        _entities.remove(entity.uuid)
    }


    fun removeByUuid(uuid : String) {
        _entities.remove(uuid)
    }
}

