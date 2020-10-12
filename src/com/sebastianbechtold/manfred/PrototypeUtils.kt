package com.sebastianbechtold.manfred

import com.sebastianbechtold.SerializableManfredComponent
import org.json.simple.JSONObject

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
