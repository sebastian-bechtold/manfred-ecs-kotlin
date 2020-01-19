# manfred-ecs-kotlin
A Kotlin implementation of my ultra-light-weight entity component system (ECS) called 'Manfred'.

Manfred is super simple, yet fast. Or maybe fast because it is simple?

Hint: If you still run into performance issues due to too many entities in a ManfredEntityList, take a look at the different entity types you store there, and try to separate the entities in multiple lists depending on their type/use.

For example, in a game where you have a lot of colliding (e.g. enemies) as well non-colliding (e.g. particles) entities, it might make sense to put them into two separate lists. This is even move obvious for groups of entities that don't have any common components.
