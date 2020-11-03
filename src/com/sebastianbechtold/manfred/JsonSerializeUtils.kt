package com.sebastianbechtold.manfred

import org.json.simple.JSONObject


interface IPersistComponent


abstract class SerializableManfredComponent : ManfredComponent() {

    var json = JSONObject()

    open fun copy() : SerializableManfredComponent {

        val result = this.javaClass.getDeclaredConstructor().newInstance()

        result.load(this.toJson())
        return result
    }

    open fun toJson() : JSONObject {
        return JSONObject()
    }

    open fun load(aJson : JSONObject) {
        json = aJson
    }

    open fun initialize(gel : ManfredEntityList, entity : ManfredEntity?) : Boolean {

        return true
    }
}

fun entityFromJson(uuid: String, entityJson: JSONObject): ManfredEntity {

    var result = ManfredEntity(uuid)

    for (className in entityJson.keys) {

        var c: Class<*>? = null

        try {
            c = Class.forName(className as String)
        } catch (e: Exception) {
            println("Class not found: " + className)
        }

        if (c == null) {
            continue
        }

        var componentJson = entityJson[className] as JSONObject


        try {
            val component = c.getDeclaredConstructor().newInstance() as SerializableManfredComponent

            component.load(componentJson)

            result.setComponent(component as IManfredComponent)

        } catch (e: java.lang.Exception) {
            println("Failed to instantiate component: " + className + ". Reason: " + e.message)
        }
    }

    return result
}


fun entitiesFromJson(entitiesJson: JSONObject): ManfredEntityList? {

    var result = ManfredEntityList()

    //################### BEGIN Load entities ###################
    for (kvp in entitiesJson as JSONObject) {
        result.add(entityFromJson(kvp.key as String, kvp.value as JSONObject))
    }
    //################### END Load entities ###################

    return result
}


fun entitiesToJson(entities: ManfredEntityList): JSONObject {

    val result = JSONObject()

    for (entity in entities) {
        var entityJson = JSONObject()

        for (comp in entity) {
            if (comp is SerializableManfredComponent && comp is IPersistComponent) {
                entityJson[comp.javaClass.canonicalName] = comp.toJson()
            }
        }

        result[entity.uuid] = entityJson
    }

    return result
}