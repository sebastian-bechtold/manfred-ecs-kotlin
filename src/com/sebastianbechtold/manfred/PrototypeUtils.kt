package com.sebastianbechtold.manfred

import org.json.simple.JSONObject
import java.io.FileReader

interface IPrototypeComponent

class PrototypeRefComponent() : SerializableManfredComponent(), IPersistComponent {

    var prototypeId = ""

    override fun toJson(): JSONObject {
        var result = JSONObject()

        result.set("prototype", prototypeId)

        return result
    }

    override fun load(aJson: JSONObject) {
        json = aJson

        prototypeId = json["prototype"] as String
    }
}


fun applyPrototype(entity : ManfredEntity, prototype : ManfredEntity) {

    for (prototypeComp in prototype) {
        if (prototypeComp is IPrototypeComponent) {
            entity.setComponent(prototypeComp)
        } else if (prototypeComp is SerializableManfredComponent) {
            entity.setComponent(prototypeComp.copy())
        }
    }
}



fun loadPrototypes(prototypesFilePath : String) : ManfredEntityList? {

    var result = ManfredEntityList()

    var parser = org.json.simple.parser.JSONParser()

    var prototypeJson: JSONObject? = null


    try {
        val fileReader = FileReader(prototypesFilePath)

        prototypeJson = parser.parse(fileReader) as JSONObject
    } catch (e: Exception) {
        print("Failed to read entity prototype definitions file")
        return null
    }


    var p = entitiesFromJson(prototypeJson)

    if (p != null) {
        for (entity in p) {

            var json = JSONObject()
            json["prototype"] = entity.uuid
            var pc = PrototypeRefComponent()
            pc.load(json)
            entity.setComponent(pc)

            result.add(entity)
        }
    }

    return result
}

