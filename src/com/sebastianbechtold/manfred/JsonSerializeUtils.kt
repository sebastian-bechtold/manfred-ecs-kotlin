package com.sebastianbechtold.manfred

import com.sebastianbechtold.ISerializableManfredComponent
import org.json.simple.JSONObject

fun instantiate(uuid: String, entityJson: JSONObject, prototypes: HashMap<String, IManfredComponent>): ManfredEntity {

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

        var prototypeKey = componentJson["prototype"]

        if (prototypeKey != null) {
            var prototype = prototypes[prototypeKey]
            if (prototype != null) {
                result.setComponent(prototype)
            } else {
                println("Prototype component not found: " + prototypeKey)
            }
        } else {
            try {
                val component = c.getDeclaredConstructor().newInstance() as ISerializableManfredComponent

                component.load(componentJson)

                result.setComponent(component as IManfredComponent)

            } catch (e: java.lang.Exception) {
                println("Failed to instantiate component: " + className)
            }
        }
    }

    return result
}


fun entitiesFromJson(entitiesJson: JSONObject, prototypes: HashMap<String, IManfredComponent>): ManfredEntityList? {

    var result = ManfredEntityList()

    //################### BEGIN Load entities ###################
    for (kvp in entitiesJson as JSONObject) {
        result.add(instantiate(kvp.key as String, kvp.value as JSONObject, prototypes))
    }
    //################### END Load entities ###################

    return result
}


fun entitiesToJson(entities: ManfredEntityList): JSONObject {

    val result = JSONObject()

    for (entity in entities) {
        var entityJson = JSONObject()

        for (comp in entity) {
            if (comp is ISerializableManfredComponent) {
                entityJson[comp.javaClass.canonicalName] = comp.toJson()
            }
        }

        result[entity.uuid] = entityJson
    }

    return result
}